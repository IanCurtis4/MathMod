package com.mathmod.program;

import com.mathmod.registry.ModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public final class PlayerCastModifiers {
    private PlayerCastModifiers() {
    }

    public static CastModifiers snapshot(LivingEntity entity) {
        int parsimonyLevel = effectLevel(entity, ModMobEffects.PARSIMONY);
        int conservationLevel = effectLevel(entity, ModMobEffects.CONSERVATION);
        return new CastModifiers(
                parsimonyLevel,
                conservationLevel * CastModifiers.CONSERVATION_CHANCE_PER_LEVEL
        );
    }

    private static int effectLevel(LivingEntity entity, Holder<MobEffect> effect) {
        var instance = entity.getEffect(effect);
        return instance == null ? 0 : instance.getAmplifier() + 1;
    }
}
