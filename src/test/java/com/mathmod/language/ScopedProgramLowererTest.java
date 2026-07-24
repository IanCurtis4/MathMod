package com.mathmod.language;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.RuneType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopedProgramLowererTest {
    @Test
    void lowersLetBoundFunctionWithSharedArgumentNode() {
        ScopedExpression twice = new ScopedExpression.Lambda("x", RuneTypeExpression.value(RuneType.NUMBER),
                new ScopedExpression.RuneCall("test:add", List.of(
                        new ScopedExpression.Argument("left", new ScopedExpression.ParameterReference(0)),
                        new ScopedExpression.Argument("right", new ScopedExpression.ParameterReference(0))
                )));
        ScopedExpression program = new ScopedExpression.Let("twice", twice,
                new ScopedExpression.RuneCall("test:emit", List.of(
                        new ScopedExpression.Argument("value", new ScopedExpression.Application(
                                new ScopedExpression.ParameterReference(0), ScopedTypeCheckerTest.number("4")
                        ))
                )));

        ScopedLoweringResult result = new ScopedProgramLowerer(ScopedTypeCheckerTest.registry()).lower(
                ScopedTypeCheckerTest.source(program, RuneType.UNIT));

        assertTrue(result.valid(), () -> result.issues().toString());
        ProgramGraph graph = result.graph().orElseThrow();
        assertEquals(3, graph.nodes().size());
        assertEquals(3, graph.edges().size());
        assertEquals(graph.edges().get(0).fromNodeId(), graph.edges().get(1).fromNodeId());
        assertEquals("test:emit", graph.nodes().stream()
                .filter(node -> node.id().equals(graph.outputNodeId())).findFirst().orElseThrow().runeId());
    }
}
