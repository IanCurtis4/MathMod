package com.mathmod.physics;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DerivedPhysicsCoreTest {
    @Test
    void sampledUnionCountsFullSlabAndOverlappingBoxesWithoutDoubleCounting() {
        assertEquals(1.0D, VoxelShapeVolume.sampledUnion(List.of(new PhysicsBox(0, 0, 0, 1, 1, 1)), 16));
        assertEquals(.5D, VoxelShapeVolume.sampledUnion(List.of(new PhysicsBox(0, 0, 0, 1, .5D, 1)), 16));
        assertEquals(.5D, VoxelShapeVolume.sampledUnion(List.of(
                new PhysicsBox(0, 0, 0, 1, .5D, 1), new PhysicsBox(0, 0, 0, 1, .5D, 1)
        ), 16));
        assertEquals(1.0D, VoxelShapeVolume.sampledUnion(List.of(new PhysicsBox(-2, -2, -2, 2, 2, 2)), 16));
    }

    @Test
    void exactProfileOutranksTagAndFallbackIsStable() {
        PhysicalProfileSnapshot snapshot = new PhysicalProfileSnapshot(4, PhysicsPolicy.defaults(), List.of(
                declaration("mathmod:stone_tag", new PhysicalSelector(PhysicalSelector.Kind.TAG, "minecraft:stone"), PhysicalProfileSource.BUILT_IN, 0, 1.5D),
                declaration("pack:stone", new PhysicalSelector(PhysicalSelector.Kind.BLOCK, "minecraft:stone"), PhysicalProfileSource.DATA_PACK, 0, 4D)
        ));
        BlockPhysicalInput stone = input("minecraft:stone", Set.of("minecraft:stone"));
        BlockPhysicalProfile resolved = snapshot.resolve(stone);

        assertEquals(4D, resolved.density());
        assertEquals(PhysicalProfileSource.DATA_PACK, resolved.source());
        assertEquals(1, snapshot.cacheSize());

        BlockPhysicalProfile fallback = snapshot.resolve(input("example:unknown", Set.of()));
        assertEquals(PhysicalProfileSource.FALLBACK, fallback.source());
        assertEquals(1D, fallback.occupiedVolume());
    }

    @Test
    void sameLayerTagTieIsRejectedAtResolution() {
        PhysicalProfileSnapshot snapshot = new PhysicalProfileSnapshot(1, PhysicsPolicy.defaults(), List.of(
                declaration("pack:a", new PhysicalSelector(PhysicalSelector.Kind.TAG, "forge:metal"), PhysicalProfileSource.DATA_PACK, 4, 3),
                declaration("pack:b", new PhysicalSelector(PhysicalSelector.Kind.TAG, "forge:metal"), PhysicalProfileSource.DATA_PACK, 4, 5)
        ));

        assertThrows(IllegalStateException.class, () -> snapshot.resolve(input("example:metal", Set.of("forge:metal"))));
    }

    @Test
    void aggregateIsMassWeightedPermutationInvariantAndCompressionObeysGamma() {
        List<MassPoint> ordered = List.of(
                new MassPoint(new PhysicsVector(0, 0, 0), 1),
                new MassPoint(new PhysicsVector(4, 0, 0), 3)
        );
        ConstructPhysicalProfile gammaZero = ConstructPhysicalProfile.aggregate(ordered, 0);
        ConstructPhysicalProfile reordered = ConstructPhysicalProfile.aggregate(List.of(ordered.get(1), ordered.get(0)), 0);

        assertEquals(3D, gammaZero.centerOfMass().x());
        assertEquals(gammaZero.centerOfMass(), reordered.centerOfMass());
        assertEquals(gammaZero.inertia(), reordered.inertia());
        assertEquals(4D, gammaZero.totalMass());
        assertEquals(gammaZero.totalMass(), gammaZero.compress(.5D).totalMass());
        assertEquals(gammaZero.scalarInertia(new PhysicsVector(0, 1, 0)) * .25D,
                gammaZero.compress(.5D).scalarInertia(new PhysicsVector(0, 1, 0)), 1.0E-9D);

        ConstructPhysicalProfile gammaThree = ConstructPhysicalProfile.aggregate(ordered, 3).compress(.5D);
        assertEquals(.5D, gammaThree.totalMass(), 1.0E-9D);
    }

    @Test
    void zeroMassAggregateUsesStableArithmeticCenterAndZeroTensor() {
        ConstructPhysicalProfile profile = ConstructPhysicalProfile.aggregate(List.of(
                new MassPoint(new PhysicsVector(0, 0, 0), 0), new MassPoint(new PhysicsVector(2, 0, 0), 0)
        ), 0);

        assertEquals(0D, profile.totalMass());
        assertEquals(new PhysicsVector(1, 0, 0), profile.centerOfMass());
        assertEquals(InertiaTensor.ZERO, profile.inertia());
    }

    private static PhysicalProfileDeclaration declaration(
            String id, PhysicalSelector selector, PhysicalProfileSource source, int priority, double density
    ) {
        return new PhysicalProfileDeclaration(id, selector, source, priority, density, null, null, null, null, null, null);
    }

    private static BlockPhysicalInput input(String blockId, Set<String> tags) {
        return new BlockPhysicalInput(blockId, blockId + "[]", tags,
                List.of(new PhysicsBox(0, 0, 0, 1, 1, 1)), 1.5D, 6D);
    }
}
