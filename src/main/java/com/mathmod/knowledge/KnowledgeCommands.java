package com.mathmod.knowledge;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import com.mathmod.util.NamespacedId;

public final class KnowledgeCommands {
    private static final DynamicCommandExceptionType UNKNOWN_KIND =
            new DynamicCommandExceptionType(value -> Component.translatable("command.mathmod.knowledge.unknown_kind", value));
    private static final DynamicCommandExceptionType INVALID_ID =
            new DynamicCommandExceptionType(value -> Component.translatable("command.mathmod.knowledge.invalid_id", value));

    private KnowledgeCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("mathmod")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("knowledge")
                                .then(Commands.literal("get")
                                        .executes(context -> show(
                                                context.getSource(),
                                                context.getSource().getPlayerOrException()
                                        ))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> show(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "player")
                                                ))))
                                .then(mutationBranch("grant", true))
                                .then(mutationBranch("revoke", false))
                                .then(Commands.literal("clear")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> clear(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "player")
                                                ))))
                                .then(Commands.literal("migrate")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> migrate(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "player")
                                                ))))
                        )
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> mutationBranch(String name, boolean grant) {
        return Commands.literal(name)
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("kind", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                        Arrays.stream(KnowledgeKind.values())
                                                .map(KnowledgeKind::serializedName),
                                        builder
                                ))
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(context -> mutate(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "kind"),
                                                StringArgumentType.getString(context, "id"),
                                                grant
                                        )))));
    }

    private static int show(CommandSourceStack source, ServerPlayer player) {
        PlayerKnowledge knowledge = KnowledgeService.get(player);
        source.sendSuccess(() -> Component.translatable(
                "command.mathmod.knowledge.header",
                player.getDisplayName(),
                knowledge.schemaVersion(),
                knowledge.totalEntries()
        ), false);
        for (KnowledgeKind kind : KnowledgeKind.values()) {
            String values = knowledge.entries(kind).stream()
                    .sorted(Comparator.naturalOrder())
                    .map(NamespacedId::toString)
                    .collect(Collectors.joining(", "));
            source.sendSuccess(() -> Component.translatable(
                    "command.mathmod.knowledge.line",
                    Component.translatable(kind.translationKey()),
                    knowledge.entries(kind).size(),
                    values.isEmpty()
                            ? Component.translatable("command.mathmod.knowledge.none")
                            : Component.literal(values)
            ), false);
        }
        return knowledge.totalEntries();
    }

    private static int mutate(
            CommandSourceStack source,
            ServerPlayer player,
            String kindValue,
            String idValue,
            boolean grant
    ) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        KnowledgeKind kind = KnowledgeKind.parse(kindValue)
                .orElseThrow(() -> UNKNOWN_KIND.create(kindValue));
        NamespacedId id;
        try {
            id = KnowledgeAliases.parseUserId(idValue);
        } catch (IllegalArgumentException exception) {
            throw INVALID_ID.create(idValue);
        }
        NamespacedId canonical = KnowledgeAliases.current().resolve(kind, id);
        boolean changed = grant
                ? KnowledgeService.grant(player, kind, canonical)
                : KnowledgeService.revoke(player, kind, canonical);
        String message = changed
                ? (grant ? "command.mathmod.knowledge.granted" : "command.mathmod.knowledge.revoked")
                : (grant ? "command.mathmod.knowledge.already_known" : "command.mathmod.knowledge.not_known");
        source.sendSuccess(() -> Component.translatable(
                message,
                Component.translatable(kind.translationKey()),
                canonical,
                player.getDisplayName()
        ), true);
        return changed ? 1 : 0;
    }

    private static int clear(CommandSourceStack source, ServerPlayer player) {
        boolean changed = KnowledgeService.clear(player);
        source.sendSuccess(() -> Component.translatable(
                changed ? "command.mathmod.knowledge.cleared" : "command.mathmod.knowledge.already_empty",
                player.getDisplayName()
        ), true);
        return changed ? 1 : 0;
    }

    private static int migrate(CommandSourceStack source, ServerPlayer player) {
        boolean changed = KnowledgeService.migrate(player);
        source.sendSuccess(() -> Component.translatable(
                changed ? "command.mathmod.knowledge.migrated" : "command.mathmod.knowledge.current",
                player.getDisplayName()
        ), true);
        return changed ? 1 : 0;
    }
}
