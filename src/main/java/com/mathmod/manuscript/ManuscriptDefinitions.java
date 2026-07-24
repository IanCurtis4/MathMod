package com.mathmod.manuscript;

import com.mathmod.acquisition.ManuscriptAcquisitionBuildResult;
import com.mathmod.acquisition.ManuscriptAcquisitionConfig;
import com.mathmod.acquisition.ManuscriptAcquisitionPublication;
import com.mathmod.acquisition.ManuscriptAcquisitionPublicationStore;
import com.mathmod.acquisition.ManuscriptAcquisitionSnapshot;
import com.mathmod.util.NamespacedId;

import java.util.Optional;

public final class ManuscriptDefinitions {
    private static final ManuscriptAcquisitionPublicationStore STORE = new ManuscriptAcquisitionPublicationStore();

    private ManuscriptDefinitions() {
    }

    public static ManuscriptSnapshot snapshot() {
        return STORE.publication().manuscripts();
    }

    public static ManuscriptAcquisitionSnapshot acquisitionSnapshot() {
        return STORE.publication().acquisition();
    }

    public static ManuscriptAcquisitionConfig acquisitionConfig() {
        return STORE.publication().config();
    }

    public static long acquisitionGeneration() {
        return STORE.publication().generation();
    }

    public static long refreshAcquisitionConfig(ManuscriptAcquisitionConfig config) {
        return STORE.refreshConfig(config).generation();
    }

    public static Optional<ManuscriptDefinition> manuscript(NamespacedId id) {
        return snapshot().manuscript(id);
    }

    public static Optional<TraditionDefinition> tradition(NamespacedId id) {
        return snapshot().tradition(id);
    }

    static boolean publish(
            ManuscriptSnapshotBuildResult manuscripts,
            ManuscriptAcquisitionBuildResult acquisition,
            ManuscriptAcquisitionConfig config
    ) {
        return STORE.publish(manuscripts, acquisition, config);
    }
}
