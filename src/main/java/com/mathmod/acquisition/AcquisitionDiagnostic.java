package com.mathmod.acquisition;

import com.mathmod.manuscript.ManuscriptDefinitionSource;
import com.mathmod.util.NamespacedId;

import java.util.Objects;

public record AcquisitionDiagnostic(Code code, NamespacedId id, ManuscriptDefinitionSource source, String message) {
    public AcquisitionDiagnostic {
        code = Objects.requireNonNull(code, "code");
        id = Objects.requireNonNull(id, "id");
        source = Objects.requireNonNull(source, "source");
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        message = message.trim();
    }

    public enum Code {
        AMBIGUOUS_SOURCE,
        SHADOWED,
        DECODE_FAILED,
        UNKNOWN_MANUSCRIPT,
        POOL_COLLISION,
        TRADE_COLLISION,
        RECORD_LIMIT_EXCEEDED
    }
}
