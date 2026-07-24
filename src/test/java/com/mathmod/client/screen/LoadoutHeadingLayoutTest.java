package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoadoutHeadingLayoutTest {
    @Test
    void longNamesStayInsideTheLoadoutPanelAndExposeTheirClippedRegion() {
        LoadoutHeadingLayout layout = LoadoutHeadingLayout.forWidths(16, 132, 58, 8, 180);

        assertTrue(layout.nameClipped());
        assertTrue(layout.nameX() + layout.visibleNameWidth() <= 148);
        assertFalse(layout.isOverClippedName(layout.nameX() - 1));
        assertTrue(layout.isOverClippedName(layout.nameX()));
        assertTrue(layout.isOverClippedName(layout.nameX() + layout.visibleNameWidth() - 1));
        assertFalse(layout.isOverClippedName(layout.nameX() + layout.visibleNameWidth()));
    }

    @Test
    void fittingNamesDoNotCreateAContextualTooltipTarget() {
        LoadoutHeadingLayout layout = LoadoutHeadingLayout.forWidths(16, 180, 58, 8, 42);

        assertFalse(layout.nameClipped());
        assertFalse(layout.isOverClippedName(layout.nameX()));
    }

    @Test
    void exhaustedHeaderSpaceCannotCrossThePanelBoundary() {
        LoadoutHeadingLayout layout = LoadoutHeadingLayout.forWidths(16, 48, 60, 8, 180);

        assertTrue(layout.nameClipped());
        assertEquals(0, layout.availableNameWidth());
        assertFalse(layout.isOverClippedName(layout.nameX()));
    }
}
