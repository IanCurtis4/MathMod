package com.mathmod.acquisition;

import com.google.gson.JsonParser;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcquisitionCodecsTest {
    @Test
    void decodesResourceIdAndBoundedTradePolicy() {
        ManuscriptAcquisitionDefinition definition = AcquisitionCodecs.decode(
                id("village_record"),
                JsonParser.parseString("""
                        {
                          "schema_version": 1,
                          "manuscript": "mathmod:rotated_horizon",
                          "loot_pools": ["mathmod:village_cartographer_chest"],
                          "loot_weight": 5,
                          "trade": {
                            "level": 2,
                            "emerald_cost": 8,
                            "max_uses": 2,
                            "villager_xp": 10,
                            "weight": 4
                          }
                        }
                        """)
        ).result().orElseThrow();

        assertEquals(id("village_record"), definition.id());
        assertEquals(id("rotated_horizon"), definition.manuscriptId());
        assertTrue(definition.trade().orElseThrow().requiresBook());
    }

    @Test
    void rejectsEmptySourcesAndOutOfRangeTradeValues() {
        assertTrue(AcquisitionCodecs.decode(id("empty"), JsonParser.parseString("""
                {"schema_version": 1, "manuscript": "mathmod:record"}
                """)).error().isPresent());
        assertTrue(AcquisitionCodecs.decode(id("expensive"), JsonParser.parseString("""
                {
                  "schema_version": 1,
                  "manuscript": "mathmod:record",
                  "trade": {"level": 2, "emerald_cost": 25, "max_uses": 1, "villager_xp": 5, "weight": 1}
                }
                """)).error().isPresent());
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
