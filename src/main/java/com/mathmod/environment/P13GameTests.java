package com.mathmod.environment;

import com.mathmod.MathMod;
import com.mathmod.block.RuneAnchorBlockEntity;
import com.mathmod.program.ProgramExecutionResult;
import com.mathmod.program.ProgramExecutor;
import com.mathmod.program.ProgramPresets;
import com.mathmod.registry.ModBlocks;
import com.mathmod.field.FieldSamplingContext;
import com.mathmod.field.SamplePoint;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Runtime evidence that P13 remains a bounded anchor observation. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class P13GameTests {
    private P13GameTests() {
    }

    @GameTest(template = "empty")
    public static void dimensionalSurveyEmitsOnlyBoundedAnchorSignal(GameTestHelper helper) {
        BlockPos anchorPos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlock(anchorPos, ModBlocks.RUNE_ANCHOR.get().defaultBlockState(), 3);
        RuneAnchorBlockEntity anchor = (RuneAnchorBlockEntity) helper.getLevel().getBlockEntity(anchorPos);
        helper.assertTrue(anchor != null, "A P13 survey needs a real Rune Anchor block entity");
        helper.assertTrue(anchor.setProgram(ProgramPresets.dimensionalSurvey()), "The Dimensional Survey graph must inscribe");
        BlockState before = helper.getLevel().getBlockState(anchorPos);

        ProgramExecutionResult result = ProgramExecutor.executeFromAnchor(
                ProgramPresets.dimensionalSurvey(), helper.getLevel(), Vec3.atCenterOf(anchorPos)
        );

        helper.assertTrue(result.success(), "A loaded local P13 survey must execute: " + result.messageKey());
        helper.assertTrue(anchor.signalPower() >= 0 && anchor.signalPower() <= 15,
                "Dimensional Survey may only emit a bounded redstone signal");
        helper.assertTrue(anchor.environmentalReport().isPresent(),
                "Dimensional Survey must persist a player-safe report on its anchor");
        anchor.environmentalReport().ifPresent(report -> {
            helper.assertTrue(report.sampleCount() == 18, "Survey reports the fixed P5 sample count");
            helper.assertTrue(report.signal() >= 0 && report.signal() <= 15, "Survey report signal remains bounded");
            helper.assertTrue(report.intensities().size() == 3, "Survey report only names its three configured channels");
        });
        helper.assertTrue(helper.getLevel().getBlockState(anchorPos).equals(before),
                "Dimensional Survey must not replace or mutate terrain");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void capturedEnvironmentalContextKeepsItsGenerationAcrossPublication(GameTestHelper helper) {
        FieldSamplingContext captured = new FieldSamplingContext(
                helper.getLevel(), new SamplePoint(0.5D, 2.5D, 0.5D)
        );
        long before = captured.environmentalSession().generation();
        EnvironmentalFieldServices.publish(EnvironmentalFieldSnapshot.builtIns());

        helper.assertTrue(
                captured.environmentalSession().generation() == before,
                "An execution context must retain its environmental generation after publication"
        );
        helper.assertTrue(
                EnvironmentalFieldServices.snapshot().generation() > before,
                "A published candidate must become a distinct later generation"
        );
        helper.succeed();
    }
}
