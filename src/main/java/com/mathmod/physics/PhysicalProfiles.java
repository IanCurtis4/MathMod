package com.mathmod.physics;

import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Server-authoritative holder for the complete immutable physical-profile snapshot. */
public final class PhysicalProfiles {
    private static volatile PhysicalProfileSnapshot active = new PhysicalProfileSnapshot(
            0, PhysicsPolicy.defaults(), List.of()
    );

    private PhysicalProfiles() {
    }

    public static PhysicalProfileSnapshot snapshot() {
        return active;
    }

    public static BlockPhysicalProfile resolve(BlockState state) {
        return active.resolve(CanonicalBlockPhysicalInputAdapter.from(state));
    }

    /**
     * Publishes a fully validated candidate in one volatile write. Failed callers never
     * reach this method, so the prior snapshot and its cache remain usable.
     */
    static synchronized void publishData(PhysicsPolicy policy, List<PhysicalProfileDeclaration> declarations) {
        PhysicalProfileSnapshot current = active;
        active = new PhysicalProfileSnapshot(current.version() + 1, policy, declarations);
    }
}
