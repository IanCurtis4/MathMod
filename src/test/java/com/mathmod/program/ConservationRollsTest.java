package com.mathmod.program;

import org.junit.jupiter.api.Test;

import java.util.PrimitiveIterator;
import java.util.stream.DoubleStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConservationRollsTest {
    @Test
    void rollsEveryConsumedUnitIndependently() {
        PrimitiveIterator.OfDouble rolls = DoubleStream.of(0.10D, 0.20D, 0.45D, 0.90D).iterator();

        assertEquals(2, ConservationRolls.consumedQuantity(4, 0.30D, rolls::nextDouble));
    }

    @Test
    void zeroChanceConsumesEverything() {
        assertEquals(5, ConservationRolls.consumedQuantity(5, 0.0D, () -> 0.5D));
    }
}
