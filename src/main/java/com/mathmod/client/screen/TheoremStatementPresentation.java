package com.mathmod.client.screen;

import com.mathmod.program.TheoremFormulaBreaks;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public final class TheoremStatementPresentation {
    private TheoremStatementPresentation() {
    }

    public static List<FormattedCharSequence> lines(Font font, String formula, int maxWidth) {
        if (font.width(formula) <= maxWidth) {
            return List.of(Component.literal(formula).getVisualOrderText());
        }

        int separator = TheoremFormulaBreaks.outerArgumentSeparator(formula);
        if (separator >= 0) {
            String first = formula.substring(0, separator + 1);
            String second = formula.substring(separator + 1);
            if (font.width(first) <= maxWidth && font.width(second) <= maxWidth) {
                return List.of(
                        Component.literal(first).getVisualOrderText(),
                        Component.literal(second).getVisualOrderText()
                );
            }
        }

        return font.split(Component.literal(formula), maxWidth);
    }
}
