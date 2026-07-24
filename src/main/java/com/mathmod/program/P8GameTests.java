package com.mathmod.program;

import com.mathmod.MathMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
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

    private static SpatialRegion region(BlockPos first, BlockPos second) {
        return new BoxSpatialRegion(Vec3.atCenterOf(first), Vec3.atCenterOf(second));
    }

    private static int count(ServerPlayer player, net.minecraft.world.item.Item item) {
        return player.getInventory().items.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum()
                + player.getInventory().offhand.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
    }
}
