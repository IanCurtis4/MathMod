package com.mathmod.acquisition;

import com.mathmod.MathMod;
import com.mathmod.manuscript.ManuscriptDefinitions;
import com.mathmod.registry.ModBlocks;
import com.mathmod.registry.ModVillagers;
import com.mathmod.registry.ModStructures;
import com.mathmod.registry.ModDataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.server.level.ServerPlayer;
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

    @GameTest(template = "empty")
    public static void acquisitionFeatureFlagsRemainIndependent(GameTestHelper helper) {
        ManuscriptAcquisitionConfig before = ManuscriptDefinitions.acquisitionConfig();
        try {
            assertFeatureState(helper, before, false, true, true, true, false, true, true, true);
            assertFeatureState(helper, before, true, false, true, true, true, false, false, true);
            assertFeatureState(helper, before, true, true, false, true, true, true, false, true);
            assertFeatureState(helper, before, true, true, true, false, true, true, true, false);
        } finally {
            ManuscriptDefinitions.refreshAcquisitionConfig(before);
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void reconciliationRemovesOnlyRejectedMarkedOffers(GameTestHelper helper) {
        ManuscriptAcquisitionConfig before = ManuscriptDefinitions.acquisitionConfig();
        try {
            ManuscriptDefinitions.refreshAcquisitionConfig(new ManuscriptAcquisitionConfig(
                    true, true, true, before.mathematicianHouseEnabled(), before.surplusPolicy(), 1, 3
            ));
            Villager villager = new Villager(EntityType.VILLAGER, helper.getLevel());
            villager.setVillagerData(new VillagerData(VillagerType.PLAINS, ModVillagers.MATHEMAGICIAN.get(), 2));
            ManuscriptAcquisitionSnapshot.Candidate candidate = ManuscriptDefinitions.acquisitionSnapshot().candidates().stream()
                    .filter(entry -> entry.trade().isPresent())
                    .findFirst().orElseThrow();
            MerchantOffer retained = MathemagicianTrades.createManuscriptOffer(candidate);
            retained.increaseUses();
            retained.increaseUses();
            retained.setSpecialPriceDiff(-2);
            retained.updateDemand();
            MerchantOffer rejected = MathemagicianTrades.createManuscriptOffer(candidate);
            rejected.getResult().set(ModDataComponents.MATHEMAGICIAN_TRADE.get(), "mathmod:retired_offer");
            MerchantOffer unmarked = new MerchantOffer(new ItemCost(Items.STICK), new ItemStack(Items.APPLE), 4, 0, 0.0F);
            int retainedUses = retained.getUses();
            int retainedMaxUses = retained.getMaxUses();
            ItemStack retainedCost = retained.getCostA().copy();
            int retainedSpecialPrice = retained.getSpecialPriceDiff();
            int retainedDemand = retained.getDemand();

            villager.getOffers().add(retained);
            villager.getOffers().add(rejected);
            villager.getOffers().add(unmarked);
            MathemagicianOfferReconciler.reconcile(villager);

            helper.assertTrue(villager.getOffers().contains(retained),
                    "a valid marked offer must retain its exact existing instance");
            helper.assertTrue(retained.getUses() == retainedUses && retained.getMaxUses() == retainedMaxUses
                            && ItemStack.matches(retained.getCostA(), retainedCost)
                            && retained.getSpecialPriceDiff() == retainedSpecialPrice
                            && retained.getDemand() == retainedDemand,
                    "reconciliation must preserve the retained offer's uses, base cost, special price, and demand");
            helper.assertFalse(villager.getOffers().contains(rejected),
                    "only the rejected marked offer may be removed");
            helper.assertTrue(villager.getOffers().contains(unmarked),
                    "an unmarked offer must stay untouched during reconciliation");
        } finally {
            ManuscriptDefinitions.refreshAcquisitionConfig(before);
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void openMerchantReconciliationLeavesEveryOfferUntouched(GameTestHelper helper) {
        ManuscriptAcquisitionConfig before = ManuscriptDefinitions.acquisitionConfig();
        try {
            ManuscriptDefinitions.refreshAcquisitionConfig(new ManuscriptAcquisitionConfig(
                    true, true, true, before.mathematicianHouseEnabled(), before.surplusPolicy(), 1, 3
            ));
            Villager villager = mathematician(helper, 2);
            ServerPlayer tradingPlayer = helper.makeMockServerPlayerInLevel();
            ManuscriptAcquisitionSnapshot.Candidate candidate = ManuscriptDefinitions.acquisitionSnapshot().candidates().stream()
                    .filter(entry -> entry.trade().isPresent()).findFirst().orElseThrow();
            MerchantOffer valid = MathemagicianTrades.createManuscriptOffer(candidate);
            valid.increaseUses();
            valid.setSpecialPriceDiff(-3);
            valid.updateDemand();
            MerchantOffer rejected = MathemagicianTrades.createManuscriptOffer(candidate);
            rejected.getResult().set(ModDataComponents.MATHEMAGICIAN_TRADE.get(), "mathmod:retired_offer");
            MerchantOffer unmarked = new MerchantOffer(new ItemCost(Items.STICK), new ItemStack(Items.APPLE), 4, 0, 0.0F);
            villager.getOffers().add(valid);
            villager.getOffers().add(rejected);
            villager.getOffers().add(unmarked);
            java.util.List<MerchantOffer> beforeOffers = java.util.List.copyOf(villager.getOffers());
            int validUses = valid.getUses();
            int validSpecialPrice = valid.getSpecialPriceDiff();
            int validDemand = valid.getDemand();

            villager.setTradingPlayer(tradingPlayer);
            MathemagicianOfferReconciler.reconcile(villager);

            helper.assertTrue(villager.getOffers().size() == beforeOffers.size(),
                    "an open merchant menu must not add or remove offers");
            for (int index = 0; index < beforeOffers.size(); index++) {
                helper.assertTrue(villager.getOffers().get(index) == beforeOffers.get(index),
                        "an open merchant menu must preserve exact offer order and instances");
            }
            helper.assertTrue(valid.getUses() == validUses && valid.getSpecialPriceDiff() == validSpecialPrice
                            && valid.getDemand() == validDemand,
                    "an open merchant menu must preserve valid marked mutable offer state");
        } finally {
            ManuscriptDefinitions.refreshAcquisitionConfig(before);
        }
        helper.succeed();
    }

    private static void assertFeatureState(
            GameTestHelper helper,
            ManuscriptAcquisitionConfig before,
            boolean loot,
            boolean profession,
            boolean trades,
            boolean house,
            boolean expectedLoot,
            boolean expectedProfession,
            boolean expectedTrades,
            boolean expectedHouse
    ) {
        ManuscriptDefinitions.refreshAcquisitionConfig(new ManuscriptAcquisitionConfig(
                loot, profession, trades, house, before.surplusPolicy(), 1, 3
        ));
        ManuscriptAcquisitionConfig actual = ManuscriptDefinitions.acquisitionConfig();
        helper.assertTrue(actual.manuscriptLootEnabled() == expectedLoot, "loot flag must be independent");
        helper.assertTrue(actual.mathematicianProfessionEnabled() == expectedProfession,
                "profession flag must be independent");
        helper.assertTrue(actual.effectiveTradesEnabled() == expectedTrades,
                "effective trades must require both profession and trade flags");
        helper.assertTrue(actual.mathematicianHouseEnabled() == expectedHouse, "house flag must be independent");
    }

    private static Villager mathematician(GameTestHelper helper, int level) {
        Villager villager = new Villager(EntityType.VILLAGER, helper.getLevel());
        villager.setVillagerData(new VillagerData(VillagerType.PLAINS, ModVillagers.MATHEMAGICIAN.get(), level));
        return villager;
    }
}
