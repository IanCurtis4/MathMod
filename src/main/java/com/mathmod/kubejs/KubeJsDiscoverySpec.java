package com.mathmod.kubejs;

import com.mathmod.knowledge.DiscoveryDefinition;
import com.mathmod.knowledge.KnowledgeGrant;
import com.mathmod.knowledge.KnowledgeKind;
import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.List;

public final class KubeJsDiscoverySpec {
    private final NamespacedId id;
    private NamespacedId manuscript;
    private String titleKey;
    private NamespacedId patchouliEntry;
    private final List<KnowledgeGrant> grants = new ArrayList<>();

    KubeJsDiscoverySpec(String id) {
        this.id = NamespacedId.parse(id);
    }

    public KubeJsDiscoverySpec manuscript(String manuscriptId) {
        this.manuscript = NamespacedId.parse(manuscriptId);
        return this;
    }

    public KubeJsDiscoverySpec titleKey(String titleKey) {
        this.titleKey = titleKey;
        return this;
    }

    public KubeJsDiscoverySpec patchouliEntry(String patchouliEntry) {
        this.patchouliEntry = NamespacedId.parse(patchouliEntry);
        return this;
    }

    public KubeJsDiscoverySpec grantRune(String runeId) {
        grants.add(new KnowledgeGrant(KnowledgeKind.RUNE, NamespacedId.parse(runeId)));
        return this;
    }

    public KubeJsDiscoverySpec grantTheorem(String theoremId) {
        grants.add(new KnowledgeGrant(KnowledgeKind.THEOREM, NamespacedId.parse(theoremId)));
        return this;
    }

    public void register() {
        DiscoveryDefinition definition = new DiscoveryDefinition(
                id,
                manuscript,
                titleKey,
                patchouliEntry,
                grants
        );
        KubeJsCompat.configure(api -> api.registerDiscovery(definition));
    }
}
