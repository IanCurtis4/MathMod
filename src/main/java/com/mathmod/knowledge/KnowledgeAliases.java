package com.mathmod.knowledge;

import com.mathmod.MathMod;
import com.mathmod.program.ProgramPresets;
import com.mathmod.util.NamespacedId;

import java.util.LinkedHashMap;
import java.util.Map;

public final class KnowledgeAliases {
    private static final int MAX_ALIASES = 4096;
    private static final Map<KnowledgeKey, KnowledgeKey> KUBE_ALIASES = new LinkedHashMap<>();
    private static Map<KnowledgeKey, KnowledgeKey> dataAliases = Map.of();
    private static volatile KnowledgeAliasRegistry current = createCurrent();

    private KnowledgeAliases() {
    }

    public static KnowledgeAliasRegistry current() {
        return KnowledgeReloadPublication.aliases();
    }

    public static void registerKube(
            KnowledgeKind kind,
            NamespacedId alias,
            NamespacedId target
    ) {
        KnowledgeReloadPublication.registerKube(kind, alias, target);
    }

    static void publishData(Map<KnowledgeKey, KnowledgeKey> aliases) {
        KnowledgeReloadPublication.publishAliases(aliases);
    }

    static Prepared prepareData(Map<KnowledgeKey, KnowledgeKey> aliases) {
        if (aliases.size() > MAX_ALIASES) {
            throw new IllegalArgumentException(
                    "Too many knowledge aliases: " + aliases.size() + " > " + MAX_ALIASES
            );
        }
        return prepare(KUBE_ALIASES, aliases);
    }

    static Prepared prepareCurrent() {
        return prepare(KUBE_ALIASES, dataAliases);
    }

    static Prepared prepareKube(KnowledgeKind kind, NamespacedId alias, NamespacedId target) {
        Map<KnowledgeKey, KnowledgeKey> candidate = new LinkedHashMap<>(KUBE_ALIASES);
        candidate.put(new KnowledgeKey(kind, alias), new KnowledgeKey(kind, target));
        return prepare(candidate, dataAliases);
    }

    static void commit(Prepared prepared) {
        KUBE_ALIASES.clear();
        KUBE_ALIASES.putAll(prepared.kubeAliases());
        dataAliases = prepared.dataAliases();
        current = prepared.registry();
    }

    private static Prepared prepare(
            Map<KnowledgeKey, KnowledgeKey> kubeAliases,
            Map<KnowledgeKey, KnowledgeKey> dataAliases
    ) {
        Map<KnowledgeKey, KnowledgeKey> copiedKube = Map.copyOf(kubeAliases);
        Map<KnowledgeKey, KnowledgeKey> copiedData = Map.copyOf(dataAliases);
        return new Prepared(copiedKube, copiedData, createCurrent(copiedKube, copiedData));
    }

    static KnowledgeAliasRegistry rawCurrent() {
        return current;
    }

    record Prepared(
            Map<KnowledgeKey, KnowledgeKey> kubeAliases,
            Map<KnowledgeKey, KnowledgeKey> dataAliases,
            KnowledgeAliasRegistry registry
    ) {
        Prepared {
            kubeAliases = Map.copyOf(kubeAliases);
            dataAliases = Map.copyOf(dataAliases);
        }
    }

    public static NamespacedId parseUserId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Knowledge id must not be blank");
        }
        String normalized = value.trim();
        NamespacedId parsed = NamespacedId.tryParse(
                normalized.contains(":") ? normalized : MathMod.MOD_ID + ":" + normalized
        ).orElse(null);
        if (parsed == null) {
            throw new IllegalArgumentException("Invalid knowledge id: " + value);
        }
        return parsed;
    }

    private static KnowledgeAliasRegistry createCurrent() {
        return createCurrent(Map.of(), Map.of());
    }

    private static KnowledgeAliasRegistry createCurrent(
            Map<KnowledgeKey, KnowledgeKey> kubeAliases,
            Map<KnowledgeKey, KnowledgeKey> dataAliases
    ) {
        Map<KnowledgeKey, KnowledgeKey> merged = new LinkedHashMap<>();
        ProgramPresets.talismanPresets().forEach(preset -> {
            NamespacedId canonical = NamespacedId.parse(preset.id());
            merged.put(
                    new KnowledgeKey(
                            KnowledgeKind.THEOREM,
                            NamespacedId.of("minecraft", canonical.path())
                    ),
                    new KnowledgeKey(KnowledgeKind.THEOREM, canonical)
            );
        });
        merged.putAll(kubeAliases);
        merged.putAll(dataAliases);

        KnowledgeAliasRegistry.Builder builder = KnowledgeAliasRegistry.builder();
        merged.forEach((from, to) -> builder.add(from.kind(), from.id(), to.id()));
        return builder.build();
    }
}
