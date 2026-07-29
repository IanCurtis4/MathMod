package com.mathmod.language;

import com.mathmod.runes.ProgramGraph;

import java.util.List;
import java.util.Optional;

public record ScopedLoweringResult(Optional<ProgramGraph> graph, List<ScopedLanguageIssue> issues) {
    public ScopedLoweringResult {
        graph = graph == null ? Optional.empty() : graph;
        issues = ScopedLanguageIssue.normalize(issues);
        if (!issues.isEmpty()) graph = Optional.empty();
    }

    public boolean valid() {
        return graph.isPresent() && issues.isEmpty();
    }
}
