package com.mathmod.program;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstructPreviewModelTest {
    @Test
    void cavalieriPreviewExposesTheServerCapsAndPhysicalParameters() {
        ConstructPreviewModel preview = ConstructPreviewModel.from(ProgramPresets.cavalieriProjectile());

        assertEquals("minecraft:stone", preview.materialId());
        assertEquals(0.5D, preview.scale());
        assertEquals(0.35D, preview.angularSpeed());
        assertEquals(128, preview.maximumMassEquivalent());
        assertEquals(100, preview.maximumLifetimeTicks());
        assertEquals(2.0D, preview.maximumLaunchSpeed());
        assertTrue(preview.serverAuthoritative());
    }
}
