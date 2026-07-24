package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class KnowledgeDefinitionSnapshot {
    private final List<EpiphanyDefinition> epiphanies;
    private final List<DiscoveryDefinition> discoveries;
    private final Map<NamespacedId, EpiphanyDefinition> epiphaniesById;
    private final Map<NamespacedId, DiscoveryDefinition> discoveriesById;
    private final Map<NamespacedId, DiscoveryDefinition> discoveriesByManuscript;
    private final Map<KnowledgeKey, KnowledgeRequirement> requirementsByGrant;

    public KnowledgeDefinitionSnapshot(
            Iterable<EpiphanyDefinition> epiphanies,
            Iterable<DiscoveryDefinition> discoveries
    ) {
        this.epiphaniesById = indexEpiphanies(epiphanies);
        this.discoveriesById = indexDiscoveries(discoveries);
        this.discoveriesByManuscript = indexManuscripts(discoveriesById.values());
        this.epiphanies = sorted(epiphaniesById);
        this.discoveries = sorted(discoveriesById);
        this.requirementsByGrant = indexRequirements(this.epiphanies, this.discoveries);
    }

    public List<EpiphanyDefinition> epiphanies() {
        return epiphanies;
    }

    public List<DiscoveryDefinition> discoveries() {
        return discoveries;
    }

    public Optional<EpiphanyDefinition> epiphany(NamespacedId id) {
        return Optional.ofNullable(epiphaniesById.get(id));
    }

    public Optional<DiscoveryDefinition> discovery(NamespacedId id) {
        return Optional.ofNullable(discoveriesById.get(id));
    }

    public Optional<DiscoveryDefinition> discoveryForManuscript(NamespacedId manuscriptId) {
        return Optional.ofNullable(discoveriesByManuscript.get(manuscriptId));
    }

    public Optional<KnowledgeRequirement> requirementFor(KnowledgeKind kind, NamespacedId id) {
        return Optional.ofNullable(requirementsByGrant.get(new KnowledgeKey(kind, id)));
    }

    private static Map<NamespacedId, EpiphanyDefinition> indexEpiphanies(
            Iterable<EpiphanyDefinition> definitions
    ) {
        Map<NamespacedId, EpiphanyDefinition> indexed = new LinkedHashMap<>();
        definitions.forEach(definition -> indexed.put(definition.id(), definition));
        return Map.copyOf(indexed);
    }

    private static Map<NamespacedId, DiscoveryDefinition> indexDiscoveries(
            Iterable<DiscoveryDefinition> definitions
    ) {
        Map<NamespacedId, DiscoveryDefinition> indexed = new LinkedHashMap<>();
        definitions.forEach(definition -> indexed.put(definition.id(), definition));
        return Map.copyOf(indexed);
    }

    private static Map<NamespacedId, DiscoveryDefinition> indexManuscripts(
            Iterable<DiscoveryDefinition> definitions
    ) {
        Map<NamespacedId, DiscoveryDefinition> indexed = new LinkedHashMap<>();
        definitions.forEach(definition -> {
            DiscoveryDefinition previous = indexed.putIfAbsent(
                    definition.manuscriptId(),
                    definition
            );
            if (previous != null && !previous.id().equals(definition.id())) {
                throw new IllegalArgumentException(
                        "Manuscript " + definition.manuscriptId()
                                + " is claimed by both " + previous.id()
                                + " and " + definition.id()
                );
            }
        });
        return Map.copyOf(indexed);
    }

    private static Map<KnowledgeKey, KnowledgeRequirement> indexRequirements(
            List<EpiphanyDefinition> epiphanies,
            List<DiscoveryDefinition> discoveries
    ) {
        Map<KnowledgeKey, KnowledgeRequirement> indexed = new LinkedHashMap<>();
        epiphanies.forEach(epiphany -> epiphany.grants().forEach(grant ->
                indexed.putIfAbsent(
                        new KnowledgeKey(grant.kind(), grant.id()),
                        requirement(
                                KnowledgeKind.EPIPHANY,
                                epiphany.id(),
                                epiphany.titleTranslationKey()
                        )
                )));
        discoveries.forEach(discovery -> discovery.grants().forEach(grant ->
                indexed.putIfAbsent(
                        new KnowledgeKey(grant.kind(), grant.id()),
                        requirement(
                                KnowledgeKind.DISCOVERY,
                                discovery.id(),
                                discovery.titleTranslationKey()
                        )
                )));
        return Map.copyOf(indexed);
    }

    private static KnowledgeRequirement requirement(
            KnowledgeKind kind,
            NamespacedId id,
            String titleTranslationKey
    ) {
        return new KnowledgeRequirement(
                kind,
                id,
                titleTranslationKey,
                "knowledge." + id.namespace() + ".route." + id.path().replace('/', '.')
        );
    }

    private static <T> List<T> sorted(Map<NamespacedId, T> indexed) {
        return indexed.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(Map.Entry::getValue)
                .toList();
    }
}
