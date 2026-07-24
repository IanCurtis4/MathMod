package com.mathmod.manuscript;

import java.util.List;
import java.util.Objects;

public record ManuscriptSnapshotBuildResult(
        ManuscriptSnapshot snapshot,
        List<ManuscriptDiagnostic> diagnostics,
        boolean publishable
) {
    public ManuscriptSnapshotBuildResult {
        snapshot = Objects.requireNonNull(snapshot, "snapshot");
        diagnostics = List.copyOf(diagnostics);
    }
}
