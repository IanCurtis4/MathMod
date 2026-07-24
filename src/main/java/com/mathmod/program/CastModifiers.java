package com.mathmod.program;

import java.util.LinkedHashMap;
import java.util.Map;

public record CastModifiers(int attributeDiscount, double conservationChance) {
    public static final int MAX_ATTRIBUTE_DISCOUNT = 2;
    public static final double CONSERVATION_CHANCE_PER_LEVEL = 0.15D;
    public static final double MAX_CONSERVATION_CHANCE = 0.45D;

    public CastModifiers {
        attributeDiscount = Math.max(0, Math.min(MAX_ATTRIBUTE_DISCOUNT, attributeDiscount));
        conservationChance = Math.max(0.0D, Math.min(MAX_CONSERVATION_CHANCE, conservationChance));
    }

    public static CastModifiers none() {
        return new CastModifiers(0, 0.0D);
    }

    public Map<String, Integer> applyAttributeDiscount(Map<String, Integer> requirements) {
        if (attributeDiscount == 0 || requirements.isEmpty()) {
            return requirements;
        }
        Map<String, Integer> adjusted = new LinkedHashMap<>();
        requirements.forEach((attribute, amount) ->
                adjusted.put(attribute, Math.max(1, amount - attributeDiscount)));
        return Map.copyOf(adjusted);
    }
}
