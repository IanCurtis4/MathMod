package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectableLineLayoutTest {
    private static final int PANEL_Y = 20;
    private static final int LINE_HEIGHT = 10;
    private static final int VIEWPORT_HEIGHT = 40;

    @Test
    void findsSelectionAfterDiagnosticLines() {
        List<Integer> lines = List.of(-1, -1, -1, 0);

        assertEquals(0, SelectableLineLayout.selectionAt(
                lines, 55, PANEL_Y, LINE_HEIGHT, 0, VIEWPORT_HEIGHT
        ));
    }

    @Test
    void ignoresRowsOutsideViewport() {
        List<Integer> lines = List.of(0, -1, -1, -1, 1);

        assertEquals(-1, SelectableLineLayout.selectionAt(
                lines, 15, PANEL_Y, LINE_HEIGHT, 0, VIEWPORT_HEIGHT
        ));
        assertEquals(-1, SelectableLineLayout.selectionAt(
                lines, 65, PANEL_Y, LINE_HEIGHT, 0, VIEWPORT_HEIGHT
        ));
    }

    @Test
    void reportsOnlyWholeVisibleRows() {
        List<Integer> lines = List.of(-1, 0, -1);

        assertTrue(SelectableLineLayout.firstVisibleRow(
                lines, 0, PANEL_Y, LINE_HEIGHT, 5, VIEWPORT_HEIGHT
        ).isPresent());
        assertTrue(SelectableLineLayout.firstVisibleRow(
                lines, 0, PANEL_Y, LINE_HEIGHT, 15, VIEWPORT_HEIGHT
        ).isEmpty());
    }

    @Test
    void measuresWrappedSelectionSpan() {
        List<Integer> lines = List.of(-1, -1, 2, 2, 2, -1);

        SelectableLineLayout.SelectionSpan span = SelectableLineLayout.span(lines, 2).orElseThrow();

        assertEquals(2, span.firstLine());
        assertEquals(3, span.lineCount());
    }
}
