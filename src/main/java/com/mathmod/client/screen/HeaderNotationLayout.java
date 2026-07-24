package com.mathmod.client.screen;

public record HeaderNotationLayout(
        ProgrammerLayout.Rect help,
        ProgrammerLayout.Rect notation
) {
    private static final int RIGHT_PADDING = 12;
    private static final int HELP_GAP = 6;
    private static final int HELP_WIDTH = 18;
    private static final int HELP_HEIGHT = 16;

    public static HeaderNotationLayout alignedRight(
            int surfaceWidth,
            int titleLabelY,
            int notationTextWidth,
            int fontLineHeight
    ) {
        int notationX = surfaceWidth - RIGHT_PADDING - notationTextWidth;
        ProgrammerLayout.Rect notation = new ProgrammerLayout.Rect(
                notationX,
                titleLabelY - 2,
                notationTextWidth + 4,
                fontLineHeight + 4
        );
        ProgrammerLayout.Rect help = new ProgrammerLayout.Rect(
                notationX - HELP_GAP - HELP_WIDTH,
                titleLabelY - 4,
                HELP_WIDTH,
                HELP_HEIGHT
        );
        return new HeaderNotationLayout(help, notation);
    }

    public ProgrammerLayout.Rect leadingAction() {
        return new ProgrammerLayout.Rect(
                help.x() - HELP_GAP - HELP_WIDTH,
                help.y(),
                HELP_WIDTH,
                HELP_HEIGHT
        );
    }
}
