package com.mathmod.knowledge;

import com.mathmod.kubejs.RuneMaterialDefinition;
import com.mathmod.program.ProgramCostPlan;
import com.mathmod.program.ProgramResources;
import com.mathmod.util.NamespacedId;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;

public final class KnowledgeProgressService {
    private KnowledgeProgressService() {
    }

    public static void recordSuccessfulCast(ServerPlayer player, ProgramCostPlan plan) {
        Set<NamespacedId> usedMaterials = new HashSet<>();
        plan.lines().forEach(line -> ProgramResources.material(line.id()).ifPresent(material ->
                usedMaterials.add(NamespacedId.of("mathmod", material.id()))));
        if (usedMaterials.isEmpty()) {
            return;
        }

        PlayerKnowledge original = KnowledgeService.get(player);
        KnowledgeProgress.ProgressUpdate update = KnowledgeProgress.advance(original, usedMaterials);
        if (!KnowledgeService.replace(player, update.knowledge())) {
            return;
        }
        for (KnowledgeProgress.ProgressNotice notice : update.notices()) {
            player.displayClientMessage(Component.translatable(
                    "knowledge.mathmod.study.progress",
                    materialName(notice.study().materialId()),
                    notice.progress(),
                    notice.study().successfulCasts()
            ), true);
        }
        for (EpiphanyDefinition epiphany : update.completed()) {
            player.displayClientMessage(Component.translatable(
                    "knowledge.mathmod.epiphany.complete",
                    Component.translatable(epiphany.titleTranslationKey())
            ), false);
        }
    }

    private static Component materialName(NamespacedId materialId) {
        return ProgramResources.material(materialId.path())
                .map(KnowledgeProgressService::materialName)
                .orElse(Component.literal(materialId.toString()));
    }

    private static Component materialName(RuneMaterialDefinition material) {
        return material.displayTranslationKey() == null
                ? Component.literal(material.fallbackDisplayName())
                : Component.translatable(material.displayTranslationKey());
    }
}
