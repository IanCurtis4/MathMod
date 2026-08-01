package com.mathmod.screen;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RuneProgrammerFunctionalTheoremTest {
    @Test void factoredLeapRoutesBeforeGraphOnlyPersistenceAndExistingPresetsRemainGeneric() throws Exception {
        String source = Files.readString(Path.of("src/main/java/com/mathmod/screen/RuneProgrammerMenu.java"));
        int functionalRoute = source.indexOf("mathmod:factored_leap");
        int graphOnlySave = source.indexOf("ProgramStorage.saveValidated(stack, graph)");
        assertTrue(functionalRoute >= 0 && graphOnlySave > functionalRoute);
        assertTrue(source.contains("ScopedFunctionalInscriptionEntryPoint.tryInscribeFactoredLeap"));
        assertTrue(source.contains("serverPlayer.containerMenu == this && stillValid(serverPlayer)"));
        assertTrue(source.contains("if (success) {"));
    }
}
