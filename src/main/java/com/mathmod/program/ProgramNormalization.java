package com.mathmod.program;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public record ProgramNormalization(Map<String, NormalizedValue> valuesByNode) {
    public ProgramNormalization {
        valuesByNode = Map.copyOf(new LinkedHashMap<>(valuesByNode));
    }

    public int normalizedNodeCount() {
        return valuesByNode.size();
    }

    public Optional<NormalizedValue> value(String nodeId) {
        return Optional.ofNullable(valuesByNode.get(nodeId));
    }
}
