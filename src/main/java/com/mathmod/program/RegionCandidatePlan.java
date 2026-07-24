package com.mathmod.program;

import java.util.List;

/** Immutable, pure voxel-center result used before any construction transaction exists. */
public record RegionCandidatePlan(GeometryBounds bounds, List<VoxelCoordinate> positions, int latticeVisits) {
    public RegionCandidatePlan {
        positions = List.copyOf(positions);
    }
}
