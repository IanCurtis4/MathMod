package com.mathmod.acquisition;

import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Pure reconciliation decision; Minecraft offer mutation stays in the server adapter. */
public record MathemagicianOfferReconciliationPlan(
        Set<NamespacedId> retained,
        List<ManuscriptAcquisitionSnapshot.Candidate> additions
) {
    private static final int MAX_CAREER_MANUSCRIPT_OFFERS = 6;

    public MathemagicianOfferReconciliationPlan {
        retained = Set.copyOf(retained);
        additions = List.copyOf(additions);
    }

    public static MathemagicianOfferReconciliationPlan create(
            ManuscriptAcquisitionSnapshot snapshot,
            UUID villagerId,
            long generation,
            int careerLevel,
            Set<NamespacedId> existingOfferIds
    ) {
        Map<NamespacedId, ManuscriptAcquisitionSnapshot.Candidate> candidates = new HashMap<>();
        snapshot.candidates().forEach(candidate -> candidates.put(candidate.id(), candidate));
        Set<NamespacedId> retained = new HashSet<>();
        for (NamespacedId id : existingOfferIds) {
            ManuscriptAcquisitionSnapshot.Candidate candidate = candidates.get(id);
            if (candidate != null && candidate.trade().isPresent()) {
                retained.add(id);
            }
        }

        List<ManuscriptAcquisitionSnapshot.Candidate> additions = new ArrayList<>();
        for (int level = 2; level <= careerLevel
                && retained.size() + additions.size() < MAX_CAREER_MANUSCRIPT_OFFERS; level++) {
            for (ManuscriptAcquisitionSnapshot.Candidate candidate : MathemagicianTradeCatalog.offersForLevel(
                    snapshot, villagerId, generation, level
            )) {
                if (retained.size() + additions.size() >= MAX_CAREER_MANUSCRIPT_OFFERS) {
                    break;
                }
                if (!retained.contains(candidate.id())) {
                    additions.add(candidate);
                }
            }
        }
        return new MathemagicianOfferReconciliationPlan(retained, additions);
    }
}
