package com.mathmod.runes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;

public record ProgramNode(String id, String runeId, Map<String, String> constants) {
    public static final Codec<ProgramNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(ProgramNode::id),
            Codec.STRING.fieldOf("rune").forGetter(ProgramNode::runeId),
            Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .optionalFieldOf("constants", Map.of())
                    .forGetter(ProgramNode::constants)
    ).apply(instance, ProgramNode::new));

    public ProgramNode {
        id = requireId(id, "id");
        runeId = requireId(runeId, "runeId");
        constants = constants == null ? Map.of() : Map.copyOf(constants);
    }

    public ProgramNode(String id, String runeId) {
        this(id, runeId, Map.of());
    }

    private static String requireId(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
