package com.mathmod.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NamespacedIdTest {
    @Test
    void parsesAndNormalizesNamespacedIds() {
        NamespacedId id = NamespacedId.parse("MathMod:Path/Value");

        assertEquals("mathmod", id.namespace());
        assertEquals("path/value", id.path());
        assertEquals("mathmod:path/value", id.toString());
    }

    @Test
    void rejectsMissingNamespaceAndInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> NamespacedId.parse("hop"));
        assertThrows(IllegalArgumentException.class, () -> NamespacedId.parse("mathmod:bad value"));
        assertTrue(NamespacedId.tryParse(null).isEmpty());
    }
}
