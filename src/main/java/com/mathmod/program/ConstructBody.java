package com.mathmod.program;

import java.util.List;

/** Immutable transient body, expressed in source voxel centers rather than placed blocks. */
record ConstructBody(
        String materialId,
        List<GeometryPoint> sourceVoxels,
        GeometryPoint centerOfMass,
        int massEquivalent,
        double scale,
        GeometryPoint spinAxis,
        double angularSpeed
) {
    ConstructBody {
        sourceVoxels = List.copyOf(sourceVoxels);
        if (sourceVoxels.isEmpty() || sourceVoxels.size() > 128 || massEquivalent != sourceVoxels.size()) {
            throw new IllegalArgumentException("Invalid construct body");
        }
    }

    static ConstructBody materialize(String materialId, List<VoxelCoordinate> positions) {
        List<GeometryPoint> voxels = positions.stream().map(VoxelCoordinate::center).toList();
        double x = voxels.stream().mapToDouble(GeometryPoint::x).average().orElseThrow();
        double y = voxels.stream().mapToDouble(GeometryPoint::y).average().orElseThrow();
        double z = voxels.stream().mapToDouble(GeometryPoint::z).average().orElseThrow();
        return new ConstructBody(materialId, voxels, new GeometryPoint(x, y, z), voxels.size(),
                1.0D, new GeometryPoint(0, 1, 0), 0.0D);
    }

    ConstructBody compress(double nextScale) {
        if (!Double.isFinite(nextScale) || nextScale < 0.25D || nextScale > 1.0D) {
            throw new IllegalArgumentException("Invalid construct scale");
        }
        return new ConstructBody(materialId, sourceVoxels, centerOfMass, massEquivalent,
                nextScale, spinAxis, angularSpeed);
    }

    ConstructBody spin(GeometryPoint axis, double speed) {
        if (axis == null || !Double.isFinite(axis.x()) || !Double.isFinite(axis.y()) || !Double.isFinite(axis.z())
                || axis.lengthSqr() <= VoxelRegion.EPSILON || !Double.isFinite(speed)
                || Math.abs(speed) > Math.PI / 4.0D) {
            throw new IllegalArgumentException("Invalid construct spin");
        }
        return new ConstructBody(materialId, sourceVoxels, centerOfMass, massEquivalent,
                scale, axis.normalize(), speed);
    }

    double inertiaAboutSpinAxis() {
        return sourceVoxels.stream().mapToDouble(point -> {
            GeometryPoint relative = point.subtract(centerOfMass);
            GeometryPoint cross = new GeometryPoint(
                    relative.y() * spinAxis.z() - relative.z() * spinAxis.y(),
                    relative.z() * spinAxis.x() - relative.x() * spinAxis.z(),
                    relative.x() * spinAxis.y() - relative.y() * spinAxis.x()
            );
            return cross.lengthSqr();
        }).sum() * scale * scale;
    }

    double collisionRadius() {
        double furthest = sourceVoxels.stream()
                .mapToDouble(point -> point.subtract(centerOfMass).length())
                .max().orElse(0.0D);
        return Math.max(0.25D, scale * (furthest + 0.866D));
    }
}
