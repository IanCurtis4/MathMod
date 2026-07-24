package com.mathmod.block;

public record TimedRedstoneSignal(int power, long expiresAt) {
    public TimedRedstoneSignal {
        if (power < 0 || power > 15) {
            throw new IllegalArgumentException("Redstone power must be between 0 and 15");
        }
        if (expiresAt < 0L) {
            throw new IllegalArgumentException("Signal expiry must not be negative");
        }
    }

    public static TimedRedstoneSignal off() {
        return new TimedRedstoneSignal(0, 0L);
    }

    public static TimedRedstoneSignal activate(long gameTime, int power, int durationTicks) {
        int boundedPower = Math.max(0, Math.min(15, power));
        if (boundedPower == 0 || durationTicks <= 0) {
            return off();
        }
        return new TimedRedstoneSignal(boundedPower, gameTime + durationTicks);
    }

    public int powerAt(long gameTime) {
        return power > 0 && gameTime < expiresAt ? power : 0;
    }

    public long remainingTicks(long gameTime) {
        return powerAt(gameTime) == 0 ? 0L : Math.max(0L, expiresAt - gameTime);
    }
}
