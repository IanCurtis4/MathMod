package com.mathmod.client.screen;

import java.util.List;

public record ProgrammerLayout(
        int width,
        int height,
        int panelTop,
        int bottomPadding,
        Rect palette,
        Rect graph,
        List<Rect> tabs,
        List<Rect> savedActions,
        List<Rect> presetActions,
        Rect customName,
        List<Rect> customActions,
        boolean compact
) {
    private static final int STANDARD_WIDTH = 396;
    private static final int STANDARD_HEIGHT = 264;
    private static final int COMPACT_WIDTH = 330;
    private static final int COMPACT_HEIGHT = 250;
    private static final int ITEM_OVERLAY_SIDE_RESERVE = 64;
    private static final int ITEM_OVERLAY_BOTTOM_RESERVE = 26;
    private static final int OVERLAY_COMPACT_MIN_HEIGHT = 188;
    static final int STANDARD_RESOURCE_ACTION_WIDTH = 72;
    static final int COMPACT_CONFIRM_ACTION_WIDTH = 64;
    static final int COMPACT_PRIMARY_ACTION_WIDTH = 70;
    static final int COMPACT_ICON_ACTION_WIDTH = 26;

    public ProgrammerLayout {
        tabs = List.copyOf(tabs);
        savedActions = List.copyOf(savedActions);
        presetActions = List.copyOf(presetActions);
        customActions = List.copyOf(customActions);
    }

    public static ProgrammerLayout forViewport(int viewportWidth, int viewportHeight) {
        return forViewport(viewportWidth, viewportHeight, false);
    }

    public static ProgrammerLayout forViewport(int viewportWidth, int viewportHeight, boolean reserveItemOverlay) {
        boolean overlayNeedsCompact = reserveItemOverlay
                && viewportWidth < STANDARD_WIDTH + ITEM_OVERLAY_SIDE_RESERVE * 2;
        boolean compact = overlayNeedsCompact || viewportWidth < 436 || viewportHeight < 292;
        if (!compact) {
            return standard();
        }
        int widthCap = reserveItemOverlay
                ? Math.min(COMPACT_WIDTH, viewportWidth - ITEM_OVERLAY_SIDE_RESERVE * 2)
                : COMPACT_WIDTH;
        return compact(viewportWidth, viewportHeight, Math.max(292, widthCap), reserveItemOverlay);
    }

    public Rect customSearch() {
        return new Rect(palette.x() + 5, panelTop + 20, palette.width() - 10, 18);
    }

    public Rect customSearchContent() {
        return customSearch().inset(4, 1);
    }

    public Rect customNameContent() {
        return customName.inset(4, 1);
    }

    public Rect theoremTab() {
        return tabs.get(0);
    }

    public Rect laboratoryTab() {
        return tabs.get(1);
    }

    public Rect inscribedTab() {
        return tabs.get(2);
    }

    private static ProgrammerLayout standard() {
        return new ProgrammerLayout(
                STANDARD_WIDTH,
                STANDARD_HEIGHT,
                78,
                12,
                new Rect(12, 78, 150, 174),
                new Rect(170, 78, 214, 174),
                List.of(
                        new Rect(12, 20, 76, 20),
                        new Rect(94, 20, 88, 20),
                        new Rect(188, 20, 88, 20)
                ),
                List.of(
                        new Rect(12, 48, 92, 20),
                        new Rect(112, 48, 82, 20),
                        new Rect(202, 48, 88, 20),
                        new Rect(298, 48, 72, 20)
                ),
                List.of(
                        new Rect(12, 48, 106, 20),
                        new Rect(126, 48, 88, 20),
                        new Rect(222, 48, 72, 20)
                ),
                new Rect(12, 49, 112, 18),
                List.of(
                        new Rect(130, 48, 82, 20),
                        new Rect(218, 48, 46, 20),
                        new Rect(270, 48, 46, 20),
                        new Rect(322, 48, STANDARD_RESOURCE_ACTION_WIDTH, 20)
                ),
                false
        );
    }

    private static ProgrammerLayout compact(
            int viewportWidth,
            int viewportHeight,
            int widthCap,
            boolean reserveItemOverlay
    ) {
        int width = Math.max(292, Math.min(widthCap, viewportWidth - 8));
        boolean reserveOverlayFooter = reserveItemOverlay
                && (viewportWidth - width) / 2 < ITEM_OVERLAY_SIDE_RESERVE;
        int heightCap = reserveOverlayFooter
                ? viewportHeight - ITEM_OVERLAY_BOTTOM_RESERVE * 2
                : viewportHeight - 8;
        int minimumHeight = reserveOverlayFooter ? OVERLAY_COMPACT_MIN_HEIGHT : 220;
        int height = Math.max(minimumHeight, Math.min(COMPACT_HEIGHT, heightCap));
        int paletteWidth = Math.max(104, Math.min(120, width - 178));
        int graphX = 10 + paletteWidth + 8;
        int graphWidth = width - graphX - 10;
        int panelHeight = height - 78 - 10;
        int compactGap = 4;
        int customNameWidth = width
                - 20
                - COMPACT_PRIMARY_ACTION_WIDTH
                - COMPACT_ICON_ACTION_WIDTH * 3
                - compactGap * 4;
        int customSaveX = 10 + customNameWidth + compactGap;
        int customUndoX = customSaveX + COMPACT_PRIMARY_ACTION_WIDTH + compactGap;
        int customResetX = customUndoX + COMPACT_ICON_ACTION_WIDTH + compactGap;
        int customResourcesX = customResetX + COMPACT_ICON_ACTION_WIDTH + compactGap;

        return new ProgrammerLayout(
                width,
                height,
                78,
                10,
                new Rect(10, 78, paletteWidth, panelHeight),
                new Rect(graphX, 78, graphWidth, panelHeight),
                List.of(
                        new Rect(10, 20, 64, 20),
                        new Rect(78, 20, 72, 20),
                        new Rect(154, 20, 80, 20)
                ),
                List.of(
                        new Rect(10, 48, 70, 20),
                        new Rect(84, 48, 62, 20),
                        new Rect(150, 48, 68, 20),
                        new Rect(222, 48, COMPACT_CONFIRM_ACTION_WIDTH, 20)
                ),
                List.of(
                        new Rect(10, 48, 86, 20),
                        new Rect(100, 48, 72, 20),
                        new Rect(176, 48, COMPACT_CONFIRM_ACTION_WIDTH, 20)
                ),
                new Rect(10, 49, customNameWidth, 18),
                List.of(
                        new Rect(customSaveX, 48, COMPACT_PRIMARY_ACTION_WIDTH, 20),
                        new Rect(customUndoX, 48, COMPACT_ICON_ACTION_WIDTH, 20),
                        new Rect(customResetX, 48, COMPACT_ICON_ACTION_WIDTH, 20),
                        new Rect(customResourcesX, 48, COMPACT_ICON_ACTION_WIDTH, 20)
                ),
                true
        );
    }

    public record Rect(int x, int y, int width, int height) {
        public int right() {
            return x + width;
        }

        public int bottom() {
            return y + height;
        }

        public boolean overlaps(Rect other) {
            return x < other.right() && right() > other.x
                    && y < other.bottom() && bottom() > other.y;
        }

        public Rect inset(int horizontal, int vertical) {
            int safeHorizontal = Math.max(0, horizontal);
            int safeVertical = Math.max(0, vertical);
            return new Rect(
                    x + safeHorizontal,
                    y + safeVertical,
                    Math.max(0, width - safeHorizontal * 2),
                    Math.max(0, height - safeVertical * 2)
            );
        }
    }
}
