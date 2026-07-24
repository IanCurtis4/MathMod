package com.mathmod.language;

import java.util.Objects;

public record ScopedLanguageIssue(Code code, String path, String message) {
    public ScopedLanguageIssue {
        code = Objects.requireNonNull(code, "code");
        path = path == null || path.isBlank() ? "$" : path.trim();
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        message = message.trim();
    }

    public enum Code {
        FREE_PARAMETER,
        AST_LIMIT,
        BINDING_DEPTH_LIMIT,
        TYPE_DEPTH_LIMIT,
        ARGUMENT_LIMIT,
        APPLICATION_LIMIT,
        LITERAL_LIMIT,
        UNKNOWN_RUNE,
        IMPURE_LAMBDA_BODY,
        EFFECT_NOT_IN_TAIL,
        TYPE_MISMATCH,
        NON_FUNCTION_APPLICATION,
        MISSING_RUNE_INPUT,
        UNEXPECTED_RUNE_INPUT,
        DISABLED_RUNE,
        FUNCTION_RESULT_FORBIDDEN
    }
}
