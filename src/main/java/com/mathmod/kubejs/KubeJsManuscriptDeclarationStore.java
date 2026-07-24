package com.mathmod.kubejs;

import com.mathmod.manuscript.ManuscriptAliasDefinition;
import com.mathmod.manuscript.ManuscriptDefinition;
import com.mathmod.manuscript.ManuscriptDefinitionSource;
import com.mathmod.manuscript.ManuscriptSnapshotBuilder;
import com.mathmod.manuscript.ManuscriptSourceLayer;
import com.mathmod.manuscript.TraditionDefinition;
import com.mathmod.util.NamespacedId;

import java.util.LinkedHashMap;
import java.util.Map;

/** Startup-only staging boundary for the future P7 public KubeJS builders. */
public final class KubeJsManuscriptDeclarationStore {
    public static final String SOURCE_NAME = "kubejs:startup_scripts";
    private static final ManuscriptDefinitionSource SOURCE = new ManuscriptDefinitionSource(
            ManuscriptSourceLayer.KUBEJS,
            0,
            SOURCE_NAME
    );

    private final Map<NamespacedId, TraditionDefinition> traditions = new LinkedHashMap<>();
    private final Map<NamespacedId, ManuscriptDefinition> manuscripts = new LinkedHashMap<>();
    private final Map<NamespacedId, ManuscriptAliasDefinition> aliases = new LinkedHashMap<>();
    private State state = State.OPEN;
    private Snapshot frozenSnapshot;

    public synchronized void register(TraditionDefinition definition) {
        ensureOpen();
        putUnique(traditions, definition.id(), definition, "tradition", ManuscriptSnapshotBuilder.MAX_TRADITIONS);
    }

    public synchronized void register(ManuscriptDefinition definition) {
        ensureOpen();
        putUnique(manuscripts, definition.id(), definition, "manuscript", ManuscriptSnapshotBuilder.MAX_MANUSCRIPTS);
    }

    public synchronized void register(ManuscriptAliasDefinition definition) {
        ensureOpen();
        putUnique(aliases, definition.from(), definition, "manuscript alias", ManuscriptSnapshotBuilder.MAX_ALIASES);
    }

    public synchronized Snapshot freeze() {
        if (frozenSnapshot == null) {
            frozenSnapshot = new Snapshot(traditions, manuscripts, aliases, SOURCE);
            state = State.FROZEN;
        }
        return frozenSnapshot;
    }

    public synchronized State state() {
        return state;
    }

    private void ensureOpen() {
        if (state != State.OPEN) {
            throw new IllegalStateException("KubeJS manuscript declarations are frozen until the next server start");
        }
    }

    private static <T> void putUnique(
            Map<NamespacedId, T> values,
            NamespacedId id,
            T value,
            String kind,
            int maximum
    ) {
        if (values.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate KubeJS " + kind + " id " + id);
        }
        if (values.size() >= maximum) {
            throw new IllegalStateException("KubeJS " + kind + " declarations exceed " + maximum);
        }
        values.put(id, value);
    }

    public enum State {
        OPEN,
        FROZEN
    }

    public record Snapshot(
            Map<NamespacedId, TraditionDefinition> traditions,
            Map<NamespacedId, ManuscriptDefinition> manuscripts,
            Map<NamespacedId, ManuscriptAliasDefinition> aliases,
            ManuscriptDefinitionSource source
    ) {
        public Snapshot {
            traditions = Map.copyOf(traditions);
            manuscripts = Map.copyOf(manuscripts);
            aliases = Map.copyOf(aliases);
            if (source == null || source.layer() != ManuscriptSourceLayer.KUBEJS) {
                throw new IllegalArgumentException("KubeJS declaration snapshots require a KubeJS source");
            }
        }
    }
}
