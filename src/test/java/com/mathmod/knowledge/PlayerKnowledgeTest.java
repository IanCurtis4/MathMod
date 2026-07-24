package com.mathmod.knowledge;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerKnowledgeTest {
    @Test
    void grantsAndRevokesWithoutMutatingEarlierSnapshots() {
        NamespacedId theorem = id("hop");
        PlayerKnowledge empty = PlayerKnowledge.empty();
        PlayerKnowledge granted = empty.grant(KnowledgeKind.THEOREM, theorem);

        assertFalse(empty.knows(KnowledgeKind.THEOREM, theorem));
        assertTrue(granted.knows(KnowledgeKind.THEOREM, theorem));
        assertSame(granted, granted.grant(KnowledgeKind.THEOREM, theorem));
        assertNotSame(granted, granted.revoke(KnowledgeKind.THEOREM, theorem));
        assertEquals(PlayerKnowledge.empty(), granted.revoke(KnowledgeKind.THEOREM, theorem));
    }

    @Test
    void codecRoundTripPreservesAllKnowledgeKinds() {
        PlayerKnowledge knowledge = new PlayerKnowledge(
                PlayerKnowledge.CURRENT_SCHEMA_VERSION,
                Set.of(id("iron")),
                Set.of(id("iron_force")),
                Set.of(id("metallic_balance")),
                Set.of(id("rotated_horizon")),
                Set.of(id("vector_add")),
                Set.of(id("hop")),
                java.util.Map.of(id("progress/test"), 2)
        );

        JsonElement encoded = PlayerKnowledge.CODEC.encodeStart(JsonOps.INSTANCE, knowledge).getOrThrow();
        PlayerKnowledge decoded = PlayerKnowledge.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(knowledge, decoded);
    }

    @Test
    void migrationCanonicalizesAliasesAndAdvancesSchema() {
        NamespacedId oldId = id("old_hop");
        NamespacedId currentId = id("hop");
        KnowledgeAliasRegistry aliases = KnowledgeAliasRegistry.builder()
                .add(KnowledgeKind.THEOREM, oldId, currentId)
                .build();
        PlayerKnowledge old = new PlayerKnowledge(
                1,
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(oldId),
                java.util.Map.of()
        );

        PlayerKnowledge migrated = old.migrate(aliases);

        assertEquals(PlayerKnowledge.CURRENT_SCHEMA_VERSION, migrated.schemaVersion());
        assertEquals(Set.of(currentId), migrated.unlockedTheorems());
    }

    @Test
    void rejectsUnboundedKnowledgeSets() {
        LinkedHashSet<NamespacedId> ids = new LinkedHashSet<>();
        for (int index = 0; index <= PlayerKnowledge.MAX_IDS_PER_KIND; index++) {
            ids.add(id("entry_" + index));
        }

        assertThrows(IllegalArgumentException.class, () -> new PlayerKnowledge(
                1,
                ids,
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                java.util.Map.of()
        ));
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
