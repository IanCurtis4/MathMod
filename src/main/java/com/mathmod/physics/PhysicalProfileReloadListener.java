package com.mathmod.physics;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mathmod.MathMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/** Reloads schema-1 physical declarations, then atomically swaps the server snapshot. */
public final class PhysicalProfileReloadListener implements PreparableReloadListener {
    private static final String PROFILE_PATH = "mathmod/physics/profiles";
    private static final String POLICY_PATH = "mathmod/physics/policies";
    private static final String DEFAULT_POLICY_ID = MathMod.MOD_ID + ":default";

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new PhysicalProfileReloadListener());
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor
    ) {
        return CompletableFuture.supplyAsync(() -> load(resourceManager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(this::apply, gameExecutor);
    }

    static LoadResult load(ResourceManager resources) {
        List<PhysicalProfileDeclaration> profiles = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        resources.listResources(PROFILE_PATH, PhysicalProfileReloadListener::isJson).forEach((location, resource) -> {
            try (Reader reader = resource.openAsReader()) {
                profiles.add(parseProfile(resourceId(location, PROFILE_PATH), JsonParser.parseReader(reader).getAsJsonObject()));
            } catch (Exception exception) {
                String error = "profile " + location + " from " + resource.sourcePackId() + ": " + exception.getMessage();
                errors.add(error);
                MathMod.LOGGER.error("Rejected physical {}", error);
            }
        });
        Map<String, PhysicsPolicy> policies = new LinkedHashMap<>();
        resources.listResources(POLICY_PATH, PhysicalProfileReloadListener::isJson).forEach((location, resource) -> {
            try (Reader reader = resource.openAsReader()) {
                policies.put(resourceId(location, POLICY_PATH), parsePolicy(JsonParser.parseReader(reader).getAsJsonObject()));
            } catch (Exception exception) {
                String error = "policy " + location + " from " + resource.sourcePackId() + ": " + exception.getMessage();
                errors.add(error);
                MathMod.LOGGER.error("Rejected physical {}", error);
            }
        });
        return new LoadResult(List.copyOf(profiles), Map.copyOf(policies), List.copyOf(errors));
    }

    private void apply(LoadResult result) {
        try {
            if (!result.errors().isEmpty()) throw new IllegalArgumentException(result.errors().getFirst());
            List<PhysicalProfileDeclaration> profiles = validateBindings(result.profiles());
            if (result.policies().size() > 64) throw new IllegalArgumentException("too_many_policies");
            PhysicsPolicy policy = result.policies().getOrDefault(DEFAULT_POLICY_ID, PhysicsPolicy.defaults());
            PhysicalProfiles.publishData(policy, profiles);
            MathMod.LOGGER.info("Physical reload published {} profiles at snapshot {}", profiles.size(), PhysicalProfiles.snapshot().version());
        } catch (IllegalArgumentException | IllegalStateException exception) {
            MathMod.LOGGER.error("Physical profiles were not published; the previous snapshot remains active: {}", exception.getMessage());
        }
    }

    private static List<PhysicalProfileDeclaration> validateBindings(List<PhysicalProfileDeclaration> profiles) {
        if (profiles.size() > 4096) throw new IllegalArgumentException("too_many_profiles");
        for (PhysicalProfileDeclaration profile : profiles) {
            ResourceLocation id = ResourceLocation.tryParse(profile.selector().id());
            if (id == null) throw new IllegalArgumentException("invalid_id " + profile.selector().id());
            if (profile.selector().kind() == PhysicalSelector.Kind.BLOCK && !BuiltInRegistries.BLOCK.containsKey(id)) {
                throw new IllegalArgumentException("unknown_block " + id);
            }
            if (profile.selector().kind() == PhysicalSelector.Kind.TAG
                    && BuiltInRegistries.BLOCK.getTag(TagKey.create(BuiltInRegistries.BLOCK.key(), id)).isEmpty()) {
                throw new IllegalArgumentException("unknown_tag " + id);
            }
        }
        validateTagAmbiguities(profiles);
        return profiles;
    }

    private static void validateTagAmbiguities(List<PhysicalProfileDeclaration> profiles) {
        List<PhysicalProfileDeclaration> tags = profiles.stream()
                .filter(profile -> profile.selector().kind() == PhysicalSelector.Kind.TAG)
                .toList();
        for (int leftIndex = 0; leftIndex < tags.size(); leftIndex++) {
            PhysicalProfileDeclaration left = tags.get(leftIndex);
            for (int rightIndex = leftIndex + 1; rightIndex < tags.size(); rightIndex++) {
                PhysicalProfileDeclaration right = tags.get(rightIndex);
                if (left.source().precedence() != right.source().precedence() || left.priority() != right.priority()) continue;
                Set<ResourceLocation> overlapping = tagMembers(left.selector().id());
                overlapping.retainAll(tagMembers(right.selector().id()));
                if (!overlapping.isEmpty()) throw new IllegalArgumentException("ambiguous_tag_match " + left.id() + " / " + right.id());
            }
        }
    }

    private static Set<ResourceLocation> tagMembers(String tagId) {
        ResourceLocation id = ResourceLocation.parse(tagId);
        return BuiltInRegistries.BLOCK.getTag(TagKey.create(BuiltInRegistries.BLOCK.key(), id))
                .orElseThrow(() -> new IllegalArgumentException("unknown_tag " + id))
                .stream()
                .map(holder -> BuiltInRegistries.BLOCK.getKey(holder.value()))
                .collect(java.util.stream.Collectors.toSet());
    }

    private static PhysicalProfileDeclaration parseProfile(String id, JsonObject json) {
        requireFields(json, Set.of("schema_version", "selector", "priority", "density", "structural_strength", "brittleness", "elasticity", "thermal_resistance", "magical_resistance", "compression_mass_exponent"));
        requireSchema(json);
        JsonObject selector = requiredObject(json, "selector");
        requireFields(selector, Set.of("type", "id"));
        String selectorType = requiredString(selector, "type");
        PhysicalSelector.Kind kind = switch (selectorType) {
            case "block" -> PhysicalSelector.Kind.BLOCK;
            case "tag" -> PhysicalSelector.Kind.TAG;
            default -> throw new IllegalArgumentException("invalid_id selector.type");
        };
        return new PhysicalProfileDeclaration(
                id, new PhysicalSelector(kind, requiredString(selector, "id")), PhysicalProfileSource.DATA_PACK,
                optionalInt(json, "priority", 0), requiredNumber(json, "density"),
                optionalNumber(json, "structural_strength"), optionalNumber(json, "brittleness"),
                optionalNumber(json, "elasticity"), optionalNumber(json, "thermal_resistance"),
                optionalNumber(json, "magical_resistance"), optionalNumber(json, "compression_mass_exponent")
        );
    }

    private static PhysicsPolicy parsePolicy(JsonObject json) {
        requireFields(json, Set.of("schema_version", "shape_resolution", "default_density", "hardness_weight", "blast_resistance_weight", "fallback_base_mass", "fallback_hardness_weight", "fallback_blast_weight", "default_compression_mass_exponent", "default_structural_strength", "default_brittleness", "default_elasticity", "default_thermal_resistance", "default_magical_resistance"));
        requireSchema(json);
        PhysicsPolicy defaults = PhysicsPolicy.defaults();
        return new PhysicsPolicy(optionalInt(json, "shape_resolution", defaults.shapeResolution()), optionalNumber(json, "default_density", defaults.defaultDensity()), optionalNumber(json, "hardness_weight", defaults.hardnessWeight()), optionalNumber(json, "blast_resistance_weight", defaults.blastResistanceWeight()), optionalNumber(json, "fallback_base_mass", defaults.fallbackBaseMass()), optionalNumber(json, "fallback_hardness_weight", defaults.fallbackHardnessWeight()), optionalNumber(json, "fallback_blast_weight", defaults.fallbackBlastWeight()), optionalNumber(json, "default_compression_mass_exponent", defaults.defaultCompressionMassExponent()), optionalNumber(json, "default_structural_strength", defaults.defaultStructuralStrength()), optionalNumber(json, "default_brittleness", defaults.defaultBrittleness()), optionalNumber(json, "default_elasticity", defaults.defaultElasticity()), optionalNumber(json, "default_thermal_resistance", defaults.defaultThermalResistance()), optionalNumber(json, "default_magical_resistance", defaults.defaultMagicalResistance()));
    }

    private static String resourceId(ResourceLocation location, String directory) {
        String prefix = directory + "/";
        String path = location.getPath();
        if (!path.startsWith(prefix) || !path.endsWith(".json")) throw new IllegalArgumentException("invalid_id " + location);
        return location.getNamespace() + ":" + path.substring(prefix.length(), path.length() - 5);
    }
    private static boolean isJson(ResourceLocation location) { return location.getPath().endsWith(".json"); }
    private static void requireSchema(JsonObject json) { if (requiredInt(json, "schema_version") != 1) throw new IllegalArgumentException("unsupported_schema"); }
    private static void requireFields(JsonObject json, Set<String> allowed) { for (String field : json.keySet()) if (!allowed.contains(field)) throw new IllegalArgumentException("unknown_field " + field); }
    private static JsonObject requiredObject(JsonObject json, String field) { if (!json.has(field) || !json.get(field).isJsonObject()) throw new IllegalArgumentException("invalid_id " + field); return json.getAsJsonObject(field); }
    private static String requiredString(JsonObject json, String field) { if (!json.has(field) || !json.get(field).isJsonPrimitive() || !json.get(field).getAsJsonPrimitive().isString()) throw new IllegalArgumentException("invalid_id " + field); String value = json.get(field).getAsString().trim(); if (value.isEmpty()) throw new IllegalArgumentException("invalid_id " + field); return value; }
    private static int requiredInt(JsonObject json, String field) { if (!json.has(field) || !json.get(field).isJsonPrimitive() || !json.get(field).getAsJsonPrimitive().isNumber()) throw new IllegalArgumentException("invalid_number " + field); return json.get(field).getAsInt(); }
    private static int optionalInt(JsonObject json, String field, int fallback) { return json.has(field) ? requiredInt(json, field) : fallback; }
    private static double requiredNumber(JsonObject json, String field) { if (!json.has(field)) throw new IllegalArgumentException("invalid_number " + field); return optionalNumber(json, field); }
    private static Double optionalNumber(JsonObject json, String field) { if (!json.has(field) || !json.get(field).isJsonPrimitive() || !json.get(field).getAsJsonPrimitive().isNumber()) throw new IllegalArgumentException("invalid_number " + field); return json.get(field).getAsDouble(); }
    private static double optionalNumber(JsonObject json, String field, double fallback) { return json.has(field) ? optionalNumber(json, field) : fallback; }

    record LoadResult(List<PhysicalProfileDeclaration> profiles, Map<String, PhysicsPolicy> policies, List<String> errors) {
    }
}
