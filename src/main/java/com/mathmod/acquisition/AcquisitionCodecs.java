package com.mathmod.acquisition;

import com.google.gson.JsonElement;
import com.mathmod.util.NamespacedId;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public final class AcquisitionCodecs {
    private AcquisitionCodecs() {
    }

    public static DataResult<ManuscriptAcquisitionDefinition> decode(
            NamespacedId id,
            JsonElement json
    ) {
        return RawAcquisition.CODEC.parse(JsonOps.INSTANCE, json)
                .flatMap(raw -> validated(() -> raw.toDefinition(id)));
    }

    private record RawTrade(
            int level,
            int emeraldCost,
            boolean requiresBook,
            int maxUses,
            int villagerXp,
            int weight
    ) {
        private static final Codec<RawTrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("level").forGetter(RawTrade::level),
                Codec.INT.fieldOf("emerald_cost").forGetter(RawTrade::emeraldCost),
                Codec.BOOL.optionalFieldOf("requires_book", true).forGetter(RawTrade::requiresBook),
                Codec.INT.fieldOf("max_uses").forGetter(RawTrade::maxUses),
                Codec.INT.fieldOf("villager_xp").forGetter(RawTrade::villagerXp),
                Codec.INT.fieldOf("weight").forGetter(RawTrade::weight)
        ).apply(instance, RawTrade::new));

        private ManuscriptTradeDefinition toDefinition() {
            return new ManuscriptTradeDefinition(level, emeraldCost, requiresBook, maxUses, villagerXp, weight);
        }
    }

    private record RawAcquisition(
            int schemaVersion,
            NamespacedId manuscriptId,
            List<NamespacedId> lootPools,
            int lootWeight,
            Optional<RawTrade> trade
    ) {
        private static final Codec<RawAcquisition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("schema_version").forGetter(RawAcquisition::schemaVersion),
                NamespacedId.CODEC.fieldOf("manuscript").forGetter(RawAcquisition::manuscriptId),
                NamespacedId.CODEC.listOf().optionalFieldOf("loot_pools", List.of()).forGetter(RawAcquisition::lootPools),
                Codec.INT.optionalFieldOf("loot_weight", 0).forGetter(RawAcquisition::lootWeight),
                RawTrade.CODEC.optionalFieldOf("trade").forGetter(RawAcquisition::trade)
        ).apply(instance, RawAcquisition::new));

        private ManuscriptAcquisitionDefinition toDefinition(NamespacedId id) {
            return new ManuscriptAcquisitionDefinition(
                    schemaVersion,
                    id,
                    manuscriptId,
                    lootPools,
                    lootWeight,
                    trade.map(RawTrade::toDefinition)
            );
        }
    }

    private static <T> DataResult<T> validated(ThrowingSupplier<T> supplier) {
        try {
            return DataResult.success(supplier.get());
        } catch (IllegalArgumentException exception) {
            return DataResult.error(exception::getMessage);
        }
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get();
    }
}
