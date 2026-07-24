package com.mathmod.acquisition;

import com.mathmod.util.NamespacedId;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record ManuscriptAcquisitionDefinition(
        int schemaVersion,
        NamespacedId id,
        NamespacedId manuscriptId,
        List<NamespacedId> lootPools,
        int lootWeight,
        Optional<ManuscriptTradeDefinition> trade
) {
    public static final int CURRENT_SCHEMA_VERSION = 1;
    public static final int MAX_LOOT_POOLS = 8;

    public ManuscriptAcquisitionDefinition {
        if (schemaVersion != CURRENT_SCHEMA_VERSION) {
            throw new IllegalArgumentException("Unsupported acquisition schema " + schemaVersion);
        }
        id = boundedId(id, "id");
        manuscriptId = boundedId(manuscriptId, "manuscriptId");
        lootPools = List.copyOf(Objects.requireNonNull(lootPools, "lootPools"));
        if (lootPools.size() > MAX_LOOT_POOLS) {
            throw new IllegalArgumentException("An acquisition definition supports at most " + MAX_LOOT_POOLS + " loot pools");
        }
        lootPools.forEach(pool -> boundedId(pool, "lootPool"));
        if (new LinkedHashSet<>(lootPools).size() != lootPools.size()) {
            throw new IllegalArgumentException("Loot pools must be unique");
        }
        if (lootWeight < 0 || lootWeight > 1_024) {
            throw new IllegalArgumentException("lootWeight must be between 0 and 1024");
        }
        if (!lootPools.isEmpty() && lootWeight == 0) {
            throw new IllegalArgumentException("lootWeight must be positive when loot pools are declared");
        }
        trade = Objects.requireNonNull(trade, "trade");
        if (lootPools.isEmpty() && trade.isEmpty()) {
            throw new IllegalArgumentException("An acquisition definition needs loot pools or a trade");
        }
    }

    private static NamespacedId boundedId(NamespacedId id, String field) {
        Objects.requireNonNull(id, field);
        if (id.toString().length() > 128) {
            throw new IllegalArgumentException(field + " must be at most 128 characters");
        }
        return id;
    }
}
