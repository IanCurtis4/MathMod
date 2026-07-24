package com.mathmod.knowledge;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldLedgerViewTest {
    @Test
    void emptyLedgerExposesProgressAndRoutesWithoutGrantingKnowledge() {
        PlayerKnowledge knowledge = KnowledgeProgress.advance(
                PlayerKnowledge.empty(),
                Set.of(KnowledgeDefinitions.HARMONIC_MOTION_EPIPHANY
                        .studies()
                        .getFirst()
                        .materialId())
        ).knowledge();

        FieldLedgerView view = FieldLedgerView.from(
                knowledge,
                new KnowledgeDefinitionSnapshot(
                        java.util.List.of(KnowledgeDefinitions.HARMONIC_MOTION_EPIPHANY),
                        java.util.List.of(KnowledgeDefinitions.ROTATED_HORIZON_DISCOVERY)
                )
        );

        assertEquals(2, view.totalCount());
        assertEquals(0, view.completedCount());
        FieldLedgerView.Entry epiphany = view.epiphanies().getFirst();
        assertFalse(epiphany.complete());
        assertEquals(1, epiphany.studies().getFirst().progress());
        assertEquals(2, epiphany.studies().getFirst().required());
        assertEquals("knowledge.mathmod.route.harmonic_motion", epiphany.routeTranslationKey());
    }

    @Test
    void completedKnowledgeIsReflectedInBothLedgerFamilies() {
        PlayerKnowledge knowledge = KnowledgeDefinitions.grantLegacyAccess(PlayerKnowledge.empty());
        FieldLedgerView view = FieldLedgerView.from(
                knowledge,
                new KnowledgeDefinitionSnapshot(
                        java.util.List.of(KnowledgeDefinitions.HARMONIC_MOTION_EPIPHANY),
                        java.util.List.of(KnowledgeDefinitions.ROTATED_HORIZON_DISCOVERY)
                )
        );

        assertEquals(2, view.completedCount());
        assertTrue(view.epiphanies().getFirst().complete());
        assertTrue(view.discoveries().getFirst().complete());
    }
}
