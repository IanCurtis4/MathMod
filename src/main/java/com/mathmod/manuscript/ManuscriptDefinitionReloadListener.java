package com.mathmod.manuscript;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mathmod.MathMod;
import com.mathmod.acquisition.AcquisitionCodecs;
import com.mathmod.acquisition.MathModServerConfig;
import com.mathmod.acquisition.ManuscriptAcquisitionBuildResult;
import com.mathmod.acquisition.ManuscriptAcquisitionConfig;
import com.mathmod.acquisition.ManuscriptAcquisitionSnapshotBuilder;
import com.mathmod.kubejs.KubeJsCompat;
import com.mathmod.kubejs.KubeJsManuscriptDeclarationStore;
import com.mathmod.program.ProgramPresets;
import com.mathmod.util.NamespacedId;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.io.Reader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ManuscriptDefinitionReloadListener implements PreparableReloadListener {
    private static final String TRADITION_PATH = "mathmod/traditions";
    private static final String MANUSCRIPT_PATH = "mathmod/manuscripts";
    private static final String ALIAS_PATH = "mathmod/manuscript_aliases";
    private static final String ACQUISITION_PATH = "mathmod/manuscript_acquisition";

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new ManuscriptDefinitionReloadListener());
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
        return CompletableFuture.supplyAsync(() -> loadAll(resourceManager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(this::apply, gameExecutor);
    }

    private static ReloadCandidate loadAll(ResourceManager resources) {
        ManuscriptSnapshotBuildResult manuscripts = load(resources);
        ManuscriptAcquisitionBuildResult acquisition = loadAcquisition(resources, manuscripts.snapshot());
        return new ReloadCandidate(manuscripts, acquisition, MathModServerConfig.snapshot());
    }

    static ManuscriptSnapshotBuildResult load(ResourceManager resources) {
        ManuscriptSnapshotBuilder builder = new ManuscriptSnapshotBuilder(
                theoremId -> ProgramPresets.presetForId(theoremId.toString()).isPresent()
        );
        loadTraditions(resources, builder);
        loadManuscripts(resources, builder);
        loadAliases(resources, builder);
        addValidatedKubeDeclarations(builder, KubeJsCompat.freezeManuscriptDeclarations());
        return builder.build();
    }

    private static void addValidatedKubeDeclarations(
            ManuscriptSnapshotBuilder builder,
            KubeJsManuscriptDeclarationStore.Snapshot declarations
    ) {
        declarations.traditions().forEach((id, definition) -> {
            if (validItem(definition.icon())) {
                builder.addTradition(definition, declarations.source());
            } else {
                builder.reject(
                        ManuscriptDiagnostic.Code.UNKNOWN_ICON,
                        ManuscriptDiagnostic.RecordKind.TRADITION,
                        id,
                        declarations.source(),
                        "Unknown item icon " + definition.icon()
                );
            }
        });
        declarations.manuscripts().forEach((id, definition) -> {
            if (validItem(definition.icon())) {
                builder.addManuscript(definition, declarations.source());
            } else {
                builder.reject(
                        ManuscriptDiagnostic.Code.UNKNOWN_ICON,
                        ManuscriptDiagnostic.RecordKind.MANUSCRIPT,
                        id,
                        declarations.source(),
                        "Unknown item icon " + definition.icon()
                );
            }
        });
        declarations.aliases().values().forEach(definition ->
                builder.addAlias(definition, declarations.source()));
    }

    private void apply(ReloadCandidate candidate) {
        candidate.manuscripts().diagnostics().forEach(ManuscriptDefinitionReloadListener::logDiagnostic);
        candidate.acquisition().diagnostics().forEach(ManuscriptDefinitionReloadListener::logAcquisitionDiagnostic);
        if (!ManuscriptDefinitions.publish(
                candidate.manuscripts(),
                candidate.acquisition(),
                candidate.config()
        )) {
            MathMod.LOGGER.error(
                    "Manuscript acquisition reload was rejected; the previous published generation remains active"
            );
            return;
        }
        ManuscriptSnapshot snapshot = candidate.manuscripts().snapshot();
        MathMod.LOGGER.info(
                "Published manuscript acquisition generation {}: {} traditions, {} manuscripts, {} aliases, {} acquisition candidates",
                ManuscriptDefinitions.acquisitionGeneration(),
                snapshot.traditions().size(),
                snapshot.manuscripts().size(),
                snapshot.aliasCount(),
                candidate.acquisition().snapshot().candidates().size()
        );
    }

    private static ManuscriptAcquisitionBuildResult loadAcquisition(
            ResourceManager resources,
            ManuscriptSnapshot manuscripts
    ) {
        ManuscriptAcquisitionSnapshotBuilder builder = new ManuscriptAcquisitionSnapshotBuilder(manuscripts);
        resources.listResources(ACQUISITION_PATH, ManuscriptDefinitionReloadListener::isJson)
                .forEach((location, resource) -> {
                    NamespacedId id = definitionId(location, ACQUISITION_PATH);
                    ManuscriptDefinitionSource source = source(resource);
                    readJson(resource)
                            .flatMap(json -> AcquisitionCodecs.decode(id, json))
                            .resultOrPartial(message -> builder.reject(id, source, message))
                            .ifPresent(definition -> builder.add(definition, source));
                });
        return builder.build();
    }

    private static void loadTraditions(ResourceManager resources, ManuscriptSnapshotBuilder builder) {
        resources.listResources(TRADITION_PATH, ManuscriptDefinitionReloadListener::isJson)
                .forEach((location, resource) -> {
                    NamespacedId id = definitionId(location, TRADITION_PATH);
                    ManuscriptDefinitionSource source = source(resource);
                    readJson(resource)
                            .flatMap(json -> ManuscriptCodecs.decodeTradition(id, json))
                            .resultOrPartial(message -> builder.reject(
                                    ManuscriptDiagnostic.Code.DECODE_FAILED,
                                    ManuscriptDiagnostic.RecordKind.TRADITION,
                                    id,
                                    source,
                                    message
                            ))
                            .ifPresent(definition -> {
                                if (validItem(definition.icon())) {
                                    builder.addTradition(definition, source);
                                } else {
                                    builder.reject(
                                            ManuscriptDiagnostic.Code.UNKNOWN_ICON,
                                            ManuscriptDiagnostic.RecordKind.TRADITION,
                                            id,
                                            source,
                                            "Unknown item icon " + definition.icon()
                                    );
                                }
                            });
                });
    }

    private static void loadManuscripts(ResourceManager resources, ManuscriptSnapshotBuilder builder) {
        resources.listResources(MANUSCRIPT_PATH, ManuscriptDefinitionReloadListener::isJson)
                .forEach((location, resource) -> {
                    NamespacedId id = definitionId(location, MANUSCRIPT_PATH);
                    ManuscriptDefinitionSource source = source(resource);
                    readJson(resource)
                            .flatMap(json -> ManuscriptCodecs.decodeManuscript(id, json))
                            .resultOrPartial(message -> builder.reject(
                                    ManuscriptDiagnostic.Code.DECODE_FAILED,
                                    ManuscriptDiagnostic.RecordKind.MANUSCRIPT,
                                    id,
                                    source,
                                    message
                            ))
                            .ifPresent(definition -> {
                                if (validItem(definition.icon())) {
                                    builder.addManuscript(definition, source);
                                } else {
                                    builder.reject(
                                            ManuscriptDiagnostic.Code.UNKNOWN_ICON,
                                            ManuscriptDiagnostic.RecordKind.MANUSCRIPT,
                                            id,
                                            source,
                                            "Unknown item icon " + definition.icon()
                                    );
                                }
                            });
                });
    }

    private static void loadAliases(ResourceManager resources, ManuscriptSnapshotBuilder builder) {
        resources.listResources(ALIAS_PATH, ManuscriptDefinitionReloadListener::isJson)
                .forEach((location, resource) -> {
                    NamespacedId id = definitionId(location, ALIAS_PATH);
                    ManuscriptDefinitionSource source = source(resource);
                    readJson(resource)
                            .flatMap(json -> ManuscriptCodecs.ALIAS.parse(
                                    com.mojang.serialization.JsonOps.INSTANCE,
                                    json
                            ))
                            .resultOrPartial(message -> builder.reject(
                                    ManuscriptDiagnostic.Code.DECODE_FAILED,
                                    ManuscriptDiagnostic.RecordKind.ALIAS,
                                    id,
                                    source,
                                    message
                            ))
                            .ifPresent(definition -> builder.addAlias(definition, source));
                });
    }

    private static DataResult<JsonElement> readJson(Resource resource) {
        try (Reader reader = resource.openAsReader()) {
            return DataResult.success(JsonParser.parseReader(reader));
        } catch (Exception exception) {
            String message = exception.getMessage() == null
                    ? exception.getClass().getSimpleName()
                    : exception.getMessage();
            return DataResult.error(() -> message);
        }
    }

    private static NamespacedId definitionId(ResourceLocation location, String directory) {
        String prefix = directory + "/";
        String path = location.getPath();
        if (!path.startsWith(prefix) || !path.endsWith(".json")) {
            throw new IllegalArgumentException("Invalid manuscript resource path " + location);
        }
        return NamespacedId.of(
                location.getNamespace(),
                path.substring(prefix.length(), path.length() - ".json".length())
        );
    }

    private static ManuscriptDefinitionSource source(Resource resource) {
        return ManuscriptKubeAssembly.sourceForPackId(resource.sourcePackId());
    }

    private static boolean validItem(NamespacedId id) {
        return BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(id.toString()));
    }

    private static boolean isJson(ResourceLocation location) {
        return location.getPath().endsWith(".json");
    }

    private static void logDiagnostic(ManuscriptDiagnostic diagnostic) {
        String message = "Manuscript {} {} from {}: {}";
        if (diagnostic.severity() == ManuscriptDiagnostic.Severity.ERROR
                || diagnostic.severity() == ManuscriptDiagnostic.Severity.FATAL) {
            MathMod.LOGGER.error(
                    message,
                    diagnostic.recordKind(),
                    diagnostic.id(),
                    diagnostic.source().sourceName(),
                    diagnostic.message()
            );
        } else {
            MathMod.LOGGER.info(
                    message,
                    diagnostic.recordKind(),
                    diagnostic.id(),
                    diagnostic.source().sourceName(),
                    diagnostic.message()
            );
        }
    }

    private static void logAcquisitionDiagnostic(com.mathmod.acquisition.AcquisitionDiagnostic diagnostic) {
        MathMod.LOGGER.error(
                "Acquisition {} from {}: {}",
                diagnostic.id(),
                diagnostic.source().sourceName(),
                diagnostic.message()
        );
    }

    private record ReloadCandidate(
            ManuscriptSnapshotBuildResult manuscripts,
            ManuscriptAcquisitionBuildResult acquisition,
            ManuscriptAcquisitionConfig config
    ) {
    }
}
