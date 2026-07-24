package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.ProgramValidator;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.runes.RuneType;
import com.mathmod.runes.ValidationIssue;
import com.mathmod.runes.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ProgramExecutionPolicy {
    public static final double MAX_PUSH_LENGTH = 1.5D;
    public static final double MAX_RAYCAST_RANGE = 32.0D;
    public static final double MAX_BLINK_RANGE = 16.0D;
    public static final double MAX_SACRIFICE_RADIUS = 6.0D;
    public static final int MAX_SPAWNED_ITEM_COUNT = 16;
    public static final double MAX_ENTITY_PULSE_RADIUS = 6.0D;
    public static final double MAX_ENTITY_PULSE_STRENGTH = 1.25D;
    public static final double MAX_TARGET_QUERY_RADIUS = 8.0D;
    public static final int MAX_TARGET_LIST_SIZE = 8;
    public static final double MAX_ENTITY_LIST_PUSH_LENGTH = 1.0D;
    public static final double MAX_BLOCK_QUERY_RADIUS = 5.0D;
    public static final int MAX_BLOCK_LIST_SIZE = 64;
    public static final double MAX_REGION_RADIUS = 8.0D;
    public static final double MAX_REGION_BOX_EXTENT = 16.0D;
    public static final int MAX_REGION_SAMPLE_POINTS = 96;
    public static final double MIN_REGION_SAMPLE_STEP = 0.5D;
    public static final double MAX_REGION_SAMPLE_STEP = 4.0D;
    public static final double MAX_HEAL_AMOUNT = 8.0D;
    public static final int MAX_BENEFICIAL_EFFECT_DURATION_TICKS = 20 * 60;
    public static final int MAX_HARMFUL_EFFECT_DURATION_TICKS = 20 * 15;
    public static final int MAX_METAMAGIC_EFFECT_DURATION_TICKS = 20 * 180;
    public static final int MAX_EFFECT_AMPLIFIER = 2;
    public static final int MAX_DEFENSIVE_EFFECT_DURATION_TICKS = 20 * 30;
    public static final int MAX_DEFENSIVE_EFFECT_AMPLIFIER = 1;
    public static final int MAX_ANCHOR_SIGNAL_DURATION_TICKS = 20 * 30;

    private static final Set<String> SUPPORTED_EXECUTOR_KEYS = Set.of(
            "constant_number",
            "vector_from_numbers",
            "self_player",
            "player_position",
            "look_vector",
            "player_frame",
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
            "living_density_field",
            "environmental_field",
            "project_environmental_channel",
            "field_gradient",
            "dimensional_survey",
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
            "region_union",
            "region_intersection",
            "region_difference",
            "solid_of_revolution",
            "fill_region",
            "materialize_construct",
            "compress_construct",
            "spin_construct",
            "launch_construct",
            "region_contains",
            "sample_region",
            "raycast_block",
            "ray_hit_position",
            "blink_self_to_hit",
            "anchor_origin",
            "consume_nearby_item",
            "spawn_item",
            "pulse_nearby_entities",
            "nearby_entities",
            "sense_nearby_entities",
            "filter_entities",
            "filter_entities_in_region",
            "nearest_entities",
            "farthest_entities",
            "entity_positions",
            "entity_velocities",
            "vector_lengths",
            "sum_numbers",
            "mean_number",
            "max_number",
            "count_entities",
            "nearby_blocks",
            "block_positions",
            "filter_blocks_in_region",
            "count_blocks",
            "average_position",
            "emit_anchor_redstone",
            "push_entities_plan",
            "player_as_entity_list",
            "heal_entities_plan",
            "cleanse_entities_plan",
            "resistance_entities_plan",
            "absorption_entities_plan",
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
            "push_self",
            "debug_marker"
    );

    private ProgramExecutionPolicy() {
    }

    public static boolean supportsExecutorKey(String executorKey) {
        return SUPPORTED_EXECUTOR_KEYS.contains(executorKey);
    }

    public static ValidationResult validateExecutable(ProgramGraph graph, RuneRegistry runeRegistry) {
        return validateExecutable(graph, runeRegistry, 0);
    }

    public static ValidationResult validateExecutable(ProgramGraph graph, RuneRegistry runeRegistry, int budgetBonus) {
        int effectiveBudgetLimit = Math.min(
                ProgramValidator.MAX_BUDGET_LIMIT,
                graph.budgetLimit() + Math.max(0, budgetBonus)
        );
        ProgramGraph effectiveGraph = new ProgramGraph(
                graph.nodes(),
                graph.edges(),
                graph.outputNodeId(),
                effectiveBudgetLimit
        );
        ValidationResult base = new ProgramValidator(runeRegistry).validate(effectiveGraph);
        List<ValidationIssue> issues = new ArrayList<>(base.issues());
        issues.addAll(ScalarDomainValidator.closedDomainIssues(effectiveGraph, runeRegistry));
        issues.addAll(P9EffectPolicy.structuralIssues(effectiveGraph, runeRegistry));

        if (base.outputType() != null && base.outputType() != RuneType.UNIT) {
            issues.add(ValidationIssue.localizedError(
                    graph.outputNodeId(),
                    "Executable programs must output unit, but output node returns " + base.outputType().id() + ".",
                    "validation.mathmod.output_not_unit",
                    RuneType.UNIT.id(),
                    base.outputType().id()
            ));
        }

        for (ProgramNode node : graph.nodes()) {
            runeRegistry.find(node.runeId())
                    .map(RuneDefinition::executorKey)
                    .filter(executorKey -> !supportsExecutorKey(executorKey))
                    .ifPresent(executorKey -> issues.add(ValidationIssue.localizedError(
                            node.id(),
                            "Rune '" + node.runeId() + "' uses unsupported executor key '" + executorKey + "'.",
                            "validation.mathmod.unsupported_executor",
                            node.runeId(),
                            executorKey
                    )));
        }

        return new ValidationResult(issues, base.budgetUsed(), base.outputType());
    }
}
