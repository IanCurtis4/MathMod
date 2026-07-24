package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ButtonDisplayPolicyTest {
    @Test
    void textualButtonFollowsItsCurrentLabel() {
        assertEquals("Inscribed", ButtonDisplayPolicy.visibleLabel("Inscribed", null));
    }

    @Test
    void iconButtonKeepsItsFixedSymbol() {
        assertEquals("<-", ButtonDisplayPolicy.visibleLabel("Undo", "<-"));
    }

    @Test
    void compactIconsUseIconPaddingInsteadOfTextPadding() {
        assertEquals(14, ButtonDisplayPolicy.horizontalPadding(null));
        assertEquals(6, ButtonDisplayPolicy.horizontalPadding("?"));
    }
}
