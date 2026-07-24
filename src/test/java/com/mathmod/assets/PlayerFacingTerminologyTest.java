package com.mathmod.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerFacingTerminologyTest {
    private static final Path ASSET_ROOT = Path.of("src/main/resources/assets/mathmod");
    private static final String[] PROOF_SURFACES = {
            "item.mathmod.programmed_talisman.empty",
            "item.mathmod.programmed_talisman.tooltip.empty",
            "item.mathmod.programmed_talisman.tooltip.program",
            "item.mathmod.programmed_talisman.execute_invalid",
            "screen.mathmod.rune_programmer.empty",
            "screen.mathmod.rune_programmer.custom_empty",
            "screen.mathmod.rune_programmer.clear_hint",
            "screen.mathmod.rune_programmer.resources_inscribed_hint",
            "screen.mathmod.rune_programmer.resources_preview_hint",
            "screen.mathmod.talisman_resources.empty"
    };

    @Test
    void firstContactCallsTheValidatedConstructionAProof() throws Exception {
        Map<String, LocaleCopy> copies = Map.of(
                "en_us", new LocaleCopy("proof", Pattern.compile("(?i)\\bprogram\\b"), "spell"),
                "pt_br", new LocaleCopy("prova", Pattern.compile("(?i)\\bprograma\\b"), "magia")
        );

        for (var locale : copies.entrySet()) {
            JsonObject language = readLanguage(locale.getKey());
            LocaleCopy copy = locale.getValue();

            for (String key : PROOF_SURFACES) {
                String value = language.get(key).getAsString();
                assertTrue(
                        value.toLowerCase().contains(copy.proofWord()),
                        () -> key + " must name the player's proof in " + locale.getKey()
                );
                assertFalse(
                        copy.technicalProgramWord().matcher(value).find(),
                        () -> key + " must not expose the implementation term in " + locale.getKey()
                );
            }

            String spellName = language.get(
                    "item.mathmod.programmed_talisman.tooltip.name"
            ).getAsString().toLowerCase();
            assertTrue(spellName.contains(copy.spellWord()));
        }
    }

    @Test
    void firstSpellGuideUsesTheSameInscribedProofTerm() throws Exception {
        Map<String, String> expectedPhrases = Map.of(
                "en_us", "casts the inscribed proof",
                "pt_br", "conjura a prova inscrita"
        );

        for (var locale : expectedPhrases.entrySet()) {
            JsonObject entry = JsonParser.parseString(Files.readString(
                    ASSET_ROOT.resolve(
                            "patchouli_books/field_manual/" + locale.getKey()
                                    + "/entries/basics/can_i_make_spell.json"
                    )
            )).getAsJsonObject();
            String firstPage = entry.getAsJsonArray("pages")
                    .get(0)
                    .getAsJsonObject()
                    .get("text")
                    .getAsString()
                    .toLowerCase();
            assertTrue(firstPage.contains(locale.getValue()));
            assertFalse(firstPage.contains("inscribed program"));
            assertFalse(firstPage.contains("programa inscrito"));
        }
    }

    @Test
    void thirdProgrammerTabNamesThePhysicalTalismanInsteadOfAnAbstractSavedLibrary() throws Exception {
        Map<String, String> expectedNames = Map.of(
                "en_us", "Talisman",
                "pt_br", "Talismã"
        );

        for (var locale : expectedNames.entrySet()) {
            JsonObject language = readLanguage(locale.getKey());
            assertTrue(language.get("screen.mathmod.rune_programmer.tab_saved")
                    .getAsString()
                    .equals(locale.getValue()));
            String lore = language.get("screen.mathmod.rune_programmer.tab_saved_lore")
                    .getAsString()
                    .toLowerCase();
            assertTrue(lore.contains(locale.getValue().toLowerCase()));
            assertFalse(lore.contains("notebook"));
            assertFalse(lore.contains("caderno"));
        }
    }

    private static JsonObject readLanguage(String locale) throws Exception {
        return JsonParser.parseString(Files.readString(
                ASSET_ROOT.resolve("lang/" + locale + ".json")
        )).getAsJsonObject();
    }

    private record LocaleCopy(
            String proofWord,
            Pattern technicalProgramWord,
            String spellWord
    ) {
    }
}
