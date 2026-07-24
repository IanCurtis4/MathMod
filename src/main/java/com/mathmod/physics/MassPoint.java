package com.mathmod.physics;

public record MassPoint(PhysicsVector position, double mass) {
    public MassPoint {
        if (position == null || !Double.isFinite(mass) || mass < 0 || mass > 256) throw new IllegalArgumentException("Invalid mass point");
    }
}
