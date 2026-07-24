package com.mathmod.registry;

import com.mathmod.MathMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MathMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mathmod"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.PROGRAMMED_TALISMAN.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.PROGRAMMED_TALISMAN.get());
                        output.accept(ModItems.CHALK.get());
                        output.accept(ModItems.RUNE_ANCHOR.get());
                        output.accept(ModItems.DEMONSTRATION_TABLE.get());
                        for (var discovery : com.mathmod.knowledge.KnowledgeDefinitions.discoveries()) {
                            ItemStack manuscript = new ItemStack(ModItems.FIELD_MANUSCRIPT.get());
                            manuscript.set(
                                    ModDataComponents.MANUSCRIPT_ID.get(),
                                    discovery.manuscriptId().toString()
                            );
                            output.accept(manuscript);
                        }
                        output.accept(ModItems.FIELD_LEDGER.get());
                        output.accept(ModItems.VITAL_SALT.get());
                        output.accept(ModItems.MERCURIAL_DRAUGHT.get());
                        output.accept(ModItems.UMBRAL_POWDER.get());
                        output.accept(ModItems.NOCTILUCENT_LENS.get());
                        output.accept(ModItems.GRAVE_SALT.get());
                        output.accept(ModItems.BINDING_RESIN.get());
                        output.accept(ModItems.HOMUNCULAR_MATRIX.get());
                        output.accept(ModItems.AXIOMATIC_INK.get());
                        output.accept(ModItems.RECURSIVE_SEAL.get());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
