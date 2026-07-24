package com.mathmod.program;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnchorProgramPresetTest {
    @Test
    void presetsCycleInGameplayOrder() {
        assertEquals(AnchorProgramPreset.SACRIFICE_PULSE, AnchorProgramPreset.ANCHOR_PULSE.next());
        assertEquals(AnchorProgramPreset.OFFERING_SPARK, AnchorProgramPreset.SACRIFICE_PULSE.next());
        assertEquals(AnchorProgramPreset.WARDING_PULSE, AnchorProgramPreset.OFFERING_SPARK.next());
        assertEquals(AnchorProgramPreset.KINETIC_TRANSDUCER, AnchorProgramPreset.WARDING_PULSE.next());
        assertEquals(AnchorProgramPreset.THRESHOLD_BEACON, AnchorProgramPreset.KINETIC_TRANSDUCER.next());
        assertEquals(AnchorProgramPreset.GRADIENT_LANTERN, AnchorProgramPreset.THRESHOLD_BEACON.next());
        assertEquals(AnchorProgramPreset.DIMENSIONAL_SURVEY, AnchorProgramPreset.GRADIENT_LANTERN.next());
        assertEquals(AnchorProgramPreset.ANCHOR_PULSE, AnchorProgramPreset.DIMENSIONAL_SURVEY.next());
    }

    @Test
    void knownGraphsCanInferPresetIdentity() {
        assertEquals(AnchorProgramPreset.ANCHOR_PULSE, AnchorProgramPreset.infer(ProgramPresets.anchorPulse()).orElseThrow());
        assertEquals(AnchorProgramPreset.SACRIFICE_PULSE, AnchorProgramPreset.infer(ProgramPresets.sacrificePulse()).orElseThrow());
        assertEquals(AnchorProgramPreset.OFFERING_SPARK, AnchorProgramPreset.infer(ProgramPresets.offeringSpark()).orElseThrow());
        assertEquals(AnchorProgramPreset.WARDING_PULSE, AnchorProgramPreset.infer(ProgramPresets.wardingPulse()).orElseThrow());
        assertEquals(AnchorProgramPreset.KINETIC_TRANSDUCER, AnchorProgramPreset.infer(ProgramPresets.kineticTransducer()).orElseThrow());
        assertEquals(AnchorProgramPreset.THRESHOLD_BEACON, AnchorProgramPreset.infer(ProgramPresets.thresholdBeacon()).orElseThrow());
        assertEquals(AnchorProgramPreset.GRADIENT_LANTERN, AnchorProgramPreset.infer(ProgramPresets.gradientLantern()).orElseThrow());
        assertEquals(AnchorProgramPreset.DIMENSIONAL_SURVEY, AnchorProgramPreset.infer(ProgramPresets.dimensionalSurvey()).orElseThrow());
    }

    @Test
    void legacyExactAmethystGraphsCanInferPresetIdentity() {
        assertEquals(
                AnchorProgramPreset.SACRIFICE_PULSE,
                AnchorProgramPreset.infer(ProgramPresets.sacrificePulse(ProgramPresets.LEGACY_AMETHYST_SACRIFICE_SELECTOR)).orElseThrow()
        );
        assertEquals(
                AnchorProgramPreset.OFFERING_SPARK,
                AnchorProgramPreset.infer(ProgramPresets.offeringSpark(ProgramPresets.LEGACY_AMETHYST_SACRIFICE_SELECTOR)).orElseThrow()
        );
        assertEquals(
                AnchorProgramPreset.WARDING_PULSE,
                AnchorProgramPreset.infer(ProgramPresets.wardingPulse(ProgramPresets.LEGACY_AMETHYST_SACRIFICE_SELECTOR)).orElseThrow()
        );
    }

    @Test
    void unknownPresetIdIsIgnored() {
        assertTrue(AnchorProgramPreset.fromId("mathmod:future_custom_program").isEmpty());
    }

    @Test
    void thresholdBeaconIsATypedExecutableAnchorProof() {
        var result = ProgramStorage.validateExecutable(ProgramPresets.thresholdBeacon());

        assertTrue(result.valid());
        assertTrue(result.budgetUsed() <= ProgramPresets.thresholdBeacon().budgetLimit());
        assertTrue(ProgramCosts.attributeRequirementsFor(ProgramPresets.thresholdBeacon()).containsKey("information"));
        assertTrue(ProgramCosts.attributeRequirementsFor(ProgramPresets.thresholdBeacon()).containsKey("precision"));
    }

    @Test
    void gradientLanternIsATypedBoundedAnchorProof() {
        var graph = ProgramPresets.gradientLantern();
        var result = ProgramStorage.validateExecutable(graph);

        assertTrue(result.valid(), result.issues().toString());
        assertTrue(result.budgetUsed() <= graph.budgetLimit());
        assertTrue(ProgramCosts.attributeRequirementsFor(graph).containsKey("information"));
        assertTrue(ProgramCosts.attributeRequirementsFor(graph).containsKey("spatial"));
        assertTrue(ProgramCosts.attributeRequirementsFor(graph).containsKey("precision"));
    }

    @Test
    void dimensionalSurveyIsATypedBoundedAnchorProof() {
        var graph = ProgramPresets.dimensionalSurvey();
        var result = ProgramStorage.validateExecutable(graph);

        assertTrue(result.valid(), result.issues().toString());
        assertTrue(result.budgetUsed() <= graph.budgetLimit());
        assertTrue(ProgramCosts.attributeRequirementsFor(graph).containsKey("information"));
        assertTrue(ProgramCosts.attributeRequirementsFor(graph).containsKey("spatial"));
        assertTrue(ProgramCosts.attributeRequirementsFor(graph).containsKey("precision"));
    }
}
