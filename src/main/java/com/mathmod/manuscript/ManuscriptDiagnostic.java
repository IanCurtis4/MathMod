package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;

import java.util.Objects;

public record ManuscriptDiagnostic(
        Severity severity,
        Code code,
        RecordKind recordKind,
        NamespacedId id,
        ManuscriptDefinitionSource source,
        String message
) {
    public ManuscriptDiagnostic {
        severity = Objects.requireNonNull(severity, "severity");
        code = Objects.requireNonNull(code, "code");
        recordKind = Objects.requireNonNull(recordKind, "recordKind");
        id = Objects.requireNonNull(id, "id");
        source = Objects.requireNonNull(source, "source");
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        message = message.trim();
    }

    public enum Severity {
        INFO,
        WARNING,
        ERROR,
        FATAL
    }

    public enum Code {
        SHADOWED,
        AMBIGUOUS_SOURCE,
        DECODE_FAILED,
        UNKNOWN_ICON,
        UNKNOWN_TRADITION,
        UNKNOWN_THEOREM,
        ALIAS_SHADOWS_MANUSCRIPT,
        ALIAS_CYCLE,
        ALIAS_TOO_DEEP,
        ALIAS_MISSING_TARGET,
        RECORD_LIMIT_EXCEEDED
    }

    public enum RecordKind {
        TRADITION,
        MANUSCRIPT,
        ALIAS,
        SNAPSHOT
    }
}
