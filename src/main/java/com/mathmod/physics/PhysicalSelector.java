package com.mathmod.physics;

import java.util.Set;

public record PhysicalSelector(Kind kind, String id) {
    public enum Kind { BLOCK, TAG }

    public PhysicalSelector {
        if (kind == null || id == null || id.isBlank()) throw new IllegalArgumentException("Selector is required");
        id = id.trim();
    }

    boolean matches(BlockPhysicalInput input) {
        return kind == Kind.BLOCK ? id.equals(input.blockId()) : input.tags().contains(id);
    }
}
