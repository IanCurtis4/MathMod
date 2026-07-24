package com.mathmod.network;

import com.mathmod.MathMod;
import com.mathmod.integration.patchouli.FieldManualOpenScheduler;
import com.mathmod.screen.TalismanResourcesMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenResourceHelpPayload() implements CustomPacketPayload {
    public static final OpenResourceHelpPayload INSTANCE = new OpenResourceHelpPayload();
    public static final Type<OpenResourceHelpPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MathMod.MOD_ID, "open_resource_help")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenResourceHelpPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenResourceHelpPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)
                || !(player.containerMenu instanceof TalismanResourcesMenu)) {
            return;
        }
        if (!ModList.get().isLoaded("patchouli")) {
            player.displayClientMessage(
                    Component.translatable("screen.mathmod.talisman_resources.help_failed"),
                    false
            );
            return;
        }
        FieldManualOpenScheduler.schedule(player, FieldManualOpenScheduler.Target.RESOURCE_COSTS);
    }
}
