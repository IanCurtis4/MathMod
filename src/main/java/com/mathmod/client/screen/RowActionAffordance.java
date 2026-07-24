package com.mathmod.client.screen;

final class RowActionAffordance {
    static final int TEXT_GAP = 3;
    static final int SCROLLBAR_RESERVE = 5;

    private RowActionAffordance() {
    }

    static Geometry layout(int panelX, int panelWidth, int rowY, int rowHeight, int actionSize) {
        int x = panelX + panelWidth - SCROLLBAR_RESERVE - actionSize;
        int y = rowY + Math.max(0, (rowHeight - actionSize) / 2);
        return new Geometry(x, y, actionSize, actionSize, x - TEXT_GAP);
    }

    static int textWidth(int textX, Geometry geometry) {
        return Math.max(0, geometry.textRight() - textX);
    }

    record Geometry(int x, int y, int width, int height, int textRight) {
    }
}
