package com.mathmod.knowledge;

import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class KnowledgeDefinitionReloadListenerTest {
    @BeforeEach void bootstrapRunes() { MathModRuneBootstrap.bootstrap(); }
    @AfterEach void restoreBuiltInData() { KnowledgeDefinitionReloadListener.CandidatePublication.apply(result(Map.of(), Map.of(), Map.of())); }

    @Test void sameResourceValidThenMalformedRetainsBothExactSnapshots() {
        KnowledgeDefinitionReloadListener.CandidatePublication.apply(validVitalReplacement(3));
        KnowledgeDefinitionSnapshot definitions = KnowledgeDefinitions.snapshot(); KnowledgeAliasRegistry aliases = KnowledgeAliases.current();
        KnowledgeDefinitionReloadListener.CandidatePublication.apply(result(Map.of(), Map.of(), Map.of(), "definition mathmod:epiphanies/vital_correspondence: Unsupported schema_version"));
        assertSame(definitions, KnowledgeDefinitions.snapshot()); assertSame(aliases, KnowledgeAliases.current()); assertEquals(3, successfulCasts());
    }

    @Test void malformedJsonPathAndRuntimeValidationRetainBothSnapshots() {
        assertRejected(result(Map.of(), Map.of(), Map.of(), "definition mathmod:epiphanies/vital_correspondence: malformed JSON"));
        assertRejected(result(Map.of(), Map.of(), Map.of(), "definition mathmod:epiphanies/.json: Invalid knowledge resource path"));
        assertRejected(result(Map.of(KnowledgeDefinitions.VITAL_CORRESPONDENCE, epiphany(KnowledgeDefinitions.VITAL_CORRESPONDENCE, "mathmod:missing_rune", 3)), Map.of(), Map.of()));
    }

    @Test void invalidAliasAndGlobalLimitRetainBothSnapshots() {
        KnowledgeKey self = new KnowledgeKey(KnowledgeKind.RUNE, NamespacedId.of("mathmod", "self")); assertRejected(result(Map.of(), Map.of(), Map.of(self, self)));
        Map<NamespacedId, EpiphanyDefinition> tooMany = new LinkedHashMap<>();
        for (int index = 0; index <= 256; index++) { NamespacedId id = NamespacedId.of("mathmod", "limit_" + index); tooMany.put(id, epiphany(id, "mathmod:number_sin", 2)); }
        assertRejected(result(tooMany, Map.of(), Map.of()));
    }

    @Test void validRemovalPublishesFallbackAndCandidateMutationCannotLeak() {
        Map<NamespacedId, EpiphanyDefinition> mutable = new LinkedHashMap<>(); EpiphanyDefinition replacement = epiphany(KnowledgeDefinitions.VITAL_CORRESPONDENCE, "mathmod:vital_infusion_plan", 3);
        mutable.put(replacement.id(), replacement); var candidate = result(mutable, Map.of(), Map.of()); mutable.clear();
        KnowledgeDefinitionReloadListener.CandidatePublication.apply(candidate); assertEquals(3, successfulCasts());
        KnowledgeDefinitionReloadListener.CandidatePublication.apply(result(Map.of(), Map.of(), Map.of())); assertEquals(2, successfulCasts());
    }

    private static void assertRejected(KnowledgeDefinitionReloadListener.CandidatePublication.LoadResult rejected) {
        KnowledgeDefinitionReloadListener.CandidatePublication.apply(validVitalReplacement(3)); KnowledgeDefinitionSnapshot definitions = KnowledgeDefinitions.snapshot(); KnowledgeAliasRegistry aliases = KnowledgeAliases.current();
        KnowledgeDefinitionReloadListener.CandidatePublication.apply(rejected); assertSame(definitions, KnowledgeDefinitions.snapshot()); assertSame(aliases, KnowledgeAliases.current()); assertEquals(3, successfulCasts());
    }
    private static KnowledgeDefinitionReloadListener.CandidatePublication.LoadResult validVitalReplacement(int casts) { return result(Map.of(KnowledgeDefinitions.VITAL_CORRESPONDENCE, epiphany(KnowledgeDefinitions.VITAL_CORRESPONDENCE, "mathmod:vital_infusion_plan", casts)), Map.of(), Map.of()); }
    private static KnowledgeDefinitionReloadListener.CandidatePublication.LoadResult result(Map<NamespacedId, EpiphanyDefinition> epiphanies, Map<NamespacedId, DiscoveryDefinition> discoveries, Map<KnowledgeKey, KnowledgeKey> aliases, String... failures) { return new KnowledgeDefinitionReloadListener.CandidatePublication.LoadResult(epiphanies, discoveries, aliases, List.of(failures)); }
    private static int successfulCasts() { return KnowledgeDefinitions.epiphany(KnowledgeDefinitions.VITAL_CORRESPONDENCE).orElseThrow().studies().getFirst().successfulCasts(); }
    private static EpiphanyDefinition epiphany(NamespacedId id, String rune, int casts) { return new EpiphanyDefinition(id, "test." + id.path(), NamespacedId.of("mathmod", "correlation_" + id.path()), List.of(new MaterialStudyRequirement(NamespacedId.of("mathmod", "one_" + id.path()), 1, casts), new MaterialStudyRequirement(NamespacedId.of("mathmod", "two_" + id.path()), 2, 2)), List.of(new KnowledgeGrant(KnowledgeKind.RUNE, NamespacedId.parse(rune)))); }
}
