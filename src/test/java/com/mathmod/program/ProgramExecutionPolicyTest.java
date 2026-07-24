package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RuneType;
import com.mathmod.runes.ValidationIssue;
import com.mathmod.runes.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramExecutionPolicyTest {
    @Test
    void executableProgramsMustReturnUnit() {
        RuneRegistry registry = new RuneRegistry();
        registry.register(RuneDefinition.builder("test:number")
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("constant_number")
                .build());
        ProgramGraph graph = new ProgramGraph(
                List.of(new ProgramNode("number", "test:number")),
                List.of(),
                "number",
                10
        );

        ValidationResult result = ProgramExecutionPolicy.validateExecutable(graph, registry);

        assertFalse(result.valid());
        assertTrue(hasMessageContaining(result, "must output unit"));
        ValidationIssue issue = result.issues().stream()
                .filter(candidate -> candidate.messageKey().equals("validation.mathmod.output_not_unit"))
                .findFirst()
                .orElseThrow();
        assertEquals(List.of("unit", "number"), issue.messageArguments());
    }

    @Test
    void executableProgramsRejectUnsupportedExecutorKeys() {
        RuneRegistry registry = new RuneRegistry();
        registry.register(RuneDefinition.builder("test:future")
                .output(RuneType.UNIT)
                .budgetCost(1)
                .executorKey("future_javascript_runtime")
                .build());
        ProgramGraph graph = new ProgramGraph(
                List.of(new ProgramNode("future", "test:future")),
                List.of(),
                "future",
                10
        );

        ValidationResult result = ProgramExecutionPolicy.validateExecutable(graph, registry);

        assertFalse(result.valid());
        assertTrue(hasMessageContaining(result, "unsupported executor key"));
    }

    @Test
    void executableProgramsCanUseResourceBudgetBonus() {
        RuneRegistry registry = new RuneRegistry();
        registry.register(RuneDefinition.builder("test:expensive_unit")
                .output(RuneType.UNIT)
                .budgetCost(12)
                .executorKey("debug_marker")
                .build());
        ProgramGraph graph = new ProgramGraph(
                List.of(new ProgramNode("unit", "test:expensive_unit")),
                List.of(),
                "unit",
                5
        );

        assertFalse(ProgramExecutionPolicy.validateExecutable(graph, registry).valid());
        assertTrue(ProgramExecutionPolicy.validateExecutable(graph, registry, 7).valid());
    }

    @Test
    void defensiveAlchemyMustFeedTheFinalExecutionRuneDirectly() {
        RuneRegistry registry = defensiveRegistry();
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("plan", "test:resistance"),
                        new ProgramNode("execute", "test:execute")
                ),
                List.of(),
                "execute",
                10
        );

        ValidationResult result = ProgramExecutionPolicy.validateExecutable(graph, registry);

        assertFalse(result.valid());
        assertTrue(result.issues().stream().anyMatch(issue ->
                issue.messageKey().equals("validation.mathmod.p9_defensive_shape")));
    }

    @Test
    void directDefensiveAlchemyPlanIsExecutable() {
        RuneRegistry registry = defensiveRegistry();
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("plan", "test:resistance"),
                        new ProgramNode("execute", "test:execute")
                ),
                List.of(new com.mathmod.runes.ProgramEdge("plan", "execute", "plan")),
                "execute",
                10
        );

        assertTrue(ProgramExecutionPolicy.validateExecutable(graph, registry).valid());
    }

    private static RuneRegistry defensiveRegistry() {
        RuneRegistry registry = new RuneRegistry();
        registry.register(RuneDefinition.builder("test:resistance")
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(1)
                .executorKey("resistance_entities_plan")
                .build());
        registry.register(RuneDefinition.builder("test:execute")
                .input("plan", RuneType.EFFECT_PLAN)
                .output(RuneType.UNIT)
                .budgetCost(1)
                .executorKey("execute_effect_plan")
                .build());
        return registry;
    }

    private static boolean hasMessageContaining(ValidationResult result, String text) {
        return result.issues().stream().anyMatch(issue -> issue.message().contains(text));
    }
}
