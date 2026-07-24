package com.mathmod.program;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Server-independent geometry planner. World, permission, and payment checks belong to P8-B. */
public final class RegionCandidatePlanner {
    public static final int MAX_LATTICE_VISITS = 4_096;
    public static final int MAX_CANDIDATES = 256;

    private RegionCandidatePlanner() {
    }

    public static Result plan(VoxelRegion region) {
        GeometryBounds bounds = region.bounds();
        if (!finite(bounds)) {
            return Result.failure("non_finite_bounds");
        }
        int minX = first(bounds.minX());
        int minY = first(bounds.minY());
        int minZ = first(bounds.minZ());
        int maxX = last(bounds.maxX());
        int maxY = last(bounds.maxY());
        int maxZ = last(bounds.maxZ());
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return Result.success(new RegionCandidatePlan(bounds, List.of(), 0));
        }

        List<VoxelCoordinate> positions = new ArrayList<>();
        int visits = 0;
        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
                    if (++visits > MAX_LATTICE_VISITS) {
                        return Result.failure("lattice_limit");
                    }
                    VoxelCoordinate position = new VoxelCoordinate(x, y, z);
                    if (!region.contains(position.center())) {
                        continue;
                    }
                    positions.add(position);
                    if (positions.size() > MAX_CANDIDATES) {
                        return Result.failure("candidate_limit");
                    }
                }
            }
        }
        return Result.success(new RegionCandidatePlan(bounds, positions, visits));
    }

    static Result plan(SpatialRegion region) {
        AABB bounds = region.bounds();
        GeometryBounds pureBounds = new GeometryBounds(
                bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ
        );
        if (!finite(pureBounds)) {
            return Result.failure("non_finite_bounds");
        }
        int minX = first(pureBounds.minX());
        int minY = first(pureBounds.minY());
        int minZ = first(pureBounds.minZ());
        int maxX = last(pureBounds.maxX());
        int maxY = last(pureBounds.maxY());
        int maxZ = last(pureBounds.maxZ());
        List<VoxelCoordinate> positions = new ArrayList<>();
        int visits = 0;
        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
                    if (++visits > MAX_LATTICE_VISITS) return Result.failure("lattice_limit");
                    VoxelCoordinate position = new VoxelCoordinate(x, y, z);
                    if (region.contains(new Vec3(x + 0.5D, y + 0.5D, z + 0.5D))) {
                        positions.add(position);
                        if (positions.size() > MAX_CANDIDATES) return Result.failure("candidate_limit");
                    }
                }
            }
        }
        return Result.success(new RegionCandidatePlan(pureBounds, positions, visits));
    }

    private static int first(double minimum) {
        return (int) Math.ceil(minimum - 0.5D - VoxelRegion.EPSILON);
    }

    private static int last(double maximum) {
        return (int) Math.floor(maximum - 0.5D + VoxelRegion.EPSILON);
    }

    private static boolean finite(GeometryBounds bounds) {
        return Double.isFinite(bounds.minX()) && Double.isFinite(bounds.minY()) && Double.isFinite(bounds.minZ())
                && Double.isFinite(bounds.maxX()) && Double.isFinite(bounds.maxY()) && Double.isFinite(bounds.maxZ());
    }

    public record Result(Optional<RegionCandidatePlan> plan, String issue) {
        public Result {
            plan = plan == null ? Optional.empty() : plan;
            issue = issue == null ? "" : issue;
        }

        public static Result success(RegionCandidatePlan plan) {
            return new Result(Optional.of(plan), "");
        }

        public static Result failure(String issue) {
            return new Result(Optional.empty(), issue);
        }

        public boolean valid() {
            return plan.isPresent();
        }
    }
}
