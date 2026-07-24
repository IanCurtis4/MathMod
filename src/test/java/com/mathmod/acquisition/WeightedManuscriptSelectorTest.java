package com.mathmod.acquisition;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeightedManuscriptSelectorTest {
    @Test
    void selectionIsStableForAnOrderedPoolAndSeededRandom() {
        NamespacedId pool = id("village");
        ManuscriptAcquisitionSnapshot snapshot = new ManuscriptAcquisitionSnapshot(
                Map.of(
                        id("a"), candidate("a", 1, pool),
                        id("b"), candidate("b", 3, pool)
                ),
                Map.of(pool, List.of(candidate("b", 3, pool), candidate("a", 1, pool))),
                Map.of()
        );

        List<NamespacedId> first = draws(snapshot, pool, new Random(42L));
        List<NamespacedId> second = draws(snapshot, pool, new Random(42L));

        assertEquals(first, second);
        assertTrue(first.contains(id("a")) || first.contains(id("b")));
    }

    @Test
    void emptyPoolFailsClosed() {
        assertTrue(WeightedManuscriptSelector.selectLoot(
                ManuscriptAcquisitionSnapshot.empty(), id("missing"), new Random(1L)
        ).isEmpty());
    }

    private static List<NamespacedId> draws(
            ManuscriptAcquisitionSnapshot snapshot,
            NamespacedId pool,
            Random random
    ) {
        return java.util.stream.IntStream.range(0, 8)
                .mapToObj(ignored -> WeightedManuscriptSelector.selectLoot(snapshot, pool, random).orElseThrow().id())
                .toList();
    }

    private static ManuscriptAcquisitionSnapshot.Candidate candidate(String id, int weight, NamespacedId pool) {
        return new ManuscriptAcquisitionSnapshot.Candidate(
                id(id), id("manuscript_" + id), List.of(pool), weight, Optional.empty()
        );
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
