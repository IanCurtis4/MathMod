package com.mathmod.registry;

import com.mathmod.MathMod;
import com.mathmod.program.GuidedWorkspaceState;
import com.mathmod.program.ResourceSelection;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramGraphStreamCodecs;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MathMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ProgramGraph>> PROGRAM =
            DATA_COMPONENTS.registerComponentType("program", builder -> builder
                    .persistent(ProgramGraph.CODEC)
                    .networkSynchronized(ProgramGraphStreamCodecs.NETWORK)
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CHALK_PRESET =
            DATA_COMPONENTS.registerComponentType("chalk_preset", builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> PROGRAM_NAME =
            DATA_COMPONENTS.registerComponentType("program_name", builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> MANUSCRIPT_ID =
            DATA_COMPONENTS.registerComponentType("manuscript_id", builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> MATHEMAGICIAN_TRADE =
            DATA_COMPONENTS.registerComponentType("mathemagician_trade", builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<java.util.List<ResourceSelection>>> PROGRAM_RESOURCES =
            DATA_COMPONENTS.registerComponentType("program_resources", builder -> builder
                    .persistent(ResourceSelection.CODEC.listOf())
                    .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(ResourceSelection.CODEC.listOf()))
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<java.util.List<String>>> PROGRAM_CUSTOM_ACTIONS =
            DATA_COMPONENTS.registerComponentType("program_custom_actions", builder -> builder
                    .persistent(Codec.STRING.listOf())
                    .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(Codec.STRING.listOf()))
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GuidedWorkspaceState>> PROGRAM_GUIDED_WORKSPACE =
            DATA_COMPONENTS.registerComponentType("program_guided_workspace", builder -> builder
                    .persistent(GuidedWorkspaceState.CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(GuidedWorkspaceState.CODEC))
                    .cacheEncoding());

    private ModDataComponents() {
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
