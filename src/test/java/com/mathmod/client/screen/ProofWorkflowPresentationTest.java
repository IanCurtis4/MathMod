package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProofWorkflowPresentationTest {
    @Test
    void graphWidthDecidesWhetherTheSigilCanCarryAStableLabel() {
        int commonJeiGraphWidth = ProgrammerLayout.forViewport(512, 400, true).graph().width();
        int minimumJeiGraphWidth = ProgrammerLayout.forViewport(320, 240, true).graph().width();
        int fixedHeaderWidth = 8 * 2 + 18;

        assertEquals(86, ProofWorkflowPresentation.sealWidth(commonJeiGraphWidth, fixedHeaderWidth, 20));
        assertEquals(10, ProofWorkflowPresentation.sealWidth(commonJeiGraphWidth, fixedHeaderWidth, 70));
        assertEquals(10, ProofWorkflowPresentation.sealWidth(minimumJeiGraphWidth, fixedHeaderWidth, 20));
        assertEquals(86, ProofWorkflowPresentation.sealWidth(176, fixedHeaderWidth, 36));
        assertEquals(10, ProofWorkflowPresentation.sealWidth(175, fixedHeaderWidth, 20));
    }

    @Test
    void everyWorkflowStateHasASeparateShortLabelKey() {
        long distinctKeys = Arrays.stream(ProofWorkflowState.values())
                .map(ProofWorkflowPresentation::shortTranslationKey)
                .distinct()
                .count();

        assertEquals(ProofWorkflowState.values().length, distinctKeys);
        Arrays.stream(ProofWorkflowState.values())
                .map(ProofWorkflowPresentation::shortTranslationKey)
                .forEach(key -> assertTrue(key.endsWith(".short")));
    }
}
