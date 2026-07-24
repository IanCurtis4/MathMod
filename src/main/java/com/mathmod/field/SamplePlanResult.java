package com.mathmod.field;

import java.util.List;
import java.util.Optional;

public record SamplePlanResult(Optional<SamplePlan> plan, List<FieldPlanningIssue> issues) {
    public SamplePlanResult {
        plan = plan == null ? Optional.empty() : plan;
        issues = List.copyOf(issues);
    }

    public static SamplePlanResult success(SamplePlan plan) {
        return new SamplePlanResult(Optional.of(plan), List.of());
    }

    public static SamplePlanResult failure(FieldPlanningIssue.Code code, String message) {
        return new SamplePlanResult(Optional.empty(), List.of(new FieldPlanningIssue(code, message)));
    }

    public boolean valid() { return plan.isPresent() && issues.isEmpty(); }
}
