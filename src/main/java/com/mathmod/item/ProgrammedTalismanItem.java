package com.mathmod.item;

import com.mathmod.program.ProgramExecutionResult;
import com.mathmod.program.ProgramExecutor;
import com.mathmod.program.ProgramCosts;
import com.mathmod.program.ProgramMessageComponents;
import com.mathmod.program.ProgramNameComponents;
import com.mathmod.program.ProgramStorage;
import com.mathmod.screen.RuneProgrammerMenu;
import com.mathmod.screen.TalismanResourcesMenu;
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
import java.util.Map;

public class ProgrammedTalismanItem extends Item {
    public ProgrammedTalismanItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            TalismanUseRoute route = TalismanUseRoute.resolve(
                    ProgramStorage.get(stack).isPresent(),
                    player.isSecondaryUseActive()
            );
            switch (route) {
                case PROGRAMMER -> openProgrammer(serverPlayer, usedHand);
                case RESOURCES -> openResources(serverPlayer, usedHand);
                case CAST -> {
                    ProgramExecutionResult result = ProgramExecutor.execute(stack, serverPlayer);
                    serverPlayer.displayClientMessage(
                            result.success()
                                    ? ProgramMessageComponents.successfulCast(
                                            ProgramNameComponents.displayName(
                                                    stack,
                                                    ProgramStorage.get(stack).orElseThrow()
                                            )
                                    )
                                    : ProgramMessageComponents.executionResult(result),
                            true
                    );
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static void openProgrammer(ServerPlayer serverPlayer, InteractionHand usedHand) {
        serverPlayer.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) ->
                                new RuneProgrammerMenu(containerId, inventory, usedHand),
                        Component.translatable("screen.mathmod.rune_programmer")
                ),
                buffer -> buffer.writeEnum(usedHand)
        );
    }

    public static void openResources(ServerPlayer serverPlayer, InteractionHand usedHand) {
        serverPlayer.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) ->
                                new TalismanResourcesMenu(containerId, inventory, usedHand),
                        Component.translatable("screen.mathmod.talisman_resources")
                ),
                buffer -> buffer.writeEnum(usedHand)
        );
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        ProgramStorage.get(stack).ifPresentOrElse(
                graph -> {
                    Component spellName = ProgramNameComponents.displayName(stack, graph);
                    tooltipComponents.add(ItemTooltipStyles.identity(Component.translatable(
                            "item.mathmod.programmed_talisman.tooltip.name",
                            spellName
                    )));
                    tooltipComponents.add(ItemTooltipStyles.detail(Component.translatable(
                            "item.mathmod.programmed_talisman.tooltip.program",
                            graph.nodes().size(),
                            graph.budgetLimit()
                    )));
                    Map<String, Integer> requirements = ProgramCosts.requirementsFor(graph);
                    if (!requirements.isEmpty()) {
                        tooltipComponents.add(ItemTooltipStyles.detail(Component.translatable(
                                "item.mathmod.programmed_talisman.tooltip.cost",
                                ProgramMessageComponents.selectors(requirements)
                        )));
                    }
                    Map<String, Integer> attributes = ProgramCosts.attributeRequirementsFor(graph);
                    if (!attributes.isEmpty()) {
                        tooltipComponents.add(ItemTooltipStyles.detail(Component.translatable(
                                 "item.mathmod.programmed_talisman.tooltip.attributes",
                                 ProgramMessageComponents.attributes(attributes)
                         )));
                    }
                    tooltipComponents.add(ItemTooltipStyles.primaryAction(Component.translatable(
                            "item.mathmod.programmed_talisman.tooltip.action.cast"
                    )));
                    tooltipComponents.add(ItemTooltipStyles.secondaryAction(Component.translatable(
                            "item.mathmod.programmed_talisman.tooltip.action.resources"
                    )));
                },
                () -> {
                    tooltipComponents.add(ItemTooltipStyles.detail(Component.translatable(
                            "item.mathmod.programmed_talisman.tooltip.empty"
                    )));
                    tooltipComponents.add(ItemTooltipStyles.primaryAction(Component.translatable(
                            "item.mathmod.programmed_talisman.tooltip.action.programmer"
                    )));
                }
        );
    }
}
