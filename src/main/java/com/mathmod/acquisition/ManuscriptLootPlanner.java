package com.mathmod.acquisition;

import com.mathmod.util.NamespacedId;

import java.util.Objects;
import java.util.Optional;
import java.util.random.RandomGenerator;

/** Pure chance and pool selection for one eligible container. */
public final class ManuscriptLootPlanner {
    private ManuscriptLootPlanner() {
    }

    public static Optional<NamespacedId> selectManuscript(
            ManuscriptAcquisitionSnapshot snapshot,
            ManuscriptAcquisitionConfig config,
            NamespacedId pool,
            RandomGenerator random
    ) {
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(pool, "pool");
        Objects.requireNonNull(random, "random");
        if (!config.manuscriptLootEnabled()
                || config.villageLootChanceNumerator() == 0
                || random.nextInt(config.villageLootChanceDenominator()) >= config.villageLootChanceNumerator()) {
            return Optional.empty();
        }
        return WeightedManuscriptSelector.selectLoot(snapshot, pool, random)
                .map(ManuscriptAcquisitionSnapshot.Candidate::manuscriptId);
    }
}
