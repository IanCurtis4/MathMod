package com.mathmod.registry;

import com.mathmod.MathMod;
import com.mathmod.block.RuneAnchorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MathMod.MOD_ID);

    public static final DeferredBlock<RuneAnchorBlock> RUNE_ANCHOR = BLOCKS.register(
            "rune_anchor",
            () -> new RuneAnchorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(0.8F)
                    .noOcclusion())
    );

    public static final DeferredBlock<Block> DEMONSTRATION_TABLE = BLOCKS.register(
            "demonstration_table",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5F)
                    .sound(SoundType.WOOD))
    );

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
