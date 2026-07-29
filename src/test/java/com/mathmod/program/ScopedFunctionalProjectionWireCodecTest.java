package com.mathmod.program;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScopedFunctionalProjectionWireCodecTest {
    @Test void freezesProjectionOnlyWireLimitsAndLogicalFields() {
        var projection = new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.CURRENT_VALID,
                ScopedFunctionalProjection.AttemptState.SUCCESS,
                ScopedFunctionalProjection.GraphState.PRESENT,
                ScopedFunctionalProjection.GraphRelation.MISMATCH,
                List.of(new ScopedFunctionalProjection.Row("$.body", ScopedFunctionalProjection.RowKind.PARAMETER_REFERENCE, "#0", "hint", 0, 1)),
                List.of(new ScopedFunctionalProjection.Row("$.body", ScopedFunctionalProjection.RowKind.PARAMETER_REFERENCE, "#0", "", 0, 1)),
                List.of(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.MISMATCH, ScopedFunctionalProjection.Code.MISMATCH, "$")), 7);
        assertEquals(65_536, ScopedFunctionalProjectionWireCodec.MAX_BYTES);
        assertEquals("#0", projection.checkedRows().getFirst().primaryToken());
        assertEquals(ScopedFunctionalProjection.GraphRelation.MISMATCH, projection.graphRelation());
    }

    @Test void rejectsUtf8ValuesThatCouldExceedTheFrozenFrame() {
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection.Row(
                "x".repeat(513), ScopedFunctionalProjection.RowKind.RESULT, "", "", -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ScopedFunctionalProjection.Row(
                "$", ScopedFunctionalProjection.RowKind.RESULT, "é".repeat(129), "", -1, 0));
    }
}
