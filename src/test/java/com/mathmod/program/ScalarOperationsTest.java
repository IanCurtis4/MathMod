package com.mathmod.program;

import org.junit.jupiter.api.Test;

import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScalarOperationsTest {
    private static final double EPSILON = 1.0E-9D;

    @Test
    void scalarPrimitivesObeyReferenceValues() {
        assertEquals(3.0D, ScalarOperations.number("number_abs", -3.0D), EPSILON);
        assertEquals(-2.0D, ScalarOperations.number("number_min", 4.0D, -2.0D), EPSILON);
        assertEquals(4.0D, ScalarOperations.number("number_max", 4.0D, -2.0D), EPSILON);
        assertEquals(1024.0D, ScalarOperations.number("number_power", 2.0D, 10.0D), EPSILON);
        assertEquals(3.0D, ScalarOperations.number("number_sqrt", 9.0D), EPSILON);
        assertEquals(3.0D, ScalarOperations.number("number_log", 8.0D, 2.0D), EPSILON);
        assertEquals(1.0D, ScalarOperations.number("number_exp", 0.0D), EPSILON);
        assertEquals(Math.PI / 2.0D, ScalarOperations.number("number_atan2", 1.0D, 0.0D), EPSILON);
        assertEquals(4.0D, ScalarOperations.number("number_lerp", 2.0D, 10.0D, 0.25D), EPSILON);
    }

    @Test
    void powersAndDomainsFollowTheContract() {
        assertEquals(-8.0D, ScalarOperations.number("number_power", -2.0D, 3.0D), EPSILON);
        assertEquals(1.0D, ScalarOperations.number("number_power", 0.0D, 0.0D), EPSILON);
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_power", -2.0D, 0.5D));
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_power", 0.0D, -1.0D));
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_sqrt", -ScalarOperations.EPSILON));
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_log", 0.0D, 2.0D));
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_log", 2.0D, 1.0D));
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_atan2", 0.0D, 0.0D));
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_lerp", 0.0D, 1.0D, -ScalarOperations.EPSILON));
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_lerp", 0.0D, 1.0D, 1.0D + ScalarOperations.EPSILON));
    }

    @Test
    void scalarBoundaryRejectsNonFiniteAndOversizedResults() {
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_abs", Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_exp", 20.0D));
        assertThrows(IllegalArgumentException.class, () -> ScalarOperations.number("number_power", 1024.0D, 3.0D));
    }

    @Test
    void comparisonAndSelectionAreDeterministic() {
        assertTrue(ScalarOperations.atLeast(2.0D, 2.0D));
        assertFalse(ScalarOperations.atLeast(1.0D, 2.0D));
        assertEquals(7.0D, ScalarOperations.number("number_select", 0.0D, 3.0D, 7.0D), EPSILON);
        assertEquals(3.0D, ScalarOperations.number("number_select", 1.0D, 3.0D, 7.0D), EPSILON);
    }

    @Test
    void descriptorsHaveRegisteredRunesAndSupportedExecutors() {
        MathModRuneBootstrap.bootstrap();
        for (ScalarOperations.Descriptor descriptor : ScalarOperations.descriptors()) {
            assertTrue(ProgramStorage.definition(descriptor.runeId()).isPresent());
            assertTrue(ProgramExecutionPolicy.supportsExecutorKey(descriptor.executorKey()));
        }
    }

    @Test
    void closedInvalidScalarGraphIsRejectedBeforeInscription() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("value", "mathmod:constant_number", Map.of("value", "-1")),
                        new ProgramNode("sqrt", "mathmod:number_sqrt")
                ),
                List.of(new ProgramEdge("value", "sqrt", "value")),
                "sqrt",
                16
        );

        assertFalse(ProgramStorage.validateExecutable(graph).valid());
    }
}
