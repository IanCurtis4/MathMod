package com.mathmod.kubejs;

import com.mathmod.manuscript.ManuscriptDefinition;
import com.mathmod.manuscript.ManuscriptRarity;
import com.mathmod.manuscript.ManuscriptSchema;
import com.mathmod.manuscript.ManuscriptSourceLayer;
import com.mathmod.manuscript.TraditionDefinition;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KubeJsManuscriptDeclarationStoreTest {
    @Test
    void freezeReturnsOneImmutableStartupGeneration() {
        KubeJsManuscriptDeclarationStore store = new KubeJsManuscriptDeclarationStore();
        TraditionDefinition tradition = tradition("surveyors");
        store.register(tradition);
        store.register(manuscript("ridge", tradition.id()));

        KubeJsManuscriptDeclarationStore.Snapshot first = store.freeze();
        KubeJsManuscriptDeclarationStore.Snapshot second = store.freeze();

        assertEquals(first, second);
        assertEquals(KubeJsManuscriptDeclarationStore.State.FROZEN, store.state());
        assertEquals(ManuscriptSourceLayer.KUBEJS, first.source().layer());
        assertThrows(UnsupportedOperationException.class, () -> first.traditions().clear());
        assertThrows(IllegalStateException.class, () -> store.register(tradition("late")));
    }

    @Test
    void duplicateIdsAreRejectedInsteadOfDependingOnScriptOrder() {
        KubeJsManuscriptDeclarationStore store = new KubeJsManuscriptDeclarationStore();
        store.register(tradition("surveyors"));

        assertThrows(IllegalArgumentException.class, () -> store.register(tradition("surveyors")));
    }

    private static TraditionDefinition tradition(String path) {
        return new TraditionDefinition(
                ManuscriptSchema.CURRENT_VERSION,
                id(path),
                "tradition.test." + path + ".name",
                "tradition.test." + path + ".summary",
                id("paper")
        );
    }

    private static ManuscriptDefinition manuscript(String path, NamespacedId tradition) {
        return new ManuscriptDefinition(
                ManuscriptSchema.CURRENT_VERSION,
                id(path),
                tradition,
                "manuscript.test." + path + ".title",
                List.of("manuscript.test." + path + ".page.1"),
                id("paper"),
                ManuscriptRarity.COMMON,
                Optional.empty(),
                Optional.empty()
        );
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("test", path);
    }
}
