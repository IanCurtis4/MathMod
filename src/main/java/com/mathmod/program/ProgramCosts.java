package com.mathmod.program;

import com.mathmod.kubejs.KubeJsCompat;
import com.mathmod.kubejs.RuneMaterialDefinition;
import com.mathmod.runes.AttributeRequirement;
import com.mathmod.runes.MaterialRequirement;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramNode;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.RuneTier;
import com.mathmod.runes.ValidationResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public final class ProgramCosts {
    private ProgramCosts() {
    }

    public static ProgramCostPlan planForAvailableSelectors(ProgramGraph graph, Map<String, Integer> availableSelectors, boolean creative) {
        return planForAvailableSelectors(graph, ProgramResources.recommendedFor(graph), availableSelectors, creative);
    }

    public static ProgramCostPlan planForAvailableSelectors(
            ProgramGraph graph,
            List<ResourceSelection> selections,
            Map<String, Integer> availableSelectors,
            boolean creative
    ) {
        return planFor(graph, selections, selector -> countMatching(availableSelectors, selector), creative, CastModifiers.none());
    }

    public static ProgramCostPlan planForAvailableSelectors(
            ProgramGraph graph,
            List<ResourceSelection> selections,
            Map<String, Integer> availableSelectors,
            boolean creative,
            CastModifiers modifiers
    ) {
        return planFor(graph, selections, selector -> countMatching(availableSelectors, selector), creative, modifiers);
    }

    static ProgramCostPlan planForSelectorCounter(
            ProgramGraph graph,
            List<ResourceSelection> selections,
            ToIntFunction<String> availability,
            boolean creative
    ) {
        return planForSelectorCounter(graph, selections, availability, creative, CastModifiers.none());
    }

    static ProgramCostPlan planForSelectorCounter(
            ProgramGraph graph,
            List<ResourceSelection> selections,
            ToIntFunction<String> availability,
            boolean creative,
            CastModifiers modifiers
    ) {
        return planFor(graph, selections, availability, creative, modifiers);
    }

    private static ProgramCostPlan planFor(
            ProgramGraph graph,
            List<ResourceSelection> selections,
            ToIntFunction<String> availability,
            boolean creative,
            CastModifiers modifiers
    ) {
        Map<String, Integer> fixedRequirements = requirementsFor(graph);
        Map<String, Integer> originalAttributeRequirements = attributeRequirementsFor(graph);
        Map<String, Integer> attributeRequirements = modifiers.applyAttributeDiscount(originalAttributeRequirements);
        int budgetUsed = budgetUsed(graph);
        int baseBudgetLimit = graph.budgetLimit();
        RuneTier requiredTier = ProgramTiers.requiredTier(graph);
        List<RuneMaterialDefinition> materials = validMaterials();
        Map<String, RuneMaterialDefinition> materialBySelector = materials.stream()
                .collect(Collectors.toMap(
                        RuneMaterialDefinition::itemOrTag,
                        material -> material,
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));

        if (!validateFixedSelectors(fixedRequirements)) {
            return emptyPlan(
                    budgetUsed,
                    baseBudgetLimit,
                    fixedRequirements,
                    originalAttributeRequirements,
                    attributeRequirements,
                    requiredTier,
                    modifiers,
                    true
            );
        }

        List<ProgramCostLine> lines = new ArrayList<>();
        Map<String, Integer> providedAttributes = new LinkedHashMap<>();
        int budgetBonus = 0;

        for (Map.Entry<String, Integer> entry : fixedRequirements.entrySet()) {
            String selector = entry.getKey();
            int quantity = entry.getValue();

            RuneMaterialDefinition material = materialBySelector.get(selector);
            int lineBudgetBonus = material == null ? 0 : material.budgetBonus() * quantity;
            Map<String, Integer> lineAttributes = material == null ? Map.of() : scale(material.attributes(), quantity);
            budgetBonus += lineBudgetBonus;
            addAttributes(providedAttributes, lineAttributes);

            addLine(lines, new ProgramCostLine(
                    material == null ? selector : material.id(),
                    selector,
                    quantity,
                    !creative && (material == null || material.consumed()),
                    lineBudgetBonus,
                    material == null ? 0 : material.tier(),
                    lineAttributes,
                    "fixed"
            ));
        }

        for (ResourceSelection selection : selections) {
            materialById(materials, selection.materialId()).ifPresent(material -> {
                int quantity = selection.quantity();
                int lineBudgetBonus = material.budgetBonus() * quantity;
                Map<String, Integer> lineAttributes = scale(material.attributes(), quantity);
                addAttributes(providedAttributes, lineAttributes);
                addLine(lines, new ProgramCostLine(
                        material.id(),
                        material.itemOrTag(),
                        quantity,
                        !creative && material.consumed(),
                        lineBudgetBonus,
                        material.tier(),
                        lineAttributes,
                        "selected"
                ));
            });
        }

        budgetBonus = lines.stream().mapToInt(ProgramCostLine::budgetBonus).sum();
        Map<String, Integer> missingAttributes = missingAttributes(attributeRequirements, providedAttributes);
        Map<String, Integer> missingItems = missingItems(lines, availability, creative);
        RuneTier providedTier = contributingTier(
                lines,
                originalAttributeRequirements,
                budgetUsed > baseBudgetLimit
        );

        return new ProgramCostPlan(
                budgetUsed,
                baseBudgetLimit,
                budgetBonus,
                baseBudgetLimit + budgetBonus,
                fixedRequirements,
                originalAttributeRequirements,
                attributeRequirements,
                providedAttributes,
                missingAttributes,
                lines,
                missingItems,
                requiredTier,
                providedTier,
                modifiers,
                false
        );
    }

    public static Map<String, Integer> requirementsFor(ProgramGraph graph) {
        Map<String, Integer> requirements = new LinkedHashMap<>();
        for (ProgramNode node : graph.nodes()) {
            ProgramStorage.definition(node.runeId())
                    .map(RuneDefinition::materialRequirements)
                    .ifPresent(nodeRequirements -> {
                        for (MaterialRequirement requirement : nodeRequirements) {
                            requirements.merge(requirement.itemOrTag(), requirement.quantity(), Integer::sum);
                        }
                    });
        }
        return requirements;
    }

    public static Map<String, Integer> attributeRequirementsFor(ProgramGraph graph) {
        Map<String, Integer> requirements = new LinkedHashMap<>();
        for (ProgramNode node : graph.nodes()) {
            ProgramStorage.definition(node.runeId())
                    .map(RuneDefinition::attributeRequirements)
                    .ifPresent(nodeRequirements -> {
                        for (AttributeRequirement requirement : nodeRequirements) {
                            requirements.merge(requirement.attribute(), requirement.amount(), Integer::sum);
                        }
                    });
        }
        ResultMagnitudeCosts.attributeRequirements(graph)
                .forEach((attribute, amount) -> requirements.merge(attribute, amount, Integer::sum));
        return requirements;
    }

    public static String describe(Map<String, Integer> requirements) {
        return requirements.entrySet().stream()
                .map(entry -> entry.getValue() + "x " + entry.getKey())
                .collect(Collectors.joining(", "));
    }

    public static String describeLines(ProgramCostPlan plan) {
        return plan.lines().stream()
                .filter(ProgramCostLine::consumed)
                .map(line -> line.quantity() + "x " + line.selector())
                .collect(Collectors.joining(", "));
    }

    public static String describeAttributes(Map<String, Integer> attributes) {
        return attributes.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    private static Optional<RuneMaterialDefinition> materialById(List<RuneMaterialDefinition> materials, String id) {
        return materials.stream()
                .filter(material -> material.id().equals(id))
                .findFirst();
    }

    private static int budgetUsed(ProgramGraph graph) {
        ValidationResult result = ProgramStorage.validate(graph);
        return result.budgetUsed();
    }

    private static ProgramCostPlan emptyPlan(
            int budgetUsed,
            int baseBudgetLimit,
            Map<String, Integer> fixedRequirements,
            Map<String, Integer> originalAttributeRequirements,
            Map<String, Integer> attributeRequirements,
            RuneTier requiredTier,
            CastModifiers modifiers,
            boolean badSelectors
    ) {
        return new ProgramCostPlan(
                budgetUsed,
                baseBudgetLimit,
                0,
                baseBudgetLimit,
                fixedRequirements,
                originalAttributeRequirements,
                attributeRequirements,
                Map.of(),
                attributeRequirements,
                List.of(),
                Map.of(),
                requiredTier,
                RuneTier.FUNDAMENTAL,
                modifiers,
                badSelectors
        );
    }

    private static List<RuneMaterialDefinition> validMaterials() {
        MathModRuneBootstrap.bootstrap();
        Collection<RuneMaterialDefinition> materials;
        try {
            materials = KubeJsCompat.api().materials();
        } catch (IllegalStateException exception) {
            materials = KubeJsCompat.createApi(MathModRuneBootstrap.registry()).materials();
        }
        return materials.stream()
                .filter(material -> {
                    return isValidSelectorSyntax(material.itemOrTag());
                })
                .toList();
    }

    private static boolean validateFixedSelectors(Map<String, Integer> requirements) {
        for (String selector : requirements.keySet()) {
            if (!isValidSelectorSyntax(selector)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidSelectorSyntax(String selector) {
        boolean foundToken = false;
        for (String token : selector.split(",")) {
            String trimmed = token.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            foundToken = true;
            String id = trimmed.startsWith("#") ? trimmed.substring(1) : trimmed;
            if (!id.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) {
                return false;
            }
        }
        return foundToken;
    }

    private static Map<String, Integer> missingItems(
            List<ProgramCostLine> lines,
            ToIntFunction<String> availability,
            boolean creative
    ) {
        if (creative) {
            return Map.of();
        }
        Map<String, Integer> requiredBySelector = new LinkedHashMap<>();
        for (ProgramCostLine line : lines) {
            requiredBySelector.merge(line.selector(), line.quantity(), Integer::sum);
        }
        Map<String, Integer> missing = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : requiredBySelector.entrySet()) {
            int deficit = entry.getValue() - availability.applyAsInt(entry.getKey());
            if (deficit > 0) {
                missing.put(entry.getKey(), deficit);
            }
        }
        return java.util.Collections.unmodifiableMap(new LinkedHashMap<>(missing));
    }

    private static int countMatching(Map<String, Integer> availableSelectors, String selector) {
        int count = 0;
        for (String token : selector.split(",")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                count += availableSelectors.getOrDefault(trimmed, 0);
            }
        }
        return count;
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

    private static void addLine(List<ProgramCostLine> lines, ProgramCostLine next) {
        for (int i = 0; i < lines.size(); i++) {
            ProgramCostLine existing = lines.get(i);
            if (existing.id().equals(next.id())
                    && existing.selector().equals(next.selector())
                    && existing.consumed() == next.consumed()
                    && existing.reason().equals(next.reason())) {
                Map<String, Integer> attributes = new LinkedHashMap<>(existing.attributes());
                addAttributes(attributes, next.attributes());
                lines.set(i, new ProgramCostLine(
                        existing.id(),
                        existing.selector(),
                        existing.quantity() + next.quantity(),
                        existing.consumed(),
                        existing.budgetBonus() + next.budgetBonus(),
                        Math.max(existing.tier(), next.tier()),
                        attributes,
                        existing.reason()
                ));
                return;
            }
        }
        lines.add(next);
    }

    private static RuneTier contributingTier(
            List<ProgramCostLine> lines,
            Map<String, Integer> originalAttributeRequirements,
            boolean needsBudgetContribution
    ) {
        int providedLevel = RuneTier.FUNDAMENTAL.level();
        for (ProgramCostLine line : lines) {
            boolean contributes = line.reason().equals("fixed")
                    || (needsBudgetContribution && line.budgetBonus() > 0)
                    || line.attributes().keySet().stream().anyMatch(originalAttributeRequirements::containsKey);
            if (contributes) {
                providedLevel = Math.max(providedLevel, line.tier());
            }
        }
        return RuneTier.byLevel(Math.max(RuneTier.FUNDAMENTAL.level(), providedLevel));
    }

}
