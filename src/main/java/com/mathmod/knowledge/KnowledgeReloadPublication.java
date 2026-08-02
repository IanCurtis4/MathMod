package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.Map;

/** Package-private single visibility point for paired knowledge reload state. */
final class KnowledgeReloadPublication {
    private static volatile Generation current = initialGeneration();

    private KnowledgeReloadPublication() {
    }

    static synchronized void publish(
            Map<NamespacedId, EpiphanyDefinition> epiphanies,
            Map<NamespacedId, DiscoveryDefinition> discoveries,
            Map<KnowledgeKey, KnowledgeKey> aliases
    ) {
        KnowledgeDefinitionRegistry.Prepared definitions = KnowledgeDefinitions.prepareData(epiphanies, discoveries);
        KnowledgeAliases.Prepared preparedAliases = KnowledgeAliases.prepareData(aliases);
        commit(definitions, preparedAliases);
    }

    static synchronized void publishDefinitions(
            Map<NamespacedId, EpiphanyDefinition> epiphanies,
            Map<NamespacedId, DiscoveryDefinition> discoveries
    ) {
        commit(KnowledgeDefinitions.prepareData(epiphanies, discoveries), KnowledgeAliases.prepareCurrent());
    }

    static synchronized void publishAliases(Map<KnowledgeKey, KnowledgeKey> aliases) {
        commit(KnowledgeDefinitions.prepareCurrent(), KnowledgeAliases.prepareData(aliases));
    }

    static synchronized void registerKube(EpiphanyDefinition definition) {
        commit(KnowledgeDefinitions.prepareKube(definition), KnowledgeAliases.prepareCurrent());
    }

    static synchronized void registerKube(DiscoveryDefinition definition) {
        commit(KnowledgeDefinitions.prepareKube(definition), KnowledgeAliases.prepareCurrent());
    }

    static synchronized void registerKube(KnowledgeKind kind, NamespacedId alias, NamespacedId target) {
        commit(KnowledgeDefinitions.prepareCurrent(), KnowledgeAliases.prepareKube(kind, alias, target));
    }

    static KnowledgeDefinitionSnapshot definitions() {
        return current.definitions();
    }

    static KnowledgeAliasRegistry aliases() {
        return current.aliases();
    }

    static Generation currentGeneration() {
        return current;
    }

    private static void commit(
            KnowledgeDefinitionRegistry.Prepared definitions,
            KnowledgeAliases.Prepared aliases
    ) {
        KnowledgeDefinitions.commit(definitions);
        KnowledgeAliases.commit(aliases);
        current = new Generation(definitions.snapshot(), aliases.registry());
    }

    private static Generation initialGeneration() {
        return new Generation(KnowledgeDefinitions.rawSnapshot(), KnowledgeAliases.rawCurrent());
    }

    record Generation(
            KnowledgeDefinitionSnapshot definitions,
            KnowledgeAliasRegistry aliases
    ) {
    }
}
