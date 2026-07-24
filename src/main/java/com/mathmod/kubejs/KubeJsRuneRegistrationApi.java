package com.mathmod.kubejs;

import com.mathmod.program.AnchorPresetConfig;
import com.mathmod.knowledge.DiscoveryDefinition;
import com.mathmod.knowledge.EpiphanyDefinition;
import com.mathmod.knowledge.KnowledgeAliases;
import com.mathmod.knowledge.KnowledgeDefinitions;
import com.mathmod.knowledge.KnowledgeKind;
import com.mathmod.util.NamespacedId;
import com.mathmod.runes.AttributeRequirement;
import com.mathmod.runes.MaterialRequirement;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RuneRegistry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class KubeJsRuneRegistrationApi {
    private final RuneRegistry runeRegistry;
    private final Map<String, RuneMaterialDefinition> materials = new LinkedHashMap<>();

    public KubeJsRuneRegistrationApi(RuneRegistry runeRegistry) {
        this.runeRegistry = runeRegistry;
        registerDefaultMaterials();
    }

    public RuneMaterialDefinition material(String id, String itemOrTag, int budgetBonus, int tier) {
        RuneMaterialDefinition material = new RuneMaterialDefinition(id, itemOrTag, budgetBonus, tier);
        materials.put(material.id(), material);
        return material;
    }

    public RuneMaterialDefinition addMaterialAttribute(String id, String attribute, int amount) {
        RuneMaterialDefinition material = materials.get(id);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material '" + id + "'");
        }
        RuneMaterialDefinition updated = material.withAttribute(attribute, amount);
        materials.put(updated.id(), updated);
        return updated;
    }

    public RuneMaterialDefinition setMaterialConsumed(String id, boolean consumed) {
        RuneMaterialDefinition material = materials.get(id);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material '" + id + "'");
        }
        RuneMaterialDefinition updated = material.withConsumed(consumed);
        materials.put(updated.id(), updated);
        return updated;
    }

    public RuneMaterialDefinition setMaterialTier(String id, int tier) {
        RuneMaterialDefinition material = materials.get(id);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material '" + id + "'");
        }
        RuneMaterialDefinition updated = material.withTier(tier);
        materials.put(updated.id(), updated);
        return updated;
    }

    public RuneMaterialDefinition setMaterialTranslationKey(String id, String translationKey) {
        RuneMaterialDefinition material = materials.get(id);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material '" + id + "'");
        }
        RuneMaterialDefinition updated = material.withDisplayTranslationKey(translationKey);
        materials.put(updated.id(), updated);
        return updated;
    }

    public RuneDefinition rune(String id, Consumer<RuneDefinition.Builder> config) {
        RuneDefinition.Builder builder = RuneDefinition.builder(id);
        config.accept(builder);
        return runeRegistry.registerOrReplace(builder.build());
    }

    public RuneDefinition setRuneBudget(String id, int budgetCost) {
        return runeRegistry.update(id, definition -> definition.withBudgetCost(budgetCost));
    }

    public RuneDefinition setRuneTier(String id, int tier) {
        return runeRegistry.update(id, definition -> definition.withTier(com.mathmod.runes.RuneTier.byLevel(tier)));
    }

    public RuneDefinition addRuneMaterialRequirement(String id, String itemOrTag, int quantity) {
        MaterialRequirement requirement = new MaterialRequirement(itemOrTag, quantity);
        return runeRegistry.update(id, definition -> definition.withMaterialRequirement(requirement));
    }

    public RuneDefinition clearRuneMaterialRequirements(String id) {
        return runeRegistry.update(id, RuneDefinition::withoutMaterialRequirements);
    }

    public RuneDefinition addRuneAttributeRequirement(String id, String attribute, int amount) {
        AttributeRequirement requirement = new AttributeRequirement(attribute, amount);
        return runeRegistry.update(id, definition -> definition.withAttributeRequirement(requirement));
    }

    public RuneDefinition clearRuneAttributeRequirements(String id) {
        return runeRegistry.update(id, RuneDefinition::withoutAttributeRequirements);
    }

    public void disableRune(String id) {
        runeRegistry.setEnabled(id, false);
    }

    public void enableRune(String id) {
        runeRegistry.setEnabled(id, true);
    }

    public void setAnchorSacrifice(String selector, int count, double radius) {
        AnchorPresetConfig.setAnchorSacrifice(selector, count, radius);
    }

    public void setOfferingSparkDrop(String itemId, int count) {
        AnchorPresetConfig.setOfferingSparkDrop(itemId, count);
    }

    public void setWardingPulse(double radius, double strength) {
        AnchorPresetConfig.setWardingPulse(radius, strength);
    }

    public void registerEpiphany(EpiphanyDefinition definition) {
        KnowledgeDefinitions.registerKube(definition);
    }

    public void registerDiscovery(DiscoveryDefinition definition) {
        KnowledgeDefinitions.registerKube(definition);
    }

    public void registerKnowledgeAlias(String kind, String alias, String target) {
        KnowledgeKind parsedKind = KnowledgeKind.parse(kind)
                .orElseThrow(() -> new IllegalArgumentException("Unknown knowledge kind '" + kind + "'"));
        KnowledgeAliases.registerKube(
                parsedKind,
                NamespacedId.parse(alias),
                NamespacedId.parse(target)
        );
    }

    public Collection<RuneMaterialDefinition> materials() {
        return List.copyOf(materials.values());
    }

    private void registerDefaultMaterials() {
        put(registerMaterial("feather", "minecraft:feather", 1, 1)
                .withAttribute("motion", 1));

        put(registerMaterial("redstone", "minecraft:redstone", 2, 1)
                .withAttribute("information", 1)
                .withAttribute("mechanical", 1));

        put(registerMaterial("iron", "minecraft:iron_ingot", 2, 1)
                .withAttribute("force", 1)
                .withAttribute("mechanical", 1));

        put(registerMaterial("amethyst", "minecraft:amethyst_shard", 2, 2)
                .withAttribute("arcane", 1)
                .withAttribute("force", 2)
                .withAttribute("precision", 1));

        put(registerMaterial("ender_pearl", "minecraft:ender_pearl", 4, 2)
                .withAttribute("spatial", 3));

        put(registerMaterial("diamond", "minecraft:diamond", 4, 2)
                .withAttribute("precision", 2)
                .withAttribute("stability", 1));

        put(registerMaterial("quartz", "minecraft:quartz", 2, 2)
                .withConsumed(false)
                .withAttribute("precision", 1)
                .withAttribute("resonance", 2));

        put(registerMaterial("copper", "minecraft:copper_ingot", 2, 2)
                .withAttribute("continuity", 2)
                .withAttribute("mechanical", 1));

        put(registerMaterial("lapis", "minecraft:lapis_lazuli", 2, 2)
                .withConsumed(false)
                .withAttribute("information", 1)
                .withAttribute("symmetry", 2));

        put(registerMaterial("prismarine", "minecraft:prismarine_crystals", 2, 2)
                .withConsumed(false)
                .withAttribute("orientation", 2)
                .withAttribute("spatial", 1));

        put(registerMaterial("vital_salt", "mathmod:vital_salt", 4, 2)
                .withAttribute("restoration", 3)
                .withAttribute("vitality", 1));

        put(registerMaterial("mercurial_draught", "mathmod:mercurial_draught", 4, 2)
                .withAttribute("haste", 3)
                .withAttribute("transmutation", 1));

        put(registerMaterial("umbral_powder", "mathmod:umbral_powder", 4, 2)
                .withAttribute("concealment", 3)
                .withAttribute("decay", 1));

        put(registerMaterial("noctilucent_lens", "mathmod:noctilucent_lens", 4, 2)
                .withConsumed(false)
                .withAttribute("sight", 3)
                .withAttribute("precision", 1));

        put(registerMaterial("grave_salt", "mathmod:grave_salt", 4, 2)
                .withAttribute("decay", 3)
                .withAttribute("soul", 1));

        put(registerMaterial("binding_resin", "mathmod:binding_resin", 6, 3)
                .withAttribute("binding", 3)
                .withAttribute("soul", 2)
                .withAttribute("stability", 1));

        put(registerMaterial("homuncular_matrix", "mathmod:homuncular_matrix", 8, 3)
                .withConsumed(false)
                .withAttribute("infusion", 3)
                .withAttribute("vitality", 3)
                .withAttribute("stability", 2));

        put(registerMaterial("axiomatic_ink", "mathmod:axiomatic_ink", 10, 4)
                .withConsumed(true)
                .withAttribute("metamagic", 3)
                .withAttribute("economy", 3));

        put(registerMaterial("recursive_seal", "mathmod:recursive_seal", 12, 4)
                .withConsumed(false)
                .withAttribute("metamagic", 3)
                .withAttribute("conservation", 3)
                .withAttribute("stability", 2));

        put(registerMaterial("tin", "#c:ingots/tin,#forge:ingots/tin", 2, 1)
                .withDisplayTranslationKey("material.mathmod.tin")
                .withAttribute("mechanical", 1)
                .withAttribute("precision", 1));

        put(registerMaterial("bronze", "#c:ingots/bronze,#forge:ingots/bronze", 4, 2)
                .withDisplayTranslationKey("material.mathmod.bronze")
                .withAttribute("mechanical", 2)
                .withAttribute("stability", 1));

        put(registerMaterial("steel", "#c:ingots/steel,#forge:ingots/steel", 6, 2)
                .withDisplayTranslationKey("material.mathmod.steel")
                .withAttribute("force", 2)
                .withAttribute("mechanical", 2)
                .withAttribute("stability", 2));

        put(registerMaterial("osmium", "#c:ingots/osmium,#forge:ingots/osmium", 6, 2)
                .withDisplayTranslationKey("material.mathmod.osmium")
                .withAttribute("spatial", 1)
                .withAttribute("precision", 1)
                .withAttribute("stability", 2));

        put(registerMaterial("arcane_essence", "#c:dusts/arcane_essence,#forge:dusts/arcane_essence,#c:gems/source,#forge:gems/source", 8, 3)
                .withDisplayTranslationKey("material.mathmod.arcane_essence")
                .withAttribute("arcane", 3)
                .withAttribute("spatial", 2)
                .withAttribute("information", 1));

        put(registerMaterial("allthemodium", "#c:ingots/allthemodium,#forge:ingots/allthemodium", 16, 4)
                .withDisplayTranslationKey("material.mathmod.allthemodium")
                .withAttribute("arcane", 4)
                .withAttribute("force", 4)
                .withAttribute("precision", 2)
                .withAttribute("spatial", 4)
                .withAttribute("stability", 4));
    }

    private RuneMaterialDefinition registerMaterial(String id, String itemOrTag, int budgetBonus, int tier) {
        RuneMaterialDefinition material = new RuneMaterialDefinition(id, itemOrTag, budgetBonus, tier);
        materials.put(material.id(), material);
        return material;
    }

    private void put(RuneMaterialDefinition material) {
        materials.put(material.id(), material);
    }
}
