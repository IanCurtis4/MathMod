package com.mathmod.presentation;

public final class MathSemanticColors {
    public static final int IVORY = 0xF0E8D5;
    public static final int MUTED = 0x9DA6B5;
    public static final int TEAL = 0x53D6C7;
    public static final int GOLD = 0xE2B85B;
    public static final int CORAL = 0xEC7B72;
    public static final int CORAL_SOFT = 0xC99A96;
    public static final int BLUE = 0x70A7E8;
    public static final int GREEN = 0x77D38A;

    private MathSemanticColors() {
    }

    public static int opaque(int rgb) {
        return 0xFF000000 | rgb;
    }
}
