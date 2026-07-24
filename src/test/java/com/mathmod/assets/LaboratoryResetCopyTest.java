package com.mathmod.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LaboratoryResetCopyTest {
    private static final Path LANGUAGE_ROOT = Path.of("src/main/resources/assets/mathmod/lang");

    @Test
    void resetCopyNamesScopePreservationAndSecondActivation() throws Exception {
        Map<String, ExpectedCopy> expected = Map.of(
                "en_us", new ExpectedCopy("twice", "again", "spell name", "inscription"),
                "pt_br", new ExpectedCopy("duas vezes", "novamente", "nome da magia", "inscrição")
        );

        for (var locale : expected.entrySet()) {
            JsonObject language = JsonParser.parseString(Files.readString(
                    LANGUAGE_ROOT.resolve(locale.getKey() + ".json")
            )).getAsJsonObject();
            ExpectedCopy copy = locale.getValue();
            String initialHint = value(language, "screen.mathmod.rune_programmer.reset_custom_hint");
            String confirmation = value(language, "screen.mathmod.rune_programmer.reset_custom_confirm");
            String confirmationHint = value(
                    language,
                    "screen.mathmod.rune_programmer.reset_custom_confirm_hint"
            );
            String emptyHint = value(language, "screen.mathmod.rune_programmer.reset_custom_empty_hint");

            assertTrue(initialHint.contains(copy.twiceCue()));
            assertTrue(initialHint.contains(copy.nameCue()));
            assertTrue(initialHint.contains(copy.inscriptionCue()));
            assertFalse(confirmation.isBlank());
            assertTrue(confirmationHint.contains(copy.againCue()));
            assertTrue(confirmationHint.contains(copy.nameCue()));
            assertTrue(confirmationHint.contains(copy.inscriptionCue()));
            assertFalse(emptyHint.isBlank());
        }
    }

    private static String value(JsonObject language, String key) {
        return language.get(key).getAsString().toLowerCase();
    }

    private record ExpectedCopy(
            String twiceCue,
            String againCue,
            String nameCue,
            String inscriptionCue
    ) {
    }
}
