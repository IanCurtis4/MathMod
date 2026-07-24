package com.mathmod.field;

import com.mathmod.environment.EnvironmentalFieldServices;
import com.mathmod.environment.EnvironmentalSamplingSession;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

/** Per-execution server context. It is deliberately absent from persisted field values. */
public record FieldSamplingContext(ServerLevel level, SamplePoint origin, EnvironmentalSamplingSession environmentalSession) {
    public FieldSamplingContext {
        level = Objects.requireNonNull(level, "level");
        origin = Objects.requireNonNull(origin, "origin");
        environmentalSession = Objects.requireNonNull(environmentalSession, "environmentalSession");
    }

    public FieldSamplingContext(ServerLevel level, SamplePoint origin) {
        this(level, origin, EnvironmentalFieldServices.capture(level));
    }
}
