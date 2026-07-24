package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoundedTooltipPositionerTest {
    @Test
    void keepsPositionThatAlreadyReservesTheTooltipFrame() {
        TooltipBoundsPolicy.Position position = TooltipBoundsPolicy.boundedPosition(20, 30, 320, 240, 100, 80);

        assertEquals(20, position.x());
        assertEquals(30, position.y());
    }

    @Test
    void movesBottomOverflowAboveTheViewportMargin() {
        TooltipBoundsPolicy.Position position = TooltipBoundsPolicy.boundedPosition(20, 150, 320, 240, 100, 100);

        assertEquals(132, position.y());
    }

    @Test
    void movesRightOverflowInsideTheViewportMargin() {
        TooltipBoundsPolicy.Position position = TooltipBoundsPolicy.boundedPosition(250, 20, 320, 240, 100, 80);

        assertEquals(212, position.x());
    }

    @Test
    void raisesCoordinatesToTheViewportMargin() {
        TooltipBoundsPolicy.Position position = TooltipBoundsPolicy.boundedPosition(2, -4, 320, 240, 100, 80);

        assertEquals(8, position.x());
        assertEquals(8, position.y());
    }

    @Test
    void anchorsOversizedContentAsCloseToTheOriginAsPossible() {
        TooltipBoundsPolicy.Position position = TooltipBoundsPolicy.boundedPosition(12, 12, 100, 80, 104, 84);

        assertEquals(0, position.x());
        assertEquals(0, position.y());
    }
}
