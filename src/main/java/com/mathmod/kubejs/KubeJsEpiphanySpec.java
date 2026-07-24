package com.mathmod.kubejs;

import com.mathmod.knowledge.EpiphanyDefinition;
import com.mathmod.knowledge.KnowledgeGrant;
import com.mathmod.knowledge.KnowledgeKind;
import com.mathmod.knowledge.MaterialStudyRequirement;
import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.List;

public final class KubeJsEpiphanySpec {
    private final NamespacedId id;
    private String titleKey;
    private NamespacedId correlation;
    private final List<MaterialStudyRequirement> studies = new ArrayList<>();
    private final List<KnowledgeGrant> grants = new ArrayList<>();

    KubeJsEpiphanySpec(String id) {
        this.id = NamespacedId.parse(id);
    }

    public KubeJsEpiphanySpec titleKey(String titleKey) {
        this.titleKey = titleKey;
        return this;
    }

    public KubeJsEpiphanySpec correlation(String correlationId) {
        this.correlation = NamespacedId.parse(correlationId);
        return this;
    }

    public KubeJsEpiphanySpec study(String materialId, int tier, int successfulCasts) {
        studies.add(new MaterialStudyRequirement(
                NamespacedId.parse(materialId),
                tier,
                successfulCasts
        ));
        return this;
    }

    public KubeJsEpiphanySpec grantRune(String runeId) {
        grants.add(new KnowledgeGrant(KnowledgeKind.RUNE, NamespacedId.parse(runeId)));
        return this;
    }

    public KubeJsEpiphanySpec grantTheorem(String theoremId) {
        grants.add(new KnowledgeGrant(KnowledgeKind.THEOREM, NamespacedId.parse(theoremId)));
        return this;
    }

    public void register() {
        EpiphanyDefinition definition = new EpiphanyDefinition(
                id,
                titleKey,
                correlation,
                studies,
                grants
        );
        KubeJsCompat.configure(api -> api.registerEpiphany(definition));
    }
}
