package com.mathmod.acquisition;

import com.mathmod.MathMod;
import com.mathmod.manuscript.ManuscriptDefinitions;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class MathModServerConfig {
    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.BooleanValue MANUSCRIPT_LOOT_ENABLED;
    private static final ModConfigSpec.BooleanValue MATHEMAGICIAN_PROFESSION_ENABLED;
    private static final ModConfigSpec.BooleanValue MATHEMAGICIAN_TRADES_ENABLED;
    private static final ModConfigSpec.BooleanValue MATHEMAGICIAN_HOUSE_ENABLED;
    private static final ModConfigSpec.EnumValue<SurplusPolicy> SURPLUS_POLICY;
    private static final ModConfigSpec.IntValue VILLAGE_LOOT_CHANCE_NUMERATOR;
    private static final ModConfigSpec.IntValue VILLAGE_LOOT_CHANCE_DENOMINATOR;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("manuscript_acquisition");
        MANUSCRIPT_LOOT_ENABLED = builder.define("manuscriptLootEnabled", true);
        MATHEMAGICIAN_PROFESSION_ENABLED = builder.define("mathemagicianProfessionEnabled", true);
        MATHEMAGICIAN_TRADES_ENABLED = builder.define("mathemagicianTradesEnabled", true);
        MATHEMAGICIAN_HOUSE_ENABLED = builder.define("mathemagicianHouseEnabled", false);
        SURPLUS_POLICY = builder.defineEnum("surplusPolicy", SurplusPolicy.KEEP);
        VILLAGE_LOOT_CHANCE_NUMERATOR = builder.defineInRange("villageLootChanceNumerator", 1, 0, 1_000);
        VILLAGE_LOOT_CHANCE_DENOMINATOR = builder.defineInRange("villageLootChanceDenominator", 3, 1, 1_000);
        builder.pop();
        SPEC = builder.build();
    }

    private MathModServerConfig() {
    }

    public static ManuscriptAcquisitionConfig snapshot() {
        // GameTest/datapack bootstrap can request a snapshot before SERVER config loads.
        if (!SPEC.isLoaded()) {
            return ManuscriptAcquisitionConfig.defaults();
        }
        return new ManuscriptAcquisitionConfig(
                MANUSCRIPT_LOOT_ENABLED.get(),
                MATHEMAGICIAN_PROFESSION_ENABLED.get(),
                MATHEMAGICIAN_TRADES_ENABLED.get(),
                MATHEMAGICIAN_HOUSE_ENABLED.get(),
                SURPLUS_POLICY.get(),
                VILLAGE_LOOT_CHANCE_NUMERATOR.get(),
                VILLAGE_LOOT_CHANCE_DENOMINATOR.get()
        );
    }

    public static void onConfigLoading(ModConfigEvent.Loading event) {
        refreshPublishedConfig(event);
    }

    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        refreshPublishedConfig(event);
    }

    private static void refreshPublishedConfig(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getType() != ModConfig.Type.SERVER || !MathMod.MOD_ID.equals(config.getModId())) {
            return;
        }
        long generation = ManuscriptDefinitions.refreshAcquisitionConfig(snapshot());
        MathMod.LOGGER.info("Refreshed manuscript acquisition configuration at generation {}", generation);
    }
}
