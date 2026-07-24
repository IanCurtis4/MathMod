package com.mathmod.physics;

public record BlockPhysicalProfile(
        double density, double occupiedVolume, double physicalMass, double structuralStrength,
        double brittleness, double elasticity, double thermalResistance, double magicalResistance,
        double compressionMassExponent, PhysicalProfileSource source
) {
    public BlockPhysicalProfile {
        if (!finite(density, occupiedVolume, physicalMass, structuralStrength, brittleness, elasticity,
                thermalResistance, magicalResistance, compressionMassExponent) || density < 0 || occupiedVolume < 0
                || occupiedVolume > 1 || physicalMass < 0 || physicalMass > 256 || structuralStrength < 0
                || brittleness < 0 || brittleness > 1 || elasticity < 0 || elasticity > 1
                || thermalResistance < 0 || magicalResistance < 0 || compressionMassExponent < 0
                || compressionMassExponent > 3 || source == null) throw new IllegalArgumentException("Invalid physical profile");
    }
    private static boolean finite(double... values) { for (double value : values) if (!Double.isFinite(value)) return false; return true; }
}
