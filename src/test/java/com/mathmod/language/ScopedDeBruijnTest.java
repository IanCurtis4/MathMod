package com.mathmod.language;

import com.mathmod.runes.RuneType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopedDeBruijnTest {
    @Test
    void substitutionShiftsFreeParametersPastNestedBinders() {
        ScopedExpression body = new ScopedExpression.Lambda(
                "inner",
                numberType(),
                new ScopedExpression.ParameterReference(1)
        );
        ScopedExpression replacement = new ScopedExpression.ParameterReference(0);

        ScopedExpression reduced = ScopedDeBruijn.substituteTop(replacement, body);

        assertTrue(ScopedDeBruijn.alphaEquivalent(
                new ScopedExpression.Lambda(
                        "renamed",
                        numberType(),
                        new ScopedExpression.ParameterReference(1)
                ),
                reduced
        ));
    }

    @Test
    void shiftTreatsLetValueAndLetBodyAsDifferentScopes() {
        ScopedExpression expression = new ScopedExpression.Let(
                "x",
                new ScopedExpression.ParameterReference(0),
                new ScopedExpression.ParameterReference(1)
        );

        ScopedExpression shifted = ScopedDeBruijn.shift(expression, 1);

        ScopedExpression expected = new ScopedExpression.Let(
                "other",
                new ScopedExpression.ParameterReference(1),
                new ScopedExpression.ParameterReference(2)
        );
        assertTrue(ScopedDeBruijn.alphaEquivalent(expected, shifted));
    }

    @Test
    void alphaEquivalenceIgnoresNamesButNotBindingStructure() {
        ScopedExpression first = new ScopedExpression.Lambda(
                "speed",
                numberType(),
                new ScopedExpression.ParameterReference(0)
        );
        ScopedExpression renamed = new ScopedExpression.Lambda(
                "velocity",
                numberType(),
                new ScopedExpression.ParameterReference(0)
        );
        ScopedExpression different = new ScopedExpression.Lambda(
                "velocity",
                numberType(),
                new ScopedExpression.ParameterReference(1)
        );

        assertTrue(ScopedDeBruijn.alphaEquivalent(first, renamed));
        assertFalse(ScopedDeBruijn.alphaEquivalent(first, different));
    }

    @Test
    void administrativeBetaRetainsOneObservedArgumentInsteadOfCopyingIt() {
        ScopedExpression observation = new ScopedExpression.RuneCall(
                "mathmod:player_position",
                List.of()
        );
        ScopedExpression.Application application = new ScopedExpression.Application(
                new ScopedExpression.Lambda(
                        "position",
                        RuneTypeExpression.value(RuneType.VEC3),
                        new ScopedExpression.RuneCall(
                                "mathmod:vector_distance",
                                List.of(
                                        argument("a", new ScopedExpression.ParameterReference(0)),
                                        argument("b", new ScopedExpression.ParameterReference(0))
                                )
                        )
                ),
                observation
        );

        ScopedExpression reduced = ScopedDeBruijn.betaToLet(application);

        assertEquals(new ScopedExpression.Let(
                "position",
                observation,
                application.function() instanceof ScopedExpression.Lambda lambda
                        ? lambda.body()
                        : throwUnexpected()
        ), reduced);
    }

    @Test
    void betaToLetRejectsNonLambdaApplications() {
        assertThrows(IllegalArgumentException.class, () -> ScopedDeBruijn.betaToLet(
                new ScopedExpression.Application(number("1"), number("2"))
        ));
    }

    private static ScopedExpression throwUnexpected() {
        throw new AssertionError("Expected lambda");
    }

    private static RuneTypeExpression.ValueType numberType() {
        return RuneTypeExpression.value(RuneType.NUMBER);
    }

    private static ScopedExpression.Literal number(String value) {
        return new ScopedExpression.Literal(numberType(), value);
    }

    private static ScopedExpression.Argument argument(String name, ScopedExpression expression) {
        return new ScopedExpression.Argument(name, expression);
    }
}
