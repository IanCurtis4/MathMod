package com.mathmod.language;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.RuneType;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RunePurity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopedProgramCompilerTest {
    @Test
    void explicitLetSharesObservationButRepeatedTermsDoNotUseCse() {
        ScopedExpression observation = new ScopedExpression.RuneCall("test:observe", List.of());
        ScopedExpression shared = new ScopedExpression.Let("x", observation, add(new ScopedExpression.ParameterReference(0), new ScopedExpression.ParameterReference(0)));
        ScopedCompileResult sharedResult = compiler().compile(source(shared, RuneType.NUMBER));
        assertTrue(sharedResult.valid(), () -> sharedResult.issues().toString());
        ProgramGraph sharedGraph = sharedResult.graph().orElseThrow();
        assertEquals(1, nodes(sharedGraph, "test:observe"));
        assertEquals(2, edgesTo(sharedGraph, "test:add"));

        ScopedCompileResult repeated = compiler().compile(source(add(observation, observation), RuneType.NUMBER));
        assertTrue(repeated.valid(), () -> repeated.issues().toString());
        assertEquals(2, nodes(repeated.graph().orElseThrow(), "test:observe"), "L0 must not CSE repeated observations");
    }

    @Test
    void observationSharingVectorsOneThreeFiveAndSixAreExplicitOnly() {
        ScopedExpression observation = new ScopedExpression.RuneCall("test:observe", List.of());
        ScopedExpression applied = new ScopedExpression.Application(
                new ScopedExpression.Lambda("x", RuneTypeExpression.value(RuneType.NUMBER), add(new ScopedExpression.ParameterReference(0), new ScopedExpression.ParameterReference(0))), observation);
        assertShared(compiler().compile(source(applied, RuneType.NUMBER)), "OBS-SHARE-1");

        ScopedExpression nested = new ScopedExpression.Application(
                new ScopedExpression.Lambda("x", RuneTypeExpression.value(RuneType.NUMBER), new ScopedExpression.Let("y", new ScopedExpression.ParameterReference(0), add(new ScopedExpression.ParameterReference(0), new ScopedExpression.ParameterReference(1)))), observation);
        assertShared(compiler().compile(source(nested, RuneType.NUMBER)), "OBS-SHARE-3");

        ScopedExpression literal = new ScopedExpression.Let("n", ScopedTypeCheckerTest.number("1"), add(new ScopedExpression.ParameterReference(0), new ScopedExpression.ParameterReference(0)));
        ScopedCompileResult literalResult = compiler().compile(source(literal, RuneType.NUMBER));
        assertTrue(literalResult.valid(), () -> "OBS-SHARE-5 " + literalResult.issues());
        assertEquals(1, nodes(literalResult.graph().orElseThrow(), "mathmod:constant_number"));
        assertEquals(2, edgesTo(literalResult.graph().orElseThrow(), "test:add"), "OBS-SHARE-5 two sockets");

        ScopedExpression impureLambda = new ScopedExpression.Application(new ScopedExpression.Lambda("x", RuneTypeExpression.value(RuneType.NUMBER), observation), ScopedTypeCheckerTest.number("1"));
        ScopedCompileResult rejected = compiler().compile(source(impureLambda, RuneType.NUMBER));
        assertTrue(rejected.graph().isEmpty(), "OBS-SHARE-6 no graph");
        assertTrue(rejected.issues().stream().anyMatch(i -> i.code() == ScopedLanguageIssue.Code.IMPURE_LAMBDA_BODY), "OBS-SHARE-6 impurity");
    }

    @Test
    void tailOneTwoEightAndBoundEightHavePureCompilerClassifications() {
        ScopedExpression terminal = new ScopedExpression.RuneCall("test:emit", List.of(new ScopedExpression.Argument("value", add(ScopedTypeCheckerTest.number("1"), ScopedTypeCheckerTest.number("2")))));
        assertTrue(compiler().compile(source(terminal, RuneType.UNIT)).valid(), "TAIL-1 terminal Unit candidate");
        ScopedExpression observedTail = new ScopedExpression.Let("o", new ScopedExpression.RuneCall("test:observe", List.of()), new ScopedExpression.RuneCall("test:emit", List.of(new ScopedExpression.Argument("value", new ScopedExpression.ParameterReference(0)))));
        ScopedCompileResult tailResult = compiler().compile(source(observedTail, RuneType.UNIT));
        assertTrue(tailResult.valid(), "TAIL-2 bound observation feeds effect");
        ProgramGraph tailGraph = tailResult.graph().orElseThrow();
        assertEquals(1, nodes(tailGraph, "test:observe"), "TAIL-2 one observation");
        assertEquals(1, nodes(tailGraph, "test:emit"), "TAIL-2 one effect");
        assertTrue(tailGraph.edges().stream().anyMatch(edge -> edge.inputName().equals("value") && tailGraph.nodes().stream().anyMatch(node -> node.id().equals(edge.fromNodeId()) && node.runeId().equals("test:observe")) && tailGraph.nodes().stream().anyMatch(node -> node.id().equals(edge.toNodeId()) && node.runeId().equals("test:emit"))), "TAIL-2 observation feeds effect value");
        assertEquals("test:emit", tailGraph.nodes().stream().filter(node -> node.id().equals(tailGraph.outputNodeId())).findFirst().orElseThrow().runeId(), "TAIL-2 effect output");
        assertTrue(compiler().compile(source(ScopedTypeCheckerTest.number("1"), RuneType.NUMBER)).valid(), "TAIL-8 only a pure candidate; no admission claim");
        ScopedCompileResult future = compiler().compile(source(new ScopedExpression.RuneCall("future:combinator", List.of()), RuneType.NUMBER));
        assertTrue(future.graph().isEmpty(), "BOUND-8 no combinator path");
        assertTrue(future.issues().stream().anyMatch(i -> i.code() == ScopedLanguageIssue.Code.UNKNOWN_RUNE), "BOUND-8 fail closed");
    }

    @Test
    void numberResolutionCanonicalizesNegativeZeroAndFailsClosedForInvalidOrUnsupportedLiterals() {
        ScopedCompileResult zero = compiler().compile(source(ScopedTypeCheckerTest.number("-0.0"), RuneType.NUMBER));
        assertTrue(zero.valid(), () -> zero.issues().toString());
        assertEquals("0.0", zero.graph().orElseThrow().nodes().getFirst().constants().get("value"));

        assertLiteralFailure(ScopedTypeCheckerTest.number("0x1.0p0"), ScopedLanguageIssue.Code.LITERAL_INVALID);
        assertLiteralFailure(ScopedTypeCheckerTest.number("NaN"), ScopedLanguageIssue.Code.LITERAL_INVALID);
        assertLiteralFailure(new ScopedExpression.Literal(RuneTypeExpression.value(RuneType.BOOL), "true"), ScopedLanguageIssue.Code.LITERAL_UNSUPPORTED);
    }

    @Test
    void budgetFailsAtTheFirstChargePastItsLimit() {
        ScopedCompileBudget budget = new ScopedCompileBudget();
        for (int index = 0; index < ScopedLanguageLimits.MAX_EVALUATION_STEPS; index++) {
            budget.charge(ScopedCompileBudget.Event.STRUCTURAL_NODE);
        }
        try {
            budget.charge(ScopedCompileBudget.Event.TYPE_NODE);
        } catch (ScopedCompileBudget.LimitExceeded expected) {
            assertEquals(ScopedLanguageLimits.MAX_EVALUATION_STEPS, budget.chargedSteps());
            return;
        }
        throw new AssertionError("the first charge past the limit must fail");
    }

    @Test
    void graphValidationFailureNeverReturnsAPartialGraph() {
        ScopedExpression expression = ScopedTypeCheckerTest.number("1");
        for (int index = 0; index < 64; index++) {
            expression = add(expression, ScopedTypeCheckerTest.number("1"));
        }
        ScopedCompileResult result = compiler().compile(source(expression, RuneType.NUMBER));
        assertFalse(result.valid());
        assertTrue(result.graph().isEmpty());
        assertTrue(result.issues().stream().anyMatch(issue -> issue.code() == ScopedLanguageIssue.Code.LOWERED_GRAPH_INVALID));
    }

    @Test
    void publicCheckerAndLowererCreateFreshMetersForEveryAttempt() {
        ScopedProgramSource source = source(identityApplications(64), RuneType.NUMBER);
        ScopedTypeChecker checker = new ScopedTypeChecker(ScopedTypeCheckerTest.registry());
        for (int index = 0; index < 11; index++) assertTrue(checker.check(source).valid(), "fresh checker attempt " + index);
        ScopedProgramLowerer lowerer = new ScopedProgramLowerer(ScopedTypeCheckerTest.registry());
        for (int index = 0; index < 7; index++) assertTrue(lowerer.lower(source).valid(), "fresh lowerer attempt " + index);
    }

    @Test
    void literalDescriptorRequiresPureConstantNumberExecutor() {
        assertDescriptorFailure(RuneDefinition.builder("mathmod:constant_number")
                .output(RuneType.NUMBER).purity(RunePurity.EFFECT).executorKey("constant_number").build());
        assertDescriptorFailure(RuneDefinition.builder("mathmod:constant_number")
                .output(RuneType.NUMBER).purity(RunePurity.PURE).executorKey("other").build());
    }

    @Test
    void numberGrammarAndDiagnosticNormalizationAreFrozen() {
        assertCanonical("1", "1.0");
        assertCanonical("+1.5", "1.5");
        assertCanonical(".5", "0.5");
        assertCanonical("1e2", "100.0");
        for (String rejected : List.of("Infinity", "1e999", "1,0", "1m", "1x", "1.0tail")) {
            assertLiteralFailure(ScopedTypeCheckerTest.number(rejected), ScopedLanguageIssue.Code.LITERAL_INVALID);
        }
        assertLiteralFailure(ScopedTypeCheckerTest.number(" 1"), ScopedLanguageIssue.Code.LITERAL_INVALID);
        assertLiteralFailure(ScopedTypeCheckerTest.number("1 "), ScopedLanguageIssue.Code.LITERAL_INVALID);
        assertLiteralFailure(ScopedTypeCheckerTest.number("x".repeat(161)), ScopedLanguageIssue.Code.LITERAL_LIMIT);
        List<ScopedLanguageIssue> issues = ScopedLanguageIssue.normalize(List.of(
                new ScopedLanguageIssue(ScopedLanguageIssue.Code.TYPE_MISMATCH, "$.arguments[10]", "later"),
                new ScopedLanguageIssue(ScopedLanguageIssue.Code.TYPE_MISMATCH, "$.arguments[2]", "first"),
                new ScopedLanguageIssue(ScopedLanguageIssue.Code.TYPE_MISMATCH, "$.arguments[2]", "duplicate")
        ));
        assertEquals(2, issues.size());
        assertEquals("$.arguments[2]", issues.getFirst().path());
        List<ScopedLanguageIssue> ordered = ScopedLanguageIssue.normalize(List.of(
                new ScopedLanguageIssue(ScopedLanguageIssue.Code.AST_LIMIT, "$.arguments[2]", "phase one"),
                new ScopedLanguageIssue(ScopedLanguageIssue.Code.LOWERED_GRAPH_INVALID, "$.arguments[2]", "phase four"),
                new ScopedLanguageIssue(ScopedLanguageIssue.Code.LITERAL_INVALID, "$.arguments[2]", "phase three"),
                new ScopedLanguageIssue(ScopedLanguageIssue.Code.TYPE_MISMATCH, "$.arguments[2]", "z"),
                new ScopedLanguageIssue(ScopedLanguageIssue.Code.DISABLED_RUNE, "$.arguments[2]", "a"),
                new ScopedLanguageIssue(ScopedLanguageIssue.Code.DISABLED_RUNE, "$.arguments[2]", "first retained")
        ));
        assertEquals(List.of(ScopedLanguageIssue.Code.AST_LIMIT, ScopedLanguageIssue.Code.DISABLED_RUNE, ScopedLanguageIssue.Code.TYPE_MISMATCH, ScopedLanguageIssue.Code.LITERAL_INVALID, ScopedLanguageIssue.Code.LOWERED_GRAPH_INVALID), ordered.stream().map(ScopedLanguageIssue::code).toList(), "diagnostic phase ordering");
        assertEquals("a", ordered.get(1).message(), "first duplicate retained");
    }

    @Test
    void literalFailureDoesNotInventFunctionResultDiagnostic() {
        ScopedCompileResult result = compiler().compile(source(ScopedTypeCheckerTest.number("NaN"), RuneType.NUMBER));
        assertEquals(List.of(ScopedLanguageIssue.Code.LITERAL_INVALID), result.issues().stream().map(ScopedLanguageIssue::code).toList());
    }

    @Test
    void appliedLambdaKeepsTheAuthoredBodyPathForLoweringDiagnostics() {
        ScopedExpression expression = new ScopedExpression.Application(
                new ScopedExpression.Lambda("x", RuneTypeExpression.value(RuneType.NUMBER), ScopedTypeCheckerTest.number("NaN")),
                ScopedTypeCheckerTest.number("1")
        );
        ScopedCompileResult result = compiler().compile(source(expression, RuneType.NUMBER));
        assertEquals(List.of(ScopedLanguageIssue.Code.LITERAL_INVALID), result.issues().stream().map(ScopedLanguageIssue::code).toList());
        assertEquals("$.function.body", result.issues().getFirst().path());
    }

    @Test
    void boundFiveAndSixUseTheCompleteSharedPipelineAt4096And4097() {
        ScopedExpression identity = new ScopedExpression.Application(
                new ScopedExpression.Lambda("x", RuneTypeExpression.value(RuneType.NUMBER), new ScopedExpression.ParameterReference(0)),
                ScopedTypeCheckerTest.number("1")
        );
        ScopedProgramSource source = source(identity, RuneType.NUMBER);
        ScopedProgramCompiler compiler = compiler();
        ScopedCompileResult measured = compiler.compile(source);
        assertTrue(measured.valid());
        assertEquals(17, measured.chargedSteps(), "beta activation and its administrative binding are separate charges");

        ScopedCompileBudget atLimit = precharged(ScopedLanguageLimits.MAX_EVALUATION_STEPS - measured.chargedSteps());
        ScopedCompileResult accepted = compiler.compile(source, atLimit);
        assertTrue(accepted.valid(), "BOUND-5: exactly 4096 pipeline charges succeed");
        assertEquals(ScopedLanguageLimits.MAX_EVALUATION_STEPS, accepted.chargedSteps());

        ScopedCompileBudget pastLimit = precharged(ScopedLanguageLimits.MAX_EVALUATION_STEPS + 1 - measured.chargedSteps());
        ScopedCompileResult rejected = compiler.compile(source, pastLimit);
        assertTrue(rejected.graph().isEmpty(), "BOUND-6: no graph escapes the failed pipeline");
        assertEquals(List.of(ScopedLanguageIssue.Code.COMPILE_STEP_LIMIT), rejected.issues().stream().map(ScopedLanguageIssue::code).toList());
        assertEquals(ScopedLanguageLimits.MAX_EVALUATION_STEPS, rejected.chargedSteps());
    }

    private static void assertLiteralFailure(ScopedExpression expression, ScopedLanguageIssue.Code code) {
        ScopedCompileResult result = compiler().compile(source(expression, expression instanceof ScopedExpression.Literal literal ? literal.type().type() : RuneType.NUMBER));
        if (result.graph().isPresent()) {
            throw new AssertionError(expression + " unexpectedly lowered to " + result.graph().orElseThrow());
        }
        assertTrue(result.issues().stream().anyMatch(issue -> issue.code() == code), () -> result.issues().toString());
    }

    private static void assertShared(ScopedCompileResult result, String label) {
        assertTrue(result.valid(), () -> label + result.issues());
        ProgramGraph graph = result.graph().orElseThrow();
        assertEquals(1, nodes(graph, "test:observe"), label + " one observation");
        java.util.List<ProgramEdge> edges = graph.edges().stream().filter(edge -> graph.nodes().stream().anyMatch(node -> node.id().equals(edge.toNodeId()) && node.runeId().equals("test:add"))).toList();
        assertEquals(2, edges.size(), label + " two sockets");
        assertEquals(edges.get(0).fromNodeId(), edges.get(1).fromNodeId(), label + " shared source");
    }

    private static ScopedCompileBudget precharged(int steps) {
        ScopedCompileBudget budget = new ScopedCompileBudget();
        for (int index = 0; index < steps; index++) budget.charge(ScopedCompileBudget.Event.STRUCTURAL_NODE);
        return budget;
    }

    private static ScopedExpression identityApplications(int count) {
        ScopedExpression expression = ScopedTypeCheckerTest.number("1");
        for (int index = 0; index < count; index++) expression = new ScopedExpression.Application(
                new ScopedExpression.Lambda("x", RuneTypeExpression.value(RuneType.NUMBER), new ScopedExpression.ParameterReference(0)), expression);
        return expression;
    }

    private static void assertCanonical(String spelling, String expected) {
        ScopedCompileResult result = compiler().compile(source(ScopedTypeCheckerTest.number(spelling), RuneType.NUMBER));
        assertTrue(result.valid(), () -> result.issues().toString());
        assertEquals(expected, result.graph().orElseThrow().nodes().getFirst().constants().get("value"));
    }

    private static void assertDescriptorFailure(RuneDefinition definition) {
        com.mathmod.runes.RuneRegistry registry = ScopedTypeCheckerTest.registry();
        registry.registerOrReplace(definition);
        ScopedCompileResult result = new ScopedProgramCompiler(registry).compile(source(ScopedTypeCheckerTest.number("1"), RuneType.NUMBER));
        assertTrue(result.graph().isEmpty());
        assertTrue(result.issues().stream().anyMatch(issue -> issue.code() == ScopedLanguageIssue.Code.LOWERED_GRAPH_INVALID));
    }

    private static ScopedProgramCompiler compiler() {
        com.mathmod.runes.RuneRegistry registry = ScopedTypeCheckerTest.registry();
        registry.register(com.mathmod.runes.RuneDefinition.builder("test:observe")
                .output(RuneType.NUMBER).purity(com.mathmod.runes.RunePurity.OBSERVATION).build());
        return new ScopedProgramCompiler(registry);
    }

    private static ScopedProgramSource source(ScopedExpression expression, RuneType output) {
        return new ScopedProgramSource(ScopedProgramSource.CURRENT_VERSION, expression, RuneTypeExpression.value(output), 16);
    }

    private static ScopedExpression add(ScopedExpression left, ScopedExpression right) {
        return new ScopedExpression.RuneCall("test:add", List.of(
                new ScopedExpression.Argument("left", left), new ScopedExpression.Argument("right", right)
        ));
    }

    private static long nodes(ProgramGraph graph, String runeId) {
        return graph.nodes().stream().filter(node -> node.runeId().equals(runeId)).count();
    }

    private static long edgesTo(ProgramGraph graph, String runeId) {
        return graph.edges().stream().filter(edge -> graph.nodes().stream()
                .anyMatch(node -> node.id().equals(edge.toNodeId()) && node.runeId().equals(runeId))).count();
    }
}
