package com.mathmod.effect;

import com.mathmod.MathMod;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public final class SoulBoundEffect extends MobEffect {
    private static final String ANCHOR_X = "mathmod:soul_bind_x";
    private static final String ANCHOR_Y = "mathmod:soul_bind_y";
    private static final String ANCHOR_Z = "mathmod:soul_bind_z";
    private static final double FREE_RADIUS = 3.0D;
    private static final double MAX_PULL = 0.16D;

    public SoulBoundEffect() {
        super(MobEffectCategory.HARMFUL, 0x76539B);
        addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                id("soul_bound_speed"),
                -0.35D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        addAttributeModifier(
                Attributes.KNOCKBACK_RESISTANCE,
                id("soul_bound_knockback"),
                0.5D,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    public static void bindTo(LivingEntity entity, Vec3 anchor) {
        CompoundTag data = entity.getPersistentData();
        data.putDouble(ANCHOR_X, anchor.x);
        data.putDouble(ANCHOR_Y, anchor.y);
        data.putDouble(ANCHOR_Z, anchor.z);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        CompoundTag data = entity.getPersistentData();
        if (!hasAnchor(data)) {
            return true;
        }

        Vec3 anchor = new Vec3(
                data.getDouble(ANCHOR_X),
                data.getDouble(ANCHOR_Y),
                data.getDouble(ANCHOR_Z)
        );
        Vec3 displacement = anchor.subtract(entity.position());
        double distance = displacement.length();
        if (distance > FREE_RADIUS && distance > 0.0001D) {
            double strength = Math.min(MAX_PULL, 0.04D + (distance - FREE_RADIUS) * 0.025D);
            Vec3 pull = displacement.normalize().scale(strength);
            entity.addDeltaMovement(pull);
            entity.hasImpulse = true;
            if (entity instanceof ServerPlayer player) {
                player.hurtMarked = true;
            }
        }

        if (entity.level() instanceof ServerLevel level && entity.tickCount % 10 == 0) {
            level.sendParticles(
                    ParticleTypes.SOUL,
                    entity.getX(),
                    entity.getY() + entity.getBbHeight() * 0.6D,
                    entity.getZ(),
                    3,
                    0.15D,
                    0.2D,
                    0.15D,
                    0.01D
            );
        }
        return true;
    }

    private static boolean hasAnchor(CompoundTag data) {
        return data.contains(ANCHOR_X, Tag.TAG_DOUBLE)
                && data.contains(ANCHOR_Y, Tag.TAG_DOUBLE)
                && data.contains(ANCHOR_Z, Tag.TAG_DOUBLE);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MathMod.MOD_ID, path);
    }
}
