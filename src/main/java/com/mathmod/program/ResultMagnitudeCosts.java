package com.mathmod.program;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;

public final class ResultMagnitudeCosts {
    private static final int MAX_PRECISION_SURCHARGE = 8;

    private ResultMagnitudeCosts() {
    }

    public static Map<String, Integer> attributeRequirements(ProgramGraph graph) {
        return legacyAttributeRequirements(graph);
    }

    private static Map<String, Integer> legacyAttributeRequirements(ProgramGraph graph) {
        Map<String, ProgramNode> nodes = new HashMap<>();
        graph.nodes().forEach(node -> nodes.put(node.id(), node));
        Map<String, Map<String, String>> inputs = new HashMap<>();
        for (ProgramEdge edge : graph.edges()) {
            inputs.computeIfAbsent(edge.toNodeId(), ignored -> new HashMap<>())
                    .put(edge.inputName(), edge.fromNodeId());
        }
        Map<String, Integer> requirements = new LinkedHashMap<>();
        graph.nodes().stream()
                .filter(node -> node.runeId().equals("mathmod:finite_difference")
                        || node.runeId().equals("mathmod:simpson_integral")
                        || ScalarOperations.descriptor(node.runeId().replace("mathmod:", ""))
                        .map(descriptor -> ScalarOperations.isNonLinear(descriptor.executorKey()))
                        .orElse(false))
                .forEach(node -> {
                    OptionalDouble result = evaluate(node.id(), nodes, inputs, new HashMap<>(), new HashSet<>());
                    if (result.isPresent()) {
                        int amount = precisionSurcharge(result.getAsDouble());
                        if (amount > 0) {
                            requirements.merge("precision", amount, Integer::sum);
                        }
                    }
                });
        return Map.copyOf(requirements);
    }

    private static OptionalDouble evaluate(
            String nodeId,
            Map<String, ProgramNode> nodes,
            Map<String, Map<String, String>> inputs,
            Map<String, Double> cache,
            Set<String> visiting
    ) {
        if (cache.containsKey(nodeId)) {
            return OptionalDouble.of(cache.get(nodeId));
        }
        ProgramNode node = nodes.get(nodeId);
        if (node == null || !visiting.add(nodeId)) {
            return OptionalDouble.empty();
        }
        try {
            Map<String, String> sources = inputs.getOrDefault(nodeId, Map.of());
            OptionalDouble value = switch (node.runeId()) {
                case "mathmod:constant_number" -> parse(node.constants().getOrDefault("value", "0"));
                case "mathmod:number_add" -> binary("a", "b", sources, nodes, inputs, cache, visiting, (a, b) -> a + b);
                case "mathmod:number_subtract" -> binary("a", "b", sources, nodes, inputs, cache, visiting, (a, b) -> a - b);
                case "mathmod:number_multiply" -> binary("a", "b", sources, nodes, inputs, cache, visiting, (a, b) -> a * b);
                case "mathmod:number_divide" -> binary("a", "b", sources, nodes, inputs, cache, visiting,
                        (a, b) -> Math.abs(b) < 1.0E-9D ? Double.NaN : a / b);
                case "mathmod:number_abs" -> unary("value", sources, nodes, inputs, cache, visiting,
                        inputValue -> ScalarOperations.number("number_abs", inputValue));
                case "mathmod:number_min" -> binary("a", "b", sources, nodes, inputs, cache, visiting,
                        (a, b) -> ScalarOperations.number("number_min", a, b));
                case "mathmod:number_max" -> binary("a", "b", sources, nodes, inputs, cache, visiting,
                        (a, b) -> ScalarOperations.number("number_max", a, b));
                case "mathmod:number_power" -> binary("base", "exponent", sources, nodes, inputs, cache, visiting,
                        (a, b) -> ScalarOperations.number("number_power", a, b));
                case "mathmod:number_sqrt" -> unary("value", sources, nodes, inputs, cache, visiting,
                        inputValue -> ScalarOperations.number("number_sqrt", inputValue));
                case "mathmod:number_log" -> binary("value", "base", sources, nodes, inputs, cache, visiting,
                        (a, b) -> ScalarOperations.number("number_log", a, b));
                case "mathmod:number_exp" -> unary("value", sources, nodes, inputs, cache, visiting,
                        inputValue -> ScalarOperations.number("number_exp", inputValue));
                case "mathmod:number_atan2" -> binary("y", "x", sources, nodes, inputs, cache, visiting,
                        (a, b) -> ScalarOperations.number("number_atan2", a, b));
                case "mathmod:number_lerp" -> ternary("a", "b", "t", sources, nodes, inputs, cache, visiting,
                        (a, b, t) -> ScalarOperations.number("number_lerp", a, b, t));
                case "mathmod:finite_difference" -> ternary(
                        "start", "end", "step", sources, nodes, inputs, cache, visiting,
                        (start, end, step) -> Math.abs(step) < 1.0E-9D ? Double.NaN : (end - start) / step
                );
                case "mathmod:simpson_integral" -> quaternary(
                        "start", "midpoint", "end", "width", sources, nodes, inputs, cache, visiting,
                        (start, midpoint, end, width) -> width * (start + 4.0D * midpoint + end) / 6.0D
                );
                default -> OptionalDouble.empty();
            };
            if (value.isPresent() && Double.isFinite(value.getAsDouble())) {
                cache.put(nodeId, value.getAsDouble());
                return value;
            }
            return OptionalDouble.empty();
        } finally {
            visiting.remove(nodeId);
        }
    }

