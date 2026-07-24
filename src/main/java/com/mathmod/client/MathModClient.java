package com.mathmod.client;

import com.mathmod.MathMod;
import com.mathmod.client.screen.RuneProgrammerScreen;
import com.mathmod.client.screen.TalismanResourcesScreen;
import com.mathmod.client.screen.FieldLedgerScreen;
import com.mathmod.client.screen.ManuscriptReaderScreen;
import com.mathmod.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = MathMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MathModClient {
    private MathModClient() {
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.RUNE_PROGRAMMER.get(), RuneProgrammerScreen::new);
        event.register(ModMenus.TALISMAN_RESOURCES.get(), TalismanResourcesScreen::new);
        event.register(ModMenus.FIELD_LEDGER.get(), FieldLedgerScreen::new);
        event.register(ModMenus.MANUSCRIPT_READER.get(), ManuscriptReaderScreen::new);
    }
}
