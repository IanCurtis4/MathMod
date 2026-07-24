package com.mathmod.program;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GuidedWorkspaceStateTest {
    @Test
    void roundTripsVersionedWorkspaceWithoutEmbeddingExecutionGraph() {
        GuidedWorkspaceState state = GuidedWorkspaceState.create(
                "Measured Step",
                List.of(
                        CustomSpellInvocation.defaults(CustomSpellAction.SELF),
                        new CustomSpellInvocation(CustomSpellAction.NUMBER_ONE, Map.of("value", 2.5D))
                )
        );

        JsonElement encoded = GuidedWorkspaceState.CODEC.encodeStart(JsonOps.INSTANCE, state).getOrThrow();
        GuidedWorkspaceState decoded = GuidedWorkspaceState.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(state, decoded);
        assertEquals(GuidedWorkspaceState.CURRENT_VERSION, encoded.getAsJsonObject().get("version").getAsInt());
        assertFalse(encoded.getAsJsonObject().has("graph"));
        assertFalse(encoded.getAsJsonObject().has("canvas"));
    }

    @Test
    void unknownLegacyInvocationIsPreservedButNeverPartiallyReplayed() {
        GuidedWorkspaceState state = GuidedWorkspaceState.migrateLegacy(
                "Old proof",
                List.of(CustomSpellAction.SELF.persistentId(), "removed_mod:lost_form")
        ).getOrThrow();

        assertEquals("removed_mod:lost_form", state.invocationIds().get(1));
        assertFalse(state.replayable());
        assertTrue(state.replayableInvocations().isEmpty());
    }

    @Test
    void rejectsUnsupportedVersionsAndOversizedLegacyRecipes() {
        GuidedWorkspaceState future = new GuidedWorkspaceState(2, "Future", List.of("self"));
        assertTrue(GuidedWorkspaceState.CODEC.encodeStart(JsonOps.INSTANCE, future).isError());

        List<String> oversized = java.util.Collections.nCopies(
                GuidedWorkspaceState.MAX_INVOCATIONS + 1,
                CustomSpellAction.SELF.persistentId()
        );
        assertTrue(GuidedWorkspaceState.migrateLegacy("Too large", oversized).isError());
    }
}
