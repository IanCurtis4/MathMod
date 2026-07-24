package com.mathmod.runes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Arrays;

public enum RuneType {
    UNIT("unit"),
    BOOL("bool"),
    NUMBER("number"),
    CYCLIC_ELEMENT("cyclic_element"),
    VEC3("vec3"),
    FRAME("frame"),
    NUMBER_LIST("number_list"),
    VEC3_LIST("vec3_list"),
    ENTITY("entity"),
    ENTITY_LIST("entity_list"),
    PLAYER("player"),
    BLOCK_POS("block_pos"),
    BLOCK_POS_LIST("block_pos_list"),
    RAY_HIT("ray_hit"),
    REGION("region"),
    CONSTRUCT_BODY("construct_body"),
    ATTRIBUTE_FIELD("attribute_field"),
    SCALAR_FIELD("scalar_field"),
    EFFECT_PLAN("effect_plan");

    private final String id;

    public static final Codec<RuneType> CODEC = Codec.STRING.comapFlatMap(RuneType::byId, RuneType::id);

    RuneType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public String translationKey() {
        return "type.mathmod." + id;
    }

    public static DataResult<RuneType> byId(String id) {
        return Arrays.stream(values())
                .filter(type -> type.id.equals(id))
                .findFirst()
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Unknown rune type '" + id + "'"));
    }
}
