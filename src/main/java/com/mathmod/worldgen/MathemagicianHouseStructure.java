package com.mathmod.worldgen;

import com.mathmod.acquisition.MathModServerConfig;
import com.mathmod.registry.ModStructures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

/** A rare field house in plains-village biomes; it does not rewrite vanilla village pools. */
public final class MathemagicianHouseStructure extends Structure {
    public static final MapCodec<MathemagicianHouseStructure> CODEC = simpleCodec(MathemagicianHouseStructure::new);

    public MathemagicianHouseStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!MathModServerConfig.snapshot().mathematicianHouseEnabled()) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            BlockPos origin = new BlockPos(
                    context.chunkPos().getMiddleBlockX() - 3,
                    context.chunkGenerator().getFirstOccupiedHeight(
                            context.chunkPos().getMiddleBlockX(),
                            context.chunkPos().getMiddleBlockZ(),
                            Heightmap.Types.WORLD_SURFACE_WG,
                            context.heightAccessor(),
                            context.randomState()
                    ),
                    context.chunkPos().getMiddleBlockZ() - 3
            );
            builder.addPiece(new MathemagicianHousePiece(origin));
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.MATHEMAGICIAN_HOUSE.get();
    }
}
