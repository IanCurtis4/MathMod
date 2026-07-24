package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ManuscriptDefinitionTest {
    @Test
    void enforcesSchemaPageAndTranslationLimits() {
        assertThrows(IllegalArgumentException.class, () -> manuscript(2, List.of("page.one")));
        assertThrows(IllegalArgumentException.class, () -> manuscript(1, List.of()));
        assertThrows(IllegalArgumentException.class, () -> manuscript(
                1,
                List.of("1", "2", "3", "4", "5", "6", "7", "8", "9")
        ));
        assertThrows(IllegalArgumentException.class, () -> manuscript(1, List.of(" ")));
    }

    @Test
    void rarityParsingIsCaseInsensitiveAndBounded() {
        assertEquals(ManuscriptRarity.RARE, ManuscriptRarity.parse("rare").orElseThrow());
        assertEquals(Optional.empty(), ManuscriptRarity.parse("legendary"));
        assertEquals(Optional.empty(), ManuscriptRarity.parse(null));
    }

    private static ManuscriptDefinition manuscript(int schema, List<String> pages) {
        return new ManuscriptDefinition(
                schema,
                id("record"),
                id("tradition"),
                "manuscript.record.title",
                pages,
                id("paper"),
                ManuscriptRarity.COMMON,
                Optional.empty(),
                Optional.empty()
        );
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
