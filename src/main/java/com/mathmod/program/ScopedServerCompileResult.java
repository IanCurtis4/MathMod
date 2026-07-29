package com.mathmod.program;

import com.mathmod.knowledge.KnowledgeDefinitionSnapshot;
import com.mathmod.knowledge.PlayerKnowledge;
import com.mathmod.language.ScopedLanguageIssue;
import com.mathmod.kubejs.RuneMaterialDefinition;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.RuneDefinition;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

public record ScopedServerCompileResult(
        Optional<ProgramGraph> candidate,
        List<ResourceSelection> recommendations,
        long runeGeneration,
        Map<String, RuneDefinition> runeDefinitions,
        List<RuneMaterialDefinition> materialDefinitions,
        KnowledgeDefinitionSnapshot knowledgeDefinitions,
        PlayerKnowledge playerKnowledge,
        int chargedSteps,
        List<ScopedLanguageIssue> languageIssues,
        List<ScopedServerCompileIssue> serviceIssues
) {
    public ScopedServerCompileResult {
        candidate = candidate == null ? Optional.empty() : candidate;
        languageIssues = ScopedLanguageIssue.normalize(languageIssues);
        serviceIssues = ScopedServerCompileIssue.normalize(serviceIssues);
        if (!languageIssues.isEmpty() || !serviceIssues.isEmpty()) {
            candidate = Optional.empty();
            recommendations = List.of();
        }
        if (candidate.isEmpty()) {
            recommendations = List.of();
            runeDefinitions = runeDefinitions == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(runeDefinitions));
            materialDefinitions = materialDefinitions == null ? List.of() : List.copyOf(materialDefinitions);
        } else {
            recommendations = List.copyOf(Objects.requireNonNull(recommendations, "recommendations"));
            runeDefinitions = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(runeDefinitions, "runeDefinitions")));
            materialDefinitions = List.copyOf(Objects.requireNonNull(materialDefinitions, "materialDefinitions"));
            knowledgeDefinitions = Objects.requireNonNull(knowledgeDefinitions, "knowledgeDefinitions");
            playerKnowledge = Objects.requireNonNull(playerKnowledge, "playerKnowledge");
            if (runeGeneration < 0) throw new IllegalArgumentException("runeGeneration must not be negative");
            Map<String, RuneDefinition> capturedDefinitions = runeDefinitions;
            if (candidate.orElseThrow().nodes().stream().map(node -> node.runeId()).anyMatch(id -> !capturedDefinitions.containsKey(id))) {
                throw new IllegalArgumentException("candidate rune definitions must be captured evidence");
            }
        }
        if (chargedSteps < 0) throw new IllegalArgumentException("chargedSteps must not be negative");
    }

    public boolean successful() {
        return candidate.isPresent() && languageIssues.isEmpty() && serviceIssues.isEmpty();
    }
}
