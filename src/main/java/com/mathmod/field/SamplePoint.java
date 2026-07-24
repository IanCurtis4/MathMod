package com.mathmod.field;

/** Exact world-coordinate sample point. Values are finite and stable cache keys. */
public record SamplePoint(double x, double y, double z) {
    public SamplePoint {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Sample coordinates must be finite");
        }
        x = canonicalZero(x);
        y = canonicalZero(y);
        z = canonicalZero(z);
    }

    public SamplePoint offset(double dx, double dy, double dz) {
        return new SamplePoint(x + dx, y + dy, z + dz);
    }

    public double distanceSquared(SamplePoint other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    private static double canonicalZero(double value) {
        return value == 0.0D ? 0.0D : value;
    }
}
