package com.mathmod;

import com.mathmod.acquisition.MathModServerConfig;
import com.mathmod.integration.patchouli.FieldManualOpenScheduler;
import com.mathmod.program.ConstructFlightManager;
import com.mathmod.knowledge.KnowledgeCommands;
import com.mathmod.knowledge.KnowledgeEvents;
import com.mathmod.knowledge.KnowledgeDefinitions;
import com.mathmod.knowledge.KnowledgeDefinitionReloadListener;
import com.mathmod.manuscript.ManuscriptDefinitionReloadListener;
import com.mathmod.physics.PhysicalProfileReloadListener;
import com.mathmod.environment.EnvironmentalFieldReloadListener;
import com.mathmod.network.ModNetworking;
import com.mathmod.registry.ModBlocks;
import com.mathmod.registry.ModBlockEntities;
import com.mathmod.registry.ModAttachments;
import com.mathmod.registry.ModCreativeTabs;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.registry.ModItems;
import com.mathmod.registry.ModLootModifiers;
import com.mathmod.registry.ModMenus;
import com.mathmod.registry.ModMobEffects;
import com.mathmod.registry.ModPoiTypes;
import com.mathmod.registry.ModVillagers;
import com.mathmod.registry.ModStructures;
import com.mathmod.registry.ModStructurePieces;
import com.mathmod.acquisition.MathemagicianTrades;
import com.mathmod.acquisition.MathemagicianOfferReconciler;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

@Mod(MathMod.MOD_ID)
public final class MathMod {
    public static final String MOD_ID = "mathmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MathMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, MathModServerConfig.SPEC);
        ModAttachments.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModMobEffects.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModPoiTypes.register(modEventBus);
        ModVillagers.register(modEventBus);
        ModStructures.register(modEventBus);
        ModStructurePieces.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModMenus.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(MathModServerConfig::onConfigLoading);
        modEventBus.addListener(MathModServerConfig::onConfigReloading);
        modEventBus.addListener(ModNetworking::register);
        NeoForge.EVENT_BUS.addListener(FieldManualOpenScheduler::onServerTick);
        NeoForge.EVENT_BUS.addListener(ConstructFlightManager::onServerTick);
        NeoForge.EVENT_BUS.addListener(KnowledgeCommands::register);
        NeoForge.EVENT_BUS.addListener(MathemagicianTrades::onVillagerTrades);
        NeoForge.EVENT_BUS.addListener(MathemagicianOfferReconciler::onServerTick);
        NeoForge.EVENT_BUS.addListener(KnowledgeEvents::onDatapackSync);
        NeoForge.EVENT_BUS.addListener(KnowledgeEvents::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(KnowledgeDefinitionReloadListener::register);
        NeoForge.EVENT_BUS.addListener(ManuscriptDefinitionReloadListener::register);
        NeoForge.EVENT_BUS.addListener(PhysicalProfileReloadListener::register);
        NeoForge.EVENT_BUS.addListener(EnvironmentalFieldReloadListener::register);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MathModRuneBootstrap.bootstrap();
            KnowledgeDefinitions.validateRuntime(MathModRuneBootstrap.registry());
            LOGGER.info("Registered {} MathMod rune definitions", MathModRuneBootstrap.registry().definitions().size());
        });
    }
}
