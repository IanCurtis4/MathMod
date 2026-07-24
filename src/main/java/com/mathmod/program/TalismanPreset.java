package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.util.NamespacedId;

import java.util.Objects;
import java.util.function.Supplier;

public record TalismanPreset(
        int buttonId,
        String id,
        Category category,
        String nameKey,
        String hintKey,
        String formula,
        String catalogFormula,
        String iconRuneId,
        Provenance provenance,
        Supplier<ProgramGraph> graphFactory
) {
    public TalismanPreset {
        if (buttonId < 0) {
            throw new IllegalArgumentException("buttonId must not be negative");
        }
        id = requireNamespacedId(id);
        category = Objects.requireNonNull(category, "category");
        nameKey = requireText(nameKey, "nameKey");
        hintKey = requireText(hintKey, "hintKey");
        formula = requireText(formula, "formula");
        catalogFormula = requireText(catalogFormula, "catalogFormula");
        iconRuneId = requireText(iconRuneId, "iconRuneId");
        provenance = Objects.requireNonNull(provenance, "provenance");
        graphFactory = Objects.requireNonNull(graphFactory, "graphFactory");
    }

    public ProgramGraph graph() {
        return graphFactory.get();
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }

    private static String requireNamespacedId(String value) {
        String id = requireText(value, "id");
        return NamespacedId.parse(id).toString();
    }

    public enum Category {
        MOVEMENT("screen.mathmod.rune_programmer.category.movement"),
        SENSING("screen.mathmod.rune_programmer.category.sensing"),
        CONTROL("screen.mathmod.rune_programmer.category.control"),
        ALCHEMY("screen.mathmod.rune_programmer.category.alchemy"),
        METAMAGIC("screen.mathmod.rune_programmer.category.metamagic");

        private final String translationKey;

        Category(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return translationKey;
        }
    }

    public enum Provenance {
        CONVERGENT_EXERCISE("screen.mathmod.rune_programmer.provenance.convergent_exercise"),
        HORIZON_MEASURERS("screen.mathmod.rune_programmer.provenance.horizon_measurers"),
        GATHERERS_OF_MEANS("screen.mathmod.rune_programmer.provenance.gatherers_of_means"),
        HORIZON_BOUNDARY_SYNTHESIS("screen.mathmod.rune_programmer.provenance.horizon_boundary_synthesis"),
        HORIZON_MEANS_SYNTHESIS("screen.mathmod.rune_programmer.provenance.horizon_means_synthesis"),
        COMPOUNDERS_OF_CORRESPONDENCE("screen.mathmod.rune_programmer.provenance.compounders_of_correspondence"),
        KEEPERS_OF_THE_REMAINDER("screen.mathmod.rune_programmer.provenance.keepers_of_the_remainder");

        private final String translationKey;

        Provenance(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return translationKey;
        }
    }
}
