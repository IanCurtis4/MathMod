package com.mathmod.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlchemicalRecipeAssetTest {
    private static final Path RECIPE_ROOT = Path.of("src/main/resources/data/mathmod/recipe");
    private static final Set<String> REAGENTS = Set.of(
            "vital_salt",
            "mercurial_draught",
            "umbral_powder",
            "noctilucent_lens",
            "grave_salt",
            "binding_resin",
            "homuncular_matrix",
            "axiomatic_ink",
            "recursive_seal"
    );

    @Test
    void everyAlchemicalReagentHasACraftingRecipeWithTheExpectedOutput() throws Exception {
        for (String reagent : REAGENTS) {
            Path recipePath = RECIPE_ROOT.resolve(reagent + ".json");
            assertTrue(Files.exists(recipePath), "Missing recipe for " + reagent);

            JsonObject recipe = JsonParser.parseString(Files.readString(recipePath)).getAsJsonObject();
            assertTrue(recipe.get("type").getAsString().startsWith("minecraft:crafting_"));
            assertEquals("mathmod:" + reagent,
                    recipe.getAsJsonObject("result").get("id").getAsString());
        }
    }
}
