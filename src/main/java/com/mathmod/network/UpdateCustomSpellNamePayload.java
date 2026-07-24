package com.mathmod.network;

import com.mathmod.MathMod;
import com.mathmod.screen.RuneProgrammerMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateCustomSpellNamePayload(String name) implements CustomPacketPayload {
    public static final Type<UpdateCustomSpellNamePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MathMod.MOD_ID, "update_custom_spell_name")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateCustomSpellNamePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    UpdateCustomSpellNamePayload::name,
                    UpdateCustomSpellNamePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateCustomSpellNamePayload payload, IPayloadContext context) {
        if (!NetworkPayloadLimits.acceptsCustomSpellName(payload.name())) {
            return;
        }
        if (context.player().containerMenu instanceof RuneProgrammerMenu menu) {
            menu.setCustomSpellName(context.player(), payload.name());
        }
    }
}
