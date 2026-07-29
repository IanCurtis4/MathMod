package com.mathmod.program;

import java.util.Comparator;
import java.util.List;

public record ScopedServerCompileIssue(Phase phase, Code code, String path) {
    public enum Code { CANCELLED, EXECUTABLE_REJECTED, RESOURCE_REJECTED, KNOWLEDGE_REJECTED, REGISTRY_GENERATION_STALE }
    public enum Phase {
        ENTRY_CANCELLATION,
        POST_COMPILATION_CANCELLATION,
        EXECUTABLE_ADMISSION,
        RESOURCE_ADMISSION,
        BETWEEN_ADMISSIONS_CANCELLATION,
        KNOWLEDGE_ADMISSION,
        BEFORE_RETURN_CANCELLATION,
        GENERATION_RECHECK
    }

    public ScopedServerCompileIssue {
        if (phase == null) throw new IllegalArgumentException("phase must not be null");
        if (code == null) throw new IllegalArgumentException("code must not be null");
        path = path == null || path.isBlank() ? "$" : path.trim();
    }

    static List<ScopedServerCompileIssue> normalize(List<ScopedServerCompileIssue> issues) {
        return (issues == null ? List.<ScopedServerCompileIssue>of() : issues).stream().distinct()
                .sorted(Comparator.comparingInt((ScopedServerCompileIssue issue) -> issue.phase().ordinal())
                        .thenComparing(ScopedServerCompileIssue::path, ScopedServerCompileIssue::comparePath)
                        .thenComparing(issue -> issue.code().name()))
                .toList();
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
