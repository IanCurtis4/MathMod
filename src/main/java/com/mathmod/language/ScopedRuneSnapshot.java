package com.mathmod.language;

import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RuneRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable pure capture used by one compile attempt; generation belongs to a later server slice. */
public record ScopedRuneSnapshot(Map<String, RuneDefinition> definitions) {
    public ScopedRuneSnapshot {
        definitions = Map.copyOf(new LinkedHashMap<>(definitions));
    }

    public static ScopedRuneSnapshot capture(RuneRegistry registry) {
        Map<String, RuneDefinition> captured = new LinkedHashMap<>();
        registry.definitions().forEach(definition -> captured.put(definition.id(), definition));
        return new ScopedRuneSnapshot(captured);
    }

    public RuneRegistry registry() {
        RuneRegistry copy = new RuneRegistry();
        definitions.values().forEach(copy::register);
        return copy;
    }
}
