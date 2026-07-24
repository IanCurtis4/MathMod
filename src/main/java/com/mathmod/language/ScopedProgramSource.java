package com.mathmod.language;

import java.util.Objects;

/** Versioned authoring source. It is never the directly executable item program. */
public record ScopedProgramSource(
        int version,
        ScopedExpression expression,
        RuneTypeExpression.ValueType resultType,
        int budgetLimit
) {
    public static final int CURRENT_VERSION = 1;

    public ScopedProgramSource {
        if (version != CURRENT_VERSION) {
            throw new IllegalArgumentException("Unsupported scoped source version " + version);
        }
        expression = Objects.requireNonNull(expression, "expression");
        resultType = Objects.requireNonNull(resultType, "resultType");
        if (budgetLimit < 0 || budgetLimit > ScopedLanguageLimits.MAX_BUDGET) {
            throw new IllegalArgumentException(
                    "budgetLimit must be between 0 and " + ScopedLanguageLimits.MAX_BUDGET
            );
        }
    }
}
