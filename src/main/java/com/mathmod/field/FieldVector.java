package com.mathmod.field;

/** Runtime-independent finite vector used by numerical field formulas. */
public record FieldVector(double x, double y, double z) {
    public FieldVector {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Field vector must be finite");
        }
    }
}
