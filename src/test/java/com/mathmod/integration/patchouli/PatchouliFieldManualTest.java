package com.mathmod.integration.patchouli;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatchouliFieldManualTest {
    private static final Path BOOK_ROOT = Path.of(
            "src/main/resources/assets/mathmod/patchouli_books/field_manual"
    );
    private static final Path BOOK_METADATA = Path.of(
            "src/main/resources/data/mathmod/patchouli_books/field_manual/book.json"
    );
    private static final Path LANG_ROOT = Path.of("src/main/resources/assets/mathmod/lang");

    @Test
    void contextualHelpTargetsExistingBilingualPages() throws Exception {
        assertEquals("mathmod:field_manual", FieldManualTarget.bookId());
        assertTargetExistsInBothLanguages(
                FieldManualTarget.FIRST_SPELL_ENTRY_PATH,
                FieldManualTarget.FIRST_SPELL_PAGE
        );
        assertTargetExistsInBothLanguages(
                FieldManualTarget.RESOURCE_COSTS_ENTRY_PATH,
                FieldManualTarget.RESOURCE_COSTS_PAGE
        );
        assertTargetExistsInBothLanguages(
                FieldManualTarget.ROTATED_HORIZON_ENTRY_PATH,
                FieldManualTarget.ROTATED_HORIZON_PAGE
        );
        assertTargetExistsInBothLanguages("lore/bound_measure", 0);
        assertTargetExistsInBothLanguages("lore/ledger_of_remainders", 0);
    }

    @Test
    void landingPageUsesLocalizedIdentityAndLinksToFirstSpell() throws Exception {
        var book = JsonParser.parseString(Files.readString(BOOK_METADATA)).getAsJsonObject();
        assertEquals("0", book.get("version").getAsString());
        assertFalse(book.has("i18n"), "Global i18n would treat every literal entry title as a lang key");

        Map<String, String> fields = Map.of(
                "name", "book.mathmod.field_manual.name",
                "subtitle", "book.mathmod.field_manual.subtitle",
                "landing_text", "book.mathmod.field_manual.landing"
        );
        for (var field : fields.entrySet()) {
            assertEquals(field.getValue(), book.get(field.getKey()).getAsString());
        }

        for (String locale : new String[]{"en_us", "pt_br"}) {
            var lang = JsonParser.parseString(Files.readString(LANG_ROOT.resolve(locale + ".json")))
                    .getAsJsonObject();
            for (String key : fields.values()) {
                assertTrue(lang.has(key), "Missing " + locale + " book translation " + key);
                assertFalse(lang.get(key).getAsString().isBlank(), "Blank " + locale + " book translation " + key);
            }
            assertTrue(
                    lang.get(fields.get("landing_text")).getAsString()
                            .contains("$(l:" + FieldManualTarget.FIRST_SPELL_ENTRY_PATH + ")"),
                    "Landing page must link to the first-spell entry in " + locale
            );
        }

        assertTargetExistsInBothLanguages(FieldManualTarget.FIRST_SPELL_ENTRY_PATH, 0);
    }

    @Test
    void firstSpellKeepsAnInWorldMathemagicianVoice() throws Exception {
        Map<String, String> identityTerms = Map.of(
                "en_us", "mathemagicians",
                "pt_br", "matem\u00e1gicos"
        );
        for (var locale : identityTerms.entrySet()) {
            Path entry = BOOK_ROOT
                    .resolve(locale.getKey())
                    .resolve("entries")
                    .resolve(FieldManualTarget.FIRST_SPELL_ENTRY_PATH + ".json");
            String text = Files.readString(entry).toLowerCase();

            assertTrue(text.contains(locale.getValue()), "Missing in-world identity in " + locale.getKey());
            assertFalse(text.contains("psi-like"), "Developer-facing editor comparison leaked into the manual");
            assertFalse(text.contains("\"text\": \"the first editor"), "Implementation narration leaked into the manual");
            assertFalse(text.contains("uma gui consegue"), "Implementation narration leaked into the PT-BR manual");
        }
    }

    private static void assertTargetExistsInBothLanguages(String entryPath, int page) throws Exception {
        for (String locale : new String[]{"en_us", "pt_br"}) {
            Path entry = BOOK_ROOT
                    .resolve(locale)
                    .resolve("entries")
                    .resolve(entryPath + ".json");
            assertTrue(Files.isRegularFile(entry), "Missing " + locale + " manual entry " + entryPath);
            int pageCount = JsonParser.parseString(Files.readString(entry))
                    .getAsJsonObject()
                    .getAsJsonArray("pages")
                    .size();
            assertTrue(
                    page < pageCount,
                    "Help page is outside the " + locale + " entry " + entryPath
            );
        }
    }
}
