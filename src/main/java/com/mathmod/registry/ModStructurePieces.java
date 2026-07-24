package com.mathmod.registry;

import com.mathmod.MathMod;
import com.mathmod.worldgen.MathemagicianHousePiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModStructurePieces {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, MathMod.MOD_ID);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> MATHEMAGICIAN_HOUSE =
            STRUCTURE_PIECES.register("mathemagician_house", () -> MathemagicianHousePiece::new);

    private ModStructurePieces() {
    }

    public static void register(IEventBus modEventBus) {
        STRUCTURE_PIECES.register(modEventBus);
    }
}
