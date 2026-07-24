package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProofWorkflowStateTest {
    @Test
    void emptyWorkspaceHasNoProof() {
        assertState(ProofWorkflowState.EMPTY, false, false, false, false, false);
    }

    @Test
    void invalidPreviewIsAnIncompleteProof() {
        assertState(ProofWorkflowState.INCOMPLETE, true, false, false, false, false);
    }

    @Test
    void validPreviewIsDemonstratedBeforeInscription() {
        assertState(ProofWorkflowState.DEMONSTRATED, true, true, false, false, false);
    }

    @Test
    void pendingInscriptionTakesPriorityOverTheLocalPreviewState() {
        assertState(ProofWorkflowState.INSCRIBING, true, true, true, false, false);
    }

    @Test
    void inscribedProofStillNeedsACompleteResourcePlan() {
        assertState(ProofWorkflowState.WITNESSES_REQUIRED, true, true, false, true, false);
    }

    @Test
    void resourceReadinessCannotHideAnInvalidInscribedProof() {
        assertState(ProofWorkflowState.INCOMPLETE, true, false, false, true, true);
    }

    @Test
    void inscribedProofWithResourcesIsReadyToCast() {
        assertState(ProofWorkflowState.CAST_READY, true, true, false, true, true);
    }

    private static void assertState(
            ProofWorkflowState expected,
            boolean hasPreview,
            boolean valid,
            boolean pending,
            boolean inscribed,
            boolean resourcesReady
    ) {
        assertEquals(
                expected,
                ProofWorkflowState.resolve(hasPreview, valid, pending, inscribed, resourcesReady)
        );
    }
}
