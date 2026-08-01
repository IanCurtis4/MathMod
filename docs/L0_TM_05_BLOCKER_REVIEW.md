# L0-TM-05 Blocker Review

**Owner:** Sol  
**Date:** 2026-07-29  
**Decision:** bounded ownership amendment; implementation gate remains open

## 1. Repository evidence

Terra Medium's escalation is valid in part.

The dedicated server was reproduced from the current working tree with:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat runGameTestServer --no-daemon
```

NeoForge discovered 43 GameTests. Three required tests failed:

```text
factoredleapallcommitfaultsrollbackexactsourcebytes
factoredleapmenuroutepersistsexactsourcegraphandresources
factoredleapexecutesforwardandupwardoutcome
```

The rollback test returned `COMMIT_FAILED` with `fired=false` at the first
`BEFORE` injection. The menu-route test also returned `COMMIT_FAILED`.
Therefore the functional request reaches the commit authority but is rejected
before the production/fault transaction enters its first mutation point.

## 2. Root cause and ownership decision

The immediate cause is in
`ScopedFunctionalInscriptionService.inscribe`, not in
`ScopedProgramComponentTransaction`.

For the required absent program name, the service constructs:

```text
name = ""
hasName = false
```

The transaction preflight correctly applies absence by removing
`PROGRAM_NAME`. Recapturing that probe yields:

```text
name = null
hasName = false
```

`State` equality is intentionally exact, so the preflight rejects the
non-canonical candidate before any injection. The same representational
problem is latent for an empty resource selection: an absent component must
not retain a non-null empty-list value in the candidate state.

The accepted transaction machine is behaving consistently with its exact
state invariant. There is no repository evidence that requires changing it.

Terra Medium is therefore authorized to modify exactly:

```text
src/main/java/com/mathmod/program/ScopedFunctionalInscriptionService.java
```

The correction is limited to canonical construction of absent candidate
component values:

- absent `PROGRAM_NAME` has a null value and `hasName == false`;
- absent `PROGRAM_RESOURCES` has a null value and
  `hasResources == false`;
- present values and all six presence flags retain their accepted meaning.

The correction must not:

- change `ScopedProgramComponentTransaction.java`;
- weaken exact preflight or rollback equality;
- add a second mutation path;
- reorder or remove the immediate final authority rechecks;
- separate source from its compiled result;
- change any public signature, Data Component identity/codec, schema,
  networking, client code, UI code, `ProgramGraph`,
  `GuidedWorkspaceState` or `ProgramSurfaceMode`.

`ScopedProgramComponentTransaction.java` remains read-only. If the bounded
service correction does not make both commit-path GameTests reach the existing
machine, Terra Medium must stop and return new evidence rather than expanding
scope.

## 3. Independent GameTest defect

The third failure is separate from persistence. The movement GameTest catches
the known mock-player `sync_attachments` runtime exception and permits a null
`ProgramExecutionResult`, but its assertion message eagerly calls
`result.messageKey()`. That produces the reproduced null dereference before
the already-computed movement delta is asserted.

`src/main/java/com/mathmod/program/L0FactoredLeapGameTests.java` is already
owned by L0-TM-05. Terra Medium must make that diagnostic null-safe without
weakening the required forward-and-upward outcome assertion. A swallowed
execution failure or an unconditional pass is not acceptable.

## 4. Required evidence before handoff

Terra Medium must:

1. prove the canonical candidate survives direct capture and `ItemStack.copy`;
2. prove the successful theorem route persists exact graph, source and
   resources while leaving `PROGRAM_NAME` absent;
3. prove all 12 before/after injection attempts reach the existing single
   transaction machine and restore the exact prior six-component state,
   including future-schema source bytes;
4. prove an empty resource result is represented as an absent component, or
   add an equivalent focused regression vector;
5. prove the movement outcome after the permitted mock-player synchronization
   exception without dereferencing a null result;
6. reproduce the complete focused command from
   `L0_INTERNAL_GAMEPLAY_INTEGRATION_READINESS.md`;
7. reproduce `runGameTestServer --no-daemon` and report the ten named L0
   tests separately from the global total;
8. reproduce the standard `build`;
9. produce `docs/handoffs/L0_TM_05_HANDOFF.md` with the exact delta and
   limitations.

Build green does not replace these contractual vectors.

## 5. Gate state

`L0-TM-05` remains `IN_PROGRESS`. This document removes the ownership blocker
only for the bounded service correction above. No L0-TM-05 acceptance is
granted, and no downstream task is authorized.

The later handoff review supersedes this operational status without reopening
the ownership decision: see `docs/L0_TM_05_GATE_REVIEW.md`.
