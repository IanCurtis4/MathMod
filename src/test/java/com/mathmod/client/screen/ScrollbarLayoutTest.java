package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScrollbarLayoutTest {
    @Test
    void proportionalThumbMapsBothScrollEndpoints() {
        ScrollbarLayout.Geometry top = ScrollbarLayout.geometry(
                90, 20, 100, 100, 400, 0, 300
        );
        ScrollbarLayout.Geometry bottom = ScrollbarLayout.geometry(
                90, 20, 100, 100, 400, 300, 300
        );

        assertEquals(25, top.thumbHeight());
        assertEquals(20, top.thumbY());
        assertEquals(95, bottom.thumbY());
    }

    @Test
    void shortContentUsesTheMinimumThumbHeight() {
        ScrollbarLayout.Geometry geometry = ScrollbarLayout.geometry(
                40, 10, 48, 48, 480, 0, 432
        );

        assertEquals(12, geometry.thumbHeight());
    }

    @Test
    void hitAreaIsWiderThanTheVisualTrackWithoutCapturingRowsBeyondIt() {
        ScrollbarLayout.Geometry geometry = ScrollbarLayout.geometry(
                90, 20, 100, 100, 400, 0, 300
        );

        assertTrue(geometry.contains(87, 50));
        assertTrue(geometry.contains(94, 50));
        assertFalse(geometry.contains(86.99, 50));
        assertFalse(geometry.contains(95, 50));
        assertFalse(geometry.contains(90, 120));
    }

    @Test
    void draggingTheThumbMapsToTheCompleteScrollRange() {
        ScrollbarLayout.Geometry geometry = ScrollbarLayout.geometry(
                90, 20, 100, 100, 400, 0, 300
        );
        int offset = geometry.dragOffset(25);

        assertEquals(0, geometry.scrollAt(20 + offset, offset));
        assertEquals(300, geometry.scrollAt(95 + offset, offset));
    }

    @Test
    void trackClickCentersTheThumbOnThePointer() {
        ScrollbarLayout.Geometry geometry = ScrollbarLayout.geometry(
                90, 20, 100, 100, 400, 0, 300
        );

        assertEquals(152, geometry.scrollAt(70, geometry.dragOffset(70)));
    }

    @Test
    void stepAlignmentKeepsWholeRowsAndStillReachesTheExactEnd() {
        assertEquals(24, ScrollbarLayout.nearestStep(35, 24, 96));
        assertEquals(48, ScrollbarLayout.nearestStep(37, 24, 96));
        assertEquals(95, ScrollbarLayout.nearestStep(95, 24, 95));
    }
}
