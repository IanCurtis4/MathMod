package com.mathmod.program;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public record CustomSpellInvocation(CustomSpellAction action, Map<String, Double> arguments) {
    public static final int MAX_PERSISTENT_ID_LENGTH = 512;
    private static final String ARGUMENT_SEPARATOR = ",";
    private static final String VALUE_SEPARATOR = "=";
    private static final String PARAMETER_MARKER = "?";

    public CustomSpellInvocation {
        if (action == null) {
            throw new IllegalArgumentException("Custom spell action cannot be null");
        }
        arguments = sanitize(action, arguments);
    }

    public static CustomSpellInvocation defaults(CustomSpellAction action) {
        return new CustomSpellInvocation(action, Map.of());
    }

    public double argument(String key) {
        return arguments.getOrDefault(
                key,
                action.numericParameters().stream()
                        .filter(parameter -> parameter.key().equals(key))
                        .map(CustomNumericParameter::defaultValue)
                        .findFirst()
                        .orElse(0.0D)
        );
    }

    public String persistentId() {
        if (action.numericParameters().isEmpty()) {
            return action.persistentId();
        }
        StringBuilder encoded = new StringBuilder(action.persistentId()).append(PARAMETER_MARKER);
        boolean first = true;
        for (CustomNumericParameter parameter : action.numericParameters()) {
            if (!first) {
                encoded.append(ARGUMENT_SEPARATOR);
            }
            first = false;
            encoded.append(parameter.key())
                    .append(VALUE_SEPARATOR)
                    .append(Double.toHexString(argument(parameter.key())));
        }
        return encoded.toString();
    }

    public static Optional<CustomSpellInvocation> fromPersistentId(String encoded) {
        if (encoded == null || encoded.isBlank() || encoded.length() > MAX_PERSISTENT_ID_LENGTH) {
            return Optional.empty();
        }
        int marker = encoded.indexOf(PARAMETER_MARKER);
        String actionId = marker < 0 ? encoded : encoded.substring(0, marker);
        return CustomSpellAction.fromPersistentId(actionId).map(action -> {
            Map<String, Double> values = new LinkedHashMap<>();
            if (marker >= 0 && marker + 1 < encoded.length()) {
                for (String entry : encoded.substring(marker + 1).split(ARGUMENT_SEPARATOR)) {
                    int separator = entry.indexOf(VALUE_SEPARATOR);
                    if (separator <= 0 || separator == entry.length() - 1) {
                        continue;
                    }
                    try {
                        values.put(entry.substring(0, separator), Double.parseDouble(entry.substring(separator + 1)));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            return new CustomSpellInvocation(action, values);
        });
    }

    private static Map<String, Double> sanitize(CustomSpellAction action, Map<String, Double> supplied) {
        Map<String, Double> source = supplied == null ? Map.of() : supplied;
        Map<String, Double> sanitized = new LinkedHashMap<>();
        for (CustomNumericParameter parameter : action.numericParameters()) {
            sanitized.put(
                    parameter.key(),
                    parameter.clamp(source.getOrDefault(parameter.key(), parameter.defaultValue()))
            );
        }
        return Map.copyOf(sanitized);
    }
}
