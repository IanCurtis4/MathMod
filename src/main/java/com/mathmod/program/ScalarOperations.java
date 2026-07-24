package com.mathmod.program;

import com.mathmod.runes.RuneType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Authoritative bounded semantics for P1 scalar primitives.
 */
public final class ScalarOperations {
    public static final double EPSILON = 1.0E-8D;
    public static final double MAX_ABSOLUTE_SCALAR = 1_048_576.0D;

    private static final Map<String, Descriptor> DESCRIPTORS = Map.ofEntries(
            Map.entry("number_abs", number("mathmod:number_abs", "number_abs", 1, List.of("value"), 1, Map.of())),
            Map.entry("number_min", number("mathmod:number_min", "number_min", 2, List.of("a", "b"), 1, Map.of())),
            Map.entry("number_max", number("mathmod:number_max", "number_max", 2, List.of("a", "b"), 1, Map.of())),
            Map.entry("number_power", number("mathmod:number_power", "number_power", 2, List.of("base", "exponent"), 2, Map.of("precision", 1))),
            Map.entry("number_sqrt", number("mathmod:number_sqrt", "number_sqrt", 1, List.of("value"), 2, Map.of("precision", 1))),
            Map.entry("number_log", number("mathmod:number_log", "number_log", 2, List.of("value", "base"), 2, Map.of("precision", 1))),
            Map.entry("number_exp", number("mathmod:number_exp", "number_exp", 1, List.of("value"), 2, Map.of("precision", 1))),
            Map.entry("number_atan2", number("mathmod:number_atan2", "number_atan2", 2, List.of("y", "x"), 2, Map.of("precision", 1))),
            Map.entry("number_lerp", number("mathmod:number_lerp", "number_lerp", 3, List.of("a", "b", "t"), 2, Map.of("precision", 1))),
            Map.entry("number_at_least", new Descriptor("mathmod:number_at_least", "number_at_least",
                    List.of("value", "threshold"), RuneType.BOOL, 1, Map.of("information", 1))),
            Map.entry("number_select", number("mathmod:number_select", "number_select", 3,
                    List.of("condition", "when_true", "when_false"), 1, Map.of("precision", 1)))
    );

    private ScalarOperations() {
    }

    public static List<Descriptor> descriptors() {
        return DESCRIPTORS.values().stream().sorted(java.util.Comparator.comparing(Descriptor::executorKey)).toList();
    }

    public static Optional<Descriptor> descriptor(String executorKey) {
        return Optional.ofNullable(DESCRIPTORS.get(executorKey));
    }

    public static boolean isNonLinear(String executorKey) {
        return switch (executorKey) {
            case "number_power", "number_sqrt", "number_log", "number_exp", "number_atan2", "number_lerp" -> true;
            default -> false;
        };
    }

    public static double number(String executorKey, double... values) {
        for (double value : values) {
            requireFinite(value);
        }
        double result = switch (executorKey) {
            case "number_abs" -> Math.abs(values[0]);
            case "number_min" -> Math.min(values[0], values[1]);
            case "number_max" -> Math.max(values[0], values[1]);
            case "number_power" -> power(values[0], values[1]);
            case "number_sqrt" -> sqrt(values[0]);
            case "number_log" -> log(values[0], values[1]);
            case "number_exp" -> Math.exp(values[0]);
            case "number_atan2" -> atan2(values[0], values[1]);
            case "number_lerp" -> lerp(values[0], values[1], values[2]);
            case "number_select" -> values[0] != 0.0D ? values[1] : values[2];
            default -> throw new IllegalArgumentException("Unknown scalar executor " + executorKey);
        };
        return bounded(result);
    }

    public static boolean atLeast(double value, double threshold) {
        requireFinite(value);
        requireFinite(threshold);
        return value >= threshold;
    }

    public static double bounded(double value) {
        requireFinite(value);
        if (Math.abs(value) > MAX_ABSOLUTE_SCALAR) {
            throw new IllegalArgumentException("Scalar result exceeds bound");
        }
        return value;
    }

    private static double power(double base, double exponent) {
        if (Math.abs(base) < EPSILON && exponent < 0.0D) {
            throw new IllegalArgumentException("Zero cannot have a negative exponent");
        }
        if (base < 0.0D && Math.abs(exponent - Math.rint(exponent)) >= EPSILON) {
            throw new IllegalArgumentException("Negative base requires an integral exponent");
        }
        return Math.pow(base, exponent);
    }

    private static double sqrt(double value) {
        if (value < 0.0D) {
            throw new IllegalArgumentException("Square root requires a non-negative value");
        }
        return Math.sqrt(value);
    }

    private static double log(double value, double base) {
        if (value <= 0.0D || base <= 0.0D || Math.abs(base - 1.0D) < EPSILON) {
            throw new IllegalArgumentException("Logarithm requires value > 0 and base > 0 not equal to 1");
        }
        return Math.log(value) / Math.log(base);
    }

    private static double atan2(double y, double x) {
        if (Math.abs(y) < EPSILON && Math.abs(x) < EPSILON) {
            throw new IllegalArgumentException("atan2 is undefined at the origin");
        }
        return Math.atan2(y, x);
    }

    private static double lerp(double a, double b, double t) {
        if (t < 0.0D || t > 1.0D) {
            throw new IllegalArgumentException("Interpolation factor must be within [0, 1]");
        }
        return a + (b - a) * t;
    }

    private static void requireFinite(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Scalar values must be finite");
        }
    }

    private static Descriptor number(
            String runeId,
            String executorKey,
            int arity,
            List<String> inputs,
            int budgetCost,
            Map<String, Integer> attributes
    ) {
        if (inputs.size() != arity) {
            throw new IllegalArgumentException("Scalar descriptor arity mismatch");
        }
        return new Descriptor(runeId, executorKey, inputs, RuneType.NUMBER, budgetCost, attributes);
    }

    public record Descriptor(
            String runeId,
            String executorKey,
            List<String> inputNames,
            RuneType output,
            int budgetCost,
            Map<String, Integer> attributes
    ) {
        public Descriptor {
            inputNames = List.copyOf(inputNames);
            attributes = Map.copyOf(attributes);
        }
    }
}
