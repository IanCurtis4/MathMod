package com.mathmod.language;

import java.util.List;
import java.util.Optional;

public record ScopedTypeCheckResult(
        Optional<RuneTypeExpression> inferredType,
        List<ScopedLanguageIssue> issues
) {
    public ScopedTypeCheckResult {
        inferredType = inferredType == null ? Optional.empty() : inferredType;
        issues = List.copyOf(issues);
    }

    public boolean valid() {
        return issues.isEmpty() && inferredType.isPresent();
    }
}
