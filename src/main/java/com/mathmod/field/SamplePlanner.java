package com.mathmod.field;

import com.mathmod.language.ScopedLanguageLimits;
import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SamplePlanner {
    public static final double MIN_STEP = 0.25D;
    public static final double MAX_STEP = 4.0D;
    public static final int MAX_SIMPSON_PANELS = 32;

    private final FieldProviderSnapshot providers;

    public SamplePlanner(FieldProviderSnapshot providers) {
        this.providers = providers;
    }

    public SamplePlanResult planField(
            CalculusOperator operator,
            NamespacedId providerId,
            SamplePoint origin,
            SamplePoint point,
            double step,
            int functionBodyCost,
            SampleBoundary boundary
    ) {
        if (!operator.worldField()) {
            return failure(FieldPlanningIssue.Code.WRONG_FIELD_KIND, "Operator does not consume a world field");
        }
        Optional<FieldProviderDefinition> found = providers.find(providerId);
        if (found.isEmpty()) {
            return failure(FieldPlanningIssue.Code.UNKNOWN_PROVIDER, "Unknown field provider " + providerId);
        }
        FieldProviderDefinition provider = found.orElseThrow();
        if (provider.valueKind() != operator.inputKind()) {
            return failure(FieldPlanningIssue.Code.WRONG_FIELD_KIND,
                    "Operator " + operator + " requires " + operator.inputKind());
        }
        if (!validStep(step)) {
            return failure(FieldPlanningIssue.Code.INVALID_STEP,
                    "Step must be finite and between " + MIN_STEP + " and " + MAX_STEP);
        }
        List<SamplePoint> points = centeredAxisPoints(point, step);
        double maximumRadiusSquared = provider.maximumRadius() * provider.maximumRadius();
        for (SamplePoint sample : points) {
            if (sample.distanceSquared(origin) > maximumRadiusSquared) {
                return failure(FieldPlanningIssue.Code.RADIUS_EXCEEDED,
                        "Sample lies outside provider radius " + provider.maximumRadius());
            }
            if (!boundary.isLoaded(sample)) {
                return failure(FieldPlanningIssue.Code.UNLOADED_SAMPLE,
                        "A required sample is not loaded");
            }
        }
        int samples = points.size();
        long evaluations = (long) samples * Math.max(1, functionBodyCost);
        if (evaluations > ScopedLanguageLimits.MAX_EVALUATION_STEPS) {
            return failure(FieldPlanningIssue.Code.EVALUATION_LIMIT, "Field plan exceeds evaluation limit");
        }
        List<SampleRequest> requests = points.stream()
                .map(sample -> new SampleRequest(providerId, sample))
                .toList();
        int resourceCost = saturatedCost(1, samples, provider.sampleCost(), functionBodyCost);
        return SamplePlanResult.success(new SamplePlan(
                operator,
                Optional.of(providerId),
                provider.quantity(),
                derivedQuantity(provider.quantity()),
                requests,
                List.of(),
                functionBodyCost,
                (int) evaluations,
                resourceCost
        ));
    }

    public SamplePlanResult planDerivative(double point, double step, int functionBodyCost) {
        if (!Double.isFinite(point) || !validStep(step)) {
            return failure(FieldPlanningIssue.Code.INVALID_STEP, "Derivative point and step must be finite and bounded");
        }
        return functionPlan(CalculusOperator.DERIVATIVE, List.of(point - step, point + step), functionBodyCost);
    }

    public SamplePlanResult planIntegral(double lower, double upper, int panels, int functionBodyCost) {
        if (!Double.isFinite(lower) || !Double.isFinite(upper) || lower == upper) {
            return failure(FieldPlanningIssue.Code.NON_FINITE_VALUE, "Integration bounds must be finite and distinct");
        }
        if (panels < 2 || panels > MAX_SIMPSON_PANELS || panels % 2 != 0) {
            return failure(FieldPlanningIssue.Code.INVALID_PANEL_COUNT,
                    "Simpson panel count must be even and between 2 and " + MAX_SIMPSON_PANELS);
        }
        double spacing = (upper - lower) / panels;
        List<Double> points = new ArrayList<>(panels + 1);
        for (int index = 0; index <= panels; index++) {
            double sample = lower + spacing * index;
            if (!Double.isFinite(sample)) {
                return failure(FieldPlanningIssue.Code.NON_FINITE_VALUE, "Integration sample is not finite");
            }
            points.add(sample);
        }
        return functionPlan(CalculusOperator.INTEGRATE, points, functionBodyCost);
    }

    private SamplePlanResult functionPlan(CalculusOperator operator, List<Double> points, int functionBodyCost) {
        if (functionBodyCost < 0) {
            return failure(FieldPlanningIssue.Code.EVALUATION_LIMIT, "Function body cost must not be negative");
        }
        long evaluations = (long) points.size() * Math.max(1, functionBodyCost);
        if (evaluations > ScopedLanguageLimits.MAX_EVALUATION_STEPS) {
            return failure(FieldPlanningIssue.Code.EVALUATION_LIMIT, "Function plan exceeds evaluation limit");
        }
        return SamplePlanResult.success(new SamplePlan(
                operator, Optional.empty(), FieldQuantity.SCALAR, FieldQuantity.SCALAR.symbol(),
                List.of(), points, functionBodyCost, (int) evaluations,
                saturatedCost(1, points.size(), 0, functionBodyCost)
        ));
    }

    private static List<SamplePoint> centeredAxisPoints(SamplePoint point, double step) {
        return List.of(
                point.offset(step, 0, 0), point.offset(-step, 0, 0),
                point.offset(0, step, 0), point.offset(0, -step, 0),
                point.offset(0, 0, step), point.offset(0, 0, -step)
        );
    }

    private static boolean validStep(double step) {
        return Double.isFinite(step) && step >= MIN_STEP && step <= MAX_STEP;
    }

    private static String derivedQuantity(FieldQuantity quantity) {
        return quantity.symbol() + "/block";
    }

    private static int saturatedCost(int base, int samples, int providerCost, int bodyCost) {
        long cost = base + (long) samples * (providerCost + bodyCost);
        return (int) Math.min(Integer.MAX_VALUE, cost);
    }

    private static SamplePlanResult failure(FieldPlanningIssue.Code code, String message) {
        return SamplePlanResult.failure(code, message);
    }
}
