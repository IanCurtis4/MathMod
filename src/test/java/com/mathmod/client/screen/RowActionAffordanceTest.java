package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RowActionAffordanceTest {
    @Test
    void actionStaysCenteredBeforeScrollbarReserve() {
        RowActionAffordance.Geometry geometry = RowActionAffordance.layout(200, 140, 60, 24, 11);

        assertEquals(324, geometry.x());
        assertEquals(66, geometry.y());
        assertEquals(11, geometry.width());
        assertEquals(11, geometry.height());
        assertEquals(321, geometry.textRight());
        assertEquals(5, 200 + 140 - geometry.x() - geometry.width());
    }

    @Test
    void preparedActionReservesTextWithoutCollapsingIt() {
        RowActionAffordance.Geometry geometry = RowActionAffordance.layout(20, 132, 0, 11, 9);

        assertEquals(138, geometry.x());
        assertEquals(1, geometry.y());
        assertEquals(110, RowActionAffordance.textWidth(25, geometry));
        assertTrue(geometry.textRight() < geometry.x());
    }

    @Test
    void textWidthNeverBecomesNegative() {
        RowActionAffordance.Geometry geometry = RowActionAffordance.layout(0, 12, 0, 11, 9);

        assertEquals(0, RowActionAffordance.textWidth(8, geometry));
    }
}
