package com.mathmod.program;

import com.mathmod.block.RuneAnchorBlockEntity;
import com.mathmod.effect.SoulBoundEffect;
import com.mathmod.field.CastFieldSampleCache;
import com.mathmod.field.FieldCalculus;
import com.mathmod.field.FieldProviderPublication;
import com.mathmod.field.FieldProviderServices;
import com.mathmod.field.FieldSampleException;
import com.mathmod.field.FieldSampleValue;
import com.mathmod.field.FieldSampler;
import com.mathmod.field.FieldSamplingContext;
import com.mathmod.field.SampleBoundary;
import com.mathmod.field.SamplePlan;
import com.mathmod.field.SamplePlanResult;
import com.mathmod.field.SamplePlanner;
import com.mathmod.field.SamplePoint;
import com.mathmod.field.BuiltInFieldProviders;
import com.mathmod.environment.EnvironmentalFieldServices;
import com.mathmod.environment.EnvironmentalSampleReport;
import com.mathmod.util.NamespacedId;
import com.mathmod.registry.ModMobEffects;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.runes.ProgramEdge;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.ValidationResult;
import com.mathmod.knowledge.KnowledgeProgressService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public final class ProgramExecutor {
    private ProgramExecutor() {
    }

    public static ProgramExecutionResult execute(ItemStack stack, ServerPlayer caster) {
        ProgramGraph graph = ProgramStorage.get(stack)
                .orElseThrow(() -> new IllegalArgumentException("Talisman has no program"));
        return execute(graph, ProgramResources.get(stack), caster);
    }

    public static ProgramExecutionResult execute(ProgramGraph graph, ServerPlayer caster) {
        return execute(graph, List.of(), caster);
    }

    private static ProgramExecutionResult execute(ProgramGraph graph, List<ResourceSelection> resources, ServerPlayer caster) {
        CastModifiers modifiers = PlayerCastModifiers.snapshot(caster);
        ProgramCostPlan plan = PlayerProgramCosts.planFor(caster, graph, resources, modifiers);
        ValidationResult validation = ProgramStorage.validateExecutable(graph, plan.budgetBonus());
        if (!validation.valid()) {
            return ProgramExecutionResult.failure("item.mathmod.programmed_talisman.execute_invalid");
        }

        if (!plan.success()) {
            return ProgramExecutionResult.failure(ProgramCostResult.failure(plan));
        }
        if (hasFillMaterialConflict(graph, plan)) {
            return ProgramExecutionResult.failure("item.mathmod.programmed_talisman.execute_region_invalid");
        }

        PlayerProgramCosts.CostEscrow escrow = PlayerProgramCosts.escrowPlanned(caster, plan);
        if (escrow == null) {
            return ProgramExecutionResult.failure("item.mathmod.programmed_talisman.execute_missing_items");
        }
        try {
            Runtime runtime = new Runtime(graph, caster.serverLevel(), caster, caster.position(), null);
            runtime.evaluate(graph.outputNodeId());
            escrow.settle(caster.getRandom());
            KnowledgeProgressService.recordSuccessfulCast(caster, plan);
            return ProgramExecutionResult.success("item.mathmod.programmed_talisman.executed");
        } catch (ProgramExecutionException exception) {
            escrow.restore();
            return ProgramExecutionResult.failure(exception.messageKey());
        } catch (RuntimeException exception) {
            escrow.restore();
            throw exception;
        }
    }

    public static ProgramExecutionResult executeFromAnchor(ProgramGraph graph, ServerLevel level, Vec3 origin) {
        ValidationResult validation = ProgramStorage.validateExecutable(graph);
        if (!validation.valid()) {
            return ProgramExecutionResult.failure("block.mathmod.rune_anchor.execute_invalid");
        }
        if (P9EffectPolicy.usesDefensiveAlchemy(graph, MathModRuneBootstrap.registry())) {
            return ProgramExecutionResult.failure("block.mathmod.rune_anchor.execute_p9_player_only");
        }

        try {
            Runtime runtime = new Runtime(graph, level, null, origin, BlockPos.containing(origin));
            runtime.evaluate(graph.outputNodeId());
            return ProgramExecutionResult.success("block.mathmod.rune_anchor.executed");
        } catch (ProgramExecutionException exception) {
            return ProgramExecutionResult.failure(exception.messageKey());
        }
    }

    private static boolean hasFillMaterialConflict(ProgramGraph graph, ProgramCostPlan plan) {
        for (ProgramNode node : graph.nodes()) {
            if (!node.runeId().equals("mathmod:fill_region")) {
                continue;
            }
            try {
                ItemStack material = new ItemStack(ItemSelectors.exactItem(
                        node.constants().getOrDefault("material", "minecraft:stone")
                ));
                for (ProgramCostLine line : plan.lines()) {
                    if (line.consumed() && ItemSelectors.matches(material, line.selector())) {
                        return true;
                    }
                }
            } catch (IllegalArgumentException exception) {
                return true;
            }
        }
        return false;
    }

    private static final class Runtime {
        private final ProgramGraph graph;
        private final ServerLevel level;
        private final ServerPlayer caster;
        private final Vec3 origin;
        private final BlockPos anchorPos;
        private final Map<String, ProgramNode> nodesById = new HashMap<>();
        private final Map<String, RuneDefinition> definitionsByNode = new HashMap<>();
        private final Map<String, Object> valuesByNode = new HashMap<>();
        private final CastFieldSampleCache fieldSampleCache = new CastFieldSampleCache();
        private final FieldProviderPublication fieldProviders = FieldProviderServices.snapshot();

        private Runtime(ProgramGraph graph, ServerLevel level, ServerPlayer caster, Vec3 origin, BlockPos anchorPos) {
            this.graph = graph;
            this.level = level;
            this.caster = caster;
            this.origin = origin;
            this.anchorPos = anchorPos;
            for (ProgramNode node : graph.nodes()) {
                nodesById.put(node.id(), node);
                ProgramStorage.definition(node.runeId()).ifPresent(definition -> definitionsByNode.put(node.id(), definition));
            }
            ProgramNormalizer.normalize(graph, MathModRuneBootstrap.registry())
                    .valuesByNode()
                    .forEach((nodeId, value) -> valuesByNode.put(nodeId, value.runtimeValue()));
        }

        private Object evaluate(String nodeId) {
            if (valuesByNode.containsKey(nodeId)) {
                return valuesByNode.get(nodeId);
            }

            ProgramNode node = nodesById.get(nodeId);
            if (node == null) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_missing_node");
            }
            RuneDefinition definition = definitionsByNode.get(node.id());
            if (definition == null) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_missing_node");
            }

            Object value = switch (definition.executorKey()) {
                case "constant_number" -> constantNumber(node);
                case "vector_from_numbers" -> new Vec3(
                        numberInput(node, "x"),
                        numberInput(node, "y"),
                        numberInput(node, "z")
                );
                case "self_player" -> {
                    if (caster == null) {
                        throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_missing_player");
                    }
                    yield caster;
                }
                case "player_position" -> playerInput(node, "player").position();
                case "look_vector" -> playerInput(node, "player").getLookAngle();
                case "player_frame" -> playerFrame(node);
                case "transform_local_vector" -> transformLocalVector(node);
                case "scale_vector" -> vectorInput(node, "vector").scale(numberInput(node, "factor"));
                case "number_add" -> numberInput(node, "a") + numberInput(node, "b");
                case "number_subtract" -> numberInput(node, "a") - numberInput(node, "b");
                case "number_multiply" -> numberInput(node, "a") * numberInput(node, "b");
                case "number_divide" -> divide(numberInput(node, "a"), numberInput(node, "b"));
                case "number_clamp" -> clampNumber(node);
                case "number_round" -> (double) Math.round(numberInput(node, "value"));
                case "number_abs" -> scalarNumber("number_abs", numberInput(node, "value"));
                case "number_min" -> scalarNumber("number_min", numberInput(node, "a"), numberInput(node, "b"));
                case "number_max" -> scalarNumber("number_max", numberInput(node, "a"), numberInput(node, "b"));
                case "number_power" -> scalarNumber("number_power", numberInput(node, "base"), numberInput(node, "exponent"));
                case "number_sqrt" -> scalarNumber("number_sqrt", numberInput(node, "value"));
                case "number_log" -> scalarNumber("number_log", numberInput(node, "value"), numberInput(node, "base"));
                case "number_exp" -> scalarNumber("number_exp", numberInput(node, "value"));
                case "number_atan2" -> scalarNumber("number_atan2", numberInput(node, "y"), numberInput(node, "x"));
                case "number_lerp" -> scalarNumber("number_lerp", numberInput(node, "a"), numberInput(node, "b"), numberInput(node, "t"));
                case "number_at_least" -> ScalarOperations.atLeast(numberInput(node, "value"), numberInput(node, "threshold"));
                case "number_select" -> booleanInput(node, "condition")
                        ? scalarNumber("number_select", 1.0D, numberInput(node, "when_true"), numberInput(node, "when_false"))
                        : scalarNumber("number_select", 0.0D, numberInput(node, "when_true"), numberInput(node, "when_false"));
                case "number_sin" -> mathematicalNumber(
                        () -> MathematicalOperations.sine(numberInput(node, "angle"))
                );
                case "number_cos" -> mathematicalNumber(
                        () -> MathematicalOperations.cosine(numberInput(node, "angle"))
                );
                case "living_density_field" -> new ScalarFieldValue(BuiltInFieldProviders.LIVING_DENSITY);
                case "environmental_field" -> AttributeFieldValue.INSTANCE;
                case "project_environmental_channel" -> projectEnvironmentalChannel(node);
                case "field_gradient" -> fieldGradient(node);
                case "dimensional_survey" -> dimensionalSurvey();
                case "finite_difference" -> mathematicalNumber(
                        () -> MathematicalOperations.finiteDifference(
                                numberInput(node, "start"),
                                numberInput(node, "end"),
                                numberInput(node, "step")
                        )
                );
                case "simpson_integral" -> mathematicalNumber(
                        () -> MathematicalOperations.simpsonIntegral(
                                numberInput(node, "start"),
                                numberInput(node, "midpoint"),
                                numberInput(node, "end"),
                                numberInput(node, "width")
                        )
                );
                case "vector_add" -> vectorInput(node, "a").add(vectorInput(node, "b"));
                case "vector_subtract" -> vectorInput(node, "a").subtract(vectorInput(node, "b"));
                case "vector_normalize" -> normalize(vectorInput(node, "vector"));
                case "vector_length" -> vectorInput(node, "vector").length();
                case "vector_dot" -> vectorInput(node, "a").dot(vectorInput(node, "b"));
                case "vector_cross" -> mathematicalVector(
                        () -> MathematicalOperations.cross(
                                operationVector(vectorInput(node, "a")),
                                operationVector(vectorInput(node, "b"))
                        )
                );
                case "vector_project" -> mathematicalVector(
                        () -> MathematicalOperations.project(
                                operationVector(vectorInput(node, "vector")),
                                operationVector(vectorInput(node, "onto"))
                        )
                );
                case "vector_reflect" -> mathematicalVector(
                        () -> MathematicalOperations.reflect(
                                operationVector(vectorInput(node, "vector")),
                                operationVector(vectorInput(node, "normal"))
                        )
                );
                case "vector_distance" -> vectorInput(node, "a").distanceTo(vectorInput(node, "b"));
                case "cyclic_element" -> mathematical(
                        () -> MathematicalOperations.cyclicElement(
                                numberInput(node, "order"),
                                numberInput(node, "value")
                        )
                );
                case "cyclic_compose" -> mathematical(
                        () -> cyclicElementInput(node, "a").compose(cyclicElementInput(node, "b"))
                );
                case "cyclic_inverse" -> cyclicElementInput(node, "element").inverse();
                case "cyclic_rotate_y" -> mathematicalVector(
                        () -> MathematicalOperations.rotateY(
                                operationVector(vectorInput(node, "vector")),
                                cyclicElementInput(node, "element")
                        )
                );
                case "sphere_region" -> sphereRegion(node);
                case "box_region" -> boxRegion(node);
                case "region_union" -> new UnionSpatialRegion(
                        regionInput(node, "first"), regionInput(node, "second")
                );
                case "region_intersection" -> new IntersectionSpatialRegion(
                        regionInput(node, "first"), regionInput(node, "second")
                );
                case "region_difference" -> new DifferenceSpatialRegion(
                        regionInput(node, "first"), regionInput(node, "second")
                );
                case "solid_of_revolution" -> solidOfRevolution(node);
                case "fill_region" -> fillRegion(node);
                case "materialize_construct" -> materializeConstruct(node);
                case "compress_construct" -> constructBodyInput(node, "body").compress(numberInput(node, "scale"));
                case "spin_construct" -> spinConstruct(node);
                case "launch_construct" -> launchConstruct(node);
                case "region_contains" -> regionInput(node, "region").contains(vectorInput(node, "position"));
                case "sample_region" -> sampleRegion(node);
                case "raycast_block" -> raycastBlock(node);
                case "ray_hit_position" -> rayHitPosition(node);
                case "blink_self_to_hit" -> blinkSelfToHit(node);
                case "anchor_origin" -> origin;
                case "consume_nearby_item" -> consumeNearbyItem(node);
                case "spawn_item" -> spawnItem(node);
                case "pulse_nearby_entities" -> pulseNearbyEntities(node);
                case "nearby_entities" -> nearbyEntities(node);
                case "sense_nearby_entities" -> senseNearbyEntities(node);
                case "filter_entities" -> filterEntities(node);
                case "filter_entities_in_region" -> filterEntitiesInRegion(node);
                case "nearest_entities" -> nearestEntities(node);
                case "farthest_entities" -> farthestEntities(node);
                case "entity_positions" -> entityPositions(node);
                case "entity_velocities" -> entityVelocities(node);
                case "vector_lengths" -> vectorLengths(node);
                case "sum_numbers" -> sumNumbers(node);
                case "mean_number" -> meanNumber(node);
                case "max_number" -> maxNumber(node);
                case "count_entities" -> (double) entityListInput(node, "entities").size();
                case "nearby_blocks" -> nearbyBlocks(node);
                case "block_positions" -> blockPositions(node);
                case "filter_blocks_in_region" -> filterBlocksInRegion(node);
                case "count_blocks" -> (double) blockPosListInput(node, "blocks").size();
                case "average_position" -> averagePosition(node);
                case "emit_anchor_redstone" -> emitAnchorRedstone(node);
                case "push_entities_plan" -> pushEntitiesPlan(node);
                case "player_as_entity_list" -> List.of(playerInput(node, "player"));
                case "heal_entities_plan" -> healEntitiesPlan(node);
                case "cleanse_entities_plan" -> cleanseEntitiesPlan(node);
                case "resistance_entities_plan" -> defensiveStatusEffectPlan(
                        node,
                        MobEffects.DAMAGE_RESISTANCE,
                        ParticleTypes.ENCHANT
                );
                case "absorption_entities_plan" -> defensiveStatusEffectPlan(
                        node,
                        MobEffects.ABSORPTION,
                        ParticleTypes.HEART
                );
                case "speed_entities_plan" -> statusEffectPlan(
                        node,
                        MobEffects.MOVEMENT_SPEED,
                        ProgramExecutionPolicy.MAX_BENEFICIAL_EFFECT_DURATION_TICKS,
                        true,
                        ParticleTypes.ELECTRIC_SPARK
                );
                case "invisibility_entities_plan" -> statusEffectPlan(
                        node,
                        MobEffects.INVISIBILITY,
                        ProgramExecutionPolicy.MAX_BENEFICIAL_EFFECT_DURATION_TICKS,
                        true,
                        ParticleTypes.POOF
                );
                case "night_vision_entities_plan" -> statusEffectPlan(
                        node,
                        MobEffects.NIGHT_VISION,
                        ProgramExecutionPolicy.MAX_BENEFICIAL_EFFECT_DURATION_TICKS,
                        true,
                        ParticleTypes.GLOW
                );
                case "wither_entities_plan" -> statusEffectPlan(
                        node,
                        MobEffects.WITHER,
                        ProgramExecutionPolicy.MAX_HARMFUL_EFFECT_DURATION_TICKS,
                        false,
                        ParticleTypes.SMOKE
                );
                case "soul_bind_entities_plan" -> soulBindEntitiesPlan(node);
                case "vital_infusion_plan" -> statusEffectPlan(
                        node,
                        ModMobEffects.VITAL_INFUSION,
                        ProgramExecutionPolicy.MAX_BENEFICIAL_EFFECT_DURATION_TICKS,
                        true,
                        ParticleTypes.WAX_ON
                );
                case "parsimony_plan" -> statusEffectPlan(
                        node,
                        ModMobEffects.PARSIMONY,
                        ProgramExecutionPolicy.MAX_METAMAGIC_EFFECT_DURATION_TICKS,
                        true,
                        ParticleTypes.ENCHANT
                );
                case "conservation_plan" -> statusEffectPlan(
                        node,
                        ModMobEffects.CONSERVATION,
                        ProgramExecutionPolicy.MAX_METAMAGIC_EFFECT_DURATION_TICKS,
                        true,
                        ParticleTypes.ELECTRIC_SPARK
                );
                case "combine_effect_plans" -> new CompositeEffectPlan(List.of(
                        effectPlanInput(node, "first"),
                        effectPlanInput(node, "second")
                ));
                case "execute_effect_plan" -> executeEffectPlan(node);
                case "push_self" -> pushSelf(node);
                case "debug_marker" -> debugMarker(node);
                default -> throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_unsupported");
            };

            valuesByNode.put(nodeId, value);
            return value;
        }

        private double constantNumber(ProgramNode node) {
            String value = node.constants().getOrDefault("value", "0");
            try {
                double parsed = Double.parseDouble(value);
                if (!Double.isFinite(parsed)) {
                    throw new NumberFormatException("Non-finite number");
                }
                return parsed;
            } catch (NumberFormatException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_constant");
            }
        }

        private double divide(double a, double b) {
            if (Math.abs(b) < 0.0000001D) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_math_error");
            }
            return a / b;
        }

        private double clampNumber(ProgramNode node) {
            double value = numberInput(node, "value");
            double min = numberInput(node, "min");
            double max = numberInput(node, "max");
            if (min > max) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_math_error");
            }
            return clamp(value, min, max);
        }

        private double mathematicalNumber(DoubleSupplier operation) {
            try {
                return operation.getAsDouble();
            } catch (IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_math_error");
            }
        }

        private double scalarNumber(String executorKey, double... values) {
            return mathematicalNumber(() -> ScalarOperations.number(executorKey, values));
        }

        private <T> T mathematical(Supplier<T> operation) {
            try {
                return operation.get();
            } catch (IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_math_error");
            }
        }

        private Vec3 mathematicalVector(Supplier<MathematicalOperations.Vector> operation) {
            MathematicalOperations.Vector vector = mathematical(operation);
            return finiteVector(new Vec3(vector.x(), vector.y(), vector.z()));
        }

        private static MathematicalOperations.Vector operationVector(Vec3 vector) {
            return new MathematicalOperations.Vector(vector.x, vector.y, vector.z);
        }

        private Vec3 normalize(Vec3 vector) {
            if (vector.lengthSqr() < 0.0000001D) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_math_error");
            }
            return vector.normalize();
        }

        private CoordinateFrame playerFrame(ProgramNode node) {
            ServerPlayer player = playerInput(node, "player");
            Vec3 horizontalLook = Vec3.directionFromRotation(0.0F, player.getYRot());
            try {
                return CoordinateFrame.horizontal(horizontalLook.x, horizontalLook.z);
            } catch (IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_math_error");
            }
        }

        private Vec3 transformLocalVector(ProgramNode node) {
            Vec3 local = vectorInput(node, "vector");
            CoordinateFrame.Axis world = frameInput(node, "frame").toWorld(local.x, local.y, local.z);
            return finiteVector(new Vec3(world.x(), world.y(), world.z()));
        }

        private SpatialRegion sphereRegion(ProgramNode node) {
            Vec3 center = finiteVector(vectorInput(node, "center"));
            double radius = boundedNumber(numberInput(node, "radius"), 0.0D, ProgramExecutionPolicy.MAX_REGION_RADIUS);
            return new SphereSpatialRegion(center, radius);
        }

        private SpatialRegion boxRegion(ProgramNode node) {
            Vec3 first = finiteVector(vectorInput(node, "min"));
            Vec3 second = finiteVector(vectorInput(node, "max"));
            Vec3 center = first.add(second).scale(0.5D);
            double halfMax = ProgramExecutionPolicy.MAX_REGION_BOX_EXTENT * 0.5D;
            Vec3 halfSize = new Vec3(
                    Math.min(Math.abs(first.x - second.x) * 0.5D, halfMax),
                    Math.min(Math.abs(first.y - second.y) * 0.5D, halfMax),
                    Math.min(Math.abs(first.z - second.z) * 0.5D, halfMax)
            );
            return new BoxSpatialRegion(center.subtract(halfSize), center.add(halfSize));
        }

        private SpatialRegion solidOfRevolution(ProgramNode node) {
            try {
                return new RevolutionSpatialRegion(
                        finiteVector(vectorInput(node, "origin")),
                        finiteVector(vectorInput(node, "axis")),
                        doubleConstant(node, "inner", 0.0D),
                        doubleConstant(node, "outer", 1.0D),
                        doubleConstant(node, "lower", -1.0D),
                        doubleConstant(node, "upper", 1.0D)
                );
            } catch (IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_constant");
            }
        }

        private Unit fillRegion(ProgramNode node) {
            if (caster == null) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_missing_player");
            }
            ConstructionFillService.Outcome outcome = ConstructionFillService.fill(
                    level,
                    caster,
                    regionInput(node, "region"),
                    stringConstant(node, "material", "minecraft:stone")
            );
            if (!outcome.success()) {
                throw new ProgramExecutionException(outcome.messageKey());
            }
            return Unit.INSTANCE;
        }

        private ConstructBody materializeConstruct(ProgramNode node) {
            RegionCandidatePlanner.Result result = RegionCandidatePlanner.plan(regionInput(node, "region"));
            if (!result.valid() || result.plan().orElseThrow().positions().isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_positions");
            }
            try {
                String material = stringConstant(node, "material", "minecraft:stone");
                if (!(ItemSelectors.exactItem(material) instanceof net.minecraft.world.item.BlockItem)) {
                    throw new IllegalArgumentException();
                }
                return ConstructBody.materialize(material, result.plan().orElseThrow().positions());
            } catch (IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_item");
            }
        }

        private ConstructBody spinConstruct(ProgramNode node) {
            Vec3 axis = vectorInput(node, "axis");
            try {
                return constructBodyInput(node, "body").spin(new GeometryPoint(axis.x, axis.y, axis.z), numberInput(node, "speed"));
            } catch (IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_constant");
            }
        }

        private Unit launchConstruct(ProgramNode node) {
            if (caster == null || !ConstructFlightManager.launch(caster, constructBodyInput(node, "body"),
                    vectorInput(node, "origin"), vectorInput(node, "velocity"))) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_region_invalid");
            }
            return Unit.INSTANCE;
        }

        private ConstructBody constructBodyInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof ConstructBody body) {
                return body;
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private List<Vec3> sampleRegion(ProgramNode node) {
            SpatialRegion region = regionInput(node, "region");
            double step = boundedNumber(
                    doubleConstant(node, "step", 1.0D),
                    ProgramExecutionPolicy.MIN_REGION_SAMPLE_STEP,
                    ProgramExecutionPolicy.MAX_REGION_SAMPLE_STEP
            );
            int limit = intConstant(node, "limit", 32, 1, ProgramExecutionPolicy.MAX_REGION_SAMPLE_POINTS);
            AABB bounds = region.bounds();
            ensureFiniteBounds(bounds);

            List<Vec3> samples = new ArrayList<>();
            double startX = Math.floor(bounds.minX) + 0.5D;
            double startY = Math.floor(bounds.minY) + 0.5D;
            double startZ = Math.floor(bounds.minZ) + 0.5D;

            for (double x = startX; x <= bounds.maxX && samples.size() < limit; x += step) {
                for (double y = startY; y <= bounds.maxY && samples.size() < limit; y += step) {
                    for (double z = startZ; z <= bounds.maxZ && samples.size() < limit; z += step) {
                        Vec3 sample = new Vec3(x, y, z);
                        if (region.contains(sample)) {
                            samples.add(sample);
                        }
                    }
                }
            }

            if (samples.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_positions");
            }
            return List.copyOf(samples);
        }

        private Vec3 consumeNearbyItem(ProgramNode node) {
            Vec3 position = vectorInput(node, "position");
            String itemSelector = itemSelectorConstant(node, "item", AnchorPresetConfig.sacrificeSelector());
            int count = intConstant(node, "count", 1, 1, 64);
            double radius = clamp(doubleConstant(node, "radius", 2.5D), 0.25D, ProgramExecutionPolicy.MAX_SACRIFICE_RADIUS);
            validateItemSelector(itemSelector);
            AABB bounds = new AABB(
                    position.x - radius,
                    position.y - radius,
                    position.z - radius,
                    position.x + radius,
                    position.y + radius,
                    position.z + radius
            );

            for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, bounds, entity -> !entity.isRemoved())) {
                if (!ItemSelectors.matches(itemEntity.getItem(), itemSelector) || itemEntity.getItem().getCount() < count) {
                    continue;
                }

                itemEntity.getItem().shrink(count);
                if (itemEntity.getItem().isEmpty()) {
                    itemEntity.discard();
                }
                return position;
            }

            throw new ProgramExecutionException("block.mathmod.rune_anchor.missing_sacrifice");
        }

        private Object spawnItem(ProgramNode node) {
            Vec3 position = vectorInput(node, "position");
            Item item = itemConstant(node, "item", "minecraft:glowstone_dust");
            int count = intConstant(node, "count", 1, 1, ProgramExecutionPolicy.MAX_SPAWNED_ITEM_COUNT);
            ItemEntity itemEntity = new ItemEntity(
                    level,
                    position.x,
                    position.y + 0.4D,
                    position.z,
                    new ItemStack(item, count)
            );

            level.addFreshEntity(itemEntity);
            level.sendParticles(
                    ParticleTypes.END_ROD,
                    position.x,
                    position.y + 0.4D,
                    position.z,
                    16,
                    0.25D,
                    0.25D,
                    0.25D,
                    0.01D
            );
            level.playSound(
                    null,
                    position.x,
                    position.y,
                    position.z,
                    SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.BLOCKS,
                    0.8F,
                    1.1F
            );
            return Unit.INSTANCE;
        }

        private Object pulseNearbyEntities(ProgramNode node) {
            Vec3 position = vectorInput(node, "position");
            double radius = clamp(doubleConstant(node, "radius", 4.0D), 0.5D, ProgramExecutionPolicy.MAX_ENTITY_PULSE_RADIUS);
            double strength = clamp(doubleConstant(node, "strength", 0.8D), 0.0D, ProgramExecutionPolicy.MAX_ENTITY_PULSE_STRENGTH);
            AABB bounds = new AABB(
                    position.x - radius,
                    position.y - radius,
                    position.z - radius,
                    position.x + radius,
                    position.y + radius,
                    position.z + radius
            );

            int affected = 0;
            for (LivingEntity entity : level.getEntitiesOfClass(
                    LivingEntity.class,
                    bounds,
                    entity -> entity.isAlive() && !(entity instanceof ServerPlayer)
            )) {
                Vec3 offset = entity.position().subtract(position);
                Vec3 horizontal = new Vec3(offset.x, 0.0D, offset.z);
                if (horizontal.lengthSqr() < 0.0001D) {
                    horizontal = new Vec3(0.0D, 0.0D, 1.0D);
                } else {
                    horizontal = horizontal.normalize();
                }

                entity.push(horizontal.x * strength, 0.25D, horizontal.z * strength);
                entity.hasImpulse = true;
                affected++;
            }

            if (affected > 0) {
                level.sendParticles(
                        ParticleTypes.END_ROD,
                        position.x,
                        position.y + 0.4D,
                        position.z,
                        32,
                        0.45D,
                        0.25D,
                        0.45D,
                        0.03D
                );
                level.playSound(
                        null,
                        position.x,
                        position.y,
                        position.z,
                        SoundEvents.AMETHYST_BLOCK_CHIME,
                        SoundSource.BLOCKS,
                        1.0F,
                        0.75F
                );
            }

            return Unit.INSTANCE;
        }

        private List<Entity> nearbyEntities(ProgramNode node) {
            return nearbyEntities(node, true);
        }

        private List<Entity> senseNearbyEntities(ProgramNode node) {
            return nearbyEntities(node, false);
        }

        private List<Entity> nearbyEntities(ProgramNode node, boolean requireTargets) {
            Vec3 center = vectorInput(node, "center");
            String predicate = node.constants().getOrDefault("predicate", "non_player_living");
            double radius = clamp(doubleConstant(node, "radius", 4.0D), 0.25D, ProgramExecutionPolicy.MAX_TARGET_QUERY_RADIUS);
            int limit = intConstant(node, "limit", 4, 1, ProgramExecutionPolicy.MAX_TARGET_LIST_SIZE);
            AABB bounds = new AABB(
                    center.x - radius,
                    center.y - radius,
                    center.z - radius,
                    center.x + radius,
                    center.y + radius,
                    center.z + radius
            );

            List<LivingEntity> targets = level.getEntitiesOfClass(
                    LivingEntity.class,
                    bounds,
                    entity -> matchesTargetPredicate(entity, predicate)
            );
            if (requireTargets && targets.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
            }

            return sortedAndLimited(targets, center, limit);
        }

        private List<Entity> filterEntities(ProgramNode node) {
            List<Entity> entities = entityListInput(node, "entities");
            String predicate = node.constants().getOrDefault("predicate", "non_player_living");
            List<Entity> filtered = entities.stream()
                    .filter(entity -> entity instanceof LivingEntity livingEntity && matchesTargetPredicate(livingEntity, predicate))
                    .toList();
            if (filtered.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
            }
            return filtered;
        }

        private List<Entity> filterEntitiesInRegion(ProgramNode node) {
            List<Entity> entities = entityListInput(node, "entities");
            SpatialRegion region = regionInput(node, "region");
            List<Entity> filtered = entities.stream()
                    .filter(entity -> region.contains(entity.position()))
                    .toList();
            if (filtered.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
            }
            return filtered;
        }

        private List<Entity> nearestEntities(ProgramNode node) {
            List<Entity> entities = entityListInput(node, "entities");
            Vec3 origin = vectorInput(node, "origin");
            int limit = intConstant(node, "limit", ProgramExecutionPolicy.MAX_TARGET_LIST_SIZE, 1, ProgramExecutionPolicy.MAX_TARGET_LIST_SIZE);
            if (entities.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
            }
            return sortedAndLimited(entities, origin, limit);
        }

        private List<Entity> farthestEntities(ProgramNode node) {
            List<Entity> entities = entityListInput(node, "entities");
            Vec3 origin = vectorInput(node, "origin");
            int limit = intConstant(node, "limit", ProgramExecutionPolicy.MAX_TARGET_LIST_SIZE, 1, ProgramExecutionPolicy.MAX_TARGET_LIST_SIZE);
            if (entities.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
            }
            return sortedAndLimited(entities, origin, limit, false);
        }

        private List<Vec3> entityPositions(ProgramNode node) {
            return entityListInput(node, "entities").stream()
                    .map(Entity::position)
                    .toList();
        }

        private List<Vec3> entityVelocities(ProgramNode node) {
            return entityListInput(node, "entities").stream()
                    .map(Entity::getDeltaMovement)
                    .map(Runtime::finiteVector)
                    .toList();
        }

        private List<Double> vectorLengths(ProgramNode node) {
            return vec3ListInput(node, "vectors").stream()
                    .map(Vec3::length)
                    .toList();
        }

        private double sumNumbers(ProgramNode node) {
            double total = 0.0D;
            for (double value : numberListInput(node, "values")) {
                total += value;
            }
            return boundedNumber(total, -Double.MAX_VALUE, Double.MAX_VALUE);
        }

        private double meanNumber(ProgramNode node) {
            List<Double> values = numberListInput(node, "values");
            if (values.isEmpty()) {
                return 0.0D;
            }
            return sumNumbers(node) / values.size();
        }

        private double maxNumber(ProgramNode node) {
            return numberListInput(node, "values").stream()
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .orElse(0.0D);
        }

        private List<BlockPos> nearbyBlocks(ProgramNode node) {
            Vec3 center = vectorInput(node, "center");
            String selector = node.constants().getOrDefault("selector", "any");
            double radius = clamp(doubleConstant(node, "radius", 3.0D), 0.0D, ProgramExecutionPolicy.MAX_BLOCK_QUERY_RADIUS);
            int limit = intConstant(node, "limit", 16, 1, ProgramExecutionPolicy.MAX_BLOCK_LIST_SIZE);
            int wholeRadius = (int) Math.ceil(radius);
            BlockPos centerPos = BlockPos.containing(center);
            BlockPos min = centerPos.offset(-wholeRadius, -wholeRadius, -wholeRadius);
            BlockPos max = centerPos.offset(wholeRadius, wholeRadius, wholeRadius);
            List<BlockPos> matches = new ArrayList<>();

            for (BlockPos candidate : BlockPos.betweenClosed(min, max)) {
                BlockPos blockPos = candidate.immutable();
                if (Vec3.atCenterOf(blockPos).distanceToSqr(center) > radius * radius) {
                    continue;
                }
                if (matchesBlockSelector(level.getBlockState(blockPos), selector)) {
                    matches.add(blockPos);
                }
            }

            if (matches.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_blocks");
            }
            return sortedBlockPositions(matches, center, limit);
        }

        private List<Vec3> blockPositions(ProgramNode node) {
            return blockPosListInput(node, "blocks").stream()
                    .map(Vec3::atCenterOf)
                    .toList();
        }

        private List<BlockPos> filterBlocksInRegion(ProgramNode node) {
            List<BlockPos> blocks = blockPosListInput(node, "blocks");
            SpatialRegion region = regionInput(node, "region");
            List<BlockPos> filtered = blocks.stream()
                    .filter(blockPos -> region.contains(Vec3.atCenterOf(blockPos)))
                    .toList();
            if (filtered.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_blocks");
            }
            return filtered;
        }

        private Vec3 averagePosition(ProgramNode node) {
            List<Vec3> positions = vec3ListInput(node, "positions");
            if (positions.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_positions");
            }

            Vec3 total = Vec3.ZERO;
            for (Vec3 position : positions) {
                total = total.add(position);
            }
            return total.scale(1.0D / positions.size());
        }

        private Object emitAnchorRedstone(ProgramNode node) {
            if (anchorPos == null || !(level.getBlockEntity(anchorPos) instanceof RuneAnchorBlockEntity anchor)) {
                throw new ProgramExecutionException("block.mathmod.rune_anchor.execute_requires_anchor");
            }
            int power = Math.max(0, Math.min(15, (int) Math.round(numberInput(node, "power"))));
            double seconds = boundedNumber(
                    numberInput(node, "duration"),
                    0.05D,
                    ProgramExecutionPolicy.MAX_ANCHOR_SIGNAL_DURATION_TICKS / 20.0D
            );
            int durationTicks = Math.max(1, Math.min(
                    ProgramExecutionPolicy.MAX_ANCHOR_SIGNAL_DURATION_TICKS,
                    (int) Math.round(seconds * 20.0D)
            ));
            anchor.activateSignal(level, power, durationTicks);
            return Unit.INSTANCE;
        }

        private Object dimensionalSurvey() {
            if (anchorPos == null || !(level.getBlockEntity(anchorPos) instanceof RuneAnchorBlockEntity anchor)) {
                throw new ProgramExecutionException("block.mathmod.rune_anchor.execute_requires_anchor");
            }
            List<SurveyChannel> channels = List.of(
                    new SurveyChannel(BuiltInFieldProviders.ENVIRONMENTAL_SPATIAL, NamespacedId.of("mathmod", "spatial")),
                    new SurveyChannel(BuiltInFieldProviders.ENVIRONMENTAL_STABILITY, NamespacedId.of("mathmod", "stability")),
                    new SurveyChannel(BuiltInFieldProviders.ENVIRONMENTAL_VITALITY, NamespacedId.of("mathmod", "vitality"))
            );
            SamplePoint anchorPoint = new SamplePoint(origin.x, origin.y, origin.z);
            FieldSamplingContext fieldContext = new FieldSamplingContext(level, anchorPoint);
            double strongest = -1.0D;
            double normalizedMaximum = 0.0D;
            NamespacedId dominant = null;
            Map<NamespacedId, EnvironmentalSampleReport.Intensity> intensities = new java.util.LinkedHashMap<>();
            for (SurveyChannel channel : channels) {
                SamplePlanResult result = new SamplePlanner(fieldProviders.definitions()).planField(
                        com.mathmod.field.CalculusOperator.GRADIENT,
                        channel.providerId(), anchorPoint, anchorPoint, 1.0D, 1,
                        sample -> level.hasChunkAt(BlockPos.containing(sample.x(), sample.y(), sample.z()))
                );
                if (!result.valid()) {
                    throw new ProgramExecutionException("block.mathmod.rune_anchor.execute_field_invalid");
                }
                FieldSampler sampler = fieldProviders.runtime()
                        .sampler(channel.providerId(), fieldContext)
                        .orElseThrow(() -> new ProgramExecutionException("block.mathmod.rune_anchor.execute_field_invalid"));
                try {
                    List<Double> samples = new ArrayList<>(6);
                    for (com.mathmod.field.SampleRequest request : result.plan().orElseThrow().worldSamples()) {
                        FieldSampleValue value = fieldSampleCache.sample(request.providerId(), request.point(), sampler);
                        if (!(value instanceof FieldSampleValue.Scalar scalar)) {
                            throw new ProgramExecutionException("block.mathmod.rune_anchor.execute_field_invalid");
                        }
                        samples.add(scalar.value());
                    }
                    com.mathmod.field.FieldVector gradient = FieldCalculus.centeredGradient(
                            samples.get(0), samples.get(1), samples.get(2), samples.get(3), samples.get(4), samples.get(5), 1.0D
                    );
                    double magnitude = Math.sqrt(gradient.x() * gradient.x()
                            + gradient.y() * gradient.y() + gradient.z() * gradient.z());
                    double scale = fieldContext.environmentalSession().snapshot().channel(channel.channelId())
                            .orElseThrow(() -> new ProgramExecutionException("block.mathmod.rune_anchor.execute_field_invalid"))
                            .reportScale();
                    strongest = Math.max(strongest, magnitude);
                    double normalized = Math.min(1.0D, magnitude / scale);
                    intensities.put(channel.channelId(), EnvironmentalSampleReport.Intensity.fromNormalized(normalized));
                    if (normalized > normalizedMaximum || (normalized == normalizedMaximum
                            && (dominant == null || channel.channelId().compareTo(dominant) < 0))) {
                        normalizedMaximum = normalized;
                        dominant = channel.channelId();
                    }
                } catch (FieldSampleException | IllegalArgumentException exception) {
                    throw new ProgramExecutionException("block.mathmod.rune_anchor.execute_field_invalid");
                }
            }
            if (!Double.isFinite(strongest) || !Double.isFinite(normalizedMaximum)) {
                throw new ProgramExecutionException("block.mathmod.rune_anchor.execute_field_invalid");
            }
            int signal = Math.max(0, Math.min(15, (int) Math.floor(15.0D * normalizedMaximum + 0.5D)));
            anchor.activateSignal(level, signal, 200);
            anchor.setEnvironmentalReport(new EnvironmentalSampleReport(
                    fieldContext.environmentalSession().generation(), 18, signal,
                    java.util.Objects.requireNonNull(dominant, "dominant"), intensities
            ));
            return Unit.INSTANCE;
        }

        private EffectPlan pushEntitiesPlan(ProgramNode node) {
            List<Entity> targets = entityListInput(node, "entities");
            Vec3 vector = vectorInput(node, "vector");
            if (targets.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
            }
            if (vector.length() > ProgramExecutionPolicy.MAX_ENTITY_LIST_PUSH_LENGTH) {
                vector = vector.normalize().scale(ProgramExecutionPolicy.MAX_ENTITY_LIST_PUSH_LENGTH);
            }
            return new EntityPushEffectPlan(targets, vector);
        }

        private EffectPlan healEntitiesPlan(ProgramNode node) {
            List<Entity> targets = entityListInput(node, "entities");
            double amount = boundedNumber(
                    numberInput(node, "amount"),
                    0.0D,
                    ProgramExecutionPolicy.MAX_HEAL_AMOUNT
            );
            if (targets.isEmpty() || amount <= 0.0D) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
            }
            return new EntityHealEffectPlan(targets, (float) amount);
        }

        private EffectPlan cleanseEntitiesPlan(ProgramNode node) {
            return new CleansingEffectPlan(defensiveTarget(entityListInput(node, "entities")));
        }

        private EffectPlan defensiveStatusEffectPlan(ProgramNode node, Holder<MobEffect> effect, ParticleOptions particles) {
            int duration = effectDurationTicks(node, ProgramExecutionPolicy.MAX_DEFENSIVE_EFFECT_DURATION_TICKS);
            int amplifier = defensiveEffectAmplifier(node);
            return new DefensiveStatusEffectPlan(defensiveTarget(entityListInput(node, "entities")), effect, duration, amplifier, particles);
        }

        private DefensiveTarget defensiveTarget(List<Entity> targets) {
            if (caster == null || targets.size() != 1 || !(targets.getFirst() instanceof LivingEntity livingEntity)) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_p9_bad_target");
            }
            if (livingEntity instanceof ServerPlayer player && !player.getUUID().equals(caster.getUUID())) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_p9_bad_target");
            }
            return new DefensiveTarget(livingEntity.getUUID(), caster.getUUID());
        }

        private EffectPlan statusEffectPlan(
                ProgramNode node,
                Holder<MobEffect> effect,
                int maxDurationTicks,
                boolean affectPlayers,
                ParticleOptions particles
        ) {
            List<Entity> targets = entityListInput(node, "entities");
            int duration = effectDurationTicks(node, maxDurationTicks);
            int amplifier = effectAmplifier(node);
            if (targets.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
            }
            return new EntityStatusEffectPlan(targets, effect, duration, amplifier, affectPlayers, particles);
        }

        private EffectPlan soulBindEntitiesPlan(ProgramNode node) {
            List<Entity> targets = entityListInput(node, "entities");
            Vec3 anchor = finiteVector(vectorInput(node, "anchor"));
            int duration = effectDurationTicks(node, ProgramExecutionPolicy.MAX_HARMFUL_EFFECT_DURATION_TICKS);
            if (targets.isEmpty()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
            }
            return new SoulBindingEffectPlan(targets, anchor, duration);
        }

        private int effectDurationTicks(ProgramNode node, int maxDurationTicks) {
            double seconds = boundedNumber(numberInput(node, "duration"), 0.05D, maxDurationTicks / 20.0D);
            return Math.max(1, Math.min(maxDurationTicks, (int) Math.round(seconds * 20.0D)));
        }

        private int effectAmplifier(ProgramNode node) {
            double level = boundedNumber(
                    numberInput(node, "level"),
                    1.0D,
                    ProgramExecutionPolicy.MAX_EFFECT_AMPLIFIER + 1.0D
            );
            return Math.max(0, Math.min(
                    ProgramExecutionPolicy.MAX_EFFECT_AMPLIFIER,
                    (int) Math.round(level) - 1
            ));
        }

        private int defensiveEffectAmplifier(ProgramNode node) {
            double level = boundedNumber(
                    numberInput(node, "level"),
                    1.0D,
                    ProgramExecutionPolicy.MAX_DEFENSIVE_EFFECT_AMPLIFIER + 1.0D
            );
            return Math.max(0, Math.min(
                    ProgramExecutionPolicy.MAX_DEFENSIVE_EFFECT_AMPLIFIER,
                    (int) Math.round(level) - 1
            ));
        }

        private Object executeEffectPlan(ProgramNode node) {
            effectPlanInput(node, "plan").execute(level);
            return Unit.INSTANCE;
        }

        private boolean matchesTargetPredicate(LivingEntity entity, String predicate) {
            if (!entity.isAlive() || entity == caster) {
                return false;
            }

            return switch (predicate.trim().toLowerCase(Locale.ROOT)) {
                case "living", "any_living" -> true;
                case "non_player_living" -> !(entity instanceof ServerPlayer);
                case "hostile" -> entity instanceof Monster;
                case "players" -> entity instanceof ServerPlayer;
                default -> throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_predicate");
            };
        }

        private static List<Entity> sortedAndLimited(List<? extends Entity> entities, Vec3 origin, int limit) {
            return sortedAndLimited(entities, origin, limit, true);
        }

        private static List<Entity> sortedAndLimited(List<? extends Entity> entities, Vec3 origin, int limit, boolean nearestFirst) {
            Comparator<Entity> comparator = Comparator
                    .comparingDouble((Entity entity) -> entity.position().distanceToSqr(origin));
            if (!nearestFirst) {
                comparator = comparator.reversed();
            }
            return entities.stream()
                    .sorted(comparator.thenComparing(entity -> entity.getUUID().toString()))
                    .limit(limit)
                    .map(entity -> (Entity) entity)
                    .toList();
        }

        private static List<BlockPos> sortedBlockPositions(List<BlockPos> blockPositions, Vec3 origin, int limit) {
            return blockPositions.stream()
                    .sorted(Comparator
                            .comparingDouble((BlockPos blockPos) -> Vec3.atCenterOf(blockPos).distanceToSqr(origin))
                            .thenComparingInt(BlockPos::getX)
                            .thenComparingInt(BlockPos::getY)
                            .thenComparingInt(BlockPos::getZ))
                    .limit(limit)
                    .toList();
        }

        private boolean matchesBlockSelector(BlockState state, String selector) {
            String normalized = selector.trim().toLowerCase(Locale.ROOT);
            if (normalized.equals("any") || normalized.equals("not_air")) {
                return !state.isAir();
            }
            if (normalized.equals("air")) {
                return state.isAir();
            }
            try {
                BlockSelectors.validate(selector);
                return BlockSelectors.matches(state, selector);
            } catch (IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_block");
            }
        }

        private String itemSelectorConstant(ProgramNode node, String constantName, String defaultValue) {
            String value = node.constants().getOrDefault(constantName, defaultValue);
            if (value == null || value.isBlank()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_item");
            }
            return value;
        }

        private void validateItemSelector(String selector) {
            try {
                ItemSelectors.validate(selector);
            } catch (IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_item");
            }
        }

        private Item itemConstant(ProgramNode node, String constantName, String defaultValue) {
            String value = node.constants().getOrDefault(constantName, defaultValue);
            try {
                return ItemSelectors.exactItem(value);
            } catch (IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_item");
            }
        }

        private int intConstant(ProgramNode node, String constantName, int defaultValue, int min, int max) {
            String value = node.constants().getOrDefault(constantName, Integer.toString(defaultValue));
            try {
                int parsed = Integer.parseInt(value);
                return Math.max(min, Math.min(max, parsed));
            } catch (NumberFormatException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_constant");
            }
        }

        private double doubleConstant(ProgramNode node, String constantName, double defaultValue) {
            String value = node.constants().getOrDefault(constantName, Double.toString(defaultValue));
            try {
                double parsed = Double.parseDouble(value);
                if (!Double.isFinite(parsed)) {
                    throw new NumberFormatException("Non-finite number");
                }
                return parsed;
            } catch (NumberFormatException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_constant");
            }
        }

        private String stringConstant(ProgramNode node, String constantName, String defaultValue) {
            String value = node.constants().getOrDefault(constantName, defaultValue);
            if (value == null || value.isBlank()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_constant");
            }
            return value.trim();
        }

        private BlockHitResult raycastBlock(ProgramNode node) {
            ServerPlayer player = playerInput(node, "player");
            double range = clamp(numberInput(node, "range"), 0.0D, ProgramExecutionPolicy.MAX_RAYCAST_RANGE);
            Vec3 start = player.getEyePosition();
            Vec3 end = start.add(player.getLookAngle().normalize().scale(range));
            return level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        }

        private Vec3 rayHitPosition(ProgramNode node) {
            Object value = input(node, "hit");
            if (value instanceof BlockHitResult hitResult) {
                if (hitResult.getType() == HitResult.Type.MISS) {
                    throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_miss");
                }
                return hitResult.getLocation();
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private Object blinkSelfToHit(ProgramNode node) {
            ServerPlayer player = playerInput(node, "player");
            Object value = input(node, "hit");
            if (!(value instanceof BlockHitResult hitResult)) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
            }
            if (hitResult.getType() == HitResult.Type.MISS) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_miss");
            }

            BlockPos targetBlock = hitResult.getBlockPos().relative(hitResult.getDirection());
            Vec3 target = Vec3.atBottomCenterOf(targetBlock);
            if (target.distanceTo(player.position()) > ProgramExecutionPolicy.MAX_BLINK_RANGE + 1.0D) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_miss");
            }
            if (!level.noCollision(player, player.getDimensions(player.getPose()).makeBoundingBox(target))) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_no_space");
            }

            Vec3 start = player.position();
            level.sendParticles(ParticleTypes.PORTAL, start.x, start.y + 1.0D, start.z, 24, 0.25D, 0.45D, 0.25D, 0.05D);
            player.teleportTo(target.x, target.y, target.z);
            player.resetFallDistance();
            level.sendParticles(ParticleTypes.PORTAL, target.x, target.y + 1.0D, target.z, 32, 0.25D, 0.45D, 0.25D, 0.05D);
            level.playSound(null, target.x, target.y, target.z, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.8F, 1.0F);
            return Unit.INSTANCE;
        }

        private Object pushSelf(ProgramNode node) {
            ServerPlayer player = playerInput(node, "player");
            Vec3 vector = vectorInput(node, "vector");
            if (vector.length() > ProgramExecutionPolicy.MAX_PUSH_LENGTH) {
                vector = vector.normalize().scale(ProgramExecutionPolicy.MAX_PUSH_LENGTH);
            }
            player.push(vector);
            player.hurtMarked = true;
            player.hasImpulse = true;
            player.resetFallDistance();
            return Unit.INSTANCE;
        }

        private Object debugMarker(ProgramNode node) {
            Vec3 position = vectorInput(node, "position");
            level.sendParticles(
                    ParticleTypes.END_ROD,
                    position.x,
                    position.y + 0.2D,
                    position.z,
                    24,
                    0.35D,
                    0.35D,
                    0.35D,
                    0.015D
            );
            level.playSound(
                    null,
                    position.x,
                    position.y,
                    position.z,
                    SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.BLOCKS,
                    0.7F,
                    1.25F
            );
            return Unit.INSTANCE;
        }

        private double numberInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof Number number) {
                return number.doubleValue();
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private boolean booleanInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof Boolean bool) {
                return bool;
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private Vec3 vectorInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof Vec3 vector) {
                return vector;
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private ServerPlayer playerInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof ServerPlayer player) {
                return player;
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private List<Entity> entityListInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof List<?> list && list.stream().allMatch(Entity.class::isInstance)) {
                List<Entity> entities = new ArrayList<>();
                for (Object entry : list) {
                    entities.add((Entity) entry);
                }
                return List.copyOf(entities);
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private List<BlockPos> blockPosListInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof List<?> list && list.stream().allMatch(BlockPos.class::isInstance)) {
                List<BlockPos> blockPositions = new ArrayList<>();
                for (Object entry : list) {
                    blockPositions.add((BlockPos) entry);
                }
                return List.copyOf(blockPositions);
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private List<Vec3> vec3ListInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof List<?> list && list.stream().allMatch(Vec3.class::isInstance)) {
                List<Vec3> positions = new ArrayList<>();
                for (Object entry : list) {
                    positions.add((Vec3) entry);
                }
                return List.copyOf(positions);
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private List<Double> numberListInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof List<?> list && list.stream().allMatch(Number.class::isInstance)) {
                List<Double> numbers = new ArrayList<>();
                for (Object entry : list) {
                    numbers.add(((Number) entry).doubleValue());
                }
                return List.copyOf(numbers);
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private EffectPlan effectPlanInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof EffectPlan plan) {
                return plan;
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private SpatialRegion regionInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof SpatialRegion region) {
                return region;
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private CoordinateFrame frameInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof CoordinateFrame frame) {
                return frame;
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private CyclicGroupElement cyclicElementInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof CyclicGroupElement element) {
                return element;
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private Object input(ProgramNode node, String inputName) {
            for (ProgramEdge edge : graph.edges()) {
                if (edge.toNodeId().equals(node.id()) && edge.inputName().equals(inputName)) {
                    return evaluate(edge.fromNodeId());
                }
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_missing_input");
        }

        private Vec3 fieldGradient(ProgramNode node) {
            ScalarFieldValue field = scalarFieldInput(node, "field");
            Vec3 point = vectorInput(node, "point");
            double step = numberInput(node, "step");
            SamplePoint originPoint = new SamplePoint(origin.x, origin.y, origin.z);
            SamplePoint targetPoint = new SamplePoint(point.x, point.y, point.z);
            SamplePlanResult result = new SamplePlanner(fieldProviders.definitions()).planField(
                    com.mathmod.field.CalculusOperator.GRADIENT,
                    field.providerId(),
                    originPoint,
                    targetPoint,
                    step,
                    1,
                    sample -> level.hasChunkAt(BlockPos.containing(sample.x(), sample.y(), sample.z()))
            );
            if (!result.valid()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_field_invalid");
            }
            SamplePlan plan = result.plan().orElseThrow();
            FieldSampler sampler = fieldProviders.runtime()
                    .sampler(field.providerId(), new FieldSamplingContext(level, originPoint))
                    .orElseThrow(() -> new ProgramExecutionException("item.mathmod.programmed_talisman.execute_field_invalid"));
            try {
                List<Double> samples = new ArrayList<>(6);
                for (com.mathmod.field.SampleRequest request : plan.worldSamples()) {
                    FieldSampleValue value = fieldSampleCache.sample(request.providerId(), request.point(), sampler);
                    if (!(value instanceof FieldSampleValue.Scalar scalar)) {
                        throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_field_invalid");
                    }
                    samples.add(scalar.value());
                }
                com.mathmod.field.FieldVector gradient = FieldCalculus.centeredGradient(
                        samples.get(0), samples.get(1), samples.get(2),
                        samples.get(3), samples.get(4), samples.get(5), step
                );
                return new Vec3(gradient.x(), gradient.y(), gradient.z());
            } catch (FieldSampleException | IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_field_invalid");
            }
        }

        private ScalarFieldValue projectEnvironmentalChannel(ProgramNode node) {
            attributeFieldInput(node, "field");
            String encoded = node.constants().get("channel");
            if (encoded == null || encoded.isBlank()) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_field_invalid");
            }
            try {
                NamespacedId channel = EnvironmentalFieldServices.resolveChannel(NamespacedId.parse(encoded));
                if (EnvironmentalFieldServices.snapshot().channel(channel).isEmpty()) {
                    throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_field_invalid");
                }
                NamespacedId provider = BuiltInFieldProviders.providerForEnvironmentalChannel(channel);
                if (fieldProviders.definitions().find(provider).isEmpty()) {
                    throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_field_invalid");
                }
                return new ScalarFieldValue(provider);
            } catch (IllegalArgumentException exception) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_field_invalid");
            }
        }

        private ScalarFieldValue scalarFieldInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof ScalarFieldValue field) {
                return field;
            }
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private AttributeFieldValue attributeFieldInput(ProgramNode node, String inputName) {
            Object value = input(node, inputName);
            if (value instanceof AttributeFieldValue field) return field;
            throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_type");
        }

        private static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }

        private static double boundedNumber(double value, double min, double max) {
            if (!Double.isFinite(value)) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_constant");
            }
            return clamp(value, min, max);
        }

        private static Vec3 finiteVector(Vec3 vector) {
            if (!Double.isFinite(vector.x) || !Double.isFinite(vector.y) || !Double.isFinite(vector.z)) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_constant");
            }
            return vector;
        }

        private static void ensureFiniteBounds(AABB bounds) {
            if (!Double.isFinite(bounds.minX)
                    || !Double.isFinite(bounds.minY)
                    || !Double.isFinite(bounds.minZ)
                    || !Double.isFinite(bounds.maxX)
                    || !Double.isFinite(bounds.maxY)
                    || !Double.isFinite(bounds.maxZ)) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_bad_constant");
            }
        }

        private interface EffectPlan {
            void execute(ServerLevel level);
        }

        private record EntityPushEffectPlan(List<Entity> targets, Vec3 vector) implements EffectPlan {
            @Override
            public void execute(ServerLevel level) {
                int affected = 0;
                for (Entity entity : targets) {
                    if (!(entity instanceof LivingEntity livingEntity) || !livingEntity.isAlive()) {
                        continue;
                    }
                    livingEntity.push(vector.x, vector.y, vector.z);
                    livingEntity.hasImpulse = true;
                    if (livingEntity instanceof ServerPlayer player) {
                        player.hurtMarked = true;
                    }
                    level.sendParticles(
                            ParticleTypes.END_ROD,
                            livingEntity.getX(),
                            livingEntity.getY() + 0.6D,
                            livingEntity.getZ(),
                            8,
                            0.2D,
                            0.2D,
                            0.2D,
                            0.015D
                    );
                    affected++;
                }

                if (affected == 0) {
                    throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
                }
                if (!targets.isEmpty()) {
                    Entity first = targets.get(0);
                    level.playSound(
                            null,
                            first.getX(),
                            first.getY(),
                            first.getZ(),
                            SoundEvents.AMETHYST_BLOCK_CHIME,
                            SoundSource.PLAYERS,
                            0.7F,
                            1.4F
                    );
                }
            }
        }

        private record EntityHealEffectPlan(List<Entity> targets, float amount) implements EffectPlan {
            @Override
            public void execute(ServerLevel level) {
                int affected = 0;
                for (Entity entity : targets) {
                    if (!(entity instanceof LivingEntity livingEntity) || !livingEntity.isAlive()) {
                        continue;
                    }
                    float before = livingEntity.getHealth();
                    livingEntity.heal(amount);
                    if (livingEntity.getHealth() <= before) {
                        continue;
                    }
                    level.sendParticles(
                            ParticleTypes.HEART,
                            livingEntity.getX(),
                            livingEntity.getY() + livingEntity.getBbHeight() * 0.65D,
                            livingEntity.getZ(),
                            6,
                            0.25D,
                            0.25D,
                            0.25D,
                            0.02D
                    );
                    affected++;
                }
                requireAffected(affected);
            }
        }

        private record EntityStatusEffectPlan(
                List<Entity> targets,
                Holder<MobEffect> effect,
                int duration,
                int amplifier,
                boolean affectPlayers,
                ParticleOptions particles
        ) implements EffectPlan {
            @Override
            public void execute(ServerLevel level) {
                int affected = 0;
                for (Entity entity : targets) {
                    if (!(entity instanceof LivingEntity livingEntity)
                            || !livingEntity.isAlive()
                            || (!affectPlayers && livingEntity instanceof ServerPlayer)) {
                        continue;
                    }
                    livingEntity.addEffect(new MobEffectInstance(
                            effect,
                            duration,
                            amplifier,
                            false,
                            true,
                            true
                    ));
                    level.sendParticles(
                            particles,
                            livingEntity.getX(),
                            livingEntity.getY() + livingEntity.getBbHeight() * 0.6D,
                            livingEntity.getZ(),
                            8,
                            0.25D,
                            0.3D,
                            0.25D,
                            0.02D
                    );
                    affected++;
                }
                requireAffected(affected);
            }
        }

        private record DefensiveTarget(java.util.UUID entityId, java.util.UUID casterId) {
            private LivingEntity resolve(ServerLevel level) {
                Entity entity = level.getEntity(entityId);
                if (!(entity instanceof LivingEntity livingEntity) || !livingEntity.isAlive()) {
                    return null;
                }
                if (livingEntity instanceof ServerPlayer player && !player.getUUID().equals(casterId)) {
                    return null;
                }
                return livingEntity;
            }
        }

        private record CleansingEffectPlan(DefensiveTarget target) implements EffectPlan {
            @Override
            public void execute(ServerLevel level) {
                LivingEntity livingEntity = target.resolve(level);
                if (livingEntity == null) {
                    throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_p9_bad_target");
                }
                int removed = 0;
                for (Holder<MobEffect> effect : List.of(MobEffects.POISON, MobEffects.WITHER, MobEffects.WEAKNESS)) {
                    if (livingEntity.removeEffect(effect)) {
                        removed++;
                    }
                }
                if (removed == 0) {
                    throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_p9_no_effect");
                }
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() * 0.6D, livingEntity.getZ(),
                        10, 0.25D, 0.3D, 0.25D, 0.02D);
                level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                        SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7F, 1.65F);
            }
        }

        private record DefensiveStatusEffectPlan(
                DefensiveTarget target,
                Holder<MobEffect> effect,
                int duration,
                int amplifier,
                ParticleOptions particles
        ) implements EffectPlan {
            @Override
            public void execute(ServerLevel level) {
                LivingEntity livingEntity = target.resolve(level);
                if (livingEntity == null) {
                    throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_p9_bad_target");
                }
                MobEffectInstance existing = livingEntity.getEffect(effect);
                if (existing != null && existing.getAmplifier() > amplifier) {
                    throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_p9_no_effect");
                }
                int appliedAmplifier = existing == null ? amplifier : Math.max(existing.getAmplifier(), amplifier);
                int appliedDuration = existing == null ? duration : Math.max(existing.getDuration(), duration);
                if (existing != null && existing.getAmplifier() == appliedAmplifier && existing.getDuration() >= appliedDuration) {
                    throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_p9_no_effect");
                }
                livingEntity.addEffect(new MobEffectInstance(effect, appliedDuration, appliedAmplifier, false, true, true));
                level.sendParticles(particles,
                        livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() * 0.6D, livingEntity.getZ(),
                        10, 0.25D, 0.3D, 0.25D, 0.02D);
            }
        }

        private record SoulBindingEffectPlan(List<Entity> targets, Vec3 anchor, int duration) implements EffectPlan {
            @Override
            public void execute(ServerLevel level) {
                int affected = 0;
                for (Entity entity : targets) {
                    if (!(entity instanceof LivingEntity livingEntity)
                            || !livingEntity.isAlive()
                            || livingEntity instanceof ServerPlayer) {
                        continue;
                    }
                    SoulBoundEffect.bindTo(livingEntity, anchor);
                    livingEntity.addEffect(new MobEffectInstance(
                            ModMobEffects.SOUL_BOUND,
                            duration,
                            0,
                            false,
                            true,
                            true
                    ));
                    level.sendParticles(
                            ParticleTypes.SOUL_FIRE_FLAME,
                            livingEntity.getX(),
                            livingEntity.getY() + livingEntity.getBbHeight() * 0.5D,
                            livingEntity.getZ(),
                            10,
                            0.3D,
                            0.35D,
                            0.3D,
                            0.015D
                    );
                    affected++;
                }
                requireAffected(affected);
            }
        }

        private record CompositeEffectPlan(List<EffectPlan> plans) implements EffectPlan {
            @Override
            public void execute(ServerLevel level) {
                for (EffectPlan plan : plans) {
                    plan.execute(level);
                }
            }
        }

        private static void requireAffected(int affected) {
            if (affected == 0) {
                throw new ProgramExecutionException("item.mathmod.programmed_talisman.execute_empty_targets");
            }
        }

        private record ScalarFieldValue(com.mathmod.util.NamespacedId providerId) { }

        private enum AttributeFieldValue { INSTANCE }

        private record SurveyChannel(NamespacedId providerId, NamespacedId channelId) { }
    }

    private enum Unit {
        INSTANCE
    }

    private static final class ProgramExecutionException extends RuntimeException {
        private final String messageKey;

        private ProgramExecutionException(String messageKey) {
            this.messageKey = messageKey;
        }

        private String messageKey() {
            return messageKey;
        }
    }
}
