package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;

import java.util.List;
import java.util.Optional;

/** Bounded display-only projection sent when a reader menu is opened. */
public record ManuscriptReaderView(
        NamespacedId requestedId,
        Optional<NamespacedId> canonicalId,
        Status status,
        String titleTranslationKey,
        String traditionNameTranslationKey,
        String traditionSummaryTranslationKey,
        ManuscriptRarity rarity,
        List<String> pageTranslationKeys,
        Optional<NamespacedId> patchouliEntry,
        Optional<NamespacedId> theoremId
) {
    public static final int MAX_TEXT = 160;

    public ManuscriptReaderView {
        requestedId = requireId(requestedId, "requestedId");
        canonicalId = canonicalId == null ? Optional.empty() : canonicalId.map(id -> requireId(id, "canonicalId"));
        status = status == null ? Status.MISSING : status;
        titleTranslationKey = bounded(titleTranslationKey, "titleTranslationKey");
        traditionNameTranslationKey = bounded(traditionNameTranslationKey, "traditionNameTranslationKey");
        traditionSummaryTranslationKey = bounded(traditionSummaryTranslationKey, "traditionSummaryTranslationKey");
        rarity = rarity == null ? ManuscriptRarity.COMMON : rarity;
        pageTranslationKeys = List.copyOf(pageTranslationKeys == null ? List.of() : pageTranslationKeys);
        if (pageTranslationKeys.size() > ManuscriptDefinition.MAX_PAGES) {
            throw new IllegalArgumentException("Reader view exceeds page limit");
        }
        pageTranslationKeys.forEach(key -> bounded(key, "pageTranslationKey"));
        patchouliEntry = patchouliEntry == null ? Optional.empty() : patchouliEntry.map(id -> requireId(id, "patchouliEntry"));
        theoremId = theoremId == null ? Optional.empty() : theoremId.map(id -> requireId(id, "theoremId"));
        if (status != Status.CURRENT && !pageTranslationKeys.isEmpty()) {
            throw new IllegalArgumentException("Only current records may expose pages");
        }
    }

    public static ManuscriptReaderView from(NamespacedId requestedId, ManuscriptSnapshot snapshot) {
        ManuscriptReferenceMigration migration = snapshot.migrateReference(requestedId);
        Optional<ManuscriptDefinition> definition = snapshot.manuscript(requestedId);
        if (definition.isEmpty()) {
            return new ManuscriptReaderView(requestedId, Optional.empty(), Status.MISSING,
                    "item.mathmod.field_manuscript.unknown", "", "", ManuscriptRarity.COMMON,
                    List.of(), Optional.empty(), Optional.empty());
        }
        ManuscriptDefinition value = definition.orElseThrow();
        TraditionDefinition tradition = snapshot.tradition(value.traditionId()).orElseThrow();
        return new ManuscriptReaderView(requestedId, migration.canonicalId(), Status.CURRENT,
                value.titleTranslationKey(), tradition.nameTranslationKey(), tradition.summaryTranslationKey(),
                value.rarity(), value.pageTranslationKeys(), value.patchouliEntry(), value.theoremId());
    }

    public boolean available() { return status == Status.CURRENT; }

    private static NamespacedId requireId(NamespacedId id, String label) {
        if (id == null) throw new IllegalArgumentException(label + " must not be null");
        return id;
    }

    private static String bounded(String value, String label) {
        if (value == null) return "";
        if (value.length() > MAX_TEXT) throw new IllegalArgumentException(label + " exceeds " + MAX_TEXT);
        return value;
    }

    public enum Status { CURRENT, MISSING }
}
