package com.mathmod.client.screen;

import com.mathmod.program.NormalizedValue;
import com.mathmod.program.ProgramNormalization;
import com.mathmod.program.ProgramNormalizer;
import com.mathmod.program.ProgramSurface;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RunePurity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Pure presentation model for the read-only inspector. */
final class ProgramInspectorPresentation {
    private ProgramInspectorPresentation() {
    }

    static Model build(ProgramSurface surface) {
        MathModRuneBootstrap.bootstrap();
        ProgramGraph graph = surface.graph();
        ProgramNormalization normalization = ProgramNormalizer.normalize(graph, MathModRuneBootstrap.registry());
        Map<String, ProgramNode> nodesById = new LinkedHashMap<>();
        graph.nodes().forEach(node -> nodesById.put(node.id(), node));
        Map<String, List<ProgramEdge>> incoming = new HashMap<>();
        Map<String, List<ProgramEdge>> outgoing = new HashMap<>();
        for (ProgramEdge edge : graph.edges()) {
            incoming.computeIfAbsent(edge.toNodeId(), ignored -> new ArrayList<>()).add(edge);
            outgoing.computeIfAbsent(edge.fromNodeId(), ignored -> new ArrayList<>()).add(edge);
        }

        Map<String, Integer> layers = layers(graph, incoming);
        Map<Integer, Integer> rows = new HashMap<>();
        List<Node> nodes = new ArrayList<>();
        for (ProgramNode node : graph.nodes()) {
            RuneDefinition definition = MathModRuneBootstrap.registry().find(node.runeId()).orElse(null);
            int layer = layers.getOrDefault(node.id(), 0);
            int row = rows.merge(layer, 1, Integer::sum) - 1;
            List<String> dependencies = dynamicDependencies(node.id(), incoming, nodesById);
            Optional<NormalizedValue> normalized = normalization.value(node.id());
            nodes.add(new Node(
                    node.id(),
                    node.runeId(),
                    definition == null ? node.runeId() : definition.executorKey(),
                    definition == null ? RunePurity.EFFECT : definition.purity(),
                    definition == null ? "unknown" : definition.outputType().id(),
                    definition == null ? 0 : definition.budgetCost(),
                    definition == null ? List.of() : definition.materialRequirements().stream()
                            .map(requirement -> requirement.quantity() + "x " + requirement.itemOrTag()).toList(),
                    definition == null ? List.of() : definition.attributeRequirements().stream()
                            .map(requirement -> requirement.attribute() + " " + requirement.amount()).toList(),
                    formula(node, definition, incoming.getOrDefault(node.id(), List.of())),
                    normalized.map(ProgramInspectorPresentation::valueText).orElse("dynamic"),
                    normalized.isPresent(),
                    dependencies,
                    inputNames(definition, incoming.getOrDefault(node.id(), List.of())),
                    layer,
                    row,
                    node.id().equals(graph.outputNodeId())
            ));
        }
        List<Edge> edges = graph.edges().stream()
                .filter(edge -> nodesById.containsKey(edge.fromNodeId()) && nodesById.containsKey(edge.toNodeId()))
                .map(edge -> new Edge(edge.fromNodeId(), edge.toNodeId(), edge.inputName()))
                .toList();
        return new Model(surface, List.copyOf(nodes), edges);
    }

    static Narration narration(Model model, String selectedNodeId, ProgramGraphPresentation.Viewport viewport) {
        return narration(model, selectedNodeId, viewport.zoom(), viewport.panX(), viewport.panY());
    }

    static Narration narration(Model model, String selectedNodeId, double zoom, double panX, double panY) {
        Node selected = model.node(selectedNodeId);
        if (selected == null) {
            return null;
        }
        List<Node> ordered = model.orderedNodes();
        int position = ordered.indexOf(selected) + 1;
        String bindings = selected.inputNames().stream()
                .map(input -> input + "=" + bindingSource(model, selected.id(), input))
                .reduce((left, right) -> left + ", " + right)
                .orElse("none");
        return new Narration(selected.id(), position, ordered.size(), "out:" + selected.outputType(), bindings,
                zoom, panX, panY);
    }

    private static String bindingSource(Model model, String targetId, String inputName) {
        return model.edges().stream()
                .filter(edge -> edge.toId().equals(targetId) && edge.inputName().equals(inputName))
                .map(Edge::fromId)
                .findFirst()
                .orElse("unbound");
    }

