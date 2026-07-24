package com.mathmod.manuscript;

import java.util.Locale;
import java.util.Optional;

public enum ManuscriptRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC;

    public static Optional<ManuscriptRarity> parse(String value) {
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(value.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
