package com.mathmod.physics;

import java.util.List;

/** Deterministic sampled-union volume for canonical collision boxes. */
public final class VoxelShapeVolume {
    private VoxelShapeVolume() {}

    public static double sampledUnion(List<PhysicsBox> sourceBoxes, int resolution) {
        if (resolution < 4 || resolution > 32 || (resolution & (resolution - 1)) != 0) {
            throw new IllegalArgumentException("Shape resolution must be a power of two in [4, 32]");
        }
        List<PhysicsBox> boxes = sourceBoxes.stream().map(PhysicsBox::clampedUnitCube)
                .filter(PhysicsBox::hasPositiveExtent).toList();
        if (boxes.isEmpty()) return 0.0D;
        int occupied = 0;
        for (int x = 0; x < resolution; x++) for (int y = 0; y < resolution; y++) for (int z = 0; z < resolution; z++) {
            double centerX = (x + 0.5D) / resolution;
            double centerY = (y + 0.5D) / resolution;
            double centerZ = (z + 0.5D) / resolution;
            if (boxes.stream().anyMatch(box -> box.containsCenter(centerX, centerY, centerZ))) occupied++;
        }
        return occupied / (double) (resolution * resolution * resolution);
    }
}
