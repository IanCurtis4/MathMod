package com.mathmod.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourcePreparationCopyTest {
    private static final Path ASSET_ROOT = Path.of("src/main/resources/assets/mathmod");

    @Test
    void recommendedAndPlayerChosenMaterialsShareTruthfulPreparationCopy() throws Exception {
        Map<String, LocaleCopy> copies = Map.of(
                "en_us", new LocaleCopy("Prepared Materials", "prepar", "Added Materials"),
                "pt_br", new LocaleCopy("Materiais preparados", "prepar", "Materiais adicionados")
        );

        for (var locale : copies.entrySet()) {
            JsonObject language = JsonParser.parseString(Files.readString(
                    ASSET_ROOT.resolve("lang/" + locale.getKey() + ".json")
            )).getAsJsonObject();
            LocaleCopy copy = locale.getValue();

            assertEquals(
                    copy.heading(),
                    language.get("screen.mathmod.talisman_resources.selected").getAsString()
            );
            for (String key : new String[]{
                    "screen.mathmod.talisman_resources.clear_resources_hint",
                    "screen.mathmod.talisman_resources.clear_resources_confirm_hint",
                    "screen.mathmod.talisman_resources.clear_resources_empty_hint",
                    "screen.mathmod.talisman_resources.notation.sum",
                    "screen.mathmod.talisman_resources.hint",
                    "screen.mathmod.talisman_resources.keyboard_added"
            }) {
                String value = language.get(key).getAsString();
                assertTrue(
                        value.toLowerCase().contains(copy.preparationStem()),
                        () -> key + " must describe preparation in " + locale.getKey()
                );
                assertFalse(
                        value.contains(copy.retiredHeading()),
                        () -> key + " attributes automatic recommendations to the player in " + locale.getKey()
                );
            }
            assertFalse(
                    language.get("screen.mathmod.talisman_resources.clear_resources_confirm")
                            .getAsString()
                            .isBlank(),
                    () -> "Resource-clear confirmation label is missing in " + locale.getKey()
            );
            String confirmationHint = language.get(
                    "screen.mathmod.talisman_resources.clear_resources_confirm_hint"
            ).getAsString().toLowerCase();
            assertTrue(
                    confirmationHint.contains(locale.getKey().equals("en_us") ? "again" : "novamente"),
                    () -> "Resource-clear confirmation must require a second activation in " + locale.getKey()
            );

            String manual = Files.readString(
                    ASSET_ROOT.resolve(
                            "patchouli_books/field_manual/" + locale.getKey()
                                    + "/entries/programming/resource_costs.json"
                    )
            );
            assertTrue(manual.toLowerCase().contains(copy.preparationStem()));
            assertFalse(manual.contains(copy.retiredHeading()));
        }
    }

    private record LocaleCopy(
            String heading,
            String preparationStem,
            String retiredHeading
    ) {
    }
}
