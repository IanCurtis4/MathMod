package com.mathmod.authoring;

import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.CustomSpellInvocation;
import com.mathmod.program.CustomSpellWorkspace;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.util.NamespacedId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrustedLegacyExpansionAdapterTest {
    private static final AuthoringMetadata.Snapshot SNAPSHOT = BuiltInAuthoringMetadata.snapshot();

    @Test
    void everyBuiltInDefaultFormProducesExactlyTheLegacyGraph() {
        for (CustomSpellAction action : CustomSpellAction.values()) {
            assertEqualExpansion(List.of(new CustomSpellInvocation(action, Map.of())));
        }
    }

    @Test
    void canonicalArgumentsMatchDescriptorsAndIgnoreUnknownKeys() {
        assertEqualExpansion(CustomSpellAction.NUMBER_ONE, Map.of("value", Double.POSITIVE_INFINITY, "unknown", 77.0D));
        assertEqualExpansion(CustomSpellAction.FINITE_DIFFERENCE, Map.of("start", -9_000.0D, "end", Double.NaN, "step", 0.25D));
        assertEqualExpansion(CustomSpellAction.SIMPSON_INTEGRAL,
                Map.of("lower", -2.0D, "upper", 7.0D, "f_lower", 3.0D,
                        "f_midpoint", -1.0D, "f_upper", 5.0D));
    }

    @Test
    void representativeGrowingSequencesProduceExactlyTheLegacyGraph() {
        List<CustomSpellInvocation> sequence = new ArrayList<>();
        for (CustomSpellAction action : List.of(CustomSpellAction.SELF, CustomSpellAction.LOOK_VECTOR,
                CustomSpellAction.RAYCAST, CustomSpellAction.RAY_HIT_POSITION,
                CustomSpellAction.BLINK, CustomSpellAction.NUMBER_ONE,
                CustomSpellAction.ADD_ONE, CustomSpellAction.SPHERE_REGION,
                CustomSpellAction.NEARBY_LIVING, CustomSpellAction.PUSH_TARGETS_PLAN,
                CustomSpellAction.EXECUTE_PLAN)) {
            sequence.add(CustomSpellInvocation.defaults(action));
            assertEqualExpansion(sequence);
        }
    }

    @Test
    void unknownFormAndReplayMismatchFailClosed() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();
        assertThrows(IllegalArgumentException.class, () -> TrustedLegacyExpansionAdapter.apply(
                SNAPSHOT, workspace, NamespacedId.parse("mathmod:not_a_form"), Map.of()));
        assertThrows(TrustedLegacyExpansionAdapter.ReplayMismatch.class, () ->
                TrustedLegacyExpansionAdapter.replayExactly(SNAPSHOT,
                        List.of(new TrustedLegacyExpansionAdapter.FormInvocation(NamespacedId.parse("mathmod:self"), Map.of())),
                        legacyGraph(List.of(CustomSpellInvocation.defaults(CustomSpellAction.NUMBER_ONE)))));
    }

    private static void assertEqualExpansion(List<CustomSpellInvocation> invocations) {
        ProgramGraph expected = legacyGraph(invocations);
        List<TrustedLegacyExpansionAdapter.FormInvocation> forms = invocations.stream()
                .map(invocation -> new TrustedLegacyExpansionAdapter.FormInvocation(
                        NamespacedId.parse(invocation.action().persistentId()), suppliedArguments(invocation)))
                .toList();
        assertEquals(expected, TrustedLegacyExpansionAdapter.replayExactly(SNAPSHOT, forms, expected));
    }

    private static void assertEqualExpansion(CustomSpellAction action, Map<String, Double> supplied) {
        ProgramGraph expected = legacyGraph(List.of(new CustomSpellInvocation(action, supplied)));
        assertEquals(expected, TrustedLegacyExpansionAdapter.replayExactly(SNAPSHOT,
                List.of(new TrustedLegacyExpansionAdapter.FormInvocation(
                        NamespacedId.parse(action.persistentId()), supplied)), expected));
    }

    private static ProgramGraph legacyGraph(List<CustomSpellInvocation> invocations) {
        CustomSpellWorkspace legacy = new CustomSpellWorkspace();
        invocations.forEach(legacy::apply);
        return legacy.toGraph();
    }

    private static Map<String, Double> suppliedArguments(CustomSpellInvocation invocation) {
        Map<String, Double> supplied = new LinkedHashMap<>(invocation.arguments());
        supplied.put("ignored", 999.0D);
        return supplied;
    }
}
