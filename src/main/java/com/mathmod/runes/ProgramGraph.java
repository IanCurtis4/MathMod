package com.mathmod.runes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record ProgramGraph(
        List<ProgramNode> nodes,
        List<ProgramEdge> edges,
        String outputNodeId,
        int budgetLimit
) {
    public static final int CURRENT_VERSION = 1;

    public static final Codec<ProgramGraph> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("version").forGetter(ignored -> CURRENT_VERSION),
            ProgramNode.CODEC.listOf().fieldOf("nodes").forGetter(ProgramGraph::nodes),
            ProgramEdge.CODEC.listOf().optionalFieldOf("edges", List.of()).forGetter(ProgramGraph::edges),
            Codec.STRING.fieldOf("output_node").forGetter(ProgramGraph::outputNodeId),
            Codec.INT.fieldOf("budget_limit").forGetter(ProgramGraph::budgetLimit)
    ).apply(instance, (version, nodes, edges, outputNodeId, budgetLimit) ->
            new ProgramGraph(nodes, edges, outputNodeId, budgetLimit)));

    public ProgramGraph {
        nodes = List.copyOf(nodes);
        edges = List.copyOf(edges);
        outputNodeId = outputNodeId == null ? "" : outputNodeId.trim();
        if (budgetLimit < 0) {
            throw new IllegalArgumentException("budgetLimit must not be negative");
        }
    }
}
