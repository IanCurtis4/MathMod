package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KnowledgeProgressTest {
    @Test
    void harmonicEpiphanyRequiresTwoSuccessfulCastsWithEachTier() {
        NamespacedId feather = id("feather");
        NamespacedId quartz = id("quartz");
        EpiphanyDefinition epiphany = KnowledgeDefinitions.HARMONIC_MOTION_EPIPHANY;
        PlayerKnowledge knowledge = PlayerKnowledge.empty();

        knowledge = KnowledgeProgress.advance(knowledge, Set.of(feather)).knowledge();
        assertEquals(1, knowledge.progress(epiphany.progressKey(epiphany.studies().get(0))));
        assertTrue(knowledge.knows(KnowledgeKind.MATERIAL, feather));
        assertFalse(knowledge.knows(KnowledgeKind.EPIPHANY, epiphany.id()));

        knowledge = KnowledgeProgress.advance(knowledge, Set.of(feather)).knowledge();
        knowledge = KnowledgeProgress.advance(knowledge, Set.of(quartz)).knowledge();
        KnowledgeProgress.ProgressUpdate completed =
                KnowledgeProgress.advance(knowledge, Set.of(quartz));
        knowledge = completed.knowledge();

        assertEquals(List.of(epiphany), completed.completed());
        assertTrue(knowledge.knows(KnowledgeKind.CORRELATION, epiphany.correlationId()));
        assertTrue(knowledge.knows(KnowledgeKind.EPIPHANY, epiphany.id()));
        assertTrue(knowledge.knows(KnowledgeKind.RUNE, id("number_sin")));
        assertTrue(knowledge.knows(KnowledgeKind.RUNE, id("number_cos")));
        assertTrue(knowledge.knows(KnowledgeKind.THEOREM, id("harmonic_step")));
        assertEquals(0, knowledge.progress(epiphany.progressKey(epiphany.studies().get(0))));
        assertEquals(0, knowledge.progress(epiphany.progressKey(epiphany.studies().get(1))));
        assertEquals(
                2,
                knowledge.progress(KnowledgeDefinitions.CONSERVED_REMAINDER_EPIPHANY.progressKey(
                        KnowledgeDefinitions.CONSERVED_REMAINDER_EPIPHANY.studies().get(0)
                ))
        );

        assertSame(knowledge, KnowledgeProgress.advance(knowledge, Set.of(feather, quartz)).knowledge());
    }

    @Test
    void irrelevantMaterialsDoNotCreateProgress() {
        PlayerKnowledge knowledge = PlayerKnowledge.empty();

        KnowledgeProgress.ProgressUpdate update =
                KnowledgeProgress.advance(knowledge, Set.of(id("iron")));

        assertSame(knowledge, update.knowledge());
        assertTrue(update.notices().isEmpty());
        assertTrue(update.completed().isEmpty());
    }

    @Test
    void p6StudiesCompleteIndependentCrossTierCorrelations() {
        PlayerKnowledge knowledge = PlayerKnowledge.empty();

        for (int cast = 0; cast < 2; cast++) {
            knowledge = KnowledgeProgress.advance(
                    knowledge,
                    Set.of(id("vital_salt"), id("quartz"))
            ).knowledge();
            knowledge = KnowledgeProgress.advance(
                    knowledge,
                    Set.of(id("binding_resin"), id("axiomatic_ink"))
            ).knowledge();
        }

        assertTrue(knowledge.knows(
                KnowledgeKind.EPIPHANY,
                KnowledgeDefinitions.VITAL_CORRESPONDENCE
        ));
        assertTrue(knowledge.knows(KnowledgeKind.THEOREM, id("vital_infusion")));
        assertTrue(knowledge.knows(
                KnowledgeKind.EPIPHANY,
                KnowledgeDefinitions.CONSERVED_REMAINDER
        ));
        assertTrue(knowledge.knows(KnowledgeKind.THEOREM, id("conservation_lemma")));
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
