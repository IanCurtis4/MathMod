package com.mathmod.client.screen;

import com.mathmod.runes.RuneType;

enum RuneTypeFamily {
    SCALAR("scalar", MathGuiTheme.GOLD),
    SPATIAL("spatial", MathGuiTheme.TEAL),
    ACTOR("actor", MathGuiTheme.BLUE),
    EFFECT("effect", MathGuiTheme.CORAL);

    private final String id;
    private final int color;

    RuneTypeFamily(String id, int color) {
        this.id = id;
        this.color = color;
    }

    String translationKey() {
        return "screen.mathmod.rune_programmer.type_family." + id;
    }

    int color() {
        return color;
    }

    static RuneTypeFamily of(RuneType type) {
        return switch (type) {
            case NUMBER, NUMBER_LIST, BOOL, CYCLIC_ELEMENT -> SCALAR;
            case VEC3, FRAME, VEC3_LIST, BLOCK_POS, BLOCK_POS_LIST, REGION, CONSTRUCT_BODY, RAY_HIT -> SPATIAL;
            case ATTRIBUTE_FIELD, SCALAR_FIELD -> SCALAR;
            case ENTITY, ENTITY_LIST, PLAYER -> ACTOR;
            case EFFECT_PLAN, UNIT -> EFFECT;
        };
    }
}