    private static OptionalDouble binary(
            String first, String second, Map<String, String> sources,
            Map<String, ProgramNode> nodes, Map<String, Map<String, String>> inputs,
            Map<String, Double> cache, Set<String> visiting, Binary operation
    ) {
        OptionalDouble a = input(first, sources, nodes, inputs, cache, visiting);
        OptionalDouble b = input(second, sources, nodes, inputs, cache, visiting);
        return a.isPresent() && b.isPresent()
                ? finite(operation.apply(a.getAsDouble(), b.getAsDouble()))
                : OptionalDouble.empty();
    }

    private static OptionalDouble unary(
            String inputName, Map<String, String> sources,
            Map<String, ProgramNode> nodes, Map<String, Map<String, String>> inputs,
            Map<String, Double> cache, Set<String> visiting, Unary operation
    ) {
        OptionalDouble value = input(inputName, sources, nodes, inputs, cache, visiting);
        return value.isPresent() ? finite(operation.apply(value.getAsDouble())) : OptionalDouble.empty();
    }

    private static OptionalDouble ternary(
            String first, String second, String third, Map<String, String> sources,
            Map<String, ProgramNode> nodes, Map<String, Map<String, String>> inputs,
            Map<String, Double> cache, Set<String> visiting, Ternary operation
    ) {
        OptionalDouble a = input(first, sources, nodes, inputs, cache, visiting);
        OptionalDouble b = input(second, sources, nodes, inputs, cache, visiting);
        OptionalDouble c = input(third, sources, nodes, inputs, cache, visiting);
        return a.isPresent() && b.isPresent() && c.isPresent()
                ? finite(operation.apply(a.getAsDouble(), b.getAsDouble(), c.getAsDouble()))
                : OptionalDouble.empty();
    }

    private static OptionalDouble quaternary(
            String first, String second, String third, String fourth, Map<String, String> sources,
            Map<String, ProgramNode> nodes, Map<String, Map<String, String>> inputs,
            Map<String, Double> cache, Set<String> visiting, Quaternary operation
    ) {
        OptionalDouble a = input(first, sources, nodes, inputs, cache, visiting);
        OptionalDouble b = input(second, sources, nodes, inputs, cache, visiting);
        OptionalDouble c = input(third, sources, nodes, inputs, cache, visiting);
        OptionalDouble d = input(fourth, sources, nodes, inputs, cache, visiting);
        return a.isPresent() && b.isPresent() && c.isPresent() && d.isPresent()
                ? finite(operation.apply(a.getAsDouble(), b.getAsDouble(), c.getAsDouble(), d.getAsDouble()))
                : OptionalDouble.empty();
    }

    private static OptionalDouble input(
            String name, Map<String, String> sources, Map<String, ProgramNode> nodes,
            Map<String, Map<String, String>> inputs, Map<String, Double> cache, Set<String> visiting
    ) {
        String source = sources.get(name);
        return source == null ? OptionalDouble.empty() : evaluate(source, nodes, inputs, cache, visiting);
    }

    private static OptionalDouble parse(String value) {
        try {
            return finite(Double.parseDouble(value));
        } catch (NumberFormatException ignored) {
            return OptionalDouble.empty();
        }
    }

    private static OptionalDouble finite(double value) {
        return Double.isFinite(value) ? OptionalDouble.of(value) : OptionalDouble.empty();
    }

    @FunctionalInterface
    private interface Binary {
        double apply(double a, double b);
    }

    @FunctionalInterface
    private interface Unary {
        double apply(double value);
    }

    @FunctionalInterface
    private interface Ternary {
        double apply(double a, double b, double c);
    }

    @FunctionalInterface
    private interface Quaternary {
        double apply(double a, double b, double c, double d);
    }

    static int precisionSurcharge(double result) {
        double magnitude = Math.abs(result);
        if (!Double.isFinite(magnitude) || magnitude <= 1.0D) {
            return 0;
        }
        return Math.min(
                MAX_PRECISION_SURCHARGE,
                Math.max(1, (int) Math.ceil(Math.log(magnitude) / Math.log(2.0D)))
        );
    }
}
