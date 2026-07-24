package com.mathmod.runes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RuneInput(String name, RuneType type) {
    public static final Codec<RuneInput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(RuneInput::name),
            RuneType.CODEC.fieldOf("type").forGetter(RuneInput::type)
    ).apply(instance, RuneInput::new));

    public RuneInput {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        name = name.trim();
    }
}
