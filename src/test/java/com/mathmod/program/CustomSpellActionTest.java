package com.mathmod.program;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomSpellActionTest {
    @Test
    void laboratoryMacrosHaveDistinctCatalogIcons() {
        long distinctIcons = Arrays.stream(CustomSpellAction.values())
                .map(CustomSpellAction::iconRuneId)
                .distinct()
                .count();

        assertEquals(CustomSpellAction.values().length, distinctIcons);
    }

    @Test
    void laboratoryMacrosHaveUniqueCompactNotation() {
        long distinctNotation = Arrays.stream(CustomSpellAction.values())
                .map(CustomSpellAction::compactNotation)
                .distinct()
                .count();

        assertEquals(CustomSpellAction.values().length, distinctNotation);
    }

    @Test
    void compactNotationStaysWithinThePaletteTextBudget() {
        assertTrue(Arrays.stream(CustomSpellAction.values())
                .map(CustomSpellAction::compactNotation)
                .allMatch(notation -> !notation.isBlank() && notation.length() <= 12));
    }

    @Test
    void laboratoryFormsHaveStableNamespacedIdsAndReadLegacyEnumNames() {
        long distinctIds = Arrays.stream(CustomSpellAction.values())
                .map(CustomSpellAction::id)
                .distinct()
                .count();

        assertEquals(CustomSpellAction.values().length, distinctIds);
        for (CustomSpellAction action : CustomSpellAction.values()) {
            assertTrue(action.id().startsWith("mathmod:"));
            assertEquals(action, CustomSpellAction.fromPersistentId(action.persistentId()).orElseThrow());
            assertEquals(action, CustomSpellAction.fromPersistentId(action.name()).orElseThrow());
        }
    }
}
