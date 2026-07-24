package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;

import java.util.Objects;
import java.util.Optional;

public record ManuscriptReferenceMigration(
        NamespacedId originalId,
        Optional<NamespacedId> canonicalId,
        Status status
) {
    public ManuscriptReferenceMigration {
        originalId = Objects.requireNonNull(originalId, "originalId");
        canonicalId = Objects.requireNonNull(canonicalId, "canonicalId");
        status = Objects.requireNonNull(status, "status");
        if (status == Status.MISSING && canonicalId.isPresent()) {
            throw new IllegalArgumentException("A missing reference cannot have a canonical id");
        }
        if (status != Status.MISSING && canonicalId.isEmpty()) {
            throw new IllegalArgumentException("A resolved reference must have a canonical id");
        }
    }

    public boolean requiresPersistenceUpdate() {
        return status == Status.ALIASED;
    }

    public enum Status {
        CURRENT,
        ALIASED,
        MISSING
    }
}
