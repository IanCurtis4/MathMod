package com.mathmod.program;

import com.mathmod.knowledge.KnowledgeDefinitionSnapshot;
import com.mathmod.knowledge.KnowledgeDefinitions;
import com.mathmod.knowledge.KnowledgeKind;
import com.mathmod.knowledge.PlayerKnowledge;
import com.mathmod.kubejs.RuneMaterialDefinition;
import com.mathmod.language.ScopedCompileResult;
import com.mathmod.language.ScopedProgramCompiler;
import com.mathmod.language.ScopedRuneSnapshot;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RuneRegistrySnapshot;
import com.mathmod.util.NamespacedId;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Stateless server-side admission pipeline. It never persists or mutates game state. */
public final class ScopedServerCompileService {
    private final RuneRegistry runes;
    private final Supplier<List<RuneMaterialDefinition>> materials;
    private final Supplier<KnowledgeDefinitionSnapshot> knowledgeDefinitions;

    public ScopedServerCompileService(RuneRegistry runes) {
        this(runes, ProgramResources::materials, KnowledgeDefinitions::snapshot);
    }

    ScopedServerCompileService(
            RuneRegistry runes,
            Supplier<List<RuneMaterialDefinition>> materials,
            Supplier<KnowledgeDefinitionSnapshot> knowledgeDefinitions
    ) {
        this.runes = java.util.Objects.requireNonNull(runes, "runes");
        this.materials = java.util.Objects.requireNonNull(materials, "materials");
        this.knowledgeDefinitions = java.util.Objects.requireNonNull(knowledgeDefinitions, "knowledgeDefinitions");
    }

    public ScopedServerCompileResult compile(ScopedServerCompileRequest request) {
        if (request.cancellation().cancelled()) return failure(request.playerKnowledge(), 0, List.of(issue(ScopedServerCompileIssue.Phase.ENTRY_CANCELLATION, ScopedServerCompileIssue.Code.CANCELLED)));
        RuneRegistrySnapshot capturedRunes = runes.captureSnapshot();
        List<RuneMaterialDefinition> materials = List.copyOf(this.materials.get());
        KnowledgeDefinitionSnapshot knowledgeDefinitions = this.knowledgeDefinitions.get();
        ScopedCompileResult pure = new ScopedProgramCompiler(new ScopedRuneSnapshot(capturedRunes.definitions())).compile(request.source());
        if (!pure.valid()) return result(request.playerKnowledge(), capturedRunes, materials, knowledgeDefinitions, pure, List.of());
        if (request.cancellation().cancelled()) return result(request.playerKnowledge(), capturedRunes, materials, knowledgeDefinitions, pure, List.of(cancelled(ScopedServerCompileIssue.Phase.POST_COMPILATION_CANCELLATION)));

        var graph = pure.graph().orElseThrow();
        List<ResourceSelection> recommendations = ProgramResources.recommendedFor(graph, capturedRunes.detachedRegistry(), materials);
        ProgramCostPlan costPlan = ProgramCosts.structuralPlanFor(graph, recommendations, capturedRunes.detachedRegistry(), materials);
        if (!ProgramExecutionPolicy.validateExecutable(graph, capturedRunes.detachedRegistry(), costPlan.budgetBonus()).valid()) {
            return result(request.playerKnowledge(), capturedRunes, materials, knowledgeDefinitions, pure, List.of(issue(ScopedServerCompileIssue.Phase.EXECUTABLE_ADMISSION, ScopedServerCompileIssue.Code.EXECUTABLE_REJECTED)));
        }
        if (!costPlan.success()) {
            return result(request.playerKnowledge(), capturedRunes, materials, knowledgeDefinitions, pure, List.of(issue(ScopedServerCompileIssue.Phase.RESOURCE_ADMISSION, ScopedServerCompileIssue.Code.RESOURCE_REJECTED)));
        }
        if (request.cancellation().cancelled()) return result(request.playerKnowledge(), capturedRunes, materials, knowledgeDefinitions, pure, List.of(cancelled(ScopedServerCompileIssue.Phase.BETWEEN_ADMISSIONS_CANCELLATION)));
        if (!knowledgeAllowed(graph.nodes().stream().map(node -> node.runeId()).distinct().sorted().toList(), knowledgeDefinitions, request.playerKnowledge())) {
            return result(request.playerKnowledge(), capturedRunes, materials, knowledgeDefinitions, pure, List.of(issue(ScopedServerCompileIssue.Phase.KNOWLEDGE_ADMISSION, ScopedServerCompileIssue.Code.KNOWLEDGE_REJECTED)));
        }
        if (request.cancellation().cancelled()) return result(request.playerKnowledge(), capturedRunes, materials, knowledgeDefinitions, pure, List.of(cancelled(ScopedServerCompileIssue.Phase.BEFORE_RETURN_CANCELLATION)));
        if (runes.generation() != capturedRunes.generation()) {
            return result(request.playerKnowledge(), capturedRunes, materials, knowledgeDefinitions, pure, List.of(issue(ScopedServerCompileIssue.Phase.GENERATION_RECHECK, ScopedServerCompileIssue.Code.REGISTRY_GENERATION_STALE)));
        }
        return new ScopedServerCompileResult(pure.graph(), recommendations, capturedRunes.generation(), capturedRunes.definitions(), materials,
                knowledgeDefinitions, request.playerKnowledge(), pure.chargedSteps(), pure.issues(), List.of());
    }

    private static boolean knowledgeAllowed(List<String> runeIds, KnowledgeDefinitionSnapshot definitions, PlayerKnowledge knowledge) {
        for (String runeId : runeIds) {
            var id = NamespacedId.tryParse(runeId);
            if (id.isEmpty() || definitions.requirementFor(KnowledgeKind.RUNE, id.get()).map(requirement -> !requirement.isSatisfiedBy(knowledge)).orElse(false)) return false;
        }
        return true;
    }

    private static ScopedServerCompileIssue cancelled(ScopedServerCompileIssue.Phase phase) { return issue(phase, ScopedServerCompileIssue.Code.CANCELLED); }

    private static ScopedServerCompileIssue issue(ScopedServerCompileIssue.Phase phase, ScopedServerCompileIssue.Code code) {
        return new ScopedServerCompileIssue(phase, code, "$");
    }

    private static ScopedServerCompileResult failure(PlayerKnowledge knowledge, int chargedSteps, List<ScopedServerCompileIssue> issues) {
        return new ScopedServerCompileResult(java.util.Optional.empty(), List.of(), 0, Map.of(), List.of(), null, knowledge, chargedSteps, List.of(), issues);
    }

    private static ScopedServerCompileResult result(PlayerKnowledge knowledge, RuneRegistrySnapshot runes, List<RuneMaterialDefinition> materials,
            KnowledgeDefinitionSnapshot definitions, ScopedCompileResult pure, List<ScopedServerCompileIssue> issues) {
        return new ScopedServerCompileResult(java.util.Optional.empty(), List.of(), runes.generation(), runes.definitions(), materials,
                definitions, knowledge, pure.chargedSteps(), pure.issues(), issues);
    }
}
