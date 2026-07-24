package com.mathmod.effect;

import com.mathmod.MathMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class VitalInfusionEffect extends MobEffect {
    public VitalInfusionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xD96B8A);
        addAttributeModifier(
                Attributes.MAX_HEALTH,
                id("vital_infusion_health"),
                AttributeModifier.Operation.ADD_VALUE,
                amplifier -> 4.0D * (amplifier + 1)
        );
        addAttributeModifier(
                Attributes.ARMOR,
                id("vital_infusion_armor"),
                AttributeModifier.Operation.ADD_VALUE,
                amplifier -> 2.0D * (amplifier + 1)
        );
        addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                id("vital_infusion_damage"),
                AttributeModifier.Operation.ADD_VALUE,
                amplifier -> 1.0D * (amplifier + 1)
        );
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MathMod.MOD_ID, path);
    }
}
