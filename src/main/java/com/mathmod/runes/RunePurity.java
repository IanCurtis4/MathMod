package com.mathmod.runes;

import java.util.Set;

public enum RunePurity {
    PURE,
    OBSERVATION,
    EFFECT;

    private static final Set<String> PURE_KEYS = Set.of(
            "constant_number",
            "vector_from_numbers",
            "transform_local_vector",
            "scale_vector",
            "number_add",
            "number_subtract",
            "number_multiply",
            "number_divide",
            "number_clamp",
            "number_round",
            "number_abs",
            "number_min",
            "number_max",
            "number_power",
            "number_sqrt",
            "number_log",
            "number_exp",
            "number_atan2",
            "number_lerp",
            "number_at_least",
            "number_select",
            "number_sin",
            "number_cos",
            "finite_difference",
            "simpson_integral",
            "vector_add",
            "vector_subtract",
            "vector_normalize",
            "vector_length",
            "vector_dot",
            "vector_cross",
            "vector_project",
            "vector_reflect",
            "vector_distance",
            "cyclic_element",
            "cyclic_compose",
            "cyclic_inverse",
            "cyclic_rotate_y",
            "sphere_region",
            "box_region",
            "region_contains",
            "sample_region",
            "ray_hit_position",
            "vector_lengths",
            "sum_numbers",
            "mean_number",
            "max_number",
            "count_entities",
            "block_positions",
            "filter_blocks_in_region",
            "count_blocks",
            "average_position",
            "player_as_entity_list"
    );

    private static final Set<String> OBSERVATION_KEYS = Set.of(
            "self_player",
            "player_position",
            "look_vector",
            "player_frame",
            "raycast_block",
            "anchor_origin",
            "sense_nearby_entities",
            "nearby_entities",
            "filter_entities",
            "filter_entities_in_region",
            "nearest_entities",
            "farthest_entities",
            "nearby_blocks",
            "entity_positions",
            "entity_velocities",
            "living_density_field",
            "field_gradient"
    );

    private static final Set<String> EFFECT_KEYS = Set.of(
            "blink_self_to_hit",
            "push_self",
            "consume_nearby_item",
            "spawn_item",
            "pulse_nearby_entities",
            "emit_anchor_redstone",
            "push_entities_plan",
            "heal_entities_plan",
            "speed_entities_plan",
            "invisibility_entities_plan",
            "night_vision_entities_plan",
            "wither_entities_plan",
            "soul_bind_entities_plan",
            "vital_infusion_plan",
            "parsimony_plan",
            "conservation_plan",
            "combine_effect_plans",
            "execute_effect_plan",
            "debug_marker"
    );

    public static RunePurity infer(String executorKey) {
        if (executorKey == null || executorKey.isBlank()) {
            return EFFECT;
        }
        if (EFFECT_KEYS.contains(executorKey)) {
            return EFFECT;
        }
        if (OBSERVATION_KEYS.contains(executorKey)) {
            return OBSERVATION;
        }
        return PURE_KEYS.contains(executorKey) ? PURE : EFFECT;
    }
}
