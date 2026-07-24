package com.mathmod.program;

public final class ProgramNames {
    public static final String DEFAULT_CUSTOM_NAME = "Custom Spell";
    public static final int MAX_LENGTH = 32;

    private ProgramNames() {
    }

    public static String sanitize(String value) {
        String sanitized = sanitizeOptional(value);
        return sanitized.isEmpty() ? DEFAULT_CUSTOM_NAME : sanitized;
    }

    public static String sanitizeOptional(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length() && builder.length() < MAX_LENGTH; i++) {
            char character = value.charAt(i);
            if (!Character.isISOControl(character)) {
                builder.append(character);
            }
        }

        return builder.toString().trim();
    }
}
