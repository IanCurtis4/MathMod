package com.mathmod.client.screen;

record LoadoutHeadingLayout(
        int separatorX,
        int nameX,
        int availableNameWidth,
        int visibleNameWidth,
        boolean nameClipped
) {
    static LoadoutHeadingLayout forWidths(
            int panelX,
            int panelWidth,
            int labelWidth,
            int separatorWidth,
            int nameWidth
    ) {
        int panelRight = panelX + Math.max(0, panelWidth);
        int separatorX = Math.min(panelRight, panelX + Math.max(0, labelWidth));
        int nameX = Math.min(panelRight, separatorX + Math.max(0, separatorWidth));
        int availableNameWidth = Math.max(0, panelRight - nameX);
        int visibleNameWidth = Math.min(Math.max(0, nameWidth), availableNameWidth);
        return new LoadoutHeadingLayout(
                separatorX,
                nameX,
                availableNameWidth,
                visibleNameWidth,
                nameWidth > availableNameWidth
        );
    }

    boolean isOverClippedName(double mouseX) {
        return nameClipped
                && visibleNameWidth > 0
                && mouseX >= nameX
                && mouseX < nameX + visibleNameWidth;
    }
}
