package com.mathmod.knowledge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record PlayerKnowledge(
        int schemaVersion,
        Set<NamespacedId> knownMaterials,
        Set<NamespacedId> observedCorrelations,
        Set<NamespacedId> completedEpiphanies,
        Set<NamespacedId> readDiscoveries,
        Set<NamespacedId> unlockedRunes,
        Set<NamespacedId> unlockedTheorems,
        Map<NamespacedId, Integer> studyProgress
) {
    public static final int CURRENT_SCHEMA_VERSION = 3;
    public static final int MAX_IDS_PER_KIND = 2_048;
    public static final int MAX_PROGRESS_ENTRIES = 4_096;
    public static final int MAX_PROGRESS_VALUE = 64;

    private static final Codec<Set<NamespacedId>> ID_SET_CODEC = NamespacedId.CODEC.listOf().flatXmap(
            PlayerKnowledge::decodeSet,
            PlayerKnowledge::encodeSet
    );
    private static final Codec<Map<NamespacedId, Integer>> PROGRESS_CODEC =
            Codec.unboundedMap(NamespacedId.CODEC, Codec.intRange(0, MAX_PROGRESS_VALUE)).flatXmap(
                    PlayerKnowledge::decodeProgress,
                    PlayerKnowledge::encodeProgress
            );

    public static final Codec<PlayerKnowledge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("schema_version", CURRENT_SCHEMA_VERSION).forGetter(PlayerKnowledge::schemaVersion),
            ID_SET_CODEC.optionalFieldOf("known_materials", Set.of()).forGetter(PlayerKnowledge::knownMaterials),
            ID_SET_CODEC.optionalFieldOf("observed_correlations", Set.of()).forGetter(PlayerKnowledge::observedCorrelations),
            ID_SET_CODEC.optionalFieldOf("completed_epiphanies", Set.of()).forGetter(PlayerKnowledge::completedEpiphanies),
            ID_SET_CODEC.optionalFieldOf("read_discoveries", Set.of()).forGetter(PlayerKnowledge::readDiscoveries),
            ID_SET_CODEC.optionalFieldOf("unlocked_runes", Set.of()).forGetter(PlayerKnowledge::unlockedRunes),
            ID_SET_CODEC.optionalFieldOf("unlocked_theorems", Set.of()).forGetter(PlayerKnowledge::unlockedTheorems),
            PROGRESS_CODEC.optionalFieldOf("study_progress", Map.of()).forGetter(PlayerKnowledge::studyProgress)
    ).apply(instance, PlayerKnowledge::new));

    public PlayerKnowledge {
        if (schemaVersion < 1) {
            throw new IllegalArgumentException("schemaVersion must be positive");
        }
        knownMaterials = immutableIds(knownMaterials);
        observedCorrelations = immutableIds(observedCorrelations);
        completedEpiphanies = immutableIds(completedEpiphanies);
        readDiscoveries = immutableIds(readDiscoveries);
        unlockedRunes = immutableIds(unlockedRunes);
        unlockedTheorems = immutableIds(unlockedTheorems);
        studyProgress = immutableProgress(studyProgress);
    }

    public static PlayerKnowledge empty() {
        return new PlayerKnowledge(
                CURRENT_SCHEMA_VERSION,
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Map.of()
        );
    }

    public Set<NamespacedId> entries(KnowledgeKind kind) {
        return switch (Objects.requireNonNull(kind, "kind")) {
            case MATERIAL -> knownMaterials;
            case CORRELATION -> observedCorrelations;
            case EPIPHANY -> completedEpiphanies;
            case DISCOVERY -> readDiscoveries;
            case RUNE -> unlockedRunes;
            case THEOREM -> unlockedTheorems;
        };
    }

    public boolean knows(KnowledgeKind kind, NamespacedId id) {
        return entries(kind).contains(id);
    }

    public PlayerKnowledge grant(KnowledgeKind kind, NamespacedId id) {
        Set<NamespacedId> changed = changedSet(entries(kind), id, true);
        return changed == entries(kind) ? this : replace(kind, changed);
    }

    public PlayerKnowledge revoke(KnowledgeKind kind, NamespacedId id) {
        Set<NamespacedId> changed = changedSet(entries(kind), id, false);
        return changed == entries(kind) ? this : replace(kind, changed);
    }

    public PlayerKnowledge clear() {
        return empty();
    }

    public int progress(NamespacedId key) {
        return studyProgress.getOrDefault(key, 0);
    }

    public PlayerKnowledge incrementProgress(NamespacedId key, int limit) {
        Objects.requireNonNull(key, "key");
        int boundedLimit = Math.max(1, Math.min(MAX_PROGRESS_VALUE, limit));
        int current = progress(key);
        if (current >= boundedLimit) {
            return this;
        }
        LinkedHashMap<NamespacedId, Integer> changed = new LinkedHashMap<>(studyProgress);
        changed.put(key, Math.min(boundedLimit, current + 1));
        return replaceProgress(changed);
    }

    public PlayerKnowledge clearProgress(Iterable<NamespacedId> keys) {
        LinkedHashMap<NamespacedId, Integer> changed = new LinkedHashMap<>(studyProgress);
        boolean removed = false;
        for (NamespacedId key : keys) {
            removed |= changed.remove(key) != null;
        }
        return removed ? replaceProgress(changed) : this;
    }

    public PlayerKnowledge withSchemaVersion(int version) {
        if (version == schemaVersion) {
            return this;
        }
        return new PlayerKnowledge(
                version,
                knownMaterials,
                observedCorrelations,
                completedEpiphanies,
                readDiscoveries,
                unlockedRunes,
                unlockedTheorems,
                studyProgress
        );
    }

    public PlayerKnowledge migrate(KnowledgeAliasRegistry aliases) {
        PlayerKnowledge migrated = new PlayerKnowledge(
                CURRENT_SCHEMA_VERSION,
                migrateSet(KnowledgeKind.MATERIAL, knownMaterials, aliases),
                migrateSet(KnowledgeKind.CORRELATION, observedCorrelations, aliases),
                migrateSet(KnowledgeKind.EPIPHANY, completedEpiphanies, aliases),
                migrateSet(KnowledgeKind.DISCOVERY, readDiscoveries, aliases),
                migrateSet(KnowledgeKind.RUNE, unlockedRunes, aliases),
                migrateSet(KnowledgeKind.THEOREM, unlockedTheorems, aliases),
                studyProgress
        );
        return equals(migrated) ? this : migrated;
    }

    public int totalEntries() {
        return knownMaterials.size()
                + observedCorrelations.size()
                + completedEpiphanies.size()
                + readDiscoveries.size()
                + unlockedRunes.size()
                + unlockedTheorems.size();
    }

    private PlayerKnowledge replace(KnowledgeKind kind, Set<NamespacedId> values) {
        return new PlayerKnowledge(
                schemaVersion,
                kind == KnowledgeKind.MATERIAL ? values : knownMaterials,
                kind == KnowledgeKind.CORRELATION ? values : observedCorrelations,
                kind == KnowledgeKind.EPIPHANY ? values : completedEpiphanies,
                kind == KnowledgeKind.DISCOVERY ? values : readDiscoveries,
                kind == KnowledgeKind.RUNE ? values : unlockedRunes,
                kind == KnowledgeKind.THEOREM ? values : unlockedTheorems,
                studyProgress
        );
    }

    private PlayerKnowledge replaceProgress(Map<NamespacedId, Integer> values) {
        return new PlayerKnowledge(
                schemaVersion,
                knownMaterials,
                observedCorrelations,
                completedEpiphanies,
                readDiscoveries,
                unlockedRunes,
                unlockedTheorems,
                values
        );
    }

    private static Set<NamespacedId> changedSet(
            Set<NamespacedId> original,
            NamespacedId id,
            boolean add
    ) {
        Objects.requireNonNull(id, "id");
        if (add == original.contains(id)) {
            return original;
        }
        LinkedHashSet<NamespacedId> changed = new LinkedHashSet<>(original);
        if (add) {
            if (changed.size() >= MAX_IDS_PER_KIND) {
                throw new IllegalStateException("Knowledge set reached its bounded capacity");
            }
            changed.add(id);
        } else {
            changed.remove(id);
        }
        return immutableIds(changed);
    }

    private static Set<NamespacedId> migrateSet(
            KnowledgeKind kind,
            Set<NamespacedId> values,
            KnowledgeAliasRegistry aliases
    ) {
        LinkedHashSet<NamespacedId> migrated = new LinkedHashSet<>();
        values.forEach(id -> migrated.add(aliases.resolve(kind, id)));
        return immutableIds(migrated);
    }

    private static DataResult<Set<NamespacedId>> decodeSet(List<NamespacedId> ids) {
        if (ids.size() > MAX_IDS_PER_KIND) {
            return DataResult.error(() -> "Knowledge set exceeds " + MAX_IDS_PER_KIND + " ids");
        }
        return DataResult.success(immutableIds(ids));
    }

    private static DataResult<List<NamespacedId>> encodeSet(Set<NamespacedId> ids) {
        if (ids.size() > MAX_IDS_PER_KIND) {
            return DataResult.error(() -> "Knowledge set exceeds " + MAX_IDS_PER_KIND + " ids");
        }
        return DataResult.success(new ArrayList<>(ids));
    }

    private static DataResult<Map<NamespacedId, Integer>> decodeProgress(Map<NamespacedId, Integer> progress) {
        if (progress.size() > MAX_PROGRESS_ENTRIES) {
            return DataResult.error(() -> "Study progress exceeds " + MAX_PROGRESS_ENTRIES + " entries");
        }
        return DataResult.success(immutableProgress(progress));
    }

    private static DataResult<Map<NamespacedId, Integer>> encodeProgress(Map<NamespacedId, Integer> progress) {
        if (progress.size() > MAX_PROGRESS_ENTRIES) {
            return DataResult.error(() -> "Study progress exceeds " + MAX_PROGRESS_ENTRIES + " entries");
        }
        return DataResult.success(progress);
    }

    private static Set<NamespacedId> immutableIds(Iterable<NamespacedId> values) {
        Objects.requireNonNull(values, "values");
        List<NamespacedId> sorted = new ArrayList<>();
        values.forEach(value -> sorted.add(Objects.requireNonNull(value, "knowledge id")));
        if (sorted.size() > MAX_IDS_PER_KIND) {
            throw new IllegalArgumentException("Knowledge set exceeds " + MAX_IDS_PER_KIND + " ids");
        }
        sorted.sort(NamespacedId::compareTo);
        return Collections.unmodifiableSet(new LinkedHashSet<>(sorted));
    }

    private static Map<NamespacedId, Integer> immutableProgress(Map<NamespacedId, Integer> values) {
        Objects.requireNonNull(values, "values");
        if (values.size() > MAX_PROGRESS_ENTRIES) {
            throw new IllegalArgumentException("Study progress exceeds " + MAX_PROGRESS_ENTRIES + " entries");
        }
        LinkedHashMap<NamespacedId, Integer> sorted = new LinkedHashMap<>();
        values.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    NamespacedId key = Objects.requireNonNull(entry.getKey(), "study progress id");
                    int value = Objects.requireNonNull(entry.getValue(), "study progress value");
                    if (value < 0 || value > MAX_PROGRESS_VALUE) {
                        throw new IllegalArgumentException("Study progress value is out of bounds");
                    }
                    if (value > 0) {
                        sorted.put(key, value);
                    }
                });
        return Collections.unmodifiableMap(sorted);
    }
}
