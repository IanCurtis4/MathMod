package com.mathmod.registry;

import com.google.common.collect.ImmutableSet;
import com.mathmod.MathMod;
import com.mathmod.manuscript.ManuscriptDefinitions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Predicate;

public final class ModVillagers {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, MathMod.MOD_ID);

    public static final DeferredHolder<VillagerProfession, VillagerProfession> MATHEMAGICIAN = PROFESSIONS.register(
            "mathemagician",
            () -> new VillagerProfession(
                    "mathemagician",
                    ModVillagers::isDemonstrationTable,
                    holder -> ManuscriptDefinitions.acquisitionConfig().mathematicianProfessionEnabled()
                            && isDemonstrationTable(holder),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_CARTOGRAPHER
            )
    );

    private ModVillagers() {
    }

    public static void register(IEventBus modEventBus) {
        PROFESSIONS.register(modEventBus);
    }

    private static boolean isDemonstrationTable(Holder<PoiType> holder) {
        return holder.value() == ModPoiTypes.DEMONSTRATION_TABLE.get();
    }
}
