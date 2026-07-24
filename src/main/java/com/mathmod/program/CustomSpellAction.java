package com.mathmod.program;

import com.mathmod.MathMod;
import com.mathmod.runes.RuneType;
import com.mathmod.util.NamespacedId;

import java.util.Arrays;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CustomSpellAction {
    SELF("screen.mathmod.rune_programmer.custom.self", "mathmod:self_player", "self"),
    NUMBER_ONE("screen.mathmod.rune_programmer.custom.number_literal", "mathmod:constant_number", "n"),
    ADD_ONE("screen.mathmod.rune_programmer.custom.add_one", "mathmod:number_add", "x+1"),
    SUBTRACT_ONE("screen.mathmod.rune_programmer.custom.subtract_one", "mathmod:number_subtract", "x-1"),
    DOUBLE_NUMBER("screen.mathmod.rune_programmer.custom.double_number", "mathmod:number_multiply", "2x"),
    HALVE_NUMBER("screen.mathmod.rune_programmer.custom.halve_number", "mathmod:number_divide", "x/2"),
    CLAMP_NUMBER("screen.mathmod.rune_programmer.custom.clamp_number", "mathmod:number_clamp", "clamp(x)"),
    UP_VECTOR("screen.mathmod.rune_programmer.custom.up_vector", "mathmod:vector_from_numbers", "up"),
    LOOK_VECTOR("screen.mathmod.rune_programmer.custom.look_vector", "mathmod:look_vector", "look"),
    SCALE_VECTOR("screen.mathmod.rune_programmer.custom.scale_vector", "mathmod:scale_vector", "s*v"),
    VECTOR_ADD_UP("screen.mathmod.rune_programmer.custom.vector_add_up", "mathmod:vector_add", "v+up"),
    VECTOR_SUBTRACT_UP("screen.mathmod.rune_programmer.custom.vector_subtract_up", "mathmod:vector_subtract", "v-up"),
    NORMALIZE_VECTOR("screen.mathmod.rune_programmer.custom.normalize_vector", "mathmod:vector_normalize", "v/|v|"),
    VECTOR_LENGTH("screen.mathmod.rune_programmer.custom.vector_length", "mathmod:vector_length", "|v|"),
    DOT_WITH_LOOK("screen.mathmod.rune_programmer.custom.dot_with_look", "mathmod:vector_dot", "v\u00B7look"),
    DISTANCE_TO_SELF("screen.mathmod.rune_programmer.custom.distance_to_self", "mathmod:vector_distance", "d(v,self)"),
    SPHERE_REGION("screen.mathmod.rune_programmer.custom.sphere_region", "mathmod:sphere_region", "sphere(r)"),
    BOX_REGION("screen.mathmod.rune_programmer.custom.box_region", "mathmod:box_region", "box(v)"),
    REGION_CONTAINS_SELF("screen.mathmod.rune_programmer.custom.region_contains_self", "mathmod:region_contains", "self\u2208R"),
    SAMPLE_REGION("screen.mathmod.rune_programmer.custom.sample_region", "mathmod:sample_region", "sample(R)"),
    RAYCAST("screen.mathmod.rune_programmer.custom.raycast", "mathmod:raycast_block", "ray(self)"),
    RAY_HIT_POSITION("screen.mathmod.rune_programmer.custom.ray_hit_position", "mathmod:ray_hit_position", "pos(hit)"),
    NEARBY_LIVING("screen.mathmod.rune_programmer.custom.nearby_living", "mathmod:nearby_entities", "living(4)"),
    FILTER_NON_PLAYERS("screen.mathmod.rune_programmer.custom.filter_non_players", "mathmod:filter_entities", "xs-player"),
    FILTER_TARGETS_REGION("screen.mathmod.rune_programmer.custom.filter_targets_region", "mathmod:filter_entities_in_region", "xs\u2229R"),
    NEAREST_TARGETS("screen.mathmod.rune_programmer.custom.nearest_targets", "mathmod:nearest_entities", "nearest(xs)"),
    NEARBY_BLOCKS("screen.mathmod.rune_programmer.custom.nearby_blocks", "mathmod:nearby_blocks", "blocks(4)"),
    FILTER_BLOCKS_REGION("screen.mathmod.rune_programmer.custom.filter_blocks_region", "mathmod:filter_blocks_in_region", "blocks\u2229R"),
    BLOCK_POSITIONS("screen.mathmod.rune_programmer.custom.block_positions", "mathmod:block_positions", "pos(blocks)"),
    AVERAGE_POSITION("screen.mathmod.rune_programmer.custom.average_position", "mathmod:average_position", "mean(pos)"),
    PUSH_SELF("screen.mathmod.rune_programmer.custom.push_self", "mathmod:push_self", "push(self)"),
    DEBUG_MARKER("screen.mathmod.rune_programmer.custom.debug_marker", "mathmod:debug_marker", "mark(pos)"),
    BLINK("screen.mathmod.rune_programmer.custom.blink", "mathmod:blink_self_to_hit", "blink(hit)"),
    PUSH_TARGETS_PLAN("screen.mathmod.rune_programmer.custom.push_targets_plan", "mathmod:push_entities_plan", "push(xs)"),
    EXECUTE_PLAN("screen.mathmod.rune_programmer.custom.execute_plan", "mathmod:execute_effect_plan", "exec(plan)"),
    RIGHT_BASIS_VECTOR("screen.mathmod.rune_programmer.custom.right_basis_vector", "mathmod:right_basis_vector", "right(F)"),
    FORWARD_BASIS_VECTOR("screen.mathmod.rune_programmer.custom.forward_basis_vector", "mathmod:forward_basis_vector", "forward(F)"),
    OBLIQUE_BASIS_VECTOR("screen.mathmod.rune_programmer.custom.oblique_basis_vector", "mathmod:oblique_basis_vector", "right+fwd"),
    SINE_NUMBER("screen.mathmod.rune_programmer.custom.sine_number", "mathmod:number_sin", "sin(x)"),
    COSINE_NUMBER("screen.mathmod.rune_programmer.custom.cosine_number", "mathmod:number_cos", "cos(x)"),
    CROSS_WITH_UP("screen.mathmod.rune_programmer.custom.cross_with_up", "mathmod:vector_cross", "up\u00D7v"),
    PROJECT_ONTO_LOOK("screen.mathmod.rune_programmer.custom.project_onto_look", "mathmod:vector_project", "proj_look(v)"),
    REFLECT_ACROSS_UP("screen.mathmod.rune_programmer.custom.reflect_across_up", "mathmod:vector_reflect", "refl_up(v)"),
    QUARTER_TURN_VECTOR("screen.mathmod.rune_programmer.custom.quarter_turn_vector", "mathmod:cyclic_rotate_y", "C4(1)\u00B7v"),
    HEAL_SELF("screen.mathmod.rune_programmer.custom.heal_self", "mathmod:heal_entities_plan", "heal(self)"),
    SPEED_SELF("screen.mathmod.rune_programmer.custom.speed_self", "mathmod:speed_entities_plan", "speed(self)"),
    INVISIBILITY_SELF("screen.mathmod.rune_programmer.custom.invisibility_self", "mathmod:invisibility_entities_plan", "invis(self)"),
    NIGHT_VISION_SELF("screen.mathmod.rune_programmer.custom.night_vision_self", "mathmod:night_vision_entities_plan", "night(self)"),
    WITHER_HOSTILES("screen.mathmod.rune_programmer.custom.wither_hostiles", "mathmod:wither_entities_plan", "wither(H)"),
    SOUL_BIND_HOSTILES("screen.mathmod.rune_programmer.custom.soul_bind_hostiles", "mathmod:soul_bind_entities_plan", "bind(H)"),
    VITAL_INFUSION_SELF("screen.mathmod.rune_programmer.custom.vital_infusion_self", "mathmod:vital_infusion_plan", "infuse(self)"),
    ALCHEMICAL_MANTLE("screen.mathmod.rune_programmer.custom.alchemical_mantle", "mathmod:combine_effect_plans", "speed+night"),
    PARSIMONY_SELF("screen.mathmod.rune_programmer.custom.parsimony_self", "mathmod:parsimony_plan", "pars(self)"),
    CONSERVATION_SELF("screen.mathmod.rune_programmer.custom.conservation_self", "mathmod:conservation_plan", "cons(self)"),
    FINITE_DIFFERENCE("screen.mathmod.rune_programmer.custom.finite_difference", "mathmod:finite_difference", "Δf/Δx"),
    SIMPSON_INTEGRAL("screen.mathmod.rune_programmer.custom.simpson_integral", "mathmod:simpson_integral", "∫[a,b]"),
    ABS_NUMBER("screen.mathmod.rune_programmer.custom.abs_number", "mathmod:number_abs", "|x|"),
    MIN_NUMBER("screen.mathmod.rune_programmer.custom.min_number", "mathmod:number_min", "min(x,1)"),
    MAX_NUMBER("screen.mathmod.rune_programmer.custom.max_number", "mathmod:number_max", "max(x,1)"),
    POWER_NUMBER("screen.mathmod.rune_programmer.custom.power_number", "mathmod:number_power", "x^2"),
    SQRT_NUMBER("screen.mathmod.rune_programmer.custom.sqrt_number", "mathmod:number_sqrt", "sqrt(x)"),
    LOG_NUMBER("screen.mathmod.rune_programmer.custom.log_number", "mathmod:number_log", "log_10(x)"),
    EXP_NUMBER("screen.mathmod.rune_programmer.custom.exp_number", "mathmod:number_exp", "e^x"),
    ATAN2_NUMBER("screen.mathmod.rune_programmer.custom.atan2_number", "mathmod:number_atan2", "atan2(x,1)"),
    LERP_NUMBER("screen.mathmod.rune_programmer.custom.lerp_number", "mathmod:number_lerp", "lerp(x,1,.5)"),
    AT_LEAST_NUMBER("screen.mathmod.rune_programmer.custom.at_least_number", "mathmod:number_at_least", "x>=.25"),
    SELECT_NUMBER("screen.mathmod.rune_programmer.custom.select_number", "mathmod:number_select", "if(b,1,0)");

    private final String translationKey;
    private final String iconRuneId;
    private final String compactNotation;
    private final String id;
    private static final Map<String, CustomSpellAction> BY_ID = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(CustomSpellAction::id, Function.identity()));

    CustomSpellAction(String translationKey, String iconRuneId, String compactNotation) {
        this.translationKey = translationKey;
        this.iconRuneId = iconRuneId;
        this.compactNotation = compactNotation;
        this.id = NamespacedId.of(MathMod.MOD_ID, name().toLowerCase(Locale.ROOT)).toString();
    }

    public String translationKey() {
        return translationKey;
    }

    public String iconRuneId() {
        return iconRuneId;
    }

    public String compactNotation() {
        return compactNotation;
    }

    public String id() {
        return id;
    }

    public String persistentId() {
        return id;
    }

    public List<CustomNumericParameter> numericParameters() {
        return switch (this) {
            case NUMBER_ONE -> List.of(parameter("value", 1.0D, -1024.0D, 1024.0D));
            case FINITE_DIFFERENCE -> List.of(
                    parameter("start", 0.0D, -1024.0D, 1024.0D),
                    parameter("end", 1.0D, -1024.0D, 1024.0D),
                    parameter("step", 1.0D, -1024.0D, 1024.0D)
            );
            case SIMPSON_INTEGRAL -> List.of(
                    parameter("lower", 0.0D, -1024.0D, 1024.0D),
                    parameter("upper", 1.0D, -1024.0D, 1024.0D),
                    parameter("f_lower", 0.0D, -1024.0D, 1024.0D),
                    parameter("f_midpoint", 1.0D, -1024.0D, 1024.0D),
                    parameter("f_upper", 0.0D, -1024.0D, 1024.0D)
            );
            default -> List.of();
        };
    }

    private static CustomNumericParameter parameter(String key, double defaultValue, double min, double max) {
        return new CustomNumericParameter(
                key,
                "screen.mathmod.rune_programmer.parameter." + key,
                defaultValue,
                min,
                max
        );
    }

    public static Optional<CustomSpellAction> fromPersistentId(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        String candidate = value.trim();
        String parsed = NamespacedId.tryParse(
                candidate.contains(":") ? candidate : MathMod.MOD_ID + ":" + candidate.toLowerCase(Locale.ROOT)
        ).map(NamespacedId::toString).orElse(null);
        CustomSpellAction canonical = parsed == null ? null : BY_ID.get(parsed);
        if (canonical != null) {
            return Optional.of(canonical);
        }
        try {
            return Optional.of(valueOf(candidate.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    public Category category() {
        return switch (this) {
            case SELF, NUMBER_ONE, UP_VECTOR, LOOK_VECTOR -> Category.SOURCES;
            case ADD_ONE, SUBTRACT_ONE, DOUBLE_NUMBER, HALVE_NUMBER, CLAMP_NUMBER,
                    ABS_NUMBER, MIN_NUMBER, MAX_NUMBER, POWER_NUMBER, SQRT_NUMBER,
                    LOG_NUMBER, EXP_NUMBER, ATAN2_NUMBER, LERP_NUMBER, AT_LEAST_NUMBER, SELECT_NUMBER,
                    SCALE_VECTOR, VECTOR_ADD_UP, VECTOR_SUBTRACT_UP, NORMALIZE_VECTOR,
                    VECTOR_LENGTH, DOT_WITH_LOOK, DISTANCE_TO_SELF -> Category.ALGEBRA;
            case SPHERE_REGION, BOX_REGION, REGION_CONTAINS_SELF, SAMPLE_REGION,
                    RIGHT_BASIS_VECTOR, FORWARD_BASIS_VECTOR, OBLIQUE_BASIS_VECTOR -> Category.GEOMETRY;
            case SINE_NUMBER, COSINE_NUMBER -> Category.TRIGONOMETRY;
            case FINITE_DIFFERENCE, SIMPSON_INTEGRAL -> Category.CALCULUS;
            case CROSS_WITH_UP, PROJECT_ONTO_LOOK, REFLECT_ACROSS_UP -> Category.LINEAR_ALGEBRA;
            case QUARTER_TURN_VECTOR -> Category.SYMMETRY;
            case HEAL_SELF, SPEED_SELF, INVISIBILITY_SELF, NIGHT_VISION_SELF,
                    WITHER_HOSTILES, SOUL_BIND_HOSTILES, VITAL_INFUSION_SELF,
                    ALCHEMICAL_MANTLE -> Category.ALCHEMY;
            case PARSIMONY_SELF, CONSERVATION_SELF -> Category.METAMAGIC;
            case RAYCAST, RAY_HIT_POSITION, NEARBY_LIVING, FILTER_NON_PLAYERS,
                    FILTER_TARGETS_REGION, NEAREST_TARGETS, NEARBY_BLOCKS,
                    FILTER_BLOCKS_REGION, BLOCK_POSITIONS, AVERAGE_POSITION -> Category.QUERIES;
            case PUSH_SELF, DEBUG_MARKER, BLINK, PUSH_TARGETS_PLAN, EXECUTE_PLAN -> Category.EFFECTS;
        };
    }

    public RuneType resultType() {
        return switch (this) {
            case SELF -> RuneType.PLAYER;
            case NUMBER_ONE, ADD_ONE, SUBTRACT_ONE, DOUBLE_NUMBER, HALVE_NUMBER,
                    CLAMP_NUMBER, VECTOR_LENGTH, DOT_WITH_LOOK, DISTANCE_TO_SELF,
                    SINE_NUMBER, COSINE_NUMBER, FINITE_DIFFERENCE, SIMPSON_INTEGRAL,
                    ABS_NUMBER, MIN_NUMBER, MAX_NUMBER, POWER_NUMBER, SQRT_NUMBER,
                    LOG_NUMBER, EXP_NUMBER, ATAN2_NUMBER, LERP_NUMBER, SELECT_NUMBER -> RuneType.NUMBER;
            case UP_VECTOR, LOOK_VECTOR, SCALE_VECTOR, VECTOR_ADD_UP, VECTOR_SUBTRACT_UP,
                    NORMALIZE_VECTOR, RAY_HIT_POSITION, AVERAGE_POSITION,
                    RIGHT_BASIS_VECTOR, FORWARD_BASIS_VECTOR, OBLIQUE_BASIS_VECTOR,
                    CROSS_WITH_UP, PROJECT_ONTO_LOOK, REFLECT_ACROSS_UP,
                    QUARTER_TURN_VECTOR -> RuneType.VEC3;
            case SPHERE_REGION, BOX_REGION -> RuneType.REGION;
            case REGION_CONTAINS_SELF, AT_LEAST_NUMBER -> RuneType.BOOL;
            case SAMPLE_REGION, BLOCK_POSITIONS -> RuneType.VEC3_LIST;
            case RAYCAST -> RuneType.RAY_HIT;
            case NEARBY_LIVING, FILTER_NON_PLAYERS, FILTER_TARGETS_REGION, NEAREST_TARGETS -> RuneType.ENTITY_LIST;
            case NEARBY_BLOCKS, FILTER_BLOCKS_REGION -> RuneType.BLOCK_POS_LIST;
            case PUSH_TARGETS_PLAN, HEAL_SELF, SPEED_SELF, INVISIBILITY_SELF,
                    NIGHT_VISION_SELF, WITHER_HOSTILES, SOUL_BIND_HOSTILES,
                    VITAL_INFUSION_SELF, ALCHEMICAL_MANTLE,
                    PARSIMONY_SELF, CONSERVATION_SELF -> RuneType.EFFECT_PLAN;
            case PUSH_SELF, DEBUG_MARKER, BLINK, EXECUTE_PLAN -> RuneType.UNIT;
        };
    }

    public List<CustomInputSlot> inputs() {
        return switch (this) {
            case SELF, NUMBER_ONE, UP_VECTOR, FINITE_DIFFERENCE, SIMPSON_INTEGRAL -> List.of();
            case ADD_ONE, SUBTRACT_ONE, DOUBLE_NUMBER, HALVE_NUMBER, CLAMP_NUMBER,
                    ABS_NUMBER, MIN_NUMBER, MAX_NUMBER, POWER_NUMBER, SQRT_NUMBER,
                    LOG_NUMBER, EXP_NUMBER, ATAN2_NUMBER, LERP_NUMBER, AT_LEAST_NUMBER, SELECT_NUMBER ->
                    List.of(CustomInputSlot.NUMBER);
            case SINE_NUMBER, COSINE_NUMBER -> List.of(CustomInputSlot.NUMBER);
            case LOOK_VECTOR, RAYCAST -> List.of(CustomInputSlot.PLAYER);
            case SCALE_VECTOR -> List.of(CustomInputSlot.VECTOR, CustomInputSlot.NUMBER);
            case VECTOR_ADD_UP, VECTOR_SUBTRACT_UP, NORMALIZE_VECTOR, VECTOR_LENGTH ->
                    List.of(CustomInputSlot.VECTOR);
            case CROSS_WITH_UP, REFLECT_ACROSS_UP, QUARTER_TURN_VECTOR ->
                    List.of(CustomInputSlot.VECTOR);
            case PROJECT_ONTO_LOOK -> List.of(CustomInputSlot.VECTOR, CustomInputSlot.PLAYER);
            case DOT_WITH_LOOK -> List.of(CustomInputSlot.VECTOR, CustomInputSlot.PLAYER);
            case DISTANCE_TO_SELF -> List.of(CustomInputSlot.VECTOR, CustomInputSlot.POSITION);
            case SPHERE_REGION -> List.of(CustomInputSlot.POSITION, CustomInputSlot.NUMBER);
            case BOX_REGION -> List.of(CustomInputSlot.POSITION);
            case REGION_CONTAINS_SELF -> List.of(CustomInputSlot.REGION, CustomInputSlot.POSITION);
            case SAMPLE_REGION -> List.of(CustomInputSlot.REGION);
            case RAY_HIT_POSITION -> List.of(CustomInputSlot.RAY_HIT);
            case NEARBY_LIVING, NEARBY_BLOCKS -> List.of(CustomInputSlot.POSITION);
            case FILTER_NON_PLAYERS -> List.of(CustomInputSlot.ENTITY_LIST);
            case FILTER_TARGETS_REGION ->
                    List.of(CustomInputSlot.ENTITY_LIST, CustomInputSlot.REGION);
            case NEAREST_TARGETS ->
                    List.of(CustomInputSlot.ENTITY_LIST, CustomInputSlot.POSITION);
            case FILTER_BLOCKS_REGION ->
                    List.of(CustomInputSlot.BLOCK_LIST, CustomInputSlot.REGION);
            case BLOCK_POSITIONS -> List.of(CustomInputSlot.BLOCK_LIST);
            case AVERAGE_POSITION -> List.of(CustomInputSlot.POSITION_LIST);
            case PUSH_SELF -> List.of(CustomInputSlot.PLAYER, CustomInputSlot.VECTOR);
            case DEBUG_MARKER -> List.of(CustomInputSlot.POSITION);
            case BLINK -> List.of(CustomInputSlot.PLAYER, CustomInputSlot.RAY_HIT);
            case PUSH_TARGETS_PLAN ->
                    List.of(CustomInputSlot.ENTITY_LIST, CustomInputSlot.VECTOR);
            case EXECUTE_PLAN -> List.of(CustomInputSlot.EFFECT_PLAN);
            case RIGHT_BASIS_VECTOR, FORWARD_BASIS_VECTOR, OBLIQUE_BASIS_VECTOR ->
                    List.of(CustomInputSlot.FRAME);
            case HEAL_SELF, SPEED_SELF, INVISIBILITY_SELF, NIGHT_VISION_SELF,
                    VITAL_INFUSION_SELF, ALCHEMICAL_MANTLE,
                    PARSIMONY_SELF, CONSERVATION_SELF ->
                    List.of(CustomInputSlot.PLAYER);
            case WITHER_HOSTILES -> List.of(CustomInputSlot.POSITION);
            case SOUL_BIND_HOSTILES ->
                    List.of(CustomInputSlot.POSITION, CustomInputSlot.ENTITY_LIST);
        };
    }

    public static Optional<CustomSpellAction> byOrdinal(int ordinal) {
        return Arrays.stream(values())
                .filter(action -> action.ordinal() == ordinal)
                .findFirst();
    }

    public enum Category {
        SOURCES("screen.mathmod.rune_programmer.custom.category.sources"),
        ALGEBRA("screen.mathmod.rune_programmer.custom.category.algebra"),
        GEOMETRY("screen.mathmod.rune_programmer.custom.category.geometry"),
        TRIGONOMETRY("screen.mathmod.rune_programmer.custom.category.trigonometry"),
        CALCULUS("screen.mathmod.rune_programmer.custom.category.calculus"),
        LINEAR_ALGEBRA("screen.mathmod.rune_programmer.custom.category.linear_algebra"),
        SYMMETRY("screen.mathmod.rune_programmer.custom.category.symmetry"),
        ALCHEMY("screen.mathmod.rune_programmer.custom.category.alchemy"),
        METAMAGIC("screen.mathmod.rune_programmer.custom.category.metamagic"),
        QUERIES("screen.mathmod.rune_programmer.custom.category.queries"),
        EFFECTS("screen.mathmod.rune_programmer.custom.category.effects");

        private final String translationKey;

        Category(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return translationKey;
        }
    }
}
