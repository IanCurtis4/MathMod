package com.mathmod.screen;

import com.mathmod.knowledge.FieldLedgerView;
import com.mathmod.knowledge.FieldLedgerViewCodec;
import com.mathmod.registry.ModItems;
import com.mathmod.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class FieldLedgerMenu extends AbstractContainerMenu {
    private final InteractionHand hand;
    private final FieldLedgerView view;

    public FieldLedgerMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(
                containerId,
                inventory,
                data.readEnum(InteractionHand.class),
                FieldLedgerViewCodec.read(data)
        );
    }

    public FieldLedgerMenu(
            int containerId,
            Inventory inventory,
            InteractionHand hand,
            FieldLedgerView view
    ) {
        super(ModMenus.FIELD_LEDGER.get(), containerId);
        this.hand = hand;
        this.view = view;
    }

    public FieldLedgerView view() {
        return view;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(hand).is(ModItems.FIELD_LEDGER.get());
    }
}
