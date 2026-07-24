package com.mathmod.acquisition;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MathemagicianOfferReconciliationPlanTest {
    private static final NamespacedId VALID_ID = NamespacedId.of("mathmod", "valid");
    private static final NamespacedId MISSING_ID = NamespacedId.of("mathmod", "removed_by_reload");

    @Test
    void preservesValidOfferRemovesUnknownMarkerAndFillsMissingLevel() {
        MathemagicianOfferReconciliationPlan plan = MathemagicianOfferReconciliationPlan.create(
                snapshot(),
                UUID.fromString("b6af2d3b-dddd-46cd-9a41-4cd4fdf775a0"),
                4L,
                3,
                Set.of(VALID_ID, MISSING_ID)
        );

        assertEquals(Set.of(VALID_ID), plan.retained());
        assertEquals(List.of(NamespacedId.of("mathmod", "advanced")),
                plan.additions().stream().map(ManuscriptAcquisitionSnapshot.Candidate::id).toList());
    }

    private static ManuscriptAcquisitionSnapshot snapshot() {
        ManuscriptAcquisitionSnapshot.Candidate valid = candidate(VALID_ID, "valid_manuscript", 2);
        ManuscriptAcquisitionSnapshot.Candidate advanced = candidate(
                NamespacedId.of("mathmod", "advanced"), "advanced_manuscript", 3
        );
        return new ManuscriptAcquisitionSnapshot(
                Map.of(valid.id(), valid, advanced.id(), advanced),
                Map.of(),
                Map.of(2, List.of(valid), 3, List.of(advanced))
        );
    }

    private static ManuscriptAcquisitionSnapshot.Candidate candidate(NamespacedId id, String manuscript, int level) {
        return new ManuscriptAcquisitionSnapshot.Candidate(
                id,
                NamespacedId.of("mathmod", manuscript),
                List.of(),
                0,
                Optional.of(new ManuscriptTradeDefinition(level, 8, true, 2, 10, 1))
        );
    }
}
