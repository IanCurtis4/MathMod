package com.mathmod.acquisition;

import com.mathmod.manuscript.ManuscriptSnapshotBuildResult;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class ManuscriptAcquisitionPublicationStore {
    private final AtomicReference<ManuscriptAcquisitionPublication> active =
            new AtomicReference<>(ManuscriptAcquisitionPublication.empty());

    public ManuscriptAcquisitionPublication publication() {
        return active.get();
    }

    public boolean publish(
            ManuscriptSnapshotBuildResult manuscripts,
            ManuscriptAcquisitionBuildResult acquisition,
            ManuscriptAcquisitionConfig config
    ) {
        Objects.requireNonNull(manuscripts, "manuscripts");
        Objects.requireNonNull(acquisition, "acquisition");
        Objects.requireNonNull(config, "config");
        if (!manuscripts.publishable() || !acquisition.publishable()) {
            return false;
        }
        active.updateAndGet(current -> new ManuscriptAcquisitionPublication(
                current.generation() + 1,
                manuscripts.snapshot(),
                acquisition.snapshot(),
                config
        ));
        return true;
    }

    public ManuscriptAcquisitionPublication refreshConfig(ManuscriptAcquisitionConfig config) {
        Objects.requireNonNull(config, "config");
        return active.updateAndGet(current -> new ManuscriptAcquisitionPublication(
                current.generation() + 1,
                current.manuscripts(),
                current.acquisition(),
                config
        ));
    }
}
