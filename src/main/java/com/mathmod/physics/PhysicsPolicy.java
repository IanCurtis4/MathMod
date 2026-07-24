package com.mathmod.physics;

/** Validated scalar policy used by one immutable physical-profile snapshot. */
public record PhysicsPolicy(
        int shapeResolution, double defaultDensity, double hardnessWeight, double blastResistanceWeight,
        double fallbackBaseMass, double fallbackHardnessWeight, double fallbackBlastWeight,
        double defaultCompressionMassExponent, double defaultStructuralStrength, double defaultBrittleness,
        double defaultElasticity, double defaultThermalResistance, double defaultMagicalResistance
) {
    public PhysicsPolicy {
        if (shapeResolution < 4 || shapeResolution > 32 || (shapeResolution & (shapeResolution - 1)) != 0
                || !range(defaultDensity, .01D, 64) || !range(hardnessWeight, 0, 1)
                || !range(blastResistanceWeight, 0, .25D) || !range(fallbackBaseMass, 0, 64)
                || !range(fallbackHardnessWeight, 0, 1) || !range(fallbackBlastWeight, 0, .25D)
                || !range(defaultCompressionMassExponent, 0, 3) || !range(defaultStructuralStrength, 0, 64)
                || !range(defaultBrittleness, 0, 1) || !range(defaultElasticity, 0, 1)
                || !range(defaultThermalResistance, 0, 64) || !range(defaultMagicalResistance, 0, 64)) {
            throw new IllegalArgumentException("Invalid physics policy");
        }
    }
    public static PhysicsPolicy defaults() {
        return new PhysicsPolicy(16, 1, .15D, .05D, .2D, .5D, .15D, 0,
                1, .5D, 0, 1, 0);
    }
    private static boolean range(double value, double min, double max) { return Double.isFinite(value) && value >= min && value <= max; }
}
