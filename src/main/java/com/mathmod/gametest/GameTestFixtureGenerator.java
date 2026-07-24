package com.mathmod.gametest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;

import java.nio.file.Files;
import java.nio.file.Path;

/** Produces the small reusable structure required by dedicated GameTests. */
public final class GameTestFixtureGenerator {
    private GameTestFixtureGenerator() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Expected an output directory");
        Path output = Path.of(args[0]).resolve("data/mathmod/structure/empty.nbt");
        Files.createDirectories(output.getParent());
        NbtIo.writeCompressed(emptyStructure(), output);
    }

    private static CompoundTag emptyStructure() {
        CompoundTag root = new CompoundTag();
        root.putInt("DataVersion", 3953);
        root.put("size", ints(1, 1, 1));
        ListTag palette = new ListTag();
        CompoundTag air = new CompoundTag();
        air.put("Name", StringTag.valueOf("minecraft:air"));
        palette.add(air);
        root.put("palette", palette);
        root.put("blocks", new ListTag());
        root.put("entities", new ListTag());
        return root;
    }

    private static ListTag ints(int... values) {
        ListTag result = new ListTag();
        for (int value : values) result.add(IntTag.valueOf(value));
        return result;
    }
}
