package com.mathmod.client.screen;

import com.mathmod.presentation.MathSemanticColors;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MathGuiThemeTest {
    private static final Path SCREEN_SOURCE = Path.of(
            "src/main/java/com/mathmod/client/screen"
    );
    private static final Pattern ARGB_LITERAL = Pattern.compile("0x[0-9A-Fa-f]{8}");
    private static final Pattern VANILLA_NAMED_COLOR = Pattern.compile(
            "ChatFormatting\\.(?:BLACK|DARK_BLUE|DARK_GREEN|DARK_AQUA|DARK_RED|DARK_PURPLE|"
                    + "GOLD|GRAY|DARK_GRAY|BLUE|GREEN|AQUA|RED|LIGHT_PURPLE|YELLOW|WHITE)"
    );

    @Test
    void secondaryTooltipTextHasReadableContrastOnTheDarkTheme() {
        double contrast = contrastRatio(MathGuiTheme.MUTED, MathGuiTheme.INK);

        assertTrue(contrast >= 4.5, "Expected tooltip contrast >= 4.5, got " + contrast);
    }

    @Test
    void textFieldsUseOneConsistentFocusHierarchy() {
        assertEquals(MathGuiTheme.GRID, MathGuiTheme.textFieldOutline(false, false));
        assertEquals(MathGuiTheme.MUTED, MathGuiTheme.textFieldOutline(false, true));
        assertEquals(MathGuiTheme.TEAL, MathGuiTheme.textFieldOutline(true, false));
        assertEquals(MathGuiTheme.TEAL, MathGuiTheme.textFieldOutline(true, true));
    }

    @Test
    void disabledButtonsMuteTheirSemanticAccent() {
        for (int semanticAccent : new int[]{
                MathGuiTheme.TEAL,
                MathGuiTheme.GOLD,
                MathGuiTheme.CORAL,
                MathGuiTheme.BLUE,
                MathGuiTheme.MUTED
        }) {
            assertEquals(MathGuiTheme.ACCENT_DISABLED, MathGuiTheme.buttonAccent(semanticAccent, false));
            assertEquals(semanticAccent, MathGuiTheme.buttonAccent(semanticAccent, true));
        }
    }

    @Test
    void semanticTextColorsRemainReadableOnPanels() {
        Map<String, Integer> colors = Map.of(
                "ivory", MathGuiTheme.IVORY,
                "muted", MathGuiTheme.MUTED,
                "teal", MathGuiTheme.TEAL,
                "gold", MathGuiTheme.GOLD,
                "coral", MathGuiTheme.CORAL,
                "coral soft", MathGuiTheme.CORAL_SOFT,
                "blue", MathGuiTheme.BLUE,
                "green", MathGuiTheme.GREEN
        );

        for (var color : colors.entrySet()) {
            double contrast = contrastRatio(color.getValue(), MathGuiTheme.SURFACE);
            assertTrue(
                    contrast >= 4.5,
                    () -> color.getKey() + " contrast must be >= 4.5, got " + contrast
            );
        }
    }

    @Test
    void guiTextColorsComeFromTheCommonPresentationPalette() {
        assertEquals(MathSemanticColors.opaque(MathSemanticColors.IVORY), MathGuiTheme.IVORY);
        assertEquals(MathSemanticColors.opaque(MathSemanticColors.MUTED), MathGuiTheme.MUTED);
        assertEquals(MathSemanticColors.opaque(MathSemanticColors.TEAL), MathGuiTheme.TEAL);
        assertEquals(MathSemanticColors.opaque(MathSemanticColors.GOLD), MathGuiTheme.GOLD);
        assertEquals(MathSemanticColors.opaque(MathSemanticColors.CORAL), MathGuiTheme.CORAL);
        assertEquals(MathSemanticColors.opaque(MathSemanticColors.BLUE), MathGuiTheme.BLUE);
        assertEquals(MathSemanticColors.opaque(MathSemanticColors.GREEN), MathGuiTheme.GREEN);
    }

    @Test
    void screensKeepArgbColorsInTheSharedTheme() throws IOException {
        try (Stream<Path> paths = Files.walk(SCREEN_SOURCE)) {
            for (Path path : paths.filter(file -> file.toString().endsWith(".java")).toList()) {
                if (path.getFileName().toString().equals("MathGuiTheme.java")) {
                    continue;
                }
                String source = Files.readString(path);
                assertFalse(
                        ARGB_LITERAL.matcher(source).find(),
                        () -> path + " contains a visual ARGB literal outside MathGuiTheme"
                );
            }
        }
    }

    @Test
    void screensKeepNamedColorsInTheSharedTheme() throws IOException {
        try (Stream<Path> paths = Files.walk(SCREEN_SOURCE)) {
            for (Path path : paths.filter(file -> file.toString().endsWith(".java")).toList()) {
                String source = Files.readString(path);
                assertFalse(
                        VANILLA_NAMED_COLOR.matcher(source).find(),
                        () -> path + " contains a vanilla named color instead of MathGuiTheme"
                );
            }
        }
    }

    private static double contrastRatio(int first, int second) {
        double lighter = Math.max(relativeLuminance(first), relativeLuminance(second));
        double darker = Math.min(relativeLuminance(first), relativeLuminance(second));
        return (lighter + 0.05) / (darker + 0.05);
    }

    private static double relativeLuminance(int color) {
        double red = linearChannel((color >> 16) & 0xFF);
        double green = linearChannel((color >> 8) & 0xFF);
        double blue = linearChannel(color & 0xFF);
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
    }

    private static double linearChannel(int channel) {
        double value = channel / 255.0;
        return value <= 0.04045
                ? value / 12.92
                : Math.pow((value + 0.055) / 1.055, 2.4);
    }
}
