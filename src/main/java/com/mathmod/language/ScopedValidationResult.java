package com.mathmod.language;

import java.util.List;

public record ScopedValidationResult(List<ScopedLanguageIssue> issues) {
    public ScopedValidationResult {
        issues = ScopedLanguageIssue.normalize(issues);
    }

    public boolean valid() {
        return issues.isEmpty();
    }
}
