package com.mathmod.acquisition;

import java.util.Locale;
import java.util.Optional;

public enum SurplusPolicy {
    KEEP,
    TRADE_BACK;

    public static Optional<SurplusPolicy> parse(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(value.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
