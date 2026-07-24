package com.mathmod.client.screen;

final class InscriptionFeedbackTracker {
    static final int PENDING_TICKS = 100;
    static final int RESULT_TICKS = 80;

    private Status status = Status.NONE;
    private InscriptionTarget expectedTarget;
    private int ticksRemaining;

    void begin(InscriptionTarget expectedTarget) {
        this.expectedTarget = expectedTarget;
        this.status = Status.PENDING;
        this.ticksRemaining = PENDING_TICKS;
    }

    Event tick(InscriptionTarget storedTarget) {
        if (status == Status.PENDING) {
            if (expectedTarget.equals(storedTarget)) {
                expectedTarget = null;
                status = Status.SUCCESS;
                ticksRemaining = RESULT_TICKS;
                return Event.CONFIRMED;
            }
            ticksRemaining--;
            if (ticksRemaining <= 0) {
                expectedTarget = null;
                status = Status.FAILED;
                ticksRemaining = RESULT_TICKS;
                return Event.TIMED_OUT;
            }
            return Event.NONE;
        }

        if ((status == Status.SUCCESS || status == Status.FAILED) && --ticksRemaining <= 0) {
            status = Status.NONE;
            ticksRemaining = 0;
        }
        return Event.NONE;
    }

    Status status() {
        return status;
    }

    boolean pending() {
        return status == Status.PENDING;
    }

    void reset() {
        expectedTarget = null;
        status = Status.NONE;
        ticksRemaining = 0;
    }

    enum Status {
        NONE,
        PENDING,
        SUCCESS,
        FAILED
    }

    enum Event {
        NONE,
        CONFIRMED,
        TIMED_OUT
    }
}
