package com.mathmod.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TalismanUseCopyTest {
    private static final Path LANGUAGE_ROOT = Path.of("src/main/resources/assets/mathmod/lang");

    @Test
    void tooltipPresentsStateAndWorldActionsAsSeparateScannableLines() throws Exception {
        Map<String, LocaleCopy> copies = Map.of(
                "en_us", new LocaleCopy("No proof inscribed.", "Use:", "Sneak + Use:", "witness"),
                "pt_br", new LocaleCopy("Nenhuma prova inscrita.", "Usar:", "Agachar + usar:", "testemunh")
        );

        for (var locale : copies.entrySet()) {
            JsonObject language = JsonParser.parseString(Files.readString(
                    LANGUAGE_ROOT.resolve(locale.getKey() + ".json")
            )).getAsJsonObject();
            LocaleCopy copy = locale.getValue();
            String empty = language.get("item.mathmod.programmed_talisman.tooltip.empty").getAsString();
            String programmer = language.get(
                    "item.mathmod.programmed_talisman.tooltip.action.programmer"
            ).getAsString();
            String cast = language.get(
                    "item.mathmod.programmed_talisman.tooltip.action.cast"
            ).getAsString();
            String resources = language.get(
                    "item.mathmod.programmed_talisman.tooltip.action.resources"
            ).getAsString();

            assertEquals(copy.emptyState(), empty);
            assertTrue(programmer.startsWith(copy.primaryPrefix()));
            assertTrue(cast.startsWith(copy.primaryPrefix()));
            assertTrue(resources.startsWith(copy.secondaryPrefix()));
            assertTrue(cast.toLowerCase().contains(copy.witnessStem()));
            assertTrue(resources.toLowerCase().contains(copy.witnessStem()));
        }
    }

    @Test
    void failedCastMessagesLeadBackToResourcePreparation() throws Exception {
        for (String locale : new String[]{"en_us", "pt_br"}) {
            JsonObject language = JsonParser.parseString(Files.readString(
                    LANGUAGE_ROOT.resolve(locale + ".json")
            )).getAsJsonObject();
            for (String key : new String[]{
                    "item.mathmod.programmed_talisman.execute_missing_items",
                    "item.mathmod.programmed_talisman.execute_missing_attributes",
                    "item.mathmod.programmed_talisman.execute_missing_budget"
            }) {
                String message = language.get(key).getAsString().toLowerCase();
                assertTrue(
                        locale.equals("pt_br")
                                ? message.contains("agache") && message.contains("recurso")
                                : message.contains("sneak-use") && message.contains("resource"),
                        () -> key + " must explain how to recover in " + locale
                );
            }
        }
    }

    private record LocaleCopy(
            String emptyState,
            String primaryPrefix,
            String secondaryPrefix,
            String witnessStem
    ) {
    }
}
