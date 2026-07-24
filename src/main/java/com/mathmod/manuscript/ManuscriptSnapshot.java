package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ManuscriptSnapshot {
    private static final ManuscriptSnapshot EMPTY = new ManuscriptSnapshot(
            Map.of(),
            Map.of(),
            Map.of()
    );

    private final Map<NamespacedId, SourcedManuscriptValue<TraditionDefinition>> traditions;
    private final Map<NamespacedId, SourcedManuscriptValue<ManuscriptDefinition>> manuscripts;
    private final Map<NamespacedId, NamespacedId> aliases;
    private final List<TraditionDefinition> sortedTraditions;
    private final List<ManuscriptDefinition> sortedManuscripts;

    ManuscriptSnapshot(
            Map<NamespacedId, SourcedManuscriptValue<TraditionDefinition>> traditions,
            Map<NamespacedId, SourcedManuscriptValue<ManuscriptDefinition>> manuscripts,
            Map<NamespacedId, NamespacedId> aliases
    ) {
        this.traditions = Map.copyOf(traditions);
        this.manuscripts = Map.copyOf(manuscripts);
        this.aliases = Map.copyOf(aliases);
        sortedTraditions = sortedValues(this.traditions);
        sortedManuscripts = sortedValues(this.manuscripts);
    }

    public static ManuscriptSnapshot empty() {
        return EMPTY;
    }

    public List<TraditionDefinition> traditions() {
        return sortedTraditions;
    }

    public List<ManuscriptDefinition> manuscripts() {
        return sortedManuscripts;
    }

    public Optional<TraditionDefinition> tradition(NamespacedId id) {
        return Optional.ofNullable(traditions.get(id)).map(SourcedManuscriptValue::value);
    }

    public Optional<ManuscriptDefinition> manuscript(NamespacedId id) {
        return migrateReference(id).canonicalId()
                .map(manuscripts::get)
                .map(SourcedManuscriptValue::value);
    }

    public Optional<ManuscriptDefinitionSource> traditionSource(NamespacedId id) {
        return Optional.ofNullable(traditions.get(id)).map(SourcedManuscriptValue::source);
    }

    public Optional<ManuscriptDefinitionSource> manuscriptSource(NamespacedId id) {
        return migrateReference(id).canonicalId()
                .map(manuscripts::get)
                .map(SourcedManuscriptValue::source);
    }

    public ManuscriptReferenceMigration migrateReference(NamespacedId id) {
        if (manuscripts.containsKey(id)) {
            return new ManuscriptReferenceMigration(
                    id,
                    Optional.of(id),
                    ManuscriptReferenceMigration.Status.CURRENT
            );
        }
        NamespacedId canonical = aliases.get(id);
        if (canonical != null && manuscripts.containsKey(canonical)) {
            return new ManuscriptReferenceMigration(
                    id,
                    Optional.of(canonical),
                    ManuscriptReferenceMigration.Status.ALIASED
            );
        }
        return new ManuscriptReferenceMigration(
                id,
                Optional.empty(),
                ManuscriptReferenceMigration.Status.MISSING
        );
    }

    public int aliasCount() {
        return aliases.size();
    }

    private static <T> List<T> sortedValues(
            Map<NamespacedId, SourcedManuscriptValue<T>> values
    ) {
        return values.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(entry -> entry.getValue().value())
                .toList();
    }
}
