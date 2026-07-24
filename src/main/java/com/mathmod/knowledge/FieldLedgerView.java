package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.List;

public record FieldLedgerView(List<Entry> epiphanies, List<Entry> discoveries) {
    private static final int MAX_ENTRIES = 1024;
    private static final int MAX_STUDIES = 8;
    private static final int MAX_GRANTS = 16;

    public FieldLedgerView {
        epiphanies = boundedCopy(epiphanies, MAX_ENTRIES, "epiphanies");
        discoveries = boundedCopy(discoveries, MAX_ENTRIES, "discoveries");
    }

    public static FieldLedgerView from(
            PlayerKnowledge knowledge,
            KnowledgeDefinitionSnapshot definitions
    ) {
        List<Entry> epiphanies = definitions.epiphanies().stream()
                .map(definition -> new Entry(
                        KnowledgeKind.EPIPHANY,
                        definition.id(),
                        definition.titleTranslationKey(),
                        routeKey(definition.id()),
                        knowledge.knows(KnowledgeKind.EPIPHANY, definition.id()),
                        definition.studies().stream()
                                .map(study -> new Study(
                                        study.materialId(),
                                        Math.min(
                                                knowledge.progress(definition.progressKey(study)),
                                                study.successfulCasts()
                                        ),
                                        study.successfulCasts()
                                ))
                                .toList(),
                        definition.grants()
                ))
                .toList();
        List<Entry> discoveries = definitions.discoveries().stream()
                .map(definition -> new Entry(
                        KnowledgeKind.DISCOVERY,
                        definition.id(),
                        definition.titleTranslationKey(),
                        routeKey(definition.id()),
                        knowledge.knows(KnowledgeKind.DISCOVERY, definition.id()),
                        List.of(),
                        definition.grants()
                ))
                .toList();
        return new FieldLedgerView(epiphanies, discoveries);
    }

    public int completedCount() {
        return (int) java.util.stream.Stream.concat(
                        epiphanies.stream(),
                        discoveries.stream()
                )
                .filter(Entry::complete)
                .count();
    }

    public int totalCount() {
        return epiphanies.size() + discoveries.size();
    }

    private static String routeKey(NamespacedId id) {
        return "knowledge." + id.namespace() + ".route." + id.path().replace('/', '.');
    }

    private static <T> List<T> boundedCopy(List<T> values, int maximum, String label) {
        List<T> copy = List.copyOf(values);
        if (copy.size() > maximum) {
            throw new IllegalArgumentException("Too many " + label);
        }
        return copy;
    }

    static int maximumEntries() {
        return MAX_ENTRIES;
    }

    static int maximumStudies() {
        return MAX_STUDIES;
    }

    static int maximumGrants() {
        return MAX_GRANTS;
    }

    public record Entry(
            KnowledgeKind kind,
            NamespacedId id,
            String titleTranslationKey,
            String routeTranslationKey,
            boolean complete,
            List<Study> studies,
            List<KnowledgeGrant> grants
    ) {
        public Entry {
            if (kind != KnowledgeKind.EPIPHANY && kind != KnowledgeKind.DISCOVERY) {
                throw new IllegalArgumentException("A ledger entry must be an epiphany or discovery");
            }
            studies = boundedCopy(studies, MAX_STUDIES, "studies");
            grants = boundedCopy(grants, MAX_GRANTS, "grants");
        }
    }

    public record Study(NamespacedId materialId, int progress, int required) {
        public Study {
            if (progress < 0 || required < 1 || progress > required) {
                throw new IllegalArgumentException("Invalid study progress");
            }
        }
    }
}
