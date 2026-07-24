package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;

import java.util.Map;

/** Read-only, client-safe summary for previews of a construct spell. */
public record ConstructPreviewModel(
        String materialId,
        double scale,
        double angularSpeed,
        int maximumMassEquivalent,
        int maximumCandidateCount,
        int maximumLifetimeTicks,
        double maximumLaunchSpeed,
        boolean serverAuthoritative
) {
    public static final int MAX_CANDIDATES = 128;
    public static final int MAX_LIFETIME_TICKS = 100;
    public static final double MAX_LAUNCH_SPEED = 2.0D;

    public ConstructPreviewModel {
        if (materialId == null || materialId.isBlank()) {
            throw new IllegalArgumentException("Preview material cannot be blank");
        }
        if (!Double.isFinite(scale) || scale < 0.25D || scale > 1.0D) {
            throw new IllegalArgumentException("Preview scale is outside the construct policy");
        }
        if (!Double.isFinite(angularSpeed) || Math.abs(angularSpeed) > Math.PI / 4.0D) {
            throw new IllegalArgumentException("Preview angular speed is outside the construct policy");
        }
        if (maximumMassEquivalent <= 0 || maximumMassEquivalent > MAX_CANDIDATES
                || maximumCandidateCount <= 0 || maximumCandidateCount > MAX_CANDIDATES
                || maximumLifetimeTicks != MAX_LIFETIME_TICKS
                || maximumLaunchSpeed != MAX_LAUNCH_SPEED
                || !serverAuthoritative) {
            throw new IllegalArgumentException("Preview limits must describe the server policy");
        }
    }

    public static ConstructPreviewModel cavalieriProjectile() {
        return new ConstructPreviewModel(
                "minecraft:stone", 0.5D, 0.35D, MAX_CANDIDATES, MAX_CANDIDATES,
                MAX_LIFETIME_TICKS, MAX_LAUNCH_SPEED, true
        );
    }

    public static ConstructPreviewModel from(ProgramGraph graph) {
        ProgramNode materialize = graph.nodes().stream()
                .filter(node -> node.runeId().equals("mathmod:materialize_construct"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Graph has no construct materialization"));
        ProgramNode compress = graph.nodes().stream()
                .filter(node -> node.runeId().equals("mathmod:compress_construct"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Graph has no construct compression"));
        ProgramNode spin = graph.nodes().stream()
                .filter(node -> node.runeId().equals("mathmod:spin_construct"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Graph has no construct spin"));
        return new ConstructPreviewModel(
                materialize.constants().getOrDefault("material", "minecraft:stone"),
                connectedNumber(graph, compress.id(), "scale", 1.0D),
                connectedNumber(graph, spin.id(), "speed", 0.0D),
                MAX_CANDIDATES,
                MAX_CANDIDATES,
                MAX_LIFETIME_TICKS,
                MAX_LAUNCH_SPEED,
                true
        );
    }

    private static double number(Map<String, String> constants, String key, double fallback) {
        try {
            return Double.parseDouble(constants.getOrDefault(key, Double.toString(fallback)));
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private static double connectedNumber(ProgramGraph graph, String targetId, String inputName, double fallback) {
        return graph.edges().stream()
                .filter(edge -> edge.toNodeId().equals(targetId) && edge.inputName().equals(inputName))
                .findFirst()
                .flatMap(edge -> graph.nodes().stream()
                        .filter(node -> node.id().equals(edge.fromNodeId()))
                        .findFirst())
                .map(node -> number(node.constants(), "value", fallback))
                .orElse(fallback);
    }
}
