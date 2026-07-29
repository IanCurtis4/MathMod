package com.mathmod.program;

import com.mathmod.MathMod;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.registry.ModItems;
import com.mathmod.runes.ProgramGraph;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

/** Dedicated-server evidence for A0 schema-1 persistence compatibility. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class A0CompatibilityGameTests {
    private A0CompatibilityGameTests() {
    }

    @GameTest(template = "empty")
    public static void schemaOneReadReplaysExactlyWithoutRewritingTalisman(GameTestHelper helper) {
        List<CustomSpellInvocation> invocations = executableInvocations();
        GuidedWorkspaceState workspace = GuidedWorkspaceState.create("Schema One", invocations);
        ProgramGraph graph = graphFor(invocations);
        ItemStack talisman = talisman(workspace, graph);
        ItemStack before = talisman.copy();

        GuidedWorkspaceRead read = GuidedWorkspacePersistence.read(talisman);
        helper.assertTrue(read.status() == GuidedWorkspaceRead.Status.AVAILABLE,
                "A valid schema-1 workspace must remain available through the item read path");
        GuidedWorkspaceState restored = read.state().orElseThrow();
        helper.assertTrue(restored.equals(workspace), "The persisted schema-1 workspace must be exact");
        helper.assertTrue(graphFor(restored.replayableInvocations().orElseThrow()).equals(graph),
                "Schema-1 replay must reproduce the authoritative graph exactly");
        ProgramGraph persisted = ProgramStorage.get(talisman).orElseThrow();
        helper.assertTrue(persisted.equals(graph), "The authoritative graph must remain unchanged after workspace read");
        helper.assertTrue(ProgramStorage.validateExecutable(persisted).valid(),
                "The authoritative graph must remain executable");
        helper.assertTrue(ProgramSurface.inscribed(persisted).inspect().graph().equals(graph),
                "The retained graph must remain available to read-only inspection");
        helper.assertTrue(ItemStack.isSameItemSameComponents(before, talisman),
                "Reading schema-1 metadata must not rewrite any talisman component");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void unknownSchemaOneMetadataFailsClosedWithoutRewritingGraph(GameTestHelper helper) {
        assertUnreplayablePreservesGraph(
                helper,
                new GuidedWorkspaceState(GuidedWorkspaceState.CURRENT_VERSION, "Unknown", List.of("removed_mod:lost_form")),
                "Unknown current-schema metadata must fail closed"
        );
    }

    @GameTest(template = "empty")
    public static void futureSchemaMetadataFailsClosedWithoutRewritingGraph(GameTestHelper helper) {
        assertUnreplayablePreservesGraph(
                helper,
                new GuidedWorkspaceState(GuidedWorkspaceState.CURRENT_VERSION + 1, "Future", List.of("mathmod:self")),
                "Future-schema metadata must fail closed"
        );
    }

    private static void assertUnreplayablePreservesGraph(
            GameTestHelper helper,
            GuidedWorkspaceState workspace,
            String failureMessage
    ) {
        ProgramGraph graph = graphFor(executableInvocations());
        ItemStack talisman = talisman(workspace, graph);
        ItemStack before = talisman.copy();

        GuidedWorkspaceRead read = GuidedWorkspacePersistence.read(talisman);
        helper.assertTrue(read.status() == GuidedWorkspaceRead.Status.UNREPLAYABLE, failureMessage);
        ProgramGraph persisted = ProgramStorage.get(talisman).orElseThrow();
        helper.assertTrue(persisted.equals(graph), "Unreplayable metadata must not replace the authoritative graph");
        helper.assertTrue(ProgramStorage.validateExecutable(persisted).valid(),
                "An unreplayable workspace must retain its executable graph");
        helper.assertTrue(ProgramSurface.inscribed(persisted).inspect().graph().equals(graph),
                "An unreplayable workspace must retain read-only inspection of the graph");
        helper.assertTrue(ItemStack.isSameItemSameComponents(before, talisman),
                "Fail-closed workspace reads must not rewrite any talisman component");
        helper.succeed();
    }

    private static ItemStack talisman(GuidedWorkspaceState workspace, ProgramGraph graph) {
        ItemStack talisman = new ItemStack(ModItems.PROGRAMMED_TALISMAN.get());
        talisman.set(ModDataComponents.PROGRAM.get(), graph);
        talisman.set(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get(), workspace);
        return talisman;
    }

    private static List<CustomSpellInvocation> executableInvocations() {
        return List.of(
                CustomSpellInvocation.defaults(CustomSpellAction.SELF),
                CustomSpellInvocation.defaults(CustomSpellAction.UP_VECTOR),
                CustomSpellInvocation.defaults(CustomSpellAction.PUSH_SELF)
        );
    }

    private static ProgramGraph graphFor(List<CustomSpellInvocation> invocations) {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();
        workspace.loadInvocations(invocations);
        return workspace.toGraph();
    }
}
