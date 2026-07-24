package com.mathmod.program;

import java.util.Optional;

public record GuidedWorkspaceRead(Status status, Optional<GuidedWorkspaceState> state) {
    public GuidedWorkspaceRead {
        state = state == null ? Optional.empty() : state;
        if ((status == Status.AVAILABLE) != state.isPresent()) {
            throw new IllegalArgumentException("Only an available workspace may expose state");
        }
    }

    public static GuidedWorkspaceRead absent() {
        return new GuidedWorkspaceRead(Status.ABSENT, Optional.empty());
    }

    public static GuidedWorkspaceRead available(GuidedWorkspaceState state) {
        return new GuidedWorkspaceRead(Status.AVAILABLE, Optional.of(state));
    }

    public static GuidedWorkspaceRead unreplayable() {
        return new GuidedWorkspaceRead(Status.UNREPLAYABLE, Optional.empty());
    }

    public enum Status {
        ABSENT,
        AVAILABLE,
        UNREPLAYABLE
    }
}
