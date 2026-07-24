package com.mathmod.program;

import com.mathmod.runes.BuiltInRunes;
import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RunePurity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramNormalizerTest {
    @Test
    void foldsClosedPureSubgraphsButStopsAtWorldObservations() {
        RuneRegistry registry = registry();
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("two", "mathmod:constant_number", Map.of("value", "2")),
                        new ProgramNode("three", "mathmod:constant_number", Map.of("value", "3")),
                        new ProgramNode("gain", "mathmod:number_multiply"),
                        new ProgramNode("self", "mathmod:self_player"),
                        new ProgramNode("look", "mathmod:look_vector"),
                        new ProgramNode("scaled", "mathmod:scale_vector")
                ),
                List.of(
                        new ProgramEdge("two", "gain", "a"),
                        new ProgramEdge("three", "gain", "b"),
                        new ProgramEdge("self", "look", "player"),
                        new ProgramEdge("look", "scaled", "vector"),
                        new ProgramEdge("gain", "scaled", "factor")
                ),
                "scaled",
                16
        );

        ProgramNormalization result = ProgramNormalizer.normalize(graph, registry);

        assertEquals(3, result.normalizedNodeCount());
        assertEquals(new NormalizedValue.NumberValue(6.0D), result.value("gain").orElseThrow());
        assertTrue(result.value("self").isEmpty());
        assertTrue(result.value("look").isEmpty());
        assertTrue(result.value("scaled").isEmpty());
    }

    @Test
    void leavesInvalidClosedExpressionsForNormalRuntimeDiagnostics() {
        RuneRegistry registry = registry();
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("one", "mathmod:constant_number", Map.of("value", "1")),
                        new ProgramNode("zero", "mathmod:constant_number", Map.of("value", "0")),
                        new ProgramNode("division", "mathmod:number_divide")
                ),
                List.of(
                        new ProgramEdge("one", "division", "a"),
                        new ProgramEdge("zero", "division", "b")
                ),
                "division",
                8
        );

        ProgramNormalization result = ProgramNormalizer.normalize(graph, registry);

        assertEquals(2, result.normalizedNodeCount());
        assertTrue(result.value("division").isEmpty());
    }

    private static RuneRegistry registry() {
        RuneRegistry registry = new RuneRegistry();
        BuiltInRunes.registerAll(registry);
        return registry;
    }

    @Test
    void unknownExecutorsDefaultToEffectSemantics() {
        assertEquals(RunePurity.EFFECT, RunePurity.infer("pack:arbitrary_executor"));
        assertEquals(RunePurity.PURE, RunePurity.infer("number_multiply"));
        assertEquals(RunePurity.OBSERVATION, RunePurity.infer("entity_velocities"));
    }
}
