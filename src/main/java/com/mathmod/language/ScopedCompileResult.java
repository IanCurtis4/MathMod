package com.mathmod.language;

import com.mathmod.runes.ProgramGraph;

import java.util.List;
import java.util.Optional;

public record ScopedCompileResult(Optional<ProgramGraph> graph, List<ScopedLanguageIssue> issues, int chargedSteps) {
    public ScopedCompileResult {
        graph = graph == null ? Optional.empty() : graph;
        issues = ScopedLanguageIssue.normalize(issues);
        if (!issues.isEmpty()) {
            graph = Optional.empty();
        }
    }

    public boolean valid() {
        return graph.isPresent() && issues.isEmpty();
    }
}
