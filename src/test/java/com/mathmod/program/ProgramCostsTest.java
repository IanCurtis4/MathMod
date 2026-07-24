package com.mathmod.program;

import com.mathmod.kubejs.KubeJsCompat;
import com.mathmod.kubejs.MathModKubeJS;
import com.mathmod.runes.ProgramGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramCostsTest {
    @Test
    void calculusPrecisionCostScalesLogarithmicallyWithNormalizedResult() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();
        workspace.apply(new CustomSpellInvocation(
                CustomSpellAction.FINITE_DIFFERENCE,
                Map.of("start", 0.0D, "end", 16.0D, "step", 1.0D)
        ));

        Map<String, Integer> requirements = ProgramCosts.attributeRequirementsFor(workspace.toGraph());

        assertEquals(1, requirements.get("continuity"));
        assertEquals(5, requirements.get("precision"));
    }

    @BeforeEach
    void resetCompatBefore() {
        KubeJsCompat.resetForTests();
    }

    @AfterEach
    void resetCompatAfter() {
        KubeJsCompat.resetForTests();
    }

    @Test
    void hopPresetHasNoCastCost() {
        assertTrue(ProgramCosts.requirementsFor(ProgramPresets.hop()).isEmpty());
    }

    @Test
    void blinkPresetAggregatesEnderPearlCost() {
        Map<String, Integer> requirements = ProgramCosts.requirementsFor(ProgramPresets.blink());

        assertEquals(1, requirements.size());
        assertEquals(1, requirements.get("minecraft:ender_pearl"));
    }

    @Test
    void explicitResourceSelectionsControlAttributePlan() {
        Map<String, Integer> inventory = Map.of("minecraft:feather", 1);

        ProgramCostPlan withoutLoadout = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.hop(),
                List.of(),
                inventory,
                false
        );

        assertFalse(withoutLoadout.success());
        assertEquals(1, withoutLoadout.missingAttributes().get("motion"));

        ProgramCostPlan withLoadout = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.hop(),
                List.of(new ResourceSelection("feather", 1)),
                inventory,
                false
        );

        assertTrue(withLoadout.success());
        assertTrue(withLoadout.lines().stream().anyMatch(line -> line.id().equals("feather")));
    }

    @Test
    void hopPlanUsesFeatherForMotionAttribute() {
        Map<String, Integer> inventory = Map.of("minecraft:feather", 1);

        ProgramCostPlan plan = ProgramCosts.planForAvailableSelectors(ProgramPresets.hop(), inventory, false);

        assertTrue(plan.success());
        assertEquals(1, plan.attributeRequirements().get("motion"));
        assertTrue(plan.lines().stream().anyMatch(line -> line.consumed() && line.selector().equals("minecraft:feather")));
    }

    @Test
    void liftPlanCombinesFixedItemsAndAttributeItems() {
        Map<String, Integer> inventory = Map.of(
                "minecraft:amethyst_shard", 1,
                "minecraft:redstone", 1
        );

        ProgramCostPlan plan = ProgramCosts.planForAvailableSelectors(ProgramPresets.liftNearbyEntities(), inventory, false);

        assertTrue(plan.success());
        assertEquals(1, plan.fixedRequirements().get("minecraft:amethyst_shard"));
        assertEquals(1, plan.attributeRequirements().get("information"));
        assertEquals(2, plan.attributeRequirements().get("force"));
        assertTrue(plan.lines().stream().anyMatch(line -> line.consumed() && line.selector().equals("minecraft:amethyst_shard")));
        assertTrue(plan.lines().stream().anyMatch(line -> line.consumed() && line.selector().equals("minecraft:redstone")));
    }

    @Test
    void budgetMaterialsCanRaiseEffectiveBudget() {
        Map<String, Integer> inventory = Map.of(
                "minecraft:feather", 1,
                "minecraft:diamond", 3
        );
        ProgramGraph graph = new ProgramGraph(
                ProgramPresets.hop().nodes(),
                ProgramPresets.hop().edges(),
                ProgramPresets.hop().outputNodeId(),
                1
        );

        ProgramCostPlan plan = ProgramCosts.planForAvailableSelectors(
                graph,
                List.of(new ResourceSelection("feather", 1), new ResourceSelection("diamond", 3)),
                inventory,
                false
        );

        assertTrue(plan.success());
        assertTrue(plan.budgetBonus() >= 11);
        assertTrue(ProgramStorage.validateExecutable(graph, plan.budgetBonus()).valid());
    }

    @Test
    void tierFourBudgetMaterialActsAsCatalyst() {
        MathModKubeJS.material("perfect_diamond", "minecraft:diamond", 16, 4);
        MathModKubeJS.addMaterialAttribute("perfect_diamond", "motion", 1);
        Map<String, Integer> inventory = Map.of("minecraft:diamond", 1);

        ProgramCostPlan plan = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.hop(),
                List.of(new ResourceSelection("perfect_diamond", 1)),
                inventory,
                false
        );

        assertTrue(plan.success());
        assertTrue(plan.lines().stream().anyMatch(line -> line.selector().equals("minecraft:diamond") && !line.consumed()));
        assertFalse(plan.lines().stream().anyMatch(line -> line.selector().equals("minecraft:diamond") && line.consumed()));
    }

    @Test
    void missingItemFeedbackTakesPriorityAndNamesTheDeficit() {
        ProgramCostPlan plan = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.blink(),
                List.of(),
                Map.of(),
                false
        );

        ProgramCostResult result = ProgramCostResult.failure(plan);

        assertEquals(Map.of("minecraft:ender_pearl", 1), plan.missingItems());
        assertEquals("item.mathmod.programmed_talisman.execute_missing_items", result.messageKey());
        assertTrue(result.messageArguments().isEmpty());
        assertEquals(plan.missingItems(), result.itemDeficits());
    }

    @Test
    void missingAttributeFeedbackNamesTheAbstractRequirement() {
        ProgramCostPlan plan = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.hop(),
                List.of(),
                Map.of(),
                false
        );

        ProgramCostResult result = ProgramCostResult.failure(plan);

        assertTrue(plan.missingItems().isEmpty());
        assertEquals("item.mathmod.programmed_talisman.execute_missing_attributes", result.messageKey());
        assertTrue(result.messageArguments().isEmpty());
        assertEquals(Map.of("motion", 1), result.attributeDeficits());

        ProgramExecutionResult executionResult = ProgramExecutionResult.failure(result);
        assertEquals(result.attributeDeficits(), executionResult.attributeDeficits());
    }

    @Test
    void missingBudgetFeedbackReportsTheExactDeficit() {
        ProgramGraph graph = new ProgramGraph(
                ProgramPresets.hop().nodes(),
                ProgramPresets.hop().edges(),
                ProgramPresets.hop().outputNodeId(),
                1
        );
        ProgramCostPlan plan = ProgramCosts.planForAvailableSelectors(
                graph,
                List.of(new ResourceSelection("feather", 1)),
                Map.of("minecraft:feather", 1),
                false
        );

        ProgramCostResult result = ProgramCostResult.failure(plan);

        assertTrue(plan.missingItems().isEmpty());
        assertTrue(plan.missingAttributes().isEmpty());
        assertEquals("item.mathmod.programmed_talisman.execute_missing_budget", result.messageKey());
        assertEquals(List.of(plan.missingBudgetAmount()), result.messageArguments());
    }

    @Test
    void costResultArgumentsReachTheExecutionMessage() {
        ProgramCostPlan plan = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.blink(),
                List.of(),
                Map.of(),
                false
        );
        ProgramCostResult costResult = ProgramCostResult.failure(plan);

        ProgramExecutionResult executionResult = ProgramExecutionResult.failure(costResult);

        assertFalse(executionResult.success());
        assertEquals(costResult.messageKey(), executionResult.messageKey());
        assertEquals(costResult.messageArguments(), executionResult.messageArguments());
        assertEquals(costResult.itemDeficits(), executionResult.itemDeficits());
        assertEquals(costResult.attributeDeficits(), executionResult.attributeDeficits());
    }

    @Test
    void multipleMissingItemsKeepTheirPlanOrderInPlayerFeedback() {
        ProgramCostPlan plan = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.hop(),
                List.of(
                        new ResourceSelection("diamond", 1),
                        new ResourceSelection("feather", 1)
                ),
                Map.of(),
                false
        );

        ProgramCostResult result = ProgramCostResult.failure(plan);

        assertEquals(
                List.of("minecraft:diamond", "minecraft:feather"),
                new ArrayList<>(plan.missingItems().keySet())
        );
        assertTrue(result.messageArguments().isEmpty());
        assertEquals(
                List.of("minecraft:diamond", "minecraft:feather"),
                new ArrayList<>(result.itemDeficits().keySet())
        );
    }

    @Test
    void highTierFillerDoesNotReplaceAContributingWitness() {
        MathModKubeJS.material("test_orientation", "minecraft:stone", 0, 1);
        MathModKubeJS.addMaterialAttribute("test_orientation", "orientation", 2);
        MathModKubeJS.material("unrelated_relic", "minecraft:nether_star", 0, 4);
        MathModKubeJS.addMaterialAttribute("unrelated_relic", "metamagic", 4);

        ProgramCostPlan fillerPlan = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.orthogonalStep(),
                List.of(
                        new ResourceSelection("feather", 1),
                        new ResourceSelection("test_orientation", 1),
                        new ResourceSelection("unrelated_relic", 1)
                ),
                Map.of(
                        "minecraft:feather", 1,
                        "minecraft:stone", 1,
                        "minecraft:nether_star", 1
                ),
                false
        );

        assertTrue(fillerPlan.missingAttributes().isEmpty());
        assertTrue(fillerPlan.missingTier());
        assertEquals(2, fillerPlan.requiredTier().level());
        assertEquals(1, fillerPlan.providedTier().level());

        ProgramCostPlan witnessPlan = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.orthogonalStep(),
                List.of(
                        new ResourceSelection("feather", 1),
                        new ResourceSelection("prismarine", 1)
                ),
                Map.of(
                        "minecraft:feather", 1,
                        "minecraft:prismarine_crystals", 1
                ),
                false
        );

        assertTrue(witnessPlan.success());
        assertEquals(2, witnessPlan.providedTier().level());
    }

    @Test
    void parsimonyDiscountsAttributesButNeverTier() {
        List<ResourceSelection> oneInk = List.of(new ResourceSelection("axiomatic_ink", 1));
        Map<String, Integer> inventory = Map.of("mathmod:axiomatic_ink", 1);

        ProgramCostPlan base = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.axiomOfParsimony(),
                oneInk,
                inventory,
                false
        );
        ProgramCostPlan discounted = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.axiomOfParsimony(),
                oneInk,
                inventory,
                false,
                new CastModifiers(1, 0.0D)
        );

        assertEquals(Map.of("metamagic", 4, "economy", 4), base.originalAttributeRequirements());
        assertEquals(base.originalAttributeRequirements(), base.attributeRequirements());
        assertEquals(Map.of("metamagic", 3, "economy", 3), discounted.attributeRequirements());
        assertFalse(base.success());
        assertTrue(discounted.success());
        assertEquals(base.requiredTier(), discounted.requiredTier());
        assertEquals(4, discounted.requiredTier().level());
    }

    @Test
    void unmodifiedMetamagicPlanKeepsItsFullSnapshotCost() {
        ProgramCostPlan snapshot = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.axiomOfParsimony(),
                List.of(new ResourceSelection("axiomatic_ink", 2)),
                Map.of("mathmod:axiomatic_ink", 2),
                false,
                CastModifiers.none()
        );

        assertTrue(snapshot.success());
        assertEquals(0, snapshot.modifiers().attributeDiscount());
        assertEquals(2, snapshot.lines().stream()
                .filter(line -> line.id().equals("axiomatic_ink"))
                .findFirst()
                .orElseThrow()
                .quantity());
    }

    @Test
    void conservationRecommendationUsesOnlyItsDualAttributeCatalyst() {
        List<ResourceSelection> recommended = ProgramResources.recommendedFor(
                ProgramPresets.conservationLemma()
        );

        assertEquals(List.of(new ResourceSelection("recursive_seal", 2)), recommended);
        ProgramCostPlan plan = ProgramCosts.planForAvailableSelectors(
                ProgramPresets.conservationLemma(),
                recommended,
                Map.of("mathmod:recursive_seal", 2),
                false
        );
        assertTrue(plan.success());
    }

}
