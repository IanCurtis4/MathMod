package com.mathmod.runes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable, ordered capture of the active server rune definitions. */
public record RuneRegistrySnapshot(long generation, Map<String, RuneDefinition> definitions) {
    public RuneRegistrySnapshot {
        if (generation < 0) {
            throw new IllegalArgumentException("generation must not be negative");
        }
        LinkedHashMap<String, RuneDefinition> copy = new LinkedHashMap<>();
        definitions.forEach((id, definition) -> {
            if (id == null || definition == null || !id.equals(definition.id()) || copy.putIfAbsent(id, definition) != null) {
                throw new IllegalArgumentException("Invalid rune definition snapshot");
            }
        });
        definitions = Collections.unmodifiableMap(copy);
    }

    public RuneRegistry detachedRegistry() {
        RuneRegistry detached = new RuneRegistry();
        definitions.values().forEach(detached::register);
        return detached;
    }
}
