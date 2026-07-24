package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KnowledgeAliasRegistryTest {
    @Test
    void resolvesAliasChainsWithinOneKnowledgeKind() {
        NamespacedId oldId = id("old_theorem");
        NamespacedId middleId = id("middle_theorem");
        NamespacedId currentId = id("current_theorem");
        KnowledgeAliasRegistry aliases = KnowledgeAliasRegistry.builder()
                .add(KnowledgeKind.THEOREM, oldId, middleId)
                .add(KnowledgeKind.THEOREM, middleId, currentId)
                .build();

        assertEquals(currentId, aliases.resolve(KnowledgeKind.THEOREM, oldId));
        assertEquals(oldId, aliases.resolve(KnowledgeKind.RUNE, oldId));
    }

    @Test
    void rejectsSelfAliasesConflictsAndCycles() {
        NamespacedId first = id("first");
        NamespacedId second = id("second");
        NamespacedId third = id("third");

        assertThrows(IllegalArgumentException.class, () -> KnowledgeAliasRegistry.builder()
                .add(KnowledgeKind.RUNE, first, first));
        assertThrows(IllegalArgumentException.class, () -> KnowledgeAliasRegistry.builder()
                .add(KnowledgeKind.RUNE, first, second)
                .add(KnowledgeKind.RUNE, first, third));
        assertThrows(IllegalStateException.class, () -> KnowledgeAliasRegistry.builder()
                .add(KnowledgeKind.RUNE, first, second)
                .add(KnowledgeKind.RUNE, second, first)
                .build());
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
