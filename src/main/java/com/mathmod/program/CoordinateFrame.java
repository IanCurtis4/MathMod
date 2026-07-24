package com.mathmod.program;

import java.util.Objects;

/** A right-handed orthonormal basis whose local axes are right, up, and forward. */
public record CoordinateFrame(Axis right, Axis up, Axis forward) {
    private static final double EPSILON = 1.0E-8D;

    public CoordinateFrame {
        Objects.requireNonNull(right, "right");
        Objects.requireNonNull(up, "up");
        Objects.requireNonNull(forward, "forward");
        if (right.lengthSquared() < EPSILON
                || up.lengthSquared() < EPSILON
                || forward.lengthSquared() < EPSILON) {
            throw new IllegalArgumentException("Coordinate frame axes must be non-zero");
        }
    }

    public static CoordinateFrame horizontal(double directionX, double directionZ) {
        if (!Double.isFinite(directionX) || !Double.isFinite(directionZ)) {
            throw new IllegalArgumentException("Horizontal direction must be finite");
        }
        double length = Math.sqrt(directionX * directionX + directionZ * directionZ);
        if (length < EPSILON) {
            throw new IllegalArgumentException("Horizontal direction must be non-zero");
        }
        Axis forward = new Axis(directionX / length, 0.0D, directionZ / length);
        Axis up = new Axis(0.0D, 1.0D, 0.0D);
        Axis right = new Axis(-forward.z(), 0.0D, forward.x());
        return new CoordinateFrame(right, up, forward);
    }

    public Axis toWorld(double localRight, double localUp, double localForward) {
        if (!Double.isFinite(localRight) || !Double.isFinite(localUp) || !Double.isFinite(localForward)) {
            throw new IllegalArgumentException("Local vector must be finite");
        }
        return new Axis(
                right.x() * localRight + up.x() * localUp + forward.x() * localForward,
                right.y() * localRight + up.y() * localUp + forward.y() * localForward,
                right.z() * localRight + up.z() * localUp + forward.z() * localForward
        );
    }

    public record Axis(double x, double y, double z) {
        public Axis {
            if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
                throw new IllegalArgumentException("Axis components must be finite");
            }
        }

        public double lengthSquared() {
            return x * x + y * y + z * z;
        }

        public double dot(Axis other) {
            Objects.requireNonNull(other, "other");
            return x * other.x + y * other.y + z * other.z;
        }
    }
}
