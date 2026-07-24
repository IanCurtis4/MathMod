package com.mathmod.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

final class NotationWidget extends AbstractWidget {
    private final Component symbol;
    private final int color;

    NotationWidget(
            int x,
            int y,
            int width,
            int height,
            Component symbol,
            Component explanation,
            int color
    ) {
        super(x, y, width, height, explanation);
        this.symbol = symbol;
        this.color = color;
        setTooltip(Tooltip.create(explanation));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (isHoveredOrFocused()) {
            MathGuiTheme.outlineChamfered(
                    graphics,
                    getX(),
                    getY(),
                    getWidth(),
                    getHeight(),
                    isFocused() ? MathGuiTheme.TEAL : MathGuiTheme.GOLD
            );
        }
        var font = Minecraft.getInstance().font;
        graphics.drawString(
                font,
                symbol,
                getX() + 2,
                getY() + (getHeight() - font.lineHeight) / 2,
                color,
                false
        );
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, getMessage());
    }
}
