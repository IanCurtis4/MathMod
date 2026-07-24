package com.mathmod.acquisition;

public record ManuscriptAcquisitionConfig(
        boolean manuscriptLootEnabled,
        boolean mathematicianProfessionEnabled,
        boolean mathematicianTradesEnabled,
        boolean mathematicianHouseEnabled,
        SurplusPolicy surplusPolicy,
        int villageLootChanceNumerator,
        int villageLootChanceDenominator
) {
    public ManuscriptAcquisitionConfig {
        if (surplusPolicy == null) {
            throw new IllegalArgumentException("surplusPolicy must not be null");
        }
        if (villageLootChanceNumerator < 0 || villageLootChanceNumerator > 1_000) {
            throw new IllegalArgumentException("villageLootChanceNumerator must be between 0 and 1000");
        }
        if (villageLootChanceDenominator < 1 || villageLootChanceDenominator > 1_000) {
            throw new IllegalArgumentException("villageLootChanceDenominator must be between 1 and 1000");
        }
    }

    public static ManuscriptAcquisitionConfig defaults() {
        return new ManuscriptAcquisitionConfig(true, true, true, false, SurplusPolicy.KEEP, 1, 3);
    }

    public boolean effectiveTradesEnabled() {
        return mathematicianProfessionEnabled && mathematicianTradesEnabled;
    }
}
