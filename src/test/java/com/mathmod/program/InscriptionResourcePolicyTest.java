package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InscriptionResourcePolicyTest {
    private static final ProgramGraph HOP = graph("hop");
    private static final ProgramGraph DASH = graph("dash");
    private static final List<ResourceSelection> CUSTOM = List.of(new ResourceSelection("diamond", 3));
    private static final List<ResourceSelection> RECOMMENDED = List.of(new ResourceSelection("feather", 1));

    @Test
    void keepsPlayerPreparationWhenTheGraphIsUnchanged() {
        assertEquals(
                CUSTOM,
                InscriptionResourcePolicy.resourcesToPersist(
                        Optional.of(HOP),
                        CUSTOM,
                        HOP,
                        RECOMMENDED
                )
        );
    }

    @Test
    void usesRecommendationsForANewGraph() {
        assertEquals(
                RECOMMENDED,
                InscriptionResourcePolicy.resourcesToPersist(
                        Optional.of(HOP),
                        CUSTOM,
                        DASH,
                        RECOMMENDED
                )
        );
    }

    @Test
    void usesRecommendationsForABlankTalisman() {
        assertEquals(
                RECOMMENDED,
                InscriptionResourcePolicy.resourcesToPersist(
                        Optional.empty(),
                        List.of(),
                        HOP,
                        RECOMMENDED
                )
        );
    }

    private static ProgramGraph graph(String id) {
        return new ProgramGraph(
                List.of(new ProgramNode(id, "mathmod:test")),
                List.of(),
                id,
                16
        );
    }
}
