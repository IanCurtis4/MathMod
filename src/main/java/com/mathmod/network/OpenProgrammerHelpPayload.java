package com.mathmod.network;

import com.mathmod.MathMod;
import com.mathmod.integration.patchouli.FieldManualOpenScheduler;
import com.mathmod.screen.RuneProgrammerMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenProgrammerHelpPayload() implements CustomPacketPayload {
    public static final OpenProgrammerHelpPayload INSTANCE = new OpenProgrammerHelpPayload();
    public static final Type<OpenProgrammerHelpPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MathMod.MOD_ID, "open_programmer_help")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenProgrammerHelpPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenProgrammerHelpPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)
                || !(player.containerMenu instanceof RuneProgrammerMenu)) {
            return;
        }
        if (!ModList.get().isLoaded("patchouli")) {
            player.displayClientMessage(
                    Component.translatable("screen.mathmod.rune_programmer.help_failed"),
                    false
            );
            return;
        }
        FieldManualOpenScheduler.schedule(player, FieldManualOpenScheduler.Target.FIRST_SPELL);
    }
}
