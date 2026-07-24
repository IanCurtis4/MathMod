package com.mathmod.program;

import java.util.function.DoubleSupplier;

public final class ConservationRolls {
    private ConservationRolls() {
    }

    public static int consumedQuantity(int quantity, double conservationChance, DoubleSupplier random) {
        int consumed = 0;
        for (int index = 0; index < quantity; index++) {
            if (random.getAsDouble() >= conservationChance) {
                consumed++;
            }
        }
        return consumed;
    }
}
