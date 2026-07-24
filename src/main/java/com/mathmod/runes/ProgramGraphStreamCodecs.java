package com.mathmod.runes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class ProgramGraphStreamCodecs {
    public static final StreamCodec<RegistryFriendlyByteBuf, ProgramGraph> NETWORK =
            ByteBufCodecs.fromCodecWithRegistries(ProgramGraph.CODEC);

    private ProgramGraphStreamCodecs() {
    }
}
