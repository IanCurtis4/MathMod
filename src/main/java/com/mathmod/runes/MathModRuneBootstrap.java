package com.mathmod.runes;

import com.mathmod.kubejs.KubeJsCompat;

public final class MathModRuneBootstrap {
    private static final RuneRegistry REGISTRY = new RuneRegistry();
    private static boolean bootstrapped;

    private MathModRuneBootstrap() {
    }

    public static synchronized void bootstrap() {
        if (bootstrapped) {
            return;
        }
        BuiltInRunes.registerAll(REGISTRY);
        KubeJsCompat.createApi(REGISTRY);
        bootstrapped = true;
    }

    public static RuneRegistry registry() {
        return REGISTRY;
    }
}
