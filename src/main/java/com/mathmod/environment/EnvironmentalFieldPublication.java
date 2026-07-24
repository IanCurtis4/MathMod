package com.mathmod.environment;

import com.mathmod.util.NamespacedId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** One atomically published field generation and its flattened migration aliases. */
public record EnvironmentalFieldPublication(EnvironmentalFieldSnapshot snapshot, Map<NamespacedId, NamespacedId> aliases) {
    public EnvironmentalFieldPublication {
        snapshot = Objects.requireNonNull(snapshot, "snapshot");
        aliases = flattenAliases(snapshot, aliases);
    }

    public NamespacedId resolve(NamespacedId id) {
        return aliases.getOrDefault(id, id);
    }

    private static Map<NamespacedId, NamespacedId> flattenAliases(
            EnvironmentalFieldSnapshot snapshot, Map<NamespacedId, NamespacedId> candidates
    ) {
        if (candidates.size() > 256) throw new IllegalArgumentException("Too many environmental aliases");
        Map<NamespacedId, NamespacedId> flattened = new LinkedHashMap<>();
        for (NamespacedId source : candidates.keySet()) {
            NamespacedId cursor = source;
            for (int hops = 0; hops <= 16; hops++) {
                NamespacedId next = candidates.get(cursor);
                if (next == null) {
                    if (cursor.equals(source)) throw new IllegalArgumentException("Alias has no target " + source);
                    if (snapshot.channel(cursor).isEmpty()) throw new IllegalArgumentException("Alias target is not a channel " + cursor);
                    flattened.put(source, cursor);
                    break;
                }
                cursor = next;
                if (cursor.equals(source)) throw new IllegalArgumentException("Environmental alias cycle at " + source);
                if (hops == 16) throw new IllegalArgumentException("Environmental alias chain is too long for " + source);
            }
        }
        for (NamespacedId canonical : snapshot.channels().stream().map(EnvironmentalChannel::id).toList()) {
            if (candidates.containsKey(canonical)) throw new IllegalArgumentException("Alias shadows canonical channel " + canonical);
        }
        return Map.copyOf(flattened);
    }
}
