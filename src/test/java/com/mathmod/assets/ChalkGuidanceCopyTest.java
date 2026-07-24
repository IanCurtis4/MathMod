package com.mathmod.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChalkGuidanceCopyTest {
    private static final Path LANGUAGE_ROOT = Path.of("src/main/resources/assets/mathmod/lang");
    private static final Path BOOK_ROOT = Path.of(
            "src/main/resources/assets/mathmod/patchouli_books/field_manual"
    );
    private static final List<String> ANCHOR_KEYS = List.of(
            "item.mathmod.chalk.mode_changed",
            "item.mathmod.chalk.tooltip.mode",
            "item.mathmod.chalk.tooltip.action.cycle",
            "item.mathmod.chalk.tooltip.action.inscribe",
            "item.mathmod.chalk.tooltip.action.erase",
            "item.mathmod.chalk.anchor_hint",
            "item.mathmod.chalk.anchor_saved",
            "item.mathmod.chalk.anchor_sacrifice_saved",
            "item.mathmod.chalk.anchor_offering_saved",
            "item.mathmod.chalk.anchor_ward_saved",
            "item.mathmod.chalk.anchor_cleared",
            "item.mathmod.chalk.anchor_clear_empty",
            "item.mathmod.chalk.anchor_invalid",
            "item.mathmod.rune_anchor.tooltip.role",
            "item.mathmod.rune_anchor.tooltip.action.inscribe",
            "item.mathmod.rune_anchor.tooltip.action.enact",
            "item.mathmod.rune_anchor.tooltip.action.inspect",
            "item.mathmod.rune_anchor.tooltip.action.erase",
            "block.mathmod.rune_anchor.empty",
            "block.mathmod.rune_anchor.executed",
            "block.mathmod.rune_anchor.execute_invalid",
            "block.mathmod.rune_anchor.execute_failed",
            "block.mathmod.rune_anchor.missing_sacrifice",
            "block.mathmod.rune_anchor.status",
            "block.mathmod.rune_anchor.status_empty",
            "block.mathmod.rune_anchor.preset.unknown"
    );

    @Test
    void bothLocalesDescribeTheImplementedAnchorCycle() throws Exception {
        Map<String, LocaleCopy> copies = Map.of(
                "en_us", new LocaleCopy(
                        List.of("choose", "theorem", "anchor", "inscribe", "erase"),
                        "Use",
                        "Sneak + Use",
                        Pattern.compile("(?i)\\b(?:program|preset)\\b")
                ),
                "pt_br", new LocaleCopy(
                        List.of("escolher", "teorema", "âncora", "inscrev", "apagar"),
                        "Usar",
                        "Agachar + usar",
                        Pattern.compile("(?i)\\b(?:programa|predefinição)\\b")
                )
        );

        for (var locale : copies.entrySet()) {
            JsonObject language = JsonParser.parseString(Files.readString(
                    LANGUAGE_ROOT.resolve(locale.getKey() + ".json")
            )).getAsJsonObject();
            String hint = language.get("item.mathmod.chalk.anchor_hint")
                    .getAsString()
                    .toLowerCase();

            for (String required : locale.getValue().required()) {
                assertTrue(
                        hint.contains(required),
                        () -> locale.getKey() + " chalk hint must contain " + required
                );
            }
            assertTrue(
                    language.get("item.mathmod.chalk.tooltip.action.cycle")
                            .getAsString()
                            .startsWith(locale.getValue().primaryAction()),
                    () -> locale.getKey() + " chalk cycle must be an explicit primary action"
            );
            assertTrue(
                    language.get("item.mathmod.chalk.tooltip.action.inscribe")
                            .getAsString()
                            .startsWith(locale.getValue().primaryAction()),
                    () -> locale.getKey() + " chalk inscription must be an explicit primary action"
            );
            assertTrue(
                    language.get("item.mathmod.chalk.tooltip.action.erase")
                            .getAsString()
                            .startsWith(locale.getValue().secondaryAction()),
                    () -> locale.getKey() + " chalk erasure must be an explicit secondary action"
            );
            assertTrue(
                    language.get("block.mathmod.rune_anchor.executed")
                            .getAsString()
                            .startsWith("∴ "),
                    () -> locale.getKey() + " successful anchor proof must use therefore"
            );
            for (String key : ANCHOR_KEYS) {
                String value = language.get(key).getAsString();
                assertFalse(
                        locale.getValue().technicalTerms().matcher(value).find(),
                        () -> locale.getKey() + " exposes a technical anchor term at " + key
                );
            }

            for (String entry : List.of("world_anchors.json", "current_state.json")) {
                String book = Files.readString(
                        BOOK_ROOT.resolve(locale.getKey() + "/entries/basics/" + entry)
                );
                assertFalse(
                        locale.getValue().technicalTerms().matcher(book).find(),
                        () -> locale.getKey() + " " + entry + " exposes a technical anchor term"
                );
            }
        }
    }

    private record LocaleCopy(
            List<String> required,
            String primaryAction,
            String secondaryAction,
            Pattern technicalTerms
    ) {
    }
}
