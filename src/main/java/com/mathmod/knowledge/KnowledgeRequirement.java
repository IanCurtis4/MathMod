package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.Objects;

public record KnowledgeRequirement(
        KnowledgeKind kind,
        NamespacedId id,
        String titleTranslationKey,
        String routeTranslationKey
) {
    public KnowledgeRequirement {
        kind = Objects.requireNonNull(kind, "kind");
        id = Objects.requireNonNull(id, "id");
        titleTranslationKey = requireText(titleTranslationKey, "titleTranslationKey");
        routeTranslationKey = requireText(routeTranslationKey, "routeTranslationKey");
    }

    public boolean isSatisfiedBy(PlayerKnowledge knowledge) {
        return knowledge.knows(kind, id);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
