package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.Optional;

public final class KnowledgeDiscovery {
    private KnowledgeDiscovery() {
    }

    public static Evaluation evaluate(PlayerKnowledge original, NamespacedId manuscriptId) {
        Optional<DiscoveryDefinition> definition =
                KnowledgeDefinitions.discoveryForManuscript(manuscriptId);
        if (definition.isEmpty()) {
            return new Evaluation(ReadResult.UNKNOWN, original);
        }
        DiscoveryDefinition discovery = definition.orElseThrow();
        if (original.knows(KnowledgeKind.DISCOVERY, discovery.id())) {
            return new Evaluation(ReadResult.DUPLICATE, original);
        }
        PlayerKnowledge changed = original.grant(KnowledgeKind.DISCOVERY, discovery.id());
        for (KnowledgeGrant grant : discovery.grants()) {
            changed = grant.apply(changed);
        }
        return new Evaluation(ReadResult.FIRST_READ, changed);
    }

    public enum ReadResult {
        FIRST_READ,
        DUPLICATE,
        UNKNOWN
    }

    public record Evaluation(ReadResult result, PlayerKnowledge knowledge) {
    }
}
