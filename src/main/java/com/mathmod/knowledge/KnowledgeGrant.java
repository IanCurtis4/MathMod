package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.Objects;

public record KnowledgeGrant(KnowledgeKind kind, NamespacedId id) {
    public KnowledgeGrant {
        kind = Objects.requireNonNull(kind, "kind");
        id = Objects.requireNonNull(id, "id");
        if (kind != KnowledgeKind.RUNE && kind != KnowledgeKind.THEOREM) {
            throw new IllegalArgumentException("Definitions may grant only runes or theorems directly");
        }
    }

    public PlayerKnowledge apply(PlayerKnowledge knowledge) {
        return knowledge.grant(kind, id);
    }
}
