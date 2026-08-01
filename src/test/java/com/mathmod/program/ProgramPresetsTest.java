package com.mathmod.program;

import com.mathmod.runes.RuneType;
import com.mathmod.runes.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramPresetsTest {
    @Test
    void factoredLeapIsTheOnlyCatalogAdditionAndTheThirtyThreePriorGraphsStayFrozen() {
        var frozen = ProgramPresets.talismanPresets().stream()
                .filter(preset -> !preset.id().equals(FactoredLeapTheorem.ID))
                .map(preset -> preset.id() + "=" + graphFingerprint(preset.graph()))
                .sorted().toList();
        assertEquals(33, frozen.size());
        assertEquals("9845d0176be2f24ab8a9605842d0a5accdef811acb4a61fd240e4ba93cdc1b99", digest(String.join("\n", frozen)));
    }

    @Test
    void catalogContainsThirtyFourUniqueExecutableTheorems() {
        assertEquals(34, ProgramPresets.talismanPresets().size());
        assertEquals(34, ProgramPresets.talismanPresets().stream().map(TalismanPreset::id).distinct().count());
        assertEquals(34, ProgramPresets.talismanPresets().stream().map(TalismanPreset::buttonId).distinct().count());
        assertEquals(34, ProgramPresets.talismanPresets().stream().map(TalismanPreset::formula).distinct().count());
        assertEquals(34, ProgramPresets.talismanPresets().stream().map(TalismanPreset::catalogFormula).distinct().count());

        for (TalismanPreset preset : ProgramPresets.talismanPresets()) {
            assertTrue(preset.id().startsWith("mathmod:"));
            assertEquals(preset, ProgramPresets.presetForId(preset.id()).orElseThrow());
            assertEquals(
                    preset,
                    ProgramPresets.presetForId(preset.id().substring("mathmod:".length())).orElseThrow()
            );
            ValidationResult result = ProgramStorage.validateExecutable(preset.graph());
            assertTrue(result.valid(), preset.id() + " should be executable: " + result.issues());
            assertEquals(RuneType.UNIT, result.outputType(), preset.id() + " must produce a world effect");
            assertFalse(preset.formula().isBlank());
            if (preset.id().equals(FactoredLeapTheorem.ID)) {
                assertEquals("let halve(v)=v*0.5 in push(self,halve(look)+halve((0,1,0)))", preset.formula());
                assertEquals("push(halve(look)+halve(up))", preset.catalogFormula());
            } else {
                assertFalse(preset.formula().contains(" "), preset.id() + " formula should use compact function notation");
                assertFalse(preset.formula().contains("="), preset.id() + " formula must not stop at an intermediate assignment");
                assertFalse(preset.catalogFormula().contains(" "), preset.id() + " catalog formula must remain compact");
                assertTrue(preset.catalogFormula().length() <= 18, preset.id() + " catalog formula must fit the narrow theorem card");
                assertTrue(preset.formula().matches("^(push|mark|blink|heal|cleanse|resist|absorb|apply|bind|infuse|exec|launch)\\(.+\\)$"), preset.id() + " formula must state its final world effect");
            }
        }
    }

    @Test
    void cavalieriProjectileComposesTheConstructRunesIntoAnExecutableTheorem() {
        var graph = ProgramPresets.cavalieriProjectile();
        ValidationResult result = ProgramStorage.validateExecutable(graph);

        assertTrue(result.valid(), result.issues().toString());
        assertEquals(RuneType.UNIT, result.outputType());
        assertTrue(graph.nodes().stream().anyMatch(node -> node.runeId().equals("mathmod:solid_of_revolution")));
        assertTrue(graph.nodes().stream().anyMatch(node -> node.runeId().equals("mathmod:materialize_construct")));
        assertTrue(graph.nodes().stream().anyMatch(node -> node.runeId().equals("mathmod:compress_construct")));
        assertTrue(graph.nodes().stream().anyMatch(node -> node.runeId().equals("mathmod:spin_construct")));
        assertTrue(graph.nodes().stream().anyMatch(node -> node.runeId().equals("mathmod:launch_construct")));
    }

    @Test
    void alchemicalPresetsRemainTypedExplicitlyExecutedAndWithinBudget() {
        for (var graph : java.util.List.of(
                ProgramPresets.restorationEquation(),
                ProgramPresets.mercurialStep(),
                ProgramPresets.umbralVeil(),
                ProgramPresets.noctilucentSight(),
                ProgramPresets.witheringCorollary(),
                ProgramPresets.soulConstraint(),
                ProgramPresets.vitalInfusion(),
                ProgramPresets.cleansingProposition(),
                ProgramPresets.resistanceLemma(),
                ProgramPresets.absorptionMantle(),
                ProgramPresets.alchemicalMantle()
        )) {
            ValidationResult result = ProgramStorage.validateExecutable(graph);
            assertTrue(result.valid(), result.issues().toString());
            assertTrue(result.budgetUsed() <= graph.budgetLimit(), result.issues().toString());
            assertEquals(RuneType.UNIT, result.outputType());
            assertTrue(graph.nodes().stream().anyMatch(node ->
                    node.runeId().equals("mathmod:execute_effect_plan")));
        }
    }

    @Test
    void p9ResourceEstimatesExposeTheFixedReagents() {
        var requirements = ProgramCosts.requirementsFor(ProgramPresets.resistanceLemma());

        assertEquals(1, requirements.get("mathmod:vital_salt"));
        assertEquals(1, requirements.get("mathmod:homuncular_matrix"));
        assertTrue(ProgramResources.recommendedFor(ProgramPresets.resistanceLemma()).isEmpty());
    }

    @Test
    void advancedMathematicsPresetsAreTypedExecutableAndWithinBudget() {
        for (var graph : java.util.List.of(
                ProgramPresets.harmonicStep(),
                ProgramPresets.orthogonalStep(),
                ProgramPresets.quarterTurn(),
                ProgramPresets.quadratureLeap()
        )) {
            ValidationResult result = ProgramStorage.validateExecutable(graph);
            assertTrue(result.valid(), result.issues().toString());
            assertTrue(result.budgetUsed() <= graph.budgetLimit(), result.issues().toString());
            assertEquals(RuneType.UNIT, result.outputType());
        }
    }

    @Test
    void localFrameMovementPresetsFitTheStarterBudget() {
        for (var graph : java.util.List.of(
                ProgramPresets.rightAngle(),
                ProgramPresets.planarDash(),
                ProgramPresets.obliqueLeap()
        )) {
            ValidationResult result = ProgramStorage.validateExecutable(graph);
            assertTrue(result.valid(), result.issues().toString());
            assertEquals(16, result.budgetUsed());
            assertEquals(RuneType.UNIT, result.outputType());
        }
    }

    @Test
    void hopPresetIsAValidStarterProgram() {
        ValidationResult result = ProgramStorage.validateExecutable(ProgramPresets.hop());

        assertTrue(result.valid());
        assertEquals(12, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void dashPresetIsAValidLookMovementProgram() {
        ValidationResult result = ProgramStorage.validateExecutable(ProgramPresets.dash());

        assertTrue(result.valid());
        assertEquals(11, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void rayMarkerPresetIsAValidRaycastProgram() {
        ValidationResult result = ProgramStorage.validateExecutable(ProgramPresets.rayMarker());

        assertTrue(result.valid());
        assertEquals(9, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void blinkPresetIsAValidRaycastMovementProgram() {
        ValidationResult result = ProgramStorage.validateExecutable(ProgramPresets.blink());

        assertTrue(result.valid());
        assertEquals(13, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void liftPresetIsAValidTargetQueryProgram() {
        ValidationResult result = ProgramStorage.validateExecutable(ProgramPresets.liftNearbyEntities());

        assertTrue(result.valid());
        assertEquals(22, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void anchorPulsePresetIsAValidWorldProgram() {
        ValidationResult result = ProgramStorage.validateExecutable(ProgramPresets.anchorPulse());

        assertTrue(result.valid());
        assertEquals(3, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void sacrificePulsePresetIsAValidWorldProgram() {
        ValidationResult result = ProgramStorage.validateExecutable(ProgramPresets.sacrificePulse());

        assertTrue(result.valid());
        assertEquals(7, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void offeringSparkPresetIsAValidWorldProgram() {
        ValidationResult result = ProgramStorage.validateExecutable(ProgramPresets.offeringSpark());

        assertTrue(result.valid());
        assertEquals(10, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void wardingPulsePresetIsAValidWorldProgram() {
        ValidationResult result = ProgramStorage.validateExecutable(ProgramPresets.wardingPulse());

        assertTrue(result.valid());
        assertEquals(11, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
    }

    @Test
    void kineticTransducerIsAValidBoundedAnchorProgram() {
        var graph = ProgramPresets.kineticTransducer();
        ValidationResult result = ProgramStorage.validateExecutable(graph);

        assertTrue(result.valid(), result.issues().toString());
        assertEquals(22, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
        assertTrue(graph.nodes().stream().anyMatch(node -> node.runeId().equals("mathmod:entity_velocities")));
        assertTrue(graph.nodes().stream().anyMatch(node -> node.runeId().equals("mathmod:emit_anchor_redstone")));
    }

    private static String graphFingerprint(com.mathmod.runes.ProgramGraph graph) {
        String canonical = graph.nodes().stream().map(node -> node.id() + "|" + node.runeId() + "|" + node.constants().entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).map(entry -> entry.getKey() + "=" + entry.getValue()).collect(java.util.stream.Collectors.joining(","))).collect(java.util.stream.Collectors.joining(";"))
                + "#" + graph.edges().stream().map(edge -> edge.fromNodeId() + "|" + edge.toNodeId() + "|" + edge.inputName()).collect(java.util.stream.Collectors.joining(";"))
                + "#" + graph.outputNodeId() + "#" + graph.budgetLimit();
        return digest(canonical);
    }

    private static String digest(String value) {
        try {
            byte[] bytes = java.security.MessageDigest.getInstance("SHA-256").digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(bytes);
        } catch (java.security.NoSuchAlgorithmException impossible) {
            throw new AssertionError(impossible);
        }
    }

}
