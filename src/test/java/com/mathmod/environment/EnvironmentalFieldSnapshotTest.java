package com.mathmod.environment;

import com.mathmod.field.SamplePoint;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvironmentalFieldSnapshotTest {
    private static final byte[] FIXTURE_SECRET = secret();
    private static final NamespacedId SPATIAL = NamespacedId.of("mathmod", "spatial");
    private static final NamespacedId STABILITY = NamespacedId.of("mathmod", "stability");

    @Test
    void saltedValueNoiseMatchesTheFrozenGoldenVectors() {
        assertBits("BFA0861514E94BBA", sample("minecraft:overworld", SPATIAL, 0.5D, 64.5D, 0.5D, 32));
        assertBits("3FD54FFC98EE4996", sample("minecraft:overworld", SPATIAL, -31.5D, 80.5D, 48.5D, 32));
        assertBits("3FCB79D835ABDB50", sample("minecraft:the_nether", SPATIAL, 0.5D, 64.5D, 0.5D, 32));
        assertBits("BFE9FB9EB541A077", sample("minecraft:overworld", STABILITY, 0.5D, 64.5D, 0.5D, 32));
        assertBits("BFB4A83F5404E3B8", sample("minecraft:overworld", SPATIAL, 128.5D, -12.5D, -96.5D, 64));
    }

    @Test
    void channelAndDimensionKeysProduceIndependentNoiseStreams() {
        double overworld = sample("minecraft:overworld", SPATIAL, 0.5D, 64.5D, 0.5D, 32);
        assertNotEquals(overworld, sample("minecraft:the_nether", SPATIAL, 0.5D, 64.5D, 0.5D, 32));
        assertNotEquals(overworld, sample("minecraft:overworld", STABILITY, 0.5D, 64.5D, 0.5D, 32));
    }

    @Test
    void snapshotComposesOnlyKnownBoundedChannels() {
        EnvironmentalFieldSnapshot snapshot = EnvironmentalFieldSnapshot.builtIns();
        double plains = snapshot.sample(SPATIAL, input("minecraft:overworld", "minecraft:plains"), FIXTURE_SECRET);
        double nether = snapshot.sample(SPATIAL, input("minecraft:the_nether", "minecraft:nether_wastes"), FIXTURE_SECRET);

        assertEquals(3, snapshot.channels().size());
        assertNotEquals(plains, nether);
        assertThrows(IllegalArgumentException.class, () -> snapshot.sample(
                NamespacedId.of("mathmod", "missing"), input("minecraft:overworld", "minecraft:plains"), FIXTURE_SECRET
        ));
    }

    @Test
    void aliasesFlattenToCanonicalChannelsAndRejectCycles() {
        NamespacedId legacy = NamespacedId.of("mathmod", "old_spatial");
        NamespacedId older = NamespacedId.of("mathmod", "older_spatial");
        EnvironmentalFieldPublication publication = new EnvironmentalFieldPublication(
                EnvironmentalFieldSnapshot.builtIns(), Map.of(legacy, SPATIAL, older, legacy)
        );

        assertEquals(SPATIAL, publication.resolve(legacy));
        assertEquals(SPATIAL, publication.resolve(older));
        assertThrows(IllegalArgumentException.class, () -> new EnvironmentalFieldPublication(
                EnvironmentalFieldSnapshot.builtIns(), Map.of(legacy, older, older, legacy)
        ));
    }

    private static EnvironmentalSampleInput input(String dimension, String biome) {
        return new EnvironmentalSampleInput(123456789L, dimension, biome, -64, 384, new SamplePoint(0.5D, 64.5D, 0.5D));
    }

    private static double sample(String dimension, NamespacedId channel, double x, double y, double z, int scale) {
        return SaltedValueNoise.sample(FIXTURE_SECRET, 123456789L, dimension, channel, new SamplePoint(x, y, z), scale);
    }

    private static void assertBits(String expected, double value) {
        assertEquals(Long.parseUnsignedLong(expected, 16), Double.doubleToRawLongBits(value));
    }

    private static byte[] secret() {
        byte[] secret = new byte[32];
        for (int index = 0; index < secret.length; index++) secret[index] = (byte) index;
        return secret;
    }
}
