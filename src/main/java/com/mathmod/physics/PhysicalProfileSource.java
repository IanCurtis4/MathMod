package com.mathmod.physics;

public enum PhysicalProfileSource {
    BUILT_IN(0), KUBEJS(1), DATA_PACK(2), FALLBACK(-1);

    private final int precedence;
    PhysicalProfileSource(int precedence) { this.precedence = precedence; }
    public int precedence() { return precedence; }
}
