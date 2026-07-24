package com.mathmod.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TalismanUseRouteTest {
    @Test
    void blankTalismanAlwaysOpensProgrammer() {
        assertEquals(TalismanUseRoute.PROGRAMMER, TalismanUseRoute.resolve(false, false));
        assertEquals(TalismanUseRoute.PROGRAMMER, TalismanUseRoute.resolve(false, true));
    }

    @Test
    void normalUseCastsInscribedProgram() {
        assertEquals(TalismanUseRoute.CAST, TalismanUseRoute.resolve(true, false));
    }

    @Test
    void secondaryUseOpensInscribedResources() {
        assertEquals(TalismanUseRoute.RESOURCES, TalismanUseRoute.resolve(true, true));
    }
}
