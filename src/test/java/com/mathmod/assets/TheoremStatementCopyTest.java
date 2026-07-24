package com.mathmod.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TheoremStatementCopyTest {
    private static final Path LANGUAGE_ROOT = Path.of("src/main/resources/assets/mathmod/lang");

    @Test
    void bothLocalesSeparateTheCompactStatementFromTheTypedProof() throws Exception {
        Map<String, List<String>> requiredPhrases = Map.of(
                "en_us", List.of(
                        "compact statement",
                        "not editable source",
                        "numbered runes",
                        "complete typed proof"
                ),
                "pt_br", List.of(
                        "enunciado compacto",
                        "não é código editável",
                        "runas numeradas",
                        "prova tipada completa"
                )
        );

        for (var locale : requiredPhrases.entrySet()) {
            JsonObject language = JsonParser.parseString(Files.readString(
                    LANGUAGE_ROOT.resolve(locale.getKey() + ".json")
            )).getAsJsonObject();
            String copy = (
                    language.get("screen.mathmod.rune_programmer.theorem_formula_hint_first")
                            .getAsString()
                            + " "
                            + language.get("screen.mathmod.rune_programmer.theorem_formula_hint_second")
                            .getAsString()
            ).toLowerCase();

            for (String phrase : locale.getValue()) {
                assertTrue(
                        copy.contains(phrase),
                        () -> locale.getKey() + " theorem statement guidance must contain " + phrase
                );
            }
        }
    }
}
