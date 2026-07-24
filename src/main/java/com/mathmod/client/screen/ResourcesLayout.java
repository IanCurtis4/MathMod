package com.mathmod.client.screen;

import java.util.List;

public record ResourcesLayout(
        int width,
        int height,
        ProgrammerLayout.Rect leftPanel,
        ProgrammerLayout.Rect rightPanel,
        List<ProgrammerLayout.Rect> actions,
        int bottomPadding,
        boolean compact
) {
    private static final int STANDARD_WIDTH = 360;
    private static final int COMPACT_WIDTH = 333;
    private static final int ITEM_OVERLAY_SIDE_RESERVE = 64;
    private static final int ITEM_OVERLAY_BOTTOM_RESERVE = 26;
    private static final int OVERLAY_COMPACT_MIN_HEIGHT = 188;
    static final int COMPACT_CONFIRM_ACTION_WIDTH = 68;

    public ResourcesLayout {
        actions = List.copyOf(actions);
    }

    public static ResourcesLayout forViewport(int viewportWidth, int viewportHeight) {
        return forViewport(viewportWidth, viewportHeight, false);
    }

    public static ResourcesLayout forViewport(int viewportWidth, int viewportHeight, boolean reserveItemOverlay) {
        boolean overlayNeedsCompact = reserveItemOverlay
                && viewportWidth < STANDARD_WIDTH + ITEM_OVERLAY_SIDE_RESERVE * 2;
        boolean compact = overlayNeedsCompact || viewportWidth < 380 || viewportHeight < 268;
        if (!compact) {
            return new ResourcesLayout(
                    STANDARD_WIDTH,
                    248,
                    new ProgrammerLayout.Rect(12, 78, 184, 154),
                    new ProgrammerLayout.Rect(204, 78, 144, 154),
                    List.of(
                            new ProgrammerLayout.Rect(12, 30, 72, 20),
                            new ProgrammerLayout.Rect(92, 30, 72, 20),
                            new ProgrammerLayout.Rect(172, 30, 72, 20)
                    ),
                    12,
                    false
            );
        }

        int widthCap = reserveItemOverlay
                ? Math.min(COMPACT_WIDTH, viewportWidth - ITEM_OVERLAY_SIDE_RESERVE * 2)
                : COMPACT_WIDTH;
        int width = Math.max(292, Math.min(Math.max(292, widthCap), viewportWidth - 8));
        boolean reserveOverlayFooter = reserveItemOverlay
                && (viewportWidth - width) / 2 < ITEM_OVERLAY_SIDE_RESERVE;
        int heightCap = reserveOverlayFooter
                ? viewportHeight - ITEM_OVERLAY_BOTTOM_RESERVE * 2
                : viewportHeight - 8;
        int minimumHeight = reserveOverlayFooter ? OVERLAY_COMPACT_MIN_HEIGHT : 220;
        int height = Math.max(minimumHeight, Math.min(240, heightCap));
        int usableWidth = width - 28;
        int leftWidth = Math.max(150, usableWidth * 56 / 100);
        int rightWidth = usableWidth - leftWidth;
        int panelHeight = height - 78 - 10;

        return new ResourcesLayout(
                width,
                height,
                new ProgrammerLayout.Rect(10, 78, leftWidth, panelHeight),
                new ProgrammerLayout.Rect(18 + leftWidth, 78, rightWidth, panelHeight),
                List.of(
                        new ProgrammerLayout.Rect(10, 30, 64, 20),
                        new ProgrammerLayout.Rect(78, 30, COMPACT_CONFIRM_ACTION_WIDTH, 20),
                        new ProgrammerLayout.Rect(150, 30, 60, 20)
                ),
                10,
                true
        );
    }
}
