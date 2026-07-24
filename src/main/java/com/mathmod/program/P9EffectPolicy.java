package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.ValidationIssue;

import java.util.List;
import java.util.Set;

/** Structural guardrails for the player-facing defensive alchemy slice. */
public final class P9EffectPolicy {
    private static final Set<String> DEFENSIVE_EXECUTORS = Set.of(
            "cleanse_entities_plan",
            "resistance_entities_plan",
            "absorption_entities_plan"
    );

    private P9EffectPolicy() {
    }

    public static boolean usesDefensiveAlchemy(ProgramGraph graph, RuneRegistry registry) {
        return graph.nodes().stream()
                .map(ProgramNode::runeId)
                .map(registry::find)
                .flatMap(java.util.Optional::stream)
                .map(RuneDefinition::executorKey)
                .anyMatch(DEFENSIVE_EXECUTORS::contains);
    }

    public static List<ValidationIssue> structuralIssues(ProgramGraph graph, RuneRegistry registry) {
        List<ProgramNode> defensiveNodes = graph.nodes().stream()
                .filter(node -> registry.find(node.runeId())
                        .map(RuneDefinition::executorKey)
                        .filter(DEFENSIVE_EXECUTORS::contains)
                        .isPresent())
                .toList();
        if (defensiveNodes.isEmpty()) {
            return List.of();
        }
        if (defensiveNodes.size() != 1) {
            return List.of(issue(defensiveNodes.getFirst().id(), "A defensive alchemy proof may contain exactly one defensive plan."));
        }

        ProgramNode defensive = defensiveNodes.getFirst();
        ProgramNode output = graph.nodes().stream()
                .filter(node -> node.id().equals(graph.outputNodeId()))
                .findFirst()
                .orElse(null);
        boolean outputExecutesPlan = output != null && registry.find(output.runeId())
                .map(RuneDefinition::executorKey)
                .filter("execute_effect_plan"::equals)
                .isPresent();
        boolean directExecution = graph.edges().stream().anyMatch(edge ->
                edge.fromNodeId().equals(defensive.id())
                        && edge.toNodeId().equals(graph.outputNodeId())
                        && edge.inputName().equals("plan")
        );
        if (!outputExecutesPlan || !directExecution) {
            return List.of(issue(defensive.id(), "A defensive alchemy plan must be the direct input of the final execution rune."));
        }
        return List.of();
    }

    private static ValidationIssue issue(String nodeId, String fallback) {
        return ValidationIssue.localizedError(
                nodeId,
                fallback,
                "validation.mathmod.p9_defensive_shape"
        );
    }
}
