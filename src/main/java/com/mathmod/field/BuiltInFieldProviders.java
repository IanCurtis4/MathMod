package com.mathmod.field;

import com.mathmod.environment.EnvironmentalFieldSnapshot;
import com.mathmod.environment.EnvironmentalFieldServices;
import com.mathmod.util.NamespacedId;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/** Built-in providers only expose bounded loaded-world observations. */
public final class BuiltInFieldProviders {
    public static final NamespacedId LIVING_DENSITY = NamespacedId.of("mathmod", "living_density_field");
    public static final NamespacedId ENVIRONMENTAL_SPATIAL = NamespacedId.of("mathmod", "environmental_spatial_field");
    public static final NamespacedId ENVIRONMENTAL_STABILITY = NamespacedId.of("mathmod", "environmental_stability_field");
    public static final NamespacedId ENVIRONMENTAL_VITALITY = NamespacedId.of("mathmod", "environmental_vitality_field");
    private static final double SAMPLE_RADIUS = 2.0D;
    private static final double SAMPLE_VOLUME = Math.pow(SAMPLE_RADIUS * 2.0D, 3.0D);

    private BuiltInFieldProviders() { }

    public static FieldProviderPublication publication() {
        return publication(EnvironmentalFieldServices.snapshot());
    }

    public static FieldProviderPublication publication(EnvironmentalFieldSnapshot environmentalSnapshot) {
        FieldProviderDefinition definition = new FieldProviderDefinition(
                LIVING_DENSITY, FieldValueKind.SCALAR, FieldQuantity.COUNT_PER_BLOCK, 8.0D, 2
        );
        List<FieldProviderDefinition> definitions = new java.util.ArrayList<>();
        definitions.add(definition);
        Map<NamespacedId, FieldSamplerFactory> samplers = new LinkedHashMap<>();
        samplers.put(LIVING_DENSITY, context -> point -> {
            if (!context.level().hasChunkAt(net.minecraft.core.BlockPos.containing(point.x(), point.y(), point.z()))) {
                throw new FieldSampleException(FieldPlanningIssue.Code.UNLOADED_SAMPLE, "Field sample chunk is not loaded");
            }
            AABB bounds = new AABB(
                    point.x() - SAMPLE_RADIUS, point.y() - SAMPLE_RADIUS, point.z() - SAMPLE_RADIUS,
                    point.x() + SAMPLE_RADIUS, point.y() + SAMPLE_RADIUS, point.z() + SAMPLE_RADIUS
            );
            int count = context.level().getEntitiesOfClass(LivingEntity.class, bounds,
                    entity -> entity.isAlive()).size();
            return new FieldSampleValue.Scalar(count / SAMPLE_VOLUME);
        });
        environmentalSnapshot.channels().forEach(channel -> registerEnvironmental(
                definitions, samplers, providerForEnvironmentalChannel(channel.id()), channel.id()
        ));
        return new FieldProviderPublication(
                FieldProviderSnapshot.of(definitions), new FieldProviderRuntimeRegistry(samplers)
        );
    }

    private static void registerEnvironmental(
            List<FieldProviderDefinition> definitions,
            Map<NamespacedId, FieldSamplerFactory> samplers,
            NamespacedId providerId,
            NamespacedId channel
    ) {
        definitions.add(new FieldProviderDefinition(providerId, FieldValueKind.SCALAR, FieldQuantity.CORRESPONDENCE, 8.0D, 3));
        samplers.put(providerId, context -> point -> {
            try {
                return new FieldSampleValue.Scalar(context.environmentalSession().sample(channel, point));
            } catch (IllegalArgumentException exception) {
                throw new FieldSampleException(FieldPlanningIssue.Code.PROVIDER_FAILURE, exception.getMessage());
            }
        });
    }

    public static NamespacedId providerForEnvironmentalChannel(NamespacedId channel) {
        return switch (channel.toString()) {
            case "mathmod:spatial" -> ENVIRONMENTAL_SPATIAL;
            case "mathmod:stability" -> ENVIRONMENTAL_STABILITY;
            case "mathmod:vitality" -> ENVIRONMENTAL_VITALITY;
            default -> NamespacedId.of("mathmod", "environmental/" + channel.namespace() + "/" + channel.path());
        };
    }
}
