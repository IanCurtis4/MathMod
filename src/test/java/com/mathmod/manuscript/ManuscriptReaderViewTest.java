package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManuscriptReaderViewTest {
    @Test
    void aliasResolvesToCurrentDisplayDataWithoutMutatingTheRequestedId() {
        TraditionDefinition tradition = new TraditionDefinition(1, id("tradition"), "test.tradition", "test.summary", id("compass"));
        ManuscriptDefinition manuscript = new ManuscriptDefinition(1, id("current"), tradition.id(), "test.title",
                List.of("test.page.one"), id("paper"), ManuscriptRarity.UNCOMMON, Optional.of(id("lore/current")), Optional.of(id("theorem")));
        ManuscriptReaderView view = ManuscriptReaderView.from(id("old"), new ManuscriptSnapshotBuilder()
                .addTradition(tradition, source())
                .addManuscript(manuscript, source())
                .addAlias(new ManuscriptAliasDefinition(1, id("old"), id("current")), source())
                .build().snapshot());

        assertTrue(view.available());
        assertEquals(id("old"), view.requestedId());
        assertEquals(id("current"), view.canonicalId().orElseThrow());
        assertEquals(List.of("test.page.one"), view.pageTranslationKeys());
    }

    @Test
    void missingRecordHasNoPagesOrNavigationTargets() {
        ManuscriptReaderView view = ManuscriptReaderView.from(id("missing"), ManuscriptSnapshot.empty());
        assertFalse(view.available());
        assertTrue(view.pageTranslationKeys().isEmpty());
        assertTrue(view.patchouliEntry().isEmpty());
        assertTrue(view.theoremId().isEmpty());
    }

    private static NamespacedId id(String path) { return NamespacedId.of("mathmod", path); }
    private static ManuscriptDefinitionSource source() {
        return new ManuscriptDefinitionSource(ManuscriptSourceLayer.BUILT_IN, 0, "test");
    }
}
