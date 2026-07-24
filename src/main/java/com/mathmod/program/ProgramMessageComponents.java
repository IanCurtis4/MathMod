package com.mathmod.program;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ProgramMessageComponents {
    private ProgramMessageComponents() {
    }

    public static Component executionResult(ProgramExecutionResult result) {
        List<Object> arguments = new ArrayList<>(result.messageArguments());
        if (!result.itemDeficits().isEmpty()) {
            arguments.addFirst(selectors(result.itemDeficits()));
        } else if (!result.attributeDeficits().isEmpty()) {
            arguments.addFirst(attributes(result.attributeDeficits()));
        }
        return Component.translatable(result.messageKey(), arguments.toArray());
    }

    public static Component successfulCast(Component programName) {
        return Component.translatable("item.mathmod.programmed_talisman.executed_named", programName);
    }

    public static Component selectors(Map<String, Integer> selectors) {
        MutableComponent result = Component.empty();
        boolean first = true;
        for (Map.Entry<String, Integer> entry : selectors.entrySet()) {
            if (!first) {
                result.append(", ");
            }
            result.append(Component.literal(entry.getValue() + "x "));
            result.append(selector(entry.getKey()));
            first = false;
        }
        return result;
    }

    public static Component attributes(Map<String, Integer> attributes) {
        MutableComponent result = Component.empty();
        boolean first = true;
        for (Map.Entry<String, Integer> entry : attributes.entrySet()) {
            if (!first) {
                result.append(", ");
            }
            result.append(attribute(entry.getKey()));
            result.append(" " + entry.getValue());
            first = false;
        }
        return result;
    }

    public static Component attribute(String attribute) {
        return Component.translatableWithFallback(
                ProgramAttributes.translationKey(attribute),
                ProgramAttributes.fallbackLabel(attribute)
        );
    }

    private static Component selector(String selector) {
        var material = ProgramResources.materialForSelector(selector);
        if (material.isPresent()) {
            String translationKey = material.get().displayTranslationKey();
            if (translationKey != null) {
                return Component.translatableWithFallback(
                        translationKey,
                        material.get().fallbackDisplayName()
                );
            }
            if (selector.startsWith("#") || selector.contains(",")) {
                return Component.literal(material.get().fallbackDisplayName());
            }
        }
        MutableComponent result = Component.empty();
        boolean first = true;
        for (String token : selector.split(",")) {
            String trimmed = token.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (!first) {
                result.append(" / ");
            }
            result.append(selectorToken(trimmed));
            first = false;
        }
        return first ? Component.literal(selector) : result;
    }

    private static Component selectorToken(String token) {
        if (token.startsWith("#")) {
            return Component.literal(token);
        }
        ResourceLocation id = ResourceLocation.tryParse(token);
        if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) {
            return Component.literal(token);
        }
        return Component.translatable(BuiltInRegistries.ITEM.get(id).getDescriptionId());
    }
}
