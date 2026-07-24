package com.mathmod.program;

import com.mathmod.runes.RuneTier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ProgramCostPlan(
        int budgetUsed,
        int baseBudgetLimit,
        int budgetBonus,
        int effectiveBudgetLimit,
        Map<String, Integer> fixedRequirements,
        Map<String, Integer> originalAttributeRequirements,
        Map<String, Integer> attributeRequirements,
        Map<String, Integer> providedAttributes,
        Map<String, Integer> missingAttributes,
        List<ProgramCostLine> lines,
        Map<String, Integer> missingItems,
        RuneTier requiredTier,
        RuneTier providedTier,
        CastModifiers modifiers,
        boolean badSelectors
) {
    public ProgramCostPlan {
        fixedRequirements = orderedCopy(fixedRequirements);
        originalAttributeRequirements = orderedCopy(originalAttributeRequirements);
        attributeRequirements = orderedCopy(attributeRequirements);
        providedAttributes = orderedCopy(providedAttributes);
        missingAttributes = orderedCopy(missingAttributes);
        lines = lines == null ? List.of() : List.copyOf(lines);
        missingItems = orderedCopy(missingItems);
        requiredTier = requiredTier == null ? RuneTier.FUNDAMENTAL : requiredTier;
        providedTier = providedTier == null ? RuneTier.FUNDAMENTAL : providedTier;
        modifiers = modifiers == null ? CastModifiers.none() : modifiers;
    }

    public boolean missingBudget() {
        return budgetUsed > effectiveBudgetLimit;
    }

    public int missingBudgetAmount() {
        return Math.max(0, budgetUsed - effectiveBudgetLimit);
    }

    public boolean missingTier() {
        return providedTier.level() < requiredTier.level();
    }

    public boolean success() {
        return !badSelectors
                && missingItems.isEmpty()
                && missingAttributes.isEmpty()
                && !missingTier()
                && !missingBudget();
    }

    public String messageKey() {
        if (badSelectors) {
            return "item.mathmod.programmed_talisman.execute_bad_item";
        }
        if (!missingItems.isEmpty()) {
            return "item.mathmod.programmed_talisman.execute_missing_items";
        }
        if (!missingAttributes.isEmpty()) {
            return "item.mathmod.programmed_talisman.execute_missing_attributes";
        }
        if (missingTier()) {
            return "item.mathmod.programmed_talisman.execute_missing_tier";
        }
        if (missingBudget()) {
            return "item.mathmod.programmed_talisman.execute_missing_budget";
        }
        return "";
    }

    private static <K, V> Map<K, V> orderedCopy(Map<K, V> source) {
        return source == null || source.isEmpty()
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }
}
