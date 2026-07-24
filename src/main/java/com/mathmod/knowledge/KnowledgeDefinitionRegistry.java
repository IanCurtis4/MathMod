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
        Map<NamespacedId, EpiphanyDefinition> candidate = new LinkedHashMap<>(kubeEpiphanies);
        candidate.put(definition.id(), definition);
        KnowledgeDefinitionSnapshot next = buildSnapshot(
                candidate,
                kubeDiscoveries,
                dataEpiphanies,
                dataDiscoveries
        );
        kubeEpiphanies.clear();
        kubeEpiphanies.putAll(candidate);
        snapshot = next;
    }

    synchronized void registerKube(DiscoveryDefinition definition) {
        Map<NamespacedId, DiscoveryDefinition> candidate = new LinkedHashMap<>(kubeDiscoveries);
        candidate.put(definition.id(), definition);
        KnowledgeDefinitionSnapshot next = buildSnapshot(
                kubeEpiphanies,
                candidate,
                dataEpiphanies,
                dataDiscoveries
        );
        kubeDiscoveries.clear();
        kubeDiscoveries.putAll(candidate);
        snapshot = next;
    }

    synchronized void publishData(
            Map<NamespacedId, EpiphanyDefinition> epiphanies,
            Map<NamespacedId, DiscoveryDefinition> discoveries
    ) {
        Map<NamespacedId, EpiphanyDefinition> boundedEpiphanies =
                bounded(epiphanies, MAX_EPIPHANIES, "epiphany");
        Map<NamespacedId, DiscoveryDefinition> boundedDiscoveries =
                bounded(discoveries, MAX_DISCOVERIES, "discovery");
        KnowledgeDefinitionSnapshot next = buildSnapshot(
                kubeEpiphanies,
                kubeDiscoveries,
                boundedEpiphanies,
                boundedDiscoveries
        );
        dataEpiphanies = boundedEpiphanies;
        dataDiscoveries = boundedDiscoveries;
        snapshot = next;
    }

    KnowledgeDefinitionSnapshot snapshot() {
        return snapshot;
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
