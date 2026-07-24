package com.mathmod.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RuneAnchorGuidanceCopyTest {
    private static final Path LANGUAGE_ROOT = Path.of("src/main/resources/assets/mathmod/lang");

    @Test
    void bothLocalesExposeTheCompletePhysicalActionHierarchy() throws Exception {
        Map<String, ExpectedCopy> locales = Map.of(
                "en_us",
                new ExpectedCopy("theorem", "inscription", "effect", "Rune Chalk", "Empty Hand"),
                "pt_br",
                new ExpectedCopy("teorema", "inscri\u00e7\u00e3o", "efeito", "Giz r\u00fanico", "M\u00e3o vazia")
        );

        for (var locale : locales.entrySet()) {
            JsonObject language = JsonParser.parseString(Files.readString(
                    LANGUAGE_ROOT.resolve(locale.getKey() + ".json")
            )).getAsJsonObject();
            ExpectedCopy expected = locale.getValue();
            String role = value(language, "item.mathmod.rune_anchor.tooltip.role");

            assertTrue(role.contains(expected.theorem()));
            assertTrue(role.contains(expected.inscription()));
            assertTrue(role.contains(expected.effect()));
            assertTrue(value(language, "item.mathmod.rune_anchor.tooltip.action.inscribe")
                    .startsWith(expected.chalk()));
            assertTrue(value(language, "item.mathmod.rune_anchor.tooltip.action.enact")
                    .startsWith(expected.emptyHand()));
            assertTrue(value(language, "item.mathmod.rune_anchor.tooltip.action.inspect")
                    .startsWith(expected.emptyHand()));
            assertTrue(value(language, "item.mathmod.rune_anchor.tooltip.action.erase")
                    .startsWith(expected.chalk()));
        }
    }

    private static String value(JsonObject language, String key) {
        assertTrue(language.has(key), () -> "Missing translation key " + key);
        return language.get(key).getAsString();
    }

    private record ExpectedCopy(
            String theorem,
            String inscription,
            String effect,
            String chalk,
            String emptyHand
    ) {
    }
}
