package com.mathmod.program;

import com.mathmod.physics.BlockPhysicalProfile;
import com.mathmod.physics.ConstructPhysicalProfile;
import com.mathmod.physics.MassPoint;
import com.mathmod.physics.PhysicsVector;

import java.util.List;

/** Immutable P11 launch-time physics; it is not a replacement for P8 massEquivalent. */
record CapturedConstructPhysics(
        long snapshotVersion,
        BlockPhysicalProfile materialProfile,
        ConstructPhysicalProfile constructProfile
) {
    CapturedConstructPhysics {
        if (snapshotVersion < 0 || materialProfile == null || constructProfile == null) {
            throw new IllegalArgumentException("Invalid captured construct physics");
        }
    }

    static CapturedConstructPhysics capture(long snapshotVersion, ConstructBody body, BlockPhysicalProfile materialProfile) {
        List<MassPoint> points = body.sourceVoxels().stream()
                .map(point -> new MassPoint(new PhysicsVector(point.x(), point.y(), point.z()), materialProfile.physicalMass()))
                .toList();
        ConstructPhysicalProfile aggregate = ConstructPhysicalProfile.aggregate(
                points,
                materialProfile.compressionMassExponent()
        ).compress(body.scale());
        return new CapturedConstructPhysics(snapshotVersion, materialProfile, aggregate);
    }
}
