package com.mathmod.acquisition;

import java.util.List;

public record ManuscriptAcquisitionBuildResult(
        ManuscriptAcquisitionSnapshot snapshot,
        List<AcquisitionDiagnostic> diagnostics,
        boolean publishable
) {
    public ManuscriptAcquisitionBuildResult {
        diagnostics = List.copyOf(diagnostics);
    }
}
