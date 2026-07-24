package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ProgramNameComponents {
    private ProgramNameComponents() {
    }

    public static Component displayName(ItemStack stack, ProgramGraph graph) {
        return ProgramStorage.getName(stack)
                .<Component>map(Component::literal)
                .orElseGet(() -> {
                    if (!ProgramStorage.getCustomActions(stack).isEmpty()) {
                        return Component.translatable("screen.mathmod.rune_programmer.default_custom_name");
                    }
                    return ProgramPresets.presetForGraph(graph)
                            .<Component>map(preset -> Component.translatable(preset.nameKey()))
                            .orElse(Component.translatable("screen.mathmod.rune_programmer.saved_unnamed"));
                });
    }
}
