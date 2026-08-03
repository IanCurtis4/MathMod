package com.mathmod.program;

import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CustomSpellWorkspace {
    public static final int CUSTOM_BUDGET = 32;

    private final List<ProgramNode> nodes = new ArrayList<>();
    private final List<ProgramEdge> edges = new ArrayList<>();
    private final List<CustomSpellStep> steps = new ArrayList<>();
    private int nextNodeId;
    private int nextStepId;
    private String selfNodeId;
    private String vectorNodeId;
    private String positionNodeId;
    private String rayHitNodeId;
    private String entityListNodeId;
    private String blockListNodeId;
    private String positionListNodeId;
    private String effectPlanNodeId;
    private String numberNodeId;
    private String regionNodeId;
    private String boolNodeId;
    private String frameNodeId;
    private String outputNodeId = "";

    public void clear() {
        nodes.clear();
        edges.clear();
        steps.clear();
        nextNodeId = 0;
        nextStepId = 0;
        selfNodeId = null;
        vectorNodeId = null;
        positionNodeId = null;
        rayHitNodeId = null;
        entityListNodeId = null;
        blockListNodeId = null;
        positionListNodeId = null;
        effectPlanNodeId = null;
        numberNodeId = null;
        regionNodeId = null;
        boolNodeId = null;
        frameNodeId = null;
        outputNodeId = "";
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public List<CustomSpellStep> steps() {
        return List.copyOf(steps);
    }

    public List<CustomSpellAction> actions() {
        return steps.stream()
                .map(CustomSpellStep::action)
                .toList();
    }

    public List<CustomSpellInvocation> invocations() {
        return steps.stream()
                .map(CustomSpellStep::invocation)
                .toList();
    }

    public CustomActionPreview preview(CustomSpellAction action) {
        List<CustomActionPreview.Input> inputs = action.inputs().stream()
                .map(slot -> new CustomActionPreview.Input(slot, hasInput(slot)))
                .toList();
        CustomSpellWorkspace expanded = new CustomSpellWorkspace();
        expanded.loadInvocations(invocations());
        int nodeCount = expanded.nodes.size();
        int edgeCount = expanded.edges.size();
        expanded.apply(CustomSpellInvocation.defaults(action));
        return new CustomActionPreview(
                inputs,
                expanded.nodes.size() - nodeCount,
                expanded.edges.size() - edgeCount,
                action.resultType()
        );
    }

    public void loadActions(List<CustomSpellAction> actions) {
        loadInvocations(actions.stream().map(CustomSpellInvocation::defaults).toList());
    }

    public void loadInvocations(List<CustomSpellInvocation> invocations) {
        clear();
        for (CustomSpellInvocation invocation : invocations) {
            apply(invocation);
        }
    }

    public boolean undoLast() {
        List<CustomSpellInvocation> currentInvocations = invocations();
        if (currentInvocations.isEmpty()) {
            return false;
        }
        loadInvocations(currentInvocations.subList(0, currentInvocations.size() - 1));
        return true;
    }

    public void apply(CustomSpellAction action) {
        apply(CustomSpellInvocation.defaults(action));
    }

    public void apply(CustomSpellInvocation invocation) {
        CustomSpellAction action = invocation.action();
        int stepId = nextStepId++;
        switch (action) {
            case SELF -> outputNodeId = addExplicitSelf();
            case NUMBER_ONE -> addNumberLiteral(invocation.argument("value"));
            case ADD_ONE -> addNumberAddOne();
            case SUBTRACT_ONE -> addNumberSubtractOne();
            case DOUBLE_NUMBER -> addNumberMultiplyTwo();
            case HALVE_NUMBER -> addNumberDivideTwo();
            case CLAMP_NUMBER -> addNumberClamp();
            case UP_VECTOR -> addUpVector();
            case LOOK_VECTOR -> addLookVector();
            case SCALE_VECTOR -> addScaleVector();
            case VECTOR_ADD_UP -> addVectorAddUp();
            case VECTOR_SUBTRACT_UP -> addVectorSubtractUp();
            case NORMALIZE_VECTOR -> addVectorNormalize();
            case VECTOR_LENGTH -> addVectorLength();
            case DOT_WITH_LOOK -> addDotWithLook();
            case DISTANCE_TO_SELF -> addDistanceToSelf();
            case SPHERE_REGION -> addSphereRegion();
            case BOX_REGION -> addBoxRegion();
            case REGION_CONTAINS_SELF -> addRegionContainsSelf();
            case SAMPLE_REGION -> addSampleRegion();
            case RAYCAST -> addRaycast();
            case RAY_HIT_POSITION -> addRayHitPosition();
            case NEARBY_LIVING -> addNearbyLiving();
            case FILTER_NON_PLAYERS -> addFilterNonPlayers();
            case FILTER_TARGETS_REGION -> addFilterTargetsRegion();
            case NEAREST_TARGETS -> addNearestTargets();
            case NEARBY_BLOCKS -> addNearbyBlocks();
            case FILTER_BLOCKS_REGION -> addFilterBlocksRegion();
            case BLOCK_POSITIONS -> addBlockPositions();
            case AVERAGE_POSITION -> addAveragePosition();
            case PUSH_SELF -> addPushSelf();
            case DEBUG_MARKER -> addDebugMarker();
            case BLINK -> addBlink();
            case PUSH_TARGETS_PLAN -> addPushTargetsPlan();
            case EXECUTE_PLAN -> addExecutePlan();
            case RIGHT_BASIS_VECTOR -> addLocalVector("basis_right", "0.7", "0.08", "0");
            case FORWARD_BASIS_VECTOR -> addLocalVector("basis_forward", "0", "0.1", "0.85");
            case OBLIQUE_BASIS_VECTOR -> addLocalVector("basis_oblique", "-0.5", "0.3", "0.45");
            case SINE_NUMBER -> addNumberFunction("sin", "mathmod:number_sin");
            case COSINE_NUMBER -> addNumberFunction("cos", "mathmod:number_cos");
            case FINITE_DIFFERENCE -> addFiniteDifference(invocation);
            case SIMPSON_INTEGRAL -> addSimpsonIntegral(invocation);
            case ABS_NUMBER -> addScalarUnary("abs", "mathmod:number_abs");
            case MIN_NUMBER -> addScalarBinary("min", "mathmod:number_min", "1");
            case MAX_NUMBER -> addScalarBinary("max", "mathmod:number_max", "1");
            case POWER_NUMBER -> addScalarBinary("power", "mathmod:number_power", "2");
            case SQRT_NUMBER -> addScalarUnary("sqrt", "mathmod:number_sqrt");
            case LOG_NUMBER -> addScalarBinary("log", "mathmod:number_log", "10");
            case EXP_NUMBER -> addScalarUnary("exp", "mathmod:number_exp");
            case ATAN2_NUMBER -> addScalarBinary("atan2", "mathmod:number_atan2", "1");
            case LERP_NUMBER -> addScalarTernary("lerp", "mathmod:number_lerp", "1", "0.5");
            case AT_LEAST_NUMBER -> addScalarThreshold();
            case SELECT_NUMBER -> addScalarSelect();
            case CROSS_WITH_UP -> addCrossWithUp();
            case PROJECT_ONTO_LOOK -> addProjectOntoLook();
            case REFLECT_ACROSS_UP -> addReflectAcrossUp();
            case QUARTER_TURN_VECTOR -> addQuarterTurnVector();
            case HEAL_SELF -> addHealSelf();
            case SPEED_SELF -> addSelfStatusPlan("speed", "mathmod:speed_entities_plan", "20", "2");
            case INVISIBILITY_SELF -> addSelfStatusPlan("invis", "mathmod:invisibility_entities_plan", "30", "1");
            case NIGHT_VISION_SELF -> addSelfStatusPlan("night", "mathmod:night_vision_entities_plan", "60", "1");
            case WITHER_HOSTILES -> addHostileStatusPlan("wither", "mathmod:wither_entities_plan", "8", "2");
            case SOUL_BIND_HOSTILES -> addSoulBindHostiles();
            case VITAL_INFUSION_SELF -> addSelfStatusPlan("infuse", "mathmod:vital_infusion_plan", "30", "1");
            case ALCHEMICAL_MANTLE -> addAlchemicalMantle();
            case PARSIMONY_SELF -> addSelfStatusPlan("parsimony", "mathmod:parsimony_plan", "120", "1");
            case CONSERVATION_SELF -> addSelfStatusPlan("conservation", "mathmod:conservation_plan", "120", "1");
        }
        steps.add(new CustomSpellStep(stepId, action, invocation.arguments(), outputNodeId));
    }

    public ProgramGraph toGraph() {
        return new ProgramGraph(nodes, edges, outputNodeId, CUSTOM_BUDGET);
    }

    public List<String> describeConnections() {
        if (edges.isEmpty()) {
            return List.of();
        }

        List<String> descriptions = new ArrayList<>();
        for (ProgramEdge edge : edges) {
            descriptions.add(edge.toNodeId() + "." + edge.inputName() + " <- " + edge.fromNodeId());
        }
        return descriptions;
    }

    private String ensureSelf() {
        if (selfNodeId == null) {
            selfNodeId = addNode("self", "mathmod:self_player");
        }
        return selfNodeId;
    }

    private String addExplicitSelf() {
        selfNodeId = addNode("self", "mathmod:self_player");
        return selfNodeId;
    }

    private boolean hasInput(CustomInputSlot slot) {
        return switch (slot) {
            case PLAYER -> selfNodeId != null;
            case NUMBER -> numberNodeId != null;
            case VECTOR -> vectorNodeId != null;
            case POSITION -> positionNodeId != null;
            case FRAME -> frameNodeId != null;
            case RAY_HIT -> rayHitNodeId != null;
            case ENTITY_LIST -> entityListNodeId != null;
            case BLOCK_LIST -> blockListNodeId != null;
            case POSITION_LIST -> positionListNodeId != null;
            case REGION -> regionNodeId != null;
            case EFFECT_PLAN -> effectPlanNodeId != null;
        };
    }

    private String ensureVector() {
        if (vectorNodeId == null) {
            addUpVector();
        }
        return vectorNodeId;
    }

    private String ensureNumber() {
        if (numberNodeId == null) {
            addNumberLiteral(1.0D);
        }
        return numberNodeId;
    }

    private String ensurePosition() {
        if (positionNodeId == null) {
            String player = ensureSelf();
            positionNodeId = addNode("pos", "mathmod:player_position");
            connect(player, positionNodeId, "player");
        }
        return positionNodeId;
    }

    private String ensureFrame() {
        if (frameNodeId == null) {
            String player = ensureSelf();
            frameNodeId = addNode("frame", "mathmod:player_frame");
            connect(player, frameNodeId, "player");
        }
        return frameNodeId;
    }

    private String ensureRayHit() {
        if (rayHitNodeId == null) {
            addRaycast();
        }
        return rayHitNodeId;
    }

    private String ensureEntityList() {
        if (entityListNodeId == null) {
            addNearbyLiving();
        }
        return entityListNodeId;
    }

    private String ensureBlockList() {
        if (blockListNodeId == null) {
            addNearbyBlocks();
        }
        return blockListNodeId;
    }

    private String ensurePositionList() {
        if (positionListNodeId == null) {
            addSampleRegion();
        }
        return positionListNodeId;
    }

    private String ensureEffectPlan() {
        if (effectPlanNodeId == null) {
            addPushTargetsPlan();
        }
        return effectPlanNodeId;
    }

    private String ensureRegion() {
        if (regionNodeId == null) {
            addSphereRegion();
        }
        return regionNodeId;
    }

    private void addUpVector() {
        vectorNodeId = addVectorConstant("vec", "0", "0.35", "0");
        outputNodeId = vectorNodeId;
    }

    private void addNumberLiteral(double value) {
        numberNodeId = addConstant("num", Double.toString(value));
        outputNodeId = numberNodeId;
    }

    private void addFiniteDifference(CustomSpellInvocation invocation) {
        String start = addConstant("difference_start", Double.toString(invocation.argument("start")));
        String end = addConstant("difference_end", Double.toString(invocation.argument("end")));
        String step = addConstant("difference_step", Double.toString(invocation.argument("step")));
        numberNodeId = addNode("difference", "mathmod:finite_difference");
        connect(start, numberNodeId, "start");
        connect(end, numberNodeId, "end");
        connect(step, numberNodeId, "step");
        outputNodeId = numberNodeId;
    }

    private void addSimpsonIntegral(CustomSpellInvocation invocation) {
        String lower = addConstant("integral_lower", Double.toString(invocation.argument("lower")));
        String upper = addConstant("integral_upper", Double.toString(invocation.argument("upper")));
        String width = addNode("integral_width", "mathmod:number_subtract");
        connect(upper, width, "a");
        connect(lower, width, "b");
        String start = addConstant("integral_f_lower", Double.toString(invocation.argument("f_lower")));
        String midpoint = addConstant("integral_f_midpoint", Double.toString(invocation.argument("f_midpoint")));
        String end = addConstant("integral_f_upper", Double.toString(invocation.argument("f_upper")));
        numberNodeId = addNode("integral", "mathmod:simpson_integral");
        connect(start, numberNodeId, "start");
        connect(midpoint, numberNodeId, "midpoint");
        connect(end, numberNodeId, "end");
        connect(width, numberNodeId, "width");
        outputNodeId = numberNodeId;
    }

    private void addNumberAddOne() {
        String value = ensureNumber();
        String one = addConstant("one", "1");
        numberNodeId = addNode("add", "mathmod:number_add");
        connect(value, numberNodeId, "a");
        connect(one, numberNodeId, "b");
        outputNodeId = numberNodeId;
    }

    private void addNumberSubtractOne() {
        String value = ensureNumber();
        String one = addConstant("one", "1");
        numberNodeId = addNode("sub", "mathmod:number_subtract");
        connect(value, numberNodeId, "a");
        connect(one, numberNodeId, "b");
        outputNodeId = numberNodeId;
    }

    private void addNumberMultiplyTwo() {
        String value = ensureNumber();
        String two = addConstant("two", "2");
        numberNodeId = addNode("mul", "mathmod:number_multiply");
        connect(value, numberNodeId, "a");
        connect(two, numberNodeId, "b");
        outputNodeId = numberNodeId;
    }

    private void addNumberDivideTwo() {
        String value = ensureNumber();
        String two = addConstant("two", "2");
        numberNodeId = addNode("div", "mathmod:number_divide");
        connect(value, numberNodeId, "a");
        connect(two, numberNodeId, "b");
        outputNodeId = numberNodeId;
    }

    private void addNumberClamp() {
        String value = ensureNumber();
        String min = addConstant("min", "0");
        String max = addConstant("max", "1");
        numberNodeId = addNode("clamp", "mathmod:number_clamp");
        connect(value, numberNodeId, "value");
        connect(min, numberNodeId, "min");
        connect(max, numberNodeId, "max");
        outputNodeId = numberNodeId;
    }

    private void addScalarUnary(String prefix, String runeId) {
        String value = ensureNumber();
        numberNodeId = addNode(prefix, runeId);
        connect(value, numberNodeId, "value");
        outputNodeId = numberNodeId;
    }

    private void addScalarBinary(String prefix, String runeId, String fallback) {
        String value = ensureNumber();
        String other = addConstant(prefix + "_value", fallback);
        numberNodeId = addNode(prefix, runeId);
        String first = runeId.equals("mathmod:number_power") ? "base"
                : runeId.equals("mathmod:number_log") ? "value"
                : runeId.equals("mathmod:number_atan2") ? "y" : "a";
        String second = runeId.equals("mathmod:number_power") ? "exponent"
                : runeId.equals("mathmod:number_log") ? "base"
                : runeId.equals("mathmod:number_atan2") ? "x" : "b";
        connect(value, numberNodeId, first);
        connect(other, numberNodeId, second);
        outputNodeId = numberNodeId;
    }

    private void addScalarTernary(String prefix, String runeId, String secondValue, String thirdValue) {
        String value = ensureNumber();
        String second = addConstant(prefix + "_end", secondValue);
        String third = addConstant(prefix + "_factor", thirdValue);
        numberNodeId = addNode(prefix, runeId);
        connect(value, numberNodeId, "a");
        connect(second, numberNodeId, "b");
        connect(third, numberNodeId, "t");
        outputNodeId = numberNodeId;
    }

    private void addScalarThreshold() {
        String value = ensureNumber();
        String threshold = addConstant("threshold_value", "0.25");
        boolNodeId = addNode("threshold", "mathmod:number_at_least");
        connect(value, boolNodeId, "value");
        connect(threshold, boolNodeId, "threshold");
        outputNodeId = boolNodeId;
    }

    private void addScalarSelect() {
        if (boolNodeId == null) {
            addScalarThreshold();
        }
        String whenTrue = addConstant("select_true", "1");
        String whenFalse = addConstant("select_false", "0");
        numberNodeId = addNode("select", "mathmod:number_select");
        connect(boolNodeId, numberNodeId, "condition");
        connect(whenTrue, numberNodeId, "when_true");
        connect(whenFalse, numberNodeId, "when_false");
        outputNodeId = numberNodeId;
    }

    private void addNumberFunction(String prefix, String runeId) {
        String value = ensureNumber();
        numberNodeId = addNode(prefix, runeId);
        connect(value, numberNodeId, "angle");
        outputNodeId = numberNodeId;
    }

    private void addLookVector() {
        String player = ensureSelf();
        vectorNodeId = addNode("look", "mathmod:look_vector");
        connect(player, vectorNodeId, "player");
        outputNodeId = vectorNodeId;
    }

    private void addLocalVector(String prefix, String right, String up, String forward) {
        String frame = ensureFrame();
        String local = addVectorConstant(prefix + "_local", right, up, forward);
        vectorNodeId = addNode(prefix + "_world", "mathmod:transform_local_vector");
        connect(frame, vectorNodeId, "frame");
        connect(local, vectorNodeId, "vector");
        outputNodeId = vectorNodeId;
    }

    private void addScaleVector() {
        String vector = ensureVector();
        String factor = numberNodeId == null ? addConstant("factor", "0.8") : numberNodeId;
        String scaled = addNode("scale", "mathmod:scale_vector");
        connect(vector, scaled, "vector");
        connect(factor, scaled, "factor");
        vectorNodeId = scaled;
        outputNodeId = vectorNodeId;
    }

    private void addVectorAddUp() {
        String vector = ensureVector();
        String up = addVectorConstant("up_vec", "0", "0.2", "0");
        vectorNodeId = addNode("vadd", "mathmod:vector_add");
        connect(vector, vectorNodeId, "a");
        connect(up, vectorNodeId, "b");
        outputNodeId = vectorNodeId;
    }

    private void addVectorSubtractUp() {
        String vector = ensureVector();
        String up = addVectorConstant("up_vec", "0", "0.2", "0");
        vectorNodeId = addNode("vsub", "mathmod:vector_subtract");
        connect(vector, vectorNodeId, "a");
        connect(up, vectorNodeId, "b");
        outputNodeId = vectorNodeId;
    }

    private void addVectorNormalize() {
        String vector = ensureVector();
        vectorNodeId = addNode("norm", "mathmod:vector_normalize");
        connect(vector, vectorNodeId, "vector");
        outputNodeId = vectorNodeId;
    }

    private void addVectorLength() {
        String vector = ensureVector();
        numberNodeId = addNode("len", "mathmod:vector_length");
        connect(vector, numberNodeId, "vector");
        outputNodeId = numberNodeId;
    }

    private void addDotWithLook() {
        String vector = ensureVector();
        String player = ensureSelf();
        String look = addNode("look", "mathmod:look_vector");
        connect(player, look, "player");
        numberNodeId = addNode("dot", "mathmod:vector_dot");
        connect(vector, numberNodeId, "a");
        connect(look, numberNodeId, "b");
        outputNodeId = numberNodeId;
    }

    private void addCrossWithUp() {
        String vector = ensureVector();
        String up = addVectorConstant("cross_up", "0", "1", "0");
        vectorNodeId = addNode("cross", "mathmod:vector_cross");
        connect(up, vectorNodeId, "a");
        connect(vector, vectorNodeId, "b");
        outputNodeId = vectorNodeId;
    }

    private void addProjectOntoLook() {
        String vector = ensureVector();
        String player = ensureSelf();
        String look = addNode("project_look", "mathmod:look_vector");
        connect(player, look, "player");
        vectorNodeId = addNode("project", "mathmod:vector_project");
        connect(vector, vectorNodeId, "vector");
        connect(look, vectorNodeId, "onto");
        outputNodeId = vectorNodeId;
    }

    private void addReflectAcrossUp() {
        String vector = ensureVector();
        String up = addVectorConstant("reflect_up", "0", "1", "0");
        vectorNodeId = addNode("reflect", "mathmod:vector_reflect");
        connect(vector, vectorNodeId, "vector");
        connect(up, vectorNodeId, "normal");
        outputNodeId = vectorNodeId;
    }

    private void addQuarterTurnVector() {
        String vector = ensureVector();
        String order = addConstant("cycle_order", "4");
        String value = addConstant("cycle_value", "1");
        String element = addNode("cycle", "mathmod:cyclic_element");
        connect(order, element, "order");
        connect(value, element, "value");
        vectorNodeId = addNode("cycle_action", "mathmod:cyclic_rotate_y");
        connect(element, vectorNodeId, "element");
        connect(vector, vectorNodeId, "vector");
        outputNodeId = vectorNodeId;
    }

    private void addDistanceToSelf() {
        String vector = ensureVector();
        String position = ensurePosition();
        numberNodeId = addNode("dist", "mathmod:vector_distance");
        connect(vector, numberNodeId, "a");
        connect(position, numberNodeId, "b");
        outputNodeId = numberNodeId;
    }

    private void addSphereRegion() {
        String center = ensurePosition();
        String radius = numberNodeId == null ? addConstant("radius", "4") : numberNodeId;
        regionNodeId = addNode("sphere", "mathmod:sphere_region");
        connect(center, regionNodeId, "center");
        connect(radius, regionNodeId, "radius");
        outputNodeId = regionNodeId;
    }

    private void addBoxRegion() {
        String center = ensurePosition();
        String lowOffset = addVectorConstant("box_low", "-2", "-1", "-2");
        String highOffset = addVectorConstant("box_high", "2", "2", "2");
        String min = addNode("box_min", "mathmod:vector_add");
        String max = addNode("box_max", "mathmod:vector_add");
        connect(center, min, "a");
        connect(lowOffset, min, "b");
        connect(center, max, "a");
        connect(highOffset, max, "b");
        regionNodeId = addNode("box", "mathmod:box_region");
        connect(min, regionNodeId, "min");
        connect(max, regionNodeId, "max");
        outputNodeId = regionNodeId;
    }

    private void addRegionContainsSelf() {
        String region = ensureRegion();
        String position = ensurePosition();
        boolNodeId = addNode("contains", "mathmod:region_contains");
        connect(region, boolNodeId, "region");
        connect(position, boolNodeId, "position");
        outputNodeId = boolNodeId;
    }

    private void addSampleRegion() {
        String region = ensureRegion();
        positionListNodeId = addNode("sample", "mathmod:sample_region", Map.of(
                "step", "1",
                "limit", "32"
        ));
        connect(region, positionListNodeId, "region");
        outputNodeId = positionListNodeId;
    }

    private void addRaycast() {
        String player = ensureSelf();
        String range = addConstant("range", "16");
        rayHitNodeId = addNode("ray", "mathmod:raycast_block");
        connect(player, rayHitNodeId, "player");
        connect(range, rayHitNodeId, "range");
        outputNodeId = rayHitNodeId;
    }

    private void addRayHitPosition() {
        String hit = ensureRayHit();
        positionNodeId = addNode("hit_pos", "mathmod:ray_hit_position");
        connect(hit, positionNodeId, "hit");
        outputNodeId = positionNodeId;
    }

    private void addNearbyLiving() {
        String center = ensurePosition();
        entityListNodeId = addNode("nearby", "mathmod:nearby_entities", Map.of(
                "predicate", "any_living",
                "radius", "5",
                "limit", "8"
        ));
        connect(center, entityListNodeId, "center");
        outputNodeId = entityListNodeId;
    }

    private void addFilterNonPlayers() {
        String entities = ensureEntityList();
        String filtered = addNode("filter", "mathmod:filter_entities", Map.of("predicate", "non_player_living"));
        connect(entities, filtered, "entities");
        entityListNodeId = filtered;
        outputNodeId = entityListNodeId;
    }

    private void addFilterTargetsRegion() {
        String entities = ensureEntityList();
        String region = ensureRegion();
        entityListNodeId = addNode("targets_region", "mathmod:filter_entities_in_region");
        connect(entities, entityListNodeId, "entities");
        connect(region, entityListNodeId, "region");
        outputNodeId = entityListNodeId;
    }

    private void addNearestTargets() {
        String entities = ensureEntityList();
        String origin = ensurePosition();
        String nearest = addNode("near", "mathmod:nearest_entities", Map.of("limit", "4"));
        connect(entities, nearest, "entities");
        connect(origin, nearest, "origin");
        entityListNodeId = nearest;
        outputNodeId = entityListNodeId;
    }

    private void addNearbyBlocks() {
        String center = ensurePosition();
        blockListNodeId = addNode("blocks", "mathmod:nearby_blocks", Map.of(
                "selector", "any",
                "radius", "4",
                "limit", "32"
        ));
        connect(center, blockListNodeId, "center");
        outputNodeId = blockListNodeId;
    }

    private void addFilterBlocksRegion() {
        String blocks = ensureBlockList();
        String region = ensureRegion();
        blockListNodeId = addNode("blocks_region", "mathmod:filter_blocks_in_region");
        connect(blocks, blockListNodeId, "blocks");
        connect(region, blockListNodeId, "region");
        outputNodeId = blockListNodeId;
    }

    private void addBlockPositions() {
        String blocks = ensureBlockList();
        positionListNodeId = addNode("block_positions", "mathmod:block_positions");
        connect(blocks, positionListNodeId, "blocks");
        outputNodeId = positionListNodeId;
    }

    private void addAveragePosition() {
        String positions = ensurePositionList();
        positionNodeId = addNode("average", "mathmod:average_position");
        connect(positions, positionNodeId, "positions");
        outputNodeId = positionNodeId;
    }

    private void addPushSelf() {
        String player = ensureSelf();
        String vector = ensureVector();
        outputNodeId = addNode("push", "mathmod:push_self");
        connect(player, outputNodeId, "player");
        connect(vector, outputNodeId, "vector");
    }

    private void addDebugMarker() {
        String position = ensurePosition();
        outputNodeId = addNode("mark", "mathmod:debug_marker");
        connect(position, outputNodeId, "position");
    }

    private void addBlink() {
        String player = ensureSelf();
        String hit = ensureRayHit();
        outputNodeId = addNode("blink", "mathmod:blink_self_to_hit");
        connect(player, outputNodeId, "player");
        connect(hit, outputNodeId, "hit");
    }

    private void addPushTargetsPlan() {
        String entities = ensureEntityList();
        String vector = ensureVector();
        effectPlanNodeId = addNode("plan", "mathmod:push_entities_plan");
        connect(entities, effectPlanNodeId, "entities");
        connect(vector, effectPlanNodeId, "vector");
        outputNodeId = effectPlanNodeId;
    }

    private void addExecutePlan() {
        String plan = ensureEffectPlan();
        outputNodeId = addNode("exec", "mathmod:execute_effect_plan");
        connect(plan, outputNodeId, "plan");
    }

    private String ensureSelfTargets() {
        String player = ensureSelf();
        String targets = addNode("self_targets", "mathmod:player_as_entity_list");
        connect(player, targets, "player");
        return targets;
    }

    private void addHealSelf() {
        String targets = ensureSelfTargets();
        String amount = addConstant("heal_amount", "6");
        effectPlanNodeId = addNode("heal_plan", "mathmod:heal_entities_plan");
        connect(targets, effectPlanNodeId, "entities");
        connect(amount, effectPlanNodeId, "amount");
        outputNodeId = effectPlanNodeId;
    }

    private void addSelfStatusPlan(String prefix, String runeId, String durationValue, String levelValue) {
        String targets = ensureSelfTargets();
        String duration = addConstant(prefix + "_duration", durationValue);
        String level = addConstant(prefix + "_level", levelValue);
        effectPlanNodeId = addNode(prefix + "_plan", runeId);
        connect(targets, effectPlanNodeId, "entities");
        connect(duration, effectPlanNodeId, "duration");
        connect(level, effectPlanNodeId, "level");
        outputNodeId = effectPlanNodeId;
    }

    private void addHostileStatusPlan(String prefix, String runeId, String durationValue, String levelValue) {
        String center = ensurePosition();
        String nearby = addNode(prefix + "_nearby", "mathmod:nearby_entities", Map.of(
                "predicate", "hostile",
                "radius", "8",
                "limit", "8"
        ));
        connect(center, nearby, "center");
        entityListNodeId = addNode(prefix + "_targets", "mathmod:nearest_entities", Map.of("limit", "4"));
        connect(nearby, entityListNodeId, "entities");
        connect(center, entityListNodeId, "origin");
        String duration = addConstant(prefix + "_duration", durationValue);
        String level = addConstant(prefix + "_level", levelValue);
        effectPlanNodeId = addNode(prefix + "_plan", runeId);
        connect(entityListNodeId, effectPlanNodeId, "entities");
        connect(duration, effectPlanNodeId, "duration");
        connect(level, effectPlanNodeId, "level");
        outputNodeId = effectPlanNodeId;
    }

    private void addSoulBindHostiles() {
        String center = ensurePosition();
        String nearby = addNode("bind_nearby", "mathmod:nearby_entities", Map.of(
                "predicate", "hostile",
                "radius", "8",
                "limit", "8"
        ));
        connect(center, nearby, "center");
        entityListNodeId = addNode("bind_targets", "mathmod:nearest_entities", Map.of("limit", "4"));
        connect(nearby, entityListNodeId, "entities");
        connect(center, entityListNodeId, "origin");
        String duration = addConstant("bind_duration", "12");
        effectPlanNodeId = addNode("bind_plan", "mathmod:soul_bind_entities_plan");
        connect(entityListNodeId, effectPlanNodeId, "entities");
        connect(center, effectPlanNodeId, "anchor");
        connect(duration, effectPlanNodeId, "duration");
        outputNodeId = effectPlanNodeId;
    }

    private void addAlchemicalMantle() {
        String targets = ensureSelfTargets();
        String speedDuration = addConstant("mantle_speed_duration", "20");
        String speedLevel = addConstant("mantle_speed_level", "2");
        String speed = addNode("mantle_speed", "mathmod:speed_entities_plan");
        connect(targets, speed, "entities");
        connect(speedDuration, speed, "duration");
        connect(speedLevel, speed, "level");

        String nightDuration = addConstant("mantle_night_duration", "60");
        String nightLevel = addConstant("mantle_night_level", "1");
        String night = addNode("mantle_night", "mathmod:night_vision_entities_plan");
        connect(targets, night, "entities");
        connect(nightDuration, night, "duration");
        connect(nightLevel, night, "level");

        effectPlanNodeId = addNode("mantle_plan", "mathmod:combine_effect_plans");
        connect(speed, effectPlanNodeId, "first");
        connect(night, effectPlanNodeId, "second");
        outputNodeId = effectPlanNodeId;
    }

    private String addConstant(String prefix, String value) {
        return addNode(prefix, "mathmod:constant_number", Map.of("value", value));
    }

    private String addVectorConstant(String prefix, String xValue, String yValue, String zValue) {
        String x = addConstant(prefix + "_x", xValue);
        String y = addConstant(prefix + "_y", yValue);
        String z = addConstant(prefix + "_z", zValue);
        String vector = addNode(prefix, "mathmod:vector_from_numbers");
        connect(x, vector, "x");
        connect(y, vector, "y");
        connect(z, vector, "z");
        return vector;
    }

    private String addNode(String prefix, String runeId) {
        return addNode(prefix, runeId, Map.of());
    }

    private String addNode(String prefix, String runeId, Map<String, String> constants) {
        String nodeId = prefix + "_" + nextNodeId++;
        nodes.add(new ProgramNode(nodeId, runeId, constants));
        return nodeId;
    }

    private void connect(String fromNodeId, String toNodeId, String inputName) {
        edges.add(new ProgramEdge(fromNodeId, toNodeId, inputName));
    }
}
