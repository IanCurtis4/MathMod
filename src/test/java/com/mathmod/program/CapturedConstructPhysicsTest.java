package com.mathmod.program;

import com.mathmod.physics.BlockPhysicalProfile;
import com.mathmod.physics.PhysicalProfileSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CapturedConstructPhysicsTest {
    @Test
    void captureUsesLaunchVersionAndKeepsP8MassEquivalentSeparate() {
        ConstructBody body = ConstructBody.materialize("minecraft:stone", List.of(
                new VoxelCoordinate(0, 0, 0), new VoxelCoordinate(2, 0, 0)
        )).compress(0.5D);
        BlockPhysicalProfile material = new BlockPhysicalProfile(
                3D, 1D, 3D, 1D, .5D, 0D, 1D, 0D, 0D, PhysicalProfileSource.DATA_PACK
        );

        CapturedConstructPhysics captured = CapturedConstructPhysics.capture(12, body, material);

        assertEquals(12, captured.snapshotVersion());
        assertEquals(material, captured.materialProfile());
        assertEquals(6D, captured.constructProfile().totalMass());
        assertEquals(1.5D, captured.constructProfile().centerOfMass().x());
        assertEquals(2, body.massEquivalent());
        assertEquals(0.5D, body.scale());
    }
}
