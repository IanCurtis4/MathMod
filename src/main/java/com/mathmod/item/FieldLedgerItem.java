package com.mathmod.item;

import com.mathmod.knowledge.FieldLedgerView;
import com.mathmod.knowledge.FieldLedgerViewCodec;
import com.mathmod.knowledge.KnowledgeDefinitions;
import com.mathmod.knowledge.KnowledgeService;
import com.mathmod.screen.FieldLedgerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public final class FieldLedgerItem extends Item {
    public FieldLedgerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand usedHand
    ) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            FieldLedgerView view = FieldLedgerView.from(
                    KnowledgeService.get(serverPlayer),
                    KnowledgeDefinitions.snapshot()
            );
            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (containerId, inventory, menuPlayer) ->
                                    new FieldLedgerMenu(containerId, inventory, usedHand, view),
                            Component.translatable("screen.mathmod.field_ledger")
                    ),
                    buffer -> {
                        buffer.writeEnum(usedHand);
                        FieldLedgerViewCodec.write(buffer, view);
                    }
            );
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
        tooltipComponents.add(ItemTooltipStyles.detail(
                Component.translatable("item.mathmod.field_ledger.tooltip")
        ));
        tooltipComponents.add(ItemTooltipStyles.primaryAction(
                Component.translatable("item.mathmod.field_ledger.tooltip.action")
        ));
    }
}
