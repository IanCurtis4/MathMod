package com.mathmod.physics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Set;

/** Converts a block's canonical default state into the world-independent P11 input. */
public final class CanonicalBlockPhysicalInputAdapter {
    private static final BlockPos CANONICAL_POS = BlockPos.ZERO;
    private static final double MAX_VANILLA_PROPERTY = 1_000_000D;

    private CanonicalBlockPhysicalInputAdapter() {
    }

    public static BlockPhysicalInput from(BlockState selectedState) {
        BlockState state = selectedState.getBlock().defaultBlockState();
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        Set<String> tags = state.getTags()
                .map(tag -> tag.location().toString())
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        return new BlockPhysicalInput(
                blockId,
                blockId + "#canonical",
                tags,
                state.getCollisionShape(EmptyBlockGetter.INSTANCE, CANONICAL_POS).toAabbs().stream()
                        .map(CanonicalBlockPhysicalInputAdapter::box)
                        .toList(),
                normalized(state.getDestroySpeed(EmptyBlockGetter.INSTANCE, CANONICAL_POS)),
                normalized(state.getBlock().getExplosionResistance())
        );
    }

    private static PhysicsBox box(AABB box) {
        return new PhysicsBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    private static double normalized(double value) {
        return Double.isFinite(value) && value >= 0 ? Math.min(value, MAX_VANILLA_PROPERTY) : 0D;
    }
}
