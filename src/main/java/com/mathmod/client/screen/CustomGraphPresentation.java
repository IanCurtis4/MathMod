package com.mathmod.client.screen;

import com.mathmod.program.CustomSpellStep;
import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class CustomGraphPresentation {
    private static final String CONSTANT_NUMBER = "mathmod:constant_number";
    private static final String VECTOR_FROM_NUMBERS = "mathmod:vector_from_numbers";

    private CustomGraphPresentation() {
    }

    static List<Binding> bindings(ProgramGraph graph, List<CustomSpellStep> steps) {
        Map<String, ProgramNode> nodes = new LinkedHashMap<>();
        graph.nodes().forEach(node -> nodes.put(node.id(), node));

        Map<String, Integer> stepNumbers = new HashMap<>();
        for (int index = 0; index < steps.size(); index++) {
            stepNumbers.put(steps.get(index).outputNodeId(), index + 1);
        }

        Map<String, Map<String, ProgramNode>> inputs = inputNodes(graph.edges(), nodes);
        List<Binding> bindings = new ArrayList<>();
        for (ProgramEdge edge : graph.edges()) {
            ProgramNode source = nodes.get(edge.fromNodeId());
            ProgramNode target = nodes.get(edge.toNodeId());
            if (source == null || target == null) {
                continue;
            }
            if (VECTOR_FROM_NUMBERS.equals(target.runeId()) && vectorLiteral(target, inputs) != null) {
                continue;
            }
            bindings.add(new Binding(
                    source,
                    target,
                    edge.inputName(),
                    stepNumbers.getOrDefault(source.id(), 0),
                    stepNumbers.getOrDefault(target.id(), 0),
                    literal(source, inputs)
            ));
        }
        return List.copyOf(bindings);
    }

    static String symbol(ProgramNode node) {
        String id = node.id();
        int separator = id.lastIndexOf('_');
        if (separator < 1 || separator == id.length() - 1) {
            return id;
        }
        for (int index = separator + 1; index < id.length(); index++) {
            if (!Character.isDigit(id.charAt(index))) {
                return id;
            }
        }
        return id.substring(0, separator);
    }

    private static Map<String, Map<String, ProgramNode>> inputNodes(
            List<ProgramEdge> edges,
            Map<String, ProgramNode> nodes
    ) {
        Map<String, Map<String, ProgramNode>> inputs = new HashMap<>();
        for (ProgramEdge edge : edges) {
            ProgramNode source = nodes.get(edge.fromNodeId());
            if (source != null) {
                inputs.computeIfAbsent(edge.toNodeId(), ignored -> new HashMap<>())
                        .put(edge.inputName(), source);
            }
        }
        return inputs;
    }

    private static String literal(ProgramNode node, Map<String, Map<String, ProgramNode>> inputs) {
        if (CONSTANT_NUMBER.equals(node.runeId())) {
            return node.constants().get("value");
        }
        if (VECTOR_FROM_NUMBERS.equals(node.runeId())) {
            return vectorLiteral(node, inputs);
        }
        return null;
    }

    private static String vectorLiteral(ProgramNode node, Map<String, Map<String, ProgramNode>> inputs) {
        Map<String, ProgramNode> components = inputs.get(node.id());
        if (components == null) {
            return null;
        }
        String x = numberValue(components.get("x"));
        String y = numberValue(components.get("y"));
        String z = numberValue(components.get("z"));
        if (x == null || y == null || z == null) {
            return null;
        }
        return "vec(" + x + ", " + y + ", " + z + ")";
    }

    private static String numberValue(ProgramNode node) {
        return node != null && CONSTANT_NUMBER.equals(node.runeId())
                ? node.constants().get("value")
                : null;
    }

    record Binding(
            ProgramNode source,
            ProgramNode target,
            String inputName,
            int sourceStep,
            int targetStep,
            String sourceLiteral
    ) {
    }
}
