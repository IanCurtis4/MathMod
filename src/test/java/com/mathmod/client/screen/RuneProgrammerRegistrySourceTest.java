package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RuneProgrammerRegistrySourceTest {
    @Test
    void guidedParameterDialogConsumesTheRegistryProjectionWithoutChangingItsLegacyPacket() throws Exception {
        String source = Files.readString(Path.of("src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java"));

        assertTrue(source.contains("AuthoringPalettePresentation.builtIns()"));
        assertTrue(source.contains("authoringPalette.find(action)"));
        assertTrue(source.contains("authoringPalette.categories()"));
        assertTrue(source.contains("authoringPalette.forms(category.categoryId())"));
        assertTrue(source.contains("formDisplayName(form)"));
        assertTrue(source.contains("registryFormTitle(action)"));
        assertTrue(source.contains("Component.literal(formDisplayName(form))"));
        assertTrue(source.contains("AuthoringPalettePresentation.categoryTone(categoryId)"));
        assertTrue(source.contains("parameter.canonicalize(value)"));
        assertTrue(source.contains("new ApplyCustomSpellInvocationPayload(invocation.persistentId())"));
    }

    @Test
    void registryPreviewStateAssertionsSequenceKeyboardSearchAndPointerActivation() throws Exception {
        String source = Files.readString(Path.of("src/main/java/com/mathmod/client/UiPreviewHarness.java"));

        assertTrue(source.contains("runAuthoringRegistryPreview(screen)"));
        assertTrue(source.contains("focusPaletteAndMove(screen, 0, true)"));
        assertTrue(source.contains("List.of(CustomSpellAction.SELF)"));
        assertTrue(source.contains("searchLaboratory(screen, \"simpson\")"));
        assertTrue(source.contains("firstCustomPaletteAction(screen) != CustomSpellAction.SIMPSON_INTEGRAL"));
        assertTrue(source.contains("clickFirstCustomPaletteRow(screen)"));
        assertTrue(source.contains("requireSimpsonParameterDialog(screen)"));
        assertTrue(source.contains("PARAMETER_ACTION_FIELD.get(screen) != CustomSpellAction.SIMPSON_INTEGRAL"));
        assertTrue(source.contains("!value.contains(\"simpson\")"));
    }
}
