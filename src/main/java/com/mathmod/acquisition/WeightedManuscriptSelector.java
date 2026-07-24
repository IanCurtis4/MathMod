package com.mathmod.acquisition;

import com.mathmod.util.NamespacedId;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

public final class WeightedManuscriptSelector {
    private WeightedManuscriptSelector() {
    }

    public static Optional<ManuscriptAcquisitionSnapshot.Candidate> selectLoot(
            ManuscriptAcquisitionSnapshot snapshot,
            NamespacedId pool,
            RandomGenerator random
    ) {
        List<ManuscriptAcquisitionSnapshot.Candidate> candidates = snapshot.lootPool(pool).stream()
                .filter(candidate -> candidate.lootWeight() > 0)
                .sorted(Comparator.comparing(ManuscriptAcquisitionSnapshot.Candidate::id))
                .toList();
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        long totalWeight = candidates.stream().mapToLong(ManuscriptAcquisitionSnapshot.Candidate::lootWeight).sum();
        if (totalWeight <= 0) {
            return Optional.empty();
        }
        long selected = random.nextLong(totalWeight);
        long cursor = 0;
        for (ManuscriptAcquisitionSnapshot.Candidate candidate : candidates) {
            cursor += candidate.lootWeight();
            if (selected < cursor) {
                return Optional.of(candidate);
            }
        }
        throw new IllegalStateException("Weighted selection did not resolve a candidate");
    }
}
