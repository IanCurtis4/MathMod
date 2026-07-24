package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.Objects;

public record KnowledgeKey(KnowledgeKind kind, NamespacedId id) {
    public KnowledgeKey {
        kind = Objects.requireNonNull(kind, "kind");
        id = Objects.requireNonNull(id, "id");
    }
}
