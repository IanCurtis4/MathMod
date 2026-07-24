package com.mathmod.network;

/**
 * Server-side limits for user-authored network text. These limits protect the
 * menu boundary; persisted spell data has its own validation path.
 */
public final class NetworkPayloadLimits {
    public static final int MAX_CUSTOM_SPELL_NAME_LENGTH = 128;
    public static final int MAX_CUSTOM_INVOCATION_LENGTH = 512;

    private NetworkPayloadLimits() {
    }

    public static boolean acceptsCustomSpellName(String name) {
        return name != null && name.length() <= MAX_CUSTOM_SPELL_NAME_LENGTH;
    }

    public static boolean acceptsCustomInvocation(String invocation) {
        return invocation != null && invocation.length() <= MAX_CUSTOM_INVOCATION_LENGTH;
    }
}
