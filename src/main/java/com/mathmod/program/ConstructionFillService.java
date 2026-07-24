package com.mathmod.program;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/** P8-B's fail-closed, single-tick EMPTY_ONLY fill transaction. */
final class ConstructionFillService {
    private ConstructionFillService() {
    }

    static Outcome fill(ServerLevel level, ServerPlayer player, SpatialRegion region, String materialId) {
        return fill(level, player, region, materialId, ConstructionFillService::isAdmitted,
                (serverLevel, position, state) -> serverLevel.setBlock(position, state, 2));
    }

    static Outcome fill(
            ServerLevel level,
            ServerPlayer player,
            SpatialRegion region,
            String materialId,
            PositionAdmission admission,
            BlockCommitter committer
    ) {
        Item item;
        try {
            item = ItemSelectors.exactItem(materialId);
        } catch (IllegalArgumentException exception) {
            return Outcome.failure("item.mathmod.programmed_talisman.execute_bad_item");
        }
        if (!(item instanceof BlockItem blockItem) || blockItem.getBlock().defaultBlockState().hasBlockEntity()) {
            return Outcome.failure("item.mathmod.programmed_talisman.execute_bad_item");
        }

        RegionCandidatePlanner.Result candidates = RegionCandidatePlanner.plan(region);
        if (!candidates.valid()) {
            return Outcome.failure("item.mathmod.programmed_talisman.execute_region_invalid");
        }

        BlockState target = blockItem.getBlock().defaultBlockState();
        List<Change> changes = new ArrayList<>();
        for (VoxelCoordinate coordinate : candidates.plan().orElseThrow().positions()) {
            BlockPos position = new BlockPos(coordinate.x(), coordinate.y(), coordinate.z());
            ItemStack witness = new ItemStack(item);
            if (!admission.admits(level, player, position, witness)) {
                return Outcome.failure("item.mathmod.programmed_talisman.execute_region_invalid");
            }
            BlockState current = level.getBlockState(position);
            if (current.equals(target)) {
                continue;
            }
            if (!current.isAir() || current.hasBlockEntity()) {
                return Outcome.failure("item.mathmod.programmed_talisman.execute_region_invalid");
            }
            changes.add(new Change(position, current, target));
        }
        if (changes.isEmpty()) {
            return Outcome.failure("item.mathmod.programmed_talisman.execute_empty_positions");
        }
        if (changes.size() > 128 || !hasItems(player.getInventory(), item, changes.size())) {
            return Outcome.failure(changes.size() > 128
                    ? "item.mathmod.programmed_talisman.execute_region_invalid"
                    : "item.mathmod.programmed_talisman.execute_missing_items");
        }

        Escrow escrow = Escrow.take(player.getInventory(), item, changes.size());
        if (escrow == null) {
            return Outcome.failure("item.mathmod.programmed_talisman.execute_missing_items");
        }
        List<Change> committed = new ArrayList<>();
        for (Change change : changes) {
            if (!level.getBlockState(change.position()).equals(change.before())
                    || !admission.admits(level, player, change.position(), new ItemStack(item))
                    || !committer.commit(level, change.position(), change.after())) {
                rollback(level, committed);
                escrow.restore(player.getInventory());
                return Outcome.failure("item.mathmod.programmed_talisman.execute_region_invalid");
            }
            committed.add(change);
        }
        for (Change change : committed) {
            level.updateNeighborsAt(change.position(), change.after().getBlock());
        }
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
        return Outcome.success(committed.size());
    }

    private static boolean isAdmitted(ServerLevel level, ServerPlayer player, BlockPos position, ItemStack witness) {
        return level.hasChunkAt(position)
                && level.isInWorldBounds(position)
                && level.getWorldBorder().isWithinBounds(position)
                && player.mayUseItemAt(position, Direction.UP, witness);
    }

    private static void rollback(ServerLevel level, List<Change> committed) {
        for (int index = committed.size() - 1; index >= 0; index--) {
            Change change = committed.get(index);
            level.setBlock(change.position(), change.before(), 2);
        }
    }

    private static boolean hasItems(Inventory inventory, Item item, int count) {
        int available = inventory.items.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum()
                + inventory.offhand.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
        return available >= count;
    }

    private record Change(BlockPos position, BlockState before, BlockState after) { }

    private record Escrow(Item item, List<SlotTake> takes) {
        static Escrow take(Inventory inventory, Item item, int count) {
            int remaining = count;
            List<SlotTake> takes = new ArrayList<>();
            for (int slot = 0; slot < inventory.items.size() && remaining > 0; slot++) {
                ItemStack stack = inventory.items.get(slot);
                if (!stack.is(item)) continue;
                int amount = Math.min(remaining, stack.getCount());
                stack.shrink(amount);
                takes.add(new SlotTake(false, slot, amount));
                remaining -= amount;
            }
            for (int slot = 0; slot < inventory.offhand.size() && remaining > 0; slot++) {
                ItemStack stack = inventory.offhand.get(slot);
                if (!stack.is(item)) continue;
                int amount = Math.min(remaining, stack.getCount());
                stack.shrink(amount);
                takes.add(new SlotTake(true, slot, amount));
                remaining -= amount;
            }
            return remaining == 0 ? new Escrow(item, List.copyOf(takes)) : null;
        }

        void restore(Inventory inventory) {
            for (SlotTake take : takes) {
                var slots = take.offhand() ? inventory.offhand : inventory.items;
                ItemStack stack = slots.get(take.slot());
                if (stack.isEmpty()) {
                    slots.set(take.slot(), new ItemStack(item, take.amount()));
                } else {
                    stack.grow(take.amount());
                }
            }
        }
    }

    private record SlotTake(boolean offhand, int slot, int amount) { }

    @FunctionalInterface
    interface PositionAdmission {
        boolean admits(ServerLevel level, ServerPlayer player, BlockPos position, ItemStack witness);
    }

    @FunctionalInterface
    interface BlockCommitter {
        boolean commit(ServerLevel level, BlockPos position, BlockState state);
    }

    record Outcome(boolean success, String messageKey, int placed) {
        static Outcome success(int placed) { return new Outcome(true, "", placed); }
        static Outcome failure(String messageKey) { return new Outcome(false, messageKey, 0); }
    }
}
