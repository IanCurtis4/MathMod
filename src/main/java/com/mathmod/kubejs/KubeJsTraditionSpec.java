package com.mathmod.kubejs;

import com.mathmod.manuscript.TraditionDefinition;
import com.mathmod.util.NamespacedId;

/** Declarative startup builder. It has no player, world, or execution access. */
public final class KubeJsTraditionSpec {
    private final NamespacedId id;
    private Integer schemaVersion;
    private String nameKey;
    private String summaryKey;
    private NamespacedId icon;
    private boolean registered;

    KubeJsTraditionSpec(String id) {
        this.id = NamespacedId.parse(id);
    }

    public KubeJsTraditionSpec schemaVersion(int value) {
        schemaVersion = value;
        return this;
    }

    public KubeJsTraditionSpec nameKey(String value) {
        nameKey = value;
        return this;
    }

    public KubeJsTraditionSpec summaryKey(String value) {
        summaryKey = value;
        return this;
    }

    public KubeJsTraditionSpec icon(String value) {
        icon = NamespacedId.parse(value);
        return this;
    }

    public void register() {
        if (registered) {
            throw new IllegalStateException("KubeJS tradition " + id + " is already registered");
        }
        if (schemaVersion == null) {
            throw new IllegalStateException("KubeJS tradition " + id + " requires schemaVersion");
        }
        KubeJsCompat.registerManuscriptTradition(new TraditionDefinition(
                schemaVersion, id, nameKey, summaryKey, icon
        ));
        registered = true;
    }
}
