package com.mathmod.screen;

import com.mathmod.manuscript.ManuscriptReaderView;
import com.mathmod.manuscript.ManuscriptReaderViewCodec;
import com.mathmod.registry.ModItems;
import com.mathmod.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class ManuscriptReaderMenu extends AbstractContainerMenu {
    private final InteractionHand hand;
    private final ManuscriptReaderView view;

    public ManuscriptReaderMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(containerId, inventory, data.readEnum(InteractionHand.class), ManuscriptReaderViewCodec.read(data));
    }
    public ManuscriptReaderMenu(int containerId, Inventory inventory, InteractionHand hand, ManuscriptReaderView view) {
        super(ModMenus.MANUSCRIPT_READER.get(), containerId);
        this.hand = hand;
        this.view = view;
    }
    public ManuscriptReaderView view() { return view; }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(Player player) { return player.getItemInHand(hand).is(ModItems.FIELD_MANUSCRIPT.get()); }
}
