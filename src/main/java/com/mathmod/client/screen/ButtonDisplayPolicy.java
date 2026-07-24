package com.mathmod.client.screen;

final class ButtonDisplayPolicy {
    private ButtonDisplayPolicy() {
    }

    static String visibleLabel(String currentLabel, String fixedLabel) {
        return fixedLabel == null ? currentLabel : fixedLabel;
    }

    static int horizontalPadding(String fixedLabel) {
        return fixedLabel == null ? 14 : 6;
    }
}
