package com.mathmod.program;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegionCandidatePlannerTest {
    @Test
    void closedBoundsUseBlockCentersAndStableLayerOrder() {
        VoxelRegion region = new VoxelBoxRegion(
                new GeometryPoint(-0.5D, -0.5D, -0.5D),
                new GeometryPoint(0.5D, 0.5D, 0.5D)
        );

        RegionCandidatePlanner.Result result = RegionCandidatePlanner.plan(region);

        assertTrue(result.valid());
        assertEquals(List.of(
                new VoxelCoordinate(-1, -1, -1), new VoxelCoordinate(0, -1, -1),
                new VoxelCoordinate(-1, -1, 0), new VoxelCoordinate(0, -1, 0),
                new VoxelCoordinate(-1, 0, -1), new VoxelCoordinate(0, 0, -1),
                new VoxelCoordinate(-1, 0, 0), new VoxelCoordinate(0, 0, 0)
        ), result.plan().orElseThrow().positions());
        assertEquals(8, result.plan().orElseThrow().latticeVisits());
    }

    @Test
    void booleanDifferenceRemovesTheClosedBoundaryOfItsSecondOperand() {
        VoxelRegion outer = new VoxelSphereRegion(new GeometryPoint(0, 0, 0), 1.0D);
        VoxelRegion inner = new VoxelSphereRegion(new GeometryPoint(0, 0, 0), 0.5D);
        VoxelRegion difference = new VoxelDifferenceRegion(outer, inner);

        assertTrue(outer.contains(new GeometryPoint(0.5D, 0.0D, 0.0D)));
        assertFalse(difference.contains(new GeometryPoint(0.5D, 0.0D, 0.0D)));
    }

    @Test
    void revolutionSupportsHollowBandsAndRejectsInvertedProfiles() {
        VoxelRegion hollow = new VoxelRevolutionRegion(
                new GeometryPoint(0, 0, 0), new GeometryPoint(0.0D, 1.0D, 0.0D), 0.5D, 1.0D, -1.0D, 1.0D
        );

        assertFalse(hollow.contains(new GeometryPoint(0, 0, 0)));
        assertTrue(hollow.contains(new GeometryPoint(0.75D, 0.0D, 0.0D)));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                new VoxelRevolutionRegion(
                        new GeometryPoint(0, 0, 0), new GeometryPoint(0.0D, 1.0D, 0.0D), 1.0D, 0.5D, -1.0D, 1.0D
                )
        );
    }

    @Test
    void plannerRejectsRatherThanTruncatesOversizedBounds() {
        VoxelRegion region = new VoxelBoxRegion(
                new GeometryPoint(-8.0D, -8.0D, -8.0D), new GeometryPoint(8.0D, 8.0D, 8.0D)
        );

        RegionCandidatePlanner.Result result = RegionCandidatePlanner.plan(region);

        assertFalse(result.valid());
        assertEquals("candidate_limit", result.issue());
    }
}
