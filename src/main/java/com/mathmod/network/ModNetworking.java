package com.mathmod.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ApplyCustomSpellInvocationPayload.TYPE, ApplyCustomSpellInvocationPayload.STREAM_CODEC, ApplyCustomSpellInvocationPayload::handle);
        registrar.playToServer(UpdateCustomSpellNamePayload.TYPE, UpdateCustomSpellNamePayload.STREAM_CODEC, UpdateCustomSpellNamePayload::handle);
        registrar.playToServer(OpenProgrammerHelpPayload.TYPE, OpenProgrammerHelpPayload.STREAM_CODEC, OpenProgrammerHelpPayload::handle);
        registrar.playToServer(OpenResourceHelpPayload.TYPE, OpenResourceHelpPayload.STREAM_CODEC, OpenResourceHelpPayload::handle);
        registrar.playToServer(OpenManuscriptManualPayload.TYPE, OpenManuscriptManualPayload.STREAM_CODEC, OpenManuscriptManualPayload::handle);
    }
}
