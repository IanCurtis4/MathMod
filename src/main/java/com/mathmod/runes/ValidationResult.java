package com.mathmod.runes;

import java.util.List;

public record ValidationResult(List<ValidationIssue> issues, int budgetUsed, RuneType outputType) {
    public ValidationResult {
        issues = List.copyOf(issues);
        if (budgetUsed < 0) {
            throw new IllegalArgumentException("budgetUsed must not be negative");
        }
    }

    public boolean valid() {
        return issues.stream().noneMatch(issue -> issue.severity() == ValidationIssue.Severity.ERROR);
    }
}
