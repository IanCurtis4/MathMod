package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RuneInspectorScreenSourceTest {
    @Test
    void inspectorIsReadOnlyAndProgrammerExposesItForEveryGraphSource() throws Exception {
        String inspector = Files.readString(Path.of("src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java"));
        String programmer = Files.readString(Path.of("src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java"));

        assertTrue(inspector.contains("sends no packets"));
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
}
