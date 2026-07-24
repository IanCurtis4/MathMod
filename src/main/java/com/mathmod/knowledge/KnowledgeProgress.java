package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class KnowledgeProgress {
    private KnowledgeProgress() {
    }

    public static ProgressUpdate advance(
            PlayerKnowledge original,
            Set<NamespacedId> usedMaterials
    ) {
        PlayerKnowledge changed = original;
        List<ProgressNotice> notices = new ArrayList<>();
        List<EpiphanyDefinition> completed = new ArrayList<>();

        for (EpiphanyDefinition epiphany : KnowledgeDefinitions.epiphanies()) {
            if (changed.knows(KnowledgeKind.EPIPHANY, epiphany.id())) {
                continue;
            }
            for (MaterialStudyRequirement study : epiphany.studies()) {
                if (!usedMaterials.contains(study.materialId())) {
                    continue;
                }
                NamespacedId progressKey = epiphany.progressKey(study);
                int before = changed.progress(progressKey);
                changed = changed
                        .grant(KnowledgeKind.MATERIAL, study.materialId())
                        .incrementProgress(progressKey, study.successfulCasts());
                int after = changed.progress(progressKey);
                if (after > before) {
                    notices.add(new ProgressNotice(study, after));
                }
            }
            if (epiphany.complete(changed)) {
                changed = changed
                        .grant(KnowledgeKind.CORRELATION, epiphany.correlationId())
                        .grant(KnowledgeKind.EPIPHANY, epiphany.id())
                        .clearProgress(epiphany.studies().stream().map(epiphany::progressKey).toList());
                for (KnowledgeGrant grant : epiphany.grants()) {
                    changed = grant.apply(changed);
                }
                completed.add(epiphany);
            }
        }
        return new ProgressUpdate(changed, notices, completed);
    }

    public record ProgressNotice(MaterialStudyRequirement study, int progress) {
        public ProgressNotice {
            if (progress < 1 || progress > study.successfulCasts()) {
                throw new IllegalArgumentException("progress is out of study bounds");
            }
        }
    }

    public record ProgressUpdate(
            PlayerKnowledge knowledge,
            List<ProgressNotice> notices,
            List<EpiphanyDefinition> completed
    ) {
        public ProgressUpdate {
            notices = List.copyOf(notices);
            completed = List.copyOf(completed);
        }
    }
}
