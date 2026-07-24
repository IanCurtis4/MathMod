package com.mathmod.program;

import com.mathmod.item.ProgrammedTalismanItem;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramValidator;
import com.mathmod.runes.RuneDefinition;
import com.mathmod.runes.ValidationResult;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ProgramStorage {
    private ProgramStorage() {
    }

    public static Optional<ProgramGraph> get(ItemStack stack) {
        return Optional.ofNullable(stack.get(ModDataComponents.PROGRAM.get()));
    }

    public static Optional<String> getName(ItemStack stack) {
        Optional<String> workspaceName = getGuidedWorkspace(stack).state()
                .map(GuidedWorkspaceState::name)
                .filter(name -> !name.isEmpty());
        return workspaceName.isPresent()
                ? workspaceName
                : Optional.ofNullable(stack.get(ModDataComponents.PROGRAM_NAME.get()));
    }

    public static List<CustomSpellAction> getCustomActions(ItemStack stack) {
        return getCustomInvocations(stack).stream()
                .map(CustomSpellInvocation::action)
                .toList();
    }

    public static List<CustomSpellInvocation> getCustomInvocations(ItemStack stack) {
        return getGuidedWorkspace(stack).state()
                .flatMap(GuidedWorkspaceState::replayableInvocations)
                .orElse(List.of());
    }

    public static GuidedWorkspaceRead getGuidedWorkspace(ItemStack stack) {
        return GuidedWorkspacePersistence.read(stack);
    }

    public static ValidationResult validate(ProgramGraph graph) {
        MathModRuneBootstrap.bootstrap();
        return new ProgramValidator(MathModRuneBootstrap.registry()).validate(graph);
    }

    public static ValidationResult validateExecutable(ProgramGraph graph) {
        MathModRuneBootstrap.bootstrap();
        return ProgramExecutionPolicy.validateExecutable(graph, MathModRuneBootstrap.registry());
    }

    public static ValidationResult validateExecutable(ProgramGraph graph, int budgetBonus) {
        MathModRuneBootstrap.bootstrap();
        return ProgramExecutionPolicy.validateExecutable(graph, MathModRuneBootstrap.registry(), budgetBonus);
    }

    public static Optional<RuneDefinition> definition(String runeId) {
        MathModRuneBootstrap.bootstrap();
        return MathModRuneBootstrap.registry().find(runeId);
    }

    public static ValidationResult saveValidated(ItemStack stack, ProgramGraph graph) {
        List<ResourceSelection> recommendedResources = ProgramResources.recommendedFor(graph);
        List<ResourceSelection> resourcesToPersist = resourcesToPersist(stack, graph, recommendedResources);
        ValidationResult result = validateExecutable(graph, budgetBonus(graph, recommendedResources));
        if (result.valid() && stack.getItem() instanceof ProgrammedTalismanItem) {
            stack.set(ModDataComponents.PROGRAM.get(), graph);
            GuidedWorkspacePersistence.clear(stack);
            ProgramResources.set(stack, resourcesToPersist);
        }
        return result;
    }

    public static ValidationResult saveValidated(ItemStack stack, ProgramGraph graph, String name) {
        return saveValidated(stack, graph, name, List.of());
    }

    public static ValidationResult saveValidated(ItemStack stack, ProgramGraph graph, String name, List<CustomSpellAction> customActions) {
        return saveValidatedCustom(
                stack,
                graph,
                name,
                customActions == null
                        ? List.of()
                        : customActions.stream().map(CustomSpellInvocation::defaults).toList()
        );
    }

    public static ValidationResult saveValidatedCustom(
            ItemStack stack,
            ProgramGraph graph,
            String name,
            List<CustomSpellInvocation> customInvocations
    ) {
        List<ResourceSelection> recommendedResources = ProgramResources.recommendedFor(graph);
        List<ResourceSelection> resourcesToPersist = resourcesToPersist(stack, graph, recommendedResources);
        ValidationResult result = validateExecutable(graph, budgetBonus(graph, recommendedResources));
        if (result.valid() && stack.getItem() instanceof ProgrammedTalismanItem) {
            stack.set(ModDataComponents.PROGRAM.get(), graph);
            GuidedWorkspacePersistence.write(
                    stack,
                    GuidedWorkspaceState.create(name, customInvocations == null ? List.of() : customInvocations)
            );
            ProgramResources.set(stack, resourcesToPersist);
        }
        return result;
    }

    private static List<ResourceSelection> resourcesToPersist(
            ItemStack stack,
            ProgramGraph graph,
            List<ResourceSelection> recommendedResources
    ) {
        return InscriptionResourcePolicy.resourcesToPersist(
                get(stack),
                ProgramResources.get(stack),
                graph,
                recommendedResources
        );
    }

    private static int budgetBonus(ProgramGraph graph, List<ResourceSelection> resources) {
        return ProgramCosts.planForAvailableSelectors(graph, resources, Map.of(), true).budgetBonus();
    }

    public static boolean clear(ItemStack stack) {
        if (!(stack.getItem() instanceof ProgrammedTalismanItem)) {
            return false;
        }
        boolean removedProgram = stack.remove(ModDataComponents.PROGRAM.get()) != null;
        GuidedWorkspacePersistence.clear(stack);
        ProgramResources.clear(stack);
        return removedProgram;
    }
}
