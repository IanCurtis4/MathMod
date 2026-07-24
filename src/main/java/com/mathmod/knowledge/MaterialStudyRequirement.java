package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.Objects;

public record MaterialStudyRequirement(NamespacedId materialId, int tier, int successfulCasts) {
    public MaterialStudyRequirement {
        materialId = Objects.requireNonNull(materialId, "materialId");
        if (tier < 1 || tier > 4) {
            throw new IllegalArgumentException("tier must be between 1 and 4");
        }
        if (successfulCasts < 1 || successfulCasts > PlayerKnowledge.MAX_PROGRESS_VALUE) {
            throw new IllegalArgumentException("successfulCasts is out of bounds");
        }
    }
}
