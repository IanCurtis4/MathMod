package com.mathmod.program;

/**
 * Pure, bounded mathematical primitives shared by rune execution and tests.
 */
public final class MathematicalOperations {
    private static final double EPSILON = 1.0E-8D;

    private MathematicalOperations() {
    }

    public static double sine(double radians) {
        return finite(Math.sin(finite(radians)));
    }

    public static double cosine(double radians) {
        return finite(Math.cos(finite(radians)));
    }

    public static double finiteDifference(double startValue, double endValue, double step) {
        finite(startValue);
        finite(endValue);
        finite(step);
        if (Math.abs(step) < EPSILON) {
            throw new IllegalArgumentException("Finite-difference step must be non-zero");
        }
        return finite((endValue - startValue) / step);
    }

    public static double simpsonIntegral(
            double startValue,
            double midpointValue,
            double endValue,
            double intervalWidth
    ) {
        finite(startValue);
        finite(midpointValue);
        finite(endValue);
        finite(intervalWidth);
        if (Math.abs(intervalWidth) < EPSILON) {
            throw new IllegalArgumentException("Integration interval must be non-zero");
        }
        return finite(intervalWidth * (startValue + 4.0D * midpointValue + endValue) / 6.0D);
    }

    public static Vector cross(Vector a, Vector b) {
        return new Vector(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }

    public static Vector project(Vector vector, Vector onto) {
        double denominator = onto.lengthSquared();
        if (denominator < EPSILON) {
            throw new IllegalArgumentException("Projection axis must be non-zero");
        }
        return onto.scale(vector.dot(onto) / denominator);
    }

    public static Vector reflect(Vector vector, Vector normal) {
        return vector.subtract(project(vector, normal).scale(2.0D));
    }

    public static Vector rotateY(Vector vector, CyclicGroupElement element) {
        double angle = element.angleRadians();
        double cosine = Math.cos(angle);
        double sine = Math.sin(angle);
        return new Vector(
                vector.x * cosine - vector.z * sine,
                vector.y,
                vector.x * sine + vector.z * cosine
        );
    }

    public static CyclicGroupElement cyclicElement(double order, double value) {
        return new CyclicGroupElement(exactInteger(order), exactInteger(value));
    }

    private static int exactInteger(double value) {
        finite(value);
        double rounded = Math.rint(value);
        if (Math.abs(value - rounded) > EPSILON || rounded < Integer.MIN_VALUE || rounded > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Cyclic group parameters must be integers");
        }
        return (int) rounded;
    }

    private static double finite(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Mathematical value must be finite");
        }
        return value;
    }

    public record Vector(double x, double y, double z) {
        public Vector {
            finite(x);
            finite(y);
            finite(z);
        }

        public double dot(Vector other) {
            return finite(x * other.x + y * other.y + z * other.z);
        }

        public double lengthSquared() {
            return dot(this);
        }

        public Vector scale(double factor) {
            finite(factor);
            return new Vector(x * factor, y * factor, z * factor);
        }

        public Vector subtract(Vector other) {
            return new Vector(x - other.x, y - other.y, z - other.z);
        }
    }
}
