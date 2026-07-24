package com.mathmod.program;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockSelectors {
    private BlockSelectors() {
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
                    exactBlock(trimmed);
                }
            }
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Invalid block selector: " + selector, exception);
        }

        if (!foundToken) {
            throw new IllegalArgumentException("Invalid block selector: " + selector);
        }
    }

    public static boolean matches(BlockState state, String selector) {
        for (String token : selector.split(",")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty() && matchesToken(state, trimmed)) {
                return true;
            }
        }
        return false;
    }

    public static Block exactBlock(String blockId) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
        if (block == Blocks.AIR) {
            throw new IllegalArgumentException("Unknown block: " + blockId);
        }
        return block;
    }

    private static boolean matchesToken(BlockState state, String token) {
        if (token.startsWith("#")) {
            TagKey<Block> tag = TagKey.create(Registries.BLOCK, ResourceLocation.parse(token.substring(1)));
            return state.is(tag);
        }
        return state.is(exactBlock(token));
    }
}
