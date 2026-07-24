package com.mathmod.runes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

public record MaterialRequirement(String itemOrTag, int quantity) {
    public static final Codec<MaterialRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("item_or_tag").forGetter(MaterialRequirement::itemOrTag),
            Codec.INT.fieldOf("quantity").forGetter(MaterialRequirement::quantity)
    ).apply(instance, MaterialRequirement::new));

    public MaterialRequirement {
        if (itemOrTag == null || itemOrTag.isBlank()) {
            throw new IllegalArgumentException("itemOrTag must not be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        itemOrTag = itemOrTag.trim();
    }

    public boolean isTag() {
        return itemOrTag.startsWith("#");
    }

    @Override
    public String toString() {
        return quantity + "x " + Objects.toString(itemOrTag);
    }
}
