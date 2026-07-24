package com.mathmod.field;

public sealed interface FieldSampleValue permits FieldSampleValue.Scalar, FieldSampleValue.Vector {
    FieldValueKind kind();

    record Scalar(double value) implements FieldSampleValue {
        public Scalar {
            if (!Double.isFinite(value)) throw new IllegalArgumentException("Scalar field value must be finite");
        }
        @Override public FieldValueKind kind() { return FieldValueKind.SCALAR; }
    }

    record Vector(double x, double y, double z) implements FieldSampleValue {
        public Vector {
            if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
                throw new IllegalArgumentException("Vector field value must be finite");
            }
        }
        @Override public FieldValueKind kind() { return FieldValueKind.VECTOR; }
    }
}
