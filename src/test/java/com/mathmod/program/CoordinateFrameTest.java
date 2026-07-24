package com.mathmod.program;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoordinateFrameTest {
    private static final double EPSILON = 1.0E-9D;

    @Test
    void horizontalFrameBuildsAnOrthonormalRightHandedBasis() {
        CoordinateFrame frame = CoordinateFrame.horizontal(0.0D, 1.0D);

        assertEquals(1.0D, frame.right().lengthSquared(), EPSILON);
        assertEquals(1.0D, frame.up().lengthSquared(), EPSILON);
        assertEquals(1.0D, frame.forward().lengthSquared(), EPSILON);
        assertEquals(0.0D, frame.right().dot(frame.up()), EPSILON);
        assertEquals(0.0D, frame.right().dot(frame.forward()), EPSILON);
        assertEquals(0.0D, frame.up().dot(frame.forward()), EPSILON);
    }

    @Test
    void localCoordinatesTransformIntoWorldAxes() {
        CoordinateFrame frame = CoordinateFrame.horizontal(0.0D, 1.0D);

        CoordinateFrame.Axis world = frame.toWorld(2.0D, 3.0D, 4.0D);

        assertEquals(-2.0D, world.x(), EPSILON);
        assertEquals(3.0D, world.y(), EPSILON);
        assertEquals(4.0D, world.z(), EPSILON);
    }

    @Test
    void verticalDirectionCannotDefineAHorizontalFrame() {
        assertThrows(IllegalArgumentException.class,
                () -> CoordinateFrame.horizontal(0.0D, 0.0D));
    }
}
