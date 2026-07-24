package com.mathmod.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaletteSearchTest {
    @Test
    void emptyQueryMatchesEveryEntry() {
        assertTrue(PaletteSearch.matches("", "Vetor do olhar"));
        assertTrue(PaletteSearch.matches("   ", "Vetor do olhar"));
    }

    @Test
    void matchingIgnoresCaseAccentsAndSeparators() {
        assertTrue(PaletteSearch.matches("POSICAO", "Posicao do acerto"));
        assertTrue(PaletteSearch.matches("não jogadores", "Filtrar Nao-Jogadores"));
        assertTrue(PaletteSearch.matches("ray hit", "ray_hit"));
    }

    @Test
    void everyQueryTokenMayMatchASeparateCandidate() {
        assertTrue(PaletteSearch.matches("vetor geometria", "Vetor da base", "GEOMETRIA", "vec3"));
        assertFalse(PaletteSearch.matches("vetor efeito", "Vetor da base", "GEOMETRIA", "vec3"));
    }

    @Test
    void visibleCompactNotationIsSearchable() {
        assertTrue(PaletteSearch.matches("|v|", "Vector Length", "|v|"));
        assertTrue(PaletteSearch.matches("xs\u2229R", "Targets In Region", "xs\u2229R"));
        assertTrue(PaletteSearch.matches("exec plan", "Execute Plan", "exec(plan)"));
    }
}
