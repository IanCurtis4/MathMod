package com.mathmod.authoring;

import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.CustomSpellInvocation;
import com.mathmod.program.CustomSpellWorkspace;
import com.mathmod.program.GuidedWorkspaceState;
import com.mathmod.program.ProgramSurface;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthoringCompatibilityHardeningTest {
    @Test
    void builtInSnapshotReconstructsTheSameGraphAfterARejectedCandidate() {
        AuthoringMetadata.Snapshot lastKnownGood = BuiltInAuthoringMetadata.snapshot();
        ProgramGraph expected = graphFor(schemaOneInvocations());

        assertThrows(AuthoringMetadata.CandidateFailure.class, () -> AuthoringMetadata.snapshot(
                lastKnownGood.generation() + 1,
                List.of(lastKnownGood.runeForms().values().iterator().next(), lastKnownGood.runeForms().values().iterator().next()),
                lastKnownGood.categories().values().stream().toList()
        ));

        assertEquals(lastKnownGood, BuiltInAuthoringMetadata.snapshot());
        assertEquals(expected, replayThroughTrustedForms(lastKnownGood, schemaOneInvocations()));
    }

    @Test
    void unknownMalformedAndFutureGuidedRecipesLeaveTheAuthoritativeGraphInspectable() {
        ProgramGraph authoritative = graphFor(schemaOneInvocations());
        ProgramSurface inspected = ProgramSurface.inscribed(authoritative).inspect();

        GuidedWorkspaceState unknown = new GuidedWorkspaceState(1, "Legacy", List.of("removed_mod:lost_form"));
        GuidedWorkspaceState malformed = new GuidedWorkspaceState(1, "Legacy", List.of(""));
        GuidedWorkspaceState future = new GuidedWorkspaceState(2, "Future", List.of(CustomSpellAction.SELF.persistentId()));

        assertFalse(unknown.replayable());
        assertFalse(malformed.supported());
        assertFalse(future.supported());
        assertTrue(ProgramSurface.reopenGuided(authoritative, unknown).isEmpty());
        assertTrue(ProgramSurface.reopenGuided(authoritative, malformed).isEmpty());
        assertTrue(ProgramSurface.reopenGuided(authoritative, future).isEmpty());
        assertEquals(authoritative, inspected.graph());
    }

    @Test
    void missingFormFailsClosedWithoutMutatingTheExistingWorkspaceOrGraph() {
        AuthoringMetadata.Snapshot snapshot = BuiltInAuthoringMetadata.snapshot();
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();
        workspace.apply(CustomSpellInvocation.defaults(CustomSpellAction.SELF));
        ProgramGraph before = workspace.toGraph();

        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, () ->
                TrustedLegacyExpansionAdapter.apply(snapshot, workspace, NamespacedId.parse("removed_mod:lost_form"), Map.of()));

        assertEquals("UNKNOWN_FORM: removed_mod:lost_form", failure.getMessage());
        assertEquals(before, workspace.toGraph());
    }

    private static List<CustomSpellInvocation> schemaOneInvocations() {
        return List.of(
                CustomSpellInvocation.defaults(CustomSpellAction.SELF),
                new CustomSpellInvocation(CustomSpellAction.NUMBER_ONE, Map.of("value", 2.5D)),
                CustomSpellInvocation.defaults(CustomSpellAction.ADD_ONE)
        );
    }

    private static ProgramGraph graphFor(List<CustomSpellInvocation> invocations) {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();
        workspace.loadInvocations(invocations);
        return workspace.toGraph();
    }

    private static ProgramGraph replayThroughTrustedForms(
            AuthoringMetadata.Snapshot snapshot,
            List<CustomSpellInvocation> invocations
    ) {
        ProgramGraph expected = graphFor(invocations);
        List<TrustedLegacyExpansionAdapter.FormInvocation> forms = invocations.stream()
                .map(invocation -> new TrustedLegacyExpansionAdapter.FormInvocation(
                        NamespacedId.parse(invocation.action().persistentId()), invocation.arguments()))
                .toList();
        return TrustedLegacyExpansionAdapter.replayExactly(snapshot, forms, expected);
    }
}
