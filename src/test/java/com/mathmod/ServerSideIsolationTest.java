package com.mathmod;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerSideIsolationTest {
    @Test
    void minecraftClientImportsStayUnderClientPackage() throws IOException {
        Path sourceRoot = Path.of("src/main/java");
        try (var files = Files.walk(sourceRoot)) {
            var offenders = files
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.toString().contains("\\client\\") && !path.toString().contains("/client/"))
                    .filter(path -> importsMinecraftClient(path))
                    .map(Path::toString)
                    .toList();

            assertTrue(offenders.isEmpty(), "Client-only imports found in common sources: " + offenders);
        }
    }

    @Test
    void authoringReplayAndGuidedPersistenceStayIndependentFromClientPresentation() throws IOException {
        List<Path> commonAuthorities = List.of(
                Path.of("src/main/java/com/mathmod/authoring/AuthoringMetadata.java"),
                Path.of("src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java"),
                Path.of("src/main/java/com/mathmod/authoring/TrustedLegacyExpansionAdapter.java"),
                Path.of("src/main/java/com/mathmod/program/GuidedWorkspaceState.java"),
                Path.of("src/main/java/com/mathmod/program/GuidedWorkspacePersistence.java"),
                Path.of("src/main/java/com/mathmod/program/ScopedFunctionalProjection.java"),
                Path.of("src/main/java/com/mathmod/program/ScopedFunctionalProjectionService.java"),
                Path.of("src/main/java/com/mathmod/program/ScopedFunctionalProjectionWireCodec.java")
        );

        assertTrue(commonAuthorities.stream().noneMatch(ServerSideIsolationTest::importsMinecraftClient),
                "Authoring/persistence authority must remain dedicated-server safe");
    }

    private static boolean importsMinecraftClient(Path path) {
        try {
            return Files.readString(path).contains("net.minecraft.client");
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
