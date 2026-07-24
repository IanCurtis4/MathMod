package com.mathmod.item;

import com.mathmod.knowledge.DiscoveryDefinition;
import com.mathmod.knowledge.KnowledgeDefinitions;
import com.mathmod.knowledge.KnowledgeDiscovery;
import com.mathmod.knowledge.KnowledgeDiscoveryService;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.manuscript.ManuscriptDefinitions;
import com.mathmod.manuscript.ManuscriptReaderView;
import com.mathmod.manuscript.ManuscriptReaderViewCodec;
import com.mathmod.screen.ManuscriptReaderMenu;
import com.mathmod.util.NamespacedId;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.SimpleMenuProvider;

import java.util.List;
import java.util.Optional;

public final class FieldManuscriptItem extends Item {
    public FieldManuscriptItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return definition(stack)
                .map(definition -> Component.translatable(
                        "item.mathmod.field_manuscript.named",
                        Component.translatable(definition.titleTranslationKey())
                ))
                .orElseGet(() -> Component.translatable("item.mathmod.field_manuscript.unknown"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NamespacedId manuscriptId = manuscriptId(stack).orElse(null);
            if (manuscriptId == null) {
                serverPlayer.displayClientMessage(
                        Component.translatable("item.mathmod.field_manuscript.missing_record"),
                        false
                );
                return InteractionResultHolder.fail(stack);
            }
            KnowledgeDiscovery.ReadResult result =
                    KnowledgeDiscoveryService.read(serverPlayer, manuscriptId);
            serverPlayer.displayClientMessage(Component.translatable(switch (result) {
                case FIRST_READ -> "item.mathmod.field_manuscript.first_read";
                case DUPLICATE -> "item.mathmod.field_manuscript.duplicate";
                case UNKNOWN -> "item.mathmod.field_manuscript.missing_record";
            }), false);
            ManuscriptReaderView view = ManuscriptReaderView.from(manuscriptId, ManuscriptDefinitions.snapshot());
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, menuPlayer) ->
                            new ManuscriptReaderMenu(containerId, inventory, usedHand, view),
                    Component.translatable("screen.mathmod.manuscript_reader")
            ), buffer -> {
                buffer.writeEnum(usedHand);
                ManuscriptReaderViewCodec.write(buffer, view);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        definition(stack).ifPresentOrElse(
                definition -> {
                    tooltipComponents.add(ItemTooltipStyles.identity(
                            Component.translatable(definition.titleTranslationKey())
                    ));
                    tooltipComponents.add(ItemTooltipStyles.detail(
                            Component.translatable("item.mathmod.field_manuscript.tooltip.provenance")
                    ));
                    tooltipComponents.add(ItemTooltipStyles.primaryAction(
                            Component.translatable("item.mathmod.field_manuscript.tooltip.read")
                    ));
                    tooltipComponents.add(ItemTooltipStyles.secondaryAction(
                            Component.translatable("item.mathmod.field_manuscript.tooltip.duplicate")
                    ));
                },
                () -> tooltipComponents.add(ItemTooltipStyles.detail(
                        Component.translatable("item.mathmod.field_manuscript.missing_record")
                ))
        );
    }

    private static Optional<NamespacedId> manuscriptId(ItemStack stack) {
        return NamespacedId.tryParse(stack.getOrDefault(
                ModDataComponents.MANUSCRIPT_ID.get(),
                ""
        ));
    }

    private static Optional<DiscoveryDefinition> definition(ItemStack stack) {
        return manuscriptId(stack).flatMap(KnowledgeDefinitions::discoveryForManuscript);
    }
}
