package com.mathmod.authoring;

import com.mathmod.program.CustomNumericParameter;
import com.mathmod.program.CustomSpellAction;
import com.mathmod.util.NamespacedId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Frozen built-in compatibility input. It is intentionally keyed by enum name, never ordinal. */
public final class BuiltInAuthoringMetadata {
    private record Entry(String enumName, String formId, String categoryId, int sortOrder) {}
    private static final List<Entry> TABLE = List.of(
            e("SELF","mathmod:self","mathmod:sources",0), e("NUMBER_ONE","mathmod:number_one","mathmod:sources",1), e("ADD_ONE","mathmod:add_one","mathmod:algebra",2), e("SUBTRACT_ONE","mathmod:subtract_one","mathmod:algebra",3), e("DOUBLE_NUMBER","mathmod:double_number","mathmod:algebra",4), e("HALVE_NUMBER","mathmod:halve_number","mathmod:algebra",5), e("CLAMP_NUMBER","mathmod:clamp_number","mathmod:algebra",6), e("UP_VECTOR","mathmod:up_vector","mathmod:sources",7), e("LOOK_VECTOR","mathmod:look_vector","mathmod:sources",8), e("SCALE_VECTOR","mathmod:scale_vector","mathmod:algebra",9), e("VECTOR_ADD_UP","mathmod:vector_add_up","mathmod:algebra",10), e("VECTOR_SUBTRACT_UP","mathmod:vector_subtract_up","mathmod:algebra",11), e("NORMALIZE_VECTOR","mathmod:normalize_vector","mathmod:algebra",12), e("VECTOR_LENGTH","mathmod:vector_length","mathmod:algebra",13), e("DOT_WITH_LOOK","mathmod:dot_with_look","mathmod:algebra",14), e("DISTANCE_TO_SELF","mathmod:distance_to_self","mathmod:algebra",15), e("SPHERE_REGION","mathmod:sphere_region","mathmod:geometry",16), e("BOX_REGION","mathmod:box_region","mathmod:geometry",17), e("REGION_CONTAINS_SELF","mathmod:region_contains_self","mathmod:geometry",18), e("SAMPLE_REGION","mathmod:sample_region","mathmod:geometry",19), e("RAYCAST","mathmod:raycast","mathmod:queries",20), e("RAY_HIT_POSITION","mathmod:ray_hit_position","mathmod:queries",21), e("NEARBY_LIVING","mathmod:nearby_living","mathmod:queries",22), e("FILTER_NON_PLAYERS","mathmod:filter_non_players","mathmod:queries",23), e("FILTER_TARGETS_REGION","mathmod:filter_targets_region","mathmod:queries",24), e("NEAREST_TARGETS","mathmod:nearest_targets","mathmod:queries",25), e("NEARBY_BLOCKS","mathmod:nearby_blocks","mathmod:queries",26), e("FILTER_BLOCKS_REGION","mathmod:filter_blocks_region","mathmod:queries",27), e("BLOCK_POSITIONS","mathmod:block_positions","mathmod:queries",28), e("AVERAGE_POSITION","mathmod:average_position","mathmod:queries",29), e("PUSH_SELF","mathmod:push_self","mathmod:effects",30), e("DEBUG_MARKER","mathmod:debug_marker","mathmod:effects",31), e("BLINK","mathmod:blink","mathmod:effects",32), e("PUSH_TARGETS_PLAN","mathmod:push_targets_plan","mathmod:effects",33), e("EXECUTE_PLAN","mathmod:execute_plan","mathmod:effects",34), e("RIGHT_BASIS_VECTOR","mathmod:right_basis_vector","mathmod:geometry",35), e("FORWARD_BASIS_VECTOR","mathmod:forward_basis_vector","mathmod:geometry",36), e("OBLIQUE_BASIS_VECTOR","mathmod:oblique_basis_vector","mathmod:geometry",37), e("SINE_NUMBER","mathmod:sine_number","mathmod:trigonometry",38), e("COSINE_NUMBER","mathmod:cosine_number","mathmod:trigonometry",39), e("CROSS_WITH_UP","mathmod:cross_with_up","mathmod:linear_algebra",40), e("PROJECT_ONTO_LOOK","mathmod:project_onto_look","mathmod:linear_algebra",41), e("REFLECT_ACROSS_UP","mathmod:reflect_across_up","mathmod:linear_algebra",42), e("QUARTER_TURN_VECTOR","mathmod:quarter_turn_vector","mathmod:symmetry",43), e("HEAL_SELF","mathmod:heal_self","mathmod:alchemy",44), e("SPEED_SELF","mathmod:speed_self","mathmod:alchemy",45), e("INVISIBILITY_SELF","mathmod:invisibility_self","mathmod:alchemy",46), e("NIGHT_VISION_SELF","mathmod:night_vision_self","mathmod:alchemy",47), e("WITHER_HOSTILES","mathmod:wither_hostiles","mathmod:alchemy",48), e("SOUL_BIND_HOSTILES","mathmod:soul_bind_hostiles","mathmod:alchemy",49), e("VITAL_INFUSION_SELF","mathmod:vital_infusion_self","mathmod:alchemy",50), e("ALCHEMICAL_MANTLE","mathmod:alchemical_mantle","mathmod:alchemy",51), e("PARSIMONY_SELF","mathmod:parsimony_self","mathmod:metamagic",52), e("CONSERVATION_SELF","mathmod:conservation_self","mathmod:metamagic",53), e("FINITE_DIFFERENCE","mathmod:finite_difference","mathmod:calculus",54), e("SIMPSON_INTEGRAL","mathmod:simpson_integral","mathmod:calculus",55), e("ABS_NUMBER","mathmod:abs_number","mathmod:algebra",56), e("MIN_NUMBER","mathmod:min_number","mathmod:algebra",57), e("MAX_NUMBER","mathmod:max_number","mathmod:algebra",58), e("POWER_NUMBER","mathmod:power_number","mathmod:algebra",59), e("SQRT_NUMBER","mathmod:sqrt_number","mathmod:algebra",60), e("LOG_NUMBER","mathmod:log_number","mathmod:algebra",61), e("EXP_NUMBER","mathmod:exp_number","mathmod:algebra",62), e("ATAN2_NUMBER","mathmod:atan2_number","mathmod:algebra",63), e("LERP_NUMBER","mathmod:lerp_number","mathmod:algebra",64), e("AT_LEAST_NUMBER","mathmod:at_least_number","mathmod:algebra",65), e("SELECT_NUMBER","mathmod:select_number","mathmod:algebra",66));
    private BuiltInAuthoringMetadata() {}
    public static AuthoringMetadata.Snapshot snapshot() {
        if (TABLE.size() != 67 || TABLE.stream().map(Entry::enumName).distinct().count() != TABLE.size() || TABLE.stream().map(Entry::formId).distinct().count() != TABLE.size()) throw new IllegalStateException("invalid built-in compatibility table");
        if (CustomSpellAction.values().length != TABLE.size()) throw new IllegalStateException("legacy enum/table count mismatch");
        List<AuthoringMetadata.Form> forms = new ArrayList<>();
        for (Entry entry : TABLE) {
            CustomSpellAction action = Arrays.stream(CustomSpellAction.values()).filter(value -> value.name().equals(entry.enumName())).findFirst().orElseThrow(() -> new IllegalStateException("missing legacy action " + entry.enumName()));
            if (!action.persistentId().equals(entry.formId()) || !categoryId(action).equals(entry.categoryId())) throw new IllegalStateException("legacy compatibility drift for " + entry.enumName());
            forms.add(new AuthoringMetadata.Form(NamespacedId.parse(entry.formId()), action.translationKey(), NamespacedId.parse(entry.categoryId()), new AuthoringMetadata.RuneIcon(NamespacedId.parse(action.iconRuneId())), new AuthoringMetadata.Symbol(action.compactNotation()), parameters(action.numericParameters()), action.inputs().stream().map(Enum::name).toList(), List.of(), java.util.Optional.of(action.resultType().name()), entry.sortOrder(), new AuthoringMetadata.LegacyAdapter(NamespacedId.parse("mathmod:legacy/" + entry.formId().substring("mathmod:".length())))));
        }
        return AuthoringMetadata.snapshot(0L, forms, categories());
    }
    public static List<String> frozenFormIds() { return TABLE.stream().map(Entry::formId).toList(); }
    /** Explicit identity map; enum declaration order is intentionally not part of this value. */
    public static Map<String, String> frozenEnumNameToFormId() {
        Map<String, String> result = new LinkedHashMap<>();
        TABLE.forEach(entry -> result.put(entry.enumName(), entry.formId()));
        return Map.copyOf(result);
    }
    private static AuthoringMetadata.Parameter parameter(CustomNumericParameter parameter) { return new AuthoringMetadata.Parameter(parameter.key(), AuthoringMetadata.NUMBER_TYPE, parameter.translationKey(), parameter.defaultValue(), new AuthoringMetadata.NumberConstraints(parameter.minValue(), parameter.maxValue()), java.util.Optional.empty()); }
    private static List<AuthoringMetadata.Parameter> parameters(List<CustomNumericParameter> parameters) { return parameters.stream().map(BuiltInAuthoringMetadata::parameter).toList(); }
    private static String categoryId(CustomSpellAction action) { return "mathmod:" + action.category().name().toLowerCase(java.util.Locale.ROOT); }
    private static List<AuthoringMetadata.Category> categories() { return Arrays.stream(CustomSpellAction.Category.values()).map(category -> new AuthoringMetadata.Category(NamespacedId.parse("mathmod:" + category.name().toLowerCase(java.util.Locale.ROOT)), category.translationKey(), category.ordinal())).toList(); }
    private static Entry e(String enumName, String formId, String categoryId, int sortOrder) { return new Entry(enumName, formId, categoryId, sortOrder); }
}
