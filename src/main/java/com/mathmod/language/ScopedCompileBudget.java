package com.mathmod.language;

/** One monotonic, per-attempt meter for logical scoped-compilation work. */
public final class ScopedCompileBudget {
    private final int limit;
    private int charged;

    public ScopedCompileBudget() {
        this(ScopedLanguageLimits.MAX_EVALUATION_STEPS);
    }

    public ScopedCompileBudget(int limit) {
        if (limit < 0 || limit > ScopedLanguageLimits.MAX_EVALUATION_STEPS) {
            throw new IllegalArgumentException("Compile budget must be within the scoped-language maximum");
        }
        this.limit = limit;
    }

    public void charge(Event event) {
        if (charged >= limit) {
            throw new LimitExceeded();
        }
        charged++;
    }

    public int chargedSteps() {
        return charged;
    }

    public int limit() {
        return limit;
    }

    public enum Event {
        STRUCTURAL_NODE,
        TYPE_NODE,
        LOWERING_EXPRESSION,
        CLOSURE_OR_BINDING,
        APPLICATION,
        LITERAL_RESOLUTION,
        GRAPH_NODE,
        GRAPH_EDGE
    }

    public static final class LimitExceeded extends RuntimeException {
        private LimitExceeded() {
            super(null, null, false, false);
        }
    }
}
