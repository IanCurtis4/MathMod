package com.mathmod.network;

import com.mathmod.program.CustomSpellInvocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkPayloadLimitsTest {
    @Test
    void acceptsPayloadsAtTheServerBoundary() {
        assertTrue(NetworkPayloadLimits.acceptsCustomSpellName("A proof"));
        assertTrue(NetworkPayloadLimits.acceptsCustomInvocation("mathmod:number_one?value=0x1.0p0"));
    }

    @Test
    void rejectsOversizedClientAuthoredPayloads() {
        assertFalse(NetworkPayloadLimits.acceptsCustomSpellName(
                "n".repeat(NetworkPayloadLimits.MAX_CUSTOM_SPELL_NAME_LENGTH + 1)
        ));
        assertFalse(NetworkPayloadLimits.acceptsCustomInvocation(
                "i".repeat(NetworkPayloadLimits.MAX_CUSTOM_INVOCATION_LENGTH + 1)
        ));
    }

    @Test
    void rejectsOversizedPersistedInvocationBeforeParsing() {
        assertTrue(CustomSpellInvocation.fromPersistentId(
                "i".repeat(CustomSpellInvocation.MAX_PERSISTENT_ID_LENGTH + 1)
        ).isEmpty());
    }
}
