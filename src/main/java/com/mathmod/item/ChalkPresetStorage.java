package com.mathmod.item;

import com.mathmod.program.AnchorProgramPreset;
import com.mathmod.registry.ModDataComponents;
import net.minecraft.world.item.ItemStack;

public final class ChalkPresetStorage {
    private ChalkPresetStorage() {
    }

    public static AnchorProgramPreset get(ItemStack stack) {
        String presetId = stack.getOrDefault(ModDataComponents.CHALK_PRESET.get(), AnchorProgramPreset.ANCHOR_PULSE.id());
        return AnchorProgramPreset.fromId(presetId).orElse(AnchorProgramPreset.ANCHOR_PULSE);
    }

    public static AnchorProgramPreset cycle(ItemStack stack) {
        AnchorProgramPreset nextPreset = get(stack).next();
        set(stack, nextPreset);
        return nextPreset;
    }

    public static void set(ItemStack stack, AnchorProgramPreset preset) {
        stack.set(ModDataComponents.CHALK_PRESET.get(), preset.id());
    }
}
