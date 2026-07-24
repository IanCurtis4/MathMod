package com.mathmod.knowledge;

import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.ProgramPresets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KnowledgePolicyTest {
    @Test
    void onlyCuratedAdvancedConstructionsAreInitiallyLocked() {
        PlayerKnowledge knowledge = PlayerKnowledge.empty();

        assertTrue(KnowledgePolicy.canConstruct(
                knowledge,
                ProgramPresets.presetForId("mathmod:hop").orElseThrow()
        ));
        assertFalse(KnowledgePolicy.canConstruct(
                knowledge,
                ProgramPresets.presetForId("mathmod:harmonic_step").orElseThrow()
        ));
        assertFalse(KnowledgePolicy.canConstruct(
                knowledge,
                ProgramPresets.presetForId("mathmod:quarter_turn").orElseThrow()
        ));
        assertFalse(KnowledgePolicy.canConstruct(
                knowledge,
                ProgramPresets.presetForId("mathmod:soul_constraint").orElseThrow()
        ));
        assertFalse(KnowledgePolicy.canConstruct(
                knowledge,
                ProgramPresets.presetForId("mathmod:vital_infusion").orElseThrow()
        ));
        assertFalse(KnowledgePolicy.canConstruct(
                knowledge,
                ProgramPresets.presetForId("mathmod:axiom_of_parsimony").orElseThrow()
        ));
        assertFalse(KnowledgePolicy.canConstruct(
                knowledge,
                ProgramPresets.presetForId("mathmod:conservation_lemma").orElseThrow()
        ));
        assertFalse(KnowledgePolicy.canUse(knowledge, CustomSpellAction.SINE_NUMBER));
        assertFalse(KnowledgePolicy.canUse(knowledge, CustomSpellAction.QUARTER_TURN_VECTOR));
        assertFalse(KnowledgePolicy.canUse(knowledge, CustomSpellAction.SOUL_BIND_HOSTILES));
        assertFalse(KnowledgePolicy.canUse(knowledge, CustomSpellAction.VITAL_INFUSION_SELF));
        assertFalse(KnowledgePolicy.canUse(knowledge, CustomSpellAction.PARSIMONY_SELF));
        assertFalse(KnowledgePolicy.canUse(knowledge, CustomSpellAction.CONSERVATION_SELF));
    }

    @Test
    void epiphanyAndDiscoverySatisfyTheirOwnConstructionRoutes() {
        PlayerKnowledge knowledge = PlayerKnowledge.empty()
                .grant(KnowledgeKind.EPIPHANY, KnowledgeDefinitions.HARMONIC_MOTION)
                .grant(KnowledgeKind.DISCOVERY, KnowledgeDefinitions.ROTATED_HORIZON)
                .grant(KnowledgeKind.DISCOVERY, KnowledgeDefinitions.BOUND_MEASURE)
                .grant(KnowledgeKind.EPIPHANY, KnowledgeDefinitions.VITAL_CORRESPONDENCE)
                .grant(KnowledgeKind.DISCOVERY, KnowledgeDefinitions.LEDGER_OF_REMAINDERS)
                .grant(KnowledgeKind.EPIPHANY, KnowledgeDefinitions.CONSERVED_REMAINDER);

        assertTrue(KnowledgePolicy.canConstruct(
                knowledge,
                ProgramPresets.presetForId("mathmod:harmonic_step").orElseThrow()
        ));
        assertTrue(KnowledgePolicy.canConstruct(
                knowledge,
                ProgramPresets.presetForId("mathmod:quarter_turn").orElseThrow()
        ));
        assertTrue(KnowledgePolicy.canUse(knowledge, CustomSpellAction.COSINE_NUMBER));
        assertTrue(KnowledgePolicy.canUse(knowledge, CustomSpellAction.QUARTER_TURN_VECTOR));
        assertTrue(KnowledgePolicy.canUse(knowledge, CustomSpellAction.SOUL_BIND_HOSTILES));
        assertTrue(KnowledgePolicy.canUse(knowledge, CustomSpellAction.VITAL_INFUSION_SELF));
        assertTrue(KnowledgePolicy.canUse(knowledge, CustomSpellAction.PARSIMONY_SELF));
        assertTrue(KnowledgePolicy.canUse(knowledge, CustomSpellAction.CONSERVATION_SELF));
    }
}
