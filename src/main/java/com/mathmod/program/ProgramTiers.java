package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.RuneTier;

public final class ProgramTiers {
    private ProgramTiers() {
    }

    public static RuneTier requiredTier(ProgramGraph graph) {
        RuneTier required = RuneTier.FUNDAMENTAL;
        for (var node : graph.nodes()) {
            RuneTier nodeTier = ProgramStorage.definition(node.runeId())
                    .map(definition -> definition.tier())
                    .orElse(RuneTier.FUNDAMENTAL);
            if (nodeTier.level() > required.level()) {
                required = nodeTier;
            }
        }
        return required;
    }
}
