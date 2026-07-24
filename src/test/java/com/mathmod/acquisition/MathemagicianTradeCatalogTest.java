package com.mathmod.acquisition;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MathemagicianTradeCatalogTest {
    @Test
    void selectionIsStablePerVillagerGenerationAndLevel() {
        ManuscriptAcquisitionSnapshot snapshot = snapshotWithThreeApprenticeOffers();
        UUID villager = UUID.fromString("5a0b5a5f-64f1-4fd8-8b22-987987654321");

        List<ManuscriptAcquisitionSnapshot.Candidate> first = MathemagicianTradeCatalog.offersForLevel(
                snapshot, villager, 7L, 2
        );
        List<ManuscriptAcquisitionSnapshot.Candidate> repeated = MathemagicianTradeCatalog.offersForLevel(
                snapshot, villager, 7L, 2
        );

        assertEquals(first, repeated);
        assertTrue(first.size() <= 2);
        assertEquals(first.size(), first.stream().map(ManuscriptAcquisitionSnapshot.Candidate::id).distinct().count());
    }

    @Test
    void levelWithoutTradeDefinitionsProducesNoOffers() {
        assertTrue(MathemagicianTradeCatalog.offersForLevel(
                snapshotWithThreeApprenticeOffers(), UUID.randomUUID(), 1L, 5
        ).isEmpty());
    }

    private static ManuscriptAcquisitionSnapshot snapshotWithThreeApprenticeOffers() {
        List<ManuscriptAcquisitionSnapshot.Candidate> candidates = List.of(
                candidate("first", "first_manuscript", 4),
                candidate("second", "second_manuscript", 2),
                candidate("third", "third_manuscript", 1)
        );
        Map<NamespacedId, ManuscriptAcquisitionSnapshot.Candidate> byId = candidates.stream()
                .collect(java.util.stream.Collectors.toMap(
                        ManuscriptAcquisitionSnapshot.Candidate::id,
                        candidate -> candidate
                ));
        return new ManuscriptAcquisitionSnapshot(byId, Map.of(), Map.of(2, candidates));
    }

    private static ManuscriptAcquisitionSnapshot.Candidate candidate(String id, String manuscript, int weight) {
        NamespacedId candidateId = NamespacedId.of("mathmod", id);
        return new ManuscriptAcquisitionSnapshot.Candidate(
                candidateId,
                NamespacedId.of("mathmod", manuscript),
                List.of(),
                0,
                Optional.of(new ManuscriptTradeDefinition(2, 8, true, 2, 10, weight))
        );
    }
}
