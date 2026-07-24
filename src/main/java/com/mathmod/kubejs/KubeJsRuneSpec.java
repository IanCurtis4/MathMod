package com.mathmod.kubejs;

import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RuneType;

import java.util.ArrayList;
import java.util.List;

public final class KubeJsRuneSpec {
    private final String id;
    private final List<InputSpec> inputs = new ArrayList<>();
    private String outputType = RuneType.UNIT.id();
    private int budgetCost;
    private int tier = 1;
    private String executorKey = "";
    private boolean enabled = true;

    KubeJsRuneSpec(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        this.id = id.trim();
    }

    public KubeJsRuneSpec input(String name, String type) {
        inputs.add(new InputSpec(name, type));
        return this;
    }

    public KubeJsRuneSpec output(String type) {
        outputType = type;
        return this;
    }

    public KubeJsRuneSpec budgetCost(int cost) {
        budgetCost = cost;
        return this;
    }

    public KubeJsRuneSpec tier(int level) {
        tier = level;
        return this;
    }

    public KubeJsRuneSpec executorKey(String key) {
        executorKey = key == null ? "" : key.trim();
        return this;
    }

    public KubeJsRuneSpec enabled(boolean nextEnabled) {
        enabled = nextEnabled;
        return this;
    }

    public void register() {
        KubeJsCompat.configure(api -> api.rune(id, builder -> {
            for (InputSpec input : inputs) {
                builder.input(input.name(), parseType(input.type()));
            }
            builder.output(parseType(outputType));
            builder.budgetCost(budgetCost);
            builder.tier(tier);
            builder.executorKey(executorKey);
            builder.enabled(enabled);
        }));
    }

    private static RuneType parseType(String typeId) {
        return RuneType.byId(typeId).getOrThrow(message -> new IllegalArgumentException(message));
    }

    private record InputSpec(String name, String type) {
        private InputSpec {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("input name must not be blank");
            }
            if (type == null || type.isBlank()) {
                throw new IllegalArgumentException("input type must not be blank");
            }
            name = name.trim();
            type = type.trim();
        }
    }
}
