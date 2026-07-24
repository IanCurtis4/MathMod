package com.mathmod.runes;

public record AttributeRequirement(String attribute, int amount) {
    public AttributeRequirement {
        if (attribute == null || attribute.isBlank()) {
            throw new IllegalArgumentException("attribute must not be blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        attribute = attribute.trim();
    }
}
