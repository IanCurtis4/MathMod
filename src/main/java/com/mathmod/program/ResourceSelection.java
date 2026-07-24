package com.mathmod.program;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ResourceSelection(String materialId, int quantity) {
    public static final Codec<ResourceSelection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("material").forGetter(ResourceSelection::materialId),
            Codec.INT.fieldOf("quantity").forGetter(ResourceSelection::quantity)
    ).apply(instance, ResourceSelection::new));

    public ResourceSelection {
        if (materialId == null || materialId.isBlank()) {
            throw new IllegalArgumentException("materialId must not be blank");
        }
        materialId = materialId.trim();
        quantity = Math.max(1, Math.min(64, quantity));
    }
}
