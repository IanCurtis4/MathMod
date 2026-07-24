package com.mathmod.kubejs;

import com.mathmod.manuscript.ManuscriptDefinition;
import com.mathmod.manuscript.ManuscriptRarity;
import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Declarative startup builder for bounded reader data only. */
public final class KubeJsManuscriptSpec {
    private final NamespacedId id;
    private Integer schemaVersion;
    private NamespacedId tradition;
    private String titleKey;
    private final List<String> pages = new ArrayList<>();
    private NamespacedId icon;
    private ManuscriptRarity rarity;
    private Optional<NamespacedId> patchouliEntry = Optional.empty();
    private Optional<NamespacedId> theorem = Optional.empty();
    private boolean registered;

    KubeJsManuscriptSpec(String id) {
        this.id = NamespacedId.parse(id);
    }

    public KubeJsManuscriptSpec schemaVersion(int value) { schemaVersion = value; return this; }
    public KubeJsManuscriptSpec tradition(String value) { tradition = NamespacedId.parse(value); return this; }
    public KubeJsManuscriptSpec titleKey(String value) { titleKey = value; return this; }
    public KubeJsManuscriptSpec page(String value) { pages.add(value); return this; }
    public KubeJsManuscriptSpec icon(String value) { icon = NamespacedId.parse(value); return this; }

    public KubeJsManuscriptSpec rarity(String value) {
        rarity = ManuscriptRarity.parse(value)
                .orElseThrow(() -> new IllegalArgumentException("Unknown manuscript rarity " + value));
        return this;
    }

    public KubeJsManuscriptSpec patchouliEntry(String value) {
        patchouliEntry = Optional.of(NamespacedId.parse(value));
        return this;
    }

    public KubeJsManuscriptSpec theorem(String value) {
        theorem = Optional.of(NamespacedId.parse(value));
        return this;
    }

    public void register() {
        if (registered) {
            throw new IllegalStateException("KubeJS manuscript " + id + " is already registered");
        }
        if (schemaVersion == null) {
            throw new IllegalStateException("KubeJS manuscript " + id + " requires schemaVersion");
        }
        KubeJsCompat.registerManuscript(new ManuscriptDefinition(
                schemaVersion, id, tradition, titleKey, pages, icon, rarity, patchouliEntry, theorem
        ));
        registered = true;
    }
}
