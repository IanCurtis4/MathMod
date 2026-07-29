package com.mathmod.program;

import com.mathmod.knowledge.PlayerKnowledge;
import com.mathmod.knowledge.DiscoveryDefinition;
import com.mathmod.knowledge.KnowledgeDefinitionSnapshot;
import com.mathmod.knowledge.KnowledgeGrant;
import com.mathmod.knowledge.KnowledgeKind;
import com.mathmod.language.RuneTypeExpression;
import com.mathmod.language.ScopedExpression;
import com.mathmod.language.ScopedProgramSource;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RunePurity;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RuneType;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ScopedServerCompileServiceTest {
    @Test
    void successfulAttemptReturnsOneImmutableCapturedEvidenceSet() {
        RuneRegistry registry = registry();
        ScopedServerCompileResult result = service(registry, emptyDefinitions()).compile(request(ScopedCompileCancellation.NEVER));

        assertTrue(result.successful(), () -> result.languageIssues() + " " + result.serviceIssues());
        assertEquals(1, result.runeGeneration());
        assertEquals(List.of("test:emit"), result.runeDefinitions().keySet().stream().toList());
        assertThrows(UnsupportedOperationException.class, () -> result.runeDefinitions().clear());
        assertThrows(UnsupportedOperationException.class, () -> result.recommendations().add(new ResourceSelection("x", 1)));
    }

    @Test
    void pureRejectionAndCancellationFailClosedWithoutRetry() {
        RuneRegistry registry = registry();
        ScopedServerCompileService service = service(registry, emptyDefinitions());
        ScopedServerCompileResult pure = service.compile(new ScopedServerCompileRequest(
                new ScopedProgramSource(1, new ScopedExpression.RuneCall("test:missing", List.of()), RuneTypeExpression.value(RuneType.UNIT), 16),
                PlayerKnowledge.empty(), ScopedCompileCancellation.NEVER));
        assertFalse(pure.successful());
        assertTrue(pure.candidate().isEmpty());
        ScopedServerCompileResult cancelled = service.compile(request(() -> true));
        assertEquals(List.of(ScopedServerCompileIssue.Code.CANCELLED), cancelled.serviceIssues().stream().map(ScopedServerCompileIssue::code).toList());

        AtomicBoolean firstProbe = new AtomicBoolean(true);
        ScopedServerCompileResult cancelledAfterCompilation = service.compile(request(() -> !firstProbe.getAndSet(false)));
        assertEquals(List.of(ScopedServerCompileIssue.Code.CANCELLED), cancelledAfterCompilation.serviceIssues().stream().map(ScopedServerCompileIssue::code).toList());
        assertTrue(cancelledAfterCompilation.candidate().isEmpty());
    }

    @Test
    void changeAfterCaptureIsRejectedAsStale() {
        RuneRegistry registry = registry();
        AtomicBoolean first = new AtomicBoolean(true);
        ScopedCompileCancellation probe = () -> {
            if (first.getAndSet(false)) return false;
            registry.setEnabled("test:emit", false);
            return false;
        };
        ScopedServerCompileResult result = service(registry, emptyDefinitions()).compile(request(probe));
        assertEquals(List.of(ScopedServerCompileIssue.Code.REGISTRY_GENERATION_STALE), result.serviceIssues().stream().map(ScopedServerCompileIssue::code).toList());
        assertTrue(result.candidate().isEmpty());
    }

    @Test
    void executableResourceAndKnowledgeAdmissionRejectWithoutCandidate() {
        RuneRegistry executable = registry("unsupported_key", List.of());
        assertCode(service(executable, emptyDefinitions()).compile(request(ScopedCompileCancellation.NEVER)), ScopedServerCompileIssue.Code.EXECUTABLE_REJECTED);

        RuneRegistry resource = registry("debug_marker", List.of(new com.mathmod.runes.MaterialRequirement("not a selector", 1)));
        assertCode(service(resource, emptyDefinitions()).compile(request(ScopedCompileCancellation.NEVER)), ScopedServerCompileIssue.Code.RESOURCE_REJECTED);

        KnowledgeDefinitionSnapshot required = new KnowledgeDefinitionSnapshot(List.of(), List.of(new DiscoveryDefinition(
                NamespacedId.parse("test:discovery"), NamespacedId.parse("test:manuscript"), "test.discovery",
                NamespacedId.parse("test:entry"), List.of(new KnowledgeGrant(KnowledgeKind.RUNE, NamespacedId.parse("test:emit"))))));
        assertCode(service(registry(), required).compile(request(ScopedCompileCancellation.NEVER)), ScopedServerCompileIssue.Code.KNOWLEDGE_REJECTED);
        assertTrue(service(registry(), emptyDefinitions()).compile(request(ScopedCompileCancellation.NEVER)).successful(), "absent requirement remains allowed");
    }

    @Test
    void resultAndDiagnosticInvariantsFailClosedAndNormalizeByPhasePathAndCode() {
        RuneRegistry registry = registry();
        var graph = service(registry, emptyDefinitions()).compile(request(ScopedCompileCancellation.NEVER)).candidate().orElseThrow();
        assertThrows(NullPointerException.class, () -> new ScopedServerCompileResult(java.util.Optional.of(graph), List.of(), 1, null, List.of(), emptyDefinitions(), PlayerKnowledge.empty(), 0, List.of(), List.of()));
        assertThrows(IllegalArgumentException.class, () -> new ScopedServerCompileResult(java.util.Optional.of(graph), List.of(), 1, java.util.Map.of(), List.of(), emptyDefinitions(), PlayerKnowledge.empty(), 0, List.of(), List.of()));
        ScopedServerCompileResult withIssue = new ScopedServerCompileResult(java.util.Optional.of(graph), List.of(), 1, registry.captureSnapshot().definitions(), List.of(), emptyDefinitions(), PlayerKnowledge.empty(), 0, List.of(), List.of(new ScopedServerCompileIssue(ScopedServerCompileIssue.Phase.EXECUTABLE_ADMISSION, ScopedServerCompileIssue.Code.EXECUTABLE_REJECTED, "$")));
        assertTrue(withIssue.candidate().isEmpty());
        assertTrue(withIssue.recommendations().isEmpty());

        List<ScopedServerCompileIssue> normalized = ScopedServerCompileIssue.normalize(List.of(
                new ScopedServerCompileIssue(ScopedServerCompileIssue.Phase.KNOWLEDGE_ADMISSION, ScopedServerCompileIssue.Code.KNOWLEDGE_REJECTED, "$.nodes[10]"),
                new ScopedServerCompileIssue(ScopedServerCompileIssue.Phase.EXECUTABLE_ADMISSION, ScopedServerCompileIssue.Code.EXECUTABLE_REJECTED, "$.nodes[2]"),
                new ScopedServerCompileIssue(ScopedServerCompileIssue.Phase.EXECUTABLE_ADMISSION, ScopedServerCompileIssue.Code.EXECUTABLE_REJECTED, "$.nodes[2]"),
                new ScopedServerCompileIssue(ScopedServerCompileIssue.Phase.EXECUTABLE_ADMISSION, ScopedServerCompileIssue.Code.RESOURCE_REJECTED, "$.nodes[10]")
        ));
        assertEquals(3, normalized.size());
        assertEquals("$.nodes[2]", normalized.getFirst().path());
        assertEquals(ScopedServerCompileIssue.Phase.EXECUTABLE_ADMISSION, normalized.getFirst().phase());
        assertEquals(normalized, ScopedServerCompileIssue.normalize(List.of(
                new ScopedServerCompileIssue(ScopedServerCompileIssue.Phase.EXECUTABLE_ADMISSION, ScopedServerCompileIssue.Code.RESOURCE_REJECTED, "$.nodes[10]"),
                new ScopedServerCompileIssue(ScopedServerCompileIssue.Phase.EXECUTABLE_ADMISSION, ScopedServerCompileIssue.Code.EXECUTABLE_REJECTED, "$.nodes[2]"),
                new ScopedServerCompileIssue(ScopedServerCompileIssue.Phase.KNOWLEDGE_ADMISSION, ScopedServerCompileIssue.Code.KNOWLEDGE_REJECTED, "$.nodes[10]")
        )));
    }

    private static ScopedServerCompileRequest request(ScopedCompileCancellation cancellation) {
        return new ScopedServerCompileRequest(
                new ScopedProgramSource(1, new ScopedExpression.RuneCall("test:emit", List.of()), RuneTypeExpression.value(RuneType.UNIT), 16),
                PlayerKnowledge.empty(), cancellation);
    }

    private static RuneRegistry registry() {
        return registry("debug_marker", List.of());
    }

    private static RuneRegistry registry(String executorKey, List<com.mathmod.runes.MaterialRequirement> materials) {
        RuneRegistry registry = new RuneRegistry();
        RuneDefinition.Builder definition = RuneDefinition.builder("test:emit").output(RuneType.UNIT).purity(RunePurity.EFFECT).executorKey(executorKey);
        materials.forEach(requirement -> definition.material(requirement.itemOrTag(), requirement.quantity()));
        registry.register(definition.build());
        return registry;
    }

    private static ScopedServerCompileService service(RuneRegistry registry, KnowledgeDefinitionSnapshot definitions) {
        return new ScopedServerCompileService(registry, List::of, () -> definitions);
    }

    private static KnowledgeDefinitionSnapshot emptyDefinitions() {
        return new KnowledgeDefinitionSnapshot(List.of(), List.of());
    }

    private static void assertCode(ScopedServerCompileResult result, ScopedServerCompileIssue.Code code) {
        assertEquals(List.of(code), result.serviceIssues().stream().map(ScopedServerCompileIssue::code).toList());
        assertTrue(result.candidate().isEmpty());
        assertTrue(result.recommendations().isEmpty());
    }
}
