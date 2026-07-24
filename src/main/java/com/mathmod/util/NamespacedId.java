package com.mathmod.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public record NamespacedId(String value) implements Comparable<NamespacedId> {
    private static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_.-]+");
    private static final Pattern PATH = Pattern.compile("[a-z0-9/._-]+");

    public static final Codec<NamespacedId> CODEC = Codec.STRING.comapFlatMap(
            NamespacedId::decode,
            NamespacedId::toString
    );

    public NamespacedId {
        value = Objects.requireNonNull(value, "value").trim().toLowerCase(Locale.ROOT);
        int separator = value.indexOf(':');
        if (separator <= 0
                || separator == value.length() - 1
                || !NAMESPACE.matcher(value.substring(0, separator)).matches()
                || !PATH.matcher(value.substring(separator + 1)).matches()) {
            throw new IllegalArgumentException("Invalid namespaced id: " + value);
        }
    }

    public static NamespacedId of(String namespace, String path) {
        return new NamespacedId(namespace + ":" + path);
    }

    public static NamespacedId parse(String value) {
        return new NamespacedId(value);
    }

    public static Optional<NamespacedId> tryParse(String value) {
        try {
            return Optional.of(parse(value));
        } catch (IllegalArgumentException | NullPointerException exception) {
            return Optional.empty();
        }
    }

    public String namespace() {
        return value.substring(0, value.indexOf(':'));
    }

    public String path() {
        return value.substring(value.indexOf(':') + 1);
    }

    @Override
    public int compareTo(NamespacedId other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }

    private static DataResult<NamespacedId> decode(String value) {
        try {
            return DataResult.success(parse(value));
        } catch (IllegalArgumentException exception) {
            return DataResult.error(exception::getMessage);
        }
    }
}
