package com.mathmod.block;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimedRedstoneSignalTest {
    @Test
    void activeSignalExpiresAtItsExactGameTick() {
        TimedRedstoneSignal signal = TimedRedstoneSignal.activate(100L, 9, 40);

        assertEquals(9, signal.powerAt(100L));
        assertEquals(9, signal.powerAt(139L));
        assertEquals(1L, signal.remainingTicks(139L));
        assertEquals(0, signal.powerAt(140L));
        assertEquals(0L, signal.remainingTicks(140L));
    }

    @Test
    void activationBoundsPowerAndTreatsZeroAsOff() {
        assertEquals(15, TimedRedstoneSignal.activate(0L, 99, 20).power());
        assertEquals(TimedRedstoneSignal.off(), TimedRedstoneSignal.activate(0L, 0, 20));
        assertEquals(TimedRedstoneSignal.off(), TimedRedstoneSignal.activate(0L, 8, 0));
    }
}
