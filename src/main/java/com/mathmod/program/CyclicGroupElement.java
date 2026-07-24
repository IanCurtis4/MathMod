package com.mathmod.program;

/**
 * An element of the finite cyclic group C_order, represented by a normalized
 * integer residue.
 */
public record CyclicGroupElement(int order, int value) {
    public static final int MIN_ORDER = 2;
    public static final int MAX_ORDER = 64;

    public CyclicGroupElement {
        if (order < MIN_ORDER || order > MAX_ORDER) {
            throw new IllegalArgumentException("Cyclic group order must be between 2 and 64");
        }
        value = Math.floorMod(value, order);
    }

    public CyclicGroupElement compose(CyclicGroupElement other) {
        requireSameGroup(other);
        return new CyclicGroupElement(order, value + other.value);
    }

    public CyclicGroupElement inverse() {
        return new CyclicGroupElement(order, -value);
    }

    public double angleRadians() {
        return Math.PI * 2.0D * value / order;
    }

    private void requireSameGroup(CyclicGroupElement other) {
        if (other == null || order != other.order) {
            throw new IllegalArgumentException("Cyclic elements must belong to the same group");
        }
    }
}
