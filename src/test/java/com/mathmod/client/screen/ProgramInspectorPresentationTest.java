package com.mathmod.client.screen;

import com.mathmod.program.ProgramSurface;
import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RunePurity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramInspectorPresentationTest {
    @Test
    void laysOutClosedPureNodesAndShowsTheirNormalizedValues() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("two", "mathmod:constant_number", Map.of("value", "2")),
                        new ProgramNode("three", "mathmod:constant_number", Map.of("value", "3")),
                        new ProgramNode("sum", "mathmod:number_add")
                ),
                List.of(
                        new ProgramEdge("two", "sum", "a"),
                        new ProgramEdge("three", "sum", "b")
                ),
                "sum",
                16
        );

        ProgramInspectorPresentation.Model model = ProgramInspectorPresentation.build(ProgramSurface.theorem(graph).inspect());
        ProgramInspectorPresentation.Node sum = model.node("sum");

        assertEquals(3, model.nodes().size());
        assertEquals(1, sum.layer());
        assertEquals(RunePurity.PURE, sum.purity());
        assertTrue(sum.normalized());
        assertEquals("5.0", sum.normalizedValue());
        assertTrue(sum.formula().contains("a=two"));
        assertTrue(sum.formula().contains("b=three"));
    }

    @Test
    void exposesObservationAsDynamicDependencyWithoutChangingTheGraph() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("position", "mathmod:player_position")
                ),
                List.of(new ProgramEdge("self", "position", "player")),
                "position",
                16
        );

        ProgramInspectorPresentation.Model model = ProgramInspectorPresentation.build(ProgramSurface.inscribed(graph).inspect());
        ProgramInspectorPresentation.Node position = model.node("position");

        assertFalse(position.normalized());
        assertTrue(position.dynamicDependencies().contains("self"));
        assertEquals(graph, model.surface().graph());
        assertEquals("position", model.surface().graph().outputNodeId());
    }

    @Test
    void layoutIsDeterministicForTheSameGraph() {
        ProgramGraph graph = new ProgramGraph(
                List.of(new ProgramNode("self", "mathmod:self_player"), new ProgramNode("position", "mathmod:player_position")),
                List.of(new ProgramEdge("self", "position", "player")), "position", 16);
        ProgramInspectorPresentation.Model first = ProgramInspectorPresentation.build(ProgramSurface.inscribed(graph).inspect());
        ProgramInspectorPresentation.Model second = ProgramInspectorPresentation.build(ProgramSurface.inscribed(graph).inspect());
        assertEquals(first.nodes(), second.nodes());
        assertEquals(first.edges(), second.edges());
    }

    @Test
    void exposesDeterministicNamedInputSocketsAndNarrationBindings() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("two", "mathmod:constant_number", Map.of("value", "2")),
                        new ProgramNode("three", "mathmod:constant_number", Map.of("value", "3")),
                        new ProgramNode("sum", "mathmod:number_add")
                ),
                List.of(new ProgramEdge("three", "sum", "b"), new ProgramEdge("two", "sum", "a")),
                "sum", 16);

        ProgramInspectorPresentation.Model model = ProgramInspectorPresentation.build(ProgramSurface.theorem(graph).inspect());
        ProgramInspectorPresentation.Node sum = model.node("sum");
        ProgramInspectorPresentation.Narration narration = ProgramInspectorPresentation.narration(
                model, "sum", ProgramGraphPresentation.Viewport.initial().pan(-12, -8, 320, 240, 1_000, 1_000));

        assertEquals(List.of("a", "b"), sum.inputNames());
        assertEquals("out:number", narration.outputSocket());
        assertEquals("a=two, b=three", narration.socketBindings());
        assertEquals(3, narration.position());
        assertEquals(3, narration.total());
        assertEquals(-12, narration.panX());
        assertEquals(-8, narration.panY());
    }
}
