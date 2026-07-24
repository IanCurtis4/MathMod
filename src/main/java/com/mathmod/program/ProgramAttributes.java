package com.mathmod.program;

public final class ProgramAttributes {
    private ProgramAttributes() {
    }

    public static String translationKey(String attribute) {
        return "attribute.mathmod." + attribute;
    }

    public static String fallbackLabel(String attribute) {
        StringBuilder result = new StringBuilder(attribute.length());
        boolean capitalize = true;
        for (int index = 0; index < attribute.length(); index++) {
            char character = attribute.charAt(index);
            if (character == '_' || character == '-' || character == ':' || character == '.') {
                if (!result.isEmpty() && result.charAt(result.length() - 1) != ' ') {
                    result.append(' ');
                }
                capitalize = true;
            } else {
                result.append(capitalize ? Character.toUpperCase(character) : character);
                capitalize = false;
            }
        }
        return result.toString().trim();
    }
}
