package com.mathmod.program;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ProgramCostResult(
        boolean success,
        String messageKey,
        List<Object> messageArguments,
        Map<String, Integer> itemDeficits,
        Map<String, Integer> attributeDeficits
) {
    public ProgramCostResult {
        messageKey = messageKey == null ? "" : messageKey;
        messageArguments = messageArguments == null ? List.of() : List.copyOf(messageArguments);
        itemDeficits = itemDeficits == null || itemDeficits.isEmpty()
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(itemDeficits));
        attributeDeficits = attributeDeficits == null || attributeDeficits.isEmpty()
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(attributeDeficits));
    }

    public static ProgramCostResult ok() {
        return new ProgramCostResult(true, "", List.of(), Map.of(), Map.of());
    }

    public static ProgramCostResult failure(ProgramCostPlan plan) {
        if (plan.badSelectors()) {
            return failure("item.mathmod.programmed_talisman.execute_bad_item");
        }
        if (!plan.missingItems().isEmpty()) {
            return new ProgramCostResult(
                    false,
                    "item.mathmod.programmed_talisman.execute_missing_items",
                    List.of(),
                    plan.missingItems(),
                    Map.of()
            );
        }
        if (!plan.missingAttributes().isEmpty()) {
            return new ProgramCostResult(
                    false,
                    "item.mathmod.programmed_talisman.execute_missing_attributes",
                    List.of(),
                    Map.of(),
                    plan.missingAttributes()
            );
        }
        if (plan.missingTier()) {
            return failure(
                    "item.mathmod.programmed_talisman.execute_missing_tier",
                    plan.requiredTier().level(),
                    plan.providedTier().level()
            );
        }
        if (plan.missingBudget()) {
            return failure(
                    "item.mathmod.programmed_talisman.execute_missing_budget",
                    plan.missingBudgetAmount()
            );
        }
        return ok();
    }

    public static ProgramCostResult failure(String messageKey, Object... messageArguments) {
        return new ProgramCostResult(false, messageKey, List.of(messageArguments), Map.of(), Map.of());
    }
}
