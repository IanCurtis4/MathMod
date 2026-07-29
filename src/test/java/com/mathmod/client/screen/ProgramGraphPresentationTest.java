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

    @Test
    void viewportUsesOneTransformForGeometryHitTestingAndClippingAtAllZoomExtrema() {
        for (double requestedZoom : List.of(0.001D, 1.0D, 100.0D)) {
            ProgramGraphPresentation.Viewport viewport = ProgramGraphPresentation.Viewport.initial()
                    .zoomBy(requestedZoom, 320, 240, 1_000, 1_000);
            ProgramGraphPresentation.Rect first = viewport.screenRect(0, 0, 0, 0,
                    ProgramGraphPresentation.NODE_WIDTH, ProgramGraphPresentation.NODE_HEIGHT);
            ProgramGraphPresentation.Rect second = viewport.screenRect(0, 0, 128, 0,
                    ProgramGraphPresentation.NODE_WIDTH, ProgramGraphPresentation.NODE_HEIGHT);
            assertFalse(first.intersects(second));
            assertTrue(first.contains(first.x() + first.width() - 1, first.y() + first.height() - 1));
            assertFalse(first.contains(first.x() + first.width(), first.y()));
            assertEquals(Math.round(ProgramGraphPresentation.NODE_WIDTH * viewport.zoom()), first.width());
            assertEquals(first.y() + first.height() / 3, ProgramGraphPresentation.inputSocketY(first, 0, 2));
            assertEquals(first.y() + first.height() * 2 / 3, ProgramGraphPresentation.inputSocketY(first, 1, 2));
            ProgramGraphPresentation.Rect content = new ProgramGraphPresentation.Rect(17, 23, 286, 210);
            ProgramGraphPresentation.Rect firstLabel = ProgramGraphPresentation.boundedLabelRect(
                    content, first.x() - 40, ProgramGraphPresentation.inputSocketY(first, 0, 2) - 8, 24, 9);
            ProgramGraphPresentation.Rect secondLabel = ProgramGraphPresentation.boundedLabelRect(
                    content, second.x() + 400, ProgramGraphPresentation.inputSocketY(second, 1, 2) - 8, 24, 9);
            assertTrue(firstLabel.inside(content));
            assertTrue(secondLabel.inside(content));
        }
        assertEquals(ProgramGraphPresentation.MIN_ZOOM, ProgramGraphPresentation.Viewport.initial()
                .zoomBy(0.001, 1_920, 1_080, 2_000, 2_000).zoom());
        assertEquals(ProgramGraphPresentation.MAX_ZOOM, ProgramGraphPresentation.Viewport.initial()
                .zoomBy(100, 1_920, 1_080, 2_000, 2_000).zoom());
    }

    @Test
    void viewportPansInBothDirectionsAndRevealReturnsFocusedNodeToTheViewport() {
        ProgramGraphPresentation.Viewport far = ProgramGraphPresentation.Viewport.initial()
                .pan(-10_000, -10_000, 320, 240, 1_000, 1_000);
        assertTrue(far.panX() < 0);
        assertTrue(far.panY() < 0);
        ProgramGraphPresentation.Viewport returned = far.pan(10_000, 10_000, 320, 240, 1_000, 1_000);
        assertEquals(0, returned.panX());
        assertEquals(0, returned.panY());

        ProgramGraphPresentation.Viewport revealed = far.reveal(0, 0,
                ProgramGraphPresentation.NODE_WIDTH, ProgramGraphPresentation.NODE_HEIGHT,
                320, 240, 1_000, 1_000);
        ProgramGraphPresentation.Rect focused = revealed.screenRect(0, 0, 0, 0,
                ProgramGraphPresentation.NODE_WIDTH, ProgramGraphPresentation.NODE_HEIGHT);
        assertTrue(focused.inside(new ProgramGraphPresentation.Rect(0, 0, 320, 240)));
    }

    @Test
    void atm10ViewportGeometryClipsAndRevealsAtTheActualMinimumAndLargeViewportSizes() {
        ProgramGraphPresentation.Viewport minimum = ProgramGraphPresentation.Viewport.initial()
                .zoomBy(0.001, 320, 240, 2_000, 2_000)
                .pan(-10_000, -10_000, 320, 240, 2_000, 2_000);
        ProgramGraphPresentation.Viewport large = ProgramGraphPresentation.Viewport.initial()
                .zoomBy(100, 1_920, 1_080, 2_000, 2_000)
                .reveal(1_500, 1_500, ProgramGraphPresentation.NODE_WIDTH, ProgramGraphPresentation.NODE_HEIGHT,
                        1_920, 1_080, 2_000, 2_000);
        assertFalse(minimum.screenRect(0, 0, 0, 0, ProgramGraphPresentation.NODE_WIDTH, ProgramGraphPresentation.NODE_HEIGHT)
                .inside(new ProgramGraphPresentation.Rect(0, 0, 320, 240)));
        assertTrue(large.screenRect(0, 0, 1_500, 1_500, ProgramGraphPresentation.NODE_WIDTH, ProgramGraphPresentation.NODE_HEIGHT)
                .inside(new ProgramGraphPresentation.Rect(0, 0, 1_920, 1_080)));
    }

    @Test
    void insetContentRectangleKeepsFocusedNodesAndSocketsFullyVisible() {
        ProgramGraphPresentation.Rect inner = new ProgramGraphPresentation.Rect(18, 62, 304, 220);
        for (double requestedZoom : List.of(0.001D, 100.0D)) {
            ProgramGraphPresentation.Viewport viewport = ProgramGraphPresentation.Viewport.initial()
                    .zoomBy(requestedZoom, inner.width(), inner.height(), 2_000, 2_000)
                    .reveal(1_500, 1_500, ProgramGraphPresentation.NODE_WIDTH, ProgramGraphPresentation.NODE_HEIGHT,
                            inner.width(), inner.height(), 2_000, 2_000);
            ProgramGraphPresentation.Rect focused = viewport.screenRect(inner.x(), inner.y(), 1_500, 1_500,
                    ProgramGraphPresentation.NODE_WIDTH, ProgramGraphPresentation.NODE_HEIGHT);
            assertTrue(focused.inside(inner));
            int socketY = ProgramGraphPresentation.inputSocketY(focused, 1, 2);
            assertTrue(socketY >= inner.y() && socketY < inner.y() + inner.height());
        }
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
