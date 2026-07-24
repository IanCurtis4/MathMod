package com.mathmod.program;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgramNamesTest {
    @Test
    void blankNamesBecomeDefaultCustomSpellName() {
        assertEquals(ProgramNames.DEFAULT_CUSTOM_NAME, ProgramNames.sanitize("   "));
    }

    @Test
    void optionalBlankNamesRemainAbsent() {
        assertEquals("", ProgramNames.sanitizeOptional(null));
        assertEquals("", ProgramNames.sanitizeOptional("   "));
    }

    @Test
    void namesAreTrimmedAndLimited() {
        assertEquals("Arcane Lift", ProgramNames.sanitize("  Arcane Lift  "));
        assertEquals("Arcane Lift", ProgramNames.sanitizeOptional("  Arcane Lift  "));
        assertEquals(ProgramNames.MAX_LENGTH, ProgramNames.sanitize("abcdefghijklmnopqrstuvwxyz0123456789").length());
    }

    @Test
    void controlCharactersAreRemoved() {
        assertEquals("LiftSpell", ProgramNames.sanitize("Lift\nSpell"));
    }
}
