package com.mathmod.registry;

import com.mathmod.MathMod;
import com.mathmod.screen.RuneProgrammerMenu;
import com.mathmod.screen.TalismanResourcesMenu;
import com.mathmod.screen.FieldLedgerMenu;
import com.mathmod.screen.ManuscriptReaderMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MathMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<RuneProgrammerMenu>> RUNE_PROGRAMMER =
            MENUS.register("rune_programmer", () -> IMenuTypeExtension.create(RuneProgrammerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TalismanResourcesMenu>> TALISMAN_RESOURCES =
            MENUS.register("talisman_resources", () -> IMenuTypeExtension.create(TalismanResourcesMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<FieldLedgerMenu>> FIELD_LEDGER =
            MENUS.register("field_ledger", () -> IMenuTypeExtension.create(FieldLedgerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ManuscriptReaderMenu>> MANUSCRIPT_READER =
            MENUS.register("manuscript_reader", () -> IMenuTypeExtension.create(ManuscriptReaderMenu::new));

    private ModMenus() {
    }

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
