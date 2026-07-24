package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManuscriptSnapshotBuilderTest {
    private static final ManuscriptDefinitionSource BUILT_IN = source(
            ManuscriptSourceLayer.BUILT_IN,
            0,
            "mathmod"
    );
    private static final ManuscriptDefinitionSource KUBE = source(
            ManuscriptSourceLayer.KUBEJS,
            0,
            "server_scripts/mathmod.js"
    );
    private static final ManuscriptDefinitionSource DATA = source(
            ManuscriptSourceLayer.DATA_PACK,
            4,
            "example_pack"
    );

    @Test
    void precedenceIsDeterministicAndReportsTheShadowedSource() {
        TraditionDefinition builtIn = tradition("surveyors", "tradition.builtin");
        TraditionDefinition kube = tradition("surveyors", "tradition.kube");
        TraditionDefinition data = tradition("surveyors", "tradition.data");

        ManuscriptSnapshotBuildResult result = new ManuscriptSnapshotBuilder()
                .addTradition(data, DATA)
                .addTradition(builtIn, BUILT_IN)
                .addTradition(kube, KUBE)
                .build();

        TraditionDefinition selected = result.snapshot().tradition(id("surveyors")).orElseThrow();
        assertEquals("tradition.data", selected.nameTranslationKey());
        assertEquals(DATA, result.snapshot().traditionSource(id("surveyors")).orElseThrow());
        assertEquals(2, result.diagnostics().stream()
                .filter(diagnostic -> diagnostic.code() == ManuscriptDiagnostic.Code.SHADOWED)
                .count());
    }

    @Test
    void invalidCrossReferencesAreOmittedWithoutDiscardingValidRecords() {
        TraditionDefinition tradition = tradition("surveyors", "tradition.surveyors");
        ManuscriptSnapshotBuildResult result = new ManuscriptSnapshotBuilder(
                theorem -> theorem.equals(id("known_theorem"))
        )
                .addTradition(tradition, BUILT_IN)
                .addManuscript(manuscript("valid", tradition.id(), id("known_theorem")), BUILT_IN)
                .addManuscript(manuscript("bad_tradition", id("missing"), null), BUILT_IN)
                .addManuscript(manuscript("bad_theorem", tradition.id(), id("missing")), BUILT_IN)
                .build();

        assertTrue(result.publishable());
        assertEquals(List.of(id("valid")), result.snapshot().manuscripts().stream()
                .map(ManuscriptDefinition::id)
                .toList());
        assertTrue(hasDiagnostic(result, ManuscriptDiagnostic.Code.UNKNOWN_TRADITION));
        assertTrue(hasDiagnostic(result, ManuscriptDiagnostic.Code.UNKNOWN_THEOREM));
    }

    @Test
    void aliasesAreFlattenedAndMigrationDoesNotMutatePersistence() {
        TraditionDefinition tradition = tradition("surveyors", "tradition.surveyors");
        ManuscriptDefinition current = manuscript("current", tradition.id(), null);
        ManuscriptSnapshot snapshot = new ManuscriptSnapshotBuilder()
                .addTradition(tradition, BUILT_IN)
                .addManuscript(current, BUILT_IN)
                .addAlias(alias("old", "middle"), DATA)
                .addAlias(alias("middle", "current"), DATA)
                .build()
                .snapshot();

        ManuscriptReferenceMigration migration = snapshot.migrateReference(id("old"));
        assertEquals(ManuscriptReferenceMigration.Status.ALIASED, migration.status());
        assertEquals(id("current"), migration.canonicalId().orElseThrow());
        assertTrue(migration.requiresPersistenceUpdate());
        assertEquals(current, snapshot.manuscript(id("old")).orElseThrow());
        assertEquals(2, snapshot.aliasCount());
    }

    @Test
    void cyclesAndMissingAliasTargetsAreRejectedIndividually() {
        TraditionDefinition tradition = tradition("surveyors", "tradition.surveyors");
        ManuscriptSnapshotBuildResult result = new ManuscriptSnapshotBuilder()
                .addTradition(tradition, BUILT_IN)
                .addManuscript(manuscript("current", tradition.id(), null), BUILT_IN)
                .addAlias(alias("cycle_a", "cycle_b"), DATA)
                .addAlias(alias("cycle_b", "cycle_a"), DATA)
                .addAlias(alias("lost", "missing"), DATA)
                .build();

        assertTrue(result.publishable());
        assertEquals(0, result.snapshot().aliasCount());
        assertTrue(hasDiagnostic(result, ManuscriptDiagnostic.Code.ALIAS_CYCLE));
        assertTrue(hasDiagnostic(result, ManuscriptDiagnostic.Code.ALIAS_MISSING_TARGET));
    }

    @Test
    void ambiguousRecordsAtTheSameSourceAreOmitted() {
        TraditionDefinition first = tradition("surveyors", "tradition.first");
        TraditionDefinition second = tradition("surveyors", "tradition.second");

        ManuscriptSnapshotBuildResult result = new ManuscriptSnapshotBuilder()
                .addTradition(first, DATA)
                .addTradition(second, DATA)
                .build();

        assertTrue(result.publishable());
        assertTrue(result.snapshot().traditions().isEmpty());
        assertTrue(hasDiagnostic(result, ManuscriptDiagnostic.Code.AMBIGUOUS_SOURCE));
    }

    @Test
    void higherPrecedenceRecordSupersedesAnAmbiguousLowerLayer() {
        TraditionDefinition first = tradition("surveyors", "tradition.first");
        TraditionDefinition second = tradition("surveyors", "tradition.second");
        TraditionDefinition data = tradition("surveyors", "tradition.data");

        ManuscriptSnapshotBuildResult result = new ManuscriptSnapshotBuilder()
                .addTradition(first, BUILT_IN)
                .addTradition(second, BUILT_IN)
                .addTradition(data, DATA)
                .build();

        assertEquals(data, result.snapshot().tradition(data.id()).orElseThrow());
    }

    @Test
    void storeKeepsPreviousSnapshotWhenCandidateIsNotPublishable() {
        TraditionDefinition tradition = tradition("surveyors", "tradition.surveyors");
        ManuscriptSnapshotBuildResult valid = new ManuscriptSnapshotBuilder()
                .addTradition(tradition, BUILT_IN)
                .build();
        ManuscriptSnapshotStore store = new ManuscriptSnapshotStore();

        assertTrue(store.publish(valid));
        assertFalse(store.publish(new ManuscriptSnapshotBuildResult(
                ManuscriptSnapshot.empty(),
                List.of(),
                false
        )));
        assertEquals(tradition, store.snapshot().tradition(tradition.id()).orElseThrow());
    }

    @Test
    void snapshotAndDefinitionsDefensivelyCopyCollections() {
        List<String> pages = new ArrayList<>(List.of("manuscript.test.page.1"));
        ManuscriptDefinition definition = new ManuscriptDefinition(
                ManuscriptSchema.CURRENT_VERSION,
                id("record"),
                id("surveyors"),
                "manuscript.test.title",
                pages,
                id("paper"),
                ManuscriptRarity.COMMON,
                Optional.empty(),
                Optional.empty()
        );
        pages.add("manuscript.test.page.2");

        assertEquals(1, definition.pageTranslationKeys().size());
        assertThrows(
                UnsupportedOperationException.class,
                () -> definition.pageTranslationKeys().add("another.page")
        );
    }

    @Test
    void descriptiveRecordsContainNoGrantOrExecutionField() {
        List<String> components = List.of(ManuscriptDefinition.class.getRecordComponents())
                .stream()
                .map(component -> component.getName())
                .toList();

        assertFalse(components.contains("grants"));
        assertFalse(components.contains("executor"));
        assertFalse(components.contains("program"));
        assertFalse(components.contains("command"));
    }

    private static boolean hasDiagnostic(
            ManuscriptSnapshotBuildResult result,
            ManuscriptDiagnostic.Code code
    ) {
        return result.diagnostics().stream().anyMatch(diagnostic -> diagnostic.code() == code);
    }

    private static TraditionDefinition tradition(String path, String nameKey) {
        return new TraditionDefinition(
                ManuscriptSchema.CURRENT_VERSION,
                id(path),
                nameKey,
                nameKey + ".summary",
                id("compass")
        );
    }

    private static ManuscriptDefinition manuscript(
            String path,
            NamespacedId tradition,
            NamespacedId theorem
    ) {
        return new ManuscriptDefinition(
                ManuscriptSchema.CURRENT_VERSION,
                id(path),
                tradition,
                "manuscript." + path + ".title",
                List.of("manuscript." + path + ".page.1"),
                id("paper"),
                ManuscriptRarity.COMMON,
                Optional.of(id("lore/" + path)),
                Optional.ofNullable(theorem)
        );
    }

    private static ManuscriptAliasDefinition alias(String from, String to) {
        return new ManuscriptAliasDefinition(
                ManuscriptSchema.CURRENT_VERSION,
                id(from),
                id(to)
        );
    }

    private static ManuscriptDefinitionSource source(
            ManuscriptSourceLayer layer,
            int priority,
            String name
    ) {
        return new ManuscriptDefinitionSource(layer, priority, name);
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
