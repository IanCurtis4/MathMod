package com.mathmod.client.screen;

final class TooltipBoundsPolicy {
    static final int CONTENT_INSET = 8;

    private TooltipBoundsPolicy() {
    }

    static Position boundedPosition(
            int preferredX,
            int preferredY,
            int screenWidth,
            int screenHeight,
            int tooltipWidth,
            int tooltipHeight
    ) {
        int maximumX = screenWidth - tooltipWidth - CONTENT_INSET;
        int maximumY = screenHeight - tooltipHeight - CONTENT_INSET;
        return new Position(
                clampToViewport(preferredX, maximumX),
                clampToViewport(preferredY, maximumY)
        );
    }

    private static int clampToViewport(int preferred, int maximum) {
        if (maximum < CONTENT_INSET) {
            return Math.max(0, maximum);
        }
        return Math.max(CONTENT_INSET, Math.min(preferred, maximum));
    }

    record Position(int x, int y) {
    }
}
