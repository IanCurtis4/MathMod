package com.mathmod.screen;

import com.mathmod.item.ProgrammedTalismanItem;
import com.mathmod.program.ProgramResources;
import com.mathmod.registry.ModItems;
import com.mathmod.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class TalismanResourcesMenu extends AbstractContainerMenu {
    public static final int PROGRAMMER_BUTTON = 0;
    public static final int CLEAR_RESOURCES_BUTTON = 1;
    public static final int ADD_RESOURCE_BUTTON_BASE = 100;
    public static final int REMOVE_RESOURCE_BUTTON_BASE = 1000;

    private final InteractionHand hand;

    public TalismanResourcesMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(containerId, inventory, data.readEnum(InteractionHand.class));
    }

    public TalismanResourcesMenu(int containerId, Inventory inventory, InteractionHand hand) {
        super(ModMenus.TALISMAN_RESOURCES.get(), containerId);
        this.hand = hand;
    }

    public InteractionHand hand() {
        return hand;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == PROGRAMMER_BUTTON && player instanceof ServerPlayer serverPlayer) {
            ProgrammedTalismanItem.openProgrammer(serverPlayer, hand);
            return true;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ModItems.PROGRAMMED_TALISMAN.get())) {
            return false;
        }
        if (id == CLEAR_RESOURCES_BUTTON) {
            ProgramResources.clear(stack);
            syncHeldStack(player, stack);
            return true;
        }
        if (id >= REMOVE_RESOURCE_BUTTON_BASE) {
            ProgramResources.removeAt(stack, id - REMOVE_RESOURCE_BUTTON_BASE);
            syncHeldStack(player, stack);
            return true;
        }
        if (id >= ADD_RESOURCE_BUTTON_BASE) {
            int index = id - ADD_RESOURCE_BUTTON_BASE;
            var materials = ProgramResources.materials();
            if (index >= 0 && index < materials.size()) {
                ProgramResources.add(stack, materials.get(index).id());
                syncHeldStack(player, stack);
                return true;
            }
        }
        return false;
    }

    private void syncHeldStack(Player player, ItemStack stack) {
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
        if (player instanceof ServerPlayer serverPlayer) {
            int slot = hand == InteractionHand.MAIN_HAND
                    ? serverPlayer.getInventory().selected
                    : Inventory.SLOT_OFFHAND;
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
                    ClientboundContainerSetSlotPacket.PLAYER_INVENTORY,
                    0,
                    slot,
                    stack
            ));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(hand).is(ModItems.PROGRAMMED_TALISMAN.get());
    }
}
