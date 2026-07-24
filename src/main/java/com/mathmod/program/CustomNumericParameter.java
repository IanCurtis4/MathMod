package com.mathmod.program;

public record CustomNumericParameter(
        String key,
        String translationKey,
        double defaultValue,
        double minValue,
        double maxValue
) {
    public double clamp(double value) {
        if (!Double.isFinite(value)) {
            return defaultValue;
        }
        return Math.max(minValue, Math.min(maxValue, value));
    }
}
