package com.mathmod.environment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mathmod.MathMod;
import com.mathmod.field.FieldProviderServices;
import com.mathmod.util.NamespacedId;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/** Loads only declarative P13 records and swaps one complete snapshot on success. */
public final class EnvironmentalFieldReloadListener implements PreparableReloadListener {
    private static final String CHANNEL_PATH = "mathmod/environment/channels";
    private static final String DIMENSION_PATH = "mathmod/environment/dimensions";
    private static final String BIOME_PATH = "mathmod/environment/biomes";
    private static final String CURVE_PATH = "mathmod/environment/height_curves";
    private static final String ALIAS_PATH = "mathmod/environment/aliases";

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new EnvironmentalFieldReloadListener());
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier, ResourceManager resources, ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor
    ) {
        return CompletableFuture.supplyAsync(() -> load(resources), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(this::apply, gameExecutor);
    }

    static LoadResult load(ResourceManager resources) {
        List<ChannelRecord> channels = load(resources, CHANNEL_PATH, EnvironmentalFieldReloadListener::parseChannel);
        List<DimensionRecord> dimensions = load(resources, DIMENSION_PATH, EnvironmentalFieldReloadListener::parseDimension);
        List<BiomeRecord> biomes = load(resources, BIOME_PATH, EnvironmentalFieldReloadListener::parseBiome);
        List<CurveRecord> curves = load(resources, CURVE_PATH, EnvironmentalFieldReloadListener::parseCurve);
        List<AliasRecord> aliases = load(resources, ALIAS_PATH, EnvironmentalFieldReloadListener::parseAlias);
        return new LoadResult(channels, dimensions, biomes, curves, aliases);
    }

    private void apply(LoadResult result) {
        try {
            EnvironmentalFieldSnapshotBuilder builder = new EnvironmentalFieldSnapshotBuilder(EnvironmentalFieldSnapshot.builtIns());
            result.channels().forEach(record -> builder.putChannel(record.channel()));
            result.dimensions().forEach(record -> builder.putDimensionValues(record.dimension(), record.values()));
            result.biomes().forEach(record -> {
                builder.putBiomeOverrides(record.biome(), record.overrides());
                builder.putBiomeAdditives(record.biome(), record.additives());
            });
            result.curves().forEach(record -> builder.putHeightCurve(record.dimension(), record.channel(), record.curve()));
            Map<NamespacedId, NamespacedId> aliases = new LinkedHashMap<>();
            result.aliases().forEach(record -> {
                if (aliases.putIfAbsent(record.from(), record.to()) != null) {
                    throw new IllegalArgumentException("Duplicate environmental alias " + record.from());
                }
            });
            EnvironmentalFieldServices.publish(builder.build(0L), aliases);
            FieldProviderServices.reloadEnvironmental(EnvironmentalFieldServices.snapshot());
            MathMod.LOGGER.info(
                    "Published environmental field generation {} with {} channels and {} aliases",
                    EnvironmentalFieldServices.snapshot().generation(),
                    EnvironmentalFieldServices.snapshot().channels().size(), aliases.size()
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            MathMod.LOGGER.error("Environmental field was not published; the previous generation remains active: {}", exception.getMessage());
        }
    }

    private static <T> List<T> load(ResourceManager resources, String directory, RecordParser<T> parser) {
        List<Map.Entry<ResourceLocation, Resource>> records = new ArrayList<>(resources.listResources(directory, EnvironmentalFieldReloadListener::isJson).entrySet());
        records.sort(Map.Entry.comparingByKey(Comparator.comparing(ResourceLocation::toString)));
        List<T> decoded = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Resource> entry : records) {
            try (Reader reader = entry.getValue().openAsReader()) {
                decoded.add(parser.parse(JsonParser.parseReader(reader).getAsJsonObject()));
            } catch (Exception exception) {
                MathMod.LOGGER.error(
                        "Rejected environmental record {} from {}: {}",
                        entry.getKey(), entry.getValue().sourcePackId(), message(exception)
                );
            }
        }
        return List.copyOf(decoded);
    }

    static ChannelRecord parseChannel(JsonObject json) {
        requireFields(json, "schema_version", "id", "minimum", "maximum", "noise_amplitude", "noise_scale", "report_scale");
        requireSchema(json);
        return new ChannelRecord(new EnvironmentalChannel(
                id(json, "id"), number(json, "minimum"), number(json, "maximum"), number(json, "noise_amplitude"),
                integer(json, "noise_scale"), number(json, "report_scale")
        ));
    }

    static DimensionRecord parseDimension(JsonObject json) {
        requireFields(json, "schema_version", "dimension", "values");
        requireSchema(json);
        return new DimensionRecord(text(json, "dimension"), numbers(object(json, "values")));
    }

    static BiomeRecord parseBiome(JsonObject json) {
        requireFields(json, "schema_version", "biome", "overrides", "additives");
        requireSchema(json);
        return new BiomeRecord(text(json, "biome"), numbers(object(json, "overrides")), numbers(object(json, "additives")));
    }

    static CurveRecord parseCurve(JsonObject json) {
        requireFields(json, "schema_version", "dimension", "channel", "points");
        requireSchema(json);
        JsonArray points = array(json, "points");
        List<EnvironmentalHeightCurve.Point> decoded = new ArrayList<>();
        for (JsonElement point : points) {
            if (!point.isJsonObject()) throw new IllegalArgumentException("invalid_curve_point");
            JsonObject value = point.getAsJsonObject();
            requireFields(value, "x", "y");
            decoded.add(new EnvironmentalHeightCurve.Point(number(value, "x"), number(value, "y")));
        }
        return new CurveRecord(text(json, "dimension"), id(json, "channel"), new EnvironmentalHeightCurve(decoded));
    }

    static AliasRecord parseAlias(JsonObject json) {
        requireFields(json, "schema_version", "from", "to");
        requireSchema(json);
        return new AliasRecord(id(json, "from"), id(json, "to"));
    }

    private static Map<NamespacedId, Double> numbers(JsonObject json) {
        if (json.size() > 16) throw new IllegalArgumentException("too_many_channel_contributions");
        Map<NamespacedId, Double> values = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                throw new IllegalArgumentException("invalid_number " + entry.getKey());
            }
            double value = entry.getValue().getAsDouble();
            if (!Double.isFinite(value) || Math.abs(value) > 16.0D) throw new IllegalArgumentException("invalid_number " + entry.getKey());
            values.put(NamespacedId.parse(entry.getKey()), value);
        }
        return Map.copyOf(values);
    }

    private static boolean isJson(ResourceLocation location) { return location.getPath().endsWith(".json"); }
    private static void requireSchema(JsonObject json) { if (integer(json, "schema_version") != 1) throw new IllegalArgumentException("unsupported_schema"); }
    private static void requireFields(JsonObject json, String... allowed) {
        java.util.Set<String> allowedSet = java.util.Set.of(allowed);
        for (String field : json.keySet()) if (!allowedSet.contains(field)) throw new IllegalArgumentException("unknown_field " + field);
    }
    private static JsonObject object(JsonObject json, String field) { if (!json.has(field) || !json.get(field).isJsonObject()) throw new IllegalArgumentException("invalid_object " + field); return json.getAsJsonObject(field); }
    private static JsonArray array(JsonObject json, String field) { if (!json.has(field) || !json.get(field).isJsonArray()) throw new IllegalArgumentException("invalid_array " + field); return json.getAsJsonArray(field); }
    private static String text(JsonObject json, String field) { if (!json.has(field) || !json.get(field).isJsonPrimitive() || !json.getAsJsonPrimitive(field).isString()) throw new IllegalArgumentException("invalid_id " + field); String value = json.get(field).getAsString().trim().toLowerCase(java.util.Locale.ROOT); if (ResourceLocation.tryParse(value) == null) throw new IllegalArgumentException("invalid_id " + field); return value; }
    private static NamespacedId id(JsonObject json, String field) { return NamespacedId.parse(text(json, field)); }
    private static int integer(JsonObject json, String field) { if (!json.has(field) || !json.get(field).isJsonPrimitive() || !json.getAsJsonPrimitive(field).isNumber()) throw new IllegalArgumentException("invalid_number " + field); return json.get(field).getAsInt(); }
    private static double number(JsonObject json, String field) { if (!json.has(field) || !json.get(field).isJsonPrimitive() || !json.getAsJsonPrimitive(field).isNumber()) throw new IllegalArgumentException("invalid_number " + field); double value = json.get(field).getAsDouble(); if (!Double.isFinite(value)) throw new IllegalArgumentException("invalid_number " + field); return value; }
    private static String message(Exception exception) { return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage(); }

    record LoadResult(List<ChannelRecord> channels, List<DimensionRecord> dimensions, List<BiomeRecord> biomes, List<CurveRecord> curves, List<AliasRecord> aliases) { }
    record ChannelRecord(EnvironmentalChannel channel) { }
    record DimensionRecord(String dimension, Map<NamespacedId, Double> values) { }
    record BiomeRecord(String biome, Map<NamespacedId, Double> overrides, Map<NamespacedId, Double> additives) { }
    record CurveRecord(String dimension, NamespacedId channel, EnvironmentalHeightCurve curve) { }
    record AliasRecord(NamespacedId from, NamespacedId to) { }
    @FunctionalInterface private interface RecordParser<T> { T parse(JsonObject json); }
}
