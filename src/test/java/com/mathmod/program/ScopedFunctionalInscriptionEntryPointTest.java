package com.mathmod.program;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScopedFunctionalInscriptionEntryPointTest {
    @Test void exposesOnlyTheoremSpecificInternalOperation() throws Exception {
        String source = java.nio.file.Files.readString(java.nio.file.Path.of(
                "src/main/java/com/mathmod/program/ScopedFunctionalInscriptionEntryPoint.java"));
        assertTrue(source.contains("@ApiStatus.Internal"));
        assertTrue(source.contains("public final class ScopedFunctionalInscriptionEntryPoint"));
        assertEquals(1, source.split("tryInscribeFactoredLeap", -1).length - 1);
        assertTrue(source.contains("public static boolean tryInscribeFactoredLeap(ServerPlayer player, InteractionHand hand, BooleanSupplier requestStillCurrent)"));
    }

    @Test void bridgeStaysBoundToExistingCoordinatorInsteadOfGraphOnlyStorage() throws Exception {
        String source = java.nio.file.Files.readString(java.nio.file.Path.of(
                "src/main/java/com/mathmod/program/ScopedFunctionalInscriptionEntryPoint.java"));
        assertTrue(source.contains("new ScopedFunctionalInscriptionService"));
        assertTrue(source.contains("FactoredLeapTheorem.source()"));
        assertFalse(source.contains("ProgramStorage"));
        assertTrue(source.contains("ScopedCommitResult.SUCCESS"));
    }
}
