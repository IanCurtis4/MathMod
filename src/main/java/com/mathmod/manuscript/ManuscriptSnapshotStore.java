package com.mathmod.manuscript;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class ManuscriptSnapshotStore {
    private final AtomicReference<ManuscriptSnapshot> active =
            new AtomicReference<>(ManuscriptSnapshot.empty());

    public ManuscriptSnapshot snapshot() {
        return active.get();
    }

    public boolean publish(ManuscriptSnapshotBuildResult result) {
        Objects.requireNonNull(result, "result");
        if (!result.publishable()) {
            return false;
        }
        active.set(result.snapshot());
        return true;
    }
}
