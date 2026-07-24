package com.mathmod.field;

import com.mathmod.util.NamespacedId;

import java.util.Objects;

/** Declarative provider metadata. Sampling behavior is server-owned and registered separately. */
public record FieldProviderDefinition(
        NamespacedId id,
        FieldValueKind valueKind,
        FieldQuantity quantity,
        double maximumRadius,
        int sampleCost
) {
    public FieldProviderDefinition {
        id = Objects.requireNonNull(id, "id");
        valueKind = Objects.requireNonNull(valueKind, "valueKind");
        quantity = Objects.requireNonNull(quantity, "quantity");
        if (!Double.isFinite(maximumRadius) || maximumRadius <= 0.0D || maximumRadius > 64.0D) {
            throw new IllegalArgumentException("maximumRadius must be finite and in (0, 64]");
        }
        if (sampleCost < 0 || sampleCost > 128) {
            throw new IllegalArgumentException("sampleCost must be between 0 and 128");
        }
    }
}
