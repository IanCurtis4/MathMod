package com.mathmod.program;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;

/** Focused shape checks; real registered-stack before/after fault vectors live in the L0 GameTest. */
class ScopedProgramComponentTransactionTest {
    @Test void exposesEveryBeforeAndAfterFailurePosition() {
        assertEquals(2, ScopedProgramComponentTransaction.Phase.values().length);
        assertArrayEquals(new ScopedProgramComponentTransaction.Phase[]{ScopedProgramComponentTransaction.Phase.BEFORE, ScopedProgramComponentTransaction.Phase.AFTER}, ScopedProgramComponentTransaction.Phase.values());
        assertEquals(12, 6 * ScopedProgramComponentTransaction.Phase.values().length);
    }

    @Test void stateEqualityIncludesByteContentOfScopedSource() {
        var first=new ScopedSourceEnvelope(1,new byte[]{4,5});
        var same=new ScopedSourceEnvelope(1,new byte[]{4,5});
        assertEquals(first,same,"transaction snapshots use envelope byte-content equality for rollback verification");
    }

    @Test void functionalCoordinatorNeverAcceptsCallerSuppliedCompileResult() {
        for (var method : ScopedFunctionalInscriptionService.class.getDeclaredMethods()) {
            for (var parameter : method.getParameterTypes()) {
                assertNotEquals(ScopedServerCompileResult.class, parameter,
                        "source/result binding must remain inside the coordinator");
            }
        }
    }

    @Test void resourcePolicyPreservesExactOldSelectionsOnlyForEqualGraph() {
        var workspace=new CustomSpellWorkspace(); workspace.loadInvocations(List.of(CustomSpellInvocation.defaults(CustomSpellAction.SELF)));
        var graph=workspace.toGraph(); var old=List.of(new ResourceSelection("feather",2)); var recommended=List.of(new ResourceSelection("paper",3));
        assertEquals(old,InscriptionResourcePolicy.resourcesToPersist(Optional.of(graph),old,graph,recommended));
        var changedWorkspace=new CustomSpellWorkspace(); changedWorkspace.loadInvocations(List.of(CustomSpellInvocation.defaults(CustomSpellAction.SELF),CustomSpellInvocation.defaults(CustomSpellAction.UP_VECTOR)));
        assertEquals(recommended,InscriptionResourcePolicy.resourcesToPersist(Optional.of(graph),old,changedWorkspace.toGraph(),recommended));
    }
}
