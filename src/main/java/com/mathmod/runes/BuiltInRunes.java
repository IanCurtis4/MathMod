package com.mathmod.runes;

import java.util.List;

import com.mathmod.program.ScalarOperations;

public final class BuiltInRunes {
    private BuiltInRunes() {
    }

    public static void registerAll(RuneRegistry registry) {
        registry.register(RuneDefinition.builder("mathmod:constant_number")
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("constant_number")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_from_numbers")
                .input("x", RuneType.NUMBER)
                .input("y", RuneType.NUMBER)
                .input("z", RuneType.NUMBER)
                .output(RuneType.VEC3)
                .budgetCost(3)
                .executorKey("vector_from_numbers")
                .build());

        registry.register(RuneDefinition.builder("mathmod:self_player")
                .output(RuneType.PLAYER)
                .budgetCost(1)
                .executorKey("self_player")
                .build());

        registry.register(RuneDefinition.builder("mathmod:player_position")
                .input("player", RuneType.PLAYER)
                .output(RuneType.VEC3)
                .budgetCost(1)
                .executorKey("player_position")
                .build());

        registry.register(RuneDefinition.builder("mathmod:look_vector")
                .input("player", RuneType.PLAYER)
                .output(RuneType.VEC3)
                .budgetCost(2)
                .executorKey("look_vector")
                .build());

        registry.register(RuneDefinition.builder("mathmod:player_frame")
                .input("player", RuneType.PLAYER)
                .output(RuneType.FRAME)
                .budgetCost(2)
                .executorKey("player_frame")
                .build());

        registry.register(RuneDefinition.builder("mathmod:transform_local_vector")
                .input("frame", RuneType.FRAME)
                .input("vector", RuneType.VEC3)
                .output(RuneType.VEC3)
                .budgetCost(2)
                .executorKey("transform_local_vector")
                .build());

        registry.register(RuneDefinition.builder("mathmod:scale_vector")
                .input("vector", RuneType.VEC3)
                .input("factor", RuneType.NUMBER)
                .output(RuneType.VEC3)
                .budgetCost(2)
                .executorKey("scale_vector")
                .build());

        registry.register(RuneDefinition.builder("mathmod:number_add")
                .input("a", RuneType.NUMBER)
                .input("b", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("number_add")
                .build());

        registry.register(RuneDefinition.builder("mathmod:number_subtract")
                .input("a", RuneType.NUMBER)
                .input("b", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("number_subtract")
                .build());

        registry.register(RuneDefinition.builder("mathmod:number_multiply")
                .input("a", RuneType.NUMBER)
                .input("b", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("number_multiply")
                .build());

        registry.register(RuneDefinition.builder("mathmod:number_divide")
                .input("a", RuneType.NUMBER)
                .input("b", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("number_divide")
                .build());

        registry.register(RuneDefinition.builder("mathmod:number_clamp")
                .input("value", RuneType.NUMBER)
                .input("min", RuneType.NUMBER)
                .input("max", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("number_clamp")
                .build());

        registry.register(RuneDefinition.builder("mathmod:number_round")
                .input("value", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("number_round")
                .build());

        for (ScalarOperations.Descriptor descriptor : ScalarOperations.descriptors()) {
            RuneDefinition.Builder builder = RuneDefinition.builder(descriptor.runeId());
            for (String input : descriptor.inputNames()) {
                builder.input(
                        input,
                        descriptor.executorKey().equals("number_select") && input.equals("condition")
                                ? RuneType.BOOL
                                : RuneType.NUMBER
                );
            }
            descriptor.attributes().forEach(builder::attribute);
            registry.register(builder
                    .output(descriptor.output())
                    .budgetCost(descriptor.budgetCost())
                    .executorKey(descriptor.executorKey())
                    .build());
        }

        registry.register(RuneDefinition.builder("mathmod:number_sin")
                .input("angle", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(2)
                .attribute("resonance", 1)
                .executorKey("number_sin")
                .build());

        registry.register(RuneDefinition.builder("mathmod:number_cos")
                .input("angle", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(2)
                .attribute("resonance", 1)
                .executorKey("number_cos")
                .build());

        registry.register(RuneDefinition.builder("mathmod:finite_difference")
                .input("start", RuneType.NUMBER)
                .input("end", RuneType.NUMBER)
                .input("step", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(2)
                .attribute("continuity", 1)
                .attribute("precision", 1)
                .executorKey("finite_difference")
                .build());

        registry.register(RuneDefinition.builder("mathmod:simpson_integral")
                .input("start", RuneType.NUMBER)
                .input("midpoint", RuneType.NUMBER)
                .input("end", RuneType.NUMBER)
                .input("width", RuneType.NUMBER)
                .output(RuneType.NUMBER)
                .budgetCost(3)
                .attribute("continuity", 2)
                .attribute("precision", 1)
                .executorKey("simpson_integral")
                .build());

        registry.register(RuneDefinition.builder("mathmod:living_density_field")
                .output(RuneType.SCALAR_FIELD)
                .budgetCost(2)
                .attribute("information", 1)
                .attribute("spatial", 1)
                .executorKey("living_density_field")
                .build());

        registry.register(RuneDefinition.builder("mathmod:environmental_field")
                .output(RuneType.ATTRIBUTE_FIELD)
                .budgetCost(2)
                .attribute("information", 1)
                .attribute("spatial", 1)
                .executorKey("environmental_field")
                .build());

        registry.register(RuneDefinition.builder("mathmod:project_environmental_channel")
                .input("field", RuneType.ATTRIBUTE_FIELD)
                .output(RuneType.SCALAR_FIELD)
                .budgetCost(2)
                .attribute("information", 1)
                .attribute("precision", 1)
                .executorKey("project_environmental_channel")
                .build());

        registry.register(RuneDefinition.builder("mathmod:field_gradient")
                .input("field", RuneType.SCALAR_FIELD)
                .input("point", RuneType.VEC3)
                .input("step", RuneType.NUMBER)
                .output(RuneType.VEC3)
                .budgetCost(5)
                .attribute("information", 2)
                .attribute("spatial", 2)
                .attribute("precision", 1)
                .executorKey("field_gradient")
                .build());

        registry.register(RuneDefinition.builder("mathmod:dimensional_survey")
                .output(RuneType.UNIT)
                .budgetCost(11)
                .attribute("information", 3)
                .attribute("spatial", 2)
                .attribute("precision", 1)
                .executorKey("dimensional_survey")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_add")
                .input("a", RuneType.VEC3)
                .input("b", RuneType.VEC3)
                .output(RuneType.VEC3)
                .budgetCost(1)
                .executorKey("vector_add")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_subtract")
                .input("a", RuneType.VEC3)
                .input("b", RuneType.VEC3)
                .output(RuneType.VEC3)
                .budgetCost(1)
                .executorKey("vector_subtract")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_normalize")
                .input("vector", RuneType.VEC3)
                .output(RuneType.VEC3)
                .budgetCost(1)
                .executorKey("vector_normalize")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_length")
                .input("vector", RuneType.VEC3)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("vector_length")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_dot")
                .input("a", RuneType.VEC3)
                .input("b", RuneType.VEC3)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("vector_dot")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_cross")
                .input("a", RuneType.VEC3)
                .input("b", RuneType.VEC3)
                .output(RuneType.VEC3)
                .budgetCost(2)
                .attribute("orientation", 1)
                .executorKey("vector_cross")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_project")
                .input("vector", RuneType.VEC3)
                .input("onto", RuneType.VEC3)
                .output(RuneType.VEC3)
                .budgetCost(2)
                .attribute("orientation", 1)
                .executorKey("vector_project")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_reflect")
                .input("vector", RuneType.VEC3)
                .input("normal", RuneType.VEC3)
                .output(RuneType.VEC3)
                .budgetCost(2)
                .attribute("orientation", 1)
                .executorKey("vector_reflect")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_distance")
                .input("a", RuneType.VEC3)
                .input("b", RuneType.VEC3)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("vector_distance")
                .build());

        registry.register(RuneDefinition.builder("mathmod:cyclic_element")
                .input("order", RuneType.NUMBER)
                .input("value", RuneType.NUMBER)
                .output(RuneType.CYCLIC_ELEMENT)
                .budgetCost(2)
                .attribute("symmetry", 1)
                .executorKey("cyclic_element")
                .build());

        registry.register(RuneDefinition.builder("mathmod:cyclic_compose")
                .input("a", RuneType.CYCLIC_ELEMENT)
                .input("b", RuneType.CYCLIC_ELEMENT)
                .output(RuneType.CYCLIC_ELEMENT)
                .budgetCost(1)
                .attribute("symmetry", 1)
                .executorKey("cyclic_compose")
                .build());

        registry.register(RuneDefinition.builder("mathmod:cyclic_inverse")
                .input("element", RuneType.CYCLIC_ELEMENT)
                .output(RuneType.CYCLIC_ELEMENT)
                .budgetCost(1)
                .attribute("symmetry", 1)
                .executorKey("cyclic_inverse")
                .build());

        registry.register(RuneDefinition.builder("mathmod:cyclic_rotate_y")
                .input("element", RuneType.CYCLIC_ELEMENT)
                .input("vector", RuneType.VEC3)
                .output(RuneType.VEC3)
                .budgetCost(2)
                .attribute("symmetry", 1)
                .attribute("orientation", 1)
                .executorKey("cyclic_rotate_y")
                .build());

        registry.register(RuneDefinition.builder("mathmod:sphere_region")
                .input("center", RuneType.VEC3)
                .input("radius", RuneType.NUMBER)
                .output(RuneType.REGION)
                .budgetCost(2)
                .executorKey("sphere_region")
                .build());

        registry.register(RuneDefinition.builder("mathmod:box_region")
                .input("min", RuneType.VEC3)
                .input("max", RuneType.VEC3)
                .output(RuneType.REGION)
                .budgetCost(2)
                .executorKey("box_region")
                .build());

        registry.register(RuneDefinition.builder("mathmod:region_union")
                .input("first", RuneType.REGION)
                .input("second", RuneType.REGION)
                .output(RuneType.REGION)
                .budgetCost(1)
                .executorKey("region_union")
                .build());

        registry.register(RuneDefinition.builder("mathmod:region_intersection")
                .input("first", RuneType.REGION)
                .input("second", RuneType.REGION)
                .output(RuneType.REGION)
                .budgetCost(1)
                .executorKey("region_intersection")
                .build());

        registry.register(RuneDefinition.builder("mathmod:region_difference")
                .input("first", RuneType.REGION)
                .input("second", RuneType.REGION)
                .output(RuneType.REGION)
                .budgetCost(1)
                .executorKey("region_difference")
                .build());

        registry.register(RuneDefinition.builder("mathmod:solid_of_revolution")
                .input("origin", RuneType.VEC3)
                .input("axis", RuneType.VEC3)
                .output(RuneType.REGION)
                .budgetCost(5)
                .attribute("spatial", 2)
                .attribute("precision", 1)
                .executorKey("solid_of_revolution")
                .build());

        registry.register(RuneDefinition.builder("mathmod:fill_region")
                .input("region", RuneType.REGION)
                .output(RuneType.UNIT)
                .budgetCost(6)
                .attribute("spatial", 3)
                .attribute("stability", 2)
                .executorKey("fill_region")
                .build());

        registry.register(RuneDefinition.builder("mathmod:materialize_construct")
                .input("region", RuneType.REGION).output(RuneType.CONSTRUCT_BODY).budgetCost(5)
                .attribute("spatial", 2).executorKey("materialize_construct").build());
        registry.register(RuneDefinition.builder("mathmod:compress_construct")
                .input("body", RuneType.CONSTRUCT_BODY).input("scale", RuneType.NUMBER)
                .output(RuneType.CONSTRUCT_BODY).budgetCost(3).attribute("precision", 2)
                .executorKey("compress_construct").build());
        registry.register(RuneDefinition.builder("mathmod:spin_construct")
                .input("body", RuneType.CONSTRUCT_BODY).input("axis", RuneType.VEC3).input("speed", RuneType.NUMBER)
                .output(RuneType.CONSTRUCT_BODY).budgetCost(3).attribute("orientation", 2)
                .executorKey("spin_construct").build());
        registry.register(RuneDefinition.builder("mathmod:launch_construct")
                .input("body", RuneType.CONSTRUCT_BODY).input("origin", RuneType.VEC3).input("velocity", RuneType.VEC3)
                .output(RuneType.UNIT).budgetCost(7).attribute("motion", 2).attribute("spatial", 2)
                .executorKey("launch_construct").build());

        registry.register(RuneDefinition.builder("mathmod:region_contains")
                .input("region", RuneType.REGION)
                .input("position", RuneType.VEC3)
                .output(RuneType.BOOL)
                .budgetCost(1)
                .executorKey("region_contains")
                .build());

        registry.register(RuneDefinition.builder("mathmod:sample_region")
                .input("region", RuneType.REGION)
                .output(RuneType.VEC3_LIST)
                .budgetCost(4)
                .attribute("precision", 1)
                .executorKey("sample_region")
                .build());

        registry.register(RuneDefinition.builder("mathmod:raycast_block")
                .input("player", RuneType.PLAYER)
                .input("range", RuneType.NUMBER)
                .output(RuneType.RAY_HIT)
                .budgetCost(4)
                .attribute("precision", 1)
                .executorKey("raycast_block")
                .build());

        registry.register(RuneDefinition.builder("mathmod:ray_hit_position")
                .input("hit", RuneType.RAY_HIT)
                .output(RuneType.VEC3)
                .budgetCost(1)
                .executorKey("ray_hit_position")
                .build());

        registry.register(RuneDefinition.builder("mathmod:blink_self_to_hit")
                .input("player", RuneType.PLAYER)
                .input("hit", RuneType.RAY_HIT)
                .output(RuneType.UNIT)
                .budgetCost(7)
                .material("minecraft:ender_pearl", 1)
                .attribute("spatial", 2)
                .executorKey("blink_self_to_hit")
                .build());

        registry.register(RuneDefinition.builder("mathmod:push_self")
                .input("player", RuneType.PLAYER)
                .input("vector", RuneType.VEC3)
                .output(RuneType.UNIT)
                .budgetCost(5)
                .attribute("motion", 1)
                .executorKey("push_self")
                .build());

        registry.register(RuneDefinition.builder("mathmod:anchor_origin")
                .output(RuneType.VEC3)
                .budgetCost(1)
                .executorKey("anchor_origin")
                .build());

        registry.register(RuneDefinition.builder("mathmod:consume_nearby_item")
                .input("position", RuneType.VEC3)
                .output(RuneType.VEC3)
                .budgetCost(4)
                .executorKey("consume_nearby_item")
                .build());

        registry.register(RuneDefinition.builder("mathmod:spawn_item")
                .input("position", RuneType.VEC3)
                .output(RuneType.UNIT)
                .budgetCost(5)
                .attribute("arcane", 1)
                .executorKey("spawn_item")
                .build());

        registry.register(RuneDefinition.builder("mathmod:pulse_nearby_entities")
                .input("position", RuneType.VEC3)
                .output(RuneType.UNIT)
                .budgetCost(6)
                .attribute("force", 1)
                .executorKey("pulse_nearby_entities")
                .build());

        registry.register(RuneDefinition.builder("mathmod:nearby_entities")
                .input("center", RuneType.VEC3)
                .output(RuneType.ENTITY_LIST)
                .budgetCost(5)
                .attribute("information", 1)
                .executorKey("nearby_entities")
                .build());

        registry.register(RuneDefinition.builder("mathmod:sense_nearby_entities")
                .input("center", RuneType.VEC3)
                .output(RuneType.ENTITY_LIST)
                .budgetCost(5)
                .attribute("information", 1)
                .executorKey("sense_nearby_entities")
                .build());

        registry.register(RuneDefinition.builder("mathmod:filter_entities")
                .input("entities", RuneType.ENTITY_LIST)
                .output(RuneType.ENTITY_LIST)
                .budgetCost(1)
                .executorKey("filter_entities")
                .build());

        registry.register(RuneDefinition.builder("mathmod:filter_entities_in_region")
                .input("entities", RuneType.ENTITY_LIST)
                .input("region", RuneType.REGION)
                .output(RuneType.ENTITY_LIST)
                .budgetCost(2)
                .executorKey("filter_entities_in_region")
                .build());

        registry.register(RuneDefinition.builder("mathmod:nearest_entities")
                .input("entities", RuneType.ENTITY_LIST)
                .input("origin", RuneType.VEC3)
                .output(RuneType.ENTITY_LIST)
                .budgetCost(2)
                .executorKey("nearest_entities")
                .build());

        registry.register(RuneDefinition.builder("mathmod:farthest_entities")
                .input("entities", RuneType.ENTITY_LIST)
                .input("origin", RuneType.VEC3)
                .output(RuneType.ENTITY_LIST)
                .budgetCost(2)
                .executorKey("farthest_entities")
                .build());

        registry.register(RuneDefinition.builder("mathmod:entity_positions")
                .input("entities", RuneType.ENTITY_LIST)
                .output(RuneType.VEC3_LIST)
                .budgetCost(1)
                .executorKey("entity_positions")
                .build());

        registry.register(RuneDefinition.builder("mathmod:entity_velocities")
                .input("entities", RuneType.ENTITY_LIST)
                .output(RuneType.VEC3_LIST)
                .budgetCost(2)
                .attribute("information", 1)
                .executorKey("entity_velocities")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vector_lengths")
                .input("vectors", RuneType.VEC3_LIST)
                .output(RuneType.NUMBER_LIST)
                .budgetCost(2)
                .executorKey("vector_lengths")
                .build());

        registry.register(RuneDefinition.builder("mathmod:sum_numbers")
                .input("values", RuneType.NUMBER_LIST)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("sum_numbers")
                .build());

        registry.register(RuneDefinition.builder("mathmod:mean_number")
                .input("values", RuneType.NUMBER_LIST)
                .output(RuneType.NUMBER)
                .budgetCost(2)
                .attribute("precision", 1)
                .executorKey("mean_number")
                .build());

        registry.register(RuneDefinition.builder("mathmod:max_number")
                .input("values", RuneType.NUMBER_LIST)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("max_number")
                .build());

        registry.register(RuneDefinition.builder("mathmod:count_entities")
                .input("entities", RuneType.ENTITY_LIST)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("count_entities")
                .build());

        registry.register(RuneDefinition.builder("mathmod:nearby_blocks")
                .input("center", RuneType.VEC3)
                .output(RuneType.BLOCK_POS_LIST)
                .budgetCost(5)
                .attribute("information", 1)
                .executorKey("nearby_blocks")
                .build());

        registry.register(RuneDefinition.builder("mathmod:block_positions")
                .input("blocks", RuneType.BLOCK_POS_LIST)
                .output(RuneType.VEC3_LIST)
                .budgetCost(1)
                .executorKey("block_positions")
                .build());

        registry.register(RuneDefinition.builder("mathmod:filter_blocks_in_region")
                .input("blocks", RuneType.BLOCK_POS_LIST)
                .input("region", RuneType.REGION)
                .output(RuneType.BLOCK_POS_LIST)
                .budgetCost(2)
                .executorKey("filter_blocks_in_region")
                .build());

        registry.register(RuneDefinition.builder("mathmod:count_blocks")
                .input("blocks", RuneType.BLOCK_POS_LIST)
                .output(RuneType.NUMBER)
                .budgetCost(1)
                .executorKey("count_blocks")
                .build());

        registry.register(RuneDefinition.builder("mathmod:average_position")
                .input("positions", RuneType.VEC3_LIST)
                .output(RuneType.VEC3)
                .budgetCost(2)
                .executorKey("average_position")
                .build());

        registry.register(RuneDefinition.builder("mathmod:emit_anchor_redstone")
                .input("power", RuneType.NUMBER)
                .input("duration", RuneType.NUMBER)
                .output(RuneType.UNIT)
                .budgetCost(3)
                .attribute("continuity", 1)
                .executorKey("emit_anchor_redstone")
                .build());

        registry.register(RuneDefinition.builder("mathmod:push_entities_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("vector", RuneType.VEC3)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(5)
                .material("minecraft:amethyst_shard", 1)
                .attribute("force", 2)
                .executorKey("push_entities_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:player_as_entity_list")
                .input("player", RuneType.PLAYER)
                .output(RuneType.ENTITY_LIST)
                .budgetCost(1)
                .executorKey("player_as_entity_list")
                .build());

        registry.register(RuneDefinition.builder("mathmod:heal_entities_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("amount", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(6)
                .attribute("restoration", 2)
                .executorKey("heal_entities_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:cleanse_entities_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(6)
                .material("mathmod:vital_salt", 1)
                .attribute("restoration", 2)
                .executorKey("cleanse_entities_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:resistance_entities_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("duration", RuneType.NUMBER)
                .input("level", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(9)
                .material("mathmod:vital_salt", 1)
                .material("mathmod:homuncular_matrix", 1)
                .attribute("vitality", 3)
                .attribute("stability", 2)
                .executorKey("resistance_entities_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:absorption_entities_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("duration", RuneType.NUMBER)
                .input("level", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(9)
                .material("mathmod:vital_salt", 1)
                .material("mathmod:homuncular_matrix", 1)
                .attribute("vitality", 4)
                .attribute("stability", 2)
                .executorKey("absorption_entities_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:speed_entities_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("duration", RuneType.NUMBER)
                .input("level", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(5)
                .attribute("haste", 2)
                .executorKey("speed_entities_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:invisibility_entities_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("duration", RuneType.NUMBER)
                .input("level", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(7)
                .attribute("concealment", 2)
                .executorKey("invisibility_entities_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:night_vision_entities_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("duration", RuneType.NUMBER)
                .input("level", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(5)
                .attribute("sight", 2)
                .executorKey("night_vision_entities_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:wither_entities_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("duration", RuneType.NUMBER)
                .input("level", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(8)
                .attribute("decay", 2)
                .executorKey("wither_entities_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:soul_bind_entities_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("anchor", RuneType.VEC3)
                .input("duration", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(9)
                .attribute("binding", 2)
                .attribute("soul", 1)
                .executorKey("soul_bind_entities_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:vital_infusion_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("duration", RuneType.NUMBER)
                .input("level", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(9)
                .attribute("infusion", 2)
                .attribute("vitality", 2)
                .executorKey("vital_infusion_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:parsimony_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("duration", RuneType.NUMBER)
                .input("level", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(10)
                .attribute("metamagic", 4)
                .attribute("economy", 4)
                .executorKey("parsimony_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:conservation_plan")
                .input("entities", RuneType.ENTITY_LIST)
                .input("duration", RuneType.NUMBER)
                .input("level", RuneType.NUMBER)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(10)
                .attribute("metamagic", 4)
                .attribute("conservation", 4)
                .executorKey("conservation_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:combine_effect_plans")
                .input("first", RuneType.EFFECT_PLAN)
                .input("second", RuneType.EFFECT_PLAN)
                .output(RuneType.EFFECT_PLAN)
                .budgetCost(2)
                .attribute("stability", 1)
                .executorKey("combine_effect_plans")
                .build());

        registry.register(RuneDefinition.builder("mathmod:execute_effect_plan")
                .input("plan", RuneType.EFFECT_PLAN)
                .output(RuneType.UNIT)
                .budgetCost(1)
                .executorKey("execute_effect_plan")
                .build());

        registry.register(RuneDefinition.builder("mathmod:debug_marker")
                .input("position", RuneType.VEC3)
                .output(RuneType.UNIT)
                .budgetCost(2)
                .executorKey("debug_marker")
                .build());

        setTier(registry, RuneTier.REFINED, List.of(
                "number_sin", "number_cos", "finite_difference", "simpson_integral",
                "living_density_field", "environmental_field", "project_environmental_channel", "field_gradient", "dimensional_survey",
                "vector_cross", "vector_project", "vector_reflect",
                "cyclic_element", "cyclic_compose", "cyclic_inverse", "cyclic_rotate_y",
                "sample_region", "solid_of_revolution", "fill_region", "materialize_construct",
                "compress_construct", "spin_construct", "launch_construct", "raycast_block", "blink_self_to_hit",
                "nearby_entities", "nearby_blocks", "push_entities_plan",
                "heal_entities_plan", "cleanse_entities_plan", "speed_entities_plan", "invisibility_entities_plan",
                "night_vision_entities_plan", "wither_entities_plan"
        ));
        setTier(registry, RuneTier.ARCANE, List.of(
                "soul_bind_entities_plan", "vital_infusion_plan", "resistance_entities_plan",
                "absorption_entities_plan", "combine_effect_plans"
        ));
        setTier(registry, RuneTier.METAMAGICAL, List.of(
                "parsimony_plan", "conservation_plan"
        ));
    }

    private static void setTier(RuneRegistry registry, RuneTier tier, List<String> paths) {
        for (String path : paths) {
            registry.update("mathmod:" + path, definition -> definition.withTier(tier));
        }
    }
}
