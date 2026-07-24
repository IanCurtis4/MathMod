package com.mathmod.client.screen;

import com.mathmod.program.CustomSpellAction;
import com.mathmod.program.CustomSpellWorkspace;
import com.mathmod.runes.ProgramNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomGraphPresentationTest {
    @Test
    void actionOutputsBecomeStableNumberedReferences() {
        CustomSpellWorkspace workspace = rightBasisPush();

        List<CustomGraphPresentation.Binding> bindings = CustomGraphPresentation.bindings(
                workspace.toGraph(),
                workspace.steps()
        );

        assertTrue(bindings.stream().anyMatch(binding ->
                binding.sourceStep() == 1
                        && binding.targetStep() == 2
                        && binding.inputName().equals("vector")
        ));
    }

    @Test
    void constantVectorConstructionCollapsesIntoOneLiteral() {
        CustomSpellWorkspace workspace = rightBasisPush();

        List<CustomGraphPresentation.Binding> bindings = CustomGraphPresentation.bindings(
                workspace.toGraph(),
                workspace.steps()
        );

        assertTrue(bindings.stream().anyMatch(binding ->
                binding.targetStep() == 1
                        && "vec(0.7, 0.08, 0)".equals(binding.sourceLiteral())
        ));
        assertFalse(bindings.stream().anyMatch(binding ->
                binding.target().runeId().equals("mathmod:vector_from_numbers")
        ));
    }

    @Test
    void workspaceStepsRememberTheirActualOutputNodes() {
        CustomSpellWorkspace workspace = rightBasisPush();

        assertEquals("basis_right_world_6", workspace.steps().get(0).outputNodeId());
        assertEquals("push_7", workspace.steps().get(1).outputNodeId());
        assertEquals(workspace.toGraph().outputNodeId(), workspace.steps().get(1).outputNodeId());
    }

    @Test
    void generatedNodeCountersAreRemovedFromDisplaySymbols() {
        assertEquals(
                "basis_right_world",
                CustomGraphPresentation.symbol(new ProgramNode(
                        "basis_right_world_6",
                        "mathmod:transform_local_vector",
                        java.util.Map.of()
                ))
        );
        assertEquals(
                "anchor_origin",
                CustomGraphPresentation.symbol(new ProgramNode(
                        "anchor_origin",
                        "mathmod:anchor_origin",
                        java.util.Map.of()
                ))
        );
    }

    private static CustomSpellWorkspace rightBasisPush() {
        CustomSpellWorkspace workspace = new CustomSpellWorkspace();
        workspace.apply(CustomSpellAction.RIGHT_BASIS_VECTOR);
        workspace.apply(CustomSpellAction.PUSH_SELF);
        return workspace;
    }
}
