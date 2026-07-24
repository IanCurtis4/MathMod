package com.mathmod.language;

public final class ScopedLanguageLimits {
    public static final int MAX_AST_NODES = 256;
    public static final int MAX_BINDING_DEPTH = 16;
    public static final int MAX_TYPE_DEPTH = 4;
    public static final int MAX_ARGUMENTS_PER_CALL = 16;
    public static final int MAX_APPLICATIONS = 64;
    public static final int MAX_LITERAL_LENGTH = 160;
    public static final int MAX_COLLECTION_BOUND = 64;
    public static final int MAX_EVALUATION_STEPS = 4_096;
    public static final int MAX_BUDGET = 128;

    private ScopedLanguageLimits() {
    }
}
