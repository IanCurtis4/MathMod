package com.mathmod.kubejs;

import com.mathmod.program.ProgramPresets;
import com.mathmod.runes.BuiltInRunes;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RuneType;
import com.mathmod.manuscript.ManuscriptSourceLayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MathModKubeJSTest {
    @AfterEach
    void resetCompat() {
        KubeJsCompat.resetForTests();
    }

    @Test
    void queuedKubeJsStyleConfigurationAppliesWhenApiIsCreated() {
        KubeJsCompat.resetForTests();

        MathModKubeJS.material("allthemodium", "allthemodium:allthemodium_ingot", 24, 4);
        MathModKubeJS.setMaterialTranslationKey("allthemodium", "material.pack.allthemodium");
        MathModKubeJS.addMaterialAttribute("allthemodium", "spatial", 6);
        MathModKubeJS.setMaterialConsumed("allthemodium", false);
        MathModKubeJS.setMaterialTier("allthemodium", 3);
        MathModKubeJS.setRuneBudget("mathmod:push_self", 8);
        MathModKubeJS.setRuneTier("mathmod:push_self", 3);
        MathModKubeJS.addRuneMaterialRequirement("mathmod:push_self", "#forge:gems/diamond", 2);
        MathModKubeJS.addRuneAttributeRequirement("mathmod:push_self", "motion", 2);
        MathModKubeJS.disableRune("mathmod:raycast_block");
        MathModKubeJS.setAnchorSacrifice("#c:gems/source", 2, 3.5D);
        MathModKubeJS.setOfferingSparkDrop("minecraft:redstone", 3);
        MathModKubeJS.setWardingPulse(5.0D, 1.0D);
        MathModKubeJS.rune("test:anchor_ping")
                .input("position", "vec3")
                .output("unit")
                .budgetCost(3)
                .tier(2)
                .executorKey("debug_marker")
                .register();

        RuneRegistry registry = new RuneRegistry();
        BuiltInRunes.registerAll(registry);
        KubeJsRuneRegistrationApi api = KubeJsCompat.createApi(registry);

        assertTrue(api.materials().size() > 1);
        assertEquals(6, api.materials().stream()
                .filter(material -> material.id().equals("allthemodium"))
                .findFirst()
                .orElseThrow()
                .attributeAmount("spatial"));
        assertEquals("material.pack.allthemodium", api.materials().stream()
                .filter(material -> material.id().equals("allthemodium"))
                .findFirst()
                .orElseThrow()
                .displayTranslationKey());
        assertEquals(3, api.materials().stream()
                .filter(material -> material.id().equals("allthemodium"))
                .findFirst()
                .orElseThrow()
                .tier());
        assertEquals(8, registry.find("mathmod:push_self").orElseThrow().budgetCost());
        assertEquals(3, registry.find("mathmod:push_self").orElseThrow().tier().level());
        assertEquals(1, registry.find("mathmod:push_self").orElseThrow().materialRequirements().size());
        assertTrue(registry.find("mathmod:push_self").orElseThrow().attributeRequirements().stream()
                .anyMatch(requirement -> requirement.attribute().equals("motion") && requirement.amount() == 2));
        assertFalse(registry.find("mathmod:raycast_block").orElseThrow().enabled());
        assertTrue(registry.find("test:anchor_ping").isPresent());
        assertEquals(RuneType.UNIT, registry.find("test:anchor_ping").orElseThrow().outputType());
        assertEquals(2, registry.find("test:anchor_ping").orElseThrow().tier().level());

        ProgramGraph offeringSpark = ProgramPresets.offeringSpark();
        ProgramNode sacrifice = node(offeringSpark, "sacrifice");
        ProgramNode drop = node(offeringSpark, "drop");
        assertEquals("#c:gems/source", sacrifice.constants().get("item"));
        assertEquals("2", sacrifice.constants().get("count"));
        assertEquals("3.5", sacrifice.constants().get("radius"));
        assertEquals("minecraft:redstone", drop.constants().get("item"));
        assertEquals("3", drop.constants().get("count"));

        ProgramGraph wardingPulse = ProgramPresets.wardingPulse();
        ProgramNode pulse = node(wardingPulse, "pulse");
        assertEquals("5.0", pulse.constants().get("radius"));
        assertEquals("1.0", pulse.constants().get("strength"));
    }

    @Test
    void advancedMathematicsMaterialsExposeSpecializedAttributesAndRoles() {
        RuneRegistry registry = new RuneRegistry();
        BuiltInRunes.registerAll(registry);
        KubeJsRuneRegistrationApi api = new KubeJsRuneRegistrationApi(registry);

        RuneMaterialDefinition quartz = material(api, "quartz");
        assertFalse(quartz.consumed());
        assertEquals(2, quartz.attributeAmount("resonance"));
        assertEquals(1, quartz.attributeAmount("precision"));

        RuneMaterialDefinition copper = material(api, "copper");
        assertTrue(copper.consumed());
        assertEquals(2, copper.attributeAmount("continuity"));

        RuneMaterialDefinition lapis = material(api, "lapis");
        assertFalse(lapis.consumed());
        assertEquals(2, lapis.attributeAmount("symmetry"));

        RuneMaterialDefinition prismarine = material(api, "prismarine");
        assertFalse(prismarine.consumed());
        assertEquals(2, prismarine.attributeAmount("orientation"));
    }

    @Test
    void alchemicalReagentsExposeSpecializedAttributesAndConsumptionRoles() {
        KubeJsRuneRegistrationApi api = new KubeJsRuneRegistrationApi(new RuneRegistry());

        RuneMaterialDefinition vitalSalt = material(api, "vital_salt");
        assertTrue(vitalSalt.consumed());
        assertEquals(3, vitalSalt.attributeAmount("restoration"));
        assertEquals(1, vitalSalt.attributeAmount("vitality"));

        RuneMaterialDefinition noctilucentLens = material(api, "noctilucent_lens");
        assertFalse(noctilucentLens.consumed());
        assertEquals(3, noctilucentLens.attributeAmount("sight"));

        RuneMaterialDefinition bindingResin = material(api, "binding_resin");
        assertTrue(bindingResin.consumed());
        assertEquals(3, bindingResin.attributeAmount("binding"));
        assertEquals(2, bindingResin.attributeAmount("soul"));

        RuneMaterialDefinition homuncularMatrix = material(api, "homuncular_matrix");
        assertFalse(homuncularMatrix.consumed());
        assertEquals(3, homuncularMatrix.attributeAmount("infusion"));
        assertEquals(3, homuncularMatrix.attributeAmount("vitality"));
        assertEquals(2, homuncularMatrix.attributeAmount("stability"));
    }

    @Test
    void metamagicWitnessesExposeTierFourAttributesAndConsumptionRoles() {
        KubeJsRuneRegistrationApi api = new KubeJsRuneRegistrationApi(new RuneRegistry());

        RuneMaterialDefinition ink = material(api, "axiomatic_ink");
        assertEquals(4, ink.tier());
        assertTrue(ink.consumed());
        assertEquals(3, ink.attributeAmount("metamagic"));
        assertEquals(3, ink.attributeAmount("economy"));

        RuneMaterialDefinition seal = material(api, "recursive_seal");
        assertEquals(4, seal.tier());
        assertFalse(seal.consumed());
        assertEquals(3, seal.attributeAmount("metamagic"));
        assertEquals(3, seal.attributeAmount("conservation"));
    }

    @Test
    void manuscriptBuildersStageOneDeclarativeStartupGeneration() {
        MathModKubeJS.tradition("pack:ridge_surveyors")
                .schemaVersion(1)
                .nameKey("tradition.pack.ridge_surveyors.name")
                .summaryKey("tradition.pack.ridge_surveyors.summary")
                .icon("minecraft:spyglass")
                .register();
        MathModKubeJS.manuscript("pack:ridge_measurement")
                .schemaVersion(1)
                .tradition("pack:ridge_surveyors")
                .titleKey("manuscript.pack.ridge_measurement.title")
                .page("manuscript.pack.ridge_measurement.page.1")
                .icon("minecraft:paper")
                .rarity("uncommon")
                .patchouliEntry("pack:lore/ridge_measurement")
                .theorem("mathmod:right_angle")
                .register();
        MathModKubeJS.manuscriptAlias(1, "pack:old_ridge_measurement", "pack:ridge_measurement");

        KubeJsManuscriptDeclarationStore.Snapshot snapshot =
                KubeJsCompat.freezeManuscriptDeclarations();

        assertEquals(1, snapshot.traditions().size());
        assertEquals(1, snapshot.manuscripts().size());
        assertEquals(1, snapshot.aliases().size());
        assertEquals(ManuscriptSourceLayer.KUBEJS, snapshot.source().layer());
        assertThrows(IllegalStateException.class, () -> MathModKubeJS.tradition("pack:late")
                .schemaVersion(1)
                .nameKey("tradition.pack.late.name")
                .summaryKey("tradition.pack.late.summary")
                .icon("minecraft:paper")
                .register());
    }

    @Test
    void manuscriptBuildersRequireSchemaAndRejectDuplicateIds() {
        assertThrows(IllegalStateException.class, () -> MathModKubeJS.tradition("pack:missing_schema")
                .nameKey("tradition.pack.missing_schema.name")
                .summaryKey("tradition.pack.missing_schema.summary")
                .icon("minecraft:paper")
                .register());

        MathModKubeJS.tradition("pack:duplicate")
                .schemaVersion(1)
                .nameKey("tradition.pack.duplicate.name")
                .summaryKey("tradition.pack.duplicate.summary")
                .icon("minecraft:paper")
                .register();
        assertThrows(IllegalArgumentException.class, () -> MathModKubeJS.tradition("pack:duplicate")
                .schemaVersion(1)
                .nameKey("tradition.pack.duplicate_again.name")
                .summaryKey("tradition.pack.duplicate_again.summary")
                .icon("minecraft:paper")
                .register());
    }

    private static ProgramNode node(ProgramGraph graph, String id) {
        return graph.nodes().stream()
                .filter(node -> node.id().equals(id))
                .findFirst()
                .orElseThrow();
    }

    private static RuneMaterialDefinition material(KubeJsRuneRegistrationApi api, String id) {
        return api.materials().stream()
                .filter(material -> material.id().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
