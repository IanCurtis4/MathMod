package com.mathmod.field;

import com.mathmod.util.NamespacedId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Complete, immutable upper-bound work plan computed before resource deduction. */
public record SamplePlan(
        CalculusOperator operator,
        Optional<NamespacedId> providerId,
        FieldQuantity inputQuantity,
        String outputQuantity,
        List<SampleRequest> worldSamples,
        List<Double> functionSamples,
        int functionBodyCost,
        int evaluationSteps,
        int resourceCost
) {
    public SamplePlan {
        operator = Objects.requireNonNull(operator, "operator");
        providerId = providerId == null ? Optional.empty() : providerId;
        inputQuantity = Objects.requireNonNull(inputQuantity, "inputQuantity");
        if (outputQuantity == null || outputQuantity.isBlank()) throw new IllegalArgumentException("outputQuantity must not be blank");
        worldSamples = List.copyOf(worldSamples);
        functionSamples = List.copyOf(functionSamples);
        if (worldSamples.size() + functionSamples.size() > operator.maximumSamples()) {
            throw new IllegalArgumentException("Plan exceeds operator sample maximum");
        }
        if (functionBodyCost < 0 || evaluationSteps < 0 || resourceCost < 0) {
            throw new IllegalArgumentException("Plan costs must not be negative");
        }
        if (operator.worldField() != providerId.isPresent()) {
            throw new IllegalArgumentException("Provider presence does not match operator kind");
        }
    }

    public int sampleCount() {
        return worldSamples.size() + functionSamples.size();
    }
}
