package com.mathmod.client.screen;

import com.mathmod.kubejs.RuneMaterialDefinition;
import com.mathmod.program.ProgramResources;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

final class MaterialPresentation {
    private MaterialPresentation() {
    }

    static Component displayName(String materialId) {
        return ProgramResources.material(materialId)
                .map(MaterialPresentation::displayName)
                .orElse(Component.literal(materialId));
    }

    static Component displayName(RuneMaterialDefinition material) {
        if (material.displayTranslationKey() != null) {
            return Component.translatableWithFallback(
                    material.displayTranslationKey(),
                    material.fallbackDisplayName()
            );
        }
        return MaterialLabelPolicy.exactItemId(material)
                .map(ResourceLocation::tryParse)
                .filter(BuiltInRegistries.ITEM::containsKey)
                .map(BuiltInRegistries.ITEM::get)
                .<Component>map(item -> Component.translatable(item.getDescriptionId()))
                .orElse(Component.literal(MaterialLabelPolicy.fallbackLabel(material)));
    }
}
