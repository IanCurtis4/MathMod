package com.mathmod.manuscript;

import com.mathmod.util.NamespacedId;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class ManuscriptCodecs {
    public static final Codec<ManuscriptRarity> RARITY = Codec.STRING.comapFlatMap(
            value -> ManuscriptRarity.parse(value)
                    .<DataResult<ManuscriptRarity>>map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown manuscript rarity " + value)),
            rarity -> rarity.name().toLowerCase(Locale.ROOT)
    );

    public static final Codec<ManuscriptAliasDefinition> ALIAS = RawAlias.CODEC.comapFlatMap(
            RawAlias::toDefinition,
            definition -> new RawAlias(
                    definition.schemaVersion(),
                    definition.from(),
                    definition.to()
            )
    );

    private ManuscriptCodecs() {
    }

    public static DataResult<TraditionDefinition> decodeTradition(
            NamespacedId id,
            JsonElement json
    ) {
        return RawTradition.CODEC.parse(JsonOps.INSTANCE, json)
                .flatMap(raw -> raw.toDefinition(id));
    }

    public static DataResult<ManuscriptDefinition> decodeManuscript(
            NamespacedId id,
            JsonElement json
    ) {
        return RawManuscript.CODEC.parse(JsonOps.INSTANCE, json)
                .flatMap(raw -> raw.toDefinition(id));
    }

    private record RawTradition(
            int schemaVersion,
            String nameTranslationKey,
            String summaryTranslationKey,
            NamespacedId icon
    ) {
        private static final Codec<RawTradition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("schema_version").forGetter(RawTradition::schemaVersion),
                Codec.STRING.fieldOf("name_key").forGetter(RawTradition::nameTranslationKey),
                Codec.STRING.fieldOf("summary_key").forGetter(RawTradition::summaryTranslationKey),
                NamespacedId.CODEC.fieldOf("icon").forGetter(RawTradition::icon)
        ).apply(instance, RawTradition::new));

        private DataResult<TraditionDefinition> toDefinition(NamespacedId id) {
            return validated(() -> new TraditionDefinition(
                    schemaVersion,
                    id,
                    nameTranslationKey,
                    summaryTranslationKey,
                    icon
            ));
        }
    }

    private record RawManuscript(
            int schemaVersion,
            NamespacedId traditionId,
            String titleTranslationKey,
            List<String> pageTranslationKeys,
            NamespacedId icon,
            ManuscriptRarity rarity,
            Optional<NamespacedId> patchouliEntry,
            Optional<NamespacedId> theoremId
    ) {
        private static final Codec<RawManuscript> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("schema_version").forGetter(RawManuscript::schemaVersion),
                NamespacedId.CODEC.fieldOf("tradition").forGetter(RawManuscript::traditionId),
                Codec.STRING.fieldOf("title_key").forGetter(RawManuscript::titleTranslationKey),
                Codec.STRING.listOf().fieldOf("page_keys").forGetter(RawManuscript::pageTranslationKeys),
                NamespacedId.CODEC.fieldOf("icon").forGetter(RawManuscript::icon),
                RARITY.fieldOf("rarity").forGetter(RawManuscript::rarity),
                NamespacedId.CODEC.optionalFieldOf("patchouli_entry").forGetter(RawManuscript::patchouliEntry),
                NamespacedId.CODEC.optionalFieldOf("theorem").forGetter(RawManuscript::theoremId)
        ).apply(instance, RawManuscript::new));

        private DataResult<ManuscriptDefinition> toDefinition(NamespacedId id) {
            return validated(() -> new ManuscriptDefinition(
                    schemaVersion,
                    id,
                    traditionId,
                    titleTranslationKey,
                    pageTranslationKeys,
                    icon,
                    rarity,
                    patchouliEntry,
                    theoremId
            ));
        }
    }

    private record RawAlias(int schemaVersion, NamespacedId from, NamespacedId to) {
        private static final Codec<RawAlias> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("schema_version").forGetter(RawAlias::schemaVersion),
                NamespacedId.CODEC.fieldOf("from").forGetter(RawAlias::from),
                NamespacedId.CODEC.fieldOf("to").forGetter(RawAlias::to)
        ).apply(instance, RawAlias::new));

        private DataResult<ManuscriptAliasDefinition> toDefinition() {
            return validated(() -> new ManuscriptAliasDefinition(schemaVersion, from, to));
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
