package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourcesLayoutTest {
    @Test
    void standardResourcesLayoutFitsScaleTwoViewport() {
        ResourcesLayout layout = ResourcesLayout.forViewport(512, 400);

        assertFalse(layout.compact());
        assertLayoutFits(layout, 512, 400);
    }

    @Test
    void compactResourcesLayoutFitsScaleThreeViewport() {
        ResourcesLayout layout = ResourcesLayout.forViewport(341, 266);

        assertTrue(layout.compact());
        assertLayoutFits(layout, 341, 266);
    }

    @Test
    void compactResourcesLayoutFitsMinimumPracticalViewport() {
        ResourcesLayout layout = ResourcesLayout.forViewport(320, 240);

        assertTrue(layout.compact());
        assertTrue(layout.rightPanel().width() >= 120);
        assertTrue(layout.actions().get(1).width() >= ResourcesLayout.COMPACT_CONFIRM_ACTION_WIDTH);
        assertLayoutFits(layout, 320, 240);
    }

    @Test
    void minimumResourcesViewportMovesAboveItemOverlayFooterWhenSidesCannotFit() {
        ResourcesLayout layout = ResourcesLayout.forViewport(320, 240, true);

        assertTrue(layout.compact());
        assertTrue((320 - layout.width()) / 2 < 64);
        assertTrue(centeredBottom(layout.height(), 240) <= 214);
        assertTrue(layout.leftPanel().height() >= 100);
        assertLayoutFits(layout, 320, 240);
    }

    @Test
    void resourcesLayoutReservesItemOverlayAtNarrowViewport() {
        ResourcesLayout layout = ResourcesLayout.forViewport(427, 240, true);

        assertTrue(layout.compact());
        assertTrue((427 - layout.width()) / 2 >= 64);
        assertLayoutFits(layout, 427, 240);
    }

    @Test
    void resourcesLayoutKeepsStandardPanelsWhenOverlayHasRoom() {
        ResourcesLayout layout = ResourcesLayout.forViewport(512, 400, true);

        assertFalse(layout.compact());
        assertTrue((512 - layout.width()) / 2 >= 64);
        assertLayoutFits(layout, 512, 400);
    }

    @Test
    void resourceViewportsUseOnlyCompleteTextAndMaterialRows() {
        ResourcesLayout standard = ResourcesLayout.forViewport(512, 400);
        ResourcesLayout compact = ResourcesLayout.forViewport(320, 240);

        assertEquals(154, PaletteCursor.wholeRowsHeight(panelContentHeight(standard), 11));
        assertEquals(144, PaletteCursor.wholeRowsHeight(panelContentHeight(standard), 24));
        assertWholeRowsViewport(compact, 11);
        assertWholeRowsViewport(compact, 24);
    }

    private static void assertLayoutFits(ResourcesLayout layout, int viewportWidth, int viewportHeight) {
        assertTrue(layout.width() <= viewportWidth);
        assertTrue(layout.height() <= viewportHeight);
        assertEquals(3, layout.actions().size());
        assertInside(layout.leftPanel(), layout);
        assertInside(layout.rightPanel(), layout);
        assertFalse(layout.leftPanel().overlaps(layout.rightPanel()));
        assertNonOverlapping(layout.actions(), layout);
    }

    private static void assertNonOverlapping(List<ProgrammerLayout.Rect> rectangles, ResourcesLayout layout) {
        for (int i = 0; i < rectangles.size(); i++) {
            assertInside(rectangles.get(i), layout);
            for (int j = i + 1; j < rectangles.size(); j++) {
                assertFalse(rectangles.get(i).overlaps(rectangles.get(j)));
            }
        }
    }

    private static void assertInside(ProgrammerLayout.Rect rectangle, ResourcesLayout layout) {
        assertTrue(rectangle.x() >= 0);
        assertTrue(rectangle.y() >= 0);
        assertTrue(rectangle.right() <= layout.width());
        assertTrue(rectangle.bottom() <= layout.height());
    }

    private static int panelContentHeight(ResourcesLayout layout) {
        return layout.height() - layout.leftPanel().y() - layout.bottomPadding() - 4;
    }

    private static void assertWholeRowsViewport(ResourcesLayout layout, int rowHeight) {
        int available = panelContentHeight(layout);
        int viewport = PaletteCursor.wholeRowsHeight(available, rowHeight);

        assertEquals(0, viewport % rowHeight);
        assertTrue(viewport <= available);
        assertTrue(available - viewport < rowHeight);
    }

    private static int centeredBottom(int layoutHeight, int viewportHeight) {
        return (viewportHeight - layoutHeight) / 2 + layoutHeight;
    }
}
