package com.mathmod.language;

import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RunePurity;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RuneType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopedTypeCheckerTest {
    @Test
    void acceptsAWellTypedTailEffect() {
        RuneRegistry registry = registry();
        ScopedExpression expression = new ScopedExpression.RuneCall("test:emit", List.of(
                new ScopedExpression.Argument("value", new ScopedExpression.RuneCall("test:add", List.of(
                        new ScopedExpression.Argument("left", number("2")),
                        new ScopedExpression.Argument("right", number("3"))
                )))
        ));
        ScopedTypeCheckResult result = new ScopedTypeChecker(registry).check(source(expression, RuneType.UNIT));
        assertTrue(result.valid(), () -> result.issues().toString());
    }

    @Test
    void rejectsAnInputWithTheWrongType() {
        RuneRegistry registry = registry();
        ScopedExpression expression = new ScopedExpression.RuneCall("test:emit", List.of(
                new ScopedExpression.Argument("value", new ScopedExpression.Literal(
                        RuneTypeExpression.value(RuneType.BOOL), "true"))
        ));
        ScopedTypeCheckResult result = new ScopedTypeChecker(registry).check(source(expression, RuneType.UNIT));
        assertTrue(result.issues().stream().anyMatch(issue -> issue.code() == ScopedLanguageIssue.Code.TYPE_MISMATCH));
    }

    static RuneRegistry registry() {
        RuneRegistry registry = new RuneRegistry();
        registry.register(RuneDefinition.builder("test:add")
                .input("left", RuneType.NUMBER).input("right", RuneType.NUMBER).output(RuneType.NUMBER)
                .purity(RunePurity.PURE).build());
        registry.register(RuneDefinition.builder("test:emit")
                .input("value", RuneType.NUMBER).output(RuneType.UNIT).purity(RunePurity.EFFECT).build());
        registry.register(RuneDefinition.builder("mathmod:constant_number")
                .output(RuneType.NUMBER).purity(RunePurity.PURE).build());
        return registry;
    }

    static ScopedProgramSource source(ScopedExpression expression, RuneType type) {
        return new ScopedProgramSource(1, expression, RuneTypeExpression.value(type), 16);
    }

    static ScopedExpression.Literal number(String value) {
        return new ScopedExpression.Literal(RuneTypeExpression.value(RuneType.NUMBER), value);
    }
}
