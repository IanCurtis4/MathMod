package com.mathmod.item;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTooltipStyleSourceTest {
    private static final Path ITEM_ROOT = Path.of("src/main/java/com/mathmod/item");

    @Test
    void sharedStyleRolesRemainWiredToTheCommonPalette() throws Exception {
        String styles = Files.readString(ITEM_ROOT.resolve("ItemTooltipStyles.java"));
        for (var role : Map.of(
                "identity", "GOLD",
                "detail", "MUTED",
                "primaryAction", "TEAL",
                "secondaryAction", "BLUE",
                "destructiveAction", "CORAL"
        ).entrySet()) {
            Pattern wiring = Pattern.compile(
                    "(?s)static MutableComponent " + role.getKey()
                            + "\\([^)]*\\)\\s*\\{\\s*return color\\("
                            + "component, MathSemanticColors\\." + role.getValue()
                            + "\\);\\s*}"
            );
            assertTrue(
                    wiring.matcher(styles).find(),
                    () -> "Missing tooltip role " + role.getKey() + " -> " + role.getValue()
            );
        }
    }

    @Test
    void allFirstContactItemsUseSemanticTooltipRoles() throws Exception {
        Map<String, String> requiredRoles = Map.of(
                "ProgrammedTalismanItem.java", "ItemTooltipStyles.primaryAction",
                "ChalkItem.java", "ItemTooltipStyles.destructiveAction",
                "RuneAnchorItem.java", "ItemTooltipStyles.secondaryAction"
        );

        for (var requirement : requiredRoles.entrySet()) {
            String source = Files.readString(ITEM_ROOT.resolve(requirement.getKey()));
            assertTrue(
                    source.contains(requirement.getValue()),
                    () -> requirement.getKey() + " does not use " + requirement.getValue()
            );
        }
    }
}
