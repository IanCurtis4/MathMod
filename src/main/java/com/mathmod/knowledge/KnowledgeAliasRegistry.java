package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class KnowledgeAliasRegistry {
    private final Map<KnowledgeKey, KnowledgeKey> aliases;

    private KnowledgeAliasRegistry(Map<KnowledgeKey, KnowledgeKey> aliases) {
        this.aliases = Map.copyOf(aliases);
        this.aliases.keySet().forEach(this::resolve);
    }

    public static Builder builder() {
        return new Builder();
    }

    public KnowledgeKey resolve(KnowledgeKey key) {
        Objects.requireNonNull(key, "key");
        KnowledgeKey current = key;
        Set<KnowledgeKey> visited = new HashSet<>();
        while (aliases.containsKey(current)) {
            if (!visited.add(current)) {
                throw new IllegalStateException("Knowledge alias cycle at " + current);
            }
            current = aliases.get(current);
        }
        return current;
    }

    public NamespacedId resolve(KnowledgeKind kind, NamespacedId id) {
        return resolve(new KnowledgeKey(kind, id)).id();
    }

    public int size() {
        return aliases.size();
    }

    public static final class Builder {
        private final Map<KnowledgeKey, KnowledgeKey> aliases = new HashMap<>();

        public Builder add(KnowledgeKind kind, NamespacedId alias, NamespacedId target) {
            KnowledgeKey from = new KnowledgeKey(kind, alias);
            KnowledgeKey to = new KnowledgeKey(kind, target);
            if (from.equals(to)) {
                throw new IllegalArgumentException("Knowledge alias cannot target itself: " + from);
            }
            KnowledgeKey previous = aliases.putIfAbsent(from, to);
            if (previous != null && !previous.equals(to)) {
                throw new IllegalArgumentException("Conflicting knowledge alias " + from);
            }
            return this;
        }

        public KnowledgeAliasRegistry build() {
            return new KnowledgeAliasRegistry(aliases);
        }
    }
}
