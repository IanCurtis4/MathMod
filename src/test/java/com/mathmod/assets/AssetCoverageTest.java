package com.mathmod.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mathmod.program.CustomInputSlot;
import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.ProgramAttributes;
import com.mathmod.program.ProgramPresets;
import com.mathmod.kubejs.KubeJsRuneRegistrationApi;
import com.mathmod.runes.BuiltInRunes;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RuneType;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetCoverageTest {
    private static final Path ASSET_ROOT = Path.of("src/main/resources/assets/mathmod");

    @Test
    void itemAndBlockTexturesAreCustomMathModAssets() {
        assertTexture16("textures/item/programmed_talisman.png");
        assertTexture16("textures/item/chalk.png");
        assertTexture16("textures/block/rune_anchor.png");

        assertModelDoesNotUseMinecraftTexture("models/item/programmed_talisman.json");
        assertModelDoesNotUseMinecraftTexture("models/item/chalk.json");
        assertModelDoesNotUseMinecraftTexture("models/block/rune_anchor.json");

        for (String reagent : Set.of(
                "vital_salt",
                "mercurial_draught",
                "umbral_powder",
                "noctilucent_lens",
                "grave_salt",
                "binding_resin",
                "homuncular_matrix",
                "axiomatic_ink",
                "recursive_seal"
        )) {
            assertTexture16("textures/item/" + reagent + ".png");
            assertModelDoesNotUseMinecraftTexture("models/item/" + reagent + ".json");
        }
    }

    @Test
    void everyBuiltInRuneHasAnIconTexture() {
        RuneRegistry registry = new RuneRegistry();
        BuiltInRunes.registerAll(registry);

        for (RuneDefinition definition : registry.definitions()) {
            String runeName = definition.id().substring(definition.id().indexOf(':') + 1);
            assertTexture16("textures/gui/runes/" + runeName + ".png");
        }
    }

    @Test
    void everyProgrammerCatalogEntryHasAnIconTexture() {
        for (CustomSpellAction action : CustomSpellAction.values()) {
            assertRuneIcon(action.iconRuneId());
        }
        ProgramPresets.talismanPresets().forEach(preset -> assertRuneIcon(preset.iconRuneId()));
    }

    @Test
    void everyProgrammerCatalogEntryHasBilingualPresentation() {
        JsonObject english = language("en_us");
        JsonObject portuguese = language("pt_br");

        for (CustomSpellAction action : CustomSpellAction.values()) {
            assertBilingualKey(english, portuguese, action.translationKey());
        }
        for (CustomSpellAction.Category category : CustomSpellAction.Category.values()) {
            assertBilingualKey(english, portuguese, category.translationKey());
        }
        for (var category : com.mathmod.program.TalismanPreset.Category.values()) {
            assertBilingualKey(english, portuguese, category.translationKey());
        }
        ProgramPresets.talismanPresets().forEach(preset -> {
            assertBilingualKey(english, portuguese, preset.nameKey());
            assertBilingualKey(english, portuguese, preset.hintKey());
        });
    }

    @Test
    void customMobEffectsHaveBilingualNamesAndVanillaSizedIcons() {
        JsonObject english = language("en_us");
        JsonObject portuguese = language("pt_br");

        for (String effect : Set.of("soul_bound", "vital_infusion", "parsimony", "conservation")) {
            assertBilingualKey(english, portuguese, "effect.mathmod." + effect);
            assertTextureSize("textures/mob_effect/" + effect + ".png", 18, 18);
        }
    }

    @Test
    void everyTheoremProvenanceHasBilingualPresentation() {
        JsonObject english = language("en_us");
        JsonObject portuguese = language("pt_br");

        assertTrue(english.has("screen.mathmod.rune_programmer.theorem_provenance"));
        assertTrue(portuguese.has("screen.mathmod.rune_programmer.theorem_provenance"));
        ProgramPresets.talismanPresets().forEach(preset -> {
            String key = preset.provenance().translationKey();
            assertTrue(english.has(key), "Missing English " + key);
            assertTrue(portuguese.has(key), "Missing PT-BR " + key);
        });
    }

    @Test
    void everyRuneFormInputHasBilingualPresentation() {
        JsonObject english = language("en_us");
        JsonObject portuguese = language("pt_br");

        assertTrue(english.has("screen.mathmod.rune_programmer.forms"));
        assertTrue(portuguese.has("screen.mathmod.rune_programmer.forms"));
        for (CustomInputSlot input : CustomInputSlot.values()) {
            assertTrue(english.has(input.translationKey()), "Missing English " + input.translationKey());
            assertTrue(portuguese.has(input.translationKey()), "Missing PT-BR " + input.translationKey());
        }
    }

    @Test
    void everyBuiltInMaterialTranslationIsBilingual() {
        JsonObject english = language("en_us");
        JsonObject portuguese = language("pt_br");
        KubeJsRuneRegistrationApi api = new KubeJsRuneRegistrationApi(new RuneRegistry());

        api.materials().stream()
                .map(material -> material.displayTranslationKey())
                .filter(key -> key != null)
                .forEach(key -> {
                    assertTrue(english.has(key), "Missing English " + key);
                    assertTrue(portuguese.has(key), "Missing PT-BR " + key);
                });
    }

    @Test
    void everyRuneTypeHasBilingualPresentation() {
        JsonObject english = language("en_us");
        JsonObject portuguese = language("pt_br");

        for (RuneType type : RuneType.values()) {
            String key = type.translationKey();
            assertTrue(english.has(key), "Missing English " + key);
            assertTrue(portuguese.has(key), "Missing PT-BR " + key);
            assertFalse(english.get(key).getAsString().isBlank(), "Blank English " + key);
            assertFalse(portuguese.get(key).getAsString().isBlank(), "Blank PT-BR " + key);
        }
    }

    @Test
    void everyBuiltInResourceAttributeHasBilingualPresentation() {
        JsonObject english = language("en_us");
        JsonObject portuguese = language("pt_br");
        RuneRegistry registry = new RuneRegistry();
        BuiltInRunes.registerAll(registry);
        KubeJsRuneRegistrationApi api = new KubeJsRuneRegistrationApi(registry);
        Set<String> attributes = new LinkedHashSet<>();

        registry.definitions().forEach(definition -> definition.attributeRequirements()
                .forEach(requirement -> attributes.add(requirement.attribute())));
        api.materials().forEach(material -> attributes.addAll(material.attributes().keySet()));

        for (String attribute : attributes) {
            String key = ProgramAttributes.translationKey(attribute);
            assertTrue(english.has(key), "Missing English " + key);
            assertTrue(portuguese.has(key), "Missing PT-BR " + key);
            assertFalse(english.get(key).getAsString().isBlank(), "Blank English " + key);
            assertFalse(portuguese.get(key).getAsString().isBlank(), "Blank PT-BR " + key);
        }
    }

    private static void assertRuneIcon(String iconId) {
        String path = iconId.substring(iconId.indexOf(':') + 1);
        assertTexture16("textures/gui/runes/" + path + ".png");
    }

    private static JsonObject language(String language) {
        Path path = ASSET_ROOT.resolve("lang/" + language + ".json");
        try {
            return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
        } catch (Exception exception) {
            throw new AssertionError("Could not read " + path, exception);
        }
    }

    private static void assertBilingualKey(JsonObject english, JsonObject portuguese, String key) {
        assertTrue(english.has(key), "Missing English " + key);
        assertTrue(portuguese.has(key), "Missing PT-BR " + key);
        assertFalse(english.get(key).getAsString().isBlank(), "Blank English " + key);
        assertFalse(portuguese.get(key).getAsString().isBlank(), "Blank PT-BR " + key);
    }

    private static void assertTexture16(String texturePath) {
        assertTextureSize(texturePath, 16, 16);
    }

    private static void assertTextureSize(String texturePath, int width, int height) {
        Path path = ASSET_ROOT.resolve(texturePath);
        assertTrue(Files.exists(path), "Missing texture " + texturePath);
        try {
            BufferedImage image = ImageIO.read(path.toFile());
            assertTrue(image != null, "Unreadable texture " + texturePath);
            assertTrue(image.getWidth() == width && image.getHeight() == height,
                    texturePath + " must be " + width + "x" + height
                            + " but was " + image.getWidth() + "x" + image.getHeight());
        } catch (IOException exception) {
            throw new AssertionError("Could not read texture " + texturePath, exception);
        }
    }

    private static void assertModelDoesNotUseMinecraftTexture(String modelPath) {
        String json;
        try {
            json = Files.readString(ASSET_ROOT.resolve(modelPath));
        } catch (Exception exception) {
            throw new AssertionError("Could not read " + modelPath, exception);
        }
        assertFalse(json.contains("\"layer0\": \"minecraft:"), modelPath + " still uses a vanilla item texture");
        assertFalse(json.contains("\"all\": \"minecraft:"), modelPath + " still uses a vanilla block texture");
    }
}
