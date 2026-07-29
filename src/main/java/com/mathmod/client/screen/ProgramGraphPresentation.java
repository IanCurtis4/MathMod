package com.mathmod.client.screen;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

final class ProgramGraphPresentation {
    static final double MIN_ZOOM = 0.50D;
    static final double MAX_ZOOM = 2.00D;
    static final int NODE_WIDTH = 104;
    static final int NODE_HEIGHT = 48;
    private ProgramGraphPresentation() {
    }

    static List<Node> nodes(ProgramGraph graph) {
        Map<String, ProgramNode> nodesById = new LinkedHashMap<>();
        Map<String, Integer> numbersById = new LinkedHashMap<>();
        for (int index = 0; index < graph.nodes().size(); index++) {
            ProgramNode node = graph.nodes().get(index);
            nodesById.put(node.id(), node);
            numbersById.put(node.id(), index + 1);
        }

        Map<String, List<InputBinding>> bindingsByTarget = new LinkedHashMap<>();
        for (ProgramEdge edge : graph.edges()) {
            ProgramNode source = nodesById.get(edge.fromNodeId());
            if (source == null || !nodesById.containsKey(edge.toNodeId())) {
                continue;
            }
            bindingsByTarget.computeIfAbsent(edge.toNodeId(), ignored -> new ArrayList<>())
                    .add(new InputBinding(
                            edge.inputName(),
                            source,
                            numbersById.get(edge.fromNodeId())
                    ));
        }

        List<Node> result = new ArrayList<>();
        for (int index = 0; index < graph.nodes().size(); index++) {
            ProgramNode node = graph.nodes().get(index);
            result.add(new Node(
                    node,
                    index + 1,
                    node.id().equals(graph.outputNodeId()),
                    bindingsByTarget.getOrDefault(node.id(), List.of())
            ));
        }
        return List.copyOf(result);
    }

    record Viewport(double panX, double panY, double zoom) {
        Viewport { zoom = min(MAX_ZOOM, max(MIN_ZOOM, zoom)); }
        static Viewport initial() { return new Viewport(0, 0, 1); }
        Viewport pan(double deltaX, double deltaY, int canvasWidth, int canvasHeight, int contentWidth, int contentHeight) {
            double maxPanX = max(0, contentWidth - canvasWidth / zoom);
            double maxPanY = max(0, contentHeight - canvasHeight / zoom);
            return new Viewport(clamp(panX + deltaX / zoom, -maxPanX, 0), clamp(panY + deltaY / zoom, -maxPanY, 0), zoom);
        }
        Viewport zoomBy(double factor, int canvasWidth, int canvasHeight, int contentWidth, int contentHeight) {
            return new Viewport(panX, panY, zoom * factor).pan(0, 0, canvasWidth, canvasHeight, contentWidth, contentHeight);
        }
        Viewport reveal(int logicalX, int logicalY, int logicalWidth, int logicalHeight, int canvasWidth, int canvasHeight, int contentWidth, int contentHeight) {
            double nextX = panX, nextY = panY;
            if (logicalX + nextX < 0) nextX = -logicalX;
            if (logicalX + logicalWidth + nextX > canvasWidth / zoom) nextX = canvasWidth / zoom - logicalWidth - logicalX;
            if (logicalY + nextY < 0) nextY = -logicalY;
            if (logicalY + logicalHeight + nextY > canvasHeight / zoom) nextY = canvasHeight / zoom - logicalHeight - logicalY;
            return new Viewport(nextX, nextY, zoom).pan(0, 0, canvasWidth, canvasHeight, contentWidth, contentHeight);
        }
        int screenX(int canvasX, int logicalX) { return canvasX + (int) Math.round((logicalX + panX) * zoom); }
        int screenY(int canvasY, int logicalY) { return canvasY + (int) Math.round((logicalY + panY) * zoom); }
        int screenLength(int logicalLength) { return Math.max(1, (int) Math.round(logicalLength * zoom)); }
        Rect screenRect(int canvasX, int canvasY, int logicalX, int logicalY, int logicalWidth, int logicalHeight) { return new Rect(screenX(canvasX, logicalX), screenY(canvasY, logicalY), screenLength(logicalWidth), screenLength(logicalHeight)); }
        private static double clamp(double value, double lower, double upper) { return max(lower, min(upper, value)); }
    }

    record Rect(int x, int y, int width, int height) {
        boolean contains(double pointX, double pointY) { return pointX >= x && pointX < x + width && pointY >= y && pointY < y + height; }
        boolean intersects(Rect other) { return x < other.x + other.width && x + width > other.x && y < other.y + other.height && y + height > other.y; }
        boolean inside(Rect viewport) { return x >= viewport.x && y >= viewport.y && x + width <= viewport.x + viewport.width && y + height <= viewport.y + viewport.height; }
    }
    static int inputSocketY(Rect node, int socketIndex, int socketCount) { return socketCount <= 0 ? node.y() + node.height() / 2 : node.y() + node.height() * (Math.max(0, socketIndex) + 1) / (socketCount + 1); }
    static Rect boundedLabelRect(Rect content, int preferredX, int preferredY, int width, int height) {
        int boundedWidth = Math.min(width, content.width()), boundedHeight = Math.min(height, content.height());
        return new Rect(max(content.x(), min(preferredX, content.x() + content.width() - boundedWidth)), max(content.y(), min(preferredY, content.y() + content.height() - boundedHeight)), boundedWidth, boundedHeight);
    }

    record Node(
            ProgramNode node,
            int number,
            boolean output,
            List<InputBinding> bindings
    ) {
        Node {
            bindings = List.copyOf(bindings);
        }

        InputBinding binding(String inputName) {
            return bindings.stream()
                    .filter(binding -> binding.inputName().equals(inputName))
                    .findFirst()
                    .orElse(null);
        }
    }

    record InputBinding(
            String inputName,
            ProgramNode source,
            int sourceNumber
    ) {
    }
}
