package com.mathmod.integration.patchouli;

import com.mathmod.MathMod;
import com.mathmod.util.NamespacedId;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.Method;

public final class PatchouliFieldManual {
    public static final ResourceLocation BOOK_ID =
            ResourceLocation.fromNamespaceAndPath(FieldManualTarget.NAMESPACE, FieldManualTarget.BOOK_PATH);
    public static final ResourceLocation FIRST_SPELL_ENTRY =
            ResourceLocation.fromNamespaceAndPath(
                    FieldManualTarget.NAMESPACE,
                    FieldManualTarget.FIRST_SPELL_ENTRY_PATH
            );
    public static final int FIRST_SPELL_PAGE = FieldManualTarget.FIRST_SPELL_PAGE;
    public static final ResourceLocation RESOURCE_COSTS_ENTRY =
            ResourceLocation.fromNamespaceAndPath(
                    FieldManualTarget.NAMESPACE,
                    FieldManualTarget.RESOURCE_COSTS_ENTRY_PATH
            );
    public static final int RESOURCE_COSTS_PAGE = FieldManualTarget.RESOURCE_COSTS_PAGE;
    public static final ResourceLocation ROTATED_HORIZON_ENTRY =
            ResourceLocation.fromNamespaceAndPath(
                    FieldManualTarget.NAMESPACE,
                    FieldManualTarget.ROTATED_HORIZON_ENTRY_PATH
            );
    public static final int ROTATED_HORIZON_PAGE = FieldManualTarget.ROTATED_HORIZON_PAGE;

    private PatchouliFieldManual() {
    }

    public static boolean openFirstSpell(ServerPlayer player) {
        return openEntry(player, FIRST_SPELL_ENTRY, FIRST_SPELL_PAGE, "first-spell");
    }

    public static boolean openResourceCosts(ServerPlayer player) {
        return openEntry(player, RESOURCE_COSTS_ENTRY, RESOURCE_COSTS_PAGE, "resource-cost");
    }

    public static boolean openRotatedHorizon(ServerPlayer player) {
        return openEntry(player, ROTATED_HORIZON_ENTRY, ROTATED_HORIZON_PAGE, "rotated-horizon");
    }

    public static boolean openDiscovery(ServerPlayer player, NamespacedId patchouliEntry) {
        ResourceLocation entry = ResourceLocation.fromNamespaceAndPath(
                patchouliEntry.namespace(),
                patchouliEntry.path()
        );
        return openEntry(player, entry, 0, patchouliEntry.toString());
    }

    private static boolean openEntry(
            ServerPlayer player,
            ResourceLocation entry,
            int page,
            String description
    ) {
        try {
            Class<?> apiClass = Class.forName("vazkii.patchouli.api.PatchouliAPI");
            Class<?> apiInterface = Class.forName("vazkii.patchouli.api.PatchouliAPI$IPatchouliAPI");
            Object api = apiClass.getMethod("get").invoke(null);
            Method openBookEntry = apiInterface.getMethod(
                    "openBookEntry",
                    ServerPlayer.class,
                    ResourceLocation.class,
                    ResourceLocation.class,
                    int.class
            );
            openBookEntry.invoke(api, player, BOOK_ID, entry, page);
            return true;
        } catch (ReflectiveOperationException | LinkageError exception) {
            MathMod.LOGGER.error("Could not open the Patchouli {} entry", description, exception);
            return false;
        }
    }
}
