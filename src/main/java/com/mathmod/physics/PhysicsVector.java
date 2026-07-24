package com.mathmod.physics;

/** Immutable vector used by the Minecraft-free derived-physics core. */
public record PhysicsVector(double x, double y, double z) {
    public static final PhysicsVector ZERO = new PhysicsVector(0, 0, 0);

    public PhysicsVector {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Physics vector must be finite");
        }
    }

    public PhysicsVector add(PhysicsVector other) { return new PhysicsVector(x + other.x, y + other.y, z + other.z); }
    public PhysicsVector subtract(PhysicsVector other) { return new PhysicsVector(x - other.x, y - other.y, z - other.z); }
    public PhysicsVector scale(double scalar) { return new PhysicsVector(x * scalar, y * scalar, z * scalar); }
    public double dot(PhysicsVector other) { return x * other.x + y * other.y + z * other.z; }
    public double lengthSquared() { return dot(this); }
    public PhysicsVector normalized() {
        double length = Math.sqrt(lengthSquared());
        if (length <= 1.0E-9D) throw new IllegalArgumentException("Cannot normalize zero vector");
        return scale(1.0D / length);
    }
}
