package com.mathmod.client.screen;

final class SavedProofGuidance {
    private SavedProofGuidance() {
    }

    static String translationKey(ProofWorkflowState state, boolean customProof) {
        return switch (state) {
            case WITNESSES_REQUIRED -> "screen.mathmod.rune_programmer.saved_witnesses_hint";
            case CAST_READY -> "screen.mathmod.rune_programmer.saved_cast_ready_hint";
            default -> customProof
                    ? "screen.mathmod.rune_programmer.saved_custom_hint"
                    : "screen.mathmod.rune_programmer.saved_preset_hint";
        };
    }
}
