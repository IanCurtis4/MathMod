package com.mathmod.field;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** Reload swaps definitions and executable samplers in one atomic operation. */
public final class FieldProviderPublicationStore {
    private final AtomicReference<FieldProviderPublication> active =
            new AtomicReference<>(FieldProviderPublication.empty());

    public FieldProviderPublication snapshot() {
        return active.get();
    }

    public void publish(FieldProviderPublication publication) {
        active.set(Objects.requireNonNull(publication, "publication"));
    }
}
