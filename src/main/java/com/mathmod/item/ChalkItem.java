package com.mathmod.item;

import com.mathmod.block.RuneAnchorBlockEntity;
import com.mathmod.program.AnchorProgramPreset;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class ChalkItem extends Item {
    public ChalkItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            AnchorProgramPreset nextPreset = ChalkPresetStorage.cycle(stack);
            player.displayClientMessage(Component.translatable(
                    "item.mathmod.chalk.mode_changed",
                    Component.translatable(nextPreset.displayNameKey())
            ), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (!level.isClientSide && player != null) {
            if (level.getBlockEntity(context.getClickedPos()) instanceof RuneAnchorBlockEntity anchor) {
                if (player.isSecondaryUseActive()) {
                    String messageKey = anchor.clearProgram()
                            ? "item.mathmod.chalk.anchor_cleared"
                            : "item.mathmod.chalk.anchor_clear_empty";
                    player.displayClientMessage(Component.translatable(messageKey), true);
                } else {
                    AnchorProgramPreset preset = ChalkPresetStorage.get(context.getItemInHand());
                    if (anchor.setProgram(preset)) {
                        player.displayClientMessage(Component.translatable(preset.saveMessageKey()), true);
                    } else {
                        player.displayClientMessage(Component.translatable("item.mathmod.chalk.anchor_invalid"), true);
                    }
                }
            } else {
                player.displayClientMessage(Component.translatable("item.mathmod.chalk.anchor_hint"), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        AnchorProgramPreset preset = ChalkPresetStorage.get(stack);
        tooltipComponents.add(ItemTooltipStyles.identity(Component.translatable(
                "item.mathmod.chalk.tooltip.mode",
                Component.translatable(preset.displayNameKey())
        )));
        tooltipComponents.add(ItemTooltipStyles.secondaryAction(
                Component.translatable("item.mathmod.chalk.tooltip.action.cycle")
        ));
        tooltipComponents.add(ItemTooltipStyles.primaryAction(
                Component.translatable("item.mathmod.chalk.tooltip.action.inscribe")
        ));
        tooltipComponents.add(ItemTooltipStyles.destructiveAction(
                Component.translatable("item.mathmod.chalk.tooltip.action.erase")
        ));
    }
}
