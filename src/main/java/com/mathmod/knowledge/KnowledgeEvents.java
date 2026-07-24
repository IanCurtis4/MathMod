package com.mathmod.knowledge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.gametest.framework.GameTestServer;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class KnowledgeEvents {
    private KnowledgeEvents() {
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.getRelevantPlayers().forEach(player -> {
            if (player.getServer() instanceof GameTestServer) {
                return;
            }
            KnowledgeService.migrate(player);
            KnowledgeService.sync(player);
        });
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !(player.getServer() instanceof GameTestServer)) {
            KnowledgeService.sync(player);
        }
    }
}
