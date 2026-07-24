package com.mathmod.environment;

import com.mathmod.field.SamplePoint;
import com.mathmod.util.NamespacedId;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.Arrays;
import java.util.Objects;

/** A server-only capture of one environmental generation for one execution. */
public final class EnvironmentalSamplingSession {
    private final ServerLevel level;
    private final EnvironmentalFieldPublication publication;
    private final byte[] secret;

    EnvironmentalSamplingSession(ServerLevel level, EnvironmentalFieldPublication publication, byte[] secret) {
        this.level = Objects.requireNonNull(level, "level");
        this.publication = Objects.requireNonNull(publication, "publication");
        this.secret = Arrays.copyOf(secret, 32);
    }

    public long generation() { return publication.snapshot().generation(); }

    public EnvironmentalFieldSnapshot snapshot() { return publication.snapshot(); }

    public double sample(NamespacedId requestedChannel, SamplePoint point) {
        BlockPos blockPos = BlockPos.containing(point.x(), point.y(), point.z());
        if (!level.hasChunkAt(blockPos)) throw new IllegalArgumentException("Environmental sample chunk is not loaded");
        ResourceLocation biome = level.getBiome(blockPos).unwrapKey()
                .map(key -> key.location())
                .orElseThrow(() -> new IllegalArgumentException("Environmental biome is unavailable"));
        EnvironmentalSampleInput input = new EnvironmentalSampleInput(
                level.getSeed(), level.dimension().location().toString(), biome.toString(),
                level.getMinBuildHeight(), level.getLogicalHeight(), point
        );
        return publication.snapshot().sample(publication.resolve(requestedChannel), input, secret);
    }
}
