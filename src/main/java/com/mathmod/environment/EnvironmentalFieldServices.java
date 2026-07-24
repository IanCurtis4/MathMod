package com.mathmod.environment;

import com.mathmod.field.SamplePoint;
import com.mathmod.util.NamespacedId;
import net.minecraft.server.level.ServerLevel;

import java.util.Arrays;

/** Server-owned P13 snapshot holder and bridge from Minecraft registry state to pure sample inputs. */
public final class EnvironmentalFieldServices {
    private static volatile EnvironmentalFieldPublication active =
            new EnvironmentalFieldPublication(EnvironmentalFieldSnapshot.builtIns(), java.util.Map.of());

    private EnvironmentalFieldServices() { }

    public static EnvironmentalFieldSnapshot snapshot() { return active.snapshot(); }

    public static NamespacedId resolveChannel(NamespacedId id) { return active.resolve(id); }

    public static synchronized void publish(EnvironmentalFieldSnapshot candidate) {
        publish(candidate, java.util.Map.of());
    }

    public static synchronized void publish(EnvironmentalFieldSnapshot candidate, java.util.Map<NamespacedId, NamespacedId> aliases) {
        active = new EnvironmentalFieldPublication(candidate.withGeneration(active.snapshot().generation() + 1L), aliases);
    }

    public static double sample(ServerLevel level, NamespacedId channel, SamplePoint point) {
        return capture(level).sample(channel, point);
    }

    public static EnvironmentalSamplingSession capture(ServerLevel level) {
        return new EnvironmentalSamplingSession(level, active, secretInternal(level));
    }

    public static synchronized byte[] secret(ServerLevel level) {
        return Arrays.copyOf(secretInternal(level), 32);
    }

    private static byte[] secretInternal(ServerLevel level) {
        return WorldFieldSecretData.get(level).secret();
    }
}
