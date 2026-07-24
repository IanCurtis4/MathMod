package com.mathmod.knowledge;

public final class KnowledgeMigrations {
    private KnowledgeMigrations() {
    }

    public static PlayerKnowledge migrate(
            PlayerKnowledge knowledge,
            KnowledgeAliasRegistry aliases
    ) {
        PlayerKnowledge changed = knowledge;
        if (knowledge.schemaVersion() < 2) {
            changed = KnowledgeDefinitions.grantP2P3LegacyAccess(changed);
        }
        if (knowledge.schemaVersion() < 3) {
            changed = KnowledgeDefinitions.grantP6LegacyAccess(changed);
        }
        return changed.migrate(aliases);
    }
}
