package com.mathmod.field;

/** Pure numerical formulas; query and cache ownership remain outside this class. */
public final class FieldCalculus {
    private FieldCalculus() { }

    public static FieldVector centeredGradient(
            double plusX, double minusX, double plusY, double minusY, double plusZ, double minusZ, double step
    ) {
        if (!Double.isFinite(step) || step <= 0.0D) throw new IllegalArgumentException("Gradient step must be positive and finite");
        return new FieldVector(
                finite((plusX - minusX) / (2.0D * step)),
                finite((plusY - minusY) / (2.0D * step)),
                finite((plusZ - minusZ) / (2.0D * step))
        );
    }

    private static double finite(double value) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Gradient result must be finite");
        return value;
    }
}
