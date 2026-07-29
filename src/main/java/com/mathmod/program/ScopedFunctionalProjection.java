package com.mathmod.program;

import java.util.List;

/** Disposable server-built presentation snapshot; it is never source or executable authority. */
public record ScopedFunctionalProjection(
        int schema,
        SourceState sourceState,
        AttemptState attemptState,
        GraphState graphState,
        GraphRelation graphRelation,
        List<Row> authoredRows,
        List<Row> checkedRows,
        List<Diagnostic> diagnostics,
        int chargedSteps
) {
    public static final int SCHEMA = 1;
    public ScopedFunctionalProjection {
        if (schema != SCHEMA || sourceState == null || attemptState == null || graphState == null || graphRelation == null
                || chargedSteps < 0 || chargedSteps > 4_096) throw new IllegalArgumentException("invalid projection");
        authoredRows=copy(authoredRows,256); checkedRows=copy(checkedRows,256); diagnostics=copy(diagnostics,256);
        if(authoredRows.size()+checkedRows.size()+diagnostics.size()>768) throw new IllegalArgumentException("projection list limit");
        validateState(sourceState, attemptState, graphState, graphRelation, authoredRows, checkedRows, diagnostics, chargedSteps);
    }
    public static ScopedFunctionalProjection unavailable() { return unavailable(GraphState.ABSENT); }
    public static ScopedFunctionalProjection unavailable(GraphState graphState) { return new ScopedFunctionalProjection(SCHEMA,SourceState.STALE,AttemptState.AUTHORITY_STALE,graphState,GraphRelation.NOT_COMPARABLE,List.of(),List.of(),List.of(new Diagnostic(Phase.STALE,Code.STALE,"$")),0); }
    public static ScopedFunctionalProjection graphOnly() { return new ScopedFunctionalProjection(SCHEMA,SourceState.ABSENT,AttemptState.NOT_RUN,GraphState.PRESENT,GraphRelation.NOT_COMPARABLE,List.of(),List.of(),List.of(),0); }
    private static <T> List<T> copy(List<T> values,int max) { if(values==null||values.size()>max)throw new IllegalArgumentException("projection list limit");return List.copyOf(values); }
    private static void validateState(SourceState source, AttemptState attempt, GraphState graph, GraphRelation relation,
                                      List<Row> authored, List<Row> checked, List<Diagnostic> diagnostics, int chargedSteps) {
        if (relation != GraphRelation.NOT_COMPARABLE && (attempt != AttemptState.SUCCESS || graph != GraphState.PRESENT)) throw new IllegalArgumentException("graph relation requires successful present graph");
        if (graph == GraphState.ABSENT && relation != GraphRelation.NOT_COMPARABLE) throw new IllegalArgumentException("absent graph cannot relate");
        switch (source) {
            case ABSENT -> requireNotRun(attempt, authored, checked, diagnostics, chargedSteps, Code.ABSENT, false);
            case CURRENT_UNREADABLE -> requireNotRun(attempt, authored, checked, diagnostics, chargedSteps, Code.UNREADABLE, true);
            case UNSUPPORTED_VERSION -> requireNotRun(attempt, authored, checked, diagnostics, chargedSteps, Code.UNSUPPORTED, true);
            case CONFLICT -> requireNotRun(attempt, authored, checked, diagnostics, chargedSteps, Code.CONFLICT, true);
            case STALE -> {
                if (attempt != AttemptState.AUTHORITY_STALE || !authored.isEmpty() || !checked.isEmpty() || relation != GraphRelation.NOT_COMPARABLE || chargedSteps != 0 || !only(diagnostics, Phase.STALE, Code.STALE)) throw new IllegalArgumentException("invalid stale projection");
            }
            case CURRENT_VALID -> validateCurrent(attempt, graph, relation, authored, checked, diagnostics);
        }
    }
    private static void requireNotRun(AttemptState attempt, List<Row> authored, List<Row> checked, List<Diagnostic> diagnostics,
                                      int chargedSteps, Code expected, boolean requiresDiagnostic) {
        if (attempt != AttemptState.NOT_RUN || !authored.isEmpty() || !checked.isEmpty() || chargedSteps != 0) throw new IllegalArgumentException("non-current source cannot expose compilation");
        if (requiresDiagnostic ? !only(diagnostics, Phase.PERSISTENCE, expected) : !diagnostics.isEmpty()) throw new IllegalArgumentException("invalid persistence diagnostic");
    }
    private static void validateCurrent(AttemptState attempt, GraphState graph, GraphRelation relation, List<Row> authored, List<Row> checked, List<Diagnostic> diagnostics) {
        if (attempt == AttemptState.NOT_RUN || authored.isEmpty()) throw new IllegalArgumentException("current source requires one compile attempt and authored rows");
        switch (attempt) {
            case SUCCESS -> {
                if (checked.isEmpty() || (graph == GraphState.PRESENT && relation == GraphRelation.NOT_COMPARABLE) ||
                        (graph == GraphState.ABSENT && relation != GraphRelation.NOT_COMPARABLE) ||
                        (relation == GraphRelation.MISMATCH ? !only(diagnostics, Phase.MISMATCH, Code.MISMATCH) : !diagnostics.isEmpty())) throw new IllegalArgumentException("invalid successful projection");
            }
            case LANGUAGE_REJECTED -> rejected(checked, relation, diagnostics, Phase.LANGUAGE, Code.LANGUAGE_REJECTED);
            case ADMISSION_REJECTED -> rejected(checked, relation, diagnostics, Phase.ADMISSION, Code.ADMISSION_REJECTED);
            case AUTHORITY_STALE -> rejected(checked, relation, diagnostics, Phase.STALE, Code.STALE);
            default -> throw new IllegalArgumentException("invalid current attempt");
        }
    }
    private static void rejected(List<Row> checked, GraphRelation relation, List<Diagnostic> diagnostics, Phase phase, Code code) {
        if (!checked.isEmpty() || relation != GraphRelation.NOT_COMPARABLE || !only(diagnostics, phase, code)) throw new IllegalArgumentException("invalid rejected projection");
    }
    private static boolean only(List<Diagnostic> diagnostics, Phase phase, Code code) { return !diagnostics.isEmpty() && diagnostics.stream().allMatch(value -> value.phase() == phase && value.code() == code); }
    public enum SourceState { ABSENT,CURRENT_VALID,CURRENT_UNREADABLE,UNSUPPORTED_VERSION,CONFLICT,STALE }
    public enum AttemptState { NOT_RUN,SUCCESS,LANGUAGE_REJECTED,ADMISSION_REJECTED,AUTHORITY_STALE }
    public enum GraphState { ABSENT,PRESENT }
    public enum GraphRelation { NOT_COMPARABLE,MATCH,MISMATCH }
    public enum RowKind { LITERAL,PARAMETER_REFERENCE,RUNE_CALL,RUNE_ARGUMENT,LAMBDA,APPLICATION,LET,RESULT }
    public enum Phase { PERSISTENCE,LANGUAGE,ADMISSION,MISMATCH,STALE }
    public enum Code { ABSENT,UNREADABLE,UNSUPPORTED,CONFLICT,LANGUAGE_REJECTED,ADMISSION_REJECTED,MISMATCH,STALE }
    public record Row(String structuralPath, RowKind kind, String primaryToken, String secondaryToken, int bindingIndex, int depth) {
        public Row { if(kind==null)throw new IllegalArgumentException("row kind"); structuralPath=bounded(structuralPath,512); primaryToken=bounded(primaryToken,256); secondaryToken=bounded(secondaryToken,256); if(bindingIndex < -1||bindingIndex>15||depth<0||depth>16)throw new IllegalArgumentException("invalid row"); }
    }
    public record Diagnostic(Phase phase, Code code, String structuralPath) { public Diagnostic { if(phase==null||code==null)throw new IllegalArgumentException("diagnostic"); structuralPath=bounded(structuralPath,512); } }
    private static String bounded(String value,int bytes) { if(value==null||value.getBytes(java.nio.charset.StandardCharsets.UTF_8).length>bytes)throw new IllegalArgumentException("projection string limit");return value; }
}
