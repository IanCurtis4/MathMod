package com.mathmod.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

final class MathTooltipRenderer {
    private MathTooltipRenderer() {
    }

    static void render(
            GuiGraphics guiGraphics,
            Font font,
            List<? extends Component> components,
            int mouseX,
            int mouseY
    ) {
        int maximumLineWidth = Math.max(1, guiGraphics.guiWidth() - TooltipBoundsPolicy.CONTENT_INSET * 2);
        List<FormattedCharSequence> lines = new ArrayList<>();
        for (Component component : components) {
            if (font.width(component) <= maximumLineWidth) {
                lines.add(component.getVisualOrderText());
            } else {
                lines.addAll(font.split(component, maximumLineWidth));
            }
        }
        guiGraphics.renderTooltip(font, lines, BoundedTooltipPositioner.INSTANCE, mouseX, mouseY);
    }
}
