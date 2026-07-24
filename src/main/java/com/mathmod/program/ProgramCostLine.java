package com.mathmod.program;

import java.util.Map;

public record ProgramCostLine(
        String id,
        String selector,
        int quantity,
        boolean consumed,
        int budgetBonus,
        int tier,
        Map<String, Integer> attributes,
        String reason
) {
    public ProgramCostLine {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (selector == null || selector.isBlank()) {
            throw new IllegalArgumentException("selector must not be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (budgetBonus < 0) {
            throw new IllegalArgumentException("budgetBonus must not be negative");
        }
        if (tier < 0 || tier > 4) {
            throw new IllegalArgumentException("tier must be between 0 and 4");
        }
        id = id.trim();
        selector = selector.trim();
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        reason = reason == null ? "" : reason.trim();
    }
}
