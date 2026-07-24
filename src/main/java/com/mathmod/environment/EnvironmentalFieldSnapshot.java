package com.mathmod.environment;

import com.mathmod.util.NamespacedId;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Immutable P13 static-field data. Runtime biome lookup is supplied as an input, never retained. */
public final class EnvironmentalFieldSnapshot {
    private final long generation;
    private final Map<NamespacedId, EnvironmentalChannel> channels;
    private final Map<String, Map<NamespacedId, Double>> dimensionBases;
    private final Map<String, Map<NamespacedId, Double>> biomeOverrides;
    private final Map<String, Map<NamespacedId, Double>> biomeAdditives;
    private final Map<String, Map<NamespacedId, EnvironmentalHeightCurve>> heightCurves;

    public EnvironmentalFieldSnapshot(
            long generation,
            List<EnvironmentalChannel> channels,
            Map<String, Map<NamespacedId, Double>> dimensionBases,
            Map<String, Map<NamespacedId, Double>> biomeOverrides,
            Map<String, Map<NamespacedId, Double>> biomeAdditives,
            Map<String, Map<NamespacedId, EnvironmentalHeightCurve>> heightCurves
    ) {
        if (generation < 0L || channels.isEmpty() || channels.size() > 32) {
            throw new IllegalArgumentException("Snapshot generation and channel count are invalid");
        }
        this.generation = generation;
        this.channels = indexChannels(channels);
        this.dimensionBases = freezeNumbers(dimensionBases);
        this.biomeOverrides = freezeNumbers(biomeOverrides);
        this.biomeAdditives = freezeNumbers(biomeAdditives);
        this.heightCurves = freezeCurves(heightCurves);
        validateReferences();
    }

    public long generation() { return generation; }

    public List<EnvironmentalChannel> channels() {
        return channels.values().stream().sorted(Comparator.comparing(EnvironmentalChannel::id)).toList();
    }

    public Optional<EnvironmentalChannel> channel(NamespacedId id) {
        return Optional.ofNullable(channels.get(id));
    }

    Map<String, Map<NamespacedId, Double>> dimensionBasesData() { return dimensionBases; }

    Map<String, Map<NamespacedId, Double>> biomeOverridesData() { return biomeOverrides; }

    Map<String, Map<NamespacedId, Double>> biomeAdditivesData() { return biomeAdditives; }

    Map<String, Map<NamespacedId, EnvironmentalHeightCurve>> heightCurvesData() { return heightCurves; }

    public EnvironmentalFieldSnapshot withGeneration(long nextGeneration) {
        return new EnvironmentalFieldSnapshot(
                nextGeneration,
                channels(),
                dimensionBases,
                biomeOverrides,
                biomeAdditives,
                heightCurves
        );
    }

