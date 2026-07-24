package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SavedProofGuidanceTest {
    @Test
    void missingWitnessesDirectEveryProofToResources() {
        assertEquals(
                "screen.mathmod.rune_programmer.saved_witnesses_hint",
                SavedProofGuidance.translationKey(ProofWorkflowState.WITNESSES_REQUIRED, false)
        );
        assertEquals(
                "screen.mathmod.rune_programmer.saved_witnesses_hint",
                SavedProofGuidance.translationKey(ProofWorkflowState.WITNESSES_REQUIRED, true)
        );
    }

    @Test
    void readyProofDirectsEveryProofToCasting() {
        assertEquals(
                "screen.mathmod.rune_programmer.saved_cast_ready_hint",
                SavedProofGuidance.translationKey(ProofWorkflowState.CAST_READY, false)
        );
        assertEquals(
                "screen.mathmod.rune_programmer.saved_cast_ready_hint",
                SavedProofGuidance.translationKey(ProofWorkflowState.CAST_READY, true)
        );
    }

    @Test
    void nonTerminalStatesRetainProofSpecificEditingGuidance() {
        assertEquals(
                "screen.mathmod.rune_programmer.saved_preset_hint",
                SavedProofGuidance.translationKey(ProofWorkflowState.INCOMPLETE, false)
        );
        assertEquals(
                "screen.mathmod.rune_programmer.saved_custom_hint",
                SavedProofGuidance.translationKey(ProofWorkflowState.INCOMPLETE, true)
        );
    }
}
