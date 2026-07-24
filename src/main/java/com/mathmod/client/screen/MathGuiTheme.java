package com.mathmod.client.screen;

import com.mathmod.presentation.MathSemanticColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

public final class MathGuiTheme {
    public static final int INK = 0xF014151B;
    public static final int SURFACE = 0xFF1C2028;
    public static final int SURFACE_RAISED = 0xFF252B35;
    public static final int SURFACE_RAISED_SOFT = 0xF0252B35;
    public static final int SURFACE_DISABLED = 0xFF17191E;
    public static final int SURFACE_ROW = 0xCC1B1F26;
    public static final int SURFACE_SELECTED = 0xFF29333A;
    public static final int SURFACE_SELECTED_SOFT = 0xCC29333A;
    public static final int GRID = 0xFF303743;
    public static final int BORDER_STRONG = 0xFF3A424E;
    public static final int BORDER_SUBTLE = 0xFF333A44;
    public static final int BORDER_DISABLED = 0xFF34363B;
    public static final int ACCENT_DISABLED = 0xFF4A4D54;
    public static final int SCROLL_TRACK = 0xFF303241;
    public static final int MODAL_BACKDROP = 0xFF101219;
    public static final int IVORY = MathSemanticColors.opaque(MathSemanticColors.IVORY);
    public static final int MUTED = MathSemanticColors.opaque(MathSemanticColors.MUTED);
    public static final int TEXT_DISABLED = 0xFF666A72;
    public static final int TEAL = MathSemanticColors.opaque(MathSemanticColors.TEAL);
    public static final int GOLD = MathSemanticColors.opaque(MathSemanticColors.GOLD);
    public static final int CORAL = MathSemanticColors.opaque(MathSemanticColors.CORAL);
    public static final int CORAL_SOFT = MathSemanticColors.opaque(MathSemanticColors.CORAL_SOFT);
    public static final int BLUE = MathSemanticColors.opaque(MathSemanticColors.BLUE);
    public static final int GREEN = MathSemanticColors.opaque(MathSemanticColors.GREEN);

    private MathGuiTheme() {
    }

    public static MutableComponent tooltip(MutableComponent component, int color) {
        return component.withStyle(style -> style.withColor(color & 0xFFFFFF));
    }

    public static MutableComponent tooltipPrimary(MutableComponent component) {
        return tooltip(component, IVORY);
    }

    public static MutableComponent tooltipSecondary(MutableComponent component) {
        return tooltip(component, MUTED);
    }

    public static int textFieldOutline(boolean focused, boolean hovered) {
        if (focused) {
            return TEAL;
        }
        return hovered ? MUTED : GRID;
    }

    public static int buttonAccent(int activeAccent, boolean active) {
        return active ? activeAccent : ACCENT_DISABLED;
    }

    public static void fillChamfered(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        if (width < 5 || height < 5) {
            graphics.fill(x, y, x + width, y + height, color);
            return;
        }
        graphics.fill(x + 2, y, x + width - 2, y + height, color);
        graphics.fill(x, y + 2, x + width, y + height - 2, color);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, color);
    }

    public static void outlineChamfered(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        graphics.hLine(x + 2, x + width - 3, y, color);
        graphics.hLine(x + 2, x + width - 3, y + height - 1, color);
        graphics.vLine(x, y + 2, y + height - 3, color);
        graphics.vLine(x + width - 1, y + 2, y + height - 3, color);
        graphics.fill(x + 1, y + 1, x + 2, y + 2, color);
        graphics.fill(x + width - 2, y + 1, x + width - 1, y + 2, color);
        graphics.fill(x + 1, y + height - 2, x + 2, y + height - 1, color);
        graphics.fill(x + width - 2, y + height - 2, x + width - 1, y + height - 1, color);
    }

    public static void panel(GuiGraphics graphics, int x, int y, int width, int height) {
        fillChamfered(graphics, x, y, width, height, SURFACE);
        outlineChamfered(graphics, x, y, width, height, GRID);
    }

    public static void drawProofGrid(GuiGraphics graphics, int x, int y, int width, int height) {
        for (int column = x + 18; column < x + width; column += 24) {
            graphics.vLine(column, y + 1, y + height - 2, 0x221B7F79);
        }
        for (int row = y + 18; row < y + height; row += 24) {
            graphics.hLine(x + 1, x + width - 2, row, 0x221B7F79);
        }
    }
}
