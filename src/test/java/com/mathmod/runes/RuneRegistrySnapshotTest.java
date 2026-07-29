package com.mathmod.runes;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RuneRegistrySnapshotTest {
    @Test
    void capturesAnImmutableGenerationAndDefinitionsAtomically() {
        RuneRegistry registry = new RuneRegistry();
        registry.register(rune("test:a"));
        RuneRegistrySnapshot snapshot = registry.captureSnapshot();
        registry.register(rune("test:b"));

        assertEquals(1, snapshot.generation());
        assertEquals(Map.of("test:a", rune("test:a")), snapshot.definitions());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.definitions().clear());
    }

    @Test
    void semanticNoOpsDoNotAdvanceButChangesAndRoundTripsDo() {
        RuneRegistry registry = new RuneRegistry();
        RuneDefinition original = rune("test:a");
        registry.register(original);
        assertEquals(1, registry.generation());
        registry.registerOrReplace(original);
        registry.setEnabled("test:a", true);
        registry.update("test:a", current -> current);
        assertEquals(1, registry.generation());
        registry.setEnabled("test:a", false);
        long changed = registry.generation();
        registry.setEnabled("test:a", true);
        assertEquals(changed + 1, registry.generation());
        RuneDefinition replacement = original.withBudgetCost(3);
        registry.registerOrReplace(replacement);
        assertEquals(changed + 2, registry.generation());
        registry.update("test:a", current -> current.withBudgetCost(4));
        assertEquals(changed + 3, registry.generation());
        registry.update("test:a", current -> original);
        assertEquals(changed + 4, registry.generation(), "A -> B -> A receives a new generation");
    }

    @Test
    void failedAndCompletePublicationsAreAtomicAndOverflowFailsBeforeMutation() throws Exception {
        RuneRegistry registry = new RuneRegistry();
        registry.register(rune("test:a"));
        long before = registry.generation();
        assertThrows(IllegalArgumentException.class, () -> registry.publishComplete(Map.of("test:wrong", rune("test:a"))));
        assertEquals(before, registry.generation());
        assertTrue(registry.find("test:a").isPresent());
        registry.publishComplete(Map.of("test:b", rune("test:b")));
        assertEquals(before + 1, registry.generation());
        assertTrue(registry.find("test:a").isEmpty());
        registry.publishComplete(Map.of("test:b", rune("test:b")));
        assertEquals(before + 1, registry.generation(), "equal complete publication is a no-op");

        long afterPublication = registry.generation();
        assertThrows(IllegalStateException.class, () -> registry.update("test:b", ignored -> { throw new IllegalStateException("fail"); }));
        assertThrows(IllegalArgumentException.class, () -> registry.update("test:b", ignored -> rune("test:wrong")));
        assertEquals(afterPublication, registry.generation());
        assertEquals(rune("test:b"), registry.find("test:b").orElseThrow());

        Field generation = RuneRegistry.class.getDeclaredField("generation");
        generation.setAccessible(true);
        generation.setLong(registry, Long.MAX_VALUE);
        assertThrows(IllegalStateException.class, () -> registry.register(rune("test:c")));
        assertTrue(registry.find("test:c").isEmpty());
    }

    private static RuneDefinition rune(String id) {
        return RuneDefinition.builder(id).output(RuneType.UNIT).purity(RunePurity.EFFECT).executorKey("debug_marker").build();
    }
}
