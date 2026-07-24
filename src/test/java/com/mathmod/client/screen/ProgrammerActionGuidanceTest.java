package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgrammerActionGuidanceTest {
    @Test
    void emptyTalismanExplainsWhyClearIsUnavailable() {
        assertEquals(
                "screen.mathmod.rune_programmer.clear_empty_hint",
                ProgrammerActionGuidance.clearTooltipKey(false, false)
        );
    }

    @Test
    void pendingInscriptionTakesPriorityOverStoredState() {
        assertEquals(
                "screen.mathmod.rune_programmer.clear_pending_hint",
                ProgrammerActionGuidance.clearTooltipKey(true, true)
        );
        assertEquals(
                "screen.mathmod.rune_programmer.clear_pending_hint",
                ProgrammerActionGuidance.clearTooltipKey(false, true)
        );
    }

    @Test
    void availableClearRetainsItsDestructiveScope() {
        assertEquals(
                "screen.mathmod.rune_programmer.clear_hint",
                ProgrammerActionGuidance.clearTooltipKey(true, false)
        );
    }

    @Test
    void undoDistinguishesAnEmptyLaboratoryFromAnExistingStep() {
        assertEquals(
                "screen.mathmod.rune_programmer.undo_custom_empty_hint",
                ProgrammerActionGuidance.undoTooltipKey(false)
        );
        assertEquals(
                "screen.mathmod.rune_programmer.undo_custom_hint",
                ProgrammerActionGuidance.undoTooltipKey(true)
        );
    }
}
