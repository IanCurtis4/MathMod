package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgrammerLayoutTest {
    @Test
    void standardLayoutLeavesSpaceForThreeJeiColumnsAtScaleTwo() {
        ProgrammerLayout layout = ProgrammerLayout.forViewport(512, 400);

        assertFalse(layout.compact());
        assertTrue((512 - layout.width()) / 2 >= 54);
        assertLayoutFits(layout, 512, 400);
    }

    @Test
    void compactLayoutFitsA1024By800WindowAtGuiScaleThree() {
        ProgrammerLayout layout = ProgrammerLayout.forViewport(341, 266);

        assertTrue(layout.compact());
        assertLayoutFits(layout, 341, 266);
    }

    @Test
    void compactLayoutStillFitsTheMinimumPracticalViewport() {
        ProgrammerLayout layout = ProgrammerLayout.forViewport(320, 240);

        assertTrue(layout.compact());
        assertTrue(layout.graph().width() >= 150);
        assertLayoutFits(layout, 320, 240);
    }

    @Test
    void minimumViewportMovesAboveItemOverlayFooterWhenSideReserveCannotFit() {
        ProgrammerLayout layout = ProgrammerLayout.forViewport(320, 240, true);

        assertTrue(layout.compact());
        assertTrue((320 - layout.width()) / 2 < 64);
        assertTrue(centeredBottom(layout.height(), 240) <= 214);
        assertTrue(layout.palette().height() >= 100);
        assertLayoutFits(layout, 320, 240);
    }

    @Test
    void itemOverlayLayoutReservesBothSideColumnsWhenSpaceAllows() {
        ProgrammerLayout layout = ProgrammerLayout.forViewport(427, 240, true);

        assertTrue(layout.compact());
        assertTrue((427 - layout.width()) / 2 >= 64);
        assertLayoutFits(layout, 427, 240);
    }

    @Test
    void itemOverlayLayoutUsesCompactWidthAtTheAtmViewport() {
        ProgrammerLayout layout = ProgrammerLayout.forViewport(512, 400, true);

        assertTrue(layout.compact());
        assertTrue((512 - layout.width()) / 2 >= 64);
        assertLayoutFits(layout, 512, 400);
    }

    @Test
    void compactLaboratoryControlsFitAtScaleThreeWithItemOverlay() {
        ProgrammerLayout layout = ProgrammerLayout.forViewport(341, 266, true);

        assertTrue(layout.compact());
        assertTrue(layout.customName().width() >= 108);
        assertTrue(layout.customActions().get(0).width() >= ProgrammerLayout.COMPACT_PRIMARY_ACTION_WIDTH);
        assertTrue(layout.customActions().subList(1, 4).stream()
                .allMatch(rect -> rect.width() == ProgrammerLayout.COMPACT_ICON_ACTION_WIDTH));
        assertLayoutFits(layout, 341, 266);
    }

    @Test
    void standardLaboratoryReservesTheFullResourcesLabelWidth() {
        ProgrammerLayout layout = ProgrammerLayout.forViewport(800, 450, true);

        assertFalse(layout.compact());
        assertEquals(
                ProgrammerLayout.STANDARD_RESOURCE_ACTION_WIDTH,
                layout.customActions().get(3).width()
        );
        assertLayoutFits(layout, 800, 450);
    }

    @Test
    void atmCompactLaboratoryGivesTheNameFieldFreedIconSpace() {
        ProgrammerLayout layout = ProgrammerLayout.forViewport(512, 400, true);

        assertTrue(layout.compact());
        assertTrue(layout.customName().width() >= 140);
        assertTrue(layout.customActions().get(0).width() >= ProgrammerLayout.COMPACT_PRIMARY_ACTION_WIDTH);
        assertLayoutFits(layout, 512, 400);
    }

    @Test
    void compactDestructiveActionsReserveTheConfirmationLabelWidth() {
        ProgrammerLayout layout = ProgrammerLayout.forViewport(512, 400, true);

        assertTrue(layout.compact());
        assertTrue(layout.savedActions().get(3).width() >= ProgrammerLayout.COMPACT_CONFIRM_ACTION_WIDTH);
        assertTrue(layout.presetActions().get(2).width() >= ProgrammerLayout.COMPACT_CONFIRM_ACTION_WIDTH);
        assertLayoutFits(layout, 512, 400);
    }

    @Test
    void tabsFollowThePlayerWorkflowFromLeftToRight() {
        for (ProgrammerLayout layout : List.of(
                ProgrammerLayout.forViewport(512, 400),
                ProgrammerLayout.forViewport(512, 400, true),
                ProgrammerLayout.forViewport(320, 240, true)
        )) {
            assertTrue(layout.theoremTab().x() < layout.laboratoryTab().x());
            assertTrue(layout.laboratoryTab().x() < layout.inscribedTab().x());
            assertTrue(layout.theoremTab().right() <= layout.laboratoryTab().x());
            assertTrue(layout.laboratoryTab().right() <= layout.inscribedTab().x());
        }
    }

    private static void assertLayoutFits(ProgrammerLayout layout, int viewportWidth, int viewportHeight) {
        assertTrue(layout.width() <= viewportWidth);
        assertTrue(layout.height() <= viewportHeight);
        assertInside(layout.palette(), layout);
        assertInside(layout.graph(), layout);
        assertInside(layout.customSearch(), layout);
        assertInside(layout.customSearchContent(), layout);
        assertInside(layout.customNameContent(), layout);
        assertInside(layout.customSearchContent(), layout.customSearch());
        assertInside(layout.customNameContent(), layout.customName());
        assertTrue(layout.customSearchContent().width() == layout.customSearch().width() - 8);
        assertTrue(layout.customNameContent().width() == layout.customName().width() - 8);
        assertTrue(layout.customSearch().x() >= layout.palette().x());
        assertTrue(layout.customSearch().right() <= layout.palette().right());
        assertTrue(layout.customSearch().y() >= layout.palette().y());
        assertTrue(layout.customSearch().bottom() <= layout.palette().bottom());
        assertFalse(layout.palette().overlaps(layout.graph()));

        assertNonOverlapping(layout.tabs(), layout);
        assertNonOverlapping(layout.savedActions(), layout);
        assertNonOverlapping(layout.presetActions(), layout);

        List<ProgrammerLayout.Rect> custom = new ArrayList<>();
        custom.add(layout.customName());
        custom.addAll(layout.customActions());
        assertNonOverlapping(custom, layout);
    }

    private static void assertNonOverlapping(List<ProgrammerLayout.Rect> rectangles, ProgrammerLayout layout) {
        for (int i = 0; i < rectangles.size(); i++) {
            ProgrammerLayout.Rect rectangle = rectangles.get(i);
            assertInside(rectangle, layout);
            for (int j = i + 1; j < rectangles.size(); j++) {
                assertFalse(rectangle.overlaps(rectangles.get(j)), rectangle + " overlaps " + rectangles.get(j));
            }
        }
    }

    private static void assertInside(ProgrammerLayout.Rect rectangle, ProgrammerLayout layout) {
        assertTrue(rectangle.x() >= 0);
        assertTrue(rectangle.y() >= 0);
        assertTrue(rectangle.right() <= layout.width(), rectangle + " exceeds layout width " + layout.width());
        assertTrue(rectangle.bottom() <= layout.height(), rectangle + " exceeds layout height " + layout.height());
    }

    private static void assertInside(
            ProgrammerLayout.Rect inner,
            ProgrammerLayout.Rect outer
    ) {
        assertTrue(inner.x() >= outer.x());
        assertTrue(inner.y() >= outer.y());
        assertTrue(inner.right() <= outer.right());
        assertTrue(inner.bottom() <= outer.bottom());
    }

    private static int centeredBottom(int layoutHeight, int viewportHeight) {
        return (viewportHeight - layoutHeight) / 2 + layoutHeight;
    }
}
