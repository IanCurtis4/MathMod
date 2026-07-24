package com.mathmod.manuscript;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManuscriptNarrativeContractTest {
    private static final Path DATA = Path.of("src/main/resources/data/mathmod/mathmod");
    private static final Path MANUAL = Path.of("src/main/resources/assets/mathmod/patchouli_books/field_manual");

    @Test
    void playableManuscriptsAgreeWithTheirDiscoveryAndTheoremRecords() throws Exception {
        Map<String, JsonObject> discoveries = records(DATA.resolve("discoveries"));
        for (Map.Entry<String, JsonObject> entry : records(DATA.resolve("manuscripts")).entrySet()) {
            JsonObject manuscript = entry.getValue();
            if (!manuscript.has("theorem")) {
                continue;
            }
            JsonObject discovery = discoveries.get(entry.getKey());
            assertTrue(discovery != null, "Playable manuscript needs a discovery: " + entry.getKey());
            assertEquals("mathmod:" + entry.getKey(), discovery.get("manuscript").getAsString());
            assertEquals(manuscript.get("title_key").getAsString(), discovery.get("title_key").getAsString());
            assertEquals(manuscript.get("patchouli_entry").getAsString(), discovery.get("patchouli_entry").getAsString());
            assertTrue(grants(discovery).contains(manuscript.get("theorem").getAsString()),
                    "Discovery must grant its manuscript theorem: " + entry.getKey());
        }
    }

    @Test
    void conjecturalManuscriptsRemainExplicitlyNonPlayableInBothManualLanguages() throws Exception {
        JsonObject weighted = records(DATA.resolve("manuscripts")).get("weighted_gathering");
        assertFalse(weighted.has("theorem"));
        assertFalse(records(DATA.resolve("discoveries")).containsKey("weighted_gathering"));
        assertConjecture("en_us", "conjecture");
        assertConjecture("pt_br", "conjectura");
    }

    private static void assertConjecture(String locale, String expectedWord) throws Exception {
        JsonObject entry = json(MANUAL.resolve(locale).resolve("entries/lore/weighted_gathering.json"));
        String text = entry.getAsJsonArray("pages").asList().stream()
                .map(page -> page.getAsJsonObject().get("text").getAsString())
                .reduce("", (left, right) -> left + " " + right)
                .toLowerCase(java.util.Locale.ROOT);
        assertTrue(text.contains(expectedWord), locale + " must label Weighted Gathering as a conjecture");
        assertTrue(text.contains(locale.equals("en_us") ? "cannot be cast" : "n\u00e3o pode ser conjurado"),
                locale + " must state that the record is not castable");
    }

    private static Map<String, JsonObject> records(Path directory) throws Exception {
        Map<String, JsonObject> records = new HashMap<>();
        try (var files = Files.list(directory)) {
            for (Path path : files.filter(file -> file.toString().endsWith(".json")).toList()) {
                String name = path.getFileName().toString().replaceFirst("\\.json$", "");
                records.put(name, json(path));
            }
        }
        return records;
    }

    private static java.util.Set<String> grants(JsonObject discovery) {
        JsonArray grants = discovery.getAsJsonArray("grants");
        java.util.Set<String> ids = new java.util.HashSet<>();
        for (var grant : grants) {
            ids.add(grant.getAsJsonObject().get("id").getAsString());
        }
        return ids;
    }

    private static JsonObject json(Path path) throws Exception {
        return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
    }
}
