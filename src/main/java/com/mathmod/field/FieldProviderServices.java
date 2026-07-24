package com.mathmod.field;

import com.mathmod.environment.EnvironmentalFieldSnapshot;

/** Central common-side holder. No client state participates in provider lookup. */
public final class FieldProviderServices {
    private static final FieldProviderPublicationStore STORE = new FieldProviderPublicationStore();
    private static boolean bootstrapped;

    private FieldProviderServices() { }

    public static synchronized void bootstrap() {
        if (!bootstrapped) {
            STORE.publish(BuiltInFieldProviders.publication());
            bootstrapped = true;
        }
    }

    public static FieldProviderPublication snapshot() {
        bootstrap();
        return STORE.snapshot();
    }

    public static synchronized void reloadEnvironmental(EnvironmentalFieldSnapshot snapshot) {
        STORE.publish(BuiltInFieldProviders.publication(snapshot));
        bootstrapped = true;
    }
}
