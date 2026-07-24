package com.mathmod.client.screen;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ProgramGraphPresentation {
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
