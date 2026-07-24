package com.mathmod.field;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** Publishes complete immutable snapshots; readers never observe a partial reload. */
public final class FieldProviderRegistry {
    private final AtomicReference<FieldProviderSnapshot> active =
            new AtomicReference<>(FieldProviderSnapshot.empty());

    public FieldProviderSnapshot snapshot() {
        return active.get();
    }

    public void publish(FieldProviderSnapshot snapshot) {
        active.set(Objects.requireNonNull(snapshot, "snapshot"));
    }
}
