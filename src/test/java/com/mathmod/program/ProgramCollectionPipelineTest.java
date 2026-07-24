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

class ProgramCollectionPipelineTest {
    @Test
    void entityCollectionsSupportFilterMapReduceAndFarthestSelection() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("nearby", "mathmod:nearby_entities", Map.of(
                                "predicate", "any_living",
                                "radius", "6",
                                "limit", "8"
                        )),
                        new ProgramNode("hostile", "mathmod:filter_entities", Map.of("predicate", "hostile")),
                        new ProgramNode("farthest", "mathmod:farthest_entities", Map.of("limit", "2")),
                        new ProgramNode("positions", "mathmod:entity_positions"),
                        new ProgramNode("count", "mathmod:count_entities")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "nearby", "center"),
                        new ProgramEdge("nearby", "hostile", "entities"),
                        new ProgramEdge("hostile", "farthest", "entities"),
                        new ProgramEdge("center", "farthest", "origin"),
                        new ProgramEdge("farthest", "positions", "entities"),
                        new ProgramEdge("farthest", "count", "entities")
                ),
                "count",
                24
        );

        ValidationResult result = ProgramStorage.validate(graph);

        assertTrue(result.valid());
        assertEquals(12, result.budgetUsed());
        assertEquals(RuneType.NUMBER, result.outputType());
    }

    @Test
    void blockCollectionsSupportQueriesPositionMappingAndReduction() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("center", "mathmod:player_position"),
                        new ProgramNode("blocks", "mathmod:nearby_blocks", Map.of(
                                "selector", "any",
                                "radius", "3",
                                "limit", "16"
                        )),
                        new ProgramNode("positions", "mathmod:block_positions"),
                        new ProgramNode("average", "mathmod:average_position"),
                        new ProgramNode("count", "mathmod:count_blocks")
                ),
                List.of(
                        new ProgramEdge("self", "center", "player"),
                        new ProgramEdge("center", "blocks", "center"),
                        new ProgramEdge("blocks", "positions", "blocks"),
                        new ProgramEdge("positions", "average", "positions"),
                        new ProgramEdge("blocks", "count", "blocks")
                ),
                "average",
                24
        );

        ValidationResult result = ProgramStorage.validate(graph);

        assertTrue(result.valid());
        assertEquals(11, result.budgetUsed());
        assertEquals(RuneType.VEC3, result.outputType());
    }

    @Test
    void entityVelocityCollectionsMapIntoNumericAggregates() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("origin", "mathmod:anchor_origin"),
                        new ProgramNode("entities", "mathmod:sense_nearby_entities"),
                        new ProgramNode("velocities", "mathmod:entity_velocities"),
                        new ProgramNode("speeds", "mathmod:vector_lengths"),
                        new ProgramNode("mean", "mathmod:mean_number")
                ),
                List.of(
                        new ProgramEdge("origin", "entities", "center"),
                        new ProgramEdge("entities", "velocities", "entities"),
                        new ProgramEdge("velocities", "speeds", "vectors"),
                        new ProgramEdge("speeds", "mean", "values")
                ),
                "mean",
                16
        );

        ValidationResult result = ProgramStorage.validate(graph);

        assertTrue(result.valid(), result.issues().toString());
        assertEquals(12, result.budgetUsed());
        assertEquals(RuneType.NUMBER, result.outputType());
    }
}
