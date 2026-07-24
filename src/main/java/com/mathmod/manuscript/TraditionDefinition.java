package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;

import java.util.Objects;

public record TraditionDefinition(
        int schemaVersion,
        NamespacedId id,
        String nameTranslationKey,
        String summaryTranslationKey,
        NamespacedId icon
) {
    public TraditionDefinition {
        ManuscriptSchema.requireSupported(schemaVersion);
        id = boundedId(id, "id");
        nameTranslationKey = translationKey(nameTranslationKey, "nameTranslationKey");
        summaryTranslationKey = translationKey(summaryTranslationKey, "summaryTranslationKey");
        icon = boundedId(icon, "icon");
    }

    private static NamespacedId boundedId(NamespacedId id, String field) {
        Objects.requireNonNull(id, field);
        if (id.toString().length() > 128) {
            throw new IllegalArgumentException(field + " must be at most 128 characters");
        }
        return id;
    }

    static String translationKey(String value, String field) {
        if (value == null || value.isBlank() || value.trim().length() > 160) {
            throw new IllegalArgumentException(field + " must contain 1 to 160 characters");
        }
        return value.trim();
    }
}
