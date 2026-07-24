package com.mathmod.physics;

/** Symmetric 3x3 tensor in block-mass times block-squared units. */
public record InertiaTensor(double xx, double yy, double zz, double xy, double xz, double yz) {
    public static final InertiaTensor ZERO = new InertiaTensor(0, 0, 0, 0, 0, 0);
    public InertiaTensor {
        if (!finite(xx, yy, zz, xy, xz, yz)) throw new IllegalArgumentException("Tensor must be finite");
    }
    public InertiaTensor add(InertiaTensor other) { return new InertiaTensor(xx + other.xx, yy + other.yy, zz + other.zz, xy + other.xy, xz + other.xz, yz + other.yz); }
    public InertiaTensor scale(double factor) { return new InertiaTensor(xx * factor, yy * factor, zz * factor, xy * factor, xz * factor, yz * factor); }
    public double project(PhysicsVector axis) {
        PhysicsVector n = axis.normalized();
        return Math.max(0, xx * n.x() * n.x() + yy * n.y() * n.y() + zz * n.z() * n.z()
                + 2 * xy * n.x() * n.y() + 2 * xz * n.x() * n.z() + 2 * yz * n.y() * n.z());
    }
    static InertiaTensor pointMass(double mass, PhysicsVector displacement) {
        double x = displacement.x(), y = displacement.y(), z = displacement.z();
        return new InertiaTensor(mass * (y * y + z * z), mass * (x * x + z * z), mass * (x * x + y * y),
                -mass * x * y, -mass * x * z, -mass * y * z);
    }
    private static boolean finite(double... values) { for (double value : values) if (!Double.isFinite(value)) return false; return true; }
}