    public double sample(NamespacedId channelId, EnvironmentalSampleInput input, byte[] secret) {
        EnvironmentalChannel channel = channel(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown environmental channel " + channelId));
        double base = value(dimensionBases, input.dimensionId(), channelId);
        double override = value(biomeOverrides, input.biomeId(), channelId);
        double additive = value(biomeAdditives, input.biomeId(), channelId);
        double height = curve(input.dimensionId(), channelId)
                .map(curve -> curve.valueAt(input.normalizedHeight()))
                .orElse(0.0D);
        double noise = channel.noiseAmplitude() * SaltedValueNoise.sample(
                secret, input.worldSeed(), input.dimensionId(), channelId, input.point(), channel.noiseScale()
        );
        return channel.clamp(base + override + additive + height + noise);
    }

    public static EnvironmentalFieldSnapshot builtIns() {
        NamespacedId spatial = NamespacedId.of("mathmod", "spatial");
        NamespacedId stability = NamespacedId.of("mathmod", "stability");
        NamespacedId vitality = NamespacedId.of("mathmod", "vitality");
        return new EnvironmentalFieldSnapshot(0L,
                List.of(
                        new EnvironmentalChannel(spatial, -16, 16, 0.75D, 32, 0.35D),
                        new EnvironmentalChannel(stability, -16, 16, 0.50D, 64, 0.30D),
                        new EnvironmentalChannel(vitality, -16, 16, 0.40D, 32, 0.25D)
                ),
                Map.of(
                        "minecraft:overworld", Map.of(spatial, 0.20D, stability, 0.35D, vitality, 0.25D),
                        "minecraft:the_nether", Map.of(spatial, -0.30D, stability, -0.45D, vitality, -0.20D),
                        "minecraft:the_end", Map.of(spatial, 0.55D, stability, 0.10D, vitality, -0.35D)
                ),
                Map.of("minecraft:plains", Map.of(vitality, 0.25D), "minecraft:desert", Map.of(stability, -0.20D)),
                Map.of("minecraft:forest", Map.of(vitality, 0.10D), "minecraft:dripstone_caves", Map.of(stability, 0.12D)),
                Map.of("minecraft:overworld", Map.of(
                        spatial, new EnvironmentalHeightCurve(List.of(
                                new EnvironmentalHeightCurve.Point(0.0D, -0.15D),
                                new EnvironmentalHeightCurve.Point(0.5D, 0.0D),
                                new EnvironmentalHeightCurve.Point(1.0D, 0.20D)
                        )),
                        stability, new EnvironmentalHeightCurve(List.of(
                                new EnvironmentalHeightCurve.Point(0.0D, 0.10D),
                                new EnvironmentalHeightCurve.Point(1.0D, -0.10D)
                        ))
                ))
        );
    }

    private static Map<NamespacedId, EnvironmentalChannel> indexChannels(List<EnvironmentalChannel> definitions) {
        Map<NamespacedId, EnvironmentalChannel> indexed = new LinkedHashMap<>();
        for (EnvironmentalChannel definition : definitions) {
            Objects.requireNonNull(definition, "channel");
            if (indexed.putIfAbsent(definition.id(), definition) != null) {
                throw new IllegalArgumentException("Duplicate environmental channel " + definition.id());
            }
        }
        return Map.copyOf(indexed);
    }

    private static Map<String, Map<NamespacedId, Double>> freezeNumbers(Map<String, Map<NamespacedId, Double>> source) {
        Map<String, Map<NamespacedId, Double>> frozen = new LinkedHashMap<>();
        for (Map.Entry<String, Map<NamespacedId, Double>> entry : source.entrySet()) {
            Map<NamespacedId, Double> values = new LinkedHashMap<>();
            for (Map.Entry<NamespacedId, Double> value : entry.getValue().entrySet()) {
                if (!Double.isFinite(value.getValue()) || Math.abs(value.getValue()) > 16.0D) {
                    throw new IllegalArgumentException("Environmental contribution must be finite and bounded");
                }
                values.put(Objects.requireNonNull(value.getKey(), "channel"), value.getValue());
            }
            frozen.put(requireId(entry.getKey()), Map.copyOf(values));
        }
        return Map.copyOf(frozen);
    }

    private static Map<String, Map<NamespacedId, EnvironmentalHeightCurve>> freezeCurves(
            Map<String, Map<NamespacedId, EnvironmentalHeightCurve>> source
    ) {
        Map<String, Map<NamespacedId, EnvironmentalHeightCurve>> frozen = new LinkedHashMap<>();
        for (Map.Entry<String, Map<NamespacedId, EnvironmentalHeightCurve>> entry : source.entrySet()) {
            frozen.put(requireId(entry.getKey()), Map.copyOf(entry.getValue()));
        }
        return Map.copyOf(frozen);
    }

    private void validateReferences() {
        List<Map<String, Map<NamespacedId, ?>>> maps = List.of(
                cast(dimensionBases), cast(biomeOverrides), cast(biomeAdditives), cast(heightCurves)
        );
        for (Map<String, Map<NamespacedId, ?>> map : maps) {
            for (Map<NamespacedId, ?> values : map.values()) {
                for (NamespacedId channel : values.keySet()) {
                    if (!channels.containsKey(channel)) {
                        throw new IllegalArgumentException("Unknown channel reference " + channel);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<NamespacedId, ?>> cast(Map<?, ?> value) {
        return (Map<String, Map<NamespacedId, ?>>) value;
    }

    private static String requireId(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Environmental selector must not be blank");
        return value.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private static double value(Map<String, Map<NamespacedId, Double>> values, String selector, NamespacedId channel) {
        return values.getOrDefault(selector, Map.of()).getOrDefault(channel, 0.0D);
    }

    private Optional<EnvironmentalHeightCurve> curve(String dimension, NamespacedId channel) {
        return Optional.ofNullable(heightCurves.getOrDefault(dimension, Map.of()).get(channel));
    }
}
