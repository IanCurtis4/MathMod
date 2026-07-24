package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.List;
import java.util.Objects;

public record DiscoveryDefinition(
        NamespacedId id,
        NamespacedId manuscriptId,
        String titleTranslationKey,
        NamespacedId patchouliEntry,
        List<KnowledgeGrant> grants
) {
    public DiscoveryDefinition {
        id = Objects.requireNonNull(id, "id");
        manuscriptId = Objects.requireNonNull(manuscriptId, "manuscriptId");
        patchouliEntry = Objects.requireNonNull(patchouliEntry, "patchouliEntry");
        if (titleTranslationKey == null || titleTranslationKey.isBlank()) {
            throw new IllegalArgumentException("titleTranslationKey must not be blank");
        }
        titleTranslationKey = titleTranslationKey.trim();
        grants = List.copyOf(grants);
        if (grants.isEmpty() || grants.size() > 16) {
            throw new IllegalArgumentException("A discovery must have between 1 and 16 grants");
        }
    }
}
