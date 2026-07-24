package com.mathmod.worldgen;

import com.mathmod.MathMod;
import com.mathmod.registry.ModBlocks;
import com.mathmod.registry.ModStructurePieces;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.LootTable;

public final class MathemagicianHousePiece extends StructurePiece {
    private static final int WIDTH = 7;
    private static final int HEIGHT = 6;
    private static final ResourceKey<LootTable> HOUSE_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE,
            MathMod.id("chests/mathemagician_house")
    );

    public MathemagicianHousePiece(BlockPos origin) {
        super(ModStructurePieces.MATHEMAGICIAN_HOUSE.get(), 0, new BoundingBox(
                origin.getX(), origin.getY(), origin.getZ(),
                origin.getX() + WIDTH - 1, origin.getY() + HEIGHT - 1, origin.getZ() + WIDTH - 1
        ));
    }

    public MathemagicianHousePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieces.MATHEMAGICIAN_HOUSE.get(), tag);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
    }

    @Override
    public void postProcess(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox chunkBounds,
            ChunkPos chunkPos,
            BlockPos pivot
    ) {
        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < WIDTH; z++) {
                placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), x, 0, z, chunkBounds);
                placeBlock(level, Blocks.SPRUCE_PLANKS.defaultBlockState(), x, 4, z, chunkBounds);
                if (x >= 1 && x <= WIDTH - 2 && z >= 1 && z <= WIDTH - 2) {
                    placeBlock(level, Blocks.SPRUCE_PLANKS.defaultBlockState(), x, 5, z, chunkBounds);
                }
            }
        }

        for (int y = 1; y <= 3; y++) {
            for (int x = 0; x < WIDTH; x++) {
                for (int z = 0; z < WIDTH; z++) {
                    if (x == 0 || x == WIDTH - 1 || z == 0 || z == WIDTH - 1) {
                        placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), x, y, z, chunkBounds);
                    } else {
                        placeBlock(level, Blocks.AIR.defaultBlockState(), x, y, z, chunkBounds);
                    }
                }
            }
        }

        for (int y = 1; y <= 3; y++) {
            placeBlock(level, Blocks.OAK_LOG.defaultBlockState(), 0, y, 0, chunkBounds);
            placeBlock(level, Blocks.OAK_LOG.defaultBlockState(), WIDTH - 1, y, 0, chunkBounds);
            placeBlock(level, Blocks.OAK_LOG.defaultBlockState(), 0, y, WIDTH - 1, chunkBounds);
            placeBlock(level, Blocks.OAK_LOG.defaultBlockState(), WIDTH - 1, y, WIDTH - 1, chunkBounds);
        }
        placeBlock(level, Blocks.GLASS_PANE.defaultBlockState(), 0, 2, 2, chunkBounds);
        placeBlock(level, Blocks.GLASS_PANE.defaultBlockState(), WIDTH - 1, 2, 4, chunkBounds);
        placeBlock(level, Blocks.GLASS_PANE.defaultBlockState(), 2, 2, WIDTH - 1, chunkBounds);
        placeBlock(level, Blocks.AIR.defaultBlockState(), 3, 1, 0, chunkBounds);
        placeBlock(level, Blocks.AIR.defaultBlockState(), 3, 2, 0, chunkBounds);
        placeBlock(level, Blocks.OAK_DOOR.defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 3, 1, 0, chunkBounds);
        placeBlock(level, Blocks.OAK_DOOR.defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3, 2, 0, chunkBounds);

        placeBlock(level, ModBlocks.DEMONSTRATION_TABLE.get().defaultBlockState(), 3, 1, 3, chunkBounds);
        placeBlock(level, Blocks.LANTERN.defaultBlockState(), 3, 3, 3, chunkBounds);
        createChest(level, chunkBounds, random, 1, 1, 4, HOUSE_LOOT);
    }
}
