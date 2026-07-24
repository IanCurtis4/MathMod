package com.mathmod.registry;

import com.mathmod.MathMod;
import com.mathmod.worldgen.MathemagicianHouseStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, MathMod.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<MathemagicianHouseStructure>> MATHEMAGICIAN_HOUSE =
            STRUCTURE_TYPES.register("mathemagician_house", () -> () -> MathemagicianHouseStructure.CODEC);

    private ModStructures() {
    }

    public static void register(IEventBus modEventBus) {
        STRUCTURE_TYPES.register(modEventBus);
    }
}
