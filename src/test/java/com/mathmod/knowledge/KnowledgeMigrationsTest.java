package com.mathmod.knowledge;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KnowledgeMigrationsTest {
    @Test
    void p1PlayersKeepPreviouslyOpenAdvancedCatalogEntries() {
        PlayerKnowledge p1 = new PlayerKnowledge(
                1,
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Map.of()
        );

        PlayerKnowledge migrated = KnowledgeMigrations.migrate(
                p1,
                KnowledgeAliasRegistry.builder().build()
        );

        assertEquals(PlayerKnowledge.CURRENT_SCHEMA_VERSION, migrated.schemaVersion());
        assertTrue(migrated.knows(KnowledgeKind.EPIPHANY, KnowledgeDefinitions.HARMONIC_MOTION));
        assertTrue(migrated.knows(KnowledgeKind.DISCOVERY, KnowledgeDefinitions.ROTATED_HORIZON));
        assertTrue(migrated.knows(KnowledgeKind.EPIPHANY, KnowledgeDefinitions.VITAL_CORRESPONDENCE));
        assertTrue(migrated.knows(KnowledgeKind.EPIPHANY, KnowledgeDefinitions.CONSERVED_REMAINDER));
        assertTrue(migrated.knows(KnowledgeKind.DISCOVERY, KnowledgeDefinitions.BOUND_MEASURE));
        assertTrue(migrated.knows(KnowledgeKind.DISCOVERY, KnowledgeDefinitions.LEDGER_OF_REMAINDERS));
    }

    @Test
    void p5PlayersKeepCatalogEntriesThatP6NowGates() {
        PlayerKnowledge p5 = new PlayerKnowledge(
                2,
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Map.of()
        );

        PlayerKnowledge migrated = KnowledgeMigrations.migrate(
                p5,
                KnowledgeAliasRegistry.builder().build()
        );

        assertTrue(migrated.knows(KnowledgeKind.THEOREM, id("soul_constraint")));
        assertTrue(migrated.knows(KnowledgeKind.THEOREM, id("vital_infusion")));
        assertTrue(migrated.knows(KnowledgeKind.THEOREM, id("axiom_of_parsimony")));
        assertTrue(migrated.knows(KnowledgeKind.THEOREM, id("conservation_lemma")));
        assertFalse(migrated.knows(KnowledgeKind.EPIPHANY, KnowledgeDefinitions.HARMONIC_MOTION));
        assertFalse(migrated.knows(KnowledgeKind.DISCOVERY, KnowledgeDefinitions.ROTATED_HORIZON));
    }

    @Test
    void newP6PlayersDoNotReceiveLegacyGrants() {
        PlayerKnowledge current = PlayerKnowledge.empty();

        PlayerKnowledge migrated = KnowledgeMigrations.migrate(
                current,
                KnowledgeAliasRegistry.builder().build()
        );

        assertFalse(migrated.knows(KnowledgeKind.EPIPHANY, KnowledgeDefinitions.HARMONIC_MOTION));
        assertFalse(migrated.knows(KnowledgeKind.DISCOVERY, KnowledgeDefinitions.ROTATED_HORIZON));
        assertFalse(migrated.knows(KnowledgeKind.EPIPHANY, KnowledgeDefinitions.VITAL_CORRESPONDENCE));
        assertFalse(migrated.knows(KnowledgeKind.DISCOVERY, KnowledgeDefinitions.BOUND_MEASURE));
    }

    private static com.mathmod.util.NamespacedId id(String path) {
        return com.mathmod.util.NamespacedId.of("mathmod", path);
    }
}
