package com.mathmod.client;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private static boolean hasCase(String mode, String locale, int width, int height) {
        return UiPreviewMatrix.cases().stream()
                .anyMatch(preview -> preview.mode().equals(mode)
                        && preview.locale().equals(locale)
                        && preview.width() == width
                        && preview.height() == height);
    }
}
