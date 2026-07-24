package com.mathmod.client.screen;

import com.mathmod.kubejs.RuneMaterialDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaterialCatalogOrderTest {
    @Test
    void portugueseCatalogUsesPresentedNamesInsteadOfStableIds() {
        List<RuneMaterialDefinition> canonical = List.of(
                material("allthemodium"),
                material("arcane_essence"),
                material("osmium"),
                material("steel"),
                material("tin")
        );
        Map<String, String> portuguese = Map.of(
                "allthemodium", "Allthemodium",
                "arcane_essence", "Essência Arcana",
                "osmium", "Ósmio",
                "steel", "Aço",
                "tin", "Estanho"
        );

        List<RuneMaterialDefinition> displayed = MaterialCatalogOrder.localized(
                canonical,
                material -> portuguese.get(material.id()),
                Locale.forLanguageTag("pt-BR")
        );

        assertEquals(
                List.of("Aço", "Allthemodium", "Essência Arcana", "Estanho", "Ósmio"),
                displayed.stream().map(material -> portuguese.get(material.id())).toList()
        );
    }

    @Test
    void displayedChoiceMapsBackToTheCanonicalServerIndex() {
        List<RuneMaterialDefinition> canonical = List.of(
                material("allthemodium"),
                material("arcane_essence"),
                material("steel")
        );

        assertEquals(2, MaterialCatalogOrder.canonicalIndex(canonical, material("steel")));
        assertEquals(-1, MaterialCatalogOrder.canonicalIndex(canonical, material("missing")));
    }

    private static RuneMaterialDefinition material(String id) {
        return new RuneMaterialDefinition(id, "#pack:" + id, 1, 1, true, Map.of());
    }
}
