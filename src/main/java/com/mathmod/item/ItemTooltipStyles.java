package com.mathmod.item;

import com.mathmod.presentation.MathSemanticColors;
import net.minecraft.network.chat.MutableComponent;

final class ItemTooltipStyles {
    private ItemTooltipStyles() {
    }

    static MutableComponent identity(MutableComponent component) {
        return color(component, MathSemanticColors.GOLD);
    }

    static MutableComponent detail(MutableComponent component) {
        return color(component, MathSemanticColors.MUTED);
    }

    static MutableComponent primaryAction(MutableComponent component) {
        return color(component, MathSemanticColors.TEAL);
    }

    static MutableComponent secondaryAction(MutableComponent component) {
        return color(component, MathSemanticColors.BLUE);
    }

    static MutableComponent destructiveAction(MutableComponent component) {
        return color(component, MathSemanticColors.CORAL);
    }

    private static MutableComponent color(MutableComponent component, int rgb) {
        return component.withStyle(style -> style.withColor(rgb));
    }
}
