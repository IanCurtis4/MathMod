package com.mathmod.client.screen;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

final class PaletteSearch {
    private PaletteSearch() {
    }

    static boolean matches(String query, String... candidates) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isBlank()) {
            return true;
        }
        String haystack = normalize(String.join(" ", candidates));
        return Arrays.stream(normalizedQuery.split(" "))
                .filter(token -> !token.isBlank())
                .allMatch(haystack::contains);
    }

    static String normalize(String value) {
        String decomposed = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD);
        return decomposed
                .replaceAll("\\p{M}+", "")
                .replace('_', ' ')
                .replace('-', ' ')
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
    }
}
