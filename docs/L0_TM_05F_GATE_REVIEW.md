# L0-TM-05F Gate Review

**Reviewer:** Sol  
**Date:** 2026-07-29  
**Decision:** `NEEDS_FIX`  
**Correction:** `L0-TM-05F2`

## 1. Reproduced evidence

The repository handoff exists and the changed files are inside the authorized
L0-TM-05F boundary.

Sol reproduced:

```text
focused nine-class command:
48 tests, 0 failures, 0 errors, 0 skipped

runGameTestServer --no-daemon:
43/43 required GameTests passed

build --no-daemon:
BUILD SUCCESSFUL
```

The first plain `build` invocation stalled in three identifiable Gradle Java
processes. Sol terminated only those Gradle processes and reproduced the build
successfully with `--no-daemon`. No game server or unrelated Java process was
terminated.

R1, R2 and R4 are closed:

- success and missing knowledge now traverse a real `RuneProgrammerMenu`;
- the successful route checks exact persisted compiled graph, source envelope,
  resources and component absence;
- empty accepted resources are committed as an absent component.

The 33 previous preset identities/graphs are frozen by a repository hash, and
the focused theorem assertions now cover the AST, compile charge, wire
round-trip, counts, constants, sockets, output and sharing.

Two residual defects prevent acceptance.

## 2. Residual findings

### L0-TM-05F-R1 — The semantic matcher is still node-id dependent

`FactoredLeapTheoremTest.assertSemanticIsomorphism` hardcodes:

```text
f0 -> self
...
f11 -> push
```

It then obtains every compiled semantic identity through
`generatedToPresentation.get(node.id())`.

This proves only that the current compiler emitted the presently observed ids.
It does not implement the clarified invariant that node ids and collection
order are non-semantic. A compiler change from `f0...f11` to any other unique
ids would make the test fail even with an identical graph.

The fixed mapping in
`docs/L0_TM_05F_GRAPH_ORACLE_CLARIFICATION.md` documents the reproduced
difference; it was not authorization to turn generated ids into a test
contract.

The correction must derive a bijection from node semantics and named
connectivity, independent of both graphs' node ids and list order. It must
prove with adversarial test graphs that:

- renaming every compiled node preserves equivalence;
- reordering nodes and edges preserves equivalence;
- a missing/extra node or edge fails;
- a changed NUMBER value fails;
- a changed socket fails;
- a changed output fails;
- duplicated `self_player` fails.

### L0-TM-05F-R2 — R3 still does not prove tooltip reads or no false success

`factoredLeapReloadReadsWithoutMutationAndFailuresNeverReportSuccess` now
performs a real `ItemStack.CODEC` round-trip and proves that
`ScopedProgramPersistence.read` plus `ProgramStorage.get` do not mutate the
reloaded item. That closes persisted reload and two read authorities.

It still does not execute a tooltip-equivalent item read, despite the explicit
readiness requirement. `ProgrammedTalismanItem.appendHoverText` remains
unexercised for the reloaded theorem item.

The method also does not observe feedback or synchronization for any rejected
or rollback-failed menu attempt. Exact item equality proves no item mutation;
it does not prove that
`item.mathmod.programmed_talisman.saved` was not emitted. The handoff
explicitly acknowledges that feedback packets were not inspected, while the
method name and readiness contract claim no false success.

The correction must:

1. execute a tooltip-equivalent read on the reloaded Factored Leap item and
   prove exact component preservation;
2. observe that missing-knowledge failure does not emit saved feedback;
3. drive at least one injected commit failure through the real menu route and
   observe no saved feedback or success-only synchronization;
4. retain the existing 12-point direct transaction rollback matrix.

A stronger server-side observation of the actual feedback/synchronization
effect is required. Source-text containment alone is insufficient. If the
NeoForge mock cannot expose such observation without a new production seam,
Terra Medium must stop and escalate rather than weaken the vector.

## 3. Correction boundary — L0-TM-05F2

`L0-TM-05F2` is `READY` for Terra Medium.

It may modify only:

```text
src/main/java/com/mathmod/program/L0FactoredLeapGameTests.java
src/test/java/com/mathmod/program/FactoredLeapTheoremTest.java
src/test/java/com/mathmod/screen/RuneProgrammerFunctionalTheoremTest.java
docs/handoffs/L0_TM_05F2_HANDOFF.md
```

All production Java remains read-only, including
`FactoredLeapTheorem.java`, `ScopedFunctionalInscriptionService.java`,
`ScopedFunctionalInscriptionEntryPoint.java`,
`ScopedProgramComponentTransaction.java`, `ProgramPresets.java` and
`RuneProgrammerMenu.java`.

The already closed `ProgramPresetsTest` and
`ScopedFunctionalInscriptionEntryPointTest` are read-only for F2. Networking,
client/UI Java, schemas, Data Components, public APIs, persistence migration,
Patchouli, `ProgramGraph`, `GuidedWorkspaceState` and `ProgramSurfaceMode`
remain forbidden.

## 4. Required handoff and gate state

The correction must close F-R1 and F-R2 individually, reproduce the exact
nine-class focused command, all 43 GameTests and the standard build, then
produce:

```text
docs/handoffs/L0_TM_05F2_HANDOFF.md
```

`L0-TM-05` and `L0-TM-05F` remain `NEEDS_FIX`. No downstream task is
authorized before Sol accepts L0-TM-05F2.
