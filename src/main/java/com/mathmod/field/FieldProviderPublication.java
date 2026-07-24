package com.mathmod.field;

import com.mathmod.util.NamespacedId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** One coherent server publication: descriptive snapshot plus private samplers. */
public record FieldProviderPublication(
        FieldProviderSnapshot definitions,
        FieldProviderRuntimeRegistry runtime
) {
    public FieldProviderPublication {
        definitions = Objects.requireNonNull(definitions, "definitions");
        runtime = Objects.requireNonNull(runtime, "runtime");
        Set<NamespacedId> definitionIds = new HashSet<>();
        definitions.definitions().forEach(definition -> definitionIds.add(definition.id()));
        if (!definitionIds.equals(runtime.ids())) {
            throw new IllegalArgumentException("Field definitions and server samplers must have identical ids");
        }
    }

    public static FieldProviderPublication empty() {
        return new FieldProviderPublication(
                FieldProviderSnapshot.empty(), new FieldProviderRuntimeRegistry(java.util.Map.of())
        );
    }
}
