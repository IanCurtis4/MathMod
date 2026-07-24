package com.mathmod.assets;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KubeJsManuscriptDocumentationTest {
    private static final Path EXAMPLE = Path.of("docs/examples/kubejs/mathmod_manuscripts.js");
    private static final Path EN_ENTRY = Path.of(
            "src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/kubejs.json"
    );
    private static final Path PT_ENTRY = Path.of(
            "src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/kubejs.json"
    );

    @Test
    void sampleUsesOnlyTheDeclarativeManuscriptSurface() throws Exception {
        String script = Files.readString(EXAMPLE);
        assertTrue(script.contains("MathMod.tradition"));
        assertTrue(script.contains("MathMod.manuscript"));
        assertTrue(script.contains("MathMod.manuscriptAlias"));
        assertTrue(script.contains(".schemaVersion(1)"));
        assertTrue(script.contains(".page("));
        assertTrue(script.contains(".register()"));
        for (String forbidden : new String[]{"callback", "executor", "grant", ".onCast(", "ServerPlayer", "loot"}) {
            assertFalse(script.contains(forbidden), "Forbidden runtime surface: " + forbidden);
        }
    }

    @Test
    void bilingualPatchouliEntryExplainsTheSameSevenPageContract() throws Exception {
        assertEquals(7, pageCount(EN_ENTRY));
        assertEquals(7, pageCount(PT_ENTRY));
        assertTrue(Files.readString(EN_ENTRY).contains("manuscriptAlias"));
        assertTrue(Files.readString(PT_ENTRY).contains("manuscriptAlias"));
    }

    private static int pageCount(Path path) throws Exception {
        return JsonParser.parseString(Files.readString(path)).getAsJsonObject()
                .getAsJsonArray("pages").size();
    }
}
