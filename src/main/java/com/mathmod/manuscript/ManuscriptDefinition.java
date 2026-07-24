package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record ManuscriptDefinition(
        int schemaVersion,
        NamespacedId id,
        NamespacedId traditionId,
        String titleTranslationKey,
        List<String> pageTranslationKeys,
        NamespacedId icon,
        ManuscriptRarity rarity,
        Optional<NamespacedId> patchouliEntry,
        Optional<NamespacedId> theoremId
) {
    public static final int MAX_PAGES = 8;
    public static final int MAX_TOTAL_PAGE_KEY_CHARACTERS = 1_280;

    public ManuscriptDefinition {
        ManuscriptSchema.requireSupported(schemaVersion);
        id = boundedId(id, "id");
        traditionId = boundedId(traditionId, "traditionId");
        titleTranslationKey = TraditionDefinition.translationKey(
                titleTranslationKey,
                "titleTranslationKey"
        );
        pageTranslationKeys = List.copyOf(Objects.requireNonNull(
                pageTranslationKeys,
                "pageTranslationKeys"
        ));
        if (pageTranslationKeys.isEmpty() || pageTranslationKeys.size() > MAX_PAGES) {
            throw new IllegalArgumentException("A manuscript must contain between 1 and 8 pages");
        }
        pageTranslationKeys = pageTranslationKeys.stream()
                .map(key -> TraditionDefinition.translationKey(key, "pageTranslationKey"))
                .toList();
        int totalCharacters = pageTranslationKeys.stream().mapToInt(String::length).sum();
        if (totalCharacters > MAX_TOTAL_PAGE_KEY_CHARACTERS) {
            throw new IllegalArgumentException("Manuscript page keys exceed the synchronized limit");
        }
        icon = boundedId(icon, "icon");
        rarity = Objects.requireNonNull(rarity, "rarity");
        patchouliEntry = boundedOptional(patchouliEntry, "patchouliEntry");
        theoremId = boundedOptional(theoremId, "theoremId");
    }

    private static Optional<NamespacedId> boundedOptional(
            Optional<NamespacedId> value,
            String field
    ) {
        return Objects.requireNonNull(value, field).map(id -> boundedId(id, field));
    }

    private static NamespacedId boundedId(NamespacedId id, String field) {
        Objects.requireNonNull(id, field);
        if (id.toString().length() > 128) {
            throw new IllegalArgumentException(field + " must be at most 128 characters");
        }
        return id;
    }
}
