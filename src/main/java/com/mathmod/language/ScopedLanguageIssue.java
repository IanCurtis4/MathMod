package com.mathmod.language;

import java.util.Objects;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        FUNCTION_RESULT_FORBIDDEN,
        COMPILE_STEP_LIMIT,
        LITERAL_INVALID,
        LITERAL_UNSUPPORTED,
        LOWERED_GRAPH_INVALID
    }

    /** Canonical phase/path/code order; messages are deliberately non-semantic. */
    public static List<ScopedLanguageIssue> normalize(List<ScopedLanguageIssue> supplied) {
        Map<String, ScopedLanguageIssue> unique = new LinkedHashMap<>();
        for (ScopedLanguageIssue issue : supplied == null ? List.<ScopedLanguageIssue>of() : supplied) {
            unique.putIfAbsent(phase(issue.code()) + "\u0000" + issue.path() + "\u0000" + issue.code(), issue);
        }
        return unique.values().stream()
                .sorted(Comparator.comparingInt((ScopedLanguageIssue issue) -> phase(issue.code()))
                        .thenComparing(ScopedLanguageIssue::path, ScopedLanguageIssue::comparePath)
                        .thenComparing(issue -> issue.code().name()))
                .toList();
    }

    private static int phase(Code code) {
        return switch (code) {
            case AST_LIMIT, BINDING_DEPTH_LIMIT, TYPE_DEPTH_LIMIT, ARGUMENT_LIMIT, APPLICATION_LIMIT, LITERAL_LIMIT, FREE_PARAMETER -> 1;
            case UNKNOWN_RUNE, IMPURE_LAMBDA_BODY, EFFECT_NOT_IN_TAIL, TYPE_MISMATCH, NON_FUNCTION_APPLICATION,
                    MISSING_RUNE_INPUT, UNEXPECTED_RUNE_INPUT, DISABLED_RUNE, FUNCTION_RESULT_FORBIDDEN -> 2;
            case LITERAL_INVALID, LITERAL_UNSUPPORTED, COMPILE_STEP_LIMIT -> 3;
            case LOWERED_GRAPH_INVALID -> 4;
        };
    }

    private static int comparePath(String left, String right) {
        String[] a = left.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        String[] b = right.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        for (int index = 0; index < Math.min(a.length, b.length); index++) {
            boolean an = a[index].chars().allMatch(Character::isDigit);
            boolean bn = b[index].chars().allMatch(Character::isDigit);
            int result = an && bn ? Integer.compare(Integer.parseInt(a[index]), Integer.parseInt(b[index])) : a[index].compareTo(b[index]);
            if (result != 0) return result;
        }
        return Integer.compare(a.length, b.length);
    }
}
