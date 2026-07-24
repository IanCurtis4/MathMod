package com.mathmod.field;

public enum CalculusOperator {
    DERIVATIVE(FieldValueKind.SCALAR, 2, false),
    GRADIENT(FieldValueKind.SCALAR, 6, true),
    DIVERGENCE(FieldValueKind.VECTOR, 6, true),
    CURL(FieldValueKind.VECTOR, 6, true),
    INTEGRATE(FieldValueKind.SCALAR, 33, false);

    private final FieldValueKind inputKind;
    private final int maximumSamples;
    private final boolean worldField;

    CalculusOperator(FieldValueKind inputKind, int maximumSamples, boolean worldField) {
        this.inputKind = inputKind;
        this.maximumSamples = maximumSamples;
        this.worldField = worldField;
    }

    public FieldValueKind inputKind() { return inputKind; }
    public int maximumSamples() { return maximumSamples; }
    public boolean worldField() { return worldField; }
}
