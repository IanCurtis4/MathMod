package com.mathmod.physics;

import java.util.List;
import java.util.Set;

/** Minecraft adapter input; it deliberately contains no world or client references. */
public record BlockPhysicalInput(
        String blockId, String canonicalStateId, Set<String> tags, List<PhysicsBox> canonicalCollisionBoxes,
        double hardness, double blastResistance
) {
    public BlockPhysicalInput {
        if (blockId == null || blockId.isBlank() || canonicalStateId == null || canonicalStateId.isBlank()) {
            throw new IllegalArgumentException("Block and canonical state ids are required");
        }
        blockId = blockId.trim(); canonicalStateId = canonicalStateId.trim();
        tags = tags == null ? Set.of() : Set.copyOf(tags);
        canonicalCollisionBoxes = canonicalCollisionBoxes == null ? List.of() : List.copyOf(canonicalCollisionBoxes);
    }
    public String cacheKey() { return canonicalStateId; }
}
