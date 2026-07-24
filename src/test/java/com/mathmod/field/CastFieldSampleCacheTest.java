package com.mathmod.field;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CastFieldSampleCacheTest {
    @Test
    void identicalSamplesAreReadOnceWithinOneCast() throws Exception {
        CastFieldSampleCache cache = new CastFieldSampleCache();
        AtomicInteger calls = new AtomicInteger();
        NamespacedId provider = NamespacedId.of("test", "field");
        SamplePoint point = new SamplePoint(1, 2, 3);
        FieldSampler sampler = ignored -> {
            calls.incrementAndGet();
            return new FieldSampleValue.Scalar(7);
        };

        assertEquals(new FieldSampleValue.Scalar(7), cache.sample(provider, point, sampler));
        assertEquals(new FieldSampleValue.Scalar(7), cache.sample(provider, point, sampler));
        assertEquals(1, calls.get());
        assertEquals(1, cache.size());
    }

    @Test
    void providerFailuresAreNotCachedAsValues() {
        CastFieldSampleCache cache = new CastFieldSampleCache();
        assertThrows(FieldSampleException.class, () -> cache.sample(
                NamespacedId.of("test", "field"), new SamplePoint(0, 0, 0),
                ignored -> { throw new FieldSampleException(FieldPlanningIssue.Code.PROVIDER_FAILURE, "failed"); }
        ));
        assertEquals(0, cache.size());
    }
}
