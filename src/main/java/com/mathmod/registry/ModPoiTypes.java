package com.mathmod.registry;

import com.mathmod.MathMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public final class ModPoiTypes {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, MathMod.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> DEMONSTRATION_TABLE = POI_TYPES.register(
            "demonstration_table",
            () -> new PoiType(Set.copyOf(ModBlocks.DEMONSTRATION_TABLE.get().getStateDefinition().getPossibleStates()), 1, 1)
    );

    private ModPoiTypes() {
    }

    public static void register(IEventBus modEventBus) {
        POI_TYPES.register(modEventBus);
    }
}
