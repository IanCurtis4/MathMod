package com.mathmod.runes;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramValidatorTest {
    private final RuneRegistry registry = registry();
    private final ProgramValidator validator = new ProgramValidator(registry);

    @Test
    void acceptsValidTypedDag() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "test:self"),
                        new ProgramNode("number", "test:number"),
                        new ProgramNode("vector", "test:vector"),
                        new ProgramNode("move", "test:move")
                ),
                List.of(
                        new ProgramEdge("number", "vector", "x"),
                        new ProgramEdge("number", "vector", "y"),
                        new ProgramEdge("number", "vector", "z"),
                        new ProgramEdge("self", "move", "player"),
                        new ProgramEdge("vector", "move", "vector")
                ),
                "move",
                10
        );

        ValidationResult result = validator.validate(graph);

        assertTrue(result.valid());
        assertEquals(7, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void rejectsTypeMismatch() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("number", "test:number"),
                        new ProgramNode("move", "test:move")
                ),
                List.of(new ProgramEdge("number", "move", "vector")),
                "move",
                10
        );

        ValidationResult result = validator.validate(graph);

        assertFalse(result.valid());
        assertTrue(hasMessageContaining(result, "Type mismatch"));
        ValidationIssue issue = result.issues().stream()
                .filter(candidate -> candidate.messageKey().equals("validation.mathmod.type_mismatch"))
                .findFirst()
                .orElseThrow();
        assertEquals(List.of("vector", "vec3", "number"), issue.messageArguments());
    }

    @Test
    void rejectsCycles() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("a", "test:number_passthrough"),
                        new ProgramNode("b", "test:number_passthrough")
                ),
                List.of(
                        new ProgramEdge("a", "b", "value"),
                        new ProgramEdge("b", "a", "value")
                ),
                "b",
                10
        );

        ValidationResult result = validator.validate(graph);

        assertFalse(result.valid());
        assertTrue(hasMessageContaining(result, "cycle"));
    }

    @Test
    void rejectsBudgetOverflow() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("self", "test:self"),
                        new ProgramNode("number", "test:number"),
                        new ProgramNode("vector", "test:vector"),
                        new ProgramNode("move", "test:move")
                ),
                List.of(
                        new ProgramEdge("number", "vector", "x"),
                        new ProgramEdge("number", "vector", "y"),
                        new ProgramEdge("number", "vector", "z"),
                        new ProgramEdge("self", "move", "player"),
                        new ProgramEdge("vector", "move", "vector")
                ),
                "move",
                3
        );

        ValidationResult result = validator.validate(graph);

        assertFalse(result.valid());
        assertTrue(hasMessageContaining(result, "exceeds limit"));
    }

    @Test
    void rejectsGraphWithoutOutputNode() {
        ProgramGraph graph = new ProgramGraph(
                List.of(new ProgramNode("number", "test:number", Map.of("value", "1"))),
                List.of(),
                "",
                10
        );

        ValidationResult result = validator.validate(graph);

        assertFalse(result.valid());
        assertTrue(hasMessageContaining(result, "output node"));
        assertTrue(result.issues().stream().anyMatch(
                issue -> issue.messageKey().equals("validation.mathmod.output_required")
        ));
    }

    @Test
    void rejectsTooManyNodes() {
        List<ProgramNode> nodes = java.util.stream.IntStream.range(0, ProgramValidator.MAX_NODES + 1)
                .mapToObj(index -> new ProgramNode("n" + index, "test:number"))
                .toList();
        ProgramGraph graph = new ProgramGraph(nodes, List.of(), "n0", 10);

        ValidationResult result = validator.validate(graph);

        assertFalse(result.valid());
        assertTrue(hasMessageContaining(result, "maximum is " + ProgramValidator.MAX_NODES));
    }

    @Test
    void rejectsExcessiveBudgetLimit() {
        ProgramGraph graph = new ProgramGraph(
                List.of(new ProgramNode("number", "test:number")),
                List.of(),
                "number",
                ProgramValidator.MAX_BUDGET_LIMIT + 1
        );

        ValidationResult result = validator.validate(graph);

        assertFalse(result.valid());
        assertTrue(hasMessageContaining(result, "exceeds maximum"));
    }

    @Test
    void requiresExplicitProjectionFromAnAttributeField() {
        ProgramGraph graph = new ProgramGraph(
                List.of(
                        new ProgramNode("field", "test:attribute_field"),
                        new ProgramNode("gradient", "test:gradient")
                ),
                List.of(new ProgramEdge("field", "gradient", "field")),
                "gradient",
                10
        );

        ValidationResult result = validator.validate(graph);

        assertFalse(result.valid());
        assertTrue(result.issues().stream().anyMatch(
                issue -> issue.messageKey().equals("validation.mathmod.type_mismatch")
        ));
    }

    private static boolean hasMessageContaining(ValidationResult result, String text) {
        return result.issues().stream().anyMatch(issue -> issue.message().contains(text));
    }

    private static RuneRegistry registry() {
        RuneRegistry registry = new RuneRegistry();
        registry.register(RuneDefinition.builder("test:number")
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .build());
        registry.register(RuneDefinition.builder("test:number_passthrough")
                .input("value", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .build());
        registry.register(RuneDefinition.builder("test:self")
                .output(RuneType.PLAYER)
                .budgetCost(1)
                .build());
        registry.register(RuneDefinition.builder("test:attribute_field")
                .output(RuneType.ATTRIBUTE_FIELD)
                .budgetCost(1)
                .build());
        registry.register(RuneDefinition.builder("test:gradient")
                .input("field", RuneType.SCALAR_FIELD)
                .output(RuneType.VEC3)
                .budgetCost(1)
                .build());
        registry.register(RuneDefinition.builder("test:vector")
                .input("x", RuneType.NUMBER)
                .input("y", RuneType.NUMBER)
                .input("z", RuneType.NUMBER)
                .output(RuneType.VEC3)
                .budgetCost(2)
                .build());
        registry.register(RuneDefinition.builder("test:move")
                .input("player", RuneType.PLAYER)
                .input("vector", RuneType.VEC3)
                .output(RuneType.UNIT)
                .budgetCost(3)
                .build());
        return registry;
    }
}
