package com.mathmod.program;

import com.mathmod.language.RuneTypeExpression;
import com.mathmod.language.ScopedExpression;
import com.mathmod.language.ScopedProgramCompiler;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.runes.RuneType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FactoredLeapTheoremTest {
    @Test void canonicalFixtureCompilesToItsFrozenExecutableGraph() {
        MathModRuneBootstrap.bootstrap();
        var source = FactoredLeapTheorem.source();
        var result = new ScopedProgramCompiler(MathModRuneBootstrap.registry()).compile(source);
        assertTrue(result.valid(), result.issues()::toString);
        assertEquals(113, result.chargedSteps());
        assertSemanticIsomorphism(result.graph().orElseThrow(), FactoredLeapTheorem.presentationGraph());
        assertEquals(RuneType.UNIT, source.resultType().type());
        assertEquals(24, source.budgetLimit());
        assertEquals(21, ProgramStorage.validateExecutable(result.graph().orElseThrow()).budgetUsed());
    }

    @Test void fixtureHasFrozenBindersDeBruijnIndicesAndPureLambdaBody() {
        var halve = assertInstanceOf(ScopedExpression.Let.class, FactoredLeapTheorem.source().expression());
        assertEquals("halve", halve.nameHint());
        var lambda = assertInstanceOf(ScopedExpression.Lambda.class, halve.value());
        assertEquals("vector", lambda.nameHint());
        assertEquals(RuneTypeExpression.value(RuneType.VEC3), lambda.parameterType());
        assertCall(lambda.body(), "mathmod:scale_vector", Map.of("vector", 0, "factor", "0.5"));

        var self = assertInstanceOf(ScopedExpression.Let.class, halve.body());
        assertEquals("self", self.nameHint()); assertCall(self.value(), "mathmod:self_player", Map.of());
        var forward = assertInstanceOf(ScopedExpression.Let.class, self.body());
        assertEquals("forward", forward.nameHint()); var forwardApply = assertInstanceOf(ScopedExpression.Application.class, forward.value());
        assertReference(forwardApply.function(), 1); assertCall(forwardApply.argument(), "mathmod:look_vector", Map.of("player", 0));
        var lift = assertInstanceOf(ScopedExpression.Let.class, forward.body());
        assertEquals("lift", lift.nameHint()); var liftApply = assertInstanceOf(ScopedExpression.Application.class, lift.value());
        assertReference(liftApply.function(), 2); assertCall(liftApply.argument(), "mathmod:vector_from_numbers", Map.of("x", "0", "y", "1", "z", "0"));
        assertCall(lift.body(), "mathmod:push_self", Map.of("player", 2, "vector", "vector_add"));

        assertEquals(List.of("mathmod:scale_vector"), runeIds(lambda.body()));
        assertEquals(1, runeIds(FactoredLeapTheorem.source().expression()).stream().filter("mathmod:push_self"::equals).count());
        assertTrue(runeIds(FactoredLeapTheorem.source().expression()).contains("mathmod:look_vector"), "observation is outside the pure lambda");
    }

    @Test void frozenGraphHasExactRuneCountsConstantsSocketsAndSharedSelf() {
        var graph = FactoredLeapTheorem.presentationGraph();
        assertEquals(12, graph.nodes().size()); assertEquals(12, graph.edges().size()); assertEquals("push", graph.outputNodeId());
        assertEquals(Map.of("mathmod:self_player", 1L, "mathmod:look_vector", 1L, "mathmod:constant_number", 5L,
                "mathmod:scale_vector", 2L, "mathmod:vector_from_numbers", 1L, "mathmod:vector_add", 1L, "mathmod:push_self", 1L),
                graph.nodes().stream().collect(java.util.stream.Collectors.groupingBy(node -> node.runeId(), java.util.stream.Collectors.counting())));
        assertEquals(List.of("0", "0", "0.5", "0.5", "1"), graph.nodes().stream().filter(node -> node.runeId().equals("mathmod:constant_number")).map(node -> node.constants().get("value")).sorted().toList());
        assertEquals(java.util.Set.of("self->look:player", "self->push:player", "look->scaledLook:vector", "halfLook->scaledLook:factor",
                "x->up:x", "y->up:y", "z->up:z", "up->scaledUp:vector", "halfUp->scaledUp:factor",
                "scaledLook->sum:a", "scaledUp->sum:b", "sum->push:vector"),
                graph.edges().stream().map(edge -> edge.fromNodeId()+"->"+edge.toNodeId()+":"+edge.inputName()).collect(java.util.stream.Collectors.toSet()));
    }

    @Test void schemaOneWireRoundTripPreservesTheExactFixture() {
        var encoded = ScopedSourceWireCodec.encode(FactoredLeapTheorem.source());
        assertEquals(1, encoded.schemaVersion());
        var decoded = ScopedSourceWireCodec.decode(encoded);
        assertEquals(com.mathmod.program.ScopedSourceRead.Status.CURRENT_VALID, decoded.status());
        assertEquals(FactoredLeapTheorem.source(), decoded.source().orElseThrow());
        assertEquals(encoded, ScopedSourceWireCodec.encode(decoded.source().orElseThrow()));
    }

    @Test void semanticMatcherRejectsAdversarialGraphsWhileIgnoringNamesAndOrder() {
        MathModRuneBootstrap.bootstrap();
        var compiled = new ScopedProgramCompiler(MathModRuneBootstrap.registry()).compile(FactoredLeapTheorem.source()).graph().orElseThrow();
        var renamed = renamedAndReordered(compiled);
        assertDoesNotThrow(() -> assertSemanticIsomorphism(renamed, FactoredLeapTheorem.presentationGraph()), "renaming every generated id and reordering both collections is non-semantic");

        assertThrows(AssertionError.class, () -> assertSemanticIsomorphism(withoutLastNode(compiled), FactoredLeapTheorem.presentationGraph()), "a missing node must fail");
        assertThrows(AssertionError.class, () -> assertSemanticIsomorphism(withExtraNode(compiled), FactoredLeapTheorem.presentationGraph()), "an extra node must fail");
        assertThrows(AssertionError.class, () -> assertSemanticIsomorphism(withoutLastEdge(compiled), FactoredLeapTheorem.presentationGraph()), "a missing edge must fail");
        assertThrows(AssertionError.class, () -> assertSemanticIsomorphism(withExtraEdge(compiled), FactoredLeapTheorem.presentationGraph()), "an extra edge must fail");
        assertThrows(AssertionError.class, () -> assertSemanticIsomorphism(withChangedNumber(compiled), FactoredLeapTheorem.presentationGraph()), "a changed NUMBER value must fail");
        assertThrows(AssertionError.class, () -> assertSemanticIsomorphism(withChangedSocket(compiled), FactoredLeapTheorem.presentationGraph()), "a changed named socket must fail");
        assertThrows(AssertionError.class, () -> assertSemanticIsomorphism(new com.mathmod.runes.ProgramGraph(compiled.nodes(), compiled.edges(), compiled.nodes().get(0).id(), compiled.budgetLimit()), FactoredLeapTheorem.presentationGraph()), "a changed output must fail");
        assertThrows(AssertionError.class, () -> assertSemanticIsomorphism(withExtraSelf(compiled), FactoredLeapTheorem.presentationGraph()), "duplicated self_player must fail");
    }

    private static void assertReference(ScopedExpression expression, int index) { assertEquals(new ScopedExpression.ParameterReference(index), expression); }
    private static void assertCall(ScopedExpression expression, String id, Map<String, ?> expected) {
        assertTrue(expression instanceof ScopedExpression.RuneCall, () -> "expected " + id + " but was " + expression);
        var call = (ScopedExpression.RuneCall) expression; assertEquals(id, call.runeId()); assertEquals(expected.keySet(), call.arguments().stream().map(ScopedExpression.Argument::inputName).collect(java.util.stream.Collectors.toSet()));
        for (var argument : call.arguments()) {
            Object value = expected.get(argument.inputName());
            if (value instanceof Integer index) assertReference(argument.expression(), index);
            else if (value instanceof String text && text.equals("vector_add")) assertCall(argument.expression(), "mathmod:vector_add", Map.of("a", 1, "b", 0));
            else assertEquals(new ScopedExpression.Literal(RuneTypeExpression.value(RuneType.NUMBER), String.valueOf(value)), argument.expression());
        }
    }
    private static List<String> runeIds(ScopedExpression expression) {
        if (expression instanceof ScopedExpression.RuneCall call) return java.util.stream.Stream.concat(java.util.stream.Stream.of(call.runeId()), call.arguments().stream().flatMap(argument -> runeIds(argument.expression()).stream())).toList();
        if (expression instanceof ScopedExpression.Lambda value) return runeIds(value.body());
        if (expression instanceof ScopedExpression.Application value) return java.util.stream.Stream.concat(runeIds(value.function()).stream(), runeIds(value.argument()).stream()).toList();
        if (expression instanceof ScopedExpression.Let value) return java.util.stream.Stream.concat(runeIds(value.value()).stream(), runeIds(value.body()).stream()).toList();
        return List.of();
    }
    private static void assertSemanticIsomorphism(com.mathmod.runes.ProgramGraph compiled, com.mathmod.runes.ProgramGraph presentation) {
        assertEquals(presentation.nodes().size(), compiled.nodes().size(), "node count");
        assertEquals(presentation.edges().size(), compiled.edges().size(), "edge count");
        assertEquals(1L, compiled.nodes().stream().filter(node -> node.runeId().equals("mathmod:self_player")).count(), "compiled self_player must be shared exactly once");
        assertEquals(1L, presentation.nodes().stream().filter(node -> node.runeId().equals("mathmod:self_player")).count(), "presentation self_player must be shared exactly once");
        assertTrue(findBijection(compiled, presentation, 0, new java.util.LinkedHashMap<>(), new java.util.HashSet<>()), "no id- and order-independent semantic bijection exists");
    }

    private static boolean findBijection(com.mathmod.runes.ProgramGraph compiled, com.mathmod.runes.ProgramGraph presentation, int index, Map<String, String> mapping, java.util.Set<String> usedPresentationIds) {
        if (index == compiled.nodes().size()) return mappedEdges(compiled, mapping).equals(edgeSet(presentation)) && mapping.get(compiled.outputNodeId()).equals(presentation.outputNodeId());
        var compiledNode = compiled.nodes().get(index);
        for (var presentationNode : presentation.nodes()) {
            if (usedPresentationIds.contains(presentationNode.id()) || !sameSemanticNode(compiledNode, presentationNode)) continue;
            mapping.put(compiledNode.id(), presentationNode.id()); usedPresentationIds.add(presentationNode.id());
            if (findBijection(compiled, presentation, index + 1, mapping, usedPresentationIds)) return true;
            mapping.remove(compiledNode.id()); usedPresentationIds.remove(presentationNode.id());
        }
        return false;
    }

    private static boolean sameSemanticNode(com.mathmod.runes.ProgramNode actual, com.mathmod.runes.ProgramNode expected) {
        if (!actual.runeId().equals(expected.runeId()) || !actual.constants().keySet().equals(expected.constants().keySet())) return false;
        for (String key : expected.constants().keySet()) {
            String actualValue = actual.constants().get(key), expectedValue = expected.constants().get(key);
            if (expected.runeId().equals("mathmod:constant_number") && key.equals("value")) {
                try {
                    double actualNumber = Double.parseDouble(actualValue), expectedNumber = Double.parseDouble(expectedValue);
                    if (!Double.isFinite(actualNumber) || !Double.isFinite(expectedNumber) || Double.compare(actualNumber, expectedNumber) != 0) return false;
                } catch (NumberFormatException ignored) { return false; }
            } else if (!expectedValue.equals(actualValue)) return false;
        }
        return true;
    }

    private static java.util.Set<String> mappedEdges(com.mathmod.runes.ProgramGraph graph, Map<String, String> mapping) {
        var result = new java.util.HashSet<String>();
        for (var edge : graph.edges()) {
            String from = mapping.get(edge.fromNodeId()), to = mapping.get(edge.toNodeId());
            if (from == null || to == null) return java.util.Set.of();
            if (!result.add(from + "->" + to + ":" + edge.inputName())) return java.util.Set.of();
        }
        return result;
    }
    private static java.util.Set<String> edgeSet(com.mathmod.runes.ProgramGraph graph) {
        var result = new java.util.HashSet<String>();
        for (var edge : graph.edges()) if (!result.add(edge.fromNodeId() + "->" + edge.toNodeId() + ":" + edge.inputName())) return java.util.Set.of();
        return result;
    }
    private static com.mathmod.runes.ProgramGraph renamedAndReordered(com.mathmod.runes.ProgramGraph graph) {
        Map<String, String> names = new java.util.LinkedHashMap<>(); for (int index = 0; index < graph.nodes().size(); index++) names.put(graph.nodes().get(index).id(), "renamed_" + index);
        var nodes = graph.nodes().stream().map(node -> new com.mathmod.runes.ProgramNode(names.get(node.id()), node.runeId(), node.constants())).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new)); java.util.Collections.reverse(nodes);
        var edges = graph.edges().stream().map(edge -> new com.mathmod.runes.ProgramEdge(names.get(edge.fromNodeId()), names.get(edge.toNodeId()), edge.inputName())).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new)); java.util.Collections.reverse(edges);
        return new com.mathmod.runes.ProgramGraph(nodes, edges, names.get(graph.outputNodeId()), graph.budgetLimit());
    }
    private static com.mathmod.runes.ProgramGraph withoutLastNode(com.mathmod.runes.ProgramGraph graph) { return new com.mathmod.runes.ProgramGraph(graph.nodes().subList(0, graph.nodes().size() - 1), graph.edges(), graph.outputNodeId(), graph.budgetLimit()); }
    private static com.mathmod.runes.ProgramGraph withExtraNode(com.mathmod.runes.ProgramGraph graph) { var nodes = new java.util.ArrayList<>(graph.nodes()); nodes.add(new com.mathmod.runes.ProgramNode("extra", "mathmod:constant_number", Map.of("value", "7"))); return new com.mathmod.runes.ProgramGraph(nodes, graph.edges(), graph.outputNodeId(), graph.budgetLimit()); }
    private static com.mathmod.runes.ProgramGraph withoutLastEdge(com.mathmod.runes.ProgramGraph graph) { return new com.mathmod.runes.ProgramGraph(graph.nodes(), graph.edges().subList(0, graph.edges().size() - 1), graph.outputNodeId(), graph.budgetLimit()); }
    private static com.mathmod.runes.ProgramGraph withExtraEdge(com.mathmod.runes.ProgramGraph graph) { var edges = new java.util.ArrayList<>(graph.edges()); edges.add(new com.mathmod.runes.ProgramEdge(graph.nodes().get(0).id(), graph.nodes().get(1).id(), "extra")); return new com.mathmod.runes.ProgramGraph(graph.nodes(), edges, graph.outputNodeId(), graph.budgetLimit()); }
    private static com.mathmod.runes.ProgramGraph withChangedNumber(com.mathmod.runes.ProgramGraph graph) { var nodes = graph.nodes().stream().map(node -> node.runeId().equals("mathmod:constant_number") ? new com.mathmod.runes.ProgramNode(node.id(), node.runeId(), Map.of("value", "99")) : node).toList(); return new com.mathmod.runes.ProgramGraph(nodes, graph.edges(), graph.outputNodeId(), graph.budgetLimit()); }
    private static com.mathmod.runes.ProgramGraph withChangedSocket(com.mathmod.runes.ProgramGraph graph) { var edges = new java.util.ArrayList<>(graph.edges()); var edge = edges.get(0); edges.set(0, new com.mathmod.runes.ProgramEdge(edge.fromNodeId(), edge.toNodeId(), "changed_socket")); return new com.mathmod.runes.ProgramGraph(graph.nodes(), edges, graph.outputNodeId(), graph.budgetLimit()); }
    private static com.mathmod.runes.ProgramGraph withExtraSelf(com.mathmod.runes.ProgramGraph graph) { var nodes = new java.util.ArrayList<>(graph.nodes()); nodes.add(new com.mathmod.runes.ProgramNode("duplicate_self", "mathmod:self_player")); return new com.mathmod.runes.ProgramGraph(nodes, graph.edges(), graph.outputNodeId(), graph.budgetLimit()); }
}
