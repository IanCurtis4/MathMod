package com.mathmod.language;

public final class BoundedFunctionCost {
    private BoundedFunctionCost() {
    }

    public static Estimate collectionApplication(int baseCost, int bodyCost, int bound) {
        if (baseCost < 0 || bodyCost < 0) {
            throw new IllegalArgumentException("Costs must not be negative");
        }
        if (bound < 0 || bound > ScopedLanguageLimits.MAX_COLLECTION_BOUND) {
            throw new IllegalArgumentException(
                    "Collection bound must be between 0 and "
                            + ScopedLanguageLimits.MAX_COLLECTION_BOUND
            );
        }
        long estimated = (long) baseCost + (long) bodyCost * bound;
        return new Estimate(
                (int) Math.min(estimated, ScopedLanguageLimits.MAX_EVALUATION_STEPS + 1L),
                estimated <= ScopedLanguageLimits.MAX_EVALUATION_STEPS
        );
    }

    public record Estimate(int evaluationSteps, boolean withinLimit) {
    }
}
