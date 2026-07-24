package com.mathmod.client.screen;

final class ProofWorkflowPresentation {
    static final int COMPACT_SEAL_WIDTH = 10;
    static final int STANDARD_SEAL_WIDTH = 86;
    static final int SEAL_GAP = 4;
    static final int LABELED_GRAPH_MIN_WIDTH = 176;

    private ProofWorkflowPresentation() {
    }

    static int sealWidth(int graphWidth, int fixedHeaderWidth, int titleWidth) {
        boolean titleFits = graphWidth
                - fixedHeaderWidth
                - STANDARD_SEAL_WIDTH
                - SEAL_GAP
                >= titleWidth;
        return graphWidth >= LABELED_GRAPH_MIN_WIDTH && titleFits
                ? STANDARD_SEAL_WIDTH
                : COMPACT_SEAL_WIDTH;
    }

    static String translationKey(ProofWorkflowState state) {
        return "screen.mathmod.rune_programmer.workflow." + statePath(state);
    }

    static String shortTranslationKey(ProofWorkflowState state) {
        return translationKey(state) + ".short";
    }

    private static String statePath(ProofWorkflowState state) {
        return switch (state) {
            case EMPTY -> "empty";
            case INCOMPLETE -> "incomplete";
            case DEMONSTRATED -> "demonstrated";
            case INSCRIBING -> "inscribing";
            case WITNESSES_REQUIRED -> "witnesses_required";
            case CAST_READY -> "cast_ready";
        };
    }
}
