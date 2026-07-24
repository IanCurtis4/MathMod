package com.mathmod.program;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

interface SpatialRegion {
    double EPSILON = 1.0E-7D;

    boolean contains(Vec3 position);

    AABB bounds();
}

record SphereSpatialRegion(Vec3 center, double radius) implements SpatialRegion {
    @Override
    public boolean contains(Vec3 position) {
        return position.distanceToSqr(center) <= radius * radius + SpatialRegion.EPSILON;
    }

    @Override
    public AABB bounds() {
        return new AABB(
                center.x - radius,
                center.y - radius,
                center.z - radius,
                center.x + radius,
                center.y + radius,
                center.z + radius
        );
    }
}

record BoxSpatialRegion(Vec3 min, Vec3 max) implements SpatialRegion {
    public BoxSpatialRegion {
        double minX = Math.min(min.x, max.x);
        double minY = Math.min(min.y, max.y);
        double minZ = Math.min(min.z, max.z);
        double maxX = Math.max(min.x, max.x);
        double maxY = Math.max(min.y, max.y);
        double maxZ = Math.max(min.z, max.z);
        min = new Vec3(minX, minY, minZ);
        max = new Vec3(maxX, maxY, maxZ);
    }

    @Override
    public boolean contains(Vec3 position) {
        return position.x >= min.x - SpatialRegion.EPSILON && position.x <= max.x + SpatialRegion.EPSILON
                && position.y >= min.y - SpatialRegion.EPSILON && position.y <= max.y + SpatialRegion.EPSILON
                && position.z >= min.z - SpatialRegion.EPSILON && position.z <= max.z + SpatialRegion.EPSILON;
    }

    @Override
    public AABB bounds() {
        return new AABB(min, max);
    }
}

record UnionSpatialRegion(SpatialRegion first, SpatialRegion second) implements SpatialRegion {
    @Override
    public boolean contains(Vec3 position) {
        return first.contains(position) || second.contains(position);
    }

    @Override
    public AABB bounds() {
        AABB a = first.bounds();
        AABB b = second.bounds();
        return new AABB(
                Math.min(a.minX, b.minX), Math.min(a.minY, b.minY), Math.min(a.minZ, b.minZ),
                Math.max(a.maxX, b.maxX), Math.max(a.maxY, b.maxY), Math.max(a.maxZ, b.maxZ)
        );
    }
}

record IntersectionSpatialRegion(SpatialRegion first, SpatialRegion second) implements SpatialRegion {
    @Override
    public boolean contains(Vec3 position) {
        return first.contains(position) && second.contains(position);
    }

    @Override
    public AABB bounds() {
        AABB a = first.bounds();
        AABB b = second.bounds();
        double minX = Math.max(a.minX, b.minX);
        double minY = Math.max(a.minY, b.minY);
        double minZ = Math.max(a.minZ, b.minZ);
        double maxX = Math.min(a.maxX, b.maxX);
        double maxY = Math.min(a.maxY, b.maxY);
        double maxZ = Math.min(a.maxZ, b.maxZ);
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return new AABB(0, 0, 0, 0, 0, 0);
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}

record DifferenceSpatialRegion(SpatialRegion first, SpatialRegion second) implements SpatialRegion {
    @Override
    public boolean contains(Vec3 position) {
        return first.contains(position) && !second.contains(position);
    }

    @Override
    public AABB bounds() {
        return first.bounds();
    }
}

record RevolutionSpatialRegion(
        Vec3 origin,
        Vec3 axis,
        double innerRadius,
        double outerRadius,
        double lower,
        double upper
) implements SpatialRegion {
    RevolutionSpatialRegion {
        if (!finite(origin) || !finite(axis)
                || axis.lengthSqr() <= SpatialRegion.EPSILON
                || !Double.isFinite(innerRadius) || !Double.isFinite(outerRadius)
                || !Double.isFinite(lower) || !Double.isFinite(upper)
                || lower >= upper
                || innerRadius < 0.0D
                || innerRadius > outerRadius
                || outerRadius > ProgramExecutionPolicy.MAX_REGION_RADIUS
                || Math.sqrt(Math.max(lower * lower, upper * upper) + outerRadius * outerRadius)
                > ProgramExecutionPolicy.MAX_REGION_RADIUS) {
            throw new IllegalArgumentException("Invalid solid of revolution");
        }
        axis = axis.normalize();
    }

    @Override
    public boolean contains(Vec3 position) {
        Vec3 displacement = position.subtract(origin);
        double axial = displacement.dot(axis);
        if (axial < lower - SpatialRegion.EPSILON || axial > upper + SpatialRegion.EPSILON) {
            return false;
        }
        double radial = displacement.subtract(axis.scale(axial)).length();
        return radial >= innerRadius - SpatialRegion.EPSILON
                && radial <= outerRadius + SpatialRegion.EPSILON;
    }

    @Override
    public AABB bounds() {
        double extent = Math.sqrt(Math.max(lower * lower, upper * upper) + outerRadius * outerRadius);
        return new AABB(
                origin.x - extent, origin.y - extent, origin.z - extent,
                origin.x + extent, origin.y + extent, origin.z + extent
        );
    }

    private static boolean finite(Vec3 vector) {
        return Double.isFinite(vector.x) && Double.isFinite(vector.y) && Double.isFinite(vector.z);
    }
}
