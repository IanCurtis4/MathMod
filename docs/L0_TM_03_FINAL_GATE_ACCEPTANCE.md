# L0-TM-03 Final Gate Acceptance

**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Accepted tasks:** `L0-TM-03`, `L0-TM-03F`  
**Accepted handoff:** `docs/handoffs/L0_TM_03F_HANDOFF.md`

## Decision

The scoped-source persistence and atomic-authority gate is accepted. R1-R6
from `docs/L0_TM_03_GATE_REVIEW.md` and every bounded correction in the three
L0-TM-03F reviews are closed by repository evidence.

`L0-TM-03` and `L0-TM-03F` transition to `DONE` with `ACCEPT`.

## R1-R6 closure

- **R1:** player knowledge is captured for compilation and consulted again as
  live server authority immediately before commit.
- **R2:** canonical source, name, resources and the complete candidate are
  built off-item before all final authority rechecks.
- **R3:** production and tests use one six-component state machine with
  off-item preflight, 12 before/after injection positions, byte-exact source
  rollback verification and explicit severe rollback-failure handling.
- **R4:** both validated `ProgramStorage` routes convert transaction failure to
  an invalid result; the registered-item injection vector proves no false
  success.
- **R5:** the persistent codec accepts 262,144 payload elements and rejects
  262,145; the envelope constructor enforces the same bound.
- **R6:** all mandatory envelope, codec, strict-read, authority, resource,
  transaction, rollback and dedicated-server vectors are present.

The final R6 closure includes 4,096/4,097 JSON tokens, 1,024/1,025 type nodes,
all structural bounds, active Java-length/UTF-8 precedence for literal and
binder hint, no-trim/no-default rejection, inseparable source/result binding,
equal/changed graph resource precedence, every stale authority, exact
source-only classifications, both Guided representations, unreplayable
conflict, explicit graph/source mismatch, all transitions and opaque
future/unreadable roundtrip.

## Reproduced commands

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.program.ScopedSourceEnvelopeTest `
  --tests com.mathmod.program.ScopedSourceWireCodecTest `
  --tests com.mathmod.program.ScopedProgramComponentTransactionTest `
  --tests com.mathmod.program.ScopedProgramPersistenceTest
```

Result: `BUILD SUCCESSFUL`; 22 active JUnit methods, 0 failures.

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; `All 28 required tests passed`.

Fourteen methods belong to `L0ScopedSourcePersistenceGameTests`:

1. `validFutureAndConflictReadsNeverRewriteGraph`
2. `existingInscriptionRoutesClearScopedSourceAtomically`
3. `injectedBeforeAndAfterComponentFailuresRestoreCompleteSnapshot`
4. `staleLiveKnowledgeMutatesNothing`
5. `cancelledFunctionalRequestMutatesNothing`
6. `cancellationAfterPureCompilationMutatesNothing`
7. `functionalSuccessWritesCompleteStateAndClearsGuided`
8. `staleFunctionalTargetMutatesNothing`
9. `staleRuneGenerationMutatesNothing`
10. `staleMaterialCatalogMutatesNothing`
11. `explicitClearRemovesCompleteProgramState`
12. `storageCommitFailureIsNeverReportedValid`
13. `itemCodecRoundTripRetainsOpaqueUnreadableAndFutureBytes`
14. `completeReadStateMatrixNeverMutatesOrCompiles`

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result: `BUILD SUCCESSFUL`; 31 actionable tasks, 1 restored from cache and 30
up-to-date.

## Identity, ownership and exclusions

The accepted component remains exactly `mathmod:program_scoped_source`, using
`ScopedSourceEnvelope.CODEC`, persistent cache encoding and no network
synchronization or stream codec. Existing component identities and schemas are
unchanged.

The exact handoff inventory is inside L0-SOL-04 ownership. No L0-TM-03/F change
to `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, networking,
client/UI or public API is accepted or required. Unrelated worktree changes
are not attributed to this task.

This gate proves common/server persistence, strict reads, server-owned
compile/commit authority, registered item-codec persistence and dedicated
server behavior. It does not claim client visibility, reconnect/network
projection, mutable editing, source migration, DSL or theorem completion.

## Downstream decision

The persistence dependency of `L0-TM-04` is now closed, but implementation is
not directly dispatchable. The authority contract still requires:

1. a new exact ownership assignment for all A1 screen and preview files;
2. an approved payload contract before any client projection/diagnostic DTO.

The next and only `READY` task is therefore:

```text
L0-SOL-05 — Read-only Functional Projection Readiness
```

It is documentation-only and must freeze local-versus-DTO projection,
source/conflict/unavailable precedence, authored/reduced/graph labels,
structural diagnostics, keyboard/narrator evidence, exact ownership and
explicit exclusion of mutable editing, source transport, inscription,
persistence/schema changes and public APIs.

```text
L0-TM-03 DONE (ACCEPT)
    -> L0-TM-03F DONE (ACCEPT)
    -> L0-SOL-05 READY

L0-TM-04 BLOCKED on accepted L0-SOL-05
L0-LU-01 BLOCKED on accepted projection scope + frozen theorem specification
L0-TM-05 BLOCKED on server + persistence + UI + Luna gates
```
