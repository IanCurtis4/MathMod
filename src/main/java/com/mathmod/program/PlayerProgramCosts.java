package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public final class PlayerProgramCosts {
    private PlayerProgramCosts() {
    }

    public static ProgramCostResult ensureAvailable(ServerPlayer player, ProgramGraph graph) {
        return ensureAvailable(player, graph, List.of());
    }

    public static ProgramCostResult ensureAvailable(ServerPlayer player, ProgramGraph graph, List<ResourceSelection> selections) {
        ProgramCostPlan plan = planFor(player, graph, selections);
        if (!plan.success()) {
            return ProgramCostResult.failure(plan);
        }
        return ProgramCostResult.ok();
    }

    public static ProgramCostResult consume(ServerPlayer player, ProgramGraph graph) {
        return consume(player, graph, List.of());
    }

    public static ProgramCostResult consume(ServerPlayer player, ProgramGraph graph, List<ResourceSelection> selections) {
        ProgramCostPlan plan = planFor(player, graph, selections, PlayerCastModifiers.snapshot(player));
        return consumePlanned(player, plan, player.getRandom());
    }

    public static ProgramCostResult consumePlanned(ServerPlayer player, ProgramCostPlan plan, RandomSource random) {
        if (!plan.success()) {
            return ProgramCostResult.failure(plan);
        }
        CostEscrow escrow = escrowPlanned(player, plan);
        if (escrow == null) {
            return ProgramCostResult.failure("item.mathmod.programmed_talisman.execute_missing_items");
        }
        escrow.settle(random);
        return ProgramCostResult.ok();
    }

    /**
     * Removes the maximum possible payment before an effect mutates the world. Callers must settle
     * after success or restore on failure; conservation refunds are applied only during settlement.
     */
    public static CostEscrow escrowPlanned(ServerPlayer player, ProgramCostPlan plan) {
        if (!plan.success()) {
            return null;
        }
        if (player.getAbilities().instabuild) {
            return CostEscrow.creative(player, plan);
        }

        Inventory inventory = player.getInventory();
        List<Withdrawal> withdrawals = new ArrayList<>();
        for (ProgramCostLine line : plan.lines()) {
            if (!line.consumed()) {
                continue;
            }
            List<StackTake> takes = takeFrom(inventory.items, false, line.selector(), line.quantity());
            int removed = takes.stream().mapToInt(take -> take.stack().getCount()).sum();
            if (removed < line.quantity()) {
                takes.addAll(takeFrom(inventory.offhand, true, line.selector(), line.quantity() - removed));
            }
            int total = takes.stream().mapToInt(take -> take.stack().getCount()).sum();
            if (total < line.quantity()) {
                restore(inventory, takes);
                withdrawals.forEach(withdrawal -> PlayerProgramCosts.restore(inventory, withdrawal.takes()));
                inventory.setChanged();
                player.containerMenu.broadcastChanges();
                return null;
            }
            withdrawals.add(new Withdrawal(line, List.copyOf(takes)));
        }
        inventory.setChanged();
        player.containerMenu.broadcastChanges();
        return new CostEscrow(player, plan, withdrawals, false);
    }

    public static ProgramCostPlan planFor(Player player, ProgramGraph graph) {
        return planFor(player, graph, List.of());
    }

    public static ProgramCostPlan planFor(Player player, ProgramGraph graph, List<ResourceSelection> selections) {
        return planFor(player, graph, selections, PlayerCastModifiers.snapshot(player));
    }

    public static ProgramCostPlan planFor(
            Player player,
            ProgramGraph graph,
            List<ResourceSelection> selections,
            CastModifiers modifiers
    ) {
        return planFor(graph, selections, player.getInventory(), player.getAbilities().instabuild, modifiers);
    }

    public static ProgramCostPlan planFor(ProgramGraph graph, List<ResourceSelection> selections, Inventory inventory, boolean creative) {
        return planFor(graph, selections, inventory, creative, CastModifiers.none());
    }

    public static ProgramCostPlan planFor(
            ProgramGraph graph,
            List<ResourceSelection> selections,
            Inventory inventory,
            boolean creative,
            CastModifiers modifiers
    ) {
        return ProgramCosts.planForSelectorCounter(
                graph,
                selections,
                selector -> countMatching(inventory, selector),
                creative,
                modifiers
        );
    }

    private static int countMatching(Inventory inventory, String selector) {
        return countMatching(inventory.items, selector) + countMatching(inventory.offhand, selector);
    }

    private static int countMatching(NonNullList<ItemStack> stacks, String selector) {
        int count = 0;
        for (ItemStack stack : stacks) {
            try {
                if (!stack.isEmpty() && ItemSelectors.matches(stack, selector)) {
                    count += stack.getCount();
                }
            } catch (IllegalArgumentException ignored) {
                return 0;
            }
        }
        return count;
    }

    private static List<StackTake> takeFrom(NonNullList<ItemStack> stacks, boolean offhand, String selector, int quantity) {
        int remaining = quantity;
        List<StackTake> takes = new ArrayList<>();
        for (int i = 0; i < stacks.size(); i++) {
            if (remaining <= 0) {
                break;
            }
            ItemStack stack = stacks.get(i);
            try {
                if (stack.isEmpty() || !ItemSelectors.matches(stack, selector)) {
                    continue;
                }
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            int removed = Math.min(stack.getCount(), remaining);
            ItemStack taken = stack.copyWithCount(removed);
            stack.shrink(removed);
            if (stack.isEmpty()) {
                stacks.set(i, ItemStack.EMPTY);
            }
            takes.add(new StackTake(offhand, i, taken));
            remaining -= removed;
        }
        return takes;
    }

    private static void restore(Inventory inventory, List<StackTake> takes) {
        for (int i = takes.size() - 1; i >= 0; i--) {
            StackTake take = takes.get(i);
            NonNullList<ItemStack> stacks = take.offhand() ? inventory.offhand : inventory.items;
            ItemStack current = stacks.get(take.slot());
            if (current.isEmpty()) {
                stacks.set(take.slot(), take.stack().copy());
            } else if (ItemStack.isSameItemSameComponents(current, take.stack())) {
                current.grow(take.stack().getCount());
            } else {
                inventory.placeItemBackInInventory(take.stack().copy());
            }
        }
    }

    public static final class CostEscrow {
        private final ServerPlayer player;
        private final ProgramCostPlan plan;
        private final List<Withdrawal> withdrawals;
        private final boolean creative;
        private boolean closed;

        private CostEscrow(ServerPlayer player, ProgramCostPlan plan, List<Withdrawal> withdrawals, boolean creative) {
            this.player = player;
            this.plan = plan;
            this.withdrawals = List.copyOf(withdrawals);
            this.creative = creative;
        }

        private static CostEscrow creative(ServerPlayer player, ProgramCostPlan plan) {
            return new CostEscrow(player, plan, List.of(), true);
        }

        public void settle(RandomSource random) {
            if (closed) {
                return;
            }
            if (!creative) {
                Inventory inventory = player.getInventory();
                for (Withdrawal withdrawal : withdrawals) {
                    int consumed = ConservationRolls.consumedQuantity(
                            withdrawal.line().quantity(),
                            plan.modifiers().conservationChance(),
                            random::nextDouble
                    );
                    restoreQuantity(inventory, withdrawal.takes(), withdrawal.line().quantity() - consumed);
                }
                inventory.setChanged();
                player.containerMenu.broadcastChanges();
            }
            closed = true;
        }

        public void restore() {
            if (closed) {
                return;
            }
            if (!creative) {
                Inventory inventory = player.getInventory();
                withdrawals.forEach(withdrawal -> PlayerProgramCosts.restore(inventory, withdrawal.takes()));
                inventory.setChanged();
                player.containerMenu.broadcastChanges();
            }
            closed = true;
        }

        private static void restoreQuantity(Inventory inventory, List<StackTake> takes, int quantity) {
            int remaining = quantity;
            for (StackTake take : takes) {
                if (remaining <= 0) {
                    break;
                }
                int count = Math.min(remaining, take.stack().getCount());
                PlayerProgramCosts.restore(inventory, List.of(new StackTake(take.offhand(), take.slot(), take.stack().copyWithCount(count))));
                remaining -= count;
            }
        }
    }

    private record Withdrawal(ProgramCostLine line, List<StackTake> takes) {
    }

    private record StackTake(boolean offhand, int slot, ItemStack stack) {
    }
}
