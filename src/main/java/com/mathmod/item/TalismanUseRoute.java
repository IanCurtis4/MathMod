package com.mathmod.item;

public enum TalismanUseRoute {
    PROGRAMMER,
    RESOURCES,
    CAST;

    public static TalismanUseRoute resolve(boolean hasProgram, boolean secondaryUse) {
        if (!hasProgram) {
            return PROGRAMMER;
        }
        return secondaryUse ? RESOURCES : CAST;
    }
}
