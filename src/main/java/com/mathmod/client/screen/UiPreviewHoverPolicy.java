package com.mathmod.client.screen;

final class UiPreviewHoverPolicy {
    private UiPreviewHoverPolicy() {
    }

    static boolean suppressesContextualHover() {
        return suppressesContextualHover(System.getProperty("mathmod.uiPreview", ""));
    }

    static boolean suppressesContextualHover(String previewMode) {
        String mode = previewMode == null ? "" : previewMode.trim();
        return !mode.isEmpty() && !mode.endsWith("-tooltip");
    }
}
