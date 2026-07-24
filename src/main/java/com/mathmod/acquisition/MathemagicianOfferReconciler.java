package com.mathmod.acquisition;

import com.mathmod.manuscript.ManuscriptDefinitions;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.registry.ModVillagers;
import com.mathmod.util.NamespacedId;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** Reconciles only marked MathMod offers after reloads, restocks, or chunk reloads. */
public final class MathemagicianOfferReconciler {
    private static final int RECONCILIATION_INTERVAL_TICKS = 100;
    private MathemagicianOfferReconciler() {
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % RECONCILIATION_INTERVAL_TICKS != 0) {
            return;
        }
        for (ServerLevel level : event.getServer().getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof Villager villager) {
                    reconcile(villager);
                }
            }
        }
    }

    static void reconcile(Villager villager) {
        if (villager.getVillagerData().getProfession() != ModVillagers.MATHEMAGICIAN.get()
                || villager.getTradingPlayer() != null
                || !ManuscriptDefinitions.acquisitionConfig().effectiveTradesEnabled()) {
            return;
        }

        ManuscriptAcquisitionSnapshot snapshot = ManuscriptDefinitions.acquisitionSnapshot();
        MerchantOffers offers = villager.getOffers();
        Set<NamespacedId> activeOfferIds = new HashSet<>();
        for (Iterator<MerchantOffer> iterator = offers.iterator(); iterator.hasNext(); ) {
            MerchantOffer offer = iterator.next();
            NamespacedId id = markedCandidateId(offer);
            if (id == null) {
                continue;
            }
            if (!snapshot.candidates().stream().anyMatch(candidate -> candidate.id().equals(id) && candidate.trade().isPresent())
                    || !activeOfferIds.add(id)) {
                iterator.remove();
                continue;
            }
        }

        MathemagicianOfferReconciliationPlan plan = MathemagicianOfferReconciliationPlan.create(
                snapshot,
                villager.getUUID(),
                ManuscriptDefinitions.acquisitionGeneration(),
                villager.getVillagerData().getLevel(),
                activeOfferIds
        );
        plan.additions().forEach(candidate -> offers.add(MathemagicianTrades.createManuscriptOffer(candidate)));
    }

    private static NamespacedId markedCandidateId(MerchantOffer offer) {
        return NamespacedId.tryParse(offer.getResult().getOrDefault(
                ModDataComponents.MATHEMAGICIAN_TRADE.get(), ""
        )).orElse(null);
    }
}
