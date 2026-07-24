package com.mathmod.program;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MathematicalOperationsTest {
    private static final double EPSILON = 1.0E-9D;

    @Test
    void trigonometryUsesRadians() {
        assertEquals(1.0D, MathematicalOperations.sine(Math.PI / 2.0D), EPSILON);
        assertEquals(-1.0D, MathematicalOperations.cosine(Math.PI), EPSILON);
    }

    @Test
    void finiteDifferenceApproximatesAChordSlopeAndRejectsZeroStep() {
        assertEquals(3.0D, MathematicalOperations.finiteDifference(1.0D, 7.0D, 2.0D), EPSILON);
        assertThrows(
                IllegalArgumentException.class,
                () -> MathematicalOperations.finiteDifference(1.0D, 2.0D, 0.0D)
        );
    }

    @Test
    void simpsonRuleIntegratesAQuadraticExactlyAcrossOnePanel() {
        assertEquals(
                1.0D / 3.0D,
                MathematicalOperations.simpsonIntegral(0.0D, 0.25D, 1.0D, 1.0D),
                EPSILON
        );
    }

    @Test
    void vectorOperationsPreserveTheirGeometricDefinitions() {
        var x = new MathematicalOperations.Vector(1.0D, 0.0D, 0.0D);
        var y = new MathematicalOperations.Vector(0.0D, 1.0D, 0.0D);
        var diagonal = new MathematicalOperations.Vector(2.0D, 3.0D, 0.0D);

        assertEquals(new MathematicalOperations.Vector(0.0D, 0.0D, 1.0D), MathematicalOperations.cross(x, y));
        assertEquals(new MathematicalOperations.Vector(2.0D, 0.0D, 0.0D), MathematicalOperations.project(diagonal, x));
        assertEquals(new MathematicalOperations.Vector(2.0D, -3.0D, 0.0D), MathematicalOperations.reflect(diagonal, y));
        assertThrows(
                IllegalArgumentException.class,
                () -> MathematicalOperations.project(diagonal, new MathematicalOperations.Vector(0.0D, 0.0D, 0.0D))
        );
    }

    @Test
    void cyclicElementsComposeInvertAndActAsRotations() {
        CyclicGroupElement quarter = new CyclicGroupElement(4, 1);
        CyclicGroupElement half = quarter.compose(quarter);

        assertEquals(new CyclicGroupElement(4, 2), half);
        assertEquals(new CyclicGroupElement(4, 3), quarter.inverse());
        assertEquals(new CyclicGroupElement(4, 0), quarter.compose(quarter.inverse()));

        var rotated = MathematicalOperations.rotateY(
                new MathematicalOperations.Vector(1.0D, 0.0D, 0.0D),
                quarter
        );
        assertEquals(0.0D, rotated.x(), EPSILON);
        assertEquals(0.0D, rotated.y(), EPSILON);
        assertEquals(1.0D, rotated.z(), EPSILON);
    }

    @Test
    void cyclicElementsRequireIntegralParametersAndMatchingGroups() {
        assertThrows(IllegalArgumentException.class, () -> MathematicalOperations.cyclicElement(4.5D, 1.0D));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CyclicGroupElement(4, 1).compose(new CyclicGroupElement(5, 1))
        );
    }
}
