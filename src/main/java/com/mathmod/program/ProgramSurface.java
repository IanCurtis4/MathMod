package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;

import java.util.Optional;

/**
 * Common-side boundary passed to programmer views. It keeps presentation mode
 * outside ProgramGraph and requires exact replay before enabling guided edits.
 */
public record ProgramSurface(
        ProgramGraph graph,
        ProgramSurfaceMode mode,
        ProgramSurfaceMode sourceMode,
        Optional<GuidedWorkspaceState> guidedWorkspace
) {
    public ProgramSurface {
        if (graph == null || mode == null || sourceMode == null) {
            throw new IllegalArgumentException("Program surface fields must not be null");
        }
        guidedWorkspace = guidedWorkspace == null ? Optional.empty() : guidedWorkspace;
        if (mode == ProgramSurfaceMode.GUIDED && guidedWorkspace.isEmpty()) {
            throw new IllegalArgumentException("Guided surfaces require an exact workspace");
        }
        if (mode != ProgramSurfaceMode.GUIDED && guidedWorkspace.isPresent()) {
            throw new IllegalArgumentException("Read-only surfaces must not expose mutable workspace state");
        }
    }

    public static ProgramSurface theorem(ProgramGraph graph) {
        return new ProgramSurface(graph, ProgramSurfaceMode.THEOREM, ProgramSurfaceMode.THEOREM, Optional.empty());
    }

    public static ProgramSurface inscribed(ProgramGraph graph) {
        return new ProgramSurface(graph, ProgramSurfaceMode.INSCRIBED, ProgramSurfaceMode.INSCRIBED, Optional.empty());
    }

    public static ProgramSurface guided(GuidedWorkspaceState workspace) {
        ProgramGraph replayed = replay(workspace).orElseThrow(
                () -> new IllegalArgumentException("Guided workspace cannot be replayed exactly")
        );
        return new ProgramSurface(replayed, ProgramSurfaceMode.GUIDED, ProgramSurfaceMode.GUIDED, Optional.of(workspace));
    }

    public static Optional<ProgramSurface> reopenGuided(ProgramGraph authoritativeGraph, GuidedWorkspaceState workspace) {
        return replay(workspace)
                .filter(authoritativeGraph::equals)
                .map(graph -> new ProgramSurface(
                        graph,
                        ProgramSurfaceMode.GUIDED,
                        ProgramSurfaceMode.INSCRIBED,
                        Optional.of(workspace)
                ));
    }

    public ProgramSurface inspect() {
        return new ProgramSurface(graph, ProgramSurfaceMode.INSPECTOR, sourceMode, Optional.empty());
    }

    private static Optional<ProgramGraph> replay(GuidedWorkspaceState workspace) {
        if (!workspace.supported()) {
            return Optional.empty();
        }
        return workspace.replayableInvocations().map(invocations -> {
            CustomSpellWorkspace replay = new CustomSpellWorkspace();
            replay.loadInvocations(invocations);
            return replay.toGraph();
        });
    }
}
