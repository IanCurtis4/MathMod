package com.mathmod.client.screen;

import com.mathmod.kubejs.RuneMaterialDefinition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterialLabelPolicyTest {
    @Test
    void singleExactItemCanUseItsLocalizedItemName() {
        RuneMaterialDefinition feather = material("feather", "minecraft:feather");

        assertEquals("minecraft:feather", MaterialLabelPolicy.exactItemId(feather).orElseThrow());
    }

    @Test
    void tagsAndAlternativesReceiveAReadableFallbackLabel() {
        RuneMaterialDefinition steel = material(
                "steel",
                "#c:ingots/steel,#forge:ingots/steel"
        );

        assertTrue(MaterialLabelPolicy.exactItemId(steel).isEmpty());
        assertEquals("Steel", MaterialLabelPolicy.fallbackLabel(steel));
    }

    @Test
    void malformedSelectorsFallBackWithoutPretendingToBeItems() {
        RuneMaterialDefinition custom = material("field_note", "field note");

        assertTrue(MaterialLabelPolicy.exactItemId(custom).isEmpty());
        assertEquals("Field Note", MaterialLabelPolicy.fallbackLabel(custom));
    }

    @Test
    void fallbackHidesNamespacesButTheDefinitionRetainsItsStableId() {
        RuneMaterialDefinition custom = material("pack:stellar_steel", "#c:ingots/stellar_steel");

        assertEquals("Stellar Steel", MaterialLabelPolicy.fallbackLabel(custom));
        assertEquals("pack:stellar_steel", custom.id());
    }

    @Test
    void presentationSurvivesMaterialMutations() {
        RuneMaterialDefinition localized = material("steel", "#c:ingots/steel")
                .withDisplayTranslationKey("material.pack.steel")
                .withAttribute("force", 2)
                .withConsumed(false);

        assertEquals("material.pack.steel", localized.displayTranslationKey());
        assertEquals(2, localized.attributeAmount("force"));
        assertTrue(!localized.consumed());
    }

    private static RuneMaterialDefinition material(String id, String selector) {
        return new RuneMaterialDefinition(id, selector, 1, 1, true, Map.of());
    }
}
