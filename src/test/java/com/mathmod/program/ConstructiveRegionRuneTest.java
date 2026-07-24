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

class ConstructiveRegionRuneTest {
    @Test
    void revolutionAndBooleanRegionGraphRemainReusableTypedValues() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("origin", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("axisX", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("axisY", "mathmod:constant_number", Map.of("value", "1")),
                        new ProgramNode("axisZ", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("zeroVec", "mathmod:vector_from_numbers"),
                        new ProgramNode("axis", "mathmod:vector_from_numbers"),
                        new ProgramNode("solid", "mathmod:solid_of_revolution", Map.of(
                                "inner", "0", "outer", "1", "lower", "-1", "upper", "1"
                        )),
                        new ProgramNode("union", "mathmod:region_union")
                ),
                List.of(
                        new ProgramEdge("origin", "zeroVec", "x"), new ProgramEdge("origin", "zeroVec", "y"),
                        new ProgramEdge("origin", "zeroVec", "z"),
                        new ProgramEdge("axisX", "axis", "x"), new ProgramEdge("axisY", "axis", "y"),
                        new ProgramEdge("axisZ", "axis", "z"),
                        new ProgramEdge("zeroVec", "solid", "origin"),
                        new ProgramEdge("axis", "solid", "axis"),
                        new ProgramEdge("solid", "union", "first"),
                        new ProgramEdge("solid", "union", "second")
                ),
                "union",
                32
        );

        ValidationResult result = ProgramStorage.validate(graph);

        assertTrue(result.valid());
        assertEquals(RuneType.REGION, result.outputType());
    }

    @Test
    void fillIsAnExplicitUnitBoundaryRatherThanAnotherRegionValue() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("center", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("radius", "mathmod:constant_number", Map.of("value", "1")),
                        new ProgramNode("vector", "mathmod:vector_from_numbers"),
                        new ProgramNode("region", "mathmod:sphere_region"),
                        new ProgramNode("fill", "mathmod:fill_region", Map.of("material", "minecraft:stone"))
                ),
                List.of(
                        new ProgramEdge("center", "vector", "x"), new ProgramEdge("center", "vector", "y"),
                        new ProgramEdge("center", "vector", "z"),
                        new ProgramEdge("vector", "region", "center"), new ProgramEdge("radius", "region", "radius"),
                        new ProgramEdge("region", "fill", "region")
                ),
                "fill",
                24
        );

        ValidationResult result = ProgramStorage.validateExecutable(graph);

        assertTrue(result.valid());
        assertEquals(RuneType.UNIT, result.outputType());
    }
}
