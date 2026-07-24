package com.mathmod.block;

import com.mathmod.program.AnchorProgramPreset;
import com.mathmod.program.ProgramExecutionResult;
import com.mathmod.program.ProgramExecutor;
import com.mathmod.program.ProgramStorage;
import com.mathmod.environment.EnvironmentalSampleReport;
import com.mathmod.registry.ModBlockEntities;
import com.mathmod.runes.ProgramGraph;
import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class RuneAnchorBlockEntity extends BlockEntity {
    private static final String PROGRAM_TAG = "program";
    private static final String PROGRAM_PRESET_TAG = "program_preset";
    private static final String SIGNAL_POWER_TAG = "signal_power";
    private static final String SIGNAL_EXPIRES_AT_TAG = "signal_expires_at";
    private static final String ENVIRONMENTAL_REPORT_TAG = "environmental_report";

    private ProgramGraph program;
    private String programPresetId = "";
    private TimedRedstoneSignal signal = TimedRedstoneSignal.off();
    private EnvironmentalSampleReport environmentalReport;

    public RuneAnchorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.RUNE_ANCHOR.get(), pos, blockState);
    }

    public Optional<ProgramGraph> program() {
        return Optional.ofNullable(program);
    }

    public Optional<AnchorProgramPreset> programPreset() {
        Optional<AnchorProgramPreset> savedPreset = AnchorProgramPreset.fromId(programPresetId);
        if (savedPreset.isPresent()) {
            return savedPreset;
        }
        return AnchorProgramPreset.infer(program);
    }

    public boolean hasProgram() {
        return program != null;
    }

    public Optional<EnvironmentalSampleReport> environmentalReport() { return Optional.ofNullable(environmentalReport); }

    public void setEnvironmentalReport(EnvironmentalSampleReport report) {
        environmentalReport = report;
        setChanged();
    }

    public boolean setProgram(ProgramGraph program) {
        return setProgram(program, "");
    }

    public boolean setProgram(AnchorProgramPreset preset) {
        return setProgram(preset.graph(), preset.id());
    }

    public boolean setProgram(ProgramGraph program, String presetId) {
        if (!ProgramStorage.validateExecutable(program).valid()) {
            return false;
        }
        if (level instanceof ServerLevel serverLevel && signal.power() > 0) {
            setSignal(serverLevel, TimedRedstoneSignal.off());
        }
        this.program = program;
        this.programPresetId = presetId == null ? "" : presetId;
        this.environmentalReport = null;
        setChanged();
        return true;
    }

    public boolean clearProgram() {
        if (program == null) {
            return false;
        }
        program = null;
        programPresetId = "";
        environmentalReport = null;
        if (level instanceof ServerLevel serverLevel) {
            setSignal(serverLevel, TimedRedstoneSignal.off());
        }
        setChanged();
        return true;
    }

    public int signalPower() {
        return level == null ? signal.power() : signal.powerAt(level.getGameTime());
    }

    public long signalRemainingTicks() {
        return level == null ? 0L : signal.remainingTicks(level.getGameTime());
    }

    public void activateSignal(ServerLevel level, int power, int durationTicks) {
        setSignal(level, TimedRedstoneSignal.activate(level.getGameTime(), power, durationTicks));
    }

    public void refreshSignal(ServerLevel level) {
        long remaining = signal.remainingTicks(level.getGameTime());
        if (remaining <= 0L) {
            if (signal.power() != 0) {
                setSignal(level, TimedRedstoneSignal.off());
            }
            return;
        }
        level.scheduleTick(worldPosition, getBlockState().getBlock(), (int) Math.min(Integer.MAX_VALUE, remaining));
    }

    private void setSignal(ServerLevel level, TimedRedstoneSignal nextSignal) {
        boolean staleStoredSignal = signal.power() > 0 && signal.powerAt(level.getGameTime()) == 0;
        int previousPower = signal.powerAt(level.getGameTime());
        signal = nextSignal;
        int nextPower = signal.powerAt(level.getGameTime());
        setChanged();
        if (previousPower != nextPower || staleStoredSignal) {
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        refreshSignal(level);
    }

    public ProgramExecutionResult execute(ServerLevel level) {
        if (program == null) {
            return ProgramExecutionResult.failure("block.mathmod.rune_anchor.empty");
        }
        Vec3 origin = Vec3.atCenterOf(worldPosition);
        return ProgramExecutor.executeFromAnchor(program, level, origin);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        program = null;
        programPresetId = "";
        signal = TimedRedstoneSignal.off();
        environmentalReport = null;
        if (tag.contains(PROGRAM_TAG)) {
            DataResult<ProgramGraph> result = ProgramGraph.CODEC.parse(NbtOps.INSTANCE, tag.get(PROGRAM_TAG));
            program = result.result().orElse(null);
        }
        if (tag.contains(PROGRAM_PRESET_TAG)) {
            programPresetId = tag.getString(PROGRAM_PRESET_TAG);
        }
        if (program != null && programPresetId.isBlank()) {
            programPresetId = AnchorProgramPreset.infer(program)
                    .map(AnchorProgramPreset::id)
                    .orElse("");
        }
        if (tag.contains(SIGNAL_POWER_TAG) && tag.contains(SIGNAL_EXPIRES_AT_TAG)) {
            int power = Math.max(0, Math.min(15, tag.getInt(SIGNAL_POWER_TAG)));
            long expiresAt = Math.max(0L, tag.getLong(SIGNAL_EXPIRES_AT_TAG));
            signal = new TimedRedstoneSignal(power, expiresAt);
        }
        if (tag.contains(ENVIRONMENTAL_REPORT_TAG)) {
            environmentalReport = EnvironmentalSampleReport.load(tag.getCompound(ENVIRONMENTAL_REPORT_TAG)).orElse(null);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (program != null) {
            ProgramGraph.CODEC.encodeStart(NbtOps.INSTANCE, program)
                    .result()
                    .ifPresent(encoded -> tag.put(PROGRAM_TAG, encoded));
        }
        if (!programPresetId.isBlank()) {
            tag.putString(PROGRAM_PRESET_TAG, programPresetId);
        }
        if (signal.power() > 0) {
            tag.putInt(SIGNAL_POWER_TAG, signal.power());
            tag.putLong(SIGNAL_EXPIRES_AT_TAG, signal.expiresAt());
        }
        if (environmentalReport != null) {
            tag.put(ENVIRONMENTAL_REPORT_TAG, environmentalReport.save());
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            refreshSignal(serverLevel);
        }
    }
}
