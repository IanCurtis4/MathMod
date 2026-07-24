package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaletteCursorTest {
    @Test
    void movementStopsAtBothEnds() {
        PaletteCursor cursor = new PaletteCursor(4);

        cursor.move(-1);
        assertEquals(0, cursor.index());
        cursor.move(9);
        assertEquals(3, cursor.index());
    }

    @Test
    void firstAndLastSelectTheExpectedRows() {
        PaletteCursor cursor = new PaletteCursor(5);

        cursor.last();
        assertEquals(4, cursor.index());
        cursor.first();
        assertEquals(0, cursor.index());
    }

    @Test
    void resizingClampsAnExistingSelection() {
        PaletteCursor cursor = new PaletteCursor(6);
        cursor.last();

        cursor.resize(2);

        assertEquals(1, cursor.index());
    }

    @Test
    void emptyPalettesRetainASafeZeroCursor() {
        PaletteCursor cursor = new PaletteCursor(0);

        cursor.last();
        cursor.move(3);

        assertEquals(0, cursor.index());
    }

    @Test
    void visibleRowsDoNotMoveTheScrollPosition() {
        assertEquals(20, PaletteCursor.revealRow(20, 30, 16, 80, 200));
    }

    @Test
    void rowsAboveAndBelowTheViewportAreRevealed() {
        assertEquals(12, PaletteCursor.revealRow(40, 12, 16, 80, 200));
        assertEquals(66, PaletteCursor.revealRow(20, 130, 16, 80, 200));
    }

    @Test
    void revealedScrollIsClampedToTheContentBounds() {
        assertEquals(0, PaletteCursor.revealRow(10, -8, 16, 80, 200));
        assertEquals(100, PaletteCursor.revealRow(20, 180, 20, 80, 100));
    }

    @Test
    void resourceMaterialKeyboardCursorRevealsTheLastCompleteCard() {
        int rowHeight = 24;
        int viewportHeight = 144;
        int materialCount = 10;
        int maxScroll = materialCount * rowHeight - viewportHeight;

        assertEquals(
                96,
                PaletteCursor.revealRow(
                        0,
                        (materialCount - 1) * rowHeight,
                        rowHeight,
                        viewportHeight,
                        maxScroll
                )
        );
    }

    @Test
    void wrappedAddedMaterialKeyboardCursorRevealsTheWholeSelection() {
        int lineHeight = 11;
        int viewportHeight = 99;
        int firstSelectionLine = 10;
        int selectionLines = 2;
        int maxScroll = 77;

        assertEquals(
                33,
                PaletteCursor.revealRow(
                        0,
                        firstSelectionLine * lineHeight,
                        selectionLines * lineHeight,
                        viewportHeight,
                        maxScroll
                )
        );
    }

    @Test
    void paletteViewportFitsOnlyCompleteRows() {
        assertEquals(112, PaletteCursor.wholeRowsHeight(115, 16));
        assertEquals(112, PaletteCursor.wholeRowsHeight(127, 16));
        assertEquals(0, PaletteCursor.wholeRowsHeight(15, 16));
    }

    @Test
    void invalidPaletteViewportDimensionsAreSafe() {
        assertEquals(0, PaletteCursor.wholeRowsHeight(80, 0));
        assertEquals(0, PaletteCursor.wholeRowsHeight(-4, 16));
    }

    @Test
    void onlyRowsFullyInsideTheViewportAreRendered() {
        assertTrue(PaletteCursor.rowFits(0, 16, 0, 112));
        assertTrue(PaletteCursor.rowFits(96, 16, 0, 112));
        assertFalse(PaletteCursor.rowFits(-16, 16, 0, 112));
        assertFalse(PaletteCursor.rowFits(112, 16, 0, 112));
    }

    @Test
    void groupedRowsRenderOnlyWhenTheRequiredContinuationFits() {
        assertTrue(PaletteCursor.rowsFit(80, 16, 2, 0, 112));
        assertFalse(PaletteCursor.rowsFit(96, 16, 2, 0, 112));
        assertFalse(PaletteCursor.rowsFit(0, 16, 0, 0, 112));
    }

    @Test
    void mixedHeightSectionHeaderRequiresItsFirstCard() {
        int sectionHeight = 16 + 30;

        assertTrue(PaletteCursor.rowFits(48, sectionHeight, 0, 96));
        assertFalse(PaletteCursor.rowFits(64, sectionHeight, 0, 96));
    }

    @Test
    void graphResourceHeadingRequiresItsFirstCostLine() {
        int headingAndFirstCostHeight = 11 * 2;

        assertTrue(PaletteCursor.rowFits(66, headingAndFirstCostHeight, 0, 88));
        assertFalse(PaletteCursor.rowFits(77, headingAndFirstCostHeight, 0, 88));
    }

    @Test
    void mixedHeightPalettesUseARowBoundaryForTheirMaximumScroll() {
        int[] rowStarts = {0, 16, 46, 76, 106, 136, 166};

        assertEquals(106, PaletteCursor.alignedMaxScroll(196, 100, rowStarts));
    }

    @Test
    void revealingAMixedHeightRowNeverStopsInsideAnotherRow() {
        int[] rowStarts = {0, 16, 46, 76, 106, 136, 166};

        assertEquals(
                76,
                PaletteCursor.revealAlignedRow(0, 136, 30, 100, 106, rowStarts)
        );
        assertEquals(
                16,
                PaletteCursor.revealAlignedRow(106, 16, 30, 100, 106, rowStarts)
        );
    }

    @Test
    void mixedHeightMouseScrollingMovesBetweenRealRowBoundaries() {
        int[] rowStarts = {0, 16, 46, 76, 106, 136, 166};

        assertEquals(16, PaletteCursor.moveAlignedScroll(0, 1, 106, rowStarts));
        assertEquals(46, PaletteCursor.moveAlignedScroll(16, 1, 106, rowStarts));
        assertEquals(16, PaletteCursor.moveAlignedScroll(46, -1, 106, rowStarts));
        assertEquals(106, PaletteCursor.moveAlignedScroll(106, 1, 106, rowStarts));
    }

    @Test
    void scrollbarDraggingSnapsToTheNearestMixedHeightBoundary() {
        int[] rowStarts = {0, 16, 46, 76, 106, 136, 166};

        assertEquals(16, PaletteCursor.nearestAlignedScroll(24, 106, rowStarts));
        assertEquals(46, PaletteCursor.nearestAlignedScroll(39, 106, rowStarts));
        assertEquals(106, PaletteCursor.nearestAlignedScroll(102, 106, rowStarts));
    }

    @Test
    void savedProofTextScrollsAcrossWholeLinesAndSpacers() {
        int[] rowStarts = {0, 11, 15, 26, 37, 41, 52, 63, 74, 85};
        int contentHeight = 96;
        int viewportHeight = 50;
        int maxScroll = PaletteCursor.alignedMaxScroll(
                contentHeight,
                viewportHeight,
                rowStarts
        );

        assertEquals(52, maxScroll);
        assertEquals(11, PaletteCursor.moveAlignedScroll(0, 1, maxScroll, rowStarts));
        assertEquals(15, PaletteCursor.moveAlignedScroll(11, 1, maxScroll, rowStarts));
        assertEquals(11, PaletteCursor.moveAlignedScroll(15, -1, maxScroll, rowStarts));
        assertTrue(PaletteCursor.rowFits(85 - maxScroll, 11, 0, viewportHeight));
    }
}
