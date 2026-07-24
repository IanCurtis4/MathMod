package com.mathmod.runes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ProgramEdge(String fromNodeId, String toNodeId, String inputName) {
    public static final Codec<ProgramEdge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("from").forGetter(ProgramEdge::fromNodeId),
            Codec.STRING.fieldOf("to").forGetter(ProgramEdge::toNodeId),
            Codec.STRING.fieldOf("input").forGetter(ProgramEdge::inputName)
    ).apply(instance, ProgramEdge::new));

    public ProgramEdge {
        fromNodeId = requireId(fromNodeId, "fromNodeId");
        toNodeId = requireId(toNodeId, "toNodeId");
        inputName = requireId(inputName, "inputName");
    }

    private static String requireId(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
