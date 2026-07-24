package com.mathmod.program;

public enum CustomInputSlot {
    PLAYER("player"),
    NUMBER("number"),
    VECTOR("vector"),
    POSITION("position"),
    FRAME("frame"),
    RAY_HIT("ray_hit"),
    ENTITY_LIST("entity_list"),
    BLOCK_LIST("block_list"),
    POSITION_LIST("position_list"),
    REGION("region"),
    EFFECT_PLAN("effect_plan");

    private final String id;

    CustomInputSlot(String id) {
        this.id = id;
    }

    public String translationKey() {
        return "screen.mathmod.rune_programmer.custom.input." + id;
    }
}
