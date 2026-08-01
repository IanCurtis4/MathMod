package com.mathmod.program;

import com.mathmod.MathMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** P12 runtime evidence for P8 fill atomicity, admission, and bounded construct flight. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class P8GameTests {
    private P8GameTests() {
    }

    @GameTest(template = "empty")
    public static void fillRollbackRestoresEscrowAfterCommitFailure(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.getInventory().items.set(0, new ItemStack(Items.STONE, 2));
        BlockPos first = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos second = helper.absolutePos(new BlockPos(2, 2, 1));
        helper.getLevel().setBlock(first, Blocks.AIR.defaultBlockState(), 2);
        helper.getLevel().setBlock(second, Blocks.AIR.defaultBlockState(), 2);
        SpatialRegion region = region(first, second);
        AtomicInteger attempts = new AtomicInteger();

        ConstructionFillService.Outcome outcome = ConstructionFillService.fill(
                helper.getLevel(), player, region, "minecraft:stone",
                (level, caster, position, witness) -> true,
                (level, position, state) -> attempts.incrementAndGet() == 1 && level.setBlock(position, state, 2)
        );

        helper.assertFalse(outcome.success(), "A failed fill commit must report failure");
        helper.assertTrue(helper.getLevel().getBlockState(first).isAir(), "Rollback must restore the first changed block");
        helper.assertTrue(helper.getLevel().getBlockState(second).isAir(), "The rejected block must remain unchanged");
        helper.assertTrue(count(player, Items.STONE) == 2, "Rollback must restore the exact block-item escrow");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fillAdmissionFailureConsumesNothing(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.getInventory().items.set(0, new ItemStack(Items.STONE, 2));
        BlockPos first = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos second = helper.absolutePos(new BlockPos(2, 2, 1));
        helper.getLevel().setBlock(first, Blocks.AIR.defaultBlockState(), 2);
        helper.getLevel().setBlock(second, Blocks.AIR.defaultBlockState(), 2);

        ConstructionFillService.Outcome outcome = ConstructionFillService.fill(
                helper.getLevel(), player, region(first, second), "minecraft:stone",
                (level, caster, position, witness) -> !position.equals(second),
                (level, position, state) -> level.setBlock(position, state, 2)
        );

        helper.assertFalse(outcome.success(), "A denied position must reject the whole fill before escrow");
        helper.assertTrue(helper.getLevel().getBlockState(first).isAir(), "Admission failure must not place any block");
        helper.assertTrue(helper.getLevel().getBlockState(second).isAir(), "Denied position must remain unchanged");
        helper.assertTrue(count(player, Items.STONE) == 2, "Admission failure must consume no material");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void constructBlockCollisionCapturesSnapshotAndDoesNotMutateTerrain(GameTestHelper helper) {
        ConstructFlightManager.clearForTests();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.getInventory().items.set(0, new ItemStack(Items.STONE));
        BlockPos origin = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlock(origin, Blocks.AIR.defaultBlockState(), 2);
        BlockPos collision = origin.east();
        helper.getLevel().setBlock(collision, Blocks.STONE.defaultBlockState(), 2);
        ConstructBody body = ConstructBody.materialize("minecraft:stone", List.of(new VoxelCoordinate(0, 0, 0)));
        helper.assertTrue(
                ConstructFlightManager.launch(player, body, Vec3.atCenterOf(origin), new Vec3(1.0D, 0.0D, 0.0D)),
                "A funded bounded construct flight must launch"
        );
        long capturedVersion = ConstructFlightManager.activeSnapshotVersion(player.getUUID()).orElse(-1L);
        helper.assertTrue(
                capturedVersion > 0L,
                "A flight must retain the physical snapshot resolved at launch"
        );

        ConstructFlightManager.tickServer(helper.getLevel().getServer());
        helper.assertTrue(ConstructFlightManager.activeFlightCount() == 0, "A construct must end at its bounded block collision");
        helper.assertTrue(helper.getLevel().getBlockState(origin).isAir(), "Construct collision must not mutate terrain");
        helper.assertTrue(helper.getLevel().getBlockState(collision).is(Blocks.STONE), "Construct collision must not break terrain");
        ConstructFlightManager.clearForTests();
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fillUnloadedCandidateFailsClosedWithoutLoadingOrConsumption(GameTestHelper helper) {
        ServerPlayer player = fundedPlayer(helper, 1);
        BlockPos unloaded = firstUnloaded(helper, helper.absolutePos(new BlockPos(1, 2, 1)));
        int beforeItems = count(player, Items.STONE);
        helper.assertFalse(helper.getLevel().hasChunkAt(unloaded), "fixture must begin with an unloaded candidate");
        ConstructionFillService.Outcome outcome = ConstructionFillService.fill(helper.getLevel(), player, region(unloaded, unloaded), "minecraft:stone");
        helper.assertFalse(outcome.success(), "an unloaded candidate must reject the whole fill");
        helper.assertFalse(helper.getLevel().hasChunkAt(unloaded), "fill must not load or ticket the rejected candidate chunk");
        helper.assertTrue(count(player, Items.STONE) == beforeItems, "unloaded rejection must consume no material");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fillFluidCandidateFailsClosedWithoutMutationOrConsumption(GameTestHelper helper) {
        ServerPlayer player = fundedPlayer(helper, 1); BlockPos position = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlock(position, Blocks.WATER.defaultBlockState(), 2); var before = helper.getLevel().getBlockState(position); int beforeItems = count(player, Items.STONE);
        ConstructionFillService.Outcome outcome = ConstructionFillService.fill(helper.getLevel(), player, region(position, position), "minecraft:stone");
        helper.assertFalse(outcome.success(), "fluid candidate must reject"); helper.assertTrue(helper.getLevel().getBlockState(position).equals(before), "fluid must not be replaced");
        helper.assertTrue(count(player, Items.STONE) == beforeItems, "fluid rejection must consume no material"); helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fillBlockEntityCandidateFailsClosedWithoutMutationOrConsumption(GameTestHelper helper) {
        ServerPlayer player = fundedPlayer(helper, 1); BlockPos position = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlock(position, Blocks.CHEST.defaultBlockState(), 2); BlockEntity beforeEntity = helper.getLevel().getBlockEntity(position); CompoundTag beforeData = beforeEntity.saveWithFullMetadata(helper.getLevel().registryAccess()); int beforeItems = count(player, Items.STONE);
        ConstructionFillService.Outcome outcome = ConstructionFillService.fill(helper.getLevel(), player, region(position, position), "minecraft:stone");
        helper.assertFalse(outcome.success(), "block-entity candidate must reject"); helper.assertTrue(helper.getLevel().getBlockState(position).is(Blocks.CHEST), "block entity block must remain");
        helper.assertTrue(beforeData.equals(helper.getLevel().getBlockEntity(position).saveWithFullMetadata(helper.getLevel().registryAccess())), "block entity data must remain exact");
        helper.assertTrue(count(player, Items.STONE) == beforeItems, "block entity rejection must consume no material"); helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fillProtectionDenialNeverCommitsOrConsumes(GameTestHelper helper) {
        ServerPlayer player = fundedPlayer(helper, 1); BlockPos position = helper.absolutePos(new BlockPos(1, 2, 1)); helper.getLevel().setBlock(position, Blocks.AIR.defaultBlockState(), 2);
        int beforeItems = count(player, Items.STONE); AtomicInteger commits = new AtomicInteger();
        ConstructionFillService.Outcome outcome = ConstructionFillService.fill(helper.getLevel(), player, region(position, position), "minecraft:stone", (level, caster, candidate, witness) -> false,
                (level, candidate, state) -> { commits.incrementAndGet(); return level.setBlock(candidate, state, 2); });
        helper.assertFalse(outcome.success(), "protection denial must reject before escrow"); helper.assertTrue(commits.get() == 0, "protection denial must never reach commit callback");
        helper.assertTrue(helper.getLevel().getBlockState(position).isAir(), "protection denial must not mutate world"); helper.assertTrue(count(player, Items.STONE) == beforeItems, "protection denial must consume no material"); helper.succeed();
    }

    @GameTest(template = "empty")
    public static void constructUnloadedFlightStopsWithoutTicketOrTerrainMutation(GameTestHelper helper) {
        ConstructFlightManager.clearForTests(); ServerPlayer player = fundedPlayer(helper, 1); BlockPos unloaded = firstUnloaded(helper, helper.absolutePos(new BlockPos(1, 2, 1))); BlockPos loaded = unloaded.west();
        while (!helper.getLevel().hasChunkAt(loaded)) loaded = loaded.west(); unloaded = loaded.east();
        helper.getLevel().setBlock(loaded, Blocks.AIR.defaultBlockState(), 2); helper.assertFalse(helper.getLevel().hasChunkAt(unloaded), "fixture must find an unloaded neighbor");
        ConstructBody body = ConstructBody.materialize("minecraft:stone", List.of(new VoxelCoordinate(0, 0, 0)));
        helper.assertTrue(ConstructFlightManager.launch(player, body, Vec3.atCenterOf(loaded), new Vec3(1.0D, 0.0D, 0.0D)), "funded flight must launch from the loaded boundary");
        ConstructFlightManager.tickServer(helper.getLevel().getServer()); ConstructFlightManager.tickServer(helper.getLevel().getServer());
        helper.assertTrue(ConstructFlightManager.activeFlightCount() == 0, "flight approaching unloaded terrain must be discarded"); helper.assertFalse(helper.getLevel().hasChunkAt(unloaded), "flight must not load or ticket the next chunk");
        helper.assertTrue(helper.getLevel().getBlockState(loaded).isAir(), "flight must not mutate terrain"); ConstructFlightManager.clearForTests(); helper.succeed();
    }

    @GameTest(template = "empty")
    public static void constructSecondLaunchForSameOwnerFailsClosed(GameTestHelper helper) {
        ConstructFlightManager.clearForTests(); ServerPlayer player = fundedPlayer(helper, 2); ConstructBody body = ConstructBody.materialize("minecraft:stone", List.of(new VoxelCoordinate(0, 0, 0)));
        helper.assertTrue(ConstructFlightManager.launch(player, body, player.position(), new Vec3(1.0D, 0.0D, 0.0D)), "first funded flight must launch");
        long firstVersion = ConstructFlightManager.activeSnapshotVersion(player.getUUID()).orElseThrow(); int afterFirst = count(player, Items.STONE);
        helper.assertFalse(ConstructFlightManager.launch(player, body, player.position(), new Vec3(1.0D, 0.0D, 0.0D)), "second owner flight must fail closed");
        helper.assertTrue(ConstructFlightManager.activeFlightCount() == 1 && ConstructFlightManager.activeSnapshotVersion(player.getUUID()).orElseThrow() == firstVersion, "rejected second launch must not replace first flight");
        helper.assertTrue(count(player, Items.STONE) == afterFirst, "rejected second launch must consume nothing"); ConstructFlightManager.clearForTests(); helper.succeed();
    }

    @GameTest(template = "empty")
    public static void constructRejectsUnboundedMotionAndUsesServerDerivedBodyCost(GameTestHelper helper) {
        ConstructFlightManager.clearForTests();
        try {
            ServerPlayer player = fundedPlayer(helper, 2); ConstructBody body = ConstructBody.materialize("minecraft:stone", List.of(new VoxelCoordinate(0, 0, 0), new VoxelCoordinate(1, 0, 0))); int before = count(player, Items.STONE);
            for (Vec3 rejected : List.of(new Vec3(Double.NaN, 0.0D, 0.0D), Vec3.ZERO, new Vec3(2.01D, 0.0D, 0.0D))) {
                helper.assertFalse(ConstructFlightManager.launch(player, body, player.position(), rejected), "non-finite, zero, or over-limit motion must reject: " + rejected);
                helper.assertTrue(count(player, Items.STONE) == before && ConstructFlightManager.activeFlightCount() == 0, "rejected motion must not consume or create flight");
            }
            helper.assertTrue(ConstructFlightManager.launch(player, body, player.position(), new Vec3(1.0D, 0.0D, 0.0D)), "bounded motion must launch");
            helper.assertTrue(count(player, Items.STONE) == before - body.massEquivalent(), "payment must equal server-owned ConstructBody.massEquivalent"); helper.succeed();
        } finally { ConstructFlightManager.clearForTests(); }
    }

    private static SpatialRegion region(BlockPos first, BlockPos second) {
        return new BoxSpatialRegion(Vec3.atCenterOf(first), Vec3.atCenterOf(second));
    }

    private static int count(ServerPlayer player, net.minecraft.world.item.Item item) {
        return player.getInventory().items.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum()
                + player.getInventory().offhand.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
    }
    private static ServerPlayer fundedPlayer(GameTestHelper helper, int stone) { ServerPlayer player = helper.makeMockServerPlayerInLevel(); player.getAbilities().instabuild = false; player.getInventory().items.set(0, new ItemStack(Items.STONE, stone)); return player; }
    private static BlockPos firstUnloaded(GameTestHelper helper, BlockPos start) { BlockPos candidate = start; while (helper.getLevel().hasChunkAt(candidate)) candidate = candidate.east(16); return candidate; }
}
