package com.mathmod.physics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Immutable declarations plus a snapshot-local, server-thread-owned LRU cache. */
public final class PhysicalProfileSnapshot {
    private static final int CACHE_CAPACITY = 32_768;
    private final long version;
    private final PhysicsPolicy policy;
    private final List<PhysicalProfileDeclaration> declarations;
    private final Map<String, BlockPhysicalProfile> cache = new LinkedHashMap<>(16, .75F, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<String, BlockPhysicalProfile> eldest) {
            return size() > CACHE_CAPACITY;
        }
    };

    public PhysicalProfileSnapshot(long version, PhysicsPolicy policy, List<PhysicalProfileDeclaration> declarations) {
        if (version < 0 || policy == null || declarations == null || declarations.size() > 4096) {
            throw new IllegalArgumentException("Invalid physical profile snapshot");
        }
        this.version = version;
        this.policy = policy;
        this.declarations = declarations.stream().sorted(Comparator.comparing(PhysicalProfileDeclaration::id)).toList();
        validateExactSelectorConflicts(this.declarations);
    }

    public long version() { return version; }
    public PhysicsPolicy policy() { return policy; }
    public List<PhysicalProfileDeclaration> declarations() { return declarations; }
    public int cacheSize() { return cache.size(); }

    public BlockPhysicalProfile resolve(BlockPhysicalInput input) {
        BlockPhysicalProfile cached = cache.get(input.cacheKey());
        if (cached != null) return cached;
        BlockPhysicalProfile resolved = resolveUncached(input);
        cache.put(input.cacheKey(), resolved);
        return resolved;
    }

    private BlockPhysicalProfile resolveUncached(BlockPhysicalInput input) {
        double volume = VoxelShapeVolume.sampledUnion(input.canonicalCollisionBoxes(), policy.shapeResolution());
        Optional<PhysicalProfileDeclaration> declaration = select(input);
        if (declaration.isEmpty()) return fallback(volume, input);
        PhysicalProfileDeclaration value = declaration.orElseThrow();
        double compactness = compactness(input);
        return new BlockPhysicalProfile(
                value.density(), volume, clamp(volume * value.density() * compactness, 0, 256),
                value.structuralStrength() == null ? policy.defaultStructuralStrength() : value.structuralStrength(),
                value.brittleness() == null ? policy.defaultBrittleness() : value.brittleness(),
                value.elasticity() == null ? policy.defaultElasticity() : value.elasticity(),
                value.thermalResistance() == null ? policy.defaultThermalResistance() : value.thermalResistance(),
                value.magicalResistance() == null ? policy.defaultMagicalResistance() : value.magicalResistance(),
                value.compressionMassExponent() == null ? policy.defaultCompressionMassExponent() : value.compressionMassExponent(),
                value.source()
        );
    }

    private BlockPhysicalProfile fallback(double volume, BlockPhysicalInput input) {
        boolean propertiesAvailable = usableProperty(input.hardness()) && usableProperty(input.blastResistance());
        double density = propertiesAvailable
                ? clamp(policy.fallbackBaseMass()
                    + policy.fallbackHardnessWeight() * Math.log1p(input.hardness())
                    + policy.fallbackBlastWeight() * Math.log1p(input.blastResistance()), .01D, 64D)
                : policy.defaultDensity();
        return new BlockPhysicalProfile(density, volume, clamp(volume * density, 0, 256),
                policy.defaultStructuralStrength(), policy.defaultBrittleness(), policy.defaultElasticity(),
                policy.defaultThermalResistance(), policy.defaultMagicalResistance(),
                policy.defaultCompressionMassExponent(), PhysicalProfileSource.FALLBACK);
    }

    private double compactness(BlockPhysicalInput input) {
        return clamp(1 + policy.hardnessWeight() * Math.log1p(usable(input.hardness()))
                + policy.blastResistanceWeight() * Math.log1p(usable(input.blastResistance())), 1, 8);
    }

    private Optional<PhysicalProfileDeclaration> select(BlockPhysicalInput input) {
        List<PhysicalProfileDeclaration> exact = declarations.stream()
                .filter(value -> value.selector().kind() == PhysicalSelector.Kind.BLOCK && value.selector().matches(input)).toList();
        if (!exact.isEmpty()) return Optional.of(selectHighestLayer(exact, false));
        List<PhysicalProfileDeclaration> tags = declarations.stream()
                .filter(value -> value.selector().kind() == PhysicalSelector.Kind.TAG && value.selector().matches(input)).toList();
        return tags.isEmpty() ? Optional.empty() : Optional.of(selectHighestLayer(tags, true));
    }

    private static PhysicalProfileDeclaration selectHighestLayer(List<PhysicalProfileDeclaration> candidates, boolean tags) {
        int layer = candidates.stream().mapToInt(value -> value.source().precedence()).max().orElseThrow();
        List<PhysicalProfileDeclaration> inLayer = candidates.stream().filter(value -> value.source().precedence() == layer).toList();
        if (!tags) {
            if (inLayer.size() != 1) throw new IllegalStateException("duplicate_selector");
            return inLayer.getFirst();
        }
        int priority = inLayer.stream().mapToInt(PhysicalProfileDeclaration::priority).max().orElseThrow();
        List<PhysicalProfileDeclaration> winners = inLayer.stream().filter(value -> value.priority() == priority).toList();
        if (winners.size() != 1) throw new IllegalStateException("ambiguous_tag_match");
        return winners.getFirst();
    }

    private static void validateExactSelectorConflicts(List<PhysicalProfileDeclaration> declarations) {
        Map<String, List<PhysicalProfileDeclaration>> exact = new LinkedHashMap<>();
        declarations.stream().filter(value -> value.selector().kind() == PhysicalSelector.Kind.BLOCK)
                .forEach(value -> exact.computeIfAbsent(value.selector().id() + "@" + value.source(), ignored -> new ArrayList<>()).add(value));
        if (exact.values().stream().anyMatch(values -> values.size() > 1)) throw new IllegalArgumentException("duplicate_selector");
    }

    private static double usable(double value) { return usableProperty(value) ? value : 0; }
    private static boolean usableProperty(double value) { return Double.isFinite(value) && value >= 0; }
    private static double clamp(double value, double min, double max) { return Math.max(min, Math.min(max, value)); }
}
