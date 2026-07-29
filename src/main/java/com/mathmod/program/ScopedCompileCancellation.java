package com.mathmod.program;

/** Server-owned probe; callers may stop an attempt but cannot provide authority. */
@FunctionalInterface
public interface ScopedCompileCancellation {
    ScopedCompileCancellation NEVER = () -> false;

    boolean cancelled();
}
