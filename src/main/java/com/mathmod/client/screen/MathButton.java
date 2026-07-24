package com.mathmod.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

public final class MathButton extends Button {
    private Tone tone;
    private final BooleanSupplier selected;
    private String fixedDisplayLabel;

    private MathButton(
            int x,
            int y,
            int width,
            int height,
            Component message,
            Component fixedDisplayMessage,
            OnPress onPress,
            Tone tone,
            BooleanSupplier selected
    ) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.tone = tone;
        this.selected = selected;
        this.fixedDisplayLabel = fixedDisplayMessage == null ? null : fixedDisplayMessage.getString();
    }

    public static MathButton action(
            int x,
            int y,
            int width,
            Component message,
            OnPress onPress,
            Tone tone
    ) {
        return new MathButton(x, y, width, 20, message, null, onPress, tone, () -> false);
    }

    public static MathButton iconAction(
            int x,
            int y,
            int width,
            Component message,
            Component icon,
            OnPress onPress,
            Tone tone
    ) {
        return new MathButton(x, y, width, 20, message, icon, onPress, tone, () -> false);
    }

    public static MathButton iconAction(
            int x,
            int y,
            int width,
            int height,
            Component message,
            Component icon,
            OnPress onPress,
            Tone tone
    ) {
        return new MathButton(x, y, width, height, message, icon, onPress, tone, () -> false);
    }

    public static MathButton tab(
            int x,
            int y,
            int width,
            Component message,
            OnPress onPress,
            BooleanSupplier selected
    ) {
        return new MathButton(x, y, width, 20, message, null, onPress, Tone.TAB, selected);
    }

    void setFixedDisplayMessage(Component message) {
        fixedDisplayLabel = message == null ? null : message.getString();
    }

    void setTone(Tone tone) {
        this.tone = tone;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean emphasized = selected.getAsBoolean();
        int accent = MathGuiTheme.buttonAccent(accentColor(), active);
        int background = active
                ? (isHoveredOrFocused() || emphasized ? MathGuiTheme.SURFACE_RAISED : MathGuiTheme.SURFACE)
                : MathGuiTheme.SURFACE_DISABLED;
        int border = active
                ? (isHoveredOrFocused() || emphasized ? accent : MathGuiTheme.GRID)
                : MathGuiTheme.BORDER_DISABLED;

        MathGuiTheme.fillChamfered(graphics, getX(), getY(), getWidth(), getHeight(), background);
        MathGuiTheme.outlineChamfered(graphics, getX(), getY(), getWidth(), getHeight(), border);

        if (tone != Tone.NEUTRAL || emphasized) {
            graphics.fill(getX() + 3, getY() + 3, getX() + 5, getY() + getHeight() - 3, accent);
        }
        if (emphasized) {
            graphics.hLine(getX() + 7, getX() + getWidth() - 7, getY() + getHeight() - 3, accent);
        }

        int textColor = active ? MathGuiTheme.IVORY : MathGuiTheme.TEXT_DISABLED;
        var font = Minecraft.getInstance().font;
        String label = ButtonDisplayPolicy.visibleLabel(getMessage().getString(), fixedDisplayLabel);
        int availableWidth = Math.max(
                0,
                getWidth() - ButtonDisplayPolicy.horizontalPadding(fixedDisplayLabel)
        );
        if (font.width(label) > availableWidth) {
            String ellipsis = "...";
            label = font.plainSubstrByWidth(
                    label,
                    Math.max(0, availableWidth - font.width(ellipsis))
            ) + ellipsis;
        }
        graphics.drawCenteredString(
                font,
                label,
                getX() + getWidth() / 2 + 1,
                getY() + (getHeight() - 8) / 2,
                textColor
        );
    }

    private int accentColor() {
        return switch (tone) {
            case TAB, PRIMARY -> MathGuiTheme.TEAL;
            case DANGER -> MathGuiTheme.CORAL;
            case RESOURCE -> MathGuiTheme.GOLD;
            case INSPECTION -> MathGuiTheme.BLUE;
            case NEUTRAL -> MathGuiTheme.MUTED;
        };
    }

    public enum Tone {
        TAB,
        PRIMARY,
        RESOURCE,
        INSPECTION,
        DANGER,
        NEUTRAL
    }
}
