package com.mathmod.runes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ProgramValidator {
    public static final int MAX_NODES = 64;
    public static final int MAX_EDGES = 128;
    public static final int MAX_BUDGET_LIMIT = 128;

    private final RuneRegistry runeRegistry;

    public ProgramValidator(RuneRegistry runeRegistry) {
        this.runeRegistry = runeRegistry;
    }

    public ValidationResult validate(ProgramGraph graph) {
        List<ValidationIssue> issues = new ArrayList<>();
        Map<String, ProgramNode> nodesById = new LinkedHashMap<>();
        Map<String, RuneDefinition> definitionsByNode = new HashMap<>();

        if (graph.nodes().size() > MAX_NODES) {
            issues.add(ValidationIssue.localizedError(
                    null,
                    "Program has " + graph.nodes().size() + " nodes, but the maximum is " + MAX_NODES + ".",
                    "validation.mathmod.too_many_nodes",
                    graph.nodes().size(),
                    MAX_NODES
            ));
        }

        if (graph.edges().size() > MAX_EDGES) {
            issues.add(ValidationIssue.localizedError(
                    null,
                    "Program has " + graph.edges().size() + " edges, but the maximum is " + MAX_EDGES + ".",
                    "validation.mathmod.too_many_edges",
                    graph.edges().size(),
                    MAX_EDGES
            ));
        }

        if (graph.budgetLimit() > MAX_BUDGET_LIMIT) {
            issues.add(ValidationIssue.localizedError(
                    null,
                    "Program budget limit " + graph.budgetLimit() + " exceeds maximum " + MAX_BUDGET_LIMIT + ".",
                    "validation.mathmod.budget_limit_too_high",
                    graph.budgetLimit(),
                    MAX_BUDGET_LIMIT
            ));
        }

        for (ProgramNode node : graph.nodes()) {
            if (nodesById.putIfAbsent(node.id(), node) != null) {
                issues.add(ValidationIssue.localizedError(
                        node.id(),
                        "Duplicate node id '" + node.id() + "'.",
                        "validation.mathmod.duplicate_node",
                        node.id()
                ));
            }
        }

        int budgetUsed = 0;
        for (ProgramNode node : graph.nodes()) {
            Optional<RuneDefinition> definition = runeRegistry.find(node.runeId());
            if (definition.isEmpty()) {
                issues.add(ValidationIssue.localizedError(
                        node.id(),
                        "Unknown rune '" + node.runeId() + "'.",
                        "validation.mathmod.unknown_rune",
                        node.runeId()
                ));
                continue;
            }
            if (!definition.get().enabled()) {
                issues.add(ValidationIssue.localizedError(
                        node.id(),
                        "Rune '" + node.runeId() + "' is disabled.",
                        "validation.mathmod.rune_disabled",
                        node.runeId()
                ));
            }
            definitionsByNode.put(node.id(), definition.get());
            budgetUsed += definition.get().budgetCost();
        }

        if (budgetUsed > graph.budgetLimit()) {
            issues.add(ValidationIssue.localizedError(
                    null,
                    "Program budget " + budgetUsed + " exceeds limit " + graph.budgetLimit() + ".",
                    "validation.mathmod.budget_exceeded",
                    budgetUsed,
                    graph.budgetLimit()
            ));
        }

        if (graph.outputNodeId().isBlank()) {
            issues.add(ValidationIssue.localizedError(
                    null,
                    "Program must designate exactly one output node.",
                    "validation.mathmod.output_required"
            ));
        } else if (!nodesById.containsKey(graph.outputNodeId())) {
            issues.add(ValidationIssue.localizedError(
                    graph.outputNodeId(),
                    "Output node does not exist.",
                    "validation.mathmod.output_missing",
                    graph.outputNodeId()
            ));
        }

        Map<String, List<String>> outgoing = new HashMap<>();
        Map<String, ProgramEdge> connectedInputs = new HashMap<>();

        for (ProgramEdge edge : graph.edges()) {
            ProgramNode source = nodesById.get(edge.fromNodeId());
            ProgramNode target = nodesById.get(edge.toNodeId());
            if (source == null) {
                issues.add(ValidationIssue.localizedError(
                        edge.fromNodeId(),
                        "Edge source node does not exist.",
                        "validation.mathmod.edge_source_missing",
                        edge.fromNodeId()
                ));
                continue;
            }
            if (target == null) {
                issues.add(ValidationIssue.localizedError(
                        edge.toNodeId(),
                        "Edge target node does not exist.",
                        "validation.mathmod.edge_target_missing",
                        edge.toNodeId()
                ));
                continue;
            }

            outgoing.computeIfAbsent(edge.fromNodeId(), ignored -> new ArrayList<>()).add(edge.toNodeId());

            RuneDefinition sourceDefinition = definitionsByNode.get(source.id());
            RuneDefinition targetDefinition = definitionsByNode.get(target.id());
            if (sourceDefinition == null || targetDefinition == null) {
                continue;
            }

            Optional<RuneInput> targetInput = targetDefinition.input(edge.inputName());
            if (targetInput.isEmpty()) {
                issues.add(ValidationIssue.localizedError(
                        target.id(),
                        "Rune '" + target.runeId() + "' has no input named '" + edge.inputName() + "'.",
                        "validation.mathmod.input_unknown",
                        target.runeId(),
                        edge.inputName()
                ));
                continue;
            }

            String inputKey = target.id() + ":" + edge.inputName();
            if (connectedInputs.putIfAbsent(inputKey, edge) != null) {
                issues.add(ValidationIssue.localizedError(
                        target.id(),
                        "Input '" + edge.inputName() + "' on node '" + target.id() + "' is connected more than once.",
                        "validation.mathmod.input_connected_twice",
                        edge.inputName(),
                        target.id()
                ));
            }

            if (sourceDefinition.outputType() != targetInput.get().type()) {
                issues.add(ValidationIssue.localizedError(
                        target.id(),
                        "Type mismatch for input '" + edge.inputName() + "': expected "
                                + targetInput.get().type().id() + " but got " + sourceDefinition.outputType().id() + ".",
                        "validation.mathmod.type_mismatch",
                        edge.inputName(),
                        targetInput.get().type().id(),
                        sourceDefinition.outputType().id()
                ));
            }
        }

        for (ProgramNode node : graph.nodes()) {
            RuneDefinition definition = definitionsByNode.get(node.id());
            if (definition == null) {
                continue;
            }
            for (RuneInput input : definition.inputs()) {
                if (!connectedInputs.containsKey(node.id() + ":" + input.name())) {
                    issues.add(ValidationIssue.localizedError(
                            node.id(),
                            "Missing required input '" + input.name() + "' for rune '" + definition.id() + "'.",
                            "validation.mathmod.input_required",
                            input.name(),
                            definition.id()
                    ));
                }
            }
        }

        detectCycles(nodesById.keySet(), outgoing, issues);

        RuneType outputType = null;
        RuneDefinition outputDefinition = definitionsByNode.get(graph.outputNodeId());
        if (outputDefinition != null) {
            outputType = outputDefinition.outputType();
        }

        return new ValidationResult(issues, budgetUsed, outputType);
    }

    private static void detectCycles(Set<String> nodeIds, Map<String, List<String>> outgoing, List<ValidationIssue> issues) {
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();
        ArrayDeque<String> stack = new ArrayDeque<>();

        for (String nodeId : nodeIds) {
            if (visit(nodeId, outgoing, visited, visiting, stack, issues)) {
                return;
            }
        }
    }

    private static boolean visit(
            String nodeId,
            Map<String, List<String>> outgoing,
            Set<String> visited,
            Set<String> visiting,
            ArrayDeque<String> stack,
            List<ValidationIssue> issues
    ) {
        if (visited.contains(nodeId)) {
            return false;
        }
        if (!visiting.add(nodeId)) {
            issues.add(ValidationIssue.localizedError(
                    nodeId,
                    "Program graph contains a cycle near node '" + nodeId + "'.",
                    "validation.mathmod.cycle",
                    nodeId
            ));
            return true;
        }

        stack.push(nodeId);
        for (String next : outgoing.getOrDefault(nodeId, List.of())) {
            if (visit(next, outgoing, visited, visiting, stack, issues)) {
                return true;
            }
        }
        stack.pop();
        visiting.remove(nodeId);
        visited.add(nodeId);
        return false;
    }
}
