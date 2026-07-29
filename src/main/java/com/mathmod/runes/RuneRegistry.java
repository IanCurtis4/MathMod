package com.mathmod.runes;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class RuneRegistry {
    private final Map<String, RuneDefinition> definitions = new LinkedHashMap<>();
    private long generation;

    public synchronized RuneDefinition register(RuneDefinition definition) {
        if (definitions.containsKey(definition.id())) {
            throw new IllegalArgumentException("Rune '" + definition.id() + "' is already registered");
        }
        advanceGeneration();
        definitions.put(definition.id(), definition);
        return definition;
    }

    public synchronized RuneDefinition registerOrReplace(RuneDefinition definition) {
        if (definition.equals(definitions.get(definition.id()))) {
            return definition;
        }
        advanceGeneration();
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
        RuneDefinition updated = definition.withEnabled(enabled);
        if (!updated.equals(definition)) {
            advanceGeneration();
            definitions.put(id, updated);
        }
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
        if (!updated.equals(definition)) {
            advanceGeneration();
            definitions.put(id, updated);
        }
        return updated;
    }

    public synchronized Collection<RuneDefinition> definitions() {
        return List.copyOf(definitions.values());
    }

    public synchronized RuneRegistrySnapshot captureSnapshot() {
        return new RuneRegistrySnapshot(generation, definitions);
    }

    public synchronized long generation() {
        return generation;
    }

    /** Internal complete publication seam for a future authoritative loader. */
    synchronized void publishComplete(Map<String, RuneDefinition> candidate) {
        Map<String, RuneDefinition> validated = validateComplete(candidate);
        if (definitions.equals(validated)) {
            return;
        }
        advanceGeneration();
        definitions.clear();
        definitions.putAll(validated);
    }

    private static Map<String, RuneDefinition> validateComplete(Map<String, RuneDefinition> candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("candidate must not be null");
        }
        Map<String, RuneDefinition> copy = new LinkedHashMap<>();
        candidate.forEach((id, definition) -> {
            if (id == null || definition == null || !id.equals(definition.id()) || copy.putIfAbsent(id, definition) != null) {
                throw new IllegalArgumentException("candidate contains an invalid rune definition");
            }
        });
        return copy;
    }

    private void advanceGeneration() {
        if (generation == Long.MAX_VALUE) {
            throw new IllegalStateException("Rune registry generation exhausted");
        }
        generation++;
    }
}
