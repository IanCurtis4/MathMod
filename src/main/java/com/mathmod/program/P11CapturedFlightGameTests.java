package com.mathmod.program;

import com.mathmod.MathMod;
import com.mathmod.physics.PhysicalProfiles;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/** P11 captured-flight GameTests reside with the package-private flight authority. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class P11CapturedFlightGameTests {
    private P11CapturedFlightGameTests() {
    }

    @GameTest(template = "empty", batch = "p12_p11_reload", timeoutTicks = 300)
    public static void capturedFlightRetainsProfileVersionAcrossRealReload(GameTestHelper helper) {
        ConstructFlightManager.clearForTests();
        MinecraftServer server = helper.getLevel().getServer();
        long capturedVersion = PhysicalProfiles.snapshot().version();
        ServerPlayer firstOwner = fundedPlayer(helper, 1);
        ConstructBody body = stoneBody();
        helper.assertTrue(ConstructFlightManager.launch(firstOwner, body, firstOwner.position(), new Vec3(1.0D, 0.0D, 0.0D)),
                "the first funded flight must launch under snapshot N");
        helper.assertTrue(ConstructFlightManager.activeSnapshotVersion(firstOwner.getUUID()).orElseThrow() == capturedVersion,
                "the active owner-A flight must capture snapshot N");
        CompletableFuture<Void> reload = server.reloadResources(List.copyOf(server.getPackRepository().getSelectedIds()));

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(reload.isDone(), "the real server reload must finish before the bounded timeout"))
                .thenExecute(() -> {
                    try {
                        helper.assertFalse(reload.isCompletedExceptionally(), "the real server reload must not complete exceptionally");
                        long replacementVersion = PhysicalProfiles.snapshot().version();
                        helper.assertTrue(replacementVersion == capturedVersion + 1,
                                "the physical-profile reload must publish exactly snapshot N+1");
                        helper.assertTrue(ConstructFlightManager.activeSnapshotVersion(firstOwner.getUUID()).orElseThrow() == capturedVersion,
                                "the active owner-A flight must retain captured snapshot N across real reload");

                        ServerPlayer secondOwner = fundedPlayer(helper, 1);
                        helper.assertTrue(ConstructFlightManager.launch(secondOwner, body, secondOwner.position(), new Vec3(1.0D, 0.0D, 0.0D)),
                                "a funded owner-B flight must launch after the reload");
                        helper.assertTrue(ConstructFlightManager.activeSnapshotVersion(secondOwner.getUUID()).orElseThrow() == replacementVersion,
                                "the future owner-B flight must capture snapshot N+1");
                    } finally {
                        ConstructFlightManager.clearForTests();
                    }
                })
                .thenSucceed();
    }

    @GameTest(template = "empty")
    public static void capturedProfileFlightStopsOnCollisionWithoutTerrainMutation(GameTestHelper helper) {
        ConstructFlightManager.clearForTests();
        try {
            ServerPlayer owner = fundedPlayer(helper, 1);
            BlockPos origin = helper.absolutePos(new BlockPos(1, 2, 1));
            BlockPos collision = origin.east();
            helper.getLevel().setBlock(origin, Blocks.AIR.defaultBlockState(), 2);
            helper.getLevel().setBlock(collision, Blocks.STONE.defaultBlockState(), 2);
            helper.assertTrue(ConstructFlightManager.launch(owner, stoneBody(), Vec3.atCenterOf(origin), new Vec3(1.0D, 0.0D, 0.0D)),
                    "a funded captured-profile flight must launch toward the collision block");
            helper.assertTrue(ConstructFlightManager.activeSnapshotVersion(owner.getUUID()).isPresent(),
                    "the collision flight must expose a captured profile version");

            ConstructFlightManager.tickServer(helper.getLevel().getServer());

            helper.assertTrue(ConstructFlightManager.activeFlightCount() == 0,
                    "collision must discard the flight without a replacement entity or flight");
            helper.assertTrue(helper.getLevel().getBlockState(origin).isAir(), "collision must not place terrain at origin");
            helper.assertTrue(helper.getLevel().getBlockState(collision).is(Blocks.STONE), "collision must not mutate terrain");
        } finally {
            ConstructFlightManager.clearForTests();
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void capturedProfileFlightStopsBeforeUnloadedChunkWithoutTicketOrTerrainMutation(GameTestHelper helper) {
        ConstructFlightManager.clearForTests();
        try {
            ServerPlayer owner = fundedPlayer(helper, 1);
            BlockPos unloaded = firstUnloaded(helper, helper.absolutePos(new BlockPos(1, 2, 1)));
            BlockPos loaded = unloaded.west();
            while (!helper.getLevel().hasChunkAt(loaded)) {
                loaded = loaded.west();
            }
            unloaded = loaded.east();
            helper.getLevel().setBlock(loaded, Blocks.AIR.defaultBlockState(), 2);
            helper.assertFalse(helper.getLevel().hasChunkAt(unloaded), "fixture must have an unloaded target chunk");
            helper.assertTrue(ConstructFlightManager.launch(owner, stoneBody(), Vec3.atCenterOf(loaded), new Vec3(1.0D, 0.0D, 0.0D)),
                    "a funded captured-profile flight must launch from the loaded boundary");
            helper.assertTrue(ConstructFlightManager.activeSnapshotVersion(owner.getUUID()).isPresent(),
                    "the unloaded-boundary flight must expose a captured profile version");

            ConstructFlightManager.tickServer(helper.getLevel().getServer());
            ConstructFlightManager.tickServer(helper.getLevel().getServer());

            helper.assertTrue(ConstructFlightManager.activeFlightCount() == 0,
                    "flight must discard before entering unavailable terrain");
            helper.assertFalse(helper.getLevel().hasChunkAt(unloaded), "flight must not load or ticket the target chunk");
            helper.assertTrue(helper.getLevel().getBlockState(loaded).isAir(), "flight must not mutate loaded terrain");
        } finally {
            ConstructFlightManager.clearForTests();
        }
        helper.succeed();
    }

    private static ConstructBody stoneBody() {
        return ConstructBody.materialize("minecraft:stone", List.of(new VoxelCoordinate(0, 0, 0)));
    }

    private static ServerPlayer fundedPlayer(GameTestHelper helper, int stone) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.getAbilities().instabuild = false;
        player.getInventory().items.set(0, new ItemStack(Items.STONE, stone));
        return player;
    }

    private static BlockPos firstUnloaded(GameTestHelper helper, BlockPos start) {
        BlockPos candidate = start;
        while (helper.getLevel().hasChunkAt(candidate)) {
            candidate = candidate.east(16);
        }
        return candidate;
    }
}
