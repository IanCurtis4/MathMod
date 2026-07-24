package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;

import java.util.List;
import java.util.Optional;

final class InscriptionResourcePolicy {
    private InscriptionResourcePolicy() {
    }

    static List<ResourceSelection> resourcesToPersist(
            Optional<ProgramGraph> storedGraph,
            List<ResourceSelection> storedResources,
            ProgramGraph nextGraph,
            List<ResourceSelection> recommendedResources
    ) {
        if (storedGraph.filter(nextGraph::equals).isPresent()) {
            return List.copyOf(storedResources);
        }
        return List.copyOf(recommendedResources);
    }
}
