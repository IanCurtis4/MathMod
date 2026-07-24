package com.mathmod.client.screen;

enum ProofWorkflowState {
    EMPTY,
    INCOMPLETE,
    DEMONSTRATED,
    INSCRIBING,
    WITNESSES_REQUIRED,
    CAST_READY;

    static ProofWorkflowState resolve(
            boolean hasPreview,
            boolean valid,
            boolean inscriptionPending,
            boolean inscribed,
            boolean resourcesReady
    ) {
        if (!hasPreview) {
            return EMPTY;
        }
        if (inscriptionPending) {
            return INSCRIBING;
        }
        if (!valid) {
            return INCOMPLETE;
        }
        if (!inscribed) {
            return DEMONSTRATED;
        }
        return resourcesReady ? CAST_READY : WITNESSES_REQUIRED;
    }
}
