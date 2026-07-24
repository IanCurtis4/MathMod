package com.mathmod.client.screen;

import java.util.List;
import java.util.Optional;

final class SelectableLineLayout {
    private SelectableLineLayout() {
    }

    static int selectionAt(
            List<Integer> selectionIndices,
            int localY,
            int panelY,
            int lineHeight,
            int scroll,
            int viewportHeight
    ) {
        if (localY < panelY || localY >= panelY + viewportHeight) {
            return -1;
        }
        int lineIndex = (localY - panelY + scroll) / lineHeight;
        if (lineIndex < 0 || lineIndex >= selectionIndices.size()) {
            return -1;
        }
        return selectionIndices.get(lineIndex);
    }

    static Optional<RowBounds> firstVisibleRow(
            List<Integer> selectionIndices,
            int selectionIndex,
            int panelY,
            int lineHeight,
            int scroll,
            int viewportHeight
    ) {
        for (int lineIndex = 0; lineIndex < selectionIndices.size(); lineIndex++) {
            if (selectionIndices.get(lineIndex) != selectionIndex) {
                continue;
            }
            int rowY = panelY + lineIndex * lineHeight - scroll;
            if (rowY >= panelY && rowY + lineHeight <= panelY + viewportHeight) {
                return Optional.of(new RowBounds(rowY, lineHeight));
            }
        }
        return Optional.empty();
    }

    static Optional<SelectionSpan> span(List<Integer> selectionIndices, int selectionIndex) {
        int firstLine = -1;
        int lineCount = 0;
        for (int lineIndex = 0; lineIndex < selectionIndices.size(); lineIndex++) {
            if (selectionIndices.get(lineIndex) == selectionIndex) {
                if (firstLine < 0) {
                    firstLine = lineIndex;
                }
                lineCount++;
            }
        }
        return firstLine < 0
                ? Optional.empty()
                : Optional.of(new SelectionSpan(firstLine, lineCount));
    }

    record RowBounds(int y, int height) {
    }

    record SelectionSpan(int firstLine, int lineCount) {
    }
}
