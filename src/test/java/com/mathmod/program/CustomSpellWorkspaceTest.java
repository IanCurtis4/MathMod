package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.RuneType;
import com.mathmod.runes.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomSpellWorkspaceTest {
    @Test
    void literalAndCalculusParametersSurviveWorkspaceReload() {
        CustomSpellWorkspace original = new CustomSpellWorkspace();
        original.apply(new CustomSpellInvocation(
                CustomSpellAction.NUMBER_ONE,
                Map.of("value", 2.5D)
        ));
        original.apply(new CustomSpellInvocation(
                CustomSpellAction.FINITE_DIFFERENCE,
                Map.of("start", 3.0D, "end", 7.0D, "step", 0.5D)
        ));

        CustomSpellWorkspace loaded = new CustomSpellWorkspace();
        loaded.loadInvocations(original.invocations());

        assertEquals(original.invocations(), loaded.invocations());
        assertEquals(original.toGraph(), loaded.toGraph());
        assertTrue(ProgramStorage.validate(loaded.toGraph()).valid());
    }

    @Test
    void simpsonPanelBuildsBoundsAndNormalizesItsResult() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();
        workspace.apply(new CustomSpellInvocation(
                CustomSpellAction.SIMPSON_INTEGRAL,
                Map.of(
                        "lower", 0.0D,
                        "upper", 2.0D,
                        "f_lower", 0.0D,
                        "f_midpoint", 1.0D,
                        "f_upper", 4.0D
                )
        ));

        ProgramGraph graph = workspace.toGraph();
        ProgramNormalization normalization = ProgramNormalizer.normalize(
                graph,
                com.mathmod.runes.MathModRuneBootstrap.registry()
        );
        NormalizedValue.NumberValue result = (NormalizedValue.NumberValue) normalization
                .value(graph.outputNodeId())
                .orElseThrow();

        assertEquals(8.0D / 3.0D, result.value(), 1.0E-9D);
    }

    @Test
    void newActionsAreAppendedAfterTheStableSavedOrdinals() {
        assertEquals(0, CustomSpellAction.SELF.ordinal());
        assertEquals(34, CustomSpellAction.EXECUTE_PLAN.ordinal());
        assertEquals(35, CustomSpellAction.RIGHT_BASIS_VECTOR.ordinal());
        assertEquals(44, CustomSpellAction.HEAL_SELF.ordinal());
        assertEquals(51, CustomSpellAction.ALCHEMICAL_MANTLE.ordinal());
    }

    @Test
    void basisVectorActionBuildsAReusableTypedTransform() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        workspace.apply(CustomSpellAction.RIGHT_BASIS_VECTOR);
        workspace.apply(CustomSpellAction.PUSH_SELF);

        ProgramGraph graph = workspace.toGraph();
        ValidationResult result = ProgramStorage.validateExecutable(graph);
        assertTrue(result.valid(), result.issues().toString());
        assertTrue(graph.nodes().stream().anyMatch(node -> node.runeId().equals("mathmod:player_frame")));
        assertTrue(graph.nodes().stream().anyMatch(node -> node.runeId().equals("mathmod:transform_local_vector")));
    }

    @Test
    void canAssembleSelfPushSpellFromScratch() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        workspace.apply(CustomSpellAction.UP_VECTOR);
        workspace.apply(CustomSpellAction.PUSH_SELF);

        ValidationResult result = ProgramStorage.validateExecutable(workspace.toGraph());

        assertTrue(result.valid());
        assertEquals(12, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
        assertEquals(2, workspace.steps().size());
        assertFalse(workspace.describeConnections().isEmpty());
    }

    @Test
    void actionPreviewDistinguishesInferredFromCurrentPremises() {
        CustomSpellWorkspace blank = new CustomSpellWorkspace();

        CustomActionPreview inferred = blank.preview(CustomSpellAction.PUSH_SELF);

        assertEquals(List.of(CustomInputSlot.PLAYER, CustomInputSlot.VECTOR), inferred.inferredInputs());
        assertTrue(inferred.currentInputs().isEmpty());
        assertEquals(6, inferred.addedRunes());
        assertEquals(5, inferred.addedBindings());
        assertEquals(RuneType.UNIT, inferred.resultType());

        blank.apply(CustomSpellAction.SELF);
        blank.apply(CustomSpellAction.UP_VECTOR);
        CustomActionPreview reused = blank.preview(CustomSpellAction.PUSH_SELF);

        assertEquals(List.of(CustomInputSlot.PLAYER, CustomInputSlot.VECTOR), reused.currentInputs());
        assertTrue(reused.inferredInputs().isEmpty());
        assertEquals(1, reused.addedRunes());
        assertEquals(2, reused.addedBindings());
    }

    @Test
    void previewDoesNotMutateTheWorkspace() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();
        workspace.apply(CustomSpellAction.RIGHT_BASIS_VECTOR);
        ProgramGraph graphBeforePreview = workspace.toGraph();
        List<CustomSpellAction> actionsBeforePreview = workspace.actions();

        CustomActionPreview preview = workspace.preview(CustomSpellAction.PUSH_SELF);

        assertEquals(graphBeforePreview, workspace.toGraph());
        assertEquals(actionsBeforePreview, workspace.actions());
        assertEquals(List.of(CustomInputSlot.PLAYER, CustomInputSlot.VECTOR), preview.currentInputs());
    }

    @Test
    void sourceFormPreviewHasNoInputPremises() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        CustomActionPreview preview = workspace.preview(CustomSpellAction.SELF);

        assertTrue(preview.inputs().isEmpty());
        assertEquals(1, preview.addedRunes());
        assertEquals(0, preview.addedBindings());
    }

    @Test
    void repeatedExplicitSelfAlwaysAddsOneRuneAndBecomesTheOutput() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        workspace.apply(CustomSpellAction.SELF);
        CustomActionPreview repeatedPreview = workspace.preview(CustomSpellAction.SELF);
        workspace.apply(CustomSpellAction.SELF);

        assertEquals(1, repeatedPreview.addedRunes());
        assertEquals(2, workspace.steps().size());
        assertEquals(2, workspace.toGraph().nodes().stream()
                .filter(node -> node.runeId().equals("mathmod:self_player"))
                .count());
        assertEquals(workspace.steps().getLast().outputNodeId(), workspace.toGraph().outputNodeId());

        assertTrue(workspace.undoLast());
        assertEquals(1, workspace.toGraph().nodes().stream()
                .filter(node -> node.runeId().equals("mathmod:self_player"))
                .count());
    }

    @Test
    void everyLaboratoryActionRemainsPreviewableAfterItsOwnRepeatedApplication() {
        for (CustomSpellAction action : CustomSpellAction.values()) {
            CustomSpellWorkspace workspace = new CustomSpellWorkspace();

            workspace.apply(action);
            CustomActionPreview firstRepeatedPreview = assertDoesNotThrow(
                    () -> workspace.preview(action), action.name() + " must preview after its first application"
            );
            workspace.apply(action);
            CustomActionPreview secondRepeatedPreview = assertDoesNotThrow(
                    () -> workspace.preview(action), action.name() + " must preview after its repeated application"
            );

            assertTrue(firstRepeatedPreview.addedRunes() >= 1, action.name() + " repeated preview must describe a real addition");
            assertTrue(secondRepeatedPreview.addedRunes() >= 1, action.name() + " repeated preview must remain a real addition");
        }
    }

    @Test
    void canAssembleTargetPlanSpellFromScratch() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        workspace.apply(CustomSpellAction.NEARBY_LIVING);
        workspace.apply(CustomSpellAction.FILTER_NON_PLAYERS);
        workspace.apply(CustomSpellAction.NEAREST_TARGETS);
        workspace.apply(CustomSpellAction.UP_VECTOR);
        workspace.apply(CustomSpellAction.PUSH_TARGETS_PLAN);
        workspace.apply(CustomSpellAction.EXECUTE_PLAN);

        ValidationResult result = ProgramStorage.validateExecutable(workspace.toGraph());

        assertTrue(result.valid());
        assertEquals(22, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
        assertEquals(6, workspace.steps().size());
        assertTrue(workspace.describeConnections().stream().anyMatch(link -> link.contains("plan")));
    }

    @Test
    void savedCustomActionSequenceCanBeLoadedForEditing() {
        CustomSpellWorkspace original = new CustomSpellWorkspace();
        original.apply(CustomSpellAction.UP_VECTOR);
        original.apply(CustomSpellAction.VECTOR_ADD_UP);
        original.apply(CustomSpellAction.PUSH_SELF);
        List<CustomSpellAction> actions = original.actions();

        CustomSpellWorkspace loaded = new CustomSpellWorkspace();
        loaded.loadActions(actions);

        assertEquals(actions, loaded.actions());
        assertEquals(original.toGraph(), loaded.toGraph());
        assertTrue(ProgramStorage.validateExecutable(loaded.toGraph()).valid());
    }

    @Test
    void undoRemovesTheLastStepAndDeterministicallyRebuildsTheGraph() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();
        workspace.apply(CustomSpellAction.LOOK_VECTOR);
        workspace.apply(CustomSpellAction.SCALE_VECTOR);
        workspace.apply(CustomSpellAction.PUSH_SELF);

        CustomSpellWorkspace expected = new CustomSpellWorkspace();
        expected.apply(CustomSpellAction.LOOK_VECTOR);
        expected.apply(CustomSpellAction.SCALE_VECTOR);

        assertTrue(workspace.undoLast());
        assertEquals(expected.actions(), workspace.actions());
        assertEquals(expected.toGraph(), workspace.toGraph());
    }

    @Test
    void undoOnAnEmptyWorkspaceDoesNothing() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        assertFalse(workspace.undoLast());
        assertTrue(workspace.isEmpty());
    }

    @Test
    void everyLaboratoryCategoryContainsAtLeastOneAction() {
        for (CustomSpellAction.Category category : CustomSpellAction.Category.values()) {
            assertTrue(java.util.Arrays.stream(CustomSpellAction.values())
                    .anyMatch(action -> action.category() == category));
        }
    }

    @Test
    void everyLaboratoryActionDeclaresItsActualOutputType() {
        for (CustomSpellAction action : CustomSpellAction.values()) {
            CustomSpellWorkspace workspace = new CustomSpellWorkspace();
            workspace.apply(action);

            ValidationResult result = ProgramStorage.validateExecutable(workspace.toGraph());

            assertEquals(action.resultType(), result.outputType(), action.name());
        }
    }

    @Test
    void intermediateCustomWorkspaceShowsInvalidNonUnitOutput() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        workspace.apply(CustomSpellAction.RAYCAST);

        ValidationResult result = ProgramStorage.validateExecutable(workspace.toGraph());

        assertFalse(result.valid());
        assertEquals(RuneType.RAY_HIT, result.outputType());
    }

    @Test
    void customWorkspaceCanUseMathematicalRunes() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        workspace.apply(CustomSpellAction.NUMBER_ONE);
        workspace.apply(CustomSpellAction.DOUBLE_NUMBER);
        workspace.apply(CustomSpellAction.CLAMP_NUMBER);
        workspace.apply(CustomSpellAction.LOOK_VECTOR);
        workspace.apply(CustomSpellAction.NORMALIZE_VECTOR);
        workspace.apply(CustomSpellAction.SCALE_VECTOR);
        workspace.apply(CustomSpellAction.PUSH_SELF);

        ValidationResult result = ProgramStorage.validateExecutable(workspace.toGraph());

        assertTrue(result.valid());
        assertEquals(RuneType.UNIT, result.outputType());
        assertFalse(workspace.describeConnections().isEmpty());
    }

    @Test
    void customWorkspaceCanFilterTargetsWithARegion() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        workspace.apply(CustomSpellAction.NEARBY_LIVING);
        workspace.apply(CustomSpellAction.SPHERE_REGION);
        workspace.apply(CustomSpellAction.FILTER_TARGETS_REGION);
        workspace.apply(CustomSpellAction.UP_VECTOR);
        workspace.apply(CustomSpellAction.PUSH_TARGETS_PLAN);
        workspace.apply(CustomSpellAction.EXECUTE_PLAN);

        ValidationResult result = ProgramStorage.validateExecutable(workspace.toGraph());

        assertTrue(result.valid());
        assertEquals(24, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
        assertTrue(workspace.describeConnections().stream().anyMatch(link -> link.contains("targets_region")));
    }

    @Test
    void customWorkspaceCanSampleARegionAndMarkItsAverage() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        workspace.apply(CustomSpellAction.SPHERE_REGION);
        workspace.apply(CustomSpellAction.SAMPLE_REGION);
        workspace.apply(CustomSpellAction.AVERAGE_POSITION);
        workspace.apply(CustomSpellAction.DEBUG_MARKER);

        ValidationResult result = ProgramStorage.validateExecutable(workspace.toGraph());

        assertTrue(result.valid());
        assertEquals(13, result.budgetUsed());
        assertEquals(RuneType.UNIT, result.outputType());
        assertTrue(workspace.describeConnections().stream().anyMatch(link -> link.contains("sample")));
    }

    @Test
    void customWorkspaceCanComposeAndExecuteAnAlchemicalMantle() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        workspace.apply(CustomSpellAction.ALCHEMICAL_MANTLE);
        workspace.apply(CustomSpellAction.EXECUTE_PLAN);

        ValidationResult result = ProgramStorage.validateExecutable(workspace.toGraph());

        assertTrue(result.valid(), result.issues().toString());
        assertEquals(RuneType.UNIT, result.outputType());
        assertTrue(workspace.toGraph().nodes().stream()
                .anyMatch(node -> node.runeId().equals("mathmod:combine_effect_plans")));
        assertTrue(workspace.toGraph().nodes().stream()
                .anyMatch(node -> node.runeId().equals("mathmod:execute_effect_plan")));
    }

    @Test
    void customWorkspaceCanBuildAHostileSoulConstraint() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();

        workspace.apply(CustomSpellAction.SOUL_BIND_HOSTILES);
        workspace.apply(CustomSpellAction.EXECUTE_PLAN);

        ValidationResult result = ProgramStorage.validateExecutable(workspace.toGraph());

        assertTrue(result.valid(), result.issues().toString());
        assertEquals(RuneType.UNIT, result.outputType());
        assertTrue(workspace.toGraph().nodes().stream()
                .anyMatch(node -> node.runeId().equals("mathmod:nearby_entities")
                        && "hostile".equals(node.constants().get("predicate"))));
        assertTrue(workspace.toGraph().nodes().stream()
                .anyMatch(node -> node.runeId().equals("mathmod:soul_bind_entities_plan")));
    }
}
