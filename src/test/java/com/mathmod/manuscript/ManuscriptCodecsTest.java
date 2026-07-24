package com.mathmod.manuscript;

import com.google.gson.JsonParser;
import com.mathmod.util.NamespacedId;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManuscriptCodecsTest {
    @Test
    void decodesDefinitionIdsFromTheirResourcePathRatherThanJsonFields() {
        TraditionDefinition tradition = ManuscriptCodecs.decodeTradition(
                id("horizon_measurers"),
                JsonParser.parseString("""
                        {
                          "schema_version": 1,
                          "name_key": "tradition.mathmod.horizon.name",
                          "summary_key": "tradition.mathmod.horizon.summary",
                          "icon": "minecraft:compass"
                        }
                        """)
        ).result().orElseThrow();
        ManuscriptDefinition manuscript = ManuscriptCodecs.decodeManuscript(
                id("rotated_horizon"),
                JsonParser.parseString("""
                        {
                          "schema_version": 1,
                          "tradition": "mathmod:horizon_measurers",
                          "title_key": "manuscript.mathmod.rotated_horizon.title",
                          "page_keys": ["manuscript.mathmod.rotated_horizon.page.1"],
                          "icon": "minecraft:compass",
                          "rarity": "uncommon",
                          "theorem": "mathmod:quarter_turn"
                        }
                        """)
        ).result().orElseThrow();

        assertEquals(id("horizon_measurers"), tradition.id());
        assertEquals(id("rotated_horizon"), manuscript.id());
        assertEquals(ManuscriptRarity.UNCOMMON, manuscript.rarity());
        assertEquals(id("quarter_turn"), manuscript.theoremId().orElseThrow());
    }

    @Test
    void rejectsUnknownSchemaAndRarityAsCodecErrors() {
        assertTrue(ManuscriptCodecs.decodeTradition(
                id("future"),
                JsonParser.parseString("""
                        {"schema_version": 2, "name_key": "name", "summary_key": "summary", "icon": "minecraft:paper"}
                        """)
        ).error().isPresent());
        assertTrue(ManuscriptCodecs.decodeManuscript(
                id("unknown_rarity"),
                JsonParser.parseString("""
                        {
                          "schema_version": 1,
                          "tradition": "mathmod:surveyors",
                          "title_key": "title",
                          "page_keys": ["page"],
                          "icon": "minecraft:paper",
                          "rarity": "legendary"
                        }
                        """)
        ).error().isPresent());
    }

    @Test
    void aliasCodecRejectsSelfAliases() {
        assertTrue(ManuscriptCodecs.ALIAS.parse(
                JsonOps.INSTANCE,
                JsonParser.parseString("""
                        {"schema_version": 1, "from": "mathmod:record", "to": "mathmod:record"}
                        """)
        ).error().isPresent());
    }

    @Test
    void recordDataCannotCarryGameplayFields() {
        List<String> components = List.of(ManuscriptDefinition.class.getRecordComponents())
                .stream()
                .map(component -> component.getName())
                .toList();

        assertFalse(components.contains("grants"));
        assertFalse(components.contains("executor"));
        assertFalse(components.contains("callback"));
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
