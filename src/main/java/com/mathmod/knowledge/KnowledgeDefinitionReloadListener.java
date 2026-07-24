package com.mathmod.knowledge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mathmod.MathMod;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.util.NamespacedId;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class KnowledgeDefinitionReloadListener implements PreparableReloadListener {
    private static final String EPIPHANY_PATH = "mathmod/epiphanies";
    private static final String DISCOVERY_PATH = "mathmod/discoveries";
    private static final String ALIAS_PATH = "mathmod/knowledge_aliases";

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new KnowledgeDefinitionReloadListener());
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor
    ) {
        return CompletableFuture.supplyAsync(
                        () -> load(resourceManager),
                        backgroundExecutor
                )
                .thenCompose(barrier::wait)
                .thenAcceptAsync(this::apply, gameExecutor);
    }

    static LoadResult load(ResourceManager resources) {
        Map<NamespacedId, EpiphanyDefinition> epiphanies = loadDefinitions(
                resources,
                EPIPHANY_PATH,
                KnowledgeDefinitionReloadListener::parseEpiphany
        );
        Map<NamespacedId, DiscoveryDefinition> discoveries = loadDefinitions(
                resources,
                DISCOVERY_PATH,
                KnowledgeDefinitionReloadListener::parseDiscovery
        );
        Map<KnowledgeKey, KnowledgeKey> aliases = new LinkedHashMap<>();
        resources.listResources(ALIAS_PATH, KnowledgeDefinitionReloadListener::isJson)
                .forEach((location, resource) -> {
                    try (Reader reader = resource.openAsReader()) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        KnowledgeKind kind = KnowledgeKind.parse(requiredText(json, "kind"))
                                .orElseThrow(() -> new IllegalArgumentException("Unknown knowledge kind"));
                        NamespacedId from = NamespacedId.parse(requiredText(json, "from"));
                        NamespacedId to = NamespacedId.parse(requiredText(json, "to"));
                        aliases.put(new KnowledgeKey(kind, from), new KnowledgeKey(kind, to));
                    } catch (Exception exception) {
                        MathMod.LOGGER.error(
                                "Rejected knowledge alias {} from {}: {}",
                                location,
                                resource.sourcePackId(),
                                exception.getMessage()
                        );
                    }
                });
        return new LoadResult(epiphanies, discoveries, aliases);
    }

    private void apply(LoadResult result) {
        Map<NamespacedId, EpiphanyDefinition> epiphanies =
                validEpiphanies(result.epiphanies());
        Map<NamespacedId, DiscoveryDefinition> discoveries =
                validDiscoveries(result.discoveries());
        boolean definitionsPublished = true;
        try {
            KnowledgeDefinitions.publishData(epiphanies, discoveries);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            definitionsPublished = false;
            MathMod.LOGGER.error(
                    "Knowledge definitions were not published; the previous definition snapshot remains active: {}",
                    exception.getMessage()
            );
        }
        boolean aliasesPublished = true;
        try {
            KnowledgeAliases.publishData(result.aliases());
        } catch (IllegalArgumentException | IllegalStateException exception) {
            aliasesPublished = false;
            MathMod.LOGGER.error(
                    "Knowledge aliases were not published; the previous alias snapshot remains active: {}",
                    exception.getMessage()
            );
        }
        if (definitionsPublished || aliasesPublished) {
            MathMod.LOGGER.info(
                    "Knowledge reload published definitions={} ({} epiphanies, {} discoveries), aliases={} ({})",
                    definitionsPublished,
                    epiphanies.size(),
                    discoveries.size(),
                    aliasesPublished,
                    result.aliases().size()
            );
        }
    }

    private static Map<NamespacedId, EpiphanyDefinition> validEpiphanies(
            Map<NamespacedId, EpiphanyDefinition> definitions
    ) {
        Map<NamespacedId, EpiphanyDefinition> valid = new LinkedHashMap<>();
        definitions.forEach((id, definition) -> {
            try {
                KnowledgeDefinitions.validateDefinition(
                        definition,
                        MathModRuneBootstrap.registry()
                );
                valid.put(id, definition);
            } catch (IllegalStateException exception) {
                MathMod.LOGGER.error("Rejected epiphany {}: {}", id, exception.getMessage());
            }
        });
        return valid;
    }

    private static Map<NamespacedId, DiscoveryDefinition> validDiscoveries(
            Map<NamespacedId, DiscoveryDefinition> definitions
    ) {
        Map<NamespacedId, DiscoveryDefinition> valid = new LinkedHashMap<>();
        definitions.forEach((id, definition) -> {
            try {
                KnowledgeDefinitions.validateDefinition(
                        definition,
                        MathModRuneBootstrap.registry()
                );
                valid.put(id, definition);
            } catch (IllegalStateException exception) {
                MathMod.LOGGER.error("Rejected discovery {}: {}", id, exception.getMessage());
            }
        });
        return valid;
    }

    private static EpiphanyDefinition parseEpiphany(NamespacedId id, JsonObject json) {
        requireSchema(json);
        JsonArray studiesJson = requiredArray(json, "studies");
        List<MaterialStudyRequirement> studies = studiesJson.asList().stream()
                .map(JsonElement::getAsJsonObject)
                .map(study -> new MaterialStudyRequirement(
                        NamespacedId.parse(requiredText(study, "material")),
                        requiredInt(study, "tier"),
                        requiredInt(study, "successful_casts")
                ))
                .toList();
        return new EpiphanyDefinition(
                id,
                requiredText(json, "title_key"),
                NamespacedId.parse(requiredText(json, "correlation")),
                studies,
                parseGrants(json)
        );
    }

    private static DiscoveryDefinition parseDiscovery(NamespacedId id, JsonObject json) {
        requireSchema(json);
        return new DiscoveryDefinition(
                id,
                NamespacedId.parse(requiredText(json, "manuscript")),
                requiredText(json, "title_key"),
                NamespacedId.parse(requiredText(json, "patchouli_entry")),
                parseGrants(json)
        );
    }

    private static List<KnowledgeGrant> parseGrants(JsonObject json) {
        return requiredArray(json, "grants").asList().stream()
                .map(JsonElement::getAsJsonObject)
                .map(grant -> new KnowledgeGrant(
                        KnowledgeKind.parse(requiredText(grant, "kind"))
                                .orElseThrow(() -> new IllegalArgumentException("Unknown grant kind")),
                        NamespacedId.parse(requiredText(grant, "id"))
                ))
                .toList();
    }

    private static <T> Map<NamespacedId, T> loadDefinitions(
            ResourceManager resources,
            String directory,
            DefinitionParser<T> parser
    ) {
        Map<NamespacedId, T> definitions = new LinkedHashMap<>();
        resources.listResources(directory, KnowledgeDefinitionReloadListener::isJson)
                .forEach((location, resource) -> {
                    try (Reader reader = resource.openAsReader()) {
                        NamespacedId id = definitionId(location, directory);
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        definitions.put(id, parser.parse(id, json));
                    } catch (Exception exception) {
                        MathMod.LOGGER.error(
                                "Rejected knowledge definition {} from {}: {}",
                                location,
                                resource.sourcePackId(),
                                exception.getMessage()
                        );
                    }
                });
        return definitions;
    }

    private static NamespacedId definitionId(ResourceLocation location, String directory) {
        String prefix = directory + "/";
        String path = location.getPath();
        if (!path.startsWith(prefix) || !path.endsWith(".json")) {
            throw new IllegalArgumentException("Invalid knowledge resource path " + location);
        }
        return NamespacedId.of(
                location.getNamespace(),
                path.substring(prefix.length(), path.length() - ".json".length())
        );
    }

    private static boolean isJson(ResourceLocation location) {
        return location.getPath().endsWith(".json");
    }

    private static void requireSchema(JsonObject json) {
        if (requiredInt(json, "schema_version") != 1) {
            throw new IllegalArgumentException("Unsupported schema_version");
        }
    }

    private static String requiredText(JsonObject json, String field) {
        if (!json.has(field) || !json.get(field).isJsonPrimitive()) {
            throw new IllegalArgumentException("Missing text field " + field);
        }
        String value = json.get(field).getAsString().trim();
        if (value.isEmpty() || value.length() > 160) {
            throw new IllegalArgumentException("Invalid text field " + field);
        }
        return value;
    }

    private static int requiredInt(JsonObject json, String field) {
        if (!json.has(field) || !json.get(field).isJsonPrimitive()) {
            throw new IllegalArgumentException("Missing integer field " + field);
        }
        return json.get(field).getAsInt();
    }

    private static JsonArray requiredArray(JsonObject json, String field) {
        if (!json.has(field) || !json.get(field).isJsonArray()) {
            throw new IllegalArgumentException("Missing array field " + field);
        }
        return json.getAsJsonArray(field);
    }

    record LoadResult(
            Map<NamespacedId, EpiphanyDefinition> epiphanies,
            Map<NamespacedId, DiscoveryDefinition> discoveries,
            Map<KnowledgeKey, KnowledgeKey> aliases
    ) {
        LoadResult {
            epiphanies = Map.copyOf(epiphanies);
            discoveries = Map.copyOf(discoveries);
            aliases = Map.copyOf(aliases);
        }
    }

    @FunctionalInterface
    private interface DefinitionParser<T> {
        T parse(NamespacedId id, JsonObject json);
    }
}
