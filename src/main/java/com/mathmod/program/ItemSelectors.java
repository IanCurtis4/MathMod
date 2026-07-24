package com.mathmod.program;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ItemSelectors {
    private ItemSelectors() {
    }

    public static void validate(String selector) {
        boolean foundToken = false;
        try {
            for (String token : selector.split(",")) {
                String trimmed = token.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                foundToken = true;
                if (trimmed.startsWith("#")) {
                    ResourceLocation.parse(trimmed.substring(1));
                } else {
                    exactItem(trimmed);
                }
            }
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Invalid item selector: " + selector, exception);
        }

        if (!foundToken) {
            throw new IllegalArgumentException("Invalid item selector: " + selector);
        }
    }

    public static boolean matches(ItemStack stack, String selector) {
        for (String token : selector.split(",")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty() && matchesToken(stack, trimmed)) {
                return true;
            }
        }
        return false;
    }

    public static Item exactItem(String itemId) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        if (item == Items.AIR) {
            throw new IllegalArgumentException("Unknown item: " + itemId);
        }
        return item;
    }

    private static boolean matchesToken(ItemStack stack, String token) {
        if (token.startsWith("#")) {
            TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(token.substring(1)));
            return stack.is(tag);
        }
        return stack.is(exactItem(token));
    }
}
