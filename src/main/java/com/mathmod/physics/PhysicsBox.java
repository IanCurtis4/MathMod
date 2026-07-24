package com.mathmod.physics;

/** Axis-aligned canonical-shape box. Coordinates may be outside the unit cube. */
public record PhysicsBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    public PhysicsBox {
        if (!finite(minX, minY, minZ, maxX, maxY, maxZ)) throw new IllegalArgumentException("Box must be finite");
    }

    public PhysicsBox clampedUnitCube() {
        return new PhysicsBox(clamp(minX), clamp(minY), clamp(minZ), clamp(maxX), clamp(maxY), clamp(maxZ));
    }

    public boolean hasPositiveExtent() { return maxX > minX && maxY > minY && maxZ > minZ; }

    public boolean containsCenter(double x, double y, double z) {
        return x >= minX && x < maxX && y >= minY && y < maxY && z >= minZ && z < maxZ;
    }

    private static double clamp(double value) { return Math.max(0.0D, Math.min(1.0D, value)); }
    private static boolean finite(double... values) {
        for (double value : values) if (!Double.isFinite(value)) return false;
        return true;
    }
}
