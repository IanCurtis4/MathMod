package com.mathmod.environment;

import com.mathmod.util.NamespacedId;

import java.util.Objects;

/** One named coordinate in P13's attribute space. It is not a spatial axis. */
public record EnvironmentalChannel(
        NamespacedId id,
        double minimum,
        double maximum,
        double noiseAmplitude,
        int noiseScale,
        double reportScale
) {
    public EnvironmentalChannel {
        id = Objects.requireNonNull(id, "id");
        if (!Double.isFinite(minimum) || !Double.isFinite(maximum) || minimum >= maximum) {
            throw new IllegalArgumentException("Channel clamp must be finite and ordered");
        }
        if (!Double.isFinite(noiseAmplitude) || noiseAmplitude < -4.0D || noiseAmplitude > 4.0D) {
            throw new IllegalArgumentException("Noise amplitude must be finite and in [-4, 4]");
        }
        if (noiseScale != 16 && noiseScale != 32 && noiseScale != 64 && noiseScale != 128) {
            throw new IllegalArgumentException("Noise scale must be one of 16, 32, 64, or 128");
        }
        if (!Double.isFinite(reportScale) || reportScale < 0.01D || reportScale > 16.0D) {
            throw new IllegalArgumentException("Report scale must be finite and in [0.01, 16]");
        }
    }

    public double clamp(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Environmental channel value must be finite");
        }
        double clamped = Math.max(minimum, Math.min(maximum, value));
        return clamped == 0.0D ? 0.0D : clamped;
    }
}
