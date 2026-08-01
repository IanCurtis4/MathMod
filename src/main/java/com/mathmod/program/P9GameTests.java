package com.mathmod.program;

import com.mathmod.MathMod;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import com.mathmod.registry.ModItems;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

/** Dedicated-server regression guard for the P9 anchor boundary. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class P9GameTests {
    private P9GameTests() {
    }

    @GameTest(template = "empty")
    public static void defensiveAlchemyRejectsAnchorExecution(GameTestHelper helper) {
        ProgramExecutionResult result = ProgramExecutor.executeFromAnchor(
                ProgramPresets.resistanceLemma(),
                helper.getLevel(),
                Vec3.ZERO
        );

        helper.assertFalse(result.success(), "P9 defensive effects must not execute from a free anchor");
        helper.assertTrue(
                result.messageKey().equals("block.mathmod.rune_anchor.execute_p9_player_only"),
                "P9 anchor rejection must preserve the player-only policy"
        );
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void missingDefensiveWitnessesLeavePlayerUnchanged(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.getAbilities().instabuild = false;
        ProgramExecutionResult result = ProgramExecutor.execute(ProgramPresets.resistanceLemma(), player);

        helper.assertFalse(result.success(), "A defensive cast without its required witnesses must fail before mutation");
        helper.assertFalse(player.hasEffect(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE),
                "A failed defensive cast must not apply Resistance");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void defensiveSelfCastConsumesEscrowAndBoundedlyRejectsRefresh(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        NetworkRegistry.configureMockConnection(player.connection.getConnection());
        player.getAbilities().instabuild = false;
        player.getInventory().items.set(0, new ItemStack(ModItems.VITAL_SALT.get(), 2));
        player.getInventory().items.set(1, new ItemStack(ModItems.HOMUNCULAR_MATRIX.get()));
        int saltBefore = count(player, ModItems.VITAL_SALT.get());
        int catalystBefore = count(player, ModItems.HOMUNCULAR_MATRIX.get());

        ProgramExecutionResult first = ProgramExecutor.execute(ProgramPresets.resistanceLemma(), player);
        helper.assertTrue(first.success(), "a funded self defensive cast must execute through the real escrow path");
        var effect = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
        helper.assertTrue(effect != null && effect.getDuration() <= 600 && effect.getAmplifier() <= 1,
                "the applied defensive effect must stay within the frozen duration and strength bounds");
        helper.assertTrue(count(player, ModItems.VITAL_SALT.get()) == saltBefore - 1,
                "the committed defensive cast must consume exactly one Vital Salt");
        helper.assertTrue(count(player, ModItems.HOMUNCULAR_MATRIX.get()) == catalystBefore,
                "the Homuncular Matrix must remain a retained catalyst");

        int durationBeforeRefresh = effect.getDuration();
        int amplifierBeforeRefresh = effect.getAmplifier();
        int saltBeforeRefresh = count(player, ModItems.VITAL_SALT.get());
        ProgramExecutionResult repeated = ProgramExecutor.execute(ProgramPresets.resistanceLemma(), player);
        helper.assertFalse(repeated.success(), "an equal active defensive effect must fail rather than accumulate");
        var afterRefresh = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
        helper.assertTrue(afterRefresh != null && afterRefresh.getDuration() == durationBeforeRefresh
                        && afterRefresh.getAmplifier() == amplifierBeforeRefresh,
                "a rejected refresh must neither downgrade nor extend the active effect");
        helper.assertTrue(count(player, ModItems.VITAL_SALT.get()) == saltBeforeRefresh,
                "a rejected refresh must restore its escrow exactly");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void defensiveAlchemyRestoresEscrowForDeadSelfTarget(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        NetworkRegistry.configureMockConnection(player.connection.getConnection());
        player.getAbilities().instabuild = false;
        player.getInventory().items.set(0, new ItemStack(ModItems.VITAL_SALT.get(), 1));
        player.getInventory().items.set(1, new ItemStack(ModItems.HOMUNCULAR_MATRIX.get()));
        int saltBefore = count(player, ModItems.VITAL_SALT.get());
        int catalystBefore = count(player, ModItems.HOMUNCULAR_MATRIX.get());

        player.setHealth(0.0F);
        helper.assertFalse(player.isAlive(), "the self target must be dead when defensive execution resolves it");
        ProgramExecutionResult result = ProgramExecutor.execute(ProgramPresets.resistanceLemma(), player);

        helper.assertFalse(result.success(), "a dead defensive self target must be rejected by the liveness recheck");
        helper.assertFalse(player.hasEffect(MobEffects.DAMAGE_RESISTANCE),
                "a rejected dead target must not receive the defensive effect");
        helper.assertTrue(count(player, ModItems.VITAL_SALT.get()) == saltBefore,
                "a dead target rejection after escrow must restore Vital Salt exactly");
        helper.assertTrue(count(player, ModItems.HOMUNCULAR_MATRIX.get()) == catalystBefore,
                "a dead target rejection must retain the catalyst exactly");
        helper.succeed();
    }

    private static int count(ServerPlayer player, net.minecraft.world.item.Item item) {
        return player.getInventory().items.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum()
                + player.getInventory().offhand.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
    }
}
