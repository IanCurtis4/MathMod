package com.mathmod.acquisition;

import com.mathmod.manuscript.ManuscriptDefinition;
import com.mathmod.manuscript.ManuscriptDefinitionSource;
import com.mathmod.manuscript.ManuscriptRarity;
import com.mathmod.manuscript.ManuscriptSchema;
import com.mathmod.manuscript.ManuscriptSnapshot;
import com.mathmod.manuscript.ManuscriptSnapshotBuilder;
import com.mathmod.manuscript.ManuscriptSourceLayer;
import com.mathmod.manuscript.TraditionDefinition;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManuscriptAcquisitionSnapshotBuilderTest {
    private static final ManuscriptDefinitionSource BUILT_IN = new ManuscriptDefinitionSource(
            ManuscriptSourceLayer.BUILT_IN, 0, "mathmod"
    );

    @Test
    void canonicalizesAliasesBeforePublishingLootCandidates() {
        ManuscriptSnapshot manuscripts = manuscriptSnapshot();
        ManuscriptAcquisitionBuildResult result = new ManuscriptAcquisitionSnapshotBuilder(manuscripts)
                .add(acquisition("record", "old_record", List.of(id("village")), 4, Optional.empty()), BUILT_IN)
                .build();

        ManuscriptAcquisitionSnapshot.Candidate candidate = result.snapshot().lootPool(id("village")).getFirst();
        assertEquals(id("record"), candidate.manuscriptId());
        assertTrue(result.publishable());
    }

    @Test
    void omitsUnknownManuscriptsAndKeepsValidCandidates() {
        ManuscriptAcquisitionBuildResult result = new ManuscriptAcquisitionSnapshotBuilder(manuscriptSnapshot())
                .add(acquisition("valid", "record", List.of(id("village")), 1, Optional.empty()), BUILT_IN)
                .add(acquisition("missing", "missing", List.of(id("village_2")), 1, Optional.empty()), BUILT_IN)
                .build();

        assertEquals(1, result.snapshot().candidates().size());
        assertTrue(result.diagnostics().stream().anyMatch(diagnostic ->
                diagnostic.code() == AcquisitionDiagnostic.Code.UNKNOWN_MANUSCRIPT));
    }

    @Test
    void poolCollisionUsesLexicographicallyFirstAcquisitionId() {
        ManuscriptAcquisitionBuildResult result = new ManuscriptAcquisitionSnapshotBuilder(manuscriptSnapshot())
                .add(acquisition("zeta", "record", List.of(id("village")), 1, Optional.empty()), BUILT_IN)
                .add(acquisition("alpha", "record", List.of(id("village")), 1, Optional.empty()), BUILT_IN)
                .build();

        assertEquals(List.of(id("alpha")), result.snapshot().candidates().stream()
                .map(ManuscriptAcquisitionSnapshot.Candidate::id)
                .toList());
        assertTrue(result.diagnostics().stream().anyMatch(diagnostic ->
                diagnostic.code() == AcquisitionDiagnostic.Code.POOL_COLLISION));
    }

    @Test
    void configDisablesTradesWhenProfessionIsDisabled() {
        ManuscriptAcquisitionConfig config = new ManuscriptAcquisitionConfig(
                true, false, true, false, SurplusPolicy.TRADE_BACK, 1, 3
        );

        assertFalse(config.effectiveTradesEnabled());
        assertTrue(ManuscriptAcquisitionConfig.defaults().effectiveTradesEnabled());
    }

    private static ManuscriptSnapshot manuscriptSnapshot() {
        TraditionDefinition tradition = new TraditionDefinition(
                ManuscriptSchema.CURRENT_VERSION,
                id("surveyors"),
                "tradition.test.name",
                "tradition.test.summary",
                id("compass")
        );
        ManuscriptDefinition manuscript = new ManuscriptDefinition(
                ManuscriptSchema.CURRENT_VERSION,
                id("record"),
                tradition.id(),
                "manuscript.test.title",
                List.of("manuscript.test.page"),
                id("paper"),
                ManuscriptRarity.COMMON,
                Optional.empty(),
                Optional.empty()
        );
        return new ManuscriptSnapshotBuilder()
                .addTradition(tradition, BUILT_IN)
                .addManuscript(manuscript, BUILT_IN)
                .addAlias(new com.mathmod.manuscript.ManuscriptAliasDefinition(
                        ManuscriptSchema.CURRENT_VERSION, id("old_record"), id("record")
                ), BUILT_IN)
                .build()
                .snapshot();
    }

    private static ManuscriptAcquisitionDefinition acquisition(
            String id,
            String manuscript,
            List<NamespacedId> pools,
            int weight,
            Optional<ManuscriptTradeDefinition> trade
    ) {
        return new ManuscriptAcquisitionDefinition(
                1,
                id(id),
                id(manuscript),
                pools,
                weight,
                trade
        );
    }

    private static NamespacedId id(String path) {
        return NamespacedId.of("mathmod", path);
    }
}
