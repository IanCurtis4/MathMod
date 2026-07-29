package com.mathmod.program;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Arrays;
import java.util.List;

/** Bounded opaque persistent source payload; schema interpretation is separate. */
public final class ScopedSourceEnvelope {
    public static final int MAX_PAYLOAD_BYTES = 262_144;
    public static final Codec<ScopedSourceEnvelope> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("schema_version").forGetter(ScopedSourceEnvelope::schemaVersion),
            // The bounded list codec rejects element 262,145 while decoding, before
            // an envelope or a complete payload value can be constructed.
            Codec.BYTE.listOf(0, MAX_PAYLOAD_BYTES).fieldOf("payload").forGetter(ScopedSourceEnvelope::payloadList)
    ).apply(instance, (schemaVersion, payload) -> new ScopedSourceEnvelope(schemaVersion, toBytes(payload))));

    private final int schemaVersion;
    private final byte[] payload;

    public ScopedSourceEnvelope(int schemaVersion, byte[] payload) {
        if (payload == null || payload.length > MAX_PAYLOAD_BYTES) {
            throw new IllegalArgumentException("payload exceeds " + MAX_PAYLOAD_BYTES + " bytes");
        }
        this.schemaVersion = schemaVersion;
        this.payload = Arrays.copyOf(payload, payload.length);
    }

    public int schemaVersion() { return schemaVersion; }

    public byte[] payload() { return Arrays.copyOf(payload, payload.length); }

    private List<Byte> payloadList() {
        java.util.ArrayList<Byte> bytes = new java.util.ArrayList<>(payload.length);
        for (byte value : payload) bytes.add(value);
        return List.copyOf(bytes);
    }

    private static byte[] toBytes(List<Byte> input) {
        if (input == null || input.size() > MAX_PAYLOAD_BYTES) {
            throw new IllegalArgumentException("payload exceeds " + MAX_PAYLOAD_BYTES + " bytes");
        }
        byte[] bytes = new byte[input.size()];
        for (int index = 0; index < bytes.length; index++) bytes[index] = input.get(index);
        return bytes;
    }

    @Override public boolean equals(Object other) {
        return other instanceof ScopedSourceEnvelope envelope
                && schemaVersion == envelope.schemaVersion && Arrays.equals(payload, envelope.payload);
    }

    @Override public int hashCode() { return 31 * Integer.hashCode(schemaVersion) + Arrays.hashCode(payload); }
}
