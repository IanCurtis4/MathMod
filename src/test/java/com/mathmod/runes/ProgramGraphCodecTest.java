package com.mathmod.runes;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramGraphCodecTest {
    @Test
    void roundTripsProgramGraphAsJson() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("power", "mathmod:constant_number", Map.of("value", "0.35")),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("power", "push", "vector")
                ),
                "push",
                12
        );

        JsonElement encoded = ProgramGraph.CODEC.encodeStart(JsonOps.INSTANCE, graph).getOrThrow();
        ProgramGraph decoded = ProgramGraph.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(graph, decoded);
        assertEquals(ProgramGraph.CURRENT_VERSION, encoded.getAsJsonObject().get("version").getAsInt());
        assertEquals("push", encoded.getAsJsonObject().get("output_node").getAsString());
    }

    @Test
    void rejectsUnknownRuneTypeIds() {
        assertTrue(RuneType.byId("not_a_type").isError());
    }

    @Test
    void acceptsCollectionAndEffectPlanTypeIds() {
        assertEquals(RuneType.ENTITY_LIST, RuneType.byId("entity_list").getOrThrow());
        assertEquals(RuneType.BLOCK_POS_LIST, RuneType.byId("block_pos_list").getOrThrow());
        assertEquals(RuneType.VEC3_LIST, RuneType.byId("vec3_list").getOrThrow());
        assertEquals(RuneType.REGION, RuneType.byId("region").getOrThrow());
        assertEquals(RuneType.EFFECT_PLAN, RuneType.byId("effect_plan").getOrThrow());
    }
}
