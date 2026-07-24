package com.mathmod.knowledge;

import com.mathmod.util.NamespacedId;
import net.minecraft.server.level.ServerPlayer;

public final class KnowledgeDiscoveryService {
    private KnowledgeDiscoveryService() {
    }

    public static KnowledgeDiscovery.ReadResult read(
            ServerPlayer player,
            NamespacedId manuscriptId
    ) {
        PlayerKnowledge original = KnowledgeService.get(player);
        KnowledgeDiscovery.Evaluation evaluation =
                KnowledgeDiscovery.evaluate(original, manuscriptId);
        if (evaluation.result() == KnowledgeDiscovery.ReadResult.FIRST_READ) {
            KnowledgeService.replace(player, evaluation.knowledge());
        }
        return evaluation.result();
    }
}
