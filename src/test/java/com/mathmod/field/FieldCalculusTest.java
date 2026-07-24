package com.mathmod.field;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldCalculusTest {
    @Test
    void centeredGradientRecoversALinearFieldExactly() {
        FieldVector gradient = FieldCalculus.centeredGradient(5, 1, 8, 2, 10, 4, 1);
        assertEquals(new FieldVector(2, 3, 3), gradient);
    }

    @Test
    void invalidStepCannotCreateANumericalGradient() {
        assertThrows(IllegalArgumentException.class,
                () -> FieldCalculus.centeredGradient(1, 1, 1, 1, 1, 1, 0));
    }
}
