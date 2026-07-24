package com.mathmod.program;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RunePurity;
import com.mathmod.runes.RuneRegistry;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ProgramNormalizer {
    private ProgramNormalizer() {
    }

    public static ProgramNormalization normalize(ProgramGraph graph, RuneRegistry registry) {
        Map<String, ProgramNode> nodes = new HashMap<>();
        graph.nodes().forEach(node -> nodes.put(node.id(), node));

        Map<String, Map<String, String>> inputs = new HashMap<>();
        for (ProgramEdge edge : graph.edges()) {
            inputs.computeIfAbsent(edge.toNodeId(), ignored -> new HashMap<>())
                    .put(edge.inputName(), edge.fromNodeId());
        }

        Map<String, NormalizedValue> normalized = new LinkedHashMap<>();
        Set<String> visiting = new HashSet<>();
        for (ProgramNode node : graph.nodes()) {
            normalizeNode(node.id(), nodes, inputs, registry, normalized, visiting);
        }
        return new ProgramNormalization(normalized);
    }

    private static Optional<NormalizedValue> normalizeNode(
            String nodeId,
            Map<String, ProgramNode> nodes,
            Map<String, Map<String, String>> inputs,
            RuneRegistry registry,
            Map<String, NormalizedValue> normalized,
            Set<String> visiting
    ) {
        if (normalized.containsKey(nodeId)) {
            return Optional.of(normalized.get(nodeId));
        }
        ProgramNode node = nodes.get(nodeId);
        if (node == null || !visiting.add(nodeId)) {
            return Optional.empty();
        }

        try {
            RuneDefinition definition = registry.find(node.runeId()).orElse(null);
            if (definition == null || definition.purity() != RunePurity.PURE) {
                return Optional.empty();
            }

            Map<String, NormalizedValue> inputValues = new HashMap<>();
            Map<String, String> inputNodes = inputs.getOrDefault(nodeId, Map.of());
            for (var input : definition.inputs()) {
                String sourceId = inputNodes.get(input.name());
                if (sourceId == null) {
                    return Optional.empty();
                }
                Optional<NormalizedValue> value = normalizeNode(
                        sourceId,
                        nodes,
                        inputs,
                        registry,
                        normalized,
                        visiting
                );
                if (value.isEmpty()) {
                    return Optional.empty();
                }
                inputValues.put(input.name(), value.get());
            }

            Optional<NormalizedValue> value = evaluate(definition.executorKey(), node, inputValues);
            value.ifPresent(result -> normalized.put(nodeId, result));
            return value;
        } catch (IllegalArgumentException | ArithmeticException ignored) {
            return Optional.empty();
        } finally {
            visiting.remove(nodeId);
        }
    }

    private static Optional<NormalizedValue> evaluate(
            String executorKey,
            ProgramNode node,
            Map<String, NormalizedValue> inputs
    ) {
        return switch (executorKey) {
            case "constant_number" -> number(parseConstant(node, "value", 0.0D));
            case "vector_from_numbers" -> vector(new Vec3(
                    numberInput(inputs, "x"),
                    numberInput(inputs, "y"),
                    numberInput(inputs, "z")
            ));
            case "scale_vector" -> vector(vectorInput(inputs, "vector").scale(numberInput(inputs, "factor")));
            case "number_add" -> number(numberInput(inputs, "a") + numberInput(inputs, "b"));
            case "number_subtract" -> number(numberInput(inputs, "a") - numberInput(inputs, "b"));
            case "number_multiply" -> number(numberInput(inputs, "a") * numberInput(inputs, "b"));
            case "number_divide" -> {
                double divisor = numberInput(inputs, "b");
                if (Math.abs(divisor) < 1.0E-9D) {
                    yield Optional.empty();
                }
                yield number(numberInput(inputs, "a") / divisor);
            }
            case "number_clamp" -> number(Math.max(
                    numberInput(inputs, "min"),
                    Math.min(numberInput(inputs, "max"), numberInput(inputs, "value"))
            ));
            case "number_round" -> number(Math.round(numberInput(inputs, "value")));
            case "number_abs" -> scalar("number_abs", numberInput(inputs, "value"));
            case "number_min" -> scalar("number_min", numberInput(inputs, "a"), numberInput(inputs, "b"));
            case "number_max" -> scalar("number_max", numberInput(inputs, "a"), numberInput(inputs, "b"));
            case "number_power" -> scalar("number_power", numberInput(inputs, "base"), numberInput(inputs, "exponent"));
            case "number_sqrt" -> scalar("number_sqrt", numberInput(inputs, "value"));
            case "number_log" -> scalar("number_log", numberInput(inputs, "value"), numberInput(inputs, "base"));
            case "number_exp" -> scalar("number_exp", numberInput(inputs, "value"));
            case "number_atan2" -> scalar("number_atan2", numberInput(inputs, "y"), numberInput(inputs, "x"));
            case "number_lerp" -> scalar("number_lerp", numberInput(inputs, "a"), numberInput(inputs, "b"), numberInput(inputs, "t"));
            case "number_at_least" -> Optional.of(new NormalizedValue.BoolValue(
                    ScalarOperations.atLeast(numberInput(inputs, "value"), numberInput(inputs, "threshold"))
            ));
            case "number_select" -> scalar(
                    "number_select",
                    boolInput(inputs, "condition") ? 1.0D : 0.0D,
                    numberInput(inputs, "when_true"),
                    numberInput(inputs, "when_false")
            );
            case "number_sin" -> number(MathematicalOperations.sine(numberInput(inputs, "angle")));
            case "number_cos" -> number(MathematicalOperations.cosine(numberInput(inputs, "angle")));
            case "finite_difference" -> number(MathematicalOperations.finiteDifference(
                    numberInput(inputs, "start"),
                    numberInput(inputs, "end"),
                    numberInput(inputs, "step")
            ));
            case "simpson_integral" -> number(MathematicalOperations.simpsonIntegral(
                    numberInput(inputs, "start"),
                    numberInput(inputs, "midpoint"),
                    numberInput(inputs, "end"),
                    numberInput(inputs, "width")
            ));
            case "vector_add" -> vector(vectorInput(inputs, "a").add(vectorInput(inputs, "b")));
            case "vector_subtract" -> vector(vectorInput(inputs, "a").subtract(vectorInput(inputs, "b")));
            case "vector_normalize" -> vector(vectorInput(inputs, "vector").normalize());
            case "vector_length" -> number(vectorInput(inputs, "vector").length());
            case "vector_dot" -> number(vectorInput(inputs, "a").dot(vectorInput(inputs, "b")));
            case "vector_cross" -> vector(vectorInput(inputs, "a").cross(vectorInput(inputs, "b")));
            case "vector_distance" -> number(vectorInput(inputs, "a").distanceTo(vectorInput(inputs, "b")));
            default -> Optional.empty();
        };
    }

    private static Optional<NormalizedValue> number(double value) {
        return Double.isFinite(value)
                ? Optional.of(new NormalizedValue.NumberValue(value))
                : Optional.empty();
    }

    private static Optional<NormalizedValue> scalar(String executorKey, double... values) {
        return number(ScalarOperations.number(executorKey, values));
    }

    private static Optional<NormalizedValue> vector(Vec3 value) {
        return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z)
                ? Optional.of(NormalizedValue.VectorValue.from(value))
                : Optional.empty();
    }

    private static double numberInput(Map<String, NormalizedValue> inputs, String name) {
        if (inputs.get(name) instanceof NormalizedValue.NumberValue number) {
            return number.value();
        }
        throw new IllegalArgumentException("Expected normalized number input '" + name + "'");
    }

    private static Vec3 vectorInput(Map<String, NormalizedValue> inputs, String name) {
        if (inputs.get(name) instanceof NormalizedValue.VectorValue vector) {
            return vector.vector();
        }
        throw new IllegalArgumentException("Expected normalized vector input '" + name + "'");
    }

    private static boolean boolInput(Map<String, NormalizedValue> inputs, String name) {
        if (inputs.get(name) instanceof NormalizedValue.BoolValue bool) {
            return bool.value();
        }
        throw new IllegalArgumentException("Expected normalized bool input '" + name + "'");
    }

    private static double parseConstant(ProgramNode node, String key, double defaultValue) {
        return Double.parseDouble(node.constants().getOrDefault(key, Double.toString(defaultValue)));
    }
}
