package com.mathmod.client;

import java.util.List;

/**
 * Deterministic capture matrix for the current UI acceptance slices.
 *
 * The matrix describes captures; it does not replace the real-client harness
 * or claim coverage for screens that are not implemented yet.
 */
public final class UiPreviewMatrix {
    private static final List<Case> CASES = List.of(
            new Case("programmer", "en_us", 1024, 800, 2, true),
            new Case("programmer", "pt_br", 1024, 800, 2, true),
            new Case("rune-inspector", "en_us", 1024, 800, 2, true),
            new Case("rune-inspector", "pt_br", 1024, 800, 2, true),
            new Case("rune-inspector", "pt_br", 640, 480, 2, true),
            new Case("rune-inspector-functional", "en_us", 1024, 800, 2, true),
            new Case("rune-inspector-functional", "pt_br", 1024, 800, 2, true),
            new Case("rune-inspector-functional", "pt_br", 640, 480, 2, true),
            new Case("minimum-viewport", "pt_br", 640, 480, 2, true),
            new Case("laboratory-parameter-dialog", "en_us", 1024, 800, 2, true),
            new Case("laboratory-parameter-dialog", "pt_br", 1024, 800, 2, true),
            new Case("laboratory-parameter-dialog", "pt_br", 640, 480, 2, true),
            new Case("authoring-registry-palette", "en_us", 1024, 800, 2, true),
            new Case("authoring-registry-palette", "pt_br", 1024, 800, 2, true),
            new Case("authoring-registry-palette", "pt_br", 640, 480, 2, true),
            new Case("patchouli-current-state", "en_us", 1024, 800, 2, true),
            new Case("patchouli-current-state", "pt_br", 1024, 800, 2, true),
            new Case("patchouli-manuscript-record", "en_us", 1024, 800, 2, true),
            new Case("patchouli-manuscript-record", "pt_br", 1024, 800, 2, true),
            new Case("patchouli-manuscript-record", "pt_br", 640, 480, 2, true),
            new Case("manuscript-reader", "en_us", 1024, 800, 2, true),
            new Case("manuscript-reader", "pt_br", 1024, 800, 2, true),
            new Case("manuscript-reader", "pt_br", 640, 480, 2, true),
            new Case("manuscript-reader-missing", "en_us", 1024, 800, 2, true),
            new Case("manuscript-reader-missing", "pt_br", 640, 480, 2, true),
            new Case("patchouli-matrix", "pt_br", 1024, 800, 2, true)
            ,new Case("construct-preview", "en_us", 1024, 800, 2, true)
            ,new Case("construct-preview", "pt_br", 1024, 800, 2, true)
            ,new Case("construct-preview", "pt_br", 640, 480, 2, true)
            ,new Case("p9-defensive-resources", "en_us", 1024, 800, 2, true)
            ,new Case("p9-defensive-resources", "pt_br", 1024, 800, 2, true)
            ,new Case("p9-defensive-resources", "pt_br", 640, 480, 2, true)
            ,new Case("laboratory-self-repeat", "en_us", 1024, 800, 2, true)
            ,new Case("laboratory-self-repeat", "pt_br", 1024, 800, 2, true)
            ,new Case("fs-01", "en_us", 1024, 800, 2, true)
            ,new Case("fs-02", "pt_br", 1024, 800, 2, true)
            ,new Case("fs-03", "pt_br", 640, 480, 2, true)
            ,new Case("fs-04", "pt_br", 640, 480, 2, true)
            ,new Case("fs-05", "en_us", 1024, 800, 2, true)
            ,new Case("fs-06", "en_us", 1024, 800, 2, true)
    );

    private UiPreviewMatrix() {
    }

    public static List<Case> cases() {
        return CASES;
    }

    public record Case(
            String mode,
            String locale,
            int width,
            int height,
            int guiScale,
            boolean jeiRequired
    ) {
        public Case {
            if (mode == null || mode.isBlank()) {
                throw new IllegalArgumentException("Preview mode cannot be blank");
            }
            if (locale == null || locale.isBlank()) {
                throw new IllegalArgumentException("Preview locale cannot be blank");
            }
            if (width <= 0 || height <= 0 || guiScale <= 0) {
                throw new IllegalArgumentException("Preview dimensions must be positive");
            }
        }

        public String id() {
            return mode + "@" + locale + "-" + width + "x" + height + "-g" + guiScale;
        }
    }
}
