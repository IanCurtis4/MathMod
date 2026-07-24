package com.mathmod.acquisition;

import com.mathmod.util.NamespacedId;

import java.util.List;
import java.util.Map;

public final class ManuscriptAcquisitionSnapshot {
    private static final ManuscriptAcquisitionSnapshot EMPTY = new ManuscriptAcquisitionSnapshot(Map.of(), Map.of(), Map.of());

    private final Map<NamespacedId, Candidate> candidates;
    private final Map<NamespacedId, List<Candidate>> lootPools;
    private final Map<Integer, List<Candidate>> tradeLevels;

    ManuscriptAcquisitionSnapshot(
            Map<NamespacedId, Candidate> candidates,
            Map<NamespacedId, List<Candidate>> lootPools,
            Map<Integer, List<Candidate>> tradeLevels
    ) {
        this.candidates = Map.copyOf(candidates);
        this.lootPools = immutableLists(lootPools);
        this.tradeLevels = immutableLists(tradeLevels);
    }

    public static ManuscriptAcquisitionSnapshot empty() {
        return EMPTY;
    }

    public List<Candidate> candidates() {
        return candidates.values().stream().sorted(java.util.Comparator.comparing(Candidate::id)).toList();
    }

    public List<Candidate> lootPool(NamespacedId poolId) {
        return lootPools.getOrDefault(poolId, List.of());
    }

    public List<Candidate> tradeLevel(int level) {
        return tradeLevels.getOrDefault(level, List.of());
    }

    private static <K> Map<K, List<Candidate>> immutableLists(Map<K, List<Candidate>> values) {
        return values.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().sorted(java.util.Comparator.comparing(Candidate::id)).toList()
        ));
    }

    public record Candidate(
            NamespacedId id,
            NamespacedId manuscriptId,
            List<NamespacedId> lootPools,
            int lootWeight,
            java.util.Optional<ManuscriptTradeDefinition> trade
    ) {
        public Candidate {
            lootPools = List.copyOf(lootPools);
            trade = trade == null ? java.util.Optional.empty() : trade;
        }
    }
}
