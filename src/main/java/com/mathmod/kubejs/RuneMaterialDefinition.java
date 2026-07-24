package com.mathmod.kubejs;

import java.util.LinkedHashMap;
import java.util.Map;

public record RuneMaterialDefinition(
        String id,
        String itemOrTag,
        int budgetBonus,
        int tier,
        boolean consumed,
        Map<String, Integer> attributes,
        String displayTranslationKey
) {
    public RuneMaterialDefinition(String id, String itemOrTag, int budgetBonus, int tier) {
        this(id, itemOrTag, budgetBonus, tier, tier < 4, Map.of(), null);
    }

    public RuneMaterialDefinition(
            String id,
            String itemOrTag,
            int budgetBonus,
            int tier,
            boolean consumed,
            Map<String, Integer> attributes
    ) {
        this(id, itemOrTag, budgetBonus, tier, consumed, attributes, null);
    }

    public RuneMaterialDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (itemOrTag == null || itemOrTag.isBlank()) {
            throw new IllegalArgumentException("itemOrTag must not be blank");
        }
        if (budgetBonus < 0) {
            throw new IllegalArgumentException("budgetBonus must not be negative");
        }
        if (tier < 0) {
            throw new IllegalArgumentException("tier must not be negative");
        }
        id = id.trim();
        itemOrTag = itemOrTag.trim();
        attributes = sanitizeAttributes(attributes);
        displayTranslationKey = displayTranslationKey == null || displayTranslationKey.isBlank()
                ? null
                : displayTranslationKey.trim();
    }

    public RuneMaterialDefinition withAttribute(String attribute, int amount) {
        if (attribute == null || attribute.isBlank()) {
            throw new IllegalArgumentException("attribute must not be blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        Map<String, Integer> nextAttributes = new LinkedHashMap<>(attributes);
        nextAttributes.put(attribute.trim(), amount);
        return new RuneMaterialDefinition(
                id,
                itemOrTag,
                budgetBonus,
                tier,
                consumed,
                nextAttributes,
                displayTranslationKey
        );
    }

    public RuneMaterialDefinition withConsumed(boolean nextConsumed) {
        return new RuneMaterialDefinition(
                id,
                itemOrTag,
                budgetBonus,
                tier,
                nextConsumed,
                attributes,
                displayTranslationKey
        );
    }

    public RuneMaterialDefinition withTier(int nextTier) {
        return new RuneMaterialDefinition(
                id,
                itemOrTag,
                budgetBonus,
                nextTier,
                consumed,
                attributes,
                displayTranslationKey
        );
    }

    public RuneMaterialDefinition withDisplayTranslationKey(String translationKey) {
        if (translationKey == null || translationKey.isBlank()) {
            throw new IllegalArgumentException("translationKey must not be blank");
        }
        return new RuneMaterialDefinition(
                id,
                itemOrTag,
                budgetBonus,
                tier,
                consumed,
                attributes,
                translationKey
        );
    }

    public int attributeAmount(String attribute) {
        return attributes.getOrDefault(attribute, 0);
    }

    public String fallbackDisplayName() {
        int namespaceSeparator = id.lastIndexOf(':');
        String path = namespaceSeparator >= 0 && namespaceSeparator + 1 < id.length()
                ? id.substring(namespaceSeparator + 1)
                : id;
        StringBuilder label = new StringBuilder(path.length());
        boolean capitalize = true;
        for (int index = 0; index < path.length(); index++) {
            char character = path.charAt(index);
            if (character == '_' || character == '-' || character == '.') {
                if (!label.isEmpty() && label.charAt(label.length() - 1) != ' ') {
                    label.append(' ');
                }
                capitalize = true;
                continue;
            }
            label.append(capitalize ? Character.toUpperCase(character) : character);
            capitalize = false;
        }
        return label.toString().trim();
    }

    private static Map<String, Integer> sanitizeAttributes(Map<String, Integer> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return Map.of();
        }

        Map<String, Integer> sanitized = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : attributes.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null || entry.getValue() <= 0) {
                continue;
            }
            sanitized.put(entry.getKey().trim(), entry.getValue());
        }
        return Map.copyOf(sanitized);
    }
}
