package com.mathmod.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgressionAssetTest {
    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path ASSETS = RESOURCES.resolve("assets/mathmod");

    @Test
    void cartographerModifierUsesTheValidatedDynamicPool() throws Exception {
        JsonObject global = json("data/neoforge/loot_modifiers/global_loot_modifiers.json");
        assertTrue(global.getAsJsonArray("entries").asList().stream()
                .anyMatch(element -> element.getAsString()
                        .equals("mathmod:village_cartographer_discovery")));

        JsonObject modifier = json("data/mathmod/loot_modifiers/village_cartographer_discovery.json");
        assertEquals("mathmod:manuscript_loot", modifier.get("type").getAsString());
        assertEquals("mathmod:village_cartographer", modifier.get("pool").getAsString());
        assertEquals(
                "minecraft:chests/village/village_cartographer",
                modifier.getAsJsonArray("conditions")
                        .get(0)
                        .getAsJsonObject()
                        .get("loot_table_id")
                        .getAsString()
        );

        assertTrue(!Files.exists(
                RESOURCES.resolve("data/mathmod/loot_table/chests/village_discoveries.json")
        ));
    }

    @Test
    void progressionEntriesAndPresentationAreBilingual() throws Exception {
        for (String locale : Set.of("en_us", "pt_br")) {
            JsonObject language = JsonParser.parseString(Files.readString(
                    ASSETS.resolve("lang/" + locale + ".json")
            )).getAsJsonObject();
            for (String key : Set.of(
                    "item.mathmod.field_manuscript",
                    "item.mathmod.field_manuscript.named",
                    "item.mathmod.field_ledger",
                    "block.mathmod.demonstration_table",
                    "entity.minecraft.villager.mathmod.mathemagician",
                    "screen.mathmod.field_ledger",
                    "screen.mathmod.field_ledger.tab.epiphanies",
                    "screen.mathmod.field_ledger.tab.discoveries",
                    "epiphany.mathmod.harmonic_motion.title",
                    "epiphany.mathmod.vital_correspondence.title",
                    "epiphany.mathmod.conserved_remainder.title",
                    "discovery.mathmod.rotated_horizon.title",
                    "discovery.mathmod.bound_measure.title",
                    "discovery.mathmod.ledger_of_remainders.title",
                    "tradition.mathmod.horizon_measurers.name",
                    "tradition.mathmod.horizon_measurers.summary",
                    "tradition.mathmod.compounders_of_correspondence.name",
                    "tradition.mathmod.compounders_of_correspondence.summary",
                    "tradition.mathmod.keepers_of_remainder.name",
                    "tradition.mathmod.keepers_of_remainder.summary",
                    "tradition.mathmod.gatherers_of_means.name",
                    "tradition.mathmod.gatherers_of_means.summary",
                    "manuscript.mathmod.rotated_horizon.title",
                    "manuscript.mathmod.rotated_horizon.page.1",
                    "manuscript.mathmod.bound_measure.title",
                    "manuscript.mathmod.bound_measure.page.1",
                    "manuscript.mathmod.ledger_of_remainders.title",
                    "manuscript.mathmod.ledger_of_remainders.page.1",
                    "manuscript.mathmod.weighted_gathering.title",
                    "manuscript.mathmod.weighted_gathering.page.1",
                    "knowledge.mathmod.study.progress",
                    "knowledge.mathmod.epiphany.complete",
                    "knowledge.mathmod.route.harmonic_motion",
                    "knowledge.mathmod.route.vital_correspondence",
                    "knowledge.mathmod.route.conserved_remainder",
                    "knowledge.mathmod.route.rotated_horizon",
                    "knowledge.mathmod.route.bound_measure",
                    "knowledge.mathmod.route.ledger_of_remainders",
                    "screen.mathmod.rune_programmer.conjecture_locked",
                    "screen.mathmod.rune_programmer.custom_locked_hint"
            )) {
                assertTrue(language.has(key), "Missing " + locale + " progression key " + key);
                assertTrue(!language.get(key).getAsString().isBlank(),
                        "Blank " + locale + " progression key " + key);
            }

            assertEntry(locale, "basics/field_ledger", 4);
            assertEntry(locale, "lore/rotated_horizon", 2);
            assertEntry(locale, "lore/bound_measure", 2);
            assertEntry(locale, "lore/ledger_of_remainders", 2);
            assertEntry(locale, "lore/weighted_gathering", 2);
            assertEntry(locale, "lore/cartographer_chests", 2);
        }
    }

    @Test
    void fieldManuscriptHasAnItemModel() {
        assertTrue(Files.isRegularFile(ASSETS.resolve("models/item/field_manuscript.json")));
        assertTrue(Files.isRegularFile(ASSETS.resolve("models/item/field_ledger.json")));
        assertTrue(Files.isRegularFile(ASSETS.resolve("models/item/demonstration_table.json")));
        assertTrue(Files.isRegularFile(ASSETS.resolve("models/block/demonstration_table.json")));
        assertTrue(Files.isRegularFile(ASSETS.resolve("blockstates/demonstration_table.json")));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/recipe/field_ledger.json")
        ));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/recipe/demonstration_table.json")
        ));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/worldgen/structure/mathemagician_house.json")
        ));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/worldgen/structure_set/mathemagician_house.json")
        ));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/loot_table/chests/mathemagician_house.json")
        ));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/mathmod/epiphanies/harmonic_motion.json")
        ));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/mathmod/discoveries/rotated_horizon.json")
        ));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/mathmod/epiphanies/vital_correspondence.json")
        ));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/mathmod/epiphanies/conserved_remainder.json")
        ));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/mathmod/discoveries/bound_measure.json")
        ));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("data/mathmod/mathmod/discoveries/ledger_of_remainders.json")
        ));
        for (String acquisition : Set.of(
                "rotated_horizon", "bound_measure", "ledger_of_remainders", "weighted_gathering"
        )) {
            assertTrue(Files.isRegularFile(RESOURCES.resolve(
                    "data/mathmod/mathmod/manuscript_acquisition/" + acquisition + ".json"
            )));
        }
    }

    private static void assertEntry(String locale, String id, int pages) throws Exception {
        Path path = ASSETS.resolve(
                "patchouli_books/field_manual/" + locale + "/entries/" + id + ".json"
        );
        assertTrue(Files.isRegularFile(path), "Missing " + locale + " entry " + id);
        assertEquals(pages, JsonParser.parseString(Files.readString(path))
                .getAsJsonObject()
                .getAsJsonArray("pages")
                .size());
    }

    private static JsonObject json(String path) throws Exception {
        return JsonParser.parseString(Files.readString(RESOURCES.resolve(path))).getAsJsonObject();
    }
}
