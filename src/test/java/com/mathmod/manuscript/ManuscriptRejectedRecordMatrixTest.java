package com.mathmod.manuscript;

import com.google.gson.JsonParser;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ManuscriptRejectedRecordMatrixTest {
    private static final NamespacedId RECORD = NamespacedId.of("mathmod", "rejected");

    @Test
    void everyMalformedCodecFixtureIsRejected() {
        List<RejectedCase> cases = List.of(
                new RejectedCase("future tradition schema", () -> ManuscriptCodecs.decodeTradition(
                        RECORD,
                        JsonParser.parseString("""
                                {"schema_version": 2, "name_key": "name", "summary_key": "summary", "icon": "minecraft:paper"}
                                """))),
                new RejectedCase("empty manuscript pages", () -> ManuscriptCodecs.decodeManuscript(
                        RECORD,
                        JsonParser.parseString("""
                                {"schema_version": 1, "tradition": "mathmod:tradition", "title_key": "title", "page_keys": [], "icon": "minecraft:paper", "rarity": "common"}
                                """))),
                new RejectedCase("too many manuscript pages", () -> ManuscriptCodecs.decodeManuscript(
                        RECORD,
                        JsonParser.parseString("""
                                {"schema_version": 1, "tradition": "mathmod:tradition", "title_key": "title", "page_keys": ["1", "2", "3", "4", "5", "6", "7", "8", "9"], "icon": "minecraft:paper", "rarity": "common"}
                                """))),
                new RejectedCase("unknown rarity", () -> ManuscriptCodecs.decodeManuscript(
                        RECORD,
                        JsonParser.parseString("""
                                {"schema_version": 1, "tradition": "mathmod:tradition", "title_key": "title", "page_keys": ["page"], "icon": "minecraft:paper", "rarity": "legendary"}
                                """))),
                new RejectedCase("self alias", () -> ManuscriptCodecs.ALIAS.parse(
                        com.mojang.serialization.JsonOps.INSTANCE,
                        JsonParser.parseString("""
                                {"schema_version": 1, "from": "mathmod:rejected", "to": "mathmod:rejected"}
                                """)))
        );

        for (RejectedCase rejectedCase : cases) {
            assertTrue(
                    rejectedCase.decode().get().error().isPresent(),
                    () -> "Fixture unexpectedly decoded: " + rejectedCase.name()
            );
        }
    }

    private record RejectedCase(String name, Supplier<com.mojang.serialization.DataResult<?>> decode) {
    }
}
