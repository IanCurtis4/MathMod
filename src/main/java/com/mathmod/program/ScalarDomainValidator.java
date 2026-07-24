package com.mathmod.program;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.ValidationIssue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Rejects closed P1 scalar expressions whose domain fails before inscription. */
final class ScalarDomainValidator {
    private ScalarDomainValidator() {
    }

    static List<ValidationIssue> closedDomainIssues(ProgramGraph graph, RuneRegistry registry) {
        boolean hasP1Scalar = graph.nodes().stream()
                .map(node -> registry.find(node.runeId()).map(definition -> definition.executorKey()).orElse(""))
                .anyMatch(executorKey -> ScalarOperations.descriptor(executorKey).isPresent());
        if (!hasP1Scalar) {
            return List.of();
        }
        Map<String, ProgramNode> nodes = new HashMap<>();
        Map<String, Map<String, String>> inputs = new HashMap<>();
        graph.nodes().forEach(node -> nodes.put(node.id(), node));
        for (ProgramEdge edge : graph.edges()) {
            inputs.computeIfAbsent(edge.toNodeId(), ignored -> new HashMap<>()).put(edge.inputName(), edge.fromNodeId());
        }

        ProgramNormalization normalization = ProgramNormalizer.normalize(graph, registry);
        List<ValidationIssue> issues = new ArrayList<>();
        for (ProgramNode node : graph.nodes()) {
            String executor = registry.find(node.runeId()).map(definition -> definition.executorKey()).orElse("");
            if (ScalarOperations.descriptor(executor).isPresent()
                    && closed(node.id(), nodes, inputs, registry, new HashSet<>())
                    && !normalization.valuesByNode().containsKey(node.id())) {
                issues.add(ValidationIssue.localizedError(
                        node.id(),
                        "Closed scalar expression violates its mathematical domain.",
                        "validation.mathmod.scalar_domain",
                        node.runeId()
                ));
            }
        }
        return List.copyOf(issues);
    }

    private static boolean closed(
            String nodeId,
            Map<String, ProgramNode> nodes,
            Map<String, Map<String, String>> inputs,
            RuneRegistry registry,
            Set<String> visiting
    ) {
        ProgramNode node = nodes.get(nodeId);
        if (node == null || !visiting.add(nodeId)) {
            return false;
        }
        try {
            String executor = registry.find(node.runeId()).map(definition -> definition.executorKey()).orElse("");
            if (executor.equals("constant_number")) {
                return true;
            }
            if (ScalarOperations.descriptor(executor).isEmpty()) {
                return false;
            }
            for (String input : ScalarOperations.descriptor(executor).orElseThrow().inputNames()) {
                String source = inputs.getOrDefault(nodeId, Map.of()).get(input);
                if (source == null || !closed(source, nodes, inputs, registry, visiting)) {
                    return false;
                }
            }
            return true;
        } finally {
            visiting.remove(nodeId);
        }
    }
}
