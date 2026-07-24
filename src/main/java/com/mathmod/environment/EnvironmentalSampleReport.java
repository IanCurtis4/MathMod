package com.mathmod.environment;

import com.mathmod.util.NamespacedId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Persisted, player-safe P13 observation. It deliberately contains no raw samples or noise data. */
public record EnvironmentalSampleReport(
        long generation,
        int sampleCount,
        int signal,
        NamespacedId dominantChannel,
        Map<NamespacedId, Intensity> intensities
) {
    private static final String GENERATION = "generation";
    private static final String SAMPLE_COUNT = "sample_count";
    private static final String SIGNAL = "signal";
    private static final String DOMINANT = "dominant";
    private static final String CHANNELS = "channels";
    private static final String CHANNEL = "channel";
    private static final String INTENSITY = "intensity";

    public EnvironmentalSampleReport {
        if (generation < 0L || sampleCount < 1 || sampleCount > 64 || signal < 0 || signal > 15) {
            throw new IllegalArgumentException("Environmental report bounds are invalid");
        }
        dominantChannel = Objects.requireNonNull(dominantChannel, "dominantChannel");
        intensities = Map.copyOf(new LinkedHashMap<>(intensities));
        if (intensities.isEmpty() || intensities.size() > 32 || !intensities.containsKey(dominantChannel)) {
            throw new IllegalArgumentException("Environmental report channels are invalid");
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong(GENERATION, generation);
        tag.putInt(SAMPLE_COUNT, sampleCount);
        tag.putInt(SIGNAL, signal);
        tag.putString(DOMINANT, dominantChannel.toString());
        ListTag channels = new ListTag();
        intensities.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.naturalOrder())).forEach(entry -> {
            CompoundTag channel = new CompoundTag();
            channel.putString(CHANNEL, entry.getKey().toString());
            channel.putString(INTENSITY, entry.getValue().serializedName());
            channels.add(channel);
        });
        tag.put(CHANNELS, channels);
        return tag;
    }

    public static Optional<EnvironmentalSampleReport> load(CompoundTag tag) {
        try {
            Map<NamespacedId, Intensity> readings = new LinkedHashMap<>();
            for (int index = 0; index < tag.getList(CHANNELS, CompoundTag.TAG_COMPOUND).size(); index++) {
                CompoundTag entry = tag.getList(CHANNELS, CompoundTag.TAG_COMPOUND).getCompound(index);
                readings.put(NamespacedId.parse(entry.getString(CHANNEL)), Intensity.parse(entry.getString(INTENSITY)));
            }
            return Optional.of(new EnvironmentalSampleReport(
                    tag.getLong(GENERATION), tag.getInt(SAMPLE_COUNT), tag.getInt(SIGNAL),
                    NamespacedId.parse(tag.getString(DOMINANT)), readings
            ));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    public enum Intensity {
        QUIET("quiet"),
        VARIABLE("variable"),
        INTENSE("intense");

        private final String serializedName;

        Intensity(String serializedName) { this.serializedName = serializedName; }

        public String serializedName() { return serializedName; }

        public static Intensity fromNormalized(double normalized) {
            if (!Double.isFinite(normalized) || normalized < 0.0D) throw new IllegalArgumentException("Invalid normalized intensity");
            return normalized < 1.0D / 3.0D ? QUIET : normalized < 2.0D / 3.0D ? VARIABLE : INTENSE;
        }

        static Intensity parse(String value) {
            for (Intensity intensity : values()) if (intensity.serializedName.equals(value)) return intensity;
            throw new IllegalArgumentException("Unknown report intensity");
        }
    }
}
