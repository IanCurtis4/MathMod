package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KnowledgeDiscoveryTest {
    @Test
    void firstReadGrantsConstructionKnowledgeAndDuplicateIsIdempotent() {
        PlayerKnowledge original = PlayerKnowledge.empty();

        KnowledgeDiscovery.Evaluation first =
                KnowledgeDiscovery.evaluate(original, KnowledgeDefinitions.ROTATED_HORIZON);

        assertEquals(KnowledgeDiscovery.ReadResult.FIRST_READ, first.result());
        assertTrue(first.knowledge().knows(
                KnowledgeKind.DISCOVERY,
                KnowledgeDefinitions.ROTATED_HORIZON
        ));
        assertTrue(first.knowledge().knows(KnowledgeKind.RUNE, id("cyclic_element")));
        assertTrue(first.knowledge().knows(KnowledgeKind.RUNE, id("cyclic_rotate_y")));
        assertTrue(first.knowledge().knows(KnowledgeKind.THEOREM, id("quarter_turn")));

        KnowledgeDiscovery.Evaluation duplicate =
                KnowledgeDiscovery.evaluate(first.knowledge(), KnowledgeDefinitions.ROTATED_HORIZON);

        assertEquals(KnowledgeDiscovery.ReadResult.DUPLICATE, duplicate.result());
        assertSame(first.knowledge(), duplicate.knowledge());
    }

    @Test
    void unknownManuscriptDoesNotMutateKnowledge() {
        PlayerKnowledge original = PlayerKnowledge.empty();
        KnowledgeDiscovery.Evaluation result =
                KnowledgeDiscovery.evaluate(original, id("missing_record"));

        assertEquals(KnowledgeDiscovery.ReadResult.UNKNOWN, result.result());
        assertSame(original, result.knowledge());
    }

    @Test
    void p6ManuscriptsRevealTheirHypothesisWithoutCompletingTheEpiphany() {
        KnowledgeDiscovery.Evaluation bound = KnowledgeDiscovery.evaluate(
                PlayerKnowledge.empty(),
                KnowledgeDefinitions.BOUND_MEASURE
        );
        KnowledgeDiscovery.Evaluation remainder = KnowledgeDiscovery.evaluate(
                bound.knowledge(),
                KnowledgeDefinitions.LEDGER_OF_REMAINDERS
        );

        assertTrue(remainder.knowledge().knows(KnowledgeKind.THEOREM, id("soul_constraint")));
        assertTrue(remainder.knowledge().knows(KnowledgeKind.THEOREM, id("axiom_of_parsimony")));
        assertFalse(remainder.knowledge().knows(
                KnowledgeKind.EPIPHANY,
                KnowledgeDefinitions.VITAL_CORRESPONDENCE
        ));
        assertFalse(remainder.knowledge().knows(
                KnowledgeKind.EPIPHANY,
                KnowledgeDefinitions.CONSERVED_REMAINDER
        ));
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
