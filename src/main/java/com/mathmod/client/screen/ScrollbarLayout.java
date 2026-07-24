package com.mathmod.client.screen;

final class ScrollbarLayout {
    static final int VISUAL_WIDTH = 2;
    static final int HIT_PADDING = 3;
    private static final int MIN_THUMB_HEIGHT = 12;

    private ScrollbarLayout() {
    }

    static Geometry geometry(
            int trackX,
            int trackY,
            int trackHeight,
            int viewportHeight,
            int contentHeight,
            int scroll,
            int maxScroll
    ) {
        int safeTrackHeight = Math.max(1, trackHeight);
        int safeViewportHeight = Math.max(1, viewportHeight);
        int safeContentHeight = Math.max(safeViewportHeight, contentHeight);
        int safeMaxScroll = Math.max(0, maxScroll);
        int thumbHeight = Math.min(
                safeTrackHeight,
                Math.max(
                        MIN_THUMB_HEIGHT,
                        safeTrackHeight * safeViewportHeight / safeContentHeight
                )
        );
        int thumbTravel = safeTrackHeight - thumbHeight;
        int clampedScroll = Math.max(0, Math.min(safeMaxScroll, scroll));
        int thumbY = safeMaxScroll == 0
                ? trackY
                : trackY + clampedScroll * thumbTravel / safeMaxScroll;
        return new Geometry(
                trackX,
                trackY,
                safeTrackHeight,
                thumbY,
                thumbHeight,
                safeMaxScroll
        );
    }

    static int nearestStep(int desired, int step, int maxScroll) {
        int clamped = Math.max(0, Math.min(maxScroll, desired));
        if (step <= 0 || clamped == maxScroll) {
            return clamped;
        }
        int lower = clamped / step * step;
        int upper = Math.min(maxScroll, lower + step);
        return clamped - lower <= upper - clamped ? lower : upper;
    }

    record Geometry(
            int trackX,
            int trackY,
            int trackHeight,
            int thumbY,
            int thumbHeight,
            int maxScroll
    ) {
        boolean scrollable() {
            return maxScroll > 0 && trackHeight > thumbHeight;
        }

        boolean contains(double mouseX, double mouseY) {
            return scrollable()
                    && mouseX >= trackX - HIT_PADDING
                    && mouseX < trackX + VISUAL_WIDTH + HIT_PADDING
                    && mouseY >= trackY
                    && mouseY < trackY + trackHeight;
        }

        boolean containsThumb(double mouseY) {
            return scrollable() && mouseY >= thumbY && mouseY < thumbY + thumbHeight;
        }

        int dragOffset(double mouseY) {
            if (!containsThumb(mouseY)) {
                return thumbHeight / 2;
            }
            return Math.max(0, Math.min(thumbHeight - 1, (int) mouseY - thumbY));
        }

        int scrollAt(double mouseY, int dragOffset) {
            if (!scrollable()) {
                return 0;
            }
            int thumbTravel = trackHeight - thumbHeight;
            int desiredThumbY = (int) Math.round(mouseY - dragOffset);
            int clampedThumbY = Math.max(trackY, Math.min(trackY + thumbTravel, desiredThumbY));
            return (int) Math.round(
                    (clampedThumbY - trackY) * maxScroll / (double) thumbTravel
            );
        }
    }
}
