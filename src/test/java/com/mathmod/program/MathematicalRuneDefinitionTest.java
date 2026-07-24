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

class MathematicalRuneDefinitionTest {
    @Test
    void localFrameTransformsAPlayerRelativeVectorIntoWorldSpace() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("frame", "mathmod:player_frame"),
                        new ProgramNode("x", "mathmod:constant_number", Map.of("value", "1")),
                        new ProgramNode("y", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("z", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("local", "mathmod:vector_from_numbers"),
                        new ProgramNode("world", "mathmod:transform_local_vector")
                ),
                List.of(
                        new ProgramEdge("self", "frame", "player"),
                        new ProgramEdge("x", "local", "x"),
                        new ProgramEdge("y", "local", "y"),
                        new ProgramEdge("z", "local", "z"),
                        new ProgramEdge("frame", "world", "frame"),
                        new ProgramEdge("local", "world", "vector")
                ),
                "world",
                16
        );

        ValidationResult result = ProgramStorage.validate(graph);

        assertTrue(result.valid());
        assertEquals(11, result.budgetUsed());
        assertEquals(RuneType.VEC3, result.outputType());
    }

    @Test
    void scalarMathCanDriveVectorScalingInExecutableGraph() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("base", "mathmod:constant_number", Map.of("value", "0.5")),
                        new ProgramNode("gain", "mathmod:constant_number", Map.of("value", "2")),
                        new ProgramNode("factor", "mathmod:number_multiply"),
                        new ProgramNode("look", "mathmod:look_vector"),
                        new ProgramNode("vector", "mathmod:scale_vector"),
                        new ProgramNode("push", "mathmod:push_self")
                ),
                List.of(
                        new ProgramEdge("base", "factor", "a"),
                        new ProgramEdge("gain", "factor", "b"),
                        new ProgramEdge("self", "look", "player"),
                        new ProgramEdge("look", "vector", "vector"),
                        new ProgramEdge("factor", "vector", "factor"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("vector", "push", "vector")
                ),
                "push",
                24
        );

        ValidationResult result = ProgramStorage.validateExecutable(graph);

        assertTrue(result.valid());
        assertEquals(13, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void vectorGeometryCanProduceAReusableNumber() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("x", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("y", "mathmod:constant_number", Map.of("value", "1")),
                        new ProgramNode("z", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("vector", "mathmod:vector_from_numbers"),
                        new ProgramNode("normalized", "mathmod:vector_normalize"),
                        new ProgramNode("length", "mathmod:vector_length")
                ),
                List.of(
                        new ProgramEdge("x", "vector", "x"),
                        new ProgramEdge("y", "vector", "y"),
                        new ProgramEdge("z", "vector", "z"),
                        new ProgramEdge("vector", "normalized", "vector"),
                        new ProgramEdge("normalized", "length", "vector")
                ),
                "length",
                16
        );

        ValidationResult result = ProgramStorage.validate(graph);

        assertTrue(result.valid());
        assertEquals(8, result.budgetUsed());
        assertEquals(RuneType.NUMBER, result.outputType());
    }
}
