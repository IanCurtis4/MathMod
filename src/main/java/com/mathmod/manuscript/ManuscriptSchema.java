package com.mathmod.manuscript;

public final class ManuscriptSchema {
    public static final int CURRENT_VERSION = 1;
    public static final int OLDEST_SUPPORTED_VERSION = 1;

    private ManuscriptSchema() {
    }

    public static void requireSupported(int version) {
        if (version < OLDEST_SUPPORTED_VERSION || version > CURRENT_VERSION) {
            throw new IllegalArgumentException("Unsupported manuscript schema version " + version);
        }
    }
}
