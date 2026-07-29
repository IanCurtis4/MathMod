package com.mathmod.client.screen;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuneInspectorScreenSourceTest {
    @Test
    void inspectorIsReadOnlyAndProgrammerExposesItForEveryGraphSource() throws Exception {
        String inspector = Files.readString(Path.of("src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java"));
        String programmer = Files.readString(Path.of("src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java"));

        assertTrue(inspector.contains("sends no packets"));
        assertTrue(inspector.contains("getNarrationMessage"));
        assertTrue(inspector.contains("inputSocketY(to, edge.inputName(), toRect)"));
        assertTrue(inspector.contains("node.inputNames()"));
        assertTrue(inspector.contains("viewport.zoomBy"));
        assertTrue(inspector.contains("nodeAt(mouseX, mouseY)"));
        assertTrue(inspector.contains("moveSelection(1)"));
        assertTrue(inspector.contains("enableScissor"));
        assertTrue(inspector.contains("mouseDragged"));
        assertTrue(inspector.contains("viewport.pan(mouseX - lastPanX"));
        assertTrue(inspector.contains("screen.mathmod.rune_inspector.read_only"));
        assertTrue(inspector.contains("drawScaledText(graphics, edge.inputName()"));
        assertTrue(inspector.contains("contentRect()"));
        assertTrue(inspector.contains("screen.mathmod.rune_inspector.input_sockets"));
        assertTrue(inspector.contains("screen.mathmod.rune_inspector.output_socket"));
        assertTrue(inspector.contains("screen.mathmod.rune_inspector.viewport"));
        assertTrue(inspector.contains("One shared geometry model for selector hit targets"));
        assertTrue(inspector.contains("font.split(heading"));
        assertTrue(inspector.contains("functionalLayoutContained"));
        assertTrue(inspector.contains("panelLabel(panel)"));
        assertTrue(inspector.contains("Math.max(0, Math.min(rows.size() - 1"));
        assertFalse(inspector.contains("Math.floorMod(functionalRow"));
        assertTrue(!inspector.contains("screen.mathmod.rune_inspector.formula\", narration.socketBindings()"));
        assertTrue(programmer.contains("case SAVED -> ProgramSurface.inscribed(preview)"));
        assertTrue(programmer.contains("case PRESETS -> ProgramSurface.theorem(preview)"));
        assertTrue(programmer.contains("case CUSTOM -> ProgramSurface.guided"));
        assertTrue(programmer.contains("source.inspect()"));
    }

    @Test
    void manuscriptTheoremRouteUsesTheReadOnlyInspector() throws Exception {
        String reader = Files.readString(Path.of(
                "src/main/java/com/mathmod/client/screen/ManuscriptReaderScreen.java"
        ));

        assertTrue(reader.contains("new RuneInspectorScreen"));
        assertTrue(reader.contains("ProgramSurface.theorem"));
        assertTrue(!reader.contains("RuneProgrammerMenu"));
        assertTrue(!reader.contains("OpenManuscriptTheoremPayload"));
    }

    @Test
    void inspectorNarrationKeysAreValidJsonAndHaveEnglishBrazilianPortugueseParity() throws Exception {
        JsonObject english = JsonParser.parseString(Files.readString(Path.of(
                "src/main/resources/assets/mathmod/lang/en_us.json"))).getAsJsonObject();
        JsonObject portuguese = JsonParser.parseString(Files.readString(Path.of(
                "src/main/resources/assets/mathmod/lang/pt_br.json"))).getAsJsonObject();
        List<String> keys = List.of(
                "screen.mathmod.rune_inspector.input_sockets",
                "screen.mathmod.rune_inspector.output_socket",
                "screen.mathmod.rune_inspector.viewport"
        );

        for (String key : keys) {
            assertTrue(english.has(key), "missing English key " + key);
            assertTrue(portuguese.has(key), "missing Brazilian Portuguese key " + key);
            assertEquals(english.get(key).getAsString().chars().filter(character -> character == '%').count(),
                    portuguese.get(key).getAsString().chars().filter(character -> character == '%').count(),
                    "format-token parity for " + key);
        }
    }

    @Test
    void functionalProjectionStatesAndRowsHaveLocalizedPlayerCopy() throws Exception {
        JsonObject english = JsonParser.parseString(Files.readString(Path.of(
                "src/main/resources/assets/mathmod/lang/en_us.json"))).getAsJsonObject();
        JsonObject portuguese = JsonParser.parseString(Files.readString(Path.of(
                "src/main/resources/assets/mathmod/lang/pt_br.json"))).getAsJsonObject();
        List<String> keys = List.of(
                "functional.source.absent", "functional.source.current_valid", "functional.source.current_unreadable",
                "functional.source.unsupported_version", "functional.source.conflict", "functional.source.stale",
                "functional.attempt.not_run", "functional.attempt.success", "functional.attempt.language_rejected",
                "functional.attempt.admission_rejected", "functional.attempt.authority_stale",
                "functional.graph.absent", "functional.graph.present", "functional.relation.not_comparable",
                "functional.relation.match", "functional.relation.mismatch", "functional.diagnostic.absent",
                "functional.diagnostic.unreadable", "functional.diagnostic.unsupported", "functional.diagnostic.conflict",
                "functional.diagnostic.language_rejected", "functional.diagnostic.admission_rejected",
                "functional.diagnostic.mismatch", "functional.diagnostic.stale", "functional.row.literal",
                "functional.row.parameter_reference", "functional.row.rune_call", "functional.row.rune_argument",
                "functional.row.lambda", "functional.row.application", "functional.row.let", "functional.row.result",
                "functional.panel.authored", "functional.panel.checked", "functional.panel.graph"
        );
        for (String suffix : keys) {
            String key = "screen.mathmod.rune_inspector." + suffix;
            assertTrue(english.has(key), "missing English functional copy " + key);
            assertTrue(portuguese.has(key), "missing Brazilian Portuguese functional copy " + key);
            assertFalse(english.get(key).getAsString().isBlank(), "blank English functional copy " + key);
            assertFalse(portuguese.get(key).getAsString().isBlank(), "blank Brazilian Portuguese functional copy " + key);
        }
    }
}
