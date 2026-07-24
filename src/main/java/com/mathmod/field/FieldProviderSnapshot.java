package com.mathmod.field;

import com.mathmod.util.NamespacedId;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FieldProviderSnapshot {
    private static final FieldProviderSnapshot EMPTY = new FieldProviderSnapshot(Map.of());
    private final Map<NamespacedId, FieldProviderDefinition> providers;
    private final List<FieldProviderDefinition> sorted;

    private FieldProviderSnapshot(Map<NamespacedId, FieldProviderDefinition> providers) {
        this.providers = Map.copyOf(providers);
        this.sorted = providers.values().stream()
                .sorted(Comparator.comparing(FieldProviderDefinition::id))
                .toList();
    }

    public static FieldProviderSnapshot empty() {
        return EMPTY;
    }

    public static FieldProviderSnapshot of(List<FieldProviderDefinition> definitions) {
        if (definitions.size() > 64) {
            throw new IllegalArgumentException("At most 64 field providers may be active");
        }
        Map<NamespacedId, FieldProviderDefinition> indexed = new LinkedHashMap<>();
        for (FieldProviderDefinition definition : definitions) {
            if (indexed.putIfAbsent(definition.id(), definition) != null) {
                throw new IllegalArgumentException("Duplicate field provider " + definition.id());
            }
        }
        return new FieldProviderSnapshot(indexed);
    }

    public Optional<FieldProviderDefinition> find(NamespacedId id) {
        return Optional.ofNullable(providers.get(id));
    }

    public List<FieldProviderDefinition> definitions() {
        return sorted;
    }
}
