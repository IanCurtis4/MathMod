package com.mathmod.registry;

import com.mathmod.MathMod;
import com.mathmod.knowledge.PlayerKnowledge;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MathMod.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerKnowledge>> PLAYER_KNOWLEDGE =
            ATTACHMENTS.register("player_knowledge", () -> AttachmentType.builder(PlayerKnowledge::empty)
                    .serialize(PlayerKnowledge.CODEC)
                    .copyOnDeath()
                    .sync(
                            (holder, receivingPlayer) -> holder == receivingPlayer,
                            ByteBufCodecs.fromCodecWithRegistries(PlayerKnowledge.CODEC)
                    )
                    .build());

    private ModAttachments() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }
}
