package com.mathmod.manuscript;

import java.util.Comparator;
import java.util.Objects;

public record ManuscriptDefinitionSource(
        ManuscriptSourceLayer layer,
        int priority,
        String sourceName
) implements Comparable<ManuscriptDefinitionSource> {
    private static final Comparator<ManuscriptDefinitionSource> PRECEDENCE = Comparator
            .comparingInt((ManuscriptDefinitionSource source) -> source.layer().precedence())
            .thenComparingInt(ManuscriptDefinitionSource::priority)
            .thenComparing(ManuscriptDefinitionSource::sourceName);

    public ManuscriptDefinitionSource {
        layer = Objects.requireNonNull(layer, "layer");
        if (priority < 0) {
            throw new IllegalArgumentException("priority must not be negative");
        }
        if (sourceName == null || sourceName.isBlank() || sourceName.trim().length() > 160) {
            throw new IllegalArgumentException("sourceName must contain 1 to 160 characters");
        }
        sourceName = sourceName.trim();
    }

    @Override
    public int compareTo(ManuscriptDefinitionSource other) {
        return PRECEDENCE.compare(this, other);
    }
}
