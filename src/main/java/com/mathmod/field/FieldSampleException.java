package com.mathmod.field;

public final class FieldSampleException extends Exception {
    private final FieldPlanningIssue.Code code;

    public FieldSampleException(FieldPlanningIssue.Code code, String message) {
        super(message);
        this.code = code;
    }

    public FieldPlanningIssue.Code code() {
        return code;
    }
}
