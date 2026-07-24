package com.mathmod.language;

/** Read policy for the future optional scoped-source data component. */
public final class FunctionalProgramMigrationPolicy {
    private FunctionalProgramMigrationPolicy() {
    }

    public static Decision legacyGraphOnly() {
        return new Decision(Status.LEGACY_GRAPH_ONLY, true, false, true, false);
    }

    public static Decision currentSource() {
        return new Decision(Status.CURRENT_SOURCE, true, true, false, false);
    }

    public static Decision unsupportedSource(int persistedVersion) {
        if (persistedVersion == ScopedProgramSource.CURRENT_VERSION) {
            throw new IllegalArgumentException("Current source version is not unsupported");
        }
        return new Decision(Status.UNSUPPORTED_SOURCE, true, false, false, false);
    }

    public static Decision unreadableCurrentSource() {
        return new Decision(Status.UNREADABLE_SOURCE, true, false, false, false);
    }

    public static Decision sourceWithoutCompiledGraph() {
        return new Decision(Status.SOURCE_WITHOUT_GRAPH, false, false, false, false);
    }

    public enum Status {
        LEGACY_GRAPH_ONLY,
        CURRENT_SOURCE,
        UNSUPPORTED_SOURCE,
        UNREADABLE_SOURCE,
        SOURCE_WITHOUT_GRAPH
    }

    public record Decision(
            Status status,
            boolean compiledGraphAvailable,
            boolean functionalEditingAvailable,
            boolean explicitConversionAvailable,
            boolean rewriteOnRead
    ) {
        public Decision {
            if (status == null) {
                throw new IllegalArgumentException("status must not be null");
            }
        }

        public boolean compiledGraphRemainsAuthoritative() {
            return compiledGraphAvailable;
        }
    }
}
