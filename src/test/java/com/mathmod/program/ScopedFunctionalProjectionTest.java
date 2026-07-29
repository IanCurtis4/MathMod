package com.mathmod.program;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScopedFunctionalProjectionTest {
    @Test void unavailableSnapshotIsBoundedAndNonExecutable() {
        var projection=ScopedFunctionalProjection.unavailable();
        assertEquals(ScopedFunctionalProjection.SourceState.STALE,projection.sourceState());
        assertEquals(ScopedFunctionalProjection.AttemptState.AUTHORITY_STALE,projection.attemptState());
        assertEquals(ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,projection.graphRelation());
        assertEquals(0,projection.chargedSteps());
    }

    @Test void staleSnapshotPreservesGraphPresenceWithoutAStaleRelationClaim() {
        var stale = ScopedFunctionalProjection.unavailable(ScopedFunctionalProjection.GraphState.PRESENT);
        assertEquals(ScopedFunctionalProjection.GraphState.PRESENT, stale.graphState());
        assertEquals(ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE, stale.graphRelation());
        assertTrue(stale.authoredRows().isEmpty());
        assertTrue(stale.checkedRows().isEmpty());
    }

    @Test void successCanMatchOrMismatchOnlyAgainstAPresentGraph() {
        var match = new ScopedFunctionalProjection(1, ScopedFunctionalProjection.SourceState.CURRENT_VALID,
                ScopedFunctionalProjection.AttemptState.SUCCESS, ScopedFunctionalProjection.GraphState.PRESENT,
                ScopedFunctionalProjection.GraphRelation.MATCH, List.of(row()), List.of(row()), List.of(), 1);
        var mismatch = new ScopedFunctionalProjection(1, ScopedFunctionalProjection.SourceState.CURRENT_VALID,
                ScopedFunctionalProjection.AttemptState.SUCCESS, ScopedFunctionalProjection.GraphState.PRESENT,
                ScopedFunctionalProjection.GraphRelation.MISMATCH, List.of(row()), List.of(row()),
                List.of(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.MISMATCH,
                        ScopedFunctionalProjection.Code.MISMATCH, "$")), 1);
        assertEquals(ScopedFunctionalProjection.GraphRelation.MATCH, match.graphRelation());
        assertEquals(ScopedFunctionalProjection.GraphRelation.MISMATCH, mismatch.graphRelation());
    }

    @Test void rejectsProjectionBoundsBeforeAcceptingListsOrRows() {
        var row=new ScopedFunctionalProjection.Row("$",ScopedFunctionalProjection.RowKind.RESULT,"","",-1,0);
        assertThrows(IllegalArgumentException.class,()->new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.ABSENT,ScopedFunctionalProjection.AttemptState.NOT_RUN,
                ScopedFunctionalProjection.GraphState.ABSENT,ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,
                java.util.Collections.nCopies(257,row),List.of(),List.of(),0));
        assertThrows(IllegalArgumentException.class,()->new ScopedFunctionalProjection.Row("$",ScopedFunctionalProjection.RowKind.RESULT,"x".repeat(257),"",-1,0));
    }

    @Test void rejectsNullAndInseparableStateCombinations() {
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection(1, null,
                ScopedFunctionalProjection.AttemptState.NOT_RUN, ScopedFunctionalProjection.GraphState.ABSENT,
                ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE, List.of(), List.of(), List.of(), 0));
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.ABSENT, ScopedFunctionalProjection.AttemptState.SUCCESS,
                ScopedFunctionalProjection.GraphState.ABSENT, ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,
                List.of(), List.of(row()), List.of(), 0));
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.CURRENT_VALID, ScopedFunctionalProjection.AttemptState.SUCCESS,
                ScopedFunctionalProjection.GraphState.ABSENT, ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,
                List.of(), List.of(), List.of(), 0));
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.ABSENT, ScopedFunctionalProjection.AttemptState.NOT_RUN,
                ScopedFunctionalProjection.GraphState.ABSENT, ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,
                List.of(), List.of(), List.of(), 1));
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.STALE, ScopedFunctionalProjection.AttemptState.AUTHORITY_STALE,
                ScopedFunctionalProjection.GraphState.ABSENT, ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,
                List.of(row()), List.of(), List.of(), 0));
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection.Row("$", null, "", "", -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection.Diagnostic(null,
                ScopedFunctionalProjection.Code.STALE, "$"));
    }

    @Test void defensivelyCopiesProjectionCollections() {
        var source = new java.util.ArrayList<>(List.of(row()));
        var projection = new ScopedFunctionalProjection(1, ScopedFunctionalProjection.SourceState.CURRENT_VALID,
                ScopedFunctionalProjection.AttemptState.LANGUAGE_REJECTED, ScopedFunctionalProjection.GraphState.ABSENT,
                ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE, source, List.of(),
                List.of(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.LANGUAGE,
                        ScopedFunctionalProjection.Code.LANGUAGE_REJECTED, "$")), 0);
        source.clear();
        assertEquals(1, projection.authoredRows().size());
        assertThrows(UnsupportedOperationException.class, () -> projection.authoredRows().clear());
    }

    @Test void rejectsEveryImpossibleSourceRowAttemptAndRelationCombination() {
        for (ScopedFunctionalProjection.SourceState source : List.of(ScopedFunctionalProjection.SourceState.ABSENT,
                ScopedFunctionalProjection.SourceState.CURRENT_UNREADABLE, ScopedFunctionalProjection.SourceState.UNSUPPORTED_VERSION,
                ScopedFunctionalProjection.SourceState.CONFLICT)) {
            assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection(1, source,
                    ScopedFunctionalProjection.AttemptState.NOT_RUN, ScopedFunctionalProjection.GraphState.ABSENT,
                    ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE, List.of(row()), List.of(), List.of(), 0));
        }
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.CURRENT_VALID, ScopedFunctionalProjection.AttemptState.LANGUAGE_REJECTED,
                ScopedFunctionalProjection.GraphState.PRESENT, ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,
                List.of(row()), List.of(row()), List.of(languageDiagnostic()), 0));
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.CURRENT_VALID, ScopedFunctionalProjection.AttemptState.SUCCESS,
                ScopedFunctionalProjection.GraphState.PRESENT, ScopedFunctionalProjection.GraphRelation.MATCH,
                List.of(), List.of(row()), List.of(), 0));
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.CURRENT_VALID, ScopedFunctionalProjection.AttemptState.LANGUAGE_REJECTED,
                ScopedFunctionalProjection.GraphState.PRESENT, ScopedFunctionalProjection.GraphRelation.MATCH,
                List.of(row()), List.of(), List.of(languageDiagnostic()), 0));
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.CURRENT_VALID, ScopedFunctionalProjection.AttemptState.SUCCESS,
                ScopedFunctionalProjection.GraphState.PRESENT, ScopedFunctionalProjection.GraphRelation.MATCH,
                List.of(row()), List.of(row()), List.of(languageDiagnostic()), 0));
    }

    private static ScopedFunctionalProjection.Row row() {
        return new ScopedFunctionalProjection.Row("$", ScopedFunctionalProjection.RowKind.RESULT, "", "", -1, 0);
    }
    private static ScopedFunctionalProjection.Diagnostic languageDiagnostic() {
        return new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.LANGUAGE,
                ScopedFunctionalProjection.Code.LANGUAGE_REJECTED, "$");
    }
}
