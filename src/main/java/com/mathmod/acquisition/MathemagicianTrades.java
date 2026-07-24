package com.mathmod.acquisition;

import com.mathmod.manuscript.ManuscriptDefinitions;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.registry.ModItems;
import com.mathmod.registry.ModVillagers;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.Optional;

public final class MathemagicianTrades {
    private MathemagicianTrades() {
    }

    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != ModVillagers.MATHEMAGICIAN.get()) {
            return;
        }
        event.getTrades().computeIfAbsent(1, ignored -> new java.util.ArrayList<>()).add(new PaperBuyback());
        for (int level = 2; level <= 5; level++) {
            event.getTrades().computeIfAbsent(level, ignored -> new java.util.ArrayList<>())
                    .add(new ManuscriptSale(level));
        }
    }

    private static final class PaperBuyback implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            if (!ManuscriptDefinitions.acquisitionConfig().mathematicianProfessionEnabled()) {
                return null;
            }
            return new MerchantOffer(
                    new ItemCost(Items.PAPER, 24),
                    new ItemStack(Items.EMERALD),
                    16,
                    2,
                    0.05F
            );
        }
    }

    private record ManuscriptSale(int level) implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            ManuscriptAcquisitionConfig config = ManuscriptDefinitions.acquisitionConfig();
            if (!config.effectiveTradesEnabled()) {
                return null;
            }
            Optional<ManuscriptAcquisitionSnapshot.Candidate> candidate = MathemagicianTradeCatalog.offersForLevel(
                    ManuscriptDefinitions.acquisitionSnapshot(),
                    trader.getUUID(),
                    ManuscriptDefinitions.acquisitionGeneration(),
                    level
            ).stream().findFirst();
            if (candidate.isEmpty()) {
                return null;
            }
            return createManuscriptOffer(candidate.orElseThrow());
        }
    }

    static MerchantOffer createManuscriptOffer(ManuscriptAcquisitionSnapshot.Candidate candidate) {
        ManuscriptTradeDefinition trade = candidate.trade().orElseThrow();
        ItemStack manuscript = new ItemStack(ModItems.FIELD_MANUSCRIPT.get());
        manuscript.set(ModDataComponents.MANUSCRIPT_ID.get(), candidate.manuscriptId().toString());
        manuscript.set(ModDataComponents.MATHEMAGICIAN_TRADE.get(), candidate.id().toString());
        Optional<ItemCost> secondCost = trade.requiresBook()
                ? Optional.of(new ItemCost(Items.BOOK))
                : Optional.empty();
        return new MerchantOffer(
                new ItemCost(Items.EMERALD, trade.emeraldCost()),
                secondCost,
                manuscript,
                trade.maxUses(),
                trade.villagerXp(),
                0.05F
        );
    }
}
