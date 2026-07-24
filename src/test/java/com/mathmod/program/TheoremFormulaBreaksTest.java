package com.mathmod.program;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TheoremFormulaBreaksTest {
    @Test
    void findsTheOuterEffectArgumentBoundary() {
        assertEquals(
                "push(self,".length() - 1,
                TheoremFormulaBreaks.outerArgumentSeparator(
                        "push(self,frame(self)*(.7,.08,0))"
                )
        );
    }

    @Test
    void ignoresNestedArgumentBoundaries() {
        assertEquals(
                -1,
                TheoremFormulaBreaks.outerArgumentSeparator(
                        "mark(mean(ores(5)))"
                )
        );
    }
}
