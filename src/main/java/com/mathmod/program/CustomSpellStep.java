package com.mathmod.program;

import java.util.Map;

public record CustomSpellStep(
        int id,
        CustomSpellAction action,
        Map<String, Double> arguments,
        String outputNodeId
) {
    public CustomSpellStep {
        arguments = arguments == null ? Map.of() : Map.copyOf(arguments);
    }

    public CustomSpellStep(int id, CustomSpellAction action, String outputNodeId) {
        this(id, action, Map.of(), outputNodeId);
    }

    public CustomSpellInvocation invocation() {
        return new CustomSpellInvocation(action, arguments);
    }
}
