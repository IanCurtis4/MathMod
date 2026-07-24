package com.mathmod.client;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatchouliPreviewMatrixTest {
    private static final Path ENTRY_ROOT = Path.of(
            "src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries"
    );

    @Test
    void matrixCoversEveryPortugueseEntrySpreadExactlyOnce() throws Exception {
        Set<String> expected = new LinkedHashSet<>();
        try (Stream<Path> files = Files.walk(ENTRY_ROOT)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".json")).sorted().toList()) {
                String entryId = ENTRY_ROOT.relativize(file)
                        .toString()
                        .replace('\\', '/')
                        .replaceFirst("\\.json$", "");
                int pageCount = JsonParser.parseString(Files.readString(file))
                        .getAsJsonObject()
                        .getAsJsonArray("pages")
                        .size();
                for (int page = 0; page < pageCount; page += 2) {
                    expected.add(entryId + "#" + page);
                }
            }
        }

        Set<String> actual = new LinkedHashSet<>();
        Set<String> screenshotIds = new HashSet<>();
        for (PatchouliPreviewMatrix.Target target : PatchouliPreviewMatrix.targets()) {
            assertTrue(actual.add(target.entryId() + "#" + target.page()),
                    "Duplicate Patchouli spread " + target);
            assertTrue(screenshotIds.add(target.screenshotId()),
                    "Duplicate Patchouli screenshot id " + target.screenshotId());
        }

        assertEquals(expected, actual);
    }
}
