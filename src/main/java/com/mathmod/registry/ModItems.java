package com.mathmod.registry;

import com.mathmod.MathMod;
import com.mathmod.item.ChalkItem;
import com.mathmod.item.FieldManuscriptItem;
import com.mathmod.item.FieldLedgerItem;
import com.mathmod.item.ProgrammedTalismanItem;
import com.mathmod.item.RuneAnchorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MathMod.MOD_ID);

    public static final DeferredItem<ProgrammedTalismanItem> PROGRAMMED_TALISMAN = ITEMS.register(
            "programmed_talisman",
            () -> new ProgrammedTalismanItem(new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<ChalkItem> CHALK = ITEMS.register(
            "chalk",
            () -> new ChalkItem(new Item.Properties())
    );

    public static final DeferredItem<RuneAnchorItem> RUNE_ANCHOR = ITEMS.register(
            "rune_anchor",
            () -> new RuneAnchorItem(ModBlocks.RUNE_ANCHOR.get(), new Item.Properties())
    );

    public static final DeferredItem<BlockItem> DEMONSTRATION_TABLE = ITEMS.register(
            "demonstration_table",
            () -> new BlockItem(ModBlocks.DEMONSTRATION_TABLE.get(), new Item.Properties())
    );

    public static final DeferredItem<FieldManuscriptItem> FIELD_MANUSCRIPT = ITEMS.register(
            "field_manuscript",
            () -> new FieldManuscriptItem(new Item.Properties()
                    .stacksTo(16)
                    .component(
                            ModDataComponents.MANUSCRIPT_ID.get(),
                            "mathmod:rotated_horizon"
                    ))
    );

    public static final DeferredItem<FieldLedgerItem> FIELD_LEDGER = ITEMS.register(
            "field_ledger",
            () -> new FieldLedgerItem(new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<Item> VITAL_SALT = reagent("vital_salt");
    public static final DeferredItem<Item> MERCURIAL_DRAUGHT = reagent("mercurial_draught");
    public static final DeferredItem<Item> UMBRAL_POWDER = reagent("umbral_powder");
    public static final DeferredItem<Item> NOCTILUCENT_LENS = reagent("noctilucent_lens");
    public static final DeferredItem<Item> GRAVE_SALT = reagent("grave_salt");
    public static final DeferredItem<Item> BINDING_RESIN = reagent("binding_resin");
    public static final DeferredItem<Item> HOMUNCULAR_MATRIX = reagent("homuncular_matrix");
    public static final DeferredItem<Item> AXIOMATIC_INK = reagent("axiomatic_ink");
    public static final DeferredItem<Item> RECURSIVE_SEAL = reagent("recursive_seal");

    private ModItems() {
    }

    private static DeferredItem<Item> reagent(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
