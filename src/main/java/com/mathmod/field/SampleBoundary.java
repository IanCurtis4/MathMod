package com.mathmod.field;

/** Server adapter used while planning; implementations must not force-load chunks. */
public interface SampleBoundary {
    boolean isLoaded(SamplePoint point);

    static SampleBoundary allLoaded() {
        return ignored -> true;
    }
}
