package com.mathmod.knowledge;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum KnowledgeKind {
    MATERIAL("material"),
    CORRELATION("correlation"),
    EPIPHANY("epiphany"),
    DISCOVERY("discovery"),
    RUNE("rune"),
    THEOREM("theorem");

    private final String serializedName;

    KnowledgeKind(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return serializedName;
    }

    public String translationKey() {
        return "knowledge_kind.mathmod." + serializedName;
    }

    public static Optional<KnowledgeKind> parse(String value) {
        if (value == null) {
            return Optional.empty();
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(kind -> kind.serializedName.equals(normalized))
                .findFirst();
    }
}
