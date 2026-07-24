package com.mathmod.language;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundedFunctionCostTest {
    @Test
    void collectionCostScalesByDeclaredBoundAndBodyCost() {
        BoundedFunctionCost.Estimate estimate =
                BoundedFunctionCost.collectionApplication(3, 5, 8);

        assertEquals(43, estimate.evaluationSteps());
        assertTrue(estimate.withinLimit());
    }

    @Test
    void rejectsUnboundedCollectionsAndFlagsEvaluationOverflow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BoundedFunctionCost.collectionApplication(
                        1,
                        1,
                        ScopedLanguageLimits.MAX_COLLECTION_BOUND + 1
                )
        );
        BoundedFunctionCost.Estimate estimate = BoundedFunctionCost.collectionApplication(
                ScopedLanguageLimits.MAX_EVALUATION_STEPS,
                ScopedLanguageLimits.MAX_EVALUATION_STEPS,
                ScopedLanguageLimits.MAX_COLLECTION_BOUND
        );
        assertFalse(estimate.withinLimit());
        assertEquals(
                ScopedLanguageLimits.MAX_EVALUATION_STEPS + 1,
                estimate.evaluationSteps()
        );
    }
}
