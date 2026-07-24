package com.mathmod.program;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConstructBodyTest {
    @Test
    void compressionRetainsItemMassAndCenterWhileReducingInertia() {
        ConstructBody body = ConstructBody.materialize("minecraft:stone", List.of(
                new VoxelCoordinate(0, 0, 0), new VoxelCoordinate(2, 0, 0)
        )).spin(new GeometryPoint(0, 1, 0), 0.25D);

        ConstructBody compressed = body.compress(0.5D);

        assertEquals(2, compressed.massEquivalent());
        assertEquals(body.centerOfMass(), compressed.centerOfMass());
        assertEquals(body.inertiaAboutSpinAxis() * 0.25D, compressed.inertiaAboutSpinAxis(), 0.000001D);
        assertEquals(0.5D, compressed.scale());
    }

    @Test
    void bodyRejectsUnboundedCompressionAndSpin() {
        ConstructBody body = ConstructBody.materialize("minecraft:stone", List.of(new VoxelCoordinate(0, 0, 0)));

        assertThrows(IllegalArgumentException.class, () -> body.compress(0.24D));
        assertThrows(IllegalArgumentException.class, () -> body.spin(new GeometryPoint(0, 0, 0), 0.1D));
        assertThrows(IllegalArgumentException.class, () -> body.spin(new GeometryPoint(0, 1, 0), Math.PI));
    }
}
