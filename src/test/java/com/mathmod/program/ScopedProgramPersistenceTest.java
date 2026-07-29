package com.mathmod.program;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Read boundary is deliberately side-effect free; registered-stack coverage is in L0 GameTests. */
class ScopedProgramPersistenceTest {
    @Test void nullItemIsAbsentAndDoesNotRequireCompilation() {
        assertEquals(ScopedSourceRead.Status.ABSENT, ScopedProgramPersistence.read(null).status());
    }

    @Test void futureEnvelopePreservesAllSignedSchemaAndOpaqueBytes() {
        var envelope=new ScopedSourceEnvelope(-101,new byte[]{0,(byte)255,4});
        assertEquals(-101,envelope.schemaVersion());
        assertArrayEquals(new byte[]{0,(byte)255,4},envelope.payload());
    }
}
