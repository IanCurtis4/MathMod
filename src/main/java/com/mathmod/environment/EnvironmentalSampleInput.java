package com.mathmod.environment;

import com.mathmod.field.SamplePoint;

import java.util.Objects;

/** Fully server-resolved static inputs for one environmental sample. */
public record EnvironmentalSampleInput(
        long worldSeed,
        String dimensionId,
        String biomeId,
        int minBuildHeight,
        int logicalHeight,
        SamplePoint point
) {
    public EnvironmentalSampleInput {
        dimensionId = requireId(dimensionId, "dimensionId");
        biomeId = requireId(biomeId, "biomeId");
        if (logicalHeight <= 0) {
            throw new IllegalArgumentException("logicalHeight must be positive");
        }
        point = Objects.requireNonNull(point, "point");
    }

    public double normalizedHeight() {
        double denominator = Math.max(1.0D, logicalHeight - 1.0D);
        return Math.max(0.0D, Math.min(1.0D, (point.y() - minBuildHeight) / denominator));
    }

    private static String requireId(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
