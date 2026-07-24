package com.mathmod.physics;

import com.mathmod.MathMod;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Dedicated-server checks for the P11 runtime boundary. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class P11GameTests {
    private P11GameTests() {
    }

    @GameTest(template = "empty")
    public static void canonicalStoneProfileIsFiniteAndBounded(GameTestHelper helper) {
        BlockPhysicalInput input = CanonicalBlockPhysicalInputAdapter.from(Blocks.STONE.defaultBlockState());
        BlockPhysicalProfile profile = PhysicalProfiles.resolve(Blocks.STONE.defaultBlockState());

        helper.assertTrue("minecraft:stone".equals(input.blockId()), "P11 adapter must preserve the canonical block id");
        helper.assertTrue(
                Math.abs(VoxelShapeVolume.sampledUnion(input.canonicalCollisionBoxes(), 16) - 1D) < 0.000001D,
                "P11 canonical stone shape must occupy one block"
        );
        helper.assertTrue(profile.physicalMass() >= 0D && profile.physicalMass() <= 256D,
                "P11 resolved physical mass must remain bounded");
        helper.assertTrue(PhysicalProfiles.snapshot().version() > 0L,
                "P11 reload listener must publish a server snapshot before construct execution");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void profilePublicationSwapsOnlyFutureSnapshots(GameTestHelper helper) {
        PhysicalProfileSnapshot before = PhysicalProfiles.snapshot();
        PhysicalProfiles.publishData(before.policy(), before.declarations());
        PhysicalProfileSnapshot after = PhysicalProfiles.snapshot();

        helper.assertTrue(after.version() == before.version() + 1,
                "A valid physical publication must atomically advance the snapshot version");
        helper.assertTrue(after.cacheSize() == 0,
                "A newly published physical snapshot must begin with an empty cache");
        helper.assertTrue(PhysicalProfiles.resolve(Blocks.STONE.defaultBlockState()).physicalMass() >= 0.0D,
                "Future resolutions must use a finite profile from the published snapshot");
        helper.succeed();
    }
}
