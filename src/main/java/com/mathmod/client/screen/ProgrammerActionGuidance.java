package com.mathmod.client.screen;

final class ProgrammerActionGuidance {
    private ProgrammerActionGuidance() {
    }

    static String clearTooltipKey(boolean hasStoredProgram, boolean inscriptionPending) {
        if (inscriptionPending) {
            return "screen.mathmod.rune_programmer.clear_pending_hint";
        }
        return hasStoredProgram
                ? "screen.mathmod.rune_programmer.clear_hint"
                : "screen.mathmod.rune_programmer.clear_empty_hint";
    }

    static String undoTooltipKey(boolean hasWorkspace) {
        return hasWorkspace
                ? "screen.mathmod.rune_programmer.undo_custom_hint"
                : "screen.mathmod.rune_programmer.undo_custom_empty_hint";
    }
}
