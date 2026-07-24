package com.mathmod.kubejs;

import com.mathmod.program.AnchorPresetConfig;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.manuscript.ManuscriptAliasDefinition;
import com.mathmod.manuscript.ManuscriptDefinition;
import com.mathmod.manuscript.TraditionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class KubeJsCompat {
    private static KubeJsRuneRegistrationApi api;
    private static final List<Consumer<KubeJsRuneRegistrationApi>> PENDING_CONFIGURATIONS = new ArrayList<>();
    private static KubeJsManuscriptDeclarationStore manuscriptDeclarations =
            new KubeJsManuscriptDeclarationStore();

    private KubeJsCompat() {
    }

    public static synchronized KubeJsRuneRegistrationApi createApi(RuneRegistry runeRegistry) {
        if (api == null) {
            api = new KubeJsRuneRegistrationApi(runeRegistry);
            for (Consumer<KubeJsRuneRegistrationApi> configuration : PENDING_CONFIGURATIONS) {
                configuration.accept(api);
            }
            PENDING_CONFIGURATIONS.clear();
        }
        return api;
    }

    public static synchronized void configure(Consumer<KubeJsRuneRegistrationApi> configuration) {
        if (api != null) {
            configuration.accept(api);
        } else {
            PENDING_CONFIGURATIONS.add(configuration);
        }
    }

    public static synchronized KubeJsRuneRegistrationApi api() {
        if (api == null) {
            throw new IllegalStateException("KubeJS API has not been initialized yet");
        }
        return api;
    }

    public static synchronized void registerManuscriptTradition(TraditionDefinition definition) {
        manuscriptDeclarations.register(definition);
    }

    public static synchronized void registerManuscript(ManuscriptDefinition definition) {
        manuscriptDeclarations.register(definition);
    }

    public static synchronized void registerManuscriptAlias(ManuscriptAliasDefinition definition) {
        manuscriptDeclarations.register(definition);
    }

    public static synchronized KubeJsManuscriptDeclarationStore.Snapshot freezeManuscriptDeclarations() {
        return manuscriptDeclarations.freeze();
    }

    public static synchronized void resetForTests() {
        api = null;
        PENDING_CONFIGURATIONS.clear();
        manuscriptDeclarations = new KubeJsManuscriptDeclarationStore();
        AnchorPresetConfig.resetForTests();
    }
}
