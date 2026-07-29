package com.mathmod.program;

import org.junit.jupiter.api.Test;
import com.mojang.serialization.JsonOps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static org.junit.jupiter.api.Assertions.*;

class ScopedSourceEnvelopeTest {
    @Test
    void copiesPayloadAndUsesByteContentForEquality() {
        byte[] bytes = {1, 2, 3};
        ScopedSourceEnvelope envelope = new ScopedSourceEnvelope(-7, bytes);
        bytes[0] = 9;
        assertArrayEquals(new byte[]{1, 2, 3}, envelope.payload());
        byte[] exposed = envelope.payload();
        exposed[1] = 9;
        assertArrayEquals(new byte[]{1, 2, 3}, envelope.payload());
        assertEquals(envelope, new ScopedSourceEnvelope(-7, new byte[]{1, 2, 3}));
        assertEquals(envelope.hashCode(), new ScopedSourceEnvelope(-7, new byte[]{1, 2, 3}).hashCode());
    }

    @Test
    void acceptsMaximumAndRejectsOversizedPayloadBeforeInterpretation() {
        assertEquals(ScopedSourceEnvelope.MAX_PAYLOAD_BYTES,
                new ScopedSourceEnvelope(1, new byte[ScopedSourceEnvelope.MAX_PAYLOAD_BYTES]).payload().length);
        assertThrows(IllegalArgumentException.class,
                () -> new ScopedSourceEnvelope(1, new byte[ScopedSourceEnvelope.MAX_PAYLOAD_BYTES + 1]));
    }

    @Test
    void persistentCodecRetainsSignedSchemaAndPayloadBytes() {
        ScopedSourceEnvelope input = new ScopedSourceEnvelope(Integer.MIN_VALUE, new byte[]{-128, 0, 127});
        var encoded = ScopedSourceEnvelope.CODEC.encodeStart(JsonOps.INSTANCE, input).getOrThrow();
        assertEquals(input, ScopedSourceEnvelope.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow());
    }

    @Test
    void persistentCodecRejectsThe262145thPayloadElement() {
        assertTrue(ScopedSourceEnvelope.CODEC.parse(JsonOps.INSTANCE, encodedPayload(ScopedSourceEnvelope.MAX_PAYLOAD_BYTES)).result().isPresent());
        assertTrue(ScopedSourceEnvelope.CODEC.parse(JsonOps.INSTANCE, encodedPayload(ScopedSourceEnvelope.MAX_PAYLOAD_BYTES + 1)).error().isPresent());
    }

    private static JsonObject encodedPayload(int size) {
        JsonArray payload=new JsonArray(size);
        for(int index=0;index<size;index++) payload.add(0);
        JsonObject object=new JsonObject(); object.addProperty("schema_version",1); object.add("payload",payload); return object;
    }
}
