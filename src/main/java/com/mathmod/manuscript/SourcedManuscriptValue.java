package com.mathmod.manuscript;

import java.util.Objects;

public record SourcedManuscriptValue<T>(T value, ManuscriptDefinitionSource source) {
    public SourcedManuscriptValue {
        value = Objects.requireNonNull(value, "value");
        source = Objects.requireNonNull(source, "source");
    }
}
