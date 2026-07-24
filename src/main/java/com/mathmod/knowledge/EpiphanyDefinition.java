package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record EpiphanyDefinition(
        NamespacedId id,
        String titleTranslationKey,
        NamespacedId correlationId,
        List<MaterialStudyRequirement> studies,
        List<KnowledgeGrant> grants
) {
    public EpiphanyDefinition {
        id = Objects.requireNonNull(id, "id");
        correlationId = Objects.requireNonNull(correlationId, "correlationId");
        titleTranslationKey = requireText(titleTranslationKey);
        studies = List.copyOf(studies);
        grants = List.copyOf(grants);
        if (studies.size() < 2 || studies.size() > 8) {
            throw new IllegalArgumentException("An epiphany must study between 2 and 8 materials");
        }
        Set<NamespacedId> materialIds = new HashSet<>();
        Set<Integer> tiers = new HashSet<>();
        studies.forEach(study -> {
            if (!materialIds.add(study.materialId())) {
                throw new IllegalArgumentException("Duplicate epiphany material " + study.materialId());
            }
            tiers.add(study.tier());
        });
        if (tiers.size() < 2) {
            throw new IllegalArgumentException("An epiphany must correlate at least two material tiers");
        }
        if (grants.isEmpty() || grants.size() > 16) {
            throw new IllegalArgumentException("An epiphany must have between 1 and 16 grants");
        }
    }

    public NamespacedId progressKey(MaterialStudyRequirement study) {
        return NamespacedId.of(
                id.namespace(),
                "progress/" + id.path() + "/" + study.materialId().namespace() + "/" + study.materialId().path()
        );
    }

    public boolean complete(PlayerKnowledge knowledge) {
        return studies.stream().allMatch(study ->
                knowledge.progress(progressKey(study)) >= study.successfulCasts());
    }

    private static String requireText(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("titleTranslationKey must not be blank");
        }
        return value.trim();
    }
}
