package com.mathmod.field;

import com.mathmod.util.NamespacedId;

import java.util.Objects;

public record SampleRequest(NamespacedId providerId, SamplePoint point) {
    public SampleRequest {
        providerId = Objects.requireNonNull(providerId, "providerId");
        point = Objects.requireNonNull(point, "point");
    }
}
