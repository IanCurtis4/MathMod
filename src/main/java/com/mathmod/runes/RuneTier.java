package com.mathmod.runes;

import java.util.Arrays;

public enum RuneTier {
    FUNDAMENTAL(1, "rune_tier.mathmod.fundamental"),
    REFINED(2, "rune_tier.mathmod.refined"),
    ARCANE(3, "rune_tier.mathmod.arcane"),
    METAMAGICAL(4, "rune_tier.mathmod.metamagical");

    private final int level;
    private final String translationKey;

    RuneTier(int level, String translationKey) {
        this.level = level;
        this.translationKey = translationKey;
    }

    public int level() {
        return level;
    }

    public String translationKey() {
        return translationKey;
    }

    public String compactLabel() {
        return "T" + level;
    }

    public static RuneTier byLevel(int level) {
        return Arrays.stream(values())
                .filter(tier -> tier.level == level)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rune tier must be between 1 and 4"));
    }
}
