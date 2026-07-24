package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeaderNotationLayoutTest {
    @Test
    void programmerAndResourcesUseTheSameHelpNotationRelationship() {
        assertAligned(HeaderNotationLayout.alignedRight(396, 6, 18, 9), 396);
        assertAligned(HeaderNotationLayout.alignedRight(330, 6, 18, 9), 330);
        assertAligned(HeaderNotationLayout.alignedRight(360, 6, 46, 9), 360);
        assertAligned(HeaderNotationLayout.alignedRight(292, 6, 46, 9), 292);
        assertAligned(HeaderNotationLayout.alignedRight(360, 6, 43, 9), 360);
        assertAligned(HeaderNotationLayout.alignedRight(292, 6, 43, 9), 292);
    }

    private static void assertAligned(HeaderNotationLayout layout, int surfaceWidth) {
        ProgrammerLayout.Rect leadingAction = layout.leadingAction();
        assertEquals(6, layout.help().x() - leadingAction.right());
        assertEquals(6, layout.notation().x() - layout.help().right());
        assertEquals(2, layout.notation().y() - layout.help().y());
        assertEquals(layout.help().y(), leadingAction.y());
        assertTrue(leadingAction.x() >= 0);
        assertTrue(layout.help().x() >= 0);
        assertTrue(layout.notation().right() <= surfaceWidth);
        assertTrue(leadingAction.right() <= layout.help().x());
        assertTrue(layout.help().right() <= layout.notation().x());
    }
}
