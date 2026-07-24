package com.mathmod.client.screen;

import com.mathmod.runes.RuneType;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuneTypeFamilyTest {
    @Test
    void everyRuneTypeBelongsToExactlyOneSemanticFamily() {
        Map<RuneTypeFamily, EnumSet<RuneType>> typesByFamily = new EnumMap<>(RuneTypeFamily.class);
        for (RuneType type : RuneType.values()) {
            typesByFamily.computeIfAbsent(RuneTypeFamily.of(type), ignored -> EnumSet.noneOf(RuneType.class))
                    .add(type);
        }

        assertEquals(
                EnumSet.of(RuneType.NUMBER, RuneType.NUMBER_LIST, RuneType.BOOL, RuneType.CYCLIC_ELEMENT, RuneType.ATTRIBUTE_FIELD, RuneType.SCALAR_FIELD),
                typesByFamily.get(RuneTypeFamily.SCALAR)
        );
        assertEquals(
                EnumSet.of(
                        RuneType.VEC3,
                        RuneType.FRAME,
                        RuneType.VEC3_LIST,
                        RuneType.BLOCK_POS,
                        RuneType.BLOCK_POS_LIST,
                        RuneType.REGION,
                        RuneType.CONSTRUCT_BODY,
                        RuneType.RAY_HIT
                ),
                typesByFamily.get(RuneTypeFamily.SPATIAL)
        );
        assertEquals(
                EnumSet.of(RuneType.ENTITY, RuneType.ENTITY_LIST, RuneType.PLAYER),
                typesByFamily.get(RuneTypeFamily.ACTOR)
        );
        assertEquals(
                EnumSet.of(RuneType.EFFECT_PLAN, RuneType.UNIT),
                typesByFamily.get(RuneTypeFamily.EFFECT)
        );
    }
}
