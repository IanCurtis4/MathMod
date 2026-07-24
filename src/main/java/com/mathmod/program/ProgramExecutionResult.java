package com.mathmod.program;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ProgramExecutionResult(
        boolean success,
        String messageKey,
        List<Object> messageArguments,
        Map<String, Integer> itemDeficits,
        Map<String, Integer> attributeDeficits
) {
    public ProgramExecutionResult {
        messageKey = messageKey == null ? "" : messageKey;
        messageArguments = messageArguments == null ? List.of() : List.copyOf(messageArguments);
        itemDeficits = itemDeficits == null || itemDeficits.isEmpty()
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(itemDeficits));
        attributeDeficits = attributeDeficits == null || attributeDeficits.isEmpty()
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(attributeDeficits));
    }

    public static ProgramExecutionResult success(String messageKey) {
        return new ProgramExecutionResult(true, messageKey, List.of(), Map.of(), Map.of());
    }

    public static ProgramExecutionResult failure(String messageKey, Object... messageArguments) {
        return new ProgramExecutionResult(false, messageKey, List.of(messageArguments), Map.of(), Map.of());
    }

    public static ProgramExecutionResult failure(ProgramCostResult costResult) {
        return new ProgramExecutionResult(
                false,
                costResult.messageKey(),
                costResult.messageArguments(),
                costResult.itemDeficits(),
                costResult.attributeDeficits()
        );
    }

}
