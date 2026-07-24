package com.mathmod.runes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record RuneDefinition(
        String id,
        List<RuneInput> inputs,
        RuneType outputType,
        int budgetCost,
        List<MaterialRequirement> materialRequirements,
        List<AttributeRequirement> attributeRequirements,
        RuneTier tier,
        RunePurity purity,
        String executorKey,
        Map<String, String> params,
        boolean enabled
) {
    public RuneDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (outputType == null) {
            throw new IllegalArgumentException("outputType must not be null");
        }
        if (budgetCost < 0) {
            throw new IllegalArgumentException("budgetCost must not be negative");
        }
        if (tier == null) {
            throw new IllegalArgumentException("tier must not be null");
        }
        if (purity == null) {
            throw new IllegalArgumentException("purity must not be null");
        }
        id = id.trim();
        inputs = List.copyOf(inputs);
        materialRequirements = List.copyOf(materialRequirements);
        attributeRequirements = List.copyOf(attributeRequirements);
        executorKey = executorKey == null ? "" : executorKey.trim();
        params = Map.copyOf(params);
    }

    public Optional<RuneInput> input(String name) {
        return inputs.stream()
                .filter(input -> input.name().equals(name))
                .findFirst();
    }

    public RuneDefinition withEnabled(boolean nextEnabled) {
        return new RuneDefinition(id, inputs, outputType, budgetCost, materialRequirements, attributeRequirements, tier, purity, executorKey, params, nextEnabled);
    }

    public RuneDefinition withBudgetCost(int nextBudgetCost) {
        return new RuneDefinition(id, inputs, outputType, nextBudgetCost, materialRequirements, attributeRequirements, tier, purity, executorKey, params, enabled);
    }

    public RuneDefinition withTier(RuneTier nextTier) {
        return new RuneDefinition(id, inputs, outputType, budgetCost, materialRequirements, attributeRequirements, nextTier, purity, executorKey, params, enabled);
    }

    public RuneDefinition withPurity(RunePurity nextPurity) {
        return new RuneDefinition(id, inputs, outputType, budgetCost, materialRequirements, attributeRequirements, tier, nextPurity, executorKey, params, enabled);
    }

    public RuneDefinition withMaterialRequirement(MaterialRequirement requirement) {
        List<MaterialRequirement> nextRequirements = new ArrayList<>(materialRequirements);
        nextRequirements.add(requirement);
        return new RuneDefinition(id, inputs, outputType, budgetCost, nextRequirements, attributeRequirements, tier, purity, executorKey, params, enabled);
    }

    public RuneDefinition withoutMaterialRequirements() {
        return new RuneDefinition(id, inputs, outputType, budgetCost, List.of(), attributeRequirements, tier, purity, executorKey, params, enabled);
    }

    public RuneDefinition withAttributeRequirement(AttributeRequirement requirement) {
        List<AttributeRequirement> nextRequirements = new ArrayList<>(attributeRequirements);
        nextRequirements.add(requirement);
        return new RuneDefinition(id, inputs, outputType, budgetCost, materialRequirements, nextRequirements, tier, purity, executorKey, params, enabled);
    }

    public RuneDefinition withoutAttributeRequirements() {
        return new RuneDefinition(id, inputs, outputType, budgetCost, materialRequirements, List.of(), tier, purity, executorKey, params, enabled);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final String id;
        private final List<RuneInput> inputs = new ArrayList<>();
        private final List<MaterialRequirement> materialRequirements = new ArrayList<>();
        private final List<AttributeRequirement> attributeRequirements = new ArrayList<>();
        private final Map<String, String> params = new LinkedHashMap<>();
        private RuneType outputType = RuneType.UNIT;
        private int budgetCost;
        private RuneTier tier = RuneTier.FUNDAMENTAL;
        private RunePurity purity;
        private String executorKey = "";
        private boolean enabled = true;

        private Builder(String id) {
            this.id = id;
        }

        public Builder input(String name, RuneType type) {
            inputs.add(new RuneInput(name, type));
            return this;
        }

        public Builder output(RuneType type) {
            this.outputType = type;
            return this;
        }

        public Builder budgetCost(int cost) {
            this.budgetCost = cost;
            return this;
        }

        public Builder tier(RuneTier tier) {
            this.tier = tier;
            return this;
        }

        public Builder tier(int level) {
            return tier(RuneTier.byLevel(level));
        }

        public Builder purity(RunePurity purity) {
            this.purity = purity;
            return this;
        }

        public Builder material(String itemOrTag, int quantity) {
            materialRequirements.add(new MaterialRequirement(itemOrTag, quantity));
            return this;
        }

        public Builder attribute(String attribute, int amount) {
            attributeRequirements.add(new AttributeRequirement(attribute, amount));
            return this;
        }

        public Builder executorKey(String key) {
            this.executorKey = key;
            return this;
        }

        public Builder param(String key, String value) {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("param key must not be blank");
            }
            params.put(key.trim(), value == null ? "" : value);
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public RuneDefinition build() {
            RunePurity resolvedPurity = purity == null ? RunePurity.infer(executorKey) : purity;
            return new RuneDefinition(id, inputs, outputType, budgetCost, materialRequirements, attributeRequirements, tier, resolvedPurity, executorKey, params, enabled);
        }
    }
}
