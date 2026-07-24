package com.mathmod.client.screen;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramGraphPresentationTest {
    @Test
    void assignsStableNumbersAndMarksTheOutput() {
        List<ProgramGraphPresentation.Node> nodes = ProgramGraphPresentation.nodes(graph());

        assertEquals(1, nodes.get(0).number());
        assertFalse(nodes.get(0).output());
        assertEquals(3, nodes.get(2).number());
        assertTrue(nodes.get(2).output());
    }

    @Test
    void resolvesIncomingEdgesToNumberedSourceNodes() {
        ProgramGraphPresentation.Node push = ProgramGraphPresentation.nodes(graph()).get(2);

        assertEquals(1, push.binding("player").sourceNumber());
        assertEquals("self", push.binding("player").source().id());
        assertEquals(2, push.binding("vector").sourceNumber());
        assertNull(push.binding("missing"));
    }

    @Test
    void ignoresDanglingEdgesWithoutChangingVisibleNodeNumbers() {
        ProgramGraph graph = graph();
        ProgramGraph withDanglingEdge = new ProgramGraph(
                graph.nodes(),
                List.of(
                        new ProgramEdge("missing", "push", "player"),
                        new ProgramEdge("self", "push", "player")
                ),
                graph.outputNodeId(),
                graph.budgetLimit()
        );

        List<ProgramGraphPresentation.Node> nodes = ProgramGraphPresentation.nodes(withDanglingEdge);

        assertEquals(3, nodes.size());
        assertEquals(1, nodes.get(2).binding("player").sourceNumber());
    }

    private static ProgramGraph graph() {
        ProgramNode self = new ProgramNode("self", "mathmod:self_player", Map.of());
        ProgramNode vector = new ProgramNode("vector", "mathmod:look_vector", Map.of());
        ProgramNode push = new ProgramNode("push", "mathmod:push_self", Map.of());
        return new ProgramGraph(
                List.of(self, vector, push),
                List.of(
                        new ProgramEdge("self", "vector", "player"),
                        new ProgramEdge("self", "push", "player"),
                        new ProgramEdge("vector", "push", "vector")
                ),
                "push",
                16
        );
    }
}
