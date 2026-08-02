package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KnowledgeReloadPublicationTest {
    @AfterEach
    void restoreData() {
        KnowledgeReloadPublication.publish(Map.of(), Map.of(), Map.of());
    }

    @Test
    void definitionPreparationFailureLeavesBothLiveObjectsUntouched() {
        KnowledgeDefinitionSnapshot definitions = KnowledgeDefinitions.snapshot();
        KnowledgeAliasRegistry aliases = KnowledgeAliases.current();
        DiscoveryDefinition collision = new DiscoveryDefinition(
                NamespacedId.of("mathmod", "collision"),
                KnowledgeDefinitions.ROTATED_HORIZON,
                "test.collision", NamespacedId.of("mathmod", "lore/collision"),
                java.util.List.of(new KnowledgeGrant(
                        KnowledgeKind.THEOREM, NamespacedId.of("mathmod", "harmonic_step"))));

        try {
            KnowledgeReloadPublication.publish(Map.of(), Map.of(collision.id(), collision), Map.of());
        } catch (IllegalArgumentException expected) {
            // The prepared definition snapshot is rejected before the paired swap.
        }

        assertSame(definitions, KnowledgeDefinitions.snapshot());
        assertSame(aliases, KnowledgeAliases.current());
    }

    @Test
    void aliasPreparationFailureLeavesBothLiveObjectsUntouched() {
        KnowledgeDefinitionSnapshot definitions = KnowledgeDefinitions.snapshot();
        KnowledgeAliasRegistry aliases = KnowledgeAliases.current();
        KnowledgeKey self = new KnowledgeKey(KnowledgeKind.RUNE, NamespacedId.of("mathmod", "self"));

        try {
            KnowledgeReloadPublication.publish(Map.of(), Map.of(), Map.of(self, self));
        } catch (IllegalArgumentException expected) {
            // The prepared alias snapshot is rejected before the paired swap.
        }

        assertSame(definitions, KnowledgeDefinitions.snapshot());
        assertSame(aliases, KnowledgeAliases.current());
    }

    @Test
    void successfulPublicationReplacesBothReaderAuthoritiesAsOneGeneration() {
        KnowledgeDefinitionSnapshot beforeDefinitions = KnowledgeDefinitions.snapshot();
        KnowledgeAliasRegistry beforeAliases = KnowledgeAliases.current();
        KnowledgeKey from = new KnowledgeKey(KnowledgeKind.THEOREM, NamespacedId.of("minecraft", "paired_test"));
        KnowledgeKey to = new KnowledgeKey(KnowledgeKind.THEOREM, NamespacedId.of("mathmod", "paired_test"));

        KnowledgeReloadPublication.publish(Map.of(), Map.of(), Map.of(from, to));

        KnowledgeReloadPublication.Generation generation = KnowledgeReloadPublication.currentGeneration();
        assertNotSame(beforeDefinitions, KnowledgeDefinitions.snapshot());
        assertNotSame(beforeAliases, KnowledgeAliases.current());
        assertSame(generation.definitions(), KnowledgeDefinitions.snapshot());
        assertSame(generation.aliases(), KnowledgeAliases.current());
    }

    @Test
    void initialReadersAlreadyResolveOnePairedHolder() {
        KnowledgeReloadPublication.Generation generation = KnowledgeReloadPublication.currentGeneration();
        assertSame(generation.definitions(), KnowledgeDefinitions.snapshot());
        assertSame(generation.aliases(), KnowledgeAliases.current());
    }

    @Test
    void epiphanyLimitsAccept256AndReject257WithoutChangingEitherAuthority() {
        Map<NamespacedId, EpiphanyDefinition> accepted = epiphanies(256);
        KnowledgeReloadPublication.publish(accepted, Map.of(), Map.of());
        KnowledgeDefinitionSnapshot definitions = KnowledgeDefinitions.snapshot();
        KnowledgeAliasRegistry aliases = KnowledgeAliases.current();
        assertEquals(256, accepted.size());
        Map<NamespacedId, EpiphanyDefinition> rejected = epiphanies(257);
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> KnowledgeReloadPublication.publish(rejected, Map.of(), Map.of()));
        assertSame(definitions, KnowledgeDefinitions.snapshot());
        assertSame(aliases, KnowledgeAliases.current());
    }

    @Test
    void discoveryLimitsAccept1024AndReject1025WithoutChangingEitherAuthority() {
        Map<NamespacedId, DiscoveryDefinition> accepted = discoveries(1024);
        KnowledgeReloadPublication.publish(Map.of(), accepted, Map.of());
        KnowledgeDefinitionSnapshot definitions = KnowledgeDefinitions.snapshot();
        KnowledgeAliasRegistry aliases = KnowledgeAliases.current();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> KnowledgeReloadPublication.publish(Map.of(), discoveries(1025), Map.of()));
        assertSame(definitions, KnowledgeDefinitions.snapshot());
        assertSame(aliases, KnowledgeAliases.current());
    }

    @Test
    void aliasLimitsAccept4096AndReject4097WithoutChangingEitherAuthority() {
        Map<KnowledgeKey, KnowledgeKey> accepted = aliases(4096);
        KnowledgeReloadPublication.publish(Map.of(), Map.of(), accepted);
        KnowledgeDefinitionSnapshot definitions = KnowledgeDefinitions.snapshot();
        KnowledgeAliasRegistry aliases = KnowledgeAliases.current();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> KnowledgeReloadPublication.publish(Map.of(), Map.of(), aliases(4097)));
        assertSame(definitions, KnowledgeDefinitions.snapshot());
        assertSame(aliases, KnowledgeAliases.current());
    }

    @Test
    void kubeDefinitionsAndAliasesRetainPrecedenceThroughPairedReaders() {
        NamespacedId id = NamespacedId.of("mathmod", "paired_kube");
        EpiphanyDefinition kube = epiphany(id, "test.kube");
        EpiphanyDefinition data = epiphany(id, "test.data");
        KnowledgeDefinitions.registerKube(kube);
        KnowledgeReloadPublication.publish(Map.of(id, data), Map.of(), Map.of());
        assertEquals("test.data", KnowledgeDefinitions.epiphany(id).orElseThrow().titleTranslationKey());

        NamespacedId from = NamespacedId.of("mathmod", "paired_old");
        NamespacedId kubeTarget = NamespacedId.of("mathmod", "paired_kube_target");
        NamespacedId dataTarget = NamespacedId.of("mathmod", "paired_data_target");
        KnowledgeAliases.registerKube(KnowledgeKind.THEOREM, from, kubeTarget);
        KnowledgeReloadPublication.publish(Map.of(), Map.of(), Map.of(
                new KnowledgeKey(KnowledgeKind.THEOREM, from), new KnowledgeKey(KnowledgeKind.THEOREM, dataTarget)));
        assertEquals(dataTarget, KnowledgeAliases.current().resolve(KnowledgeKind.THEOREM, from));
    }

    @Test
    void opposingKubeAndReloadOperationsCompleteWithOneCoordinatorOrder() throws Exception {
        CyclicBarrier barrier = new CyclicBarrier(2);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var kube = executor.submit(() -> {
                barrier.await();
                KnowledgeAliases.registerKube(KnowledgeKind.THEOREM,
                        NamespacedId.of("mathmod", "lock_old"), NamespacedId.of("mathmod", "lock_new"));
                return KnowledgeReloadPublication.currentGeneration();
            });
            var reload = executor.submit(() -> {
                barrier.await();
                KnowledgeReloadPublication.publish(Map.of(), Map.of(), Map.of());
                return KnowledgeReloadPublication.currentGeneration();
            });
            kube.get(5, TimeUnit.SECONDS);
            reload.get(5, TimeUnit.SECONDS);
            KnowledgeReloadPublication.Generation finalGeneration = KnowledgeReloadPublication.currentGeneration();
            assertSame(finalGeneration.definitions(), KnowledgeDefinitions.snapshot());
            assertSame(finalGeneration.aliases(), KnowledgeAliases.current());
        }
    }

    private static Map<NamespacedId, EpiphanyDefinition> epiphanies(int count) {
        Map<NamespacedId, EpiphanyDefinition> values = new LinkedHashMap<>();
        for (int index = 0; index < count; index++) {
            NamespacedId id = NamespacedId.of("mathmod", "limit_epiphany_" + index);
            values.put(id, epiphany(id, "test.epiphany." + index));
        }
        return values;
    }

    private static Map<NamespacedId, DiscoveryDefinition> discoveries(int count) {
        Map<NamespacedId, DiscoveryDefinition> values = new LinkedHashMap<>();
        for (int index = 0; index < count; index++) {
            NamespacedId id = NamespacedId.of("mathmod", "limit_discovery_" + index);
            values.put(id, new DiscoveryDefinition(id, NamespacedId.of("mathmod", "manuscript_" + index),
                    "test.discovery." + index, NamespacedId.of("mathmod", "lore/" + index),
                    List.of(new KnowledgeGrant(KnowledgeKind.THEOREM, NamespacedId.of("mathmod", "harmonic_step")))));
        }
        return values;
    }

    private static Map<KnowledgeKey, KnowledgeKey> aliases(int count) {
        Map<KnowledgeKey, KnowledgeKey> values = new LinkedHashMap<>();
        for (int index = 0; index < count; index++) {
            values.put(new KnowledgeKey(KnowledgeKind.THEOREM, NamespacedId.of("mathmod", "alias_" + index)),
                    new KnowledgeKey(KnowledgeKind.THEOREM, NamespacedId.of("mathmod", "target_" + index)));
        }
        return values;
    }

    private static EpiphanyDefinition epiphany(NamespacedId id, String title) {
        return new EpiphanyDefinition(id, title, NamespacedId.of("mathmod", "correlation_" + id.path()),
                List.of(new MaterialStudyRequirement(NamespacedId.of("mathmod", "material_" + id.path()), 1, 1),
                        new MaterialStudyRequirement(NamespacedId.of("mathmod", "material_two_" + id.path()), 2, 1)),
                List.of(new KnowledgeGrant(KnowledgeKind.THEOREM, NamespacedId.of("mathmod", "harmonic_step"))));
    }
}
