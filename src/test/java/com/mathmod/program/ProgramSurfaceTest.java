package com.mathmod.program;

import com.mathmod.runes.ProgramGraph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramSurfaceTest {
    @Test
    void modeCapabilitiesKeepInspectorAndInscribedProofReadOnly() {
        assertTrue(ProgramSurfaceMode.GUIDED.workspaceMutable());
        assertTrue(ProgramSurfaceMode.GUIDED.inscriptionAllowed());
        assertEquals(ProgramSurfaceMode.Persistence.GUIDED_WORKSPACE, ProgramSurfaceMode.GUIDED.persistence());

        assertTrue(ProgramSurfaceMode.INSPECTOR.readOnly());
        assertFalse(ProgramSurfaceMode.INSPECTOR.inscriptionAllowed());
        assertEquals(ProgramSurfaceMode.Persistence.NONE, ProgramSurfaceMode.INSPECTOR.persistence());

        assertTrue(ProgramSurfaceMode.INSCRIBED.readOnly());
        assertEquals(ProgramSurfaceMode.Persistence.EXECUTION_GRAPH, ProgramSurfaceMode.INSCRIBED.persistence());
    }

    @Test
    void inspectionIsTransientAndRetainsExactGraphIdentity() {
        ProgramGraph graph = guidedState().replayableInvocations().map(invocations -> {
            CustomSpellWorkspace workspace = new CustomSpellWorkspace();
            workspace.loadInvocations(invocations);
            return workspace.toGraph();
        }).orElseThrow();

        ProgramSurface inspected = ProgramSurface.inscribed(graph).inspect();

        assertSame(graph, inspected.graph());
        assertEquals(ProgramSurfaceMode.INSPECTOR, inspected.mode());
        assertEquals(ProgramSurfaceMode.INSCRIBED, inspected.sourceMode());
        assertTrue(inspected.guidedWorkspace().isEmpty());
    }

    @Test
    void guidedReopenRequiresRecipeToReproduceAuthoritativeGraphExactly() {
        GuidedWorkspaceState state = guidedState();
        ProgramSurface authored = ProgramSurface.guided(state);

        assertTrue(ProgramSurface.reopenGuided(authored.graph(), state).isPresent());

        ProgramGraph changedBudget = new ProgramGraph(
                authored.graph().nodes(),
                authored.graph().edges(),
                authored.graph().outputNodeId(),
                authored.graph().budgetLimit() + 1
        );
        assertTrue(ProgramSurface.reopenGuided(changedBudget, state).isEmpty());
    }

    @Test
    void futureWorkspaceVersionCannotOpenAWriteSurface() {
        GuidedWorkspaceState future = new GuidedWorkspaceState(
                GuidedWorkspaceState.CURRENT_VERSION + 1,
                "Future",
                List.of(CustomSpellAction.SELF.persistentId())
        );

        assertThrows(IllegalArgumentException.class, () -> ProgramSurface.guided(future));
    }

    private static GuidedWorkspaceState guidedState() {
        return GuidedWorkspaceState.create(
                "Identity proof",
                List.of(
                        CustomSpellInvocation.defaults(CustomSpellAction.SELF),
                        CustomSpellInvocation.defaults(CustomSpellAction.UP_VECTOR),
                        CustomSpellInvocation.defaults(CustomSpellAction.PUSH_SELF)
                )
        );
    }
}
