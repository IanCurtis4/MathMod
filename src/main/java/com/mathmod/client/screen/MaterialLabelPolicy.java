package com.mathmod.client.screen;

import com.mathmod.kubejs.RuneMaterialDefinition;

import java.util.Optional;
import java.util.regex.Pattern;

public final class MaterialLabelPolicy {
    private static final Pattern EXACT_ITEM_ID =
            Pattern.compile("[a-z0-9_.-]+:[a-z0-9_./-]+");

    private MaterialLabelPolicy() {
    }

    public static Optional<String> exactItemId(RuneMaterialDefinition material) {
        String selector = material.itemOrTag();
        if (selector.startsWith("#")
                || selector.contains(",")
                || !EXACT_ITEM_ID.matcher(selector).matches()) {
            return Optional.empty();
        }
        return Optional.of(selector);
    }

    public static String fallbackLabel(RuneMaterialDefinition material) {
        return material.fallbackDisplayName();
    }
}
