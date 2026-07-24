package com.mathmod.network;

import com.mathmod.MathMod;
import com.mathmod.program.CustomSpellInvocation;
import com.mathmod.screen.RuneProgrammerMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ApplyCustomSpellInvocationPayload(String invocation) implements CustomPacketPayload {
    public static final Type<ApplyCustomSpellInvocationPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MathMod.MOD_ID, "apply_custom_spell_invocation")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ApplyCustomSpellInvocationPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    ApplyCustomSpellInvocationPayload::invocation,
                    ApplyCustomSpellInvocationPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ApplyCustomSpellInvocationPayload payload, IPayloadContext context) {
        if (!NetworkPayloadLimits.acceptsCustomInvocation(payload.invocation())) {
            return;
        }
        if (context.player().containerMenu instanceof RuneProgrammerMenu menu) {
            CustomSpellInvocation.fromPersistentId(payload.invocation())
                    .ifPresent(invocation -> menu.applyCustomInvocation(context.player(), invocation));
        }
    }
}
