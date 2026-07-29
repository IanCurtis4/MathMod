package com.mathmod.language;

import com.mathmod.runes.RunePurity;
import com.mathmod.runes.RuneType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopedStructureValidatorTest {
    @Test
    void deBruijnReferencesResolveAgainstNearestLexicalBinder() {
        ScopedExpression expression = new ScopedExpression.Let(
                "outer",
                number("2"),
                new ScopedExpression.Let(
                        "inner",
                        new ScopedExpression.ParameterReference(0),
                        new ScopedExpression.RuneCall(
                                "mathmod:number_add",
                                List.of(
                                        argument("a", new ScopedExpression.ParameterReference(0)),
                                        argument("b", new ScopedExpression.ParameterReference(1))
                                )
                        )
                )
        );

        assertTrue(ScopedStructureValidator.validateStructure(source(expression)).valid());
    }

    @Test
    void letBinderIsNotVisibleInsideItsOwnValue() {
        ScopedExpression expression = new ScopedExpression.Let(
                "not_recursive",
                new ScopedExpression.ParameterReference(0),
                new ScopedExpression.ParameterReference(0)
        );

        ScopedValidationResult result = ScopedStructureValidator.validateStructure(source(expression));
        assertFalse(result.valid());
        assertTrue(hasIssue(result, ScopedLanguageIssue.Code.FREE_PARAMETER));
    }

    @Test
    void lambdaBodiesAcceptPureCallsAndRejectObservationsOrEffects() {
        ScopedExpression pure = applicationOfLambda("mathmod:number_abs");
        ScopedExpression observation = applicationOfLambda("mathmod:player_position");
        ScopedExpression effect = applicationOfLambda("mathmod:push_self");

        assertTrue(validatePurity(pure).valid());
        assertTrue(hasIssue(
                validatePurity(observation),
                ScopedLanguageIssue.Code.IMPURE_LAMBDA_BODY
        ));
        assertTrue(hasIssue(
                validatePurity(effect),
                ScopedLanguageIssue.Code.IMPURE_LAMBDA_BODY
        ));
    }

    @Test
    void topLevelObservationRemainsAvailableOutsideLambda() {
        ScopedExpression expression = new ScopedExpression.RuneCall(
                "mathmod:player_position",
                List.of()
        );

        assertTrue(validatePurity(expression).valid());
    }

    @Test
    void effectsMayOnlyOccupyTheTailPosition() {
        ScopedExpression terminalEffect = new ScopedExpression.RuneCall(
                "mathmod:push_self",
                List.of()
        );
        ScopedExpression effectArgument = new ScopedExpression.Application(
                new ScopedExpression.Lambda(
                        "ignored",
                        RuneTypeExpression.value(RuneType.UNIT),
                        number("1")
                ),
                terminalEffect
        );
        ScopedExpression effectLetValue = new ScopedExpression.Let(
                "effect",
                terminalEffect,
                number("1")
        );

        assertTrue(validatePurity(terminalEffect).valid());
        assertTrue(hasIssue(
                validatePurity(effectArgument),
                ScopedLanguageIssue.Code.EFFECT_NOT_IN_TAIL
        ));
        assertTrue(hasIssue(
                validatePurity(effectLetValue),
                ScopedLanguageIssue.Code.EFFECT_NOT_IN_TAIL
        ));
    }

    @Test
    void rejectsFreeParametersAndOverNestedFunctionTypes() {
        ScopedValidationResult free = ScopedStructureValidator.validateStructure(
                source(new ScopedExpression.ParameterReference(0))
        );
        RuneTypeExpression nested = RuneTypeExpression.value(RuneType.NUMBER);
        for (int index = 0; index <= ScopedLanguageLimits.MAX_TYPE_DEPTH; index++) {
            nested = RuneTypeExpression.function(RuneTypeExpression.value(RuneType.NUMBER), nested);
        }
        ScopedExpression lambda = new ScopedExpression.Lambda(
                "deep",
                nested,
                new ScopedExpression.ParameterReference(0)
        );
        ScopedValidationResult deep = ScopedStructureValidator.validateStructure(source(lambda));

        assertTrue(hasIssue(free, ScopedLanguageIssue.Code.FREE_PARAMETER));
        assertTrue(hasIssue(deep, ScopedLanguageIssue.Code.TYPE_DEPTH_LIMIT));
    }

    @Test
    void boundOneTwoThreeAndFourUseExactStructuralBoundaries() {
        assertTrue(ScopedStructureValidator.validateStructure(source(new ScopedExpression.Lambda("x", RuneTypeExpression.value(RuneType.NUMBER), addTree(128)))).valid(), "BOUND-1 256 AST nodes");
        assertTrue(hasIssue(ScopedStructureValidator.validateStructure(source(new ScopedExpression.Let("x", number("1"), addTree(128)))), ScopedLanguageIssue.Code.AST_LIMIT), "BOUND-1 exactly 257 AST nodes");
        assertTrue(ScopedStructureValidator.validateStructure(source(nestedLets(16))).issues().stream().noneMatch(i -> i.code() == ScopedLanguageIssue.Code.BINDING_DEPTH_LIMIT), "BOUND-2 depth 16");
        assertTrue(hasIssue(ScopedStructureValidator.validateStructure(source(nestedLets(17))), ScopedLanguageIssue.Code.BINDING_DEPTH_LIMIT), "BOUND-2 depth 17");
        assertTrue(ScopedStructureValidator.validateStructure(source(applications(64))).issues().stream().noneMatch(i -> i.code() == ScopedLanguageIssue.Code.APPLICATION_LIMIT), "BOUND-3 64 applications");
        assertTrue(hasIssue(ScopedStructureValidator.validateStructure(source(applications(65))), ScopedLanguageIssue.Code.APPLICATION_LIMIT), "BOUND-3 65 applications");
        assertTrue(ScopedStructureValidator.validateStructure(source(number("1".repeat(160)))).issues().stream().noneMatch(i -> i.code() == ScopedLanguageIssue.Code.LITERAL_LIMIT), "BOUND-4 160 characters");
        assertTrue(hasIssue(ScopedStructureValidator.validateStructure(source(number("1".repeat(161)))), ScopedLanguageIssue.Code.LITERAL_LIMIT), "BOUND-4 161 characters");
    }

    @Test
    void tailFiveAndSevenRejectNestedEffectsAndTailEightRemainsDeferred() {
        ScopedExpression effect = new ScopedExpression.RuneCall("mathmod:push_self", List.of());
        assertTrue(hasIssue(validatePurity(new ScopedExpression.RuneCall("mathmod:number_abs", List.of(argument("value", effect)))), ScopedLanguageIssue.Code.EFFECT_NOT_IN_TAIL), "TAIL-5");
        assertTrue(hasIssue(validatePurity(new ScopedExpression.RuneCall("mathmod:push_self", List.of(argument("value", effect)))), ScopedLanguageIssue.Code.EFFECT_NOT_IN_TAIL), "TAIL-7");
        assertTrue(validatePurity(number("1")).valid(), "TAIL-8 classification: structural layer permits a pure non-Unit candidate; admission is deferred");
    }

    private static ScopedExpression addTree(int literals) {
        ScopedExpression expression = number("1");
        for (int i = 1; i < literals; i++) expression = new ScopedExpression.RuneCall("mathmod:number_add", List.of(argument("a", expression), argument("b", number("1"))));
        return expression;
    }
    private static ScopedExpression nestedLets(int depth) { ScopedExpression e = number("1"); for (int i = 0; i < depth; i++) e = new ScopedExpression.Let("x", number("1"), e); return e; }
    private static ScopedExpression applications(int count) { ScopedExpression e = number("1"); for (int i = 0; i < count; i++) e = new ScopedExpression.Application(new ScopedExpression.Lambda("x", RuneTypeExpression.value(RuneType.NUMBER), new ScopedExpression.ParameterReference(0)), e); return e; }

    private static ScopedValidationResult validatePurity(ScopedExpression expression) {
        return ScopedStructureValidator.validate(source(expression), runeId -> Optional.of(switch (runeId) {
            case "mathmod:number_abs" -> RunePurity.PURE;
            case "mathmod:player_position" -> RunePurity.OBSERVATION;
            default -> RunePurity.EFFECT;
        }));
    }

    private static ScopedExpression applicationOfLambda(String runeId) {
        return new ScopedExpression.Application(
                new ScopedExpression.Lambda(
                        "x",
                        RuneTypeExpression.value(RuneType.NUMBER),
                        new ScopedExpression.RuneCall(
                                runeId,
                                List.of(argument("value", new ScopedExpression.ParameterReference(0)))
                        )
                ),
                number("-2")
        );
    }

    private static ScopedProgramSource source(ScopedExpression expression) {
        return new ScopedProgramSource(
                ScopedProgramSource.CURRENT_VERSION,
                expression,
                RuneTypeExpression.value(RuneType.NUMBER),
                64
        );
    }

    private static ScopedExpression.Literal number(String value) {
        return new ScopedExpression.Literal(
                RuneTypeExpression.value(RuneType.NUMBER),
                value
        );
    }

    private static ScopedExpression.Argument argument(String name, ScopedExpression expression) {
        return new ScopedExpression.Argument(name, expression);
    }

    private static boolean hasIssue(
            ScopedValidationResult result,
            ScopedLanguageIssue.Code code
    ) {
        return result.issues().stream().anyMatch(issue -> issue.code() == code);
    }
}
