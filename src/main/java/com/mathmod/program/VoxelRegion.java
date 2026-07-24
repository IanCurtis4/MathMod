package com.mathmod.program;

/** Pure P8 geometry; it intentionally has no Minecraft runtime dependency. */
interface VoxelRegion {
    double EPSILON = 1.0E-7D;

    boolean contains(GeometryPoint point);

    GeometryBounds bounds();
}

record GeometryPoint(double x, double y, double z) {
    GeometryPoint subtract(GeometryPoint other) { return new GeometryPoint(x - other.x, y - other.y, z - other.z); }
    GeometryPoint add(GeometryPoint other) { return new GeometryPoint(x + other.x, y + other.y, z + other.z); }
    GeometryPoint scale(double factor) { return new GeometryPoint(x * factor, y * factor, z * factor); }
    double dot(GeometryPoint other) { return x * other.x + y * other.y + z * other.z; }
    double lengthSqr() { return dot(this); }
    double length() { return Math.sqrt(lengthSqr()); }
    GeometryPoint normalize() { return scale(1.0D / length()); }
}

record GeometryBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) { }

record VoxelCoordinate(int x, int y, int z) {
    GeometryPoint center() { return new GeometryPoint(x + 0.5D, y + 0.5D, z + 0.5D); }
}

record VoxelSphereRegion(GeometryPoint center, double radius) implements VoxelRegion {
    @Override public boolean contains(GeometryPoint point) {
        return point.subtract(center).lengthSqr() <= radius * radius + EPSILON;
    }
    @Override public GeometryBounds bounds() {
        return new GeometryBounds(center.x() - radius, center.y() - radius, center.z() - radius,
                center.x() + radius, center.y() + radius, center.z() + radius);
    }
}

record VoxelBoxRegion(GeometryPoint first, GeometryPoint second) implements VoxelRegion {
    @Override public boolean contains(GeometryPoint point) {
        return point.x() >= Math.min(first.x(), second.x()) - EPSILON && point.x() <= Math.max(first.x(), second.x()) + EPSILON
                && point.y() >= Math.min(first.y(), second.y()) - EPSILON && point.y() <= Math.max(first.y(), second.y()) + EPSILON
                && point.z() >= Math.min(first.z(), second.z()) - EPSILON && point.z() <= Math.max(first.z(), second.z()) + EPSILON;
    }
    @Override public GeometryBounds bounds() {
        return new GeometryBounds(Math.min(first.x(), second.x()), Math.min(first.y(), second.y()), Math.min(first.z(), second.z()),
                Math.max(first.x(), second.x()), Math.max(first.y(), second.y()), Math.max(first.z(), second.z()));
    }
}

record VoxelDifferenceRegion(VoxelRegion first, VoxelRegion second) implements VoxelRegion {
    @Override public boolean contains(GeometryPoint point) { return first.contains(point) && !second.contains(point); }
    @Override public GeometryBounds bounds() { return first.bounds(); }
}

record VoxelRevolutionRegion(
        GeometryPoint origin, GeometryPoint axis, double innerRadius, double outerRadius, double lower, double upper
) implements VoxelRegion {
    VoxelRevolutionRegion {
        if (!finite(origin) || !finite(axis) || axis.lengthSqr() <= EPSILON || !Double.isFinite(innerRadius)
                || !Double.isFinite(outerRadius) || !Double.isFinite(lower) || !Double.isFinite(upper)
                || lower >= upper || innerRadius < 0 || innerRadius > outerRadius || outerRadius > 8
                || Math.sqrt(Math.max(lower * lower, upper * upper) + outerRadius * outerRadius) > 8) {
            throw new IllegalArgumentException("Invalid solid of revolution");
        }
        axis = axis.normalize();
    }
    @Override public boolean contains(GeometryPoint point) {
        GeometryPoint displacement = point.subtract(origin);
        double axial = displacement.dot(axis);
        if (axial < lower - EPSILON || axial > upper + EPSILON) return false;
        double radial = displacement.subtract(axis.scale(axial)).length();
        return radial >= innerRadius - EPSILON && radial <= outerRadius + EPSILON;
    }
    @Override public GeometryBounds bounds() {
        double extent = Math.sqrt(Math.max(lower * lower, upper * upper) + outerRadius * outerRadius);
        return new GeometryBounds(origin.x() - extent, origin.y() - extent, origin.z() - extent,
                origin.x() + extent, origin.y() + extent, origin.z() + extent);
    }
    private static boolean finite(GeometryPoint point) {
        return Double.isFinite(point.x()) && Double.isFinite(point.y()) && Double.isFinite(point.z());
    }
}
