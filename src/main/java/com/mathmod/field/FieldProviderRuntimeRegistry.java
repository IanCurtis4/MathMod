package com.mathmod.field;

import com.mathmod.util.NamespacedId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Server-owned runtime samplers; clients receive only declarative definitions. */
public final class FieldProviderRuntimeRegistry {
    private final Map<NamespacedId, FieldSamplerFactory> samplers;

    public FieldProviderRuntimeRegistry(Map<NamespacedId, FieldSamplerFactory> samplers) {
        this.samplers = Map.copyOf(new LinkedHashMap<>(samplers));
    }

    public Optional<FieldSamplerFactory> find(NamespacedId id) {
        return Optional.ofNullable(samplers.get(id));
    }

    public Optional<FieldSampler> sampler(NamespacedId id, FieldSamplingContext context) {
        return find(id).map(factory -> factory.create(context));
    }

    public Set<NamespacedId> ids() {
        return samplers.keySet();
    }
}
