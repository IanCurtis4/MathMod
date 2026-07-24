package com.mathmod.client;

import java.util.ArrayList;
import java.util.List;

public final class PatchouliPreviewMatrix {
    private static final List<Target> TARGETS = buildTargets();

    private PatchouliPreviewMatrix() {
    }

    public static List<Target> targets() {
        return TARGETS;
    }

    private static List<Target> buildTargets() {
        List<Target> targets = new ArrayList<>();
        addEntry(targets, "basics/can_i_make_spell", 5);
        addEntry(targets, "basics/current_state", 2);
        addEntry(targets, "basics/documentation_track", 1);
        addEntry(targets, "basics/field_ledger", 4);
        addEntry(targets, "basics/world_anchors", 5);
        addEntry(targets, "lore/convergence", 2);
        addEntry(targets, "lore/bound_measure", 2);
        addEntry(targets, "lore/cartographer_chests", 2);
        addEntry(targets, "lore/field_fragments", 4);
        addEntry(targets, "lore/ledger_of_remainders", 2);
        addEntry(targets, "lore/mathemagician", 2);
        addEntry(targets, "lore/parallel_proofs", 2);
        addEntry(targets, "lore/runes_and_types", 2);
        addEntry(targets, "lore/rotated_horizon", 2);
        addEntry(targets, "lore/weighted_gathering", 2);
        addEntry(targets, "lore/witnesses", 2);
        addEntry(targets, "programming/beta_theorems", 8);
        addEntry(targets, "programming/budget", 2);
        addEntry(targets, "programming/alchemical_effects", 18);
        addEntry(targets, "programming/coordinate_frames", 3);
        addEntry(targets, "programming/custom_programmer", 8);
        addEntry(targets, "programming/kubejs", 7);
        addEntry(targets, "programming/mathematical_runes", 10);
        addEntry(targets, "programming/metamagic", 10);
        addEntry(targets, "programming/movement_raycast", 2);
        addEntry(targets, "programming/regions", 4);
        addEntry(targets, "programming/constructs", 6);
        addEntry(targets, "programming/physical_profiles", 6);
        addEntry(targets, "programming/resource_costs", 7);
        addEntry(targets, "programming/safety", 2);
        addEntry(targets, "programming/serialization", 1);
        addEntry(targets, "programming/target_queries", 4);
        addEntry(targets, "programming/typed_graphs", 4);
        addEntry(targets, "programming/inspector", 4);
        addEntry(targets, "environment/correspondence", 6);
        addEntry(targets, "roadmap/epics", 2);
        return List.copyOf(targets);
    }

    private static void addEntry(List<Target> targets, String entryId, int pageCount) {
        for (int page = 0; page < pageCount; page += 2) {
            targets.add(new Target(
                    entryId,
                    page,
                    "patchouli-matrix-" + entryId.replace('/', '-') + "-p" + page
            ));
        }
    }

    public record Target(String entryId, int page, String screenshotId) {
        public Target {
            if (entryId == null || entryId.isBlank()) {
                throw new IllegalArgumentException("Patchouli entry id cannot be blank");
            }
            if (page < 0 || page % 2 != 0) {
                throw new IllegalArgumentException("Patchouli preview pages must begin a spread");
            }
            if (screenshotId == null || screenshotId.isBlank()) {
                throw new IllegalArgumentException("Patchouli screenshot id cannot be blank");
            }
        }
    }
}
