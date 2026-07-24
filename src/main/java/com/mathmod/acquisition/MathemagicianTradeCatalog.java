package com.mathmod.acquisition;

import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.SplittableRandom;
import java.util.UUID;

/** Deterministic manuscript offer selection for one villager and publication generation. */
public final class MathemagicianTradeCatalog {
    private static final int MAX_OFFERS_PER_LEVEL = 2;

    private MathemagicianTradeCatalog() {
    }

    public static List<ManuscriptAcquisitionSnapshot.Candidate> offersForLevel(
            ManuscriptAcquisitionSnapshot snapshot,
            UUID villagerId,
            long generation,
            int level
    ) {
        List<ManuscriptAcquisitionSnapshot.Candidate> remaining = new ArrayList<>(snapshot.tradeLevel(level));
        remaining.sort(Comparator.comparing(ManuscriptAcquisitionSnapshot.Candidate::id));
        SplittableRandom random = new SplittableRandom(seed(villagerId, generation, level));
        List<ManuscriptAcquisitionSnapshot.Candidate> selected = new ArrayList<>();
        while (!remaining.isEmpty() && selected.size() < MAX_OFFERS_PER_LEVEL) {
            selectWeighted(remaining, random).ifPresent(candidate -> {
                selected.add(candidate);
                remaining.remove(candidate);
            });
        }
        return List.copyOf(selected);
    }

    private static Optional<ManuscriptAcquisitionSnapshot.Candidate> selectWeighted(
            List<ManuscriptAcquisitionSnapshot.Candidate> candidates,
            SplittableRandom random
    ) {
        long total = candidates.stream()
                .map(ManuscriptAcquisitionSnapshot.Candidate::trade)
                .flatMap(Optional::stream)
                .mapToLong(ManuscriptTradeDefinition::weight)
                .sum();
        if (total <= 0) {
            return Optional.empty();
        }
        long roll = random.nextLong(total);
        for (ManuscriptAcquisitionSnapshot.Candidate candidate : candidates) {
            ManuscriptTradeDefinition trade = candidate.trade().orElseThrow();
            if (roll < trade.weight()) {
                return Optional.of(candidate);
            }
            roll -= trade.weight();
        }
        throw new IllegalStateException("Weighted Mathemagician offer selection did not resolve a candidate");
    }

    private static long seed(UUID villagerId, long generation, int level) {
        long mixed = villagerId.getMostSignificantBits() ^ Long.rotateLeft(villagerId.getLeastSignificantBits(), 19);
        mixed ^= Long.rotateLeft(generation, 37);
        return mixed ^ (0x9E3779B97F4A7C15L * level);
    }
}
