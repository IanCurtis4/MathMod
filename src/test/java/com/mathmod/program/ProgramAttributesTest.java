package com.mathmod.program;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgramAttributesTest {
    @Test
    void customAttributesHaveStableKeysAndReadableFallbacks() {
        assertEquals("attribute.mathmod.motion", ProgramAttributes.translationKey("motion"));
        assertEquals("Ancient Entropy", ProgramAttributes.fallbackLabel("ancient_entropy"));
        assertEquals("Pack Resonance", ProgramAttributes.fallbackLabel("pack:resonance"));
    }
}
