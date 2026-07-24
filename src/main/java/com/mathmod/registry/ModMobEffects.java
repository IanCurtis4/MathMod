package com.mathmod.registry;

import com.mathmod.MathMod;
import com.mathmod.effect.SoulBoundEffect;
import com.mathmod.effect.VitalInfusionEffect;
import com.mathmod.effect.ParsimonyEffect;
import com.mathmod.effect.ConservationEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, MathMod.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> SOUL_BOUND =
            MOB_EFFECTS.register("soul_bound", SoulBoundEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> VITAL_INFUSION =
            MOB_EFFECTS.register("vital_infusion", VitalInfusionEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> PARSIMONY =
            MOB_EFFECTS.register("parsimony", ParsimonyEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> CONSERVATION =
            MOB_EFFECTS.register("conservation", ConservationEffect::new);

    private ModMobEffects() {
    }

    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }
}
