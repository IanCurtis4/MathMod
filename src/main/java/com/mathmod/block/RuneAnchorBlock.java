package com.mathmod.block;

import com.mathmod.item.ChalkPresetStorage;
import com.mathmod.program.AnchorProgramPreset;
import com.mathmod.program.ProgramExecutionResult;
import com.mathmod.program.ProgramMessageComponents;
import com.mathmod.environment.EnvironmentalSampleReport;
import com.mathmod.util.NamespacedId;
import com.mathmod.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RuneAnchorBlock extends BaseEntityBlock {
    public static final MapCodec<RuneAnchorBlock> CODEC = BlockBehaviour.simpleCodec(RuneAnchorBlock::new);

    public RuneAnchorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof RuneAnchorBlockEntity anchor
                ? anchor.signalPower()
                : 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof RuneAnchorBlockEntity anchor
                ? anchor.signalPower()
                : 0;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof RuneAnchorBlockEntity anchor) {
            anchor.refreshSignal(level);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RuneAnchorBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!stack.is(ModItems.CHALK.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof RuneAnchorBlockEntity anchor) {
            if (player.isSecondaryUseActive()) {
                String messageKey = anchor.clearProgram()
                        ? "item.mathmod.chalk.anchor_cleared"
                        : "item.mathmod.chalk.anchor_clear_empty";
                player.displayClientMessage(Component.translatable(messageKey), true);
            } else {
                AnchorProgramPreset preset = ChalkPresetStorage.get(stack);
                if (anchor.setProgram(preset)) {
                    player.displayClientMessage(Component.translatable(preset.saveMessageKey()), true);
                } else {
                    player.displayClientMessage(Component.translatable("item.mathmod.chalk.anchor_invalid"), true);
                }
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            if (level.getBlockEntity(pos) instanceof RuneAnchorBlockEntity anchor) {
                if (player.isSecondaryUseActive()) {
                    displayAnchorStatus(serverPlayer, anchor);
                } else {
                    ProgramExecutionResult result = anchor.execute(serverLevel);
                    serverPlayer.displayClientMessage(
                            ProgramMessageComponents.executionResult(result),
                            true
                    );
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void displayAnchorStatus(ServerPlayer player, RuneAnchorBlockEntity anchor) {
        if (!anchor.hasProgram()) {
            player.displayClientMessage(Component.translatable("block.mathmod.rune_anchor.status_empty"), true);
            return;
        }

        Component presetName = anchor.programPreset()
                .map(preset -> Component.translatable(preset.displayNameKey()))
                .orElse(Component.translatable("block.mathmod.rune_anchor.preset.unknown"));
        int power = anchor.signalPower();
        if (power > 0) {
            long seconds = Math.max(1L, (anchor.signalRemainingTicks() + 19L) / 20L);
            player.displayClientMessage(Component.translatable(
                    "block.mathmod.rune_anchor.status_signal",
                    presetName,
                    power,
                    seconds
            ), true);
        } else {
            player.displayClientMessage(Component.translatable("block.mathmod.rune_anchor.status", presetName), true);
        }
        anchor.environmentalReport().ifPresent(report -> displayEnvironmentalReport(player, report));
    }

    private static void displayEnvironmentalReport(ServerPlayer player, EnvironmentalSampleReport report) {
        player.sendSystemMessage(Component.translatable(
                "block.mathmod.rune_anchor.environment_report.header",
                report.generation(), report.sampleCount(), report.signal(),
                channelName(report.dominantChannel())
        ));
        report.intensities().entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).forEach(entry ->
                player.sendSystemMessage(Component.translatable(
                        "block.mathmod.rune_anchor.environment_report.channel",
                        channelName(entry.getKey()),
                        Component.translatable("environment.mathmod.intensity." + entry.getValue().serializedName())
                ))
        );
    }

    private static Component channelName(NamespacedId channel) {
        return switch (channel.toString()) {
            case "mathmod:spatial", "mathmod:stability", "mathmod:vitality" ->
                    Component.translatable("environment.mathmod.channel." + channel.path());
            default -> Component.literal(channel.toString());
        };
    }
}
