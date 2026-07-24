package com.mathmod.physics;

/** A context-free profile declaration staged by built-ins, KubeJS, or data. */
public record PhysicalProfileDeclaration(
        String id, PhysicalSelector selector, PhysicalProfileSource source, int priority,
        double density, Double structuralStrength, Double brittleness, Double elasticity,
        Double thermalResistance, Double magicalResistance, Double compressionMassExponent
) {
    public PhysicalProfileDeclaration {
        if (id == null || id.isBlank() || selector == null || source == null || priority < -1000 || priority > 1000
                || !inRange(density, 0.01D, 64.0D)
                || !optional(structuralStrength, 0, 64) || !optional(brittleness, 0, 1)
                || !optional(elasticity, 0, 1) || !optional(thermalResistance, 0, 64)
                || !optional(magicalResistance, 0, 64) || !optional(compressionMassExponent, 0, 3)) {
            throw new IllegalArgumentException("Invalid physical profile declaration");
        }
        id = id.trim();
    }
    private static boolean optional(Double value, double min, double max) { return value == null || inRange(value, min, max); }
    private static boolean inRange(double value, double min, double max) { return Double.isFinite(value) && value >= min && value <= max; }
}
