package com.mathmod.screen;

import org.junit.jupiter.api.Test;

import com.mathmod.program.ScopedFunctionalProjection;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuneProgrammerProjectionTest {
    @Test void menuOpeningSnapshotKeepsHandAndProjectionAsOneLogicalRecord() {
        ScopedFunctionalProjection projection = new ScopedFunctionalProjection(1,
                ScopedFunctionalProjection.SourceState.ABSENT,
                ScopedFunctionalProjection.AttemptState.NOT_RUN,
                ScopedFunctionalProjection.GraphState.PRESENT,
                ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,
                List.of(), List.of(), List.of(), 0);
        assertEquals(ScopedFunctionalProjection.AttemptState.NOT_RUN, projection.attemptState());
        assertEquals(ScopedFunctionalProjection.GraphState.PRESENT, projection.graphState());
    }
}
