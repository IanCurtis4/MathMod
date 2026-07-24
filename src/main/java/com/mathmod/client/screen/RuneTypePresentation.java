package com.mathmod.client.screen;

import com.mathmod.runes.RuneType;
import net.minecraft.network.chat.Component;

import java.util.Optional;

final class RuneTypePresentation {
    private RuneTypePresentation() {
    }

    static String translationKey(RuneType type) {
        return type.translationKey();
    }

    static Component displayName(RuneType type) {
        return Component.translatable(translationKey(type));
    }

    static Component displayName(String typeId) {
        Optional<RuneType> type = RuneType.byId(typeId).result();
        return type.<Component>map(RuneTypePresentation::displayName)
                .orElseGet(() -> Component.literal(typeId));
    }
}
