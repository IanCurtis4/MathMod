package com.mathmod.client.screen;

import com.mathmod.program.CustomSpellAction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthoringPalettePresentationTest {
    @Test
    void registryProjectionPreservesTheFrozenBuiltInCategoryAndFormOrder() {
        AuthoringPalettePresentation palette = AuthoringPalettePresentation.builtIns();

        assertEquals(11, palette.categories().size());
        List<String> legacyPaletteOrder = java.util.Arrays.stream(CustomSpellAction.Category.values())
                .flatMap(category -> java.util.Arrays.stream(CustomSpellAction.values())
                        .filter(action -> action.category() == category))
                .map(CustomSpellAction::persistentId)
                .toList();
        assertEquals(legacyPaletteOrder, palette.forms().stream()
                .map(form -> form.metadata().formId().toString()).toList());
        assertEquals(67, palette.forms().size());
        assertTrue(palette.forms().stream().allMatch(form -> form.legacyAction().isPresent()));
    }

    @Test
    void numericArgumentsComeFromDescriptorDefaultsBoundsAndCanonicalization() {
        AuthoringPalettePresentation.Form numberOne = AuthoringPalettePresentation.builtIns()
                .find(CustomSpellAction.NUMBER_ONE).orElseThrow();

        assertEquals(List.of("value"), numberOne.metadata().parameters().stream()
                .map(parameter -> parameter.key()).toList());
        assertEquals(Map.of("value", 1.0D), numberOne.canonicalArguments(Map.of("value", Double.POSITIVE_INFINITY)));
        assertEquals(Map.of("value", -1024.0D), numberOne.canonicalArguments(Map.of("value", -9999.0D, "ignored", 7.0D)));
        assertEquals("number one", numberOne.technicalName());
        assertEquals("number one", numberOne.presentationName(null));
        assertEquals("One", numberOne.presentationName("One"));
    }

    @Test
    void registryPresentationKeepsTheLegacyCategoryColorParity() {
        Map<CustomSpellAction.Category, AuthoringPalettePresentation.CategoryTone> expected = Map.ofEntries(
                Map.entry(CustomSpellAction.Category.SOURCES, AuthoringPalettePresentation.CategoryTone.BLUE),
                Map.entry(CustomSpellAction.Category.ALGEBRA, AuthoringPalettePresentation.CategoryTone.GOLD),
                Map.entry(CustomSpellAction.Category.GEOMETRY, AuthoringPalettePresentation.CategoryTone.TEAL),
                Map.entry(CustomSpellAction.Category.TRIGONOMETRY, AuthoringPalettePresentation.CategoryTone.GOLD),
                Map.entry(CustomSpellAction.Category.CALCULUS, AuthoringPalettePresentation.CategoryTone.CORAL_SOFT),
                Map.entry(CustomSpellAction.Category.LINEAR_ALGEBRA, AuthoringPalettePresentation.CategoryTone.TEAL),
                Map.entry(CustomSpellAction.Category.SYMMETRY, AuthoringPalettePresentation.CategoryTone.BLUE),
                Map.entry(CustomSpellAction.Category.ALCHEMY, AuthoringPalettePresentation.CategoryTone.GREEN),
                Map.entry(CustomSpellAction.Category.METAMAGIC, AuthoringPalettePresentation.CategoryTone.GOLD),
                Map.entry(CustomSpellAction.Category.QUERIES, AuthoringPalettePresentation.CategoryTone.GREEN),
                Map.entry(CustomSpellAction.Category.EFFECTS, AuthoringPalettePresentation.CategoryTone.CORAL)
        );
        AuthoringPalettePresentation palette = AuthoringPalettePresentation.builtIns();

        for (var category : palette.categories()) {
            CustomSpellAction action = palette.forms(category.categoryId()).getFirst().legacyAction().orElseThrow();
            assertEquals(expected.get(action.category()), AuthoringPalettePresentation.categoryTone(category.categoryId()));
        }
    }
}
