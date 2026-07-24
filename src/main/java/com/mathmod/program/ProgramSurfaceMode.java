package com.mathmod.program;

/**
 * Declares what a programmer surface may do. Screens consume this policy;
 * they do not infer write access from the tab or widget that opened them.
 */
public enum ProgramSurfaceMode {
    THEOREM(false, true, Persistence.NONE),
    GUIDED(true, true, Persistence.GUIDED_WORKSPACE),
    INSCRIBED(false, false, Persistence.EXECUTION_GRAPH),
    INSPECTOR(false, false, Persistence.NONE);

    private final boolean workspaceMutable;
    private final boolean inscriptionAllowed;
    private final Persistence persistence;

    ProgramSurfaceMode(boolean workspaceMutable, boolean inscriptionAllowed, Persistence persistence) {
        this.workspaceMutable = workspaceMutable;
        this.inscriptionAllowed = inscriptionAllowed;
        this.persistence = persistence;
    }

    public boolean workspaceMutable() {
        return workspaceMutable;
    }

    public boolean inscriptionAllowed() {
        return inscriptionAllowed;
    }

    public boolean readOnly() {
        return !workspaceMutable;
    }

    public Persistence persistence() {
        return persistence;
    }

    public enum Persistence {
        NONE,
        GUIDED_WORKSPACE,
        EXECUTION_GRAPH
    }
}
