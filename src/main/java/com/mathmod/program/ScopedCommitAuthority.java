package com.mathmod.program;
import com.mathmod.knowledge.PlayerKnowledge;
import net.minecraft.world.item.ItemStack;
import java.util.function.Supplier;
/** Server-owned authorities are suppliers so precommit can observe live state. */
record ScopedCommitAuthority(Supplier<ItemStack> target, Supplier<PlayerKnowledge> knowledge, ScopedCompileCancellation cancellation) {
    ScopedCommitAuthority(Supplier<ItemStack> target, PlayerKnowledge knowledge, ScopedCompileCancellation cancellation) {
        this(target, () -> knowledge, cancellation);
    }
}
