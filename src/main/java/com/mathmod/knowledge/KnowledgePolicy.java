package com.mathmod.knowledge;

import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.TalismanPreset;
import com.mathmod.util.NamespacedId;

import java.util.Optional;

public final class KnowledgePolicy {
    private KnowledgePolicy() {
    }

    public static Optional<KnowledgeRequirement> requirementFor(TalismanPreset preset) {
        return KnowledgeDefinitions.snapshot().requirementFor(
                KnowledgeKind.THEOREM,
                NamespacedId.parse(preset.id())
        );
    }

    public static Optional<KnowledgeRequirement> requirementFor(CustomSpellAction action) {
        return requirementForRune(action.iconRuneId());
    }

    public static Optional<KnowledgeRequirement> requirementForRune(String runeId) {
        return NamespacedId.tryParse(runeId).flatMap(id ->
                KnowledgeDefinitions.snapshot().requirementFor(KnowledgeKind.RUNE, id));
    }

    public static boolean canConstruct(PlayerKnowledge knowledge, TalismanPreset preset) {
        return requirementFor(preset).map(requirement -> requirement.isSatisfiedBy(knowledge)).orElse(true);
    }

    public static boolean canUse(PlayerKnowledge knowledge, CustomSpellAction action) {
        return requirementFor(action).map(requirement -> requirement.isSatisfiedBy(knowledge)).orElse(true);
    }

    public static boolean canEdit(PlayerKnowledge knowledge, Iterable<CustomSpellAction> actions) {
        for (CustomSpellAction action : actions) {
            if (!canUse(knowledge, action)) {
                return false;
            }
        }
        return true;
    }
}
