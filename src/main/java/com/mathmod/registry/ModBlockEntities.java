package com.mathmod.registry;

import com.mathmod.MathMod;
import com.mathmod.block.RuneAnchorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MathMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RuneAnchorBlockEntity>> RUNE_ANCHOR =
            BLOCK_ENTITIES.register("rune_anchor", () -> new BlockEntityType<>(
                    RuneAnchorBlockEntity::new,
                    Set.of(ModBlocks.RUNE_ANCHOR.get()),
                    null
            ));

    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
