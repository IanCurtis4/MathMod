package com.mathmod.program;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneType;
import com.mathmod.runes.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramRegionPipelineTest {
    @Test
    void regionsCanFilterEntityCollections() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("radius", "mathmod:constant_number", Map.of("value", "4")),
                        new ProgramNode("region", "mathmod:sphere_region"),
                        new ProgramNode("nearby", "mathmod:nearby_entities", Map.of(
                                "predicate", "any_living",
                                "radius", "6",
                                "limit", "8"
                        )),
                        new ProgramNode("inside", "mathmod:filter_entities_in_region"),
                        new ProgramNode("positions", "mathmod:entity_positions"),
                        new ProgramNode("count", "mathmod:count_entities")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "region", "center"),
                        new ProgramEdge("radius", "region", "radius"),
                        new ProgramEdge("center", "nearby", "center"),
                        new ProgramEdge("nearby", "inside", "entities"),
                        new ProgramEdge("region", "inside", "region"),
                        new ProgramEdge("inside", "positions", "entities"),
                        new ProgramEdge("inside", "count", "entities")
                ),
                "count",
                24
        );

        ValidationResult result = ProgramStorage.validate(graph);

        assertTrue(result.valid());
        assertEquals(14, result.budgetUsed());
        assertEquals(RuneType.NUMBER, result.outputType());
    }

    @Test
    void regionsCanFilterBlockCollectionsAndProducePositions() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("radius", "mathmod:constant_number", Map.of("value", "4")),
                        new ProgramNode("region", "mathmod:sphere_region"),
                        new ProgramNode("blocks", "mathmod:nearby_blocks", Map.of(
                                "selector", "any",
                                "radius", "5",
                                "limit", "32"
                        )),
                        new ProgramNode("inside", "mathmod:filter_blocks_in_region"),
                        new ProgramNode("positions", "mathmod:block_positions"),
                        new ProgramNode("average", "mathmod:average_position")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "region", "center"),
                        new ProgramEdge("radius", "region", "radius"),
                        new ProgramEdge("center", "blocks", "center"),
                        new ProgramEdge("blocks", "inside", "blocks"),
                        new ProgramEdge("region", "inside", "region"),
                        new ProgramEdge("inside", "positions", "blocks"),
                        new ProgramEdge("positions", "average", "positions")
                ),
                "average",
                24
        );

        ValidationResult result = ProgramStorage.validate(graph);

        assertTrue(result.valid());
        assertEquals(15, result.budgetUsed());
        assertEquals(RuneType.VEC3, result.outputType());
    }

    @Test
    void sampledRegionsProducePositionLists() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("radius", "mathmod:constant_number", Map.of("value", "3")),
                        new ProgramNode("region", "mathmod:sphere_region"),
                        new ProgramNode("sample", "mathmod:sample_region", Map.of(
                                "step", "1",
                                "limit", "24"
                        )),
                        new ProgramNode("average", "mathmod:average_position")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "region", "center"),
                        new ProgramEdge("radius", "region", "radius"),
                        new ProgramEdge("region", "sample", "region"),
                        new ProgramEdge("sample", "average", "positions")
                ),
                "average",
                16
        );

        ValidationResult result = ProgramStorage.validate(graph);

        assertTrue(result.valid());
        assertEquals(11, result.budgetUsed());
        assertEquals(RuneType.VEC3, result.outputType());
    }
}
