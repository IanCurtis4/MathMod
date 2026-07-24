package com.mathmod.acquisition;

public record ManuscriptTradeDefinition(
        int level,
        int emeraldCost,
        boolean requiresBook,
        int maxUses,
        int villagerXp,
        int weight
) {
    public ManuscriptTradeDefinition {
        requireRange("level", level, 2, 5);
        requireRange("emeraldCost", emeraldCost, 6, 24);
        requireRange("maxUses", maxUses, 1, 4);
        requireRange("villagerXp", villagerXp, 5, 30);
        requireRange("weight", weight, 1, 1_024);
    }

    private static void requireRange(String name, int value, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
        }
    }
}
