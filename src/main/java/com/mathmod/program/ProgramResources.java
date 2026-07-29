package com.mathmod.program;

import com.mathmod.kubejs.KubeJsCompat;
import com.mathmod.kubejs.RuneMaterialDefinition;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramValidator;
import com.mathmod.runes.RuneTier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ProgramResources {
    private ProgramResources() {
    }

    public static List<ResourceSelection> get(ItemStack stack) {
        return List.copyOf(stack.getOrDefault(ModDataComponents.PROGRAM_RESOURCES.get(), List.of()));
    }

    public static void set(ItemStack stack, List<ResourceSelection> selections) {
        List<ResourceSelection> normalized = normalize(selections);
        if (normalized.isEmpty()) {
            stack.remove(ModDataComponents.PROGRAM_RESOURCES.get());
        } else {
            stack.set(ModDataComponents.PROGRAM_RESOURCES.get(), normalized);
        }
    }

    public static void clear(ItemStack stack) {
        stack.remove(ModDataComponents.PROGRAM_RESOURCES.get());
    }

    public static void add(ItemStack stack, String materialId) {
        Map<String, Integer> quantities = quantities(get(stack));
        quantities.merge(materialId, 1, Integer::sum);
        set(stack, selections(quantities));
    }

    public static void removeAt(ItemStack stack, int index) {
        List<ResourceSelection> current = new ArrayList<>(get(stack));
        if (index < 0 || index >= current.size()) {
            return;
        }
        ResourceSelection selection = current.get(index);
        if (selection.quantity() > 1) {
            current.set(index, new ResourceSelection(selection.materialId(), selection.quantity() - 1));
        } else {
            current.remove(index);
        }
        set(stack, current);
    }

    public static List<RuneMaterialDefinition> materials() {
        MathModRuneBootstrap.bootstrap();
        try {
            return KubeJsCompat.api().materials().stream()
                    .sorted(Comparator.comparing(RuneMaterialDefinition::id))
                    .toList();
        } catch (IllegalStateException exception) {
            return KubeJsCompat.createApi(MathModRuneBootstrap.registry()).materials().stream()
                .sorted(Comparator.comparing(RuneMaterialDefinition::id))
                .toList();
        }
    }

    public static Optional<RuneMaterialDefinition> material(String id) {
        return materials().stream()
                .filter(material -> material.id().equals(id))
                .findFirst();
    }

    public static List<ResourceSelection> recommendedFor(ProgramGraph graph) {
        return recommendedFor(graph, MathModRuneBootstrap.registry(), materials());
    }

    static List<ResourceSelection> recommendedFor(
            ProgramGraph graph,
            com.mathmod.runes.RuneRegistry runes,
            List<RuneMaterialDefinition> materials
    ) {
        Map<String, Integer> selected = new LinkedHashMap<>();
        Map<String, Integer> providedAttributes = new LinkedHashMap<>();
        int budgetBonus = 0;
        int providedTier = RuneTier.FUNDAMENTAL.level();

        Map<String, Integer> fixed = ProgramCosts.requirementsFor(graph, runes);
        for (Map.Entry<String, Integer> entry : fixed.entrySet()) {
            Optional<RuneMaterialDefinition> fixedMaterial = materials.stream().filter(material -> material.itemOrTag().equals(entry.getKey())).findFirst();
            fixedMaterial.ifPresent(material -> {
                addAttributes(providedAttributes, scale(material.attributes(), entry.getValue()));
            });
            budgetBonus += fixedMaterial
                    .map(material -> material.budgetBonus() * entry.getValue())
                    .orElse(0);
            providedTier = Math.max(providedTier, fixedMaterial.map(RuneMaterialDefinition::tier).orElse(1));
        }

        Map<String, Integer> requiredAttributes = ProgramCosts.attributeRequirementsFor(graph, runes);
        int requiredTier = ProgramTiers.requiredTier(graph, runes).level();
        int budgetUsed = new ProgramValidator(runes).validate(graph).budgetUsed();
        boolean needsBudgetContribution = budgetUsed > graph.budgetLimit();
        int missingBudget = Math.max(0, budgetUsed - graph.budgetLimit() - budgetBonus);
        Map<String, Integer> missingAttributes = missingAttributes(requiredAttributes, providedAttributes);

        List<RuneMaterialDefinition> candidates = materials.stream()
                .sorted(Comparator
                        .comparingInt(RuneMaterialDefinition::tier)
                        .thenComparingInt(RuneMaterialDefinition::budgetBonus)
                        .thenComparing(RuneMaterialDefinition::id))
                .toList();

        int guard = ProgramValidator.MAX_NODES;
        while ((!missingAttributes.isEmpty() || missingBudget > 0 || providedTier < requiredTier) && guard-- > 0) {
            Map<String, Integer> currentMissingAttributes = missingAttributes;
            int currentMissingBudget = missingBudget;
            boolean currentlyMissingTier = providedTier < requiredTier;
            RuneMaterialDefinition next = candidates.stream()
                    .filter(material -> isUseful(
                            material,
                            currentMissingAttributes,
                            currentMissingBudget,
                            currentlyMissingTier,
                            requiredTier,
                            requiredAttributes,
                            needsBudgetContribution
                    ))
                    .max(Comparator
                            .comparingInt((RuneMaterialDefinition material) -> usefulnessScore(
                                    material,
                                    currentMissingAttributes,
                                    currentMissingBudget,
                                    currentlyMissingTier,
                                    requiredTier,
                                    requiredAttributes,
                                    needsBudgetContribution
                            ))
                            .thenComparingInt(material -> -material.tier())
                            .thenComparingInt(material -> -material.budgetBonus())
                            .thenComparing(RuneMaterialDefinition::id, Comparator.reverseOrder()))
                    .orElse(null);
            if (next == null) {
                break;
            }
            RuneMaterialDefinition material = next;
            selected.merge(material.id(), 1, Integer::sum);
            addAttributes(providedAttributes, material.attributes());
            budgetBonus += material.budgetBonus();
            if (contributesToTier(material, requiredAttributes, needsBudgetContribution)) {
                providedTier = Math.max(providedTier, material.tier());
            }
            missingAttributes = missingAttributes(requiredAttributes, providedAttributes);
            missingBudget = Math.max(0, budgetUsed - graph.budgetLimit() - budgetBonus);
        }

        return selections(selected);
    }

    public static Optional<RuneMaterialDefinition> materialForSelector(String selector) {
        return materials().stream()
                .filter(material -> material.itemOrTag().equals(selector))
                .findFirst();
    }

    private static boolean isUseful(
            RuneMaterialDefinition material,
            Map<String, Integer> missingAttributes,
            int missingBudget,
            boolean missingTier,
            int requiredTier,
            Map<String, Integer> requiredAttributes,
            boolean needsBudgetContribution
    ) {
        if (missingBudget > 0 && material.budgetBonus() > 0) {
            return true;
        }
        for (String attribute : missingAttributes.keySet()) {
            if (material.attributeAmount(attribute) > 0) {
                return true;
            }
        }
        return missingTier
                && material.tier() >= requiredTier
                && contributesToTier(material, requiredAttributes, needsBudgetContribution);
    }

    private static boolean contributesToTier(
            RuneMaterialDefinition material,
            Map<String, Integer> requiredAttributes,
            boolean needsBudgetContribution
    ) {
        return (needsBudgetContribution && material.budgetBonus() > 0)
                || material.attributes().keySet().stream().anyMatch(requiredAttributes::containsKey);
    }

    private static int usefulnessScore(
            RuneMaterialDefinition material,
            Map<String, Integer> missingAttributes,
            int missingBudget,
            boolean missingTier,
            int requiredTier,
            Map<String, Integer> requiredAttributes,
            boolean needsBudgetContribution
    ) {
        int score = Math.min(missingBudget, material.budgetBonus());
        for (Map.Entry<String, Integer> missing : missingAttributes.entrySet()) {
            score += Math.min(missing.getValue(), material.attributeAmount(missing.getKey()));
        }
        if (missingTier
                && material.tier() >= requiredTier
                && contributesToTier(material, requiredAttributes, needsBudgetContribution)) {
            score++;
        }
        return score;
    }

    private static List<ResourceSelection> normalize(List<ResourceSelection> selections) {
        return selections(quantities(selections));
    }

    private static Map<String, Integer> quantities(List<ResourceSelection> selections) {
        Map<String, Integer> quantities = new LinkedHashMap<>();
        for (ResourceSelection selection : selections) {
            if (material(selection.materialId()).isPresent()) {
                quantities.merge(selection.materialId(), selection.quantity(), Integer::sum);
            }
        }
        return quantities;
    }

    private static List<ResourceSelection> selections(Map<String, Integer> quantities) {
        return quantities.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ResourceSelection(entry.getKey(), entry.getValue()))
                .toList();
    }

    private static Map<String, Integer> scale(Map<String, Integer> attributes, int quantity) {
        Map<String, Integer> scaled = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : attributes.entrySet()) {
            scaled.put(entry.getKey(), entry.getValue() * quantity);
        }
        return scaled;
    }

    private static void addAttributes(Map<String, Integer> target, Map<String, Integer> attributes) {
        for (Map.Entry<String, Integer> entry : attributes.entrySet()) {
            target.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    private static Map<String, Integer> missingAttributes(Map<String, Integer> required, Map<String, Integer> provided) {
        Map<String, Integer> missing = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            int deficit = entry.getValue() - provided.getOrDefault(entry.getKey(), 0);
            if (deficit > 0) {
                missing.put(entry.getKey(), deficit);
            }
        }
        return missing;
    }
}
