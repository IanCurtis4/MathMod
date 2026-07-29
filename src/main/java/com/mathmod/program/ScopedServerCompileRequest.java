package com.mathmod.program;

import com.mathmod.knowledge.PlayerKnowledge;
import com.mathmod.language.ScopedProgramSource;

import java.util.Objects;

public record ScopedServerCompileRequest(
        ScopedProgramSource source,
        PlayerKnowledge playerKnowledge,
        ScopedCompileCancellation cancellation
) {
    public ScopedServerCompileRequest {
        source = Objects.requireNonNull(source, "source");
        playerKnowledge = Objects.requireNonNull(playerKnowledge, "playerKnowledge");
        cancellation = cancellation == null ? ScopedCompileCancellation.NEVER : cancellation;
    }
}
