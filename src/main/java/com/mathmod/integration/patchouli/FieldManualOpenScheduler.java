package com.mathmod.integration.patchouli;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class FieldManualOpenScheduler {
    private static final List<PendingOpen> PENDING = new ArrayList<>();

    private FieldManualOpenScheduler() {
    }

    public static void schedule(ServerPlayer player, Target target) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        player.closeContainer();
        PENDING.removeIf(pending -> pending.server() == server && pending.playerId().equals(player.getUUID()));
        PENDING.add(new PendingOpen(server, player.getUUID(), server.getTickCount() + 1, target, null));
    }

    public static void scheduleDiscovery(ServerPlayer player, NamespacedId entry) {
        MinecraftServer server = player.getServer();
        if (server == null || entry == null) {
            return;
        }

        player.closeContainer();
        PENDING.removeIf(pending -> pending.server() == server && pending.playerId().equals(player.getUUID()));
        PENDING.add(new PendingOpen(server, player.getUUID(), server.getTickCount() + 1, null, entry));
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        Iterator<PendingOpen> iterator = PENDING.iterator();
        while (iterator.hasNext()) {
            PendingOpen pending = iterator.next();
            if (pending.server() != server || server.getTickCount() < pending.openAtTick()) {
                continue;
            }

            iterator.remove();
            ServerPlayer player = server.getPlayerList().getPlayer(pending.playerId());
            if (player != null && !openTarget(player, pending.target(), pending.discoveryEntry())) {
                player.displayClientMessage(
                        Component.translatable(pending.target() == null
                                ? "item.mathmod.field_manuscript.open_failed"
                                : pending.target().failureTranslationKey()),
                        false
                );
            }
        }
    }

    public enum Target {
        FIRST_SPELL("screen.mathmod.rune_programmer.help_failed"),
        RESOURCE_COSTS("screen.mathmod.talisman_resources.help_failed");

        private final String failureTranslationKey;

        Target(String failureTranslationKey) {
            this.failureTranslationKey = failureTranslationKey;
        }

        private String failureTranslationKey() {
            return failureTranslationKey;
        }
    }

    private static boolean openTarget(ServerPlayer player, Target target, NamespacedId pendingEntry) {
        return target == null
                ? PatchouliFieldManual.openDiscovery(player, pendingEntry)
                : switch (target) {
                    case FIRST_SPELL -> PatchouliFieldManual.openFirstSpell(player);
                    case RESOURCE_COSTS -> PatchouliFieldManual.openResourceCosts(player);
                };
    }

    private record PendingOpen(MinecraftServer server, UUID playerId, int openAtTick, Target target,
                               NamespacedId discoveryEntry) {
    }
}
