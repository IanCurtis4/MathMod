package com.mathmod.program;

import com.mathmod.runes.BuiltInRunes;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RuneType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlchemicalRuneDefinitionTest {
    @Test
    void effectRunesExposeTypedPlansInsteadOfApplyingEffectsDirectly() {
        RuneRegistry registry = registry();

        assertSignature(registry, "mathmod:heal_entities_plan",
                Map.of("entities", RuneType.ENTITY_LIST, "amount", RuneType.NUMBER));
        assertSignature(registry, "mathmod:speed_entities_plan", statusInputs());
        assertSignature(registry, "mathmod:invisibility_entities_plan", statusInputs());
        assertSignature(registry, "mathmod:night_vision_entities_plan", statusInputs());
        assertSignature(registry, "mathmod:wither_entities_plan", statusInputs());
        assertSignature(registry, "mathmod:vital_infusion_plan", statusInputs());
        assertSignature(registry, "mathmod:soul_bind_entities_plan",
                Map.of(
                        "entities", RuneType.ENTITY_LIST,
                        "anchor", RuneType.VEC3,
                        "duration", RuneType.NUMBER
                ));
        assertSignature(registry, "mathmod:combine_effect_plans",
                Map.of("first", RuneType.EFFECT_PLAN, "second", RuneType.EFFECT_PLAN));

        RuneDefinition execute = registry.find("mathmod:execute_effect_plan").orElseThrow();
        assertEquals(RuneType.UNIT, execute.outputType());
        assertEquals(List.of(RuneType.EFFECT_PLAN), execute.inputs().stream()
                .map(input -> input.type())
                .toList());
    }

    @Test
    void everyAlchemicalExecutorIsAllowedByTheServerPolicy() {
        RuneRegistry registry = registry();

        for (String id : List.of(
                "mathmod:player_as_entity_list",
                "mathmod:heal_entities_plan",
                "mathmod:speed_entities_plan",
                "mathmod:invisibility_entities_plan",
                "mathmod:night_vision_entities_plan",
                "mathmod:wither_entities_plan",
                "mathmod:soul_bind_entities_plan",
                "mathmod:vital_infusion_plan",
                "mathmod:combine_effect_plans"
        )) {
            String executor = registry.find(id).orElseThrow().executorKey();
            assertTrue(ProgramExecutionPolicy.supportsExecutorKey(executor), executor);
        }
    }

    private static RuneRegistry registry() {
        RuneRegistry registry = new RuneRegistry();
        BuiltInRunes.registerAll(registry);
        return registry;
    }

    private static Map<String, RuneType> statusInputs() {
        return Map.of(
                "entities", RuneType.ENTITY_LIST,
                "duration", RuneType.NUMBER,
                "level", RuneType.NUMBER
        );
    }

    private static void assertSignature(RuneRegistry registry, String id, Map<String, RuneType> expected) {
        RuneDefinition definition = registry.find(id).orElseThrow();
        assertEquals(RuneType.EFFECT_PLAN, definition.outputType());
        assertEquals(expected, definition.inputs().stream()
                .collect(Collectors.toMap(input -> input.name(), input -> input.type())));
    }
}
