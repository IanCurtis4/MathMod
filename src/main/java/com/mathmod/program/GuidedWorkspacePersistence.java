package com.mathmod.program;

import com.mathmod.registry.ModDataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Bridges versioned workspace state to item components and legacy components. */
public final class GuidedWorkspacePersistence {
    private GuidedWorkspacePersistence() {
    }

    public static GuidedWorkspaceRead read(ItemStack stack) {
        if (stack == null) {
            return GuidedWorkspaceRead.absent();
        }

        GuidedWorkspaceState current = stack.get(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get());
        if (current != null) {
            return current.supported() && current.replayable()
                    ? GuidedWorkspaceRead.available(current)
                    : GuidedWorkspaceRead.unreplayable();
        }

        List<String> legacyInvocations = stack.get(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get());
        if (legacyInvocations == null) {
            return GuidedWorkspaceRead.absent();
        }
        String legacyName = stack.getOrDefault(ModDataComponents.PROGRAM_NAME.get(), "");
        return GuidedWorkspaceState.migrateLegacy(legacyName, legacyInvocations)
                .result()
                .filter(GuidedWorkspaceState::replayable)
                .map(GuidedWorkspaceRead::available)
                .orElseGet(GuidedWorkspaceRead::unreplayable);
    }

    /**
     * Dual-writes legacy fields for one migration window. Both representations
     * contain the same recipe; neither replaces the authoritative ProgramGraph.
     */
    public static void write(ItemStack stack, GuidedWorkspaceState state) {
        stack.set(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get(), state);
        if (state.name().isEmpty()) {
            stack.remove(ModDataComponents.PROGRAM_NAME.get());
        } else {
            stack.set(ModDataComponents.PROGRAM_NAME.get(), state.name());
        }
        if (state.invocationIds().isEmpty()) {
            stack.remove(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get());
        } else {
            stack.set(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get(), state.invocationIds());
        }
    }

    public static void clear(ItemStack stack) {
        stack.remove(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get());
        stack.remove(ModDataComponents.PROGRAM_NAME.get());
        stack.remove(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get());
    }
}
