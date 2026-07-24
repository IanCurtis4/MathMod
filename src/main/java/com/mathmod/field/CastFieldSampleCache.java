package com.mathmod.field;

import com.mathmod.util.NamespacedId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** One instance belongs to exactly one cast/tick and is never persisted or shared. */
public final class CastFieldSampleCache {
    public static final int MAX_ENTRIES = 64;
    private final Map<Key, FieldSampleValue> values = new HashMap<>();

    public FieldSampleValue sample(NamespacedId providerId, SamplePoint point, FieldSampler sampler)
            throws FieldSampleException {
        Key key = new Key(providerId, point);
        FieldSampleValue cached = values.get(key);
        if (cached != null) return cached;
        if (values.size() >= MAX_ENTRIES) {
            throw new FieldSampleException(FieldPlanningIssue.Code.SAMPLE_LIMIT, "Cast sample cache limit exceeded");
        }
        FieldSampleValue sampled;
        try {
            sampled = Objects.requireNonNull(sampler.sample(point), "sampled value");
        } catch (IllegalArgumentException exception) {
            throw new FieldSampleException(FieldPlanningIssue.Code.NON_FINITE_VALUE, exception.getMessage());
        } catch (NullPointerException exception) {
            throw new FieldSampleException(FieldPlanningIssue.Code.PROVIDER_FAILURE, "Provider returned no value");
        }
        values.put(key, sampled);
        return sampled;
    }

    public int size() { return values.size(); }

    private record Key(NamespacedId providerId, SamplePoint point) { }
}
