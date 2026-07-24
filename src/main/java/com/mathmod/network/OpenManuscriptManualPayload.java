package com.mathmod.network;

import com.mathmod.MathMod;
import com.mathmod.integration.patchouli.FieldManualOpenScheduler;
import com.mathmod.manuscript.ManuscriptDefinitions;
import com.mathmod.screen.ManuscriptReaderMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenManuscriptManualPayload() implements CustomPacketPayload {
    public static final OpenManuscriptManualPayload INSTANCE = new OpenManuscriptManualPayload();
    public static final Type<OpenManuscriptManualPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MathMod.MOD_ID, "open_manuscript_manual")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenManuscriptManualPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenManuscriptManualPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)
                || !(player.containerMenu instanceof ManuscriptReaderMenu menu)
                || menu.view().patchouliEntry().isEmpty()) {
            return;
        }
        if (!ModList.get().isLoaded("patchouli")) {
            player.displayClientMessage(Component.translatable("screen.mathmod.manuscript_reader.manual_unavailable"), false);
            return;
        }
        FieldManualOpenScheduler.scheduleDiscovery(player, menu.view().patchouliEntry().orElseThrow());
    }
}