    private static Map<String, Integer> layers(ProgramGraph graph, Map<String, List<ProgramEdge>> incoming) {
        Map<String, Integer> result = new HashMap<>();
        ArrayDeque<ProgramNode> pending = new ArrayDeque<>(graph.nodes());
        int attempts = graph.nodes().size() * graph.nodes().size() + 1;
        while (!pending.isEmpty() && attempts-- > 0) {
            ProgramNode node = pending.removeFirst();
            List<ProgramEdge> predecessors = incoming.getOrDefault(node.id(), List.of());
            if (predecessors.stream().anyMatch(edge -> !result.containsKey(edge.fromNodeId()))) {
                pending.addLast(node);
                continue;
            }
            int layer = predecessors.stream().mapToInt(edge -> result.get(edge.fromNodeId()) + 1).max().orElse(0);
            result.put(node.id(), layer);
        }
        for (ProgramNode node : graph.nodes()) {
            result.putIfAbsent(node.id(), 0);
        }
        return result;
    }

    private static List<String> dynamicDependencies(
            String nodeId,
            Map<String, List<ProgramEdge>> incoming,
            Map<String, ProgramNode> nodes
    ) {
        Set<String> result = new LinkedHashSet<>();
        Set<String> visited = new LinkedHashSet<>();
        collectDynamic(nodeId, incoming, nodes, visited, result);
        result.remove(nodeId);
        return result.stream().sorted().toList();
    }

    private static void collectDynamic(
            String nodeId,
            Map<String, List<ProgramEdge>> incoming,
            Map<String, ProgramNode> nodes,
            Set<String> visited,
            Set<String> result
    ) {
        if (!visited.add(nodeId)) {
            return;
        }
        ProgramNode node = nodes.get(nodeId);
        RuneDefinition definition = node == null ? null : MathModRuneBootstrap.registry().find(node.runeId()).orElse(null);
        if (definition == null || definition.purity() != RunePurity.PURE) {
            result.add(nodeId);
        }
        for (ProgramEdge edge : incoming.getOrDefault(nodeId, List.of())) {
            collectDynamic(edge.fromNodeId(), incoming, nodes, visited, result);
        }
    }

    private static String formula(ProgramNode node, RuneDefinition definition, List<ProgramEdge> inputs) {
        if (definition != null && "constant_number".equals(definition.executorKey())) {
            return node.id() + " = " + node.constants().getOrDefault("value", "0");
        }
        String operator = definition == null || definition.executorKey().isEmpty()
                ? node.runeId()
                : definition.executorKey();
        String arguments = inputs.stream()
                .sorted(Comparator.comparing(ProgramEdge::inputName))
                .map(edge -> edge.inputName() + "=" + edge.fromNodeId())
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
        return node.id() + " = " + operator + "(" + arguments + ")";
    }

    private static List<String> inputNames(RuneDefinition definition, List<ProgramEdge> incoming) {
        if (definition != null) {
            return definition.inputs().stream().map(input -> input.name()).toList();
        }
        return incoming.stream().map(ProgramEdge::inputName).distinct().sorted().toList();
    }

    private static String valueText(NormalizedValue value) {
        return switch (value) {
            case NormalizedValue.NumberValue number -> Double.toString(number.value());
            case NormalizedValue.BoolValue bool -> Boolean.toString(bool.value());
            case NormalizedValue.VectorValue vector -> "(" + vector.x() + ", " + vector.y() + ", " + vector.z() + ")";
        };
    }

    record Model(ProgramSurface surface, List<Node> nodes, List<Edge> edges) {
        Node node(String id) {
            return nodes.stream().filter(node -> node.id().equals(id)).findFirst().orElse(null);
        }

        List<Node> orderedNodes() {
            return nodes.stream().sorted(Comparator.comparingInt(Node::layer).thenComparingInt(Node::row)).toList();
        }
    }

    record Node(
            String id,
            String runeId,
            String executorKey,
            RunePurity purity,
            String outputType,
            int budgetCost,
            List<String> materials,
            List<String> attributes,
            String formula,
            String normalizedValue,
            boolean normalized,
            List<String> dynamicDependencies,
            List<String> inputNames,
            int layer,
            int row,
            boolean output
    ) {
        Node {
            materials = List.copyOf(materials);
            attributes = List.copyOf(attributes);
            dynamicDependencies = List.copyOf(dynamicDependencies);
            inputNames = List.copyOf(inputNames);
        }
    }

    record Edge(String fromId, String toId, String inputName) {
    }

    record Narration(String nodeId, int position, int total, String outputSocket, String socketBindings,
                     double zoom, double panX, double panY) {
    }
}
