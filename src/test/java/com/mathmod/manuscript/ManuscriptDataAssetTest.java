package com.mathmod.manuscript;

import com.google.gson.JsonParser;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManuscriptDataAssetTest {
    private static final Path DATA = Path.of("src/main/resources/data/mathmod/mathmod");
    private static final ManuscriptDefinitionSource BUILT_IN = new ManuscriptDefinitionSource(
            ManuscriptSourceLayer.BUILT_IN,
            0,
            "mathmod"
    );

    @Test
    void builtInLoreRecordsDecodeIntoOneValidSnapshot() throws Exception {
        ManuscriptSnapshotBuilder builder = new ManuscriptSnapshotBuilder(theorem -> true);
        for (Path path : jsonFiles(DATA.resolve("traditions"))) {
            String localId = fileId(path);
            TraditionDefinition definition = ManuscriptCodecs.decodeTradition(
                    id(localId),
                    JsonParser.parseString(Files.readString(path))
            ).result().orElseThrow();
            builder.addTradition(definition, BUILT_IN);
        }
        for (Path path : jsonFiles(DATA.resolve("manuscripts"))) {
            String localId = fileId(path);
            ManuscriptDefinition definition = ManuscriptCodecs.decodeManuscript(
                    id(localId),
                    JsonParser.parseString(Files.readString(path))
            ).result().orElseThrow();
            builder.addManuscript(definition, BUILT_IN);
        }

        ManuscriptSnapshotBuildResult result = builder.build();
        assertTrue(result.publishable(), () -> "Diagnostics: " + result.diagnostics());
        assertEquals(4, result.snapshot().traditions().size());
        assertEquals(4, result.snapshot().manuscripts().size());
        assertEquals(
                List.of(
                        id("bound_measure"),
                        id("ledger_of_remainders"),
                        id("rotated_horizon"),
                        id("weighted_gathering")
                ),
                result.snapshot().manuscripts().stream().map(ManuscriptDefinition::id).toList()
        );
    }

    private static List<Path> jsonFiles(Path directory) throws Exception {
        try (var paths = Files.list(directory)) {
            return paths.filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted()
                    .toList();
        }
    }

    private static String fileId(Path path) {
        String filename = path.getFileName().toString();
        return filename.substring(0, filename.length() - ".json".length());
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
