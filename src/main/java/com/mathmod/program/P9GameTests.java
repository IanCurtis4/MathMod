package com.mathmod.program;

import com.mathmod.MathMod;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

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
}
