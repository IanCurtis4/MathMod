package com.mathmod.acquisition;

import com.mathmod.MathMod;
import com.mathmod.manuscript.ManuscriptDefinitions;
import com.mathmod.registry.ModBlocks;
import com.mathmod.registry.ModVillagers;
import com.mathmod.registry.ModStructures;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Dedicated-server smoke test for the combined manuscript/acquisition publication boundary. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class P10GameTests {
    private P10GameTests() {
    }

    @GameTest(template = "empty")
    public static void acquisitionGenerationPublishesWithLore(GameTestHelper helper) {
        helper.assertTrue(ManuscriptDefinitions.acquisitionGeneration() > 0L,
                "P10 must publish an acquisition generation during server data reload");
        helper.assertTrue(ManuscriptDefinitions.acquisitionSnapshot().candidates().size() == 4,
                "P10 Luna must publish the four built-in acquisition declarations");
        helper.assertTrue(ManuscriptDefinitions.acquisitionSnapshot().lootPool(
                com.mathmod.util.NamespacedId.of("mathmod", "village_cartographer")
        ).size() == 4, "The cartographer pool must contain the four manuscript routes");
        helper.assertTrue(ManuscriptDefinitions.acquisitionSnapshot().lootPool(
                com.mathmod.util.NamespacedId.of("mathmod", "mathemagician_house")
        ).size() == 4, "The house pool must contain the four manuscript routes");
        helper.assertTrue(ManuscriptDefinitions.acquisitionConfig().villageLootChanceDenominator() >= 1,
                "P10 server configuration must publish a valid bounded loot denominator");
        helper.assertTrue(ModBlocks.DEMONSTRATION_TABLE.get() != null,
                "P10 must register the craftable Demonstration Table");
        helper.assertTrue(ModVillagers.MATHEMAGICIAN.get() != null,
                "P10 must register the Mathemagician profession without world generation");
        helper.assertTrue(ModStructures.MATHEMAGICIAN_HOUSE.get() != null,
                "P10 must register the optional Mathemagician house structure");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void disabledFeaturesPublishIndependentSafeConfiguration(GameTestHelper helper) {
        ManuscriptAcquisitionConfig before = ManuscriptDefinitions.acquisitionConfig();
        try {
            ManuscriptDefinitions.refreshAcquisitionConfig(new ManuscriptAcquisitionConfig(
                    false, false, true, false, before.surplusPolicy(), 0, 1
            ));
            ManuscriptAcquisitionConfig disabled = ManuscriptDefinitions.acquisitionConfig();
            helper.assertFalse(disabled.manuscriptLootEnabled(), "Loot must respect its own disabled feature flag");
            helper.assertFalse(disabled.mathematicianProfessionEnabled(), "Profession must respect its own disabled feature flag");
            helper.assertFalse(disabled.effectiveTradesEnabled(), "Trades must fail closed when the profession is disabled");
            helper.assertFalse(disabled.mathematicianHouseEnabled(), "House generation must stay independently disabled");
        } finally {
            ManuscriptDefinitions.refreshAcquisitionConfig(before);
        }
        helper.succeed();
    }
}
