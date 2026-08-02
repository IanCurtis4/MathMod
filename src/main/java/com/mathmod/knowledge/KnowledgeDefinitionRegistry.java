package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class KnowledgeDefinitionRegistry {
    private static final int MAX_EPIPHANIES = 256;
    private static final int MAX_DISCOVERIES = 1024;

    private final Map<NamespacedId, EpiphanyDefinition> builtInEpiphanies;
    private final Map<NamespacedId, DiscoveryDefinition> builtInDiscoveries;
    private final Map<NamespacedId, EpiphanyDefinition> kubeEpiphanies = new LinkedHashMap<>();
    private final Map<NamespacedId, DiscoveryDefinition> kubeDiscoveries = new LinkedHashMap<>();
    private Map<NamespacedId, EpiphanyDefinition> dataEpiphanies = Map.of();
    private Map<NamespacedId, DiscoveryDefinition> dataDiscoveries = Map.of();
    private volatile KnowledgeDefinitionSnapshot snapshot;

    KnowledgeDefinitionRegistry(
            List<EpiphanyDefinition> builtInEpiphanies,
            List<DiscoveryDefinition> builtInDiscoveries
    ) {
        this.builtInEpiphanies = index(builtInEpiphanies);
        this.builtInDiscoveries = index(builtInDiscoveries);
        rebuild();
    }

    synchronized void registerKube(EpiphanyDefinition definition) {
        commit(prepareKube(definition));
    }

    synchronized void registerKube(DiscoveryDefinition definition) {
        commit(prepareKube(definition));
    }

    synchronized void publishData(
            Map<NamespacedId, EpiphanyDefinition> epiphanies,
            Map<NamespacedId, DiscoveryDefinition> discoveries
    ) {
        commit(prepareData(epiphanies, discoveries));
    }

    KnowledgeDefinitionSnapshot snapshot() {
        return snapshot;
    }

    synchronized Prepared prepareData(
            Map<NamespacedId, EpiphanyDefinition> epiphanies,
            Map<NamespacedId, DiscoveryDefinition> discoveries
    ) {
        return prepare(kubeEpiphanies, kubeDiscoveries,
                bounded(epiphanies, MAX_EPIPHANIES, "epiphany"),
                bounded(discoveries, MAX_DISCOVERIES, "discovery"));
    }

    synchronized Prepared prepareCurrent() {
        return prepare(kubeEpiphanies, kubeDiscoveries, dataEpiphanies, dataDiscoveries);
    }

    synchronized Prepared prepareKube(EpiphanyDefinition definition) {
        Map<NamespacedId, EpiphanyDefinition> candidate = new LinkedHashMap<>(kubeEpiphanies);
        candidate.put(definition.id(), definition);
        return prepare(candidate, kubeDiscoveries, dataEpiphanies, dataDiscoveries);
    }

    synchronized Prepared prepareKube(DiscoveryDefinition definition) {
        Map<NamespacedId, DiscoveryDefinition> candidate = new LinkedHashMap<>(kubeDiscoveries);
        candidate.put(definition.id(), definition);
        return prepare(kubeEpiphanies, candidate, dataEpiphanies, dataDiscoveries);
    }

    synchronized void commit(Prepared prepared) {
        kubeEpiphanies.clear();
        kubeEpiphanies.putAll(prepared.kubeEpiphanies());
        kubeDiscoveries.clear();
        kubeDiscoveries.putAll(prepared.kubeDiscoveries());
        dataEpiphanies = prepared.dataEpiphanies();
        dataDiscoveries = prepared.dataDiscoveries();
        snapshot = prepared.snapshot();
    }

    private Prepared prepare(
            Map<NamespacedId, EpiphanyDefinition> kubeEpiphanies,
            Map<NamespacedId, DiscoveryDefinition> kubeDiscoveries,
            Map<NamespacedId, EpiphanyDefinition> dataEpiphanies,
            Map<NamespacedId, DiscoveryDefinition> dataDiscoveries
    ) {
        return new Prepared(kubeEpiphanies, kubeDiscoveries, dataEpiphanies, dataDiscoveries,
                buildSnapshot(kubeEpiphanies, kubeDiscoveries, dataEpiphanies, dataDiscoveries));
    }

    record Prepared(
            Map<NamespacedId, EpiphanyDefinition> kubeEpiphanies,
            Map<NamespacedId, DiscoveryDefinition> kubeDiscoveries,
            Map<NamespacedId, EpiphanyDefinition> dataEpiphanies,
            Map<NamespacedId, DiscoveryDefinition> dataDiscoveries,
            KnowledgeDefinitionSnapshot snapshot
    ) {
        Prepared {
            kubeEpiphanies = Map.copyOf(kubeEpiphanies);
            kubeDiscoveries = Map.copyOf(kubeDiscoveries);
            dataEpiphanies = Map.copyOf(dataEpiphanies);
            dataDiscoveries = Map.copyOf(dataDiscoveries);
        }
    }

    private void rebuild() {
        snapshot = buildSnapshot(
                kubeEpiphanies,
                kubeDiscoveries,
                dataEpiphanies,
                dataDiscoveries
        );
    }

    private KnowledgeDefinitionSnapshot buildSnapshot(
            Map<NamespacedId, EpiphanyDefinition> kubeEpiphanies,
            Map<NamespacedId, DiscoveryDefinition> kubeDiscoveries,
            Map<NamespacedId, EpiphanyDefinition> dataEpiphanies,
            Map<NamespacedId, DiscoveryDefinition> dataDiscoveries
    ) {
        Map<NamespacedId, EpiphanyDefinition> epiphanies = new LinkedHashMap<>(builtInEpiphanies);
        epiphanies.putAll(kubeEpiphanies);
        epiphanies.putAll(dataEpiphanies);
        Map<NamespacedId, DiscoveryDefinition> discoveries =
                new LinkedHashMap<>(builtInDiscoveries);
        discoveries.putAll(kubeDiscoveries);
        discoveries.putAll(dataDiscoveries);
        return new KnowledgeDefinitionSnapshot(epiphanies.values(), discoveries.values());
    }

    private static <T> Map<NamespacedId, T> index(List<T> values) {
        Map<NamespacedId, T> indexed = new LinkedHashMap<>();
        for (T value : values) {
            NamespacedId id = value instanceof EpiphanyDefinition epiphany
                    ? epiphany.id()
                    : ((DiscoveryDefinition) value).id();
            if (indexed.putIfAbsent(id, value) != null) {
                throw new IllegalArgumentException("Duplicate knowledge definition " + id);
            }
        }
        return Map.copyOf(indexed);
    }

    private static <T> Map<NamespacedId, T> bounded(
            Map<NamespacedId, T> values,
            int maximum,
            String label
    ) {
        if (values.size() > maximum) {
            throw new IllegalArgumentException(
                    "Too many " + label + " definitions: " + values.size() + " > " + maximum
            );
        }
        return Map.copyOf(values);
    }
}
