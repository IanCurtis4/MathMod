package com.mathmod.acquisition;

import com.mathmod.manuscript.ManuscriptSnapshot;

import java.util.Objects;

/** One server-owned generation consumed by future loot and merchant factories. */
public record ManuscriptAcquisitionPublication(
        long generation,
        ManuscriptSnapshot manuscripts,
        ManuscriptAcquisitionSnapshot acquisition,
        ManuscriptAcquisitionConfig config
) {
    public ManuscriptAcquisitionPublication {
        if (generation < 0) {
            throw new IllegalArgumentException("generation must not be negative");
        }
        manuscripts = Objects.requireNonNull(manuscripts, "manuscripts");
        acquisition = Objects.requireNonNull(acquisition, "acquisition");
        config = Objects.requireNonNull(config, "config");
    }

    public static ManuscriptAcquisitionPublication empty() {
        return new ManuscriptAcquisitionPublication(
                0,
                ManuscriptSnapshot.empty(),
                ManuscriptAcquisitionSnapshot.empty(),
                ManuscriptAcquisitionConfig.defaults()
        );
    }
}
