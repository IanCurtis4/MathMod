package com.mathmod.field;

public record FieldPlanningIssue(Code code, String message) {
    public FieldPlanningIssue {
        if (code == null) throw new IllegalArgumentException("code must not be null");
        if (message == null || message.isBlank()) throw new IllegalArgumentException("message must not be blank");
        message = message.trim();
    }

    public enum Code {
        UNKNOWN_PROVIDER,
        WRONG_FIELD_KIND,
        INVALID_STEP,
        INVALID_PANEL_COUNT,
        SAMPLE_LIMIT,
        RADIUS_EXCEEDED,
        UNLOADED_SAMPLE,
        EVALUATION_LIMIT,
        NON_FINITE_VALUE,
        PROVIDER_FAILURE
    }
}
