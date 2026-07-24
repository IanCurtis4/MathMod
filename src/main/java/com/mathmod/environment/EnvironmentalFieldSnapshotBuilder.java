package com.mathmod.environment;

import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Mutable reload-only builder; published snapshots remain immutable. */
public final class EnvironmentalFieldSnapshotBuilder {
    private final Map<NamespacedId, EnvironmentalChannel> channels = new LinkedHashMap<>();
    private final Map<String, Map<NamespacedId, Double>> dimensionBases = new LinkedHashMap<>();
    private final Map<String, Map<NamespacedId, Double>> biomeOverrides = new LinkedHashMap<>();
    private final Map<String, Map<NamespacedId, Double>> biomeAdditives = new LinkedHashMap<>();
    private final Map<String, Map<NamespacedId, EnvironmentalHeightCurve>> heightCurves = new LinkedHashMap<>();

    public EnvironmentalFieldSnapshotBuilder(EnvironmentalFieldSnapshot base) {
        base.channels().forEach(channel -> channels.put(channel.id(), channel));
        copyNumbers(base.dimensionBasesData(), dimensionBases);
        copyNumbers(base.biomeOverridesData(), biomeOverrides);
        copyNumbers(base.biomeAdditivesData(), biomeAdditives);
        base.heightCurvesData().forEach((selector, values) -> heightCurves.put(selector, new LinkedHashMap<>(values)));
    }

    public void putChannel(EnvironmentalChannel channel) { channels.put(channel.id(), channel); }

    public void putDimensionValues(String dimension, Map<NamespacedId, Double> values) {
        merge(dimensionBases, dimension, values);
    }

    public void putBiomeOverrides(String biome, Map<NamespacedId, Double> values) {
        merge(biomeOverrides, biome, values);
    }

    public void putBiomeAdditives(String biome, Map<NamespacedId, Double> values) {
        merge(biomeAdditives, biome, values);
    }

    public void putHeightCurve(String dimension, NamespacedId channel, EnvironmentalHeightCurve curve) {
        heightCurves.computeIfAbsent(dimension, ignored -> new LinkedHashMap<>()).put(channel, curve);
    }

    public EnvironmentalFieldSnapshot build(long generation) {
        return new EnvironmentalFieldSnapshot(
                generation, new ArrayList<>(channels.values()), dimensionBases, biomeOverrides, biomeAdditives, heightCurves
        );
    }

    private static void merge(Map<String, Map<NamespacedId, Double>> target, String selector, Map<NamespacedId, Double> values) {
        target.computeIfAbsent(selector, ignored -> new LinkedHashMap<>()).putAll(values);
    }

    private static void copyNumbers(Map<String, Map<NamespacedId, Double>> source, Map<String, Map<NamespacedId, Double>> target) {
        source.forEach((selector, values) -> target.put(selector, new LinkedHashMap<>(values)));
    }
}
