package com.mathmod.client.screen;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

final class BoundedTooltipPositioner implements ClientTooltipPositioner {
    static final BoundedTooltipPositioner INSTANCE = new BoundedTooltipPositioner();

    private BoundedTooltipPositioner() {
    }

    @Override
    public Vector2ic positionTooltip(
            int screenWidth,
            int screenHeight,
            int mouseX,
            int mouseY,
            int tooltipWidth,
            int tooltipHeight
    ) {
        Vector2ic vanilla = DefaultTooltipPositioner.INSTANCE.positionTooltip(
                screenWidth,
                screenHeight,
                mouseX,
                mouseY,
                tooltipWidth,
                tooltipHeight
        );
        TooltipBoundsPolicy.Position bounded = TooltipBoundsPolicy.boundedPosition(
                vanilla.x(),
                vanilla.y(),
                screenWidth,
                screenHeight,
                tooltipWidth,
                tooltipHeight
        );
        return new Vector2i(bounded.x(), bounded.y());
    }
}
