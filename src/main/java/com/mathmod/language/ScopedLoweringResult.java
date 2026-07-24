package com.mathmod.language;

import com.mathmod.runes.ProgramGraph;

import java.util.List;
import java.util.Optional;

public record ScopedLoweringResult(Optional<ProgramGraph> graph, List<ScopedLanguageIssue> issues) {
    public ScopedLoweringResult {
        graph = graph == null ? Optional.empty() : graph;
        issues = List.copyOf(issues);
    }

    public boolean valid() {
        return graph.isPresent() && issues.isEmpty();
    }
}
