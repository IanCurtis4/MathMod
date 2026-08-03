package com.mathmod.screen;

import com.mathmod.item.ProgrammedTalismanItem;
import com.mathmod.knowledge.KnowledgePolicy;
import com.mathmod.knowledge.KnowledgeService;
import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.CustomSpellInvocation;
import com.mathmod.program.CustomSpellWorkspace;
import com.mathmod.program.ProgramNames;
import com.mathmod.program.ProgramPresets;
import com.mathmod.program.ProgramStorage;
import com.mathmod.program.ScopedFunctionalProjection;
import com.mathmod.program.ScopedFunctionalProjectionWireCodec;
import com.mathmod.program.ScopedFunctionalInscriptionEntryPoint;
import com.mathmod.registry.ModItems;
import com.mathmod.registry.ModMenus;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ValidationResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class RuneProgrammerMenu extends AbstractContainerMenu {
    public static final int SAVE_HOP_BUTTON = ProgramPresets.HOP_PRESET_ID;
    public static final int SAVE_DASH_BUTTON = ProgramPresets.DASH_PRESET_ID;
    public static final int SAVE_RAY_MARKER_BUTTON = ProgramPresets.RAY_MARKER_PRESET_ID;
    public static final int SAVE_BLINK_BUTTON = ProgramPresets.BLINK_PRESET_ID;
    public static final int SAVE_LIFT_BUTTON = ProgramPresets.LIFT_PRESET_ID;
    public static final int CLEAR_BUTTON = 5;
    public static final int SAVE_CUSTOM_BUTTON = 6;
    public static final int RESET_CUSTOM_BUTTON = 7;
    public static final int OPEN_RESOURCES_BUTTON = 8;
    public static final int UNDO_CUSTOM_BUTTON = 50;
    public static final int CUSTOM_ACTION_BUTTON_BASE = 100;

    private final InteractionHand hand;
    private final Player menuPlayer;
    private final ScopedFunctionalProjection functionalProjection;
    private final ItemStack capturedProjectionTarget;
    private boolean projectionValid = true;
    private final CustomSpellWorkspace customWorkspace = new CustomSpellWorkspace();
    private String customSpellName = "";

    public RuneProgrammerMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(containerId, inventory, data.readEnum(InteractionHand.class), ScopedFunctionalProjectionWireCodec.read(data));
    }

    public RuneProgrammerMenu(int containerId, Inventory inventory, InteractionHand hand) {
        this(containerId, inventory, hand, ScopedFunctionalProjection.unavailable());
    }

    public RuneProgrammerMenu(int containerId, Inventory inventory, InteractionHand hand, ScopedFunctionalProjection functionalProjection) {
        super(ModMenus.RUNE_PROGRAMMER.get(), containerId);
        this.hand = hand;
        this.menuPlayer = inventory.player;
        this.functionalProjection = functionalProjection;
        this.capturedProjectionTarget = inventory.player.level().isClientSide ? ItemStack.EMPTY : inventory.player.getItemInHand(hand).copy();
        addDataSlot(new net.minecraft.world.inventory.DataSlot() {
            @Override public int get() { return projectionStillBound(inventory.player) ? 1 : 0; }
            @Override public void set(int value) { projectionValid = value != 0; }
        });
        loadStoredCustomWorkspace(inventory.player);
    }

    public InteractionHand hand() {
        return hand;
    }
    public ScopedFunctionalProjection functionalProjection() { return projectionStillBound(menuPlayer) ? functionalProjection : ScopedFunctionalProjection.unavailable(functionalProjection.graphState()); }

    public boolean setCustomSpellName(Player player, String customSpellName) {
        if (!canMutateWorkspace(player)) {
            return false;
        }
        this.customSpellName = ProgramNames.sanitizeOptional(customSpellName);
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!canMutateWorkspace(player)) {
            return false;
        }
        ItemStack stack = player.getItemInHand(hand);

        var selectedPreset = ProgramPresets.presetForButton(id);
        if (selectedPreset.isPresent()) {
            var requirement = KnowledgePolicy.requirementFor(selectedPreset.orElseThrow());
            if (!KnowledgePolicy.canConstruct(KnowledgeService.get(player), selectedPreset.orElseThrow())) {
                player.displayClientMessage(Component.translatable(
                        "knowledge.mathmod.construction.locked",
                        Component.translatable(requirement.orElseThrow().titleTranslationKey())
                ), true);
                return true;
            }
            if (selectedPreset.get().id().equals("mathmod:factored_leap")) {
                if (!(player instanceof ServerPlayer serverPlayer)) return false;
                boolean success = ScopedFunctionalInscriptionEntryPoint.tryInscribeFactoredLeap(
                        serverPlayer, hand, () -> serverPlayer.containerMenu == this && stillValid(serverPlayer));
                if (success) {
                    invalidateProjection();
                    syncHeldStack(player, stack);
                    player.displayClientMessage(Component.translatable(
                            "item.mathmod.programmed_talisman.saved", 21, 24), true);
                }
                return true;
            }
            ProgramGraph graph = selectedPreset.get().graph();
            ValidationResult result = ProgramStorage.saveValidated(stack, graph);
            if (result.valid()) {
                invalidateProjection();
                syncHeldStack(player, stack);
                player.displayClientMessage(Component.translatable(
                        "item.mathmod.programmed_talisman.saved",
                        result.budgetUsed(),
                        graph.budgetLimit()
                ), true);
            } else {
                player.displayClientMessage(Component.translatable(
                        "item.mathmod.programmed_talisman.invalid",
                        result.issues().size()
                ), true);
            }
            return true;
        }

        if (id == CLEAR_BUTTON) {
            ProgramStorage.clear(stack);
            invalidateProjection();
            syncHeldStack(player, stack);
            player.displayClientMessage(Component.translatable("item.mathmod.programmed_talisman.cleared"), true);
            return true;
        }

        if (id == SAVE_CUSTOM_BUTTON) {
            if (!KnowledgePolicy.canEdit(KnowledgeService.get(player), customWorkspace.actions())) {
                player.displayClientMessage(
                        Component.translatable("knowledge.mathmod.construction.custom_locked"),
                        true
                );
                return true;
            }
            ProgramGraph graph = customWorkspace.toGraph();
            ValidationResult result = ProgramStorage.saveValidatedCustom(
                    stack,
                    graph,
                    customSpellName,
                    customWorkspace.invocations()
            );
            if (result.valid()) {
                invalidateProjection();
                syncHeldStack(player, stack);
                player.displayClientMessage(Component.translatable(
                        "item.mathmod.programmed_talisman.saved",
                        result.budgetUsed(),
                        graph.budgetLimit()
                ), true);
            } else {
                player.displayClientMessage(Component.translatable(
                        "item.mathmod.programmed_talisman.invalid",
                        result.issues().size()
                ), true);
            }
            return true;
        }

        if (id == RESET_CUSTOM_BUTTON) {
            customWorkspace.clear();
            customSpellName = "";
            player.displayClientMessage(Component.translatable("item.mathmod.programmed_talisman.custom_reset"), true);
            return true;
        }

        if (id == UNDO_CUSTOM_BUTTON) {
            boolean changed = customWorkspace.undoLast();
            player.displayClientMessage(Component.translatable(changed
                    ? "item.mathmod.programmed_talisman.custom_undo"
                    : "item.mathmod.programmed_talisman.custom_undo_empty"), true);
            return true;
        }

        if (id == OPEN_RESOURCES_BUTTON && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            ProgrammedTalismanItem.openResources(serverPlayer, hand);
            return true;
        }

        if (id >= CUSTOM_ACTION_BUTTON_BASE) {
            return CustomSpellAction.byOrdinal(id - CUSTOM_ACTION_BUTTON_BASE)
                    .map(action -> {
                        if (!KnowledgePolicy.canUse(KnowledgeService.get(player), action)) {
                            KnowledgePolicy.requirementFor(action).ifPresent(requirement ->
                                    player.displayClientMessage(Component.translatable(
                                            "knowledge.mathmod.construction.locked",
                                            Component.translatable(requirement.titleTranslationKey())
                                    ), true));
                            return true;
                        }
                        customWorkspace.apply(action);
                        return true;
                    })
                    .orElse(false);
        }

        return false;
    }

    public boolean applyCustomInvocation(Player player, CustomSpellInvocation invocation) {
        if (!canMutateWorkspace(player)) {
            return false;
        }
        if (!KnowledgePolicy.canUse(KnowledgeService.get(player), invocation.action())) {
            KnowledgePolicy.requirementFor(invocation.action()).ifPresent(requirement ->
                    player.displayClientMessage(Component.translatable(
                            "knowledge.mathmod.construction.locked",
                            Component.translatable(requirement.titleTranslationKey())
                    ), true));
            return false;
        }
        customWorkspace.apply(invocation);
        return true;
    }

    private void loadStoredCustomWorkspace(Player player) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ModItems.PROGRAMMED_TALISMAN.get())) {
            return;
        }
        customWorkspace.loadInvocations(ProgramStorage.getCustomInvocations(stack));
        ProgramStorage.getName(stack).ifPresent(name -> customSpellName = ProgramNames.sanitizeOptional(name));
    }

    private boolean canMutateWorkspace(Player player) {
        return player.containerMenu == this
                && stillValid(player)
                && ItemStack.isSameItemSameComponents(player.getItemInHand(hand), capturedProjectionTarget);
    }

    private boolean projectionStillBound(Player player) {
        if (!projectionValid || player.level().isClientSide) return projectionValid;
        if (!ItemStack.isSameItemSameComponents(player.getItemInHand(hand), capturedProjectionTarget)) projectionValid = false;
        return projectionValid;
    }

    private void invalidateProjection() { projectionValid = false; }

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

    public static ProgramGraph graphForButton(int id) {
        return ProgramPresets.presetForButton(id)
                .map(com.mathmod.program.TalismanPreset::graph)
                .orElseThrow(() -> new IllegalArgumentException("Unknown preset button id " + id));
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
