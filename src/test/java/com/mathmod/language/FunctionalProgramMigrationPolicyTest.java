package com.mathmod.language;

import com.mathmod.runes.ProgramGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionalProgramMigrationPolicyTest {
    @Test
    void legacyGraphsRemainAuthoritativeAndAreNeverRewrittenOnRead() {
        FunctionalProgramMigrationPolicy.Decision decision =
                FunctionalProgramMigrationPolicy.legacyGraphOnly();

        assertEquals(1, ProgramGraph.CURRENT_VERSION);
        assertTrue(decision.compiledGraphRemainsAuthoritative());
        assertTrue(decision.explicitConversionAvailable());
        assertFalse(decision.functionalEditingAvailable());
        assertFalse(decision.rewriteOnRead());
    }

    @Test
    void currentSourceEnablesFunctionalEditingWithoutReplacingCompiledGraph() {
        FunctionalProgramMigrationPolicy.Decision decision =
                FunctionalProgramMigrationPolicy.currentSource();

        assertTrue(decision.compiledGraphRemainsAuthoritative());
        assertTrue(decision.functionalEditingAvailable());
        assertFalse(decision.explicitConversionAvailable());
        assertFalse(decision.rewriteOnRead());
    }

    @Test
    void unsupportedOrUnreadableSourceFallsBackWithoutImplicitConversion() {
        for (FunctionalProgramMigrationPolicy.Decision decision : new FunctionalProgramMigrationPolicy.Decision[]{
                FunctionalProgramMigrationPolicy.unsupportedSource(2),
                FunctionalProgramMigrationPolicy.unreadableCurrentSource()
        }) {
            assertTrue(decision.compiledGraphRemainsAuthoritative());
            assertFalse(decision.functionalEditingAvailable());
            assertFalse(decision.explicitConversionAvailable());
            assertFalse(decision.rewriteOnRead());
        }
    }

    @Test
    void sourceWithoutCompiledGraphIsRecoveryOnly() {
        FunctionalProgramMigrationPolicy.Decision decision =
                FunctionalProgramMigrationPolicy.sourceWithoutCompiledGraph();

        assertFalse(decision.compiledGraphRemainsAuthoritative());
        assertFalse(decision.functionalEditingAvailable());
        assertFalse(decision.explicitConversionAvailable());
        assertFalse(decision.rewriteOnRead());
    }
}
