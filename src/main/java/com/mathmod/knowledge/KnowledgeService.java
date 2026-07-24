package com.mathmod.knowledge;

import com.mathmod.registry.ModAttachments;
import com.mathmod.util.NamespacedId;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class KnowledgeService {
    private KnowledgeService() {
    }

    public static PlayerKnowledge get(Player player) {
        return player.getData(ModAttachments.PLAYER_KNOWLEDGE);
    }

    public static boolean grant(ServerPlayer player, KnowledgeKind kind, NamespacedId id) {
        NamespacedId canonical = KnowledgeAliases.current().resolve(kind, id);
        PlayerKnowledge current = get(player);
        PlayerKnowledge changed = current.grant(kind, canonical);
        return replaceIfChanged(player, current, changed);
    }

    public static boolean revoke(ServerPlayer player, KnowledgeKind kind, NamespacedId id) {
        NamespacedId canonical = KnowledgeAliases.current().resolve(kind, id);
        PlayerKnowledge current = get(player);
        PlayerKnowledge changed = current.revoke(kind, canonical);
        return replaceIfChanged(player, current, changed);
    }

    public static boolean clear(ServerPlayer player) {
        PlayerKnowledge current = get(player);
        return replaceIfChanged(player, current, current.clear());
    }

    public static boolean migrate(ServerPlayer player) {
        PlayerKnowledge current = get(player);
        return replaceIfChanged(
                player,
                current,
                KnowledgeMigrations.migrate(current, KnowledgeAliases.current())
        );
    }

    public static boolean replace(ServerPlayer player, PlayerKnowledge changed) {
        return replaceIfChanged(player, get(player), changed);
    }

    public static void sync(ServerPlayer player) {
        player.syncData(ModAttachments.PLAYER_KNOWLEDGE);
    }

    private static boolean replaceIfChanged(
            ServerPlayer player,
            PlayerKnowledge current,
            PlayerKnowledge changed
    ) {
        if (current.equals(changed)) {
            return false;
        }
        player.setData(ModAttachments.PLAYER_KNOWLEDGE, changed);
        sync(player);
        return true;
    }
}
