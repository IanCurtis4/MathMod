package com.mathmod.client.screen;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.program.CustomSpellAction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InscriptionFeedbackTrackerTest {
    private static final ProgramGraph EXPECTED = graph("expected");
    private static final ProgramGraph OTHER = graph("other");

    @Test
    void confirmsOnlyWhenTheExpectedProgramReachesTheHeldTalisman() {
        InscriptionFeedbackTracker tracker = new InscriptionFeedbackTracker();
        tracker.begin(InscriptionTarget.preset(EXPECTED));

        assertEquals(InscriptionFeedbackTracker.Event.NONE, tracker.tick(InscriptionTarget.preset(OTHER)));
        assertTrue(tracker.pending());
        assertEquals(InscriptionFeedbackTracker.Event.CONFIRMED, tracker.tick(InscriptionTarget.preset(EXPECTED)));
        assertEquals(InscriptionFeedbackTracker.Status.SUCCESS, tracker.status());
        assertFalse(tracker.pending());
    }

    @Test
    void sameGraphDoesNotConfirmUntilCustomNameAndSequenceMatch() {
        InscriptionFeedbackTracker tracker = new InscriptionFeedbackTracker();
        InscriptionTarget expected = InscriptionTarget.custom(
                EXPECTED,
                "New Name",
                List.of(CustomSpellAction.PUSH_SELF)
        );
        tracker.begin(expected);

        assertEquals(InscriptionFeedbackTracker.Event.NONE, tracker.tick(InscriptionTarget.custom(
                EXPECTED,
                "Old Name",
                List.of(CustomSpellAction.PUSH_SELF)
        )));
        assertEquals(InscriptionFeedbackTracker.Event.NONE, tracker.tick(InscriptionTarget.custom(
                EXPECTED,
                "New Name",
                List.of()
        )));
        assertEquals(InscriptionFeedbackTracker.Event.CONFIRMED, tracker.tick(expected));
    }

    @Test
    void timesOutWhenTheServerNeverConfirmsTheProgram() {
        InscriptionFeedbackTracker tracker = new InscriptionFeedbackTracker();
        tracker.begin(InscriptionTarget.preset(EXPECTED));

        InscriptionFeedbackTracker.Event event = InscriptionFeedbackTracker.Event.NONE;
        for (int tick = 0; tick < InscriptionFeedbackTracker.PENDING_TICKS; tick++) {
            event = tracker.tick(InscriptionTarget.empty());
        }

        assertEquals(InscriptionFeedbackTracker.Event.TIMED_OUT, event);
        assertEquals(InscriptionFeedbackTracker.Status.FAILED, tracker.status());
        assertFalse(tracker.pending());
    }

    @Test
    void resultFeedbackExpiresWithoutChangingTheStoredProgram() {
        InscriptionFeedbackTracker tracker = new InscriptionFeedbackTracker();
        tracker.begin(InscriptionTarget.preset(EXPECTED));
        tracker.tick(InscriptionTarget.preset(EXPECTED));

        for (int tick = 0; tick < InscriptionFeedbackTracker.RESULT_TICKS; tick++) {
            tracker.tick(InscriptionTarget.preset(OTHER));
        }

        assertEquals(InscriptionFeedbackTracker.Status.NONE, tracker.status());
    }

    @Test
    void resetCancelsPendingAndCompletedFeedback() {
        InscriptionFeedbackTracker tracker = new InscriptionFeedbackTracker();
        tracker.begin(InscriptionTarget.preset(EXPECTED));

        tracker.reset();

        assertEquals(InscriptionFeedbackTracker.Status.NONE, tracker.status());
        assertFalse(tracker.pending());
        assertEquals(InscriptionFeedbackTracker.Event.NONE, tracker.tick(InscriptionTarget.preset(EXPECTED)));

        tracker.begin(InscriptionTarget.preset(EXPECTED));
        tracker.tick(InscriptionTarget.preset(EXPECTED));
        tracker.reset();

        assertEquals(InscriptionFeedbackTracker.Status.NONE, tracker.status());
    }

    private static ProgramGraph graph(String id) {
        return new ProgramGraph(
                List.of(new ProgramNode(id, "mathmod:test")),
                List.of(),
                id,
                4
        );
    }
}
