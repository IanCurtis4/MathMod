package com.mathmod.acquisition;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManuscriptLootPlannerTest {
    private static final NamespacedId POOL = NamespacedId.of("mathmod", "village_cartographer");
    private static final NamespacedId MANUSCRIPT = NamespacedId.of("mathmod", "bound_measure");

    @Test
    void selectsOneCanonicalManuscriptWhenTheChanceAndPoolPass() {
        Optional<NamespacedId> selected = ManuscriptLootPlanner.selectManuscript(
                snapshotWithOneCandidate(),
                ManuscriptAcquisitionConfig.defaults(),
                POOL,
                new FixedRandom(0)
        );

        assertEquals(Optional.of(MANUSCRIPT), selected);
    }

    @Test
    void doesNotSelectWhenTheConfiguredChanceMissesOrLootIsDisabled() {
        ManuscriptAcquisitionSnapshot snapshot = snapshotWithOneCandidate();
        ManuscriptAcquisitionConfig disabled = new ManuscriptAcquisitionConfig(
                false, true, true, false, SurplusPolicy.KEEP, 1, 3
        );

        assertTrue(ManuscriptLootPlanner.selectManuscript(
                snapshot, ManuscriptAcquisitionConfig.defaults(), POOL, new FixedRandom(2)
        ).isEmpty());
        assertTrue(ManuscriptLootPlanner.selectManuscript(
                snapshot, disabled, POOL, new FixedRandom(0)
        ).isEmpty());
    }

    @Test
    void doesNotFallBackWhenThePoolIsEmpty() {
        assertTrue(ManuscriptLootPlanner.selectManuscript(
                ManuscriptAcquisitionSnapshot.empty(),
                ManuscriptAcquisitionConfig.defaults(),
                POOL,
                new FixedRandom(0)
        ).isEmpty());
    }

    private static ManuscriptAcquisitionSnapshot snapshotWithOneCandidate() {
        NamespacedId candidateId = NamespacedId.of("mathmod", "bound_measure_chest");
        ManuscriptAcquisitionSnapshot.Candidate candidate = new ManuscriptAcquisitionSnapshot.Candidate(
                candidateId, MANUSCRIPT, List.of(POOL), 1, Optional.empty()
        );
        return new ManuscriptAcquisitionSnapshot(
                Map.of(candidateId, candidate),
                Map.of(POOL, List.of(candidate)),
                Map.of()
        );
    }

    private static final class FixedRandom implements RandomGenerator {
        private final int nextInt;

        private FixedRandom(int nextInt) {
            this.nextInt = nextInt;
        }

        @Override
        public long nextLong() {
            return nextInt;
        }

        @Override
        public int nextInt(int bound) {
            return nextInt;
        }
    }
}
