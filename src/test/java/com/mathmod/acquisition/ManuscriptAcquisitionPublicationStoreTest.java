package com.mathmod.acquisition;

import com.mathmod.manuscript.ManuscriptSnapshot;
import com.mathmod.manuscript.ManuscriptSnapshotBuildResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManuscriptAcquisitionPublicationStoreTest {
    @Test
    void rejectedCandidateKeepsBothPublishedSnapshotsAndGeneration() {
        ManuscriptAcquisitionPublicationStore store = new ManuscriptAcquisitionPublicationStore();
        ManuscriptAcquisitionConfig config = ManuscriptAcquisitionConfig.defaults();
        ManuscriptSnapshotBuildResult manuscripts = new ManuscriptSnapshotBuildResult(
                ManuscriptSnapshot.empty(), List.of(), true
        );
        ManuscriptAcquisitionBuildResult acquisition = new ManuscriptAcquisitionBuildResult(
                ManuscriptAcquisitionSnapshot.empty(), List.of(), true
        );

        assertTrue(store.publish(manuscripts, acquisition, config));
        ManuscriptAcquisitionPublication published = store.publication();

        assertFalse(store.publish(
                new ManuscriptSnapshotBuildResult(ManuscriptSnapshot.empty(), List.of(), false),
                acquisition,
                config
        ));
        assertEquals(published, store.publication());
        assertEquals(1, store.publication().generation());
    }

    @Test
    void configurationRefreshKeepsThePublishedDataAndAdvancesGeneration() {
        ManuscriptAcquisitionPublicationStore store = new ManuscriptAcquisitionPublicationStore();
        ManuscriptAcquisitionConfig initial = ManuscriptAcquisitionConfig.defaults();
        ManuscriptSnapshotBuildResult manuscripts = new ManuscriptSnapshotBuildResult(
                ManuscriptSnapshot.empty(), List.of(), true
        );
        ManuscriptAcquisitionBuildResult acquisition = new ManuscriptAcquisitionBuildResult(
                ManuscriptAcquisitionSnapshot.empty(), List.of(), true
        );
        assertTrue(store.publish(manuscripts, acquisition, initial));
        ManuscriptAcquisitionPublication before = store.publication();

        ManuscriptAcquisitionConfig refreshed = new ManuscriptAcquisitionConfig(
                false, true, true, false, SurplusPolicy.KEEP, 0, 3
        );
        ManuscriptAcquisitionPublication after = store.refreshConfig(refreshed);

        assertEquals(before.generation() + 1, after.generation());
        assertEquals(before.manuscripts(), after.manuscripts());
        assertEquals(before.acquisition(), after.acquisition());
        assertEquals(refreshed, after.config());
    }
}
