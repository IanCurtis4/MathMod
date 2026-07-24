package com.mathmod.environment;

import com.mathmod.field.SamplePoint;
import com.mathmod.util.NamespacedId;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/** Exact schema-one implementation of {@code mathmod:salted_value_v1}. */
public final class SaltedValueNoise {
    private static final byte[] PREFIX = "mathmod:salted_value_v1".getBytes(StandardCharsets.UTF_8);
    private static final double TWO_TO_NEGATIVE_53 = 1.0D / 9_007_199_254_740_992.0D;

    private SaltedValueNoise() { }

    public static double sample(byte[] secret, long worldSeed, String dimensionId, NamespacedId channelId,
                                SamplePoint point, int scale) {
        if (secret == null || secret.length != 32) {
            throw new IllegalArgumentException("Environmental secret must contain 32 bytes");
        }
        if (scale != 16 && scale != 32 && scale != 64 && scale != 128) {
            throw new IllegalArgumentException("Unsupported environmental noise scale");
        }
        long key = key(secret, worldSeed, dimensionId, channelId.toString());
        double ux = point.x() / scale;
        double uy = point.y() / scale;
        double uz = point.z() / scale;
        long x = (long) Math.floor(ux);
        long y = (long) Math.floor(uy);
        long z = (long) Math.floor(uz);
        double fx = fade(ux - x);
        double fy = fade(uy - y);
        double fz = fade(uz - z);
        double x00 = lerp(value(key, x, y, z), value(key, x + 1L, y, z), fx);
        double x10 = lerp(value(key, x, y + 1L, z), value(key, x + 1L, y + 1L, z), fx);
        double x01 = lerp(value(key, x, y, z + 1L), value(key, x + 1L, y, z + 1L), fx);
        double x11 = lerp(value(key, x, y + 1L, z + 1L), value(key, x + 1L, y + 1L, z + 1L), fx);
        return canonicalZero(lerp(lerp(x00, x10, fy), lerp(x01, x11, fy), fz));
    }

    private static long key(byte[] secret, long worldSeed, String dimensionId, String channelId) {
        byte[] dimension = dimensionId.getBytes(StandardCharsets.UTF_8);
        byte[] channel = channelId.getBytes(StandardCharsets.UTF_8);
        if (dimension.length > 0xFFFF || channel.length > 0xFFFF) {
            throw new IllegalArgumentException("Environmental id exceeds schema-one byte limit");
        }
        ByteBuffer message = ByteBuffer.allocate(PREFIX.length + 1 + Long.BYTES + 2 + dimension.length + 2 + channel.length);
        message.order(ByteOrder.LITTLE_ENDIAN).put(PREFIX).put((byte) 0).putLong(worldSeed);
        message.order(ByteOrder.BIG_ENDIAN).putShort((short) dimension.length).put(dimension)
                .putShort((short) channel.length).put(channel);
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            byte[] hash = mac.doFinal(message.array());
            return ByteBuffer.wrap(hash, 0, Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).getLong();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("HmacSHA256 is unavailable", exception);
        }
    }

    private static long mix64(long value) {
        long mixed = value + 0x9E3779B97F4A7C15L;
        mixed = (mixed ^ (mixed >>> 30)) * 0xBF58476D1CE4E5B9L;
        mixed = (mixed ^ (mixed >>> 27)) * 0x94D049BB133111EBL;
        return mixed ^ (mixed >>> 31);
    }

    private static double value(long key, long x, long y, long z) {
        long hash = mix64(key
                ^ Long.rotateLeft(mix64(x), 7)
                ^ Long.rotateLeft(mix64(y), 29)
                ^ Long.rotateLeft(mix64(z), 47));
        return 2.0D * ((hash >>> 11) * TWO_TO_NEGATIVE_53) - 1.0D;
    }

    private static double fade(double value) {
        return value * value * (3.0D - 2.0D * value);
    }

    private static double lerp(double first, double second, double fraction) {
        return first + (second - first) * fraction;
    }

    private static double canonicalZero(double value) {
        return value == 0.0D ? 0.0D : value;
    }
}
