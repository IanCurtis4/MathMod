package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KnowledgeDefinitionRegistryTest {
    @Test
    void dataPackOverridesKubeAndBuiltInDefinitionsByStableId() {
        EpiphanyDefinition builtIn = epiphany("shared", "test.title.builtin", "rune_a");
        EpiphanyDefinition kube = epiphany("shared", "test.title.kube", "rune_b");
        EpiphanyDefinition data = epiphany("shared", "test.title.data", "rune_c");
        KnowledgeDefinitionRegistry registry =
                new KnowledgeDefinitionRegistry(List.of(builtIn), List.of());

        registry.registerKube(kube);
        registry.publishData(Map.of(data.id(), data), Map.of());

        assertEquals(
                "test.title.data",
                registry.snapshot().epiphany(data.id()).orElseThrow().titleTranslationKey()
        );
        assertTrue(registry.snapshot()
                .requirementFor(KnowledgeKind.RUNE, id("rune_c"))
                .isPresent());
        assertFalse(registry.snapshot()
                .requirementFor(KnowledgeKind.RUNE, id("rune_a"))
                .isPresent());
        assertFalse(registry.snapshot()
                .requirementFor(KnowledgeKind.RUNE, id("rune_b"))
                .isPresent());
    }

    @Test
    void grantsAutomaticallyBecomeConstructionRequirements() {
        DiscoveryDefinition discovery = new DiscoveryDefinition(
                id("record"),
                id("manuscript"),
                "test.discovery.title",
                id("lore/record"),
                List.of(
                        new KnowledgeGrant(KnowledgeKind.RUNE, id("rune")),
                        new KnowledgeGrant(KnowledgeKind.THEOREM, id("theorem"))
                )
        );
        KnowledgeDefinitionSnapshot snapshot =
                new KnowledgeDefinitionSnapshot(List.of(), List.of(discovery));

        KnowledgeRequirement rune = snapshot
                .requirementFor(KnowledgeKind.RUNE, id("rune"))
                .orElseThrow();
        assertEquals(KnowledgeKind.DISCOVERY, rune.kind());
        assertEquals(discovery.id(), rune.id());
        assertEquals("knowledge.mathmod.route.record", rune.routeTranslationKey());
        assertTrue(snapshot
                .requirementFor(KnowledgeKind.THEOREM, id("theorem"))
                .isPresent());
    }

    private static EpiphanyDefinition epiphany(
            String path,
            String titleKey,
            String rune
    ) {
        return new EpiphanyDefinition(
                id(path),
                titleKey,
                id(path + "_correlation"),
                List.of(
                        new MaterialStudyRequirement(id("material_one"), 1, 1),
                        new MaterialStudyRequirement(id("material_two"), 2, 1)
                ),
                List.of(new KnowledgeGrant(KnowledgeKind.RUNE, id(rune)))
        );
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
