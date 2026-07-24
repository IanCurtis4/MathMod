package com.mathmod.program;

public final class AnchorPresetConfig {
    public static final String DEFAULT_SACRIFICE_SELECTOR =
            "minecraft:amethyst_shard,#c:gems/amethyst,#forge:gems/amethyst";
    public static final int DEFAULT_SACRIFICE_COUNT = 1;
    public static final double DEFAULT_SACRIFICE_RADIUS = 2.5D;
    public static final String DEFAULT_OFFERING_ITEM = "minecraft:glowstone_dust";
    public static final int DEFAULT_OFFERING_COUNT = 1;
    public static final double DEFAULT_WARD_RADIUS = 4.0D;
    public static final double DEFAULT_WARD_STRENGTH = 0.8D;

    private static String sacrificeSelector = DEFAULT_SACRIFICE_SELECTOR;
    private static int sacrificeCount = DEFAULT_SACRIFICE_COUNT;
    private static double sacrificeRadius = DEFAULT_SACRIFICE_RADIUS;
    private static String offeringItem = DEFAULT_OFFERING_ITEM;
    private static int offeringCount = DEFAULT_OFFERING_COUNT;
    private static double wardRadius = DEFAULT_WARD_RADIUS;
    private static double wardStrength = DEFAULT_WARD_STRENGTH;

    private AnchorPresetConfig() {
    }

    public static synchronized String sacrificeSelector() {
        return sacrificeSelector;
    }

    public static synchronized int sacrificeCount() {
        return sacrificeCount;
    }

    public static synchronized double sacrificeRadius() {
        return sacrificeRadius;
    }

    public static synchronized String offeringItem() {
        return offeringItem;
    }

    public static synchronized int offeringCount() {
        return offeringCount;
    }

    public static synchronized double wardRadius() {
        return wardRadius;
    }

    public static synchronized double wardStrength() {
        return wardStrength;
    }

    public static synchronized void setAnchorSacrifice(String selector, int count, double radius) {
        sacrificeSelector = requireNonBlank(selector, "sacrifice selector");
        sacrificeCount = requireRange(count, 1, 64, "sacrifice count");
        sacrificeRadius = requireRange(radius, 0.25D, ProgramExecutionPolicy.MAX_SACRIFICE_RADIUS, "sacrifice radius");
    }

    public static synchronized void setOfferingSparkDrop(String itemId, int count) {
        offeringItem = requireNonBlank(itemId, "offering item");
        offeringCount = requireRange(count, 1, ProgramExecutionPolicy.MAX_SPAWNED_ITEM_COUNT, "offering count");
    }

    public static synchronized void setWardingPulse(double radius, double strength) {
        wardRadius = requireRange(radius, 0.5D, ProgramExecutionPolicy.MAX_ENTITY_PULSE_RADIUS, "ward radius");
        wardStrength = requireRange(strength, 0.0D, ProgramExecutionPolicy.MAX_ENTITY_PULSE_STRENGTH, "ward strength");
    }

    public static synchronized void resetForTests() {
        sacrificeSelector = DEFAULT_SACRIFICE_SELECTOR;
        sacrificeCount = DEFAULT_SACRIFICE_COUNT;
        sacrificeRadius = DEFAULT_SACRIFICE_RADIUS;
        offeringItem = DEFAULT_OFFERING_ITEM;
        offeringCount = DEFAULT_OFFERING_COUNT;
        wardRadius = DEFAULT_WARD_RADIUS;
        wardStrength = DEFAULT_WARD_STRENGTH;
    }

    private static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }

    private static int requireRange(int value, int min, int max, String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
        }
        return value;
    }

    private static double requireRange(double value, double min, double max, String name) {
        if (!Double.isFinite(value) || value < min || value > max) {
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
        }
        return value;
    }
}
