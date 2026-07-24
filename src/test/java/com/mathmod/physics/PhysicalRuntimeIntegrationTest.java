package com.mathmod.physics;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhysicalRuntimeIntegrationTest {
    @Test
    void publicationSwapsTheEntireSnapshotAndStartsWithAnEmptyCache() {
        PhysicalProfileSnapshot before = PhysicalProfiles.snapshot();
        PhysicalProfiles.publishData(PhysicsPolicy.defaults(), List.of());
        PhysicalProfileSnapshot after = PhysicalProfiles.snapshot();

        assertEquals(before.version() + 1, after.version());
        assertEquals(0, after.cacheSize());
        assertEquals(PhysicsPolicy.defaults(), after.policy());
    }
}
