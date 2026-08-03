package com.mathmod.client;

import com.mathmod.program.ProgramPresets;
import com.mathmod.program.TalismanPreset;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiPreviewMatrixTest {
    @Test
    void everyCaptureCaseHasAUniqueStableId() {
        Set<String> ids = new HashSet<>();
        for (UiPreviewMatrix.Case preview : UiPreviewMatrix.cases()) {
            assertTrue(ids.add(preview.id()), "Duplicate UI preview case " + preview.id());
        }
        assertEquals(UiPreviewMatrix.cases().size(), ids.size());
    }

    @Test
    void matrixCoversBothLocalesAndTheCompactParameterizedBoundary() {
        assertTrue(UiPreviewMatrix.cases().stream()
                .anyMatch(preview -> preview.mode().equals("laboratory-parameter-dialog")
                        && preview.locale().equals("en_us")));
        assertTrue(UiPreviewMatrix.cases().stream()
                .anyMatch(preview -> preview.mode().equals("laboratory-parameter-dialog")
                        && preview.locale().equals("pt_br")));
        assertTrue(UiPreviewMatrix.cases().stream()
                .anyMatch(preview -> preview.mode().equals("laboratory-parameter-dialog")
                        && preview.width() == 640
                        && preview.height() == 480));
        assertTrue(hasCase("authoring-registry-palette", "en_us", 1024, 800));
        assertTrue(hasCase("authoring-registry-palette", "pt_br", 1024, 800));
        assertTrue(hasCase("authoring-registry-palette", "pt_br", 640, 480));
        assertTrue(UiPreviewMatrix.cases().stream()
                .anyMatch(preview -> preview.mode().equals("patchouli-manuscript-record")
                        && preview.locale().equals("pt_br")
                        && preview.width() == 640
                        && preview.height() == 480));
        assertTrue(UiPreviewMatrix.cases().stream()
                .anyMatch(preview -> preview.mode().equals("manuscript-reader")
                        && preview.locale().equals("pt_br")
                        && preview.width() == 640
                        && preview.height() == 480));
        assertTrue(UiPreviewMatrix.cases().stream()
                .anyMatch(preview -> preview.mode().equals("manuscript-reader-missing")
                        && preview.locale().equals("en_us")));
    }

    @Test
    void everyCaseUsesTheJeiEnvironmentRequiredByTheUiContract() {
        assertTrue(UiPreviewMatrix.cases().stream().allMatch(UiPreviewMatrix.Case::jeiRequired));
    }

    @Test
    void p12RuntimePreviewsCoverBilingualStandardAndCompactBoundaries() {
        assertTrue(hasCase("construct-preview", "en_us", 1024, 800));
        assertTrue(hasCase("construct-preview", "pt_br", 1024, 800));
        assertTrue(hasCase("construct-preview", "pt_br", 640, 480));
        assertTrue(hasCase("p9-defensive-resources", "en_us", 1024, 800));
        assertTrue(hasCase("p9-defensive-resources", "pt_br", 1024, 800));
        assertTrue(hasCase("p9-defensive-resources", "pt_br", 640, 480));
    }

    @Test
    void functionalInspectorPreviewCoversTheServerBackedLocaleMatrix() {
        assertTrue(hasCase("rune-inspector-functional", "en_us", 1024, 800));
        assertTrue(hasCase("rune-inspector-functional", "pt_br", 1024, 800));
        assertTrue(hasCase("rune-inspector-functional", "pt_br", 640, 480));
    }

    @Test
    void theoremPreflightsRunForSelfRepeatAndTheCatalogExceptionIsIdentitySpecific() throws Exception {
        String harness = Files.readString(Path.of("src/main/java/com/mathmod/client/UiPreviewHarness.java"));

        assertTrue(harness.contains("!PREVIEW.equalsIgnoreCase(\"authoring-registry-palette\")"));
        assertFalse(harness.contains("&& !selfRepeatPreview()"));
        assertTrue(harness.contains("requireTheoremCatalogFormulaFit(minecraft, screen);"));
        assertTrue(harness.contains("requireTheoremStatementFit(minecraft, screen);"));
        assertTrue(harness.contains("preset -> !FactoredLeapCatalogException.isExact(preset)"));
        assertTrue(harness.contains(").size() > 3"));
        assertTrue(hasCase("laboratory-self-repeat", "en_us", 1024, 800));
        assertTrue(hasCase("laboratory-self-repeat", "pt_br", 1024, 800));
    }

    @Test
    void catalogExceptionRequiresBothTheFrozenIdAndFrozenFormula() {
        TalismanPreset factoredLeap = ProgramPresets.presetForId("mathmod:factored_leap").orElseThrow();

        assertTrue(FactoredLeapCatalogException.isExact(factoredLeap));
        assertFalse(FactoredLeapCatalogException.isExact(copyWith(
                factoredLeap,
                "mathmod:factored_leap",
                "push(halve(look)+halve(other))"
        )));
        assertFalse(FactoredLeapCatalogException.isExact(copyWith(
                factoredLeap,
                "mathmod:another_theorem",
                factoredLeap.catalogFormula()
        )));
    }

    @Test
    void factoredLeapPresentationVectorsCoverEveryRequiredLocaleAndViewport() {
        assertTrue(hasCase("fs-01", "en_us", 1024, 800));
        assertTrue(hasCase("fs-02", "pt_br", 1024, 800));
        assertTrue(hasCase("fs-03", "pt_br", 640, 480));
        assertTrue(hasCase("fs-04", "pt_br", 640, 480));
        assertTrue(hasCase("fs-05", "en_us", 1024, 800));
        assertTrue(hasCase("fs-06", "en_us", 1024, 800));
    }

    private static boolean hasCase(String mode, String locale, int width, int height) {
        return UiPreviewMatrix.cases().stream()
                .anyMatch(preview -> preview.mode().equals(mode)
                        && preview.locale().equals(locale)
                        && preview.width() == width
                        && preview.height() == height);
    }

    private static TalismanPreset copyWith(TalismanPreset source, String id, String catalogFormula) {
        return new TalismanPreset(
                source.buttonId(),
                id,
                source.category(),
                source.nameKey(),
                source.hintKey(),
                source.formula(),
                catalogFormula,
                source.iconRuneId(),
                source.provenance(),
                source::graph
        );
    }
}
