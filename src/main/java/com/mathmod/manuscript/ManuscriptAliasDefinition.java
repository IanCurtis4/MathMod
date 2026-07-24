package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;

import java.util.Objects;

public record ManuscriptAliasDefinition(
        int schemaVersion,
        NamespacedId from,
        NamespacedId to
) {
    public ManuscriptAliasDefinition {
        ManuscriptSchema.requireSupported(schemaVersion);
        from = Objects.requireNonNull(from, "from");
        to = Objects.requireNonNull(to, "to");
        if (from.equals(to)) {
            throw new IllegalArgumentException("A manuscript alias cannot target itself: " + from);
        }
    }
}
