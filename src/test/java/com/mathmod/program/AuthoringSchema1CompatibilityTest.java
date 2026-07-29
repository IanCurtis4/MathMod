package com.mathmod.program;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.mathmod.runes.ProgramGraph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthoringSchema1CompatibilityTest {
    @Test
    void frozenSchemaOneSaveDecodesAndReplaysWithoutChangingTheEncodedRecipe() {
        JsonElement saved = JsonParser.parseString("""
                {"version":1,"name":"Schema One","invocations":["mathmod:self","mathmod:number_one?value=0x1.4p1","mathmod:add_one"]}
                """);

        GuidedWorkspaceState decoded = GuidedWorkspaceState.CODEC.parse(JsonOps.INSTANCE, saved).getOrThrow();
        JsonElement reencoded = GuidedWorkspaceState.CODEC.encodeStart(JsonOps.INSTANCE, decoded).getOrThrow();
        ProgramGraph replayed = graphFor(decoded.replayableInvocations().orElseThrow());

        assertEquals(1, decoded.version());
        assertEquals(saved, reencoded);
        assertEquals(replayed, graphFor(List.of(
                CustomSpellInvocation.defaults(CustomSpellAction.SELF),
                new CustomSpellInvocation(CustomSpellAction.NUMBER_ONE, java.util.Map.of("value", 2.5D)),
                CustomSpellInvocation.defaults(CustomSpellAction.ADD_ONE)
        )));
    }

    @Test
    void legacyUnknownAndMalformedRecipesStayUnreplayableWithoutPartialReplay() {
        GuidedWorkspaceState unknown = GuidedWorkspaceState.migrateLegacy(
                "Old proof", List.of("mathmod:self", "removed_mod:lost_form")
        ).getOrThrow();
        GuidedWorkspaceState future = new GuidedWorkspaceState(2, "Future", List.of("mathmod:self"));
        JsonElement malformedSerialized = JsonParser.parseString("""
                {"version":1,"name":"Old proof","invocations":[""]}
                """);

        assertEquals(List.of("mathmod:self", "removed_mod:lost_form"), unknown.invocationIds());
        assertFalse(unknown.replayable());
        assertTrue(unknown.replayableInvocations().isEmpty());
        assertTrue(GuidedWorkspaceState.CODEC.parse(JsonOps.INSTANCE, malformedSerialized).error().isPresent(),
                "Malformed serialized workspace data must be rejected before it becomes a typed component");
        assertFalse(future.supported());
    }

    private static ProgramGraph graphFor(List<CustomSpellInvocation> invocations) {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();
        workspace.loadInvocations(invocations);
        return workspace.toGraph();
    }

}
