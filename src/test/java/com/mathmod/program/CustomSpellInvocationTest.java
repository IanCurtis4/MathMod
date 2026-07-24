package com.mathmod.program;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomSpellInvocationTest {
    @Test
    void parameterizedInvocationRoundTripsThroughPersistentForm() {
        CustomSpellInvocation original = new CustomSpellInvocation(
                CustomSpellAction.SIMPSON_INTEGRAL,
                Map.of(
                        "lower", -2.5D,
                        "upper", 3.0D,
                        "f_lower", 1.0D,
                        "f_midpoint", 4.0D,
                        "f_upper", 9.0D
                )
        );

        CustomSpellInvocation decoded = CustomSpellInvocation
                .fromPersistentId(original.persistentId())
                .orElseThrow();

        assertEquals(original, decoded);
    }

    @Test
    void legacyActionIdLoadsWithParameterDefaults() {
        CustomSpellInvocation decoded = CustomSpellInvocation
                .fromPersistentId(CustomSpellAction.NUMBER_ONE.persistentId())
                .orElseThrow();

        assertEquals(CustomSpellAction.NUMBER_ONE, decoded.action());
        assertEquals(1.0D, decoded.argument("value"));
    }

    @Test
    void suppliedParametersAreClampedAtTheDomainBoundary() {
        CustomSpellInvocation invocation = new CustomSpellInvocation(
                CustomSpellAction.NUMBER_ONE,
                Map.of("value", 1_000_000.0D)
        );

        assertEquals(1024.0D, invocation.argument("value"));
        assertTrue(Double.isFinite(invocation.argument("value")));
    }
}
