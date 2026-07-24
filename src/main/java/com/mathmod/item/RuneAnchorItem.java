package com.mathmod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class RuneAnchorItem extends BlockItem {
    public RuneAnchorItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(ItemTooltipStyles.identity(
                Component.translatable("item.mathmod.rune_anchor.tooltip.role")
        ));
        tooltipComponents.add(ItemTooltipStyles.primaryAction(
                Component.translatable("item.mathmod.rune_anchor.tooltip.action.inscribe")
        ));
        tooltipComponents.add(ItemTooltipStyles.primaryAction(
                Component.translatable("item.mathmod.rune_anchor.tooltip.action.enact")
        ));
        tooltipComponents.add(ItemTooltipStyles.secondaryAction(
                Component.translatable("item.mathmod.rune_anchor.tooltip.action.inspect")
        ));
        tooltipComponents.add(ItemTooltipStyles.destructiveAction(
                Component.translatable("item.mathmod.rune_anchor.tooltip.action.erase")
        ));
    }
}
