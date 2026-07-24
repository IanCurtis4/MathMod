package com.mathmod.runes;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class RuneRegistry {
    private final Map<String, RuneDefinition> definitions = new LinkedHashMap<>();

    public synchronized RuneDefinition register(RuneDefinition definition) {
        if (definitions.containsKey(definition.id())) {
            throw new IllegalArgumentException("Rune '" + definition.id() + "' is already registered");
        }
        definitions.put(definition.id(), definition);
        return definition;
    }

    public synchronized RuneDefinition registerOrReplace(RuneDefinition definition) {
        definitions.put(definition.id(), definition);
        return definition;
    }

    public synchronized Optional<RuneDefinition> find(String id) {
        return Optional.ofNullable(definitions.get(id));
    }

    public synchronized void setEnabled(String id, boolean enabled) {
        RuneDefinition definition = definitions.get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Rune '" + id + "' is not registered");
        }
        definitions.put(id, definition.withEnabled(enabled));
    }

    public synchronized RuneDefinition update(String id, UnaryOperator<RuneDefinition> updater) {
        RuneDefinition definition = definitions.get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Rune '" + id + "' is not registered");
        }
        RuneDefinition updated = updater.apply(definition);
        if (!updated.id().equals(id)) {
            throw new IllegalArgumentException("Updated rune id must remain '" + id + "'");
        }
        definitions.put(id, updated);
        return updated;
    }

    public synchronized Collection<RuneDefinition> definitions() {
        return List.copyOf(definitions.values());
    }
}
