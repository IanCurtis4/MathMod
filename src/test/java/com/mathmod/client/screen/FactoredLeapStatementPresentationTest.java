package com.mathmod.client.screen;

import com.mathmod.program.ProgramPresets;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FactoredLeapStatementPresentationTest {
    @Test
    void headerGeometryRetainsTheLegacyMinimumForOneAndTwoLines() {
        assertEquals(2, TheoremStatementGeometry.effectiveLineCount(1));
        assertEquals(2, TheoremStatementGeometry.effectiveLineCount(2));
        assertEquals(3, TheoremStatementGeometry.effectiveLineCount(3));
        assertEquals(22, TheoremStatementGeometry.heightForRenderedLineCount(1));
        assertEquals(22, TheoremStatementGeometry.heightForRenderedLineCount(2));
        assertEquals(33, TheoremStatementGeometry.heightForRenderedLineCount(3));
        assertEquals(37, TheoremStatementGeometry.graphViewportOffsetForRenderedLineCount(1));
        assertEquals(37, TheoremStatementGeometry.graphViewportOffsetForRenderedLineCount(2));
        assertEquals(48, TheoremStatementGeometry.graphViewportOffsetForRenderedLineCount(3));
        assertThrows(IllegalStateException.class, () -> TheoremStatementGeometry.effectiveLineCount(4));
    }

    @Test
    void factoredLeapUsesTheFrozenStatementAndTheScreenRejectsAnUnrepresentableFourthLine() throws Exception {
        var preset = ProgramPresets.presetForId("mathmod:factored_leap").orElseThrow();
        String screen = Files.readString(Path.of("src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java"));
        String harness = Files.readString(Path.of("src/main/java/com/mathmod/client/UiPreviewHarness.java"));

        assertEquals("let halve(v)=v*0.5 in push(self,halve(look)+halve((0,1,0)))", preset.formula());
        assertTrue(screen.contains("if (renderedLineCount > 3)"));
        assertTrue(screen.contains("Theorem statement exceeds the supported three-line presentation"));
        assertFalse(screen.contains("Math.min(3, formulaLines.size())"));
        assertTrue(screen.contains("updateTheoremStatementGeometry();\n        updateModeButtons();"));
        assertTrue(screen.contains("TheoremStatementGeometry.graphViewportOffsetForRenderedLineCount(theoremStatementLineCount())"));
        assertTrue(screen.contains("GRAPH_WIDTH - TEXT_PADDING * 2 - theoremStatementInspectorReserve()"));
        assertTrue(screen.contains("return layout.compact() ? 26 : inspectorHeaderReserve();"));
        assertTrue(screen.contains("return layout.compact() ? 20 : 34;"));
        assertTrue(harness.contains(".filter(preset -> TheoremStatementPresentation.lines("));
        assertTrue(harness.contains(").size() > 3"));
        assertTrue(harness.contains("selectFactoredLeapTheorem(screen);"));
        assertTrue(harness.contains("top + graphViewportY(screen) + 16 + 5"));
        assertFalse(harness.contains("previewLayout.panelTop() + 37 + 16 + 5"));
    }
}
