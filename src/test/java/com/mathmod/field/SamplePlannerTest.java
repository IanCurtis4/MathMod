package com.mathmod.field;

import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SamplePlannerTest {
    private static final NamespacedId SCALAR = NamespacedId.of("test", "scalar");
    private static final NamespacedId VECTOR = NamespacedId.of("test", "vector");

    @Test
    void gradientPlansSixCenteredSamplesAndKnownWorstCaseCost() {
        SamplePlanner planner = planner();
        SamplePoint center = new SamplePoint(10, 64, -3);

        SamplePlanResult result = planner.planField(
                CalculusOperator.GRADIENT, SCALAR, center, center, 1.0D, 3, SampleBoundary.allLoaded());

        assertTrue(result.valid(), () -> result.issues().toString());
        SamplePlan plan = result.plan().orElseThrow();
        assertEquals(6, plan.sampleCount());
        assertEquals(18, plan.evaluationSteps());
        assertEquals(31, plan.resourceCost());
        assertEquals(center.offset(1, 0, 0), plan.worldSamples().get(0).point());
        assertEquals(center.offset(-1, 0, 0), plan.worldSamples().get(1).point());
    }

    @Test
    void curlUsesSixProviderCallsRatherThanTwelve() {
        SamplePlan plan = planner().planField(
                CalculusOperator.CURL, VECTOR, new SamplePoint(0, 0, 0),
                new SamplePoint(0, 0, 0), 0.5D, 1, SampleBoundary.allLoaded())
                .plan().orElseThrow();
        assertEquals(6, plan.worldSamples().size());
    }

    @Test
    void unloadedOrOutOfRadiusSamplesRejectTheEntirePlan() {
        SamplePlanner planner = planner();
        SamplePlanResult unloaded = planner.planField(
                CalculusOperator.GRADIENT, SCALAR, new SamplePoint(0, 0, 0),
                new SamplePoint(0, 0, 0), 1, 1, ignored -> false);
        SamplePlanResult radius = planner.planField(
                CalculusOperator.GRADIENT, SCALAR, new SamplePoint(0, 0, 0),
                new SamplePoint(8, 0, 0), 1, 1, SampleBoundary.allLoaded());

        assertFalse(unloaded.valid());
        assertEquals(FieldPlanningIssue.Code.UNLOADED_SAMPLE, unloaded.issues().getFirst().code());
        assertFalse(radius.valid());
        assertEquals(FieldPlanningIssue.Code.RADIUS_EXCEEDED, radius.issues().getFirst().code());
    }

    @Test
    void compositeSimpsonRequiresEvenBoundedPanelsAndPlansNPlusOneSamples() {
        SamplePlanResult valid = planner().planIntegral(2, -2, 32, 4);
        SamplePlanResult odd = planner().planIntegral(0, 1, 3, 1);

        assertEquals(33, valid.plan().orElseThrow().functionSamples().size());
        assertEquals(132, valid.plan().orElseThrow().evaluationSteps());
        assertEquals(FieldPlanningIssue.Code.INVALID_PANEL_COUNT, odd.issues().getFirst().code());
    }

    private static SamplePlanner planner() {
        return new SamplePlanner(FieldProviderSnapshot.of(List.of(
                new FieldProviderDefinition(SCALAR, FieldValueKind.SCALAR, FieldQuantity.SIGNAL, 8, 2),
                new FieldProviderDefinition(VECTOR, FieldValueKind.VECTOR, FieldQuantity.BLOCK_PER_TICK, 8, 2)
        )));
    }
}
