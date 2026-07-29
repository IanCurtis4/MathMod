# L0-TM-03F Gate Rereview

**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Reviewed handoff:** `docs/handoffs/L0_TM_03F_HANDOFF.md`  
**Previous review:** `docs/L0_TM_03F_GATE_REVIEW.md`

## Decision

The second L0-TM-03F handoff is not accepted. It closes the stale
generation/material, post-compile cancellation, source/result API shape,
`ProgramStorage` false-success and part of the read-matrix findings. The
required commands are green. However, the repository still does not contain
all exact vectors required by `docs/L0_ATOMIC_PERSISTENCE_READINESS.md` and
the previous bounded correction.

`L0-TM-03` and `L0-TM-03F` remain `NEEDS_FIX`. `L0-TM-04`, `L0-LU-01` and
`L0-TM-05` remain blocked.

## Closed since the previous review

- 4,096 calls to the JSON token counter are accepted and the 4,097th is
  rejected.
- 1,024 calls to the type-node counter are accepted and the 1,025th is
  rejected.
- BOM, truncated, trailing/multi-root, missing-field and wrong-type payloads
  are exercised by an active test.
- source/result separation is rejected by reflection over the coordinator
  method signatures.
- cancellation after pure compilation mutates nothing.
- stale rune generation mutates nothing.
- stale material definitions mutate nothing.
- an injected `ProgramStorage` transaction failure returns an invalid
  `ValidationResult` and restores the item.
- registered-stack source-only and physical Guided/source paths receive
  additional no-mutation coverage.

## Remaining blocking findings

### L0-TM-03F-R6A2 — exact wire vectors are still incomplete

`ScopedSourceWireCodecTest.exactAstArgumentAndExpressionDepthEdges` does not
exercise expression depth 256/257. Its last two assertions call
`rootLambda(..., 16)` and `rootLambda(..., 17)`, which exercise binding depth
16/17 and expression depths only 17/18. The mandatory expression-depth edge
remains absent.

The no-trim/no-default vector remains absent. No active focused test supplies
blank or surrounding-whitespace rune ids, input names, type ids or binder
hints and proves that the wire reader rejects them before model constructors
can trim or substitute values.

The active multibyte test does not prove the complete required boundaries:

- literal inputs use 160/161 two-byte characters, producing 320/322 UTF-8
  bytes rather than the required 640/641 byte cases;
- there is no active multibyte binder-hint assertion;
- the rune-id and input-name two-byte assertions cover their byte limits, but
  only through `CURRENT_UNREADABLE` for the rejected case rather than the
  expected stable field diagnostic.

The method
`legacyStrictJsonAndIndependentUtf8BoundariesAreRejectedAtExactEdges`
contains additional assertions but has no `@Test` annotation and is therefore
not part of the reported 21-method execution. Even that inactive method does
not reach the literal 640/641 byte cases.

If the accepted Java-length limit makes a nominal UTF-8 boundary unreachable
for a valid Java string, the correction must record and prove that precedence
explicitly rather than claiming the unreachable accepted edge was exercised.
No wire limit may be silently dropped.

### L0-TM-03F-R6B2 — changed-graph resource precedence is not proved

`resourcePolicyPreservesExactOldSelectionsOnlyForEqualGraph` proves:

- equal old/candidate graph preserves old resources;
- no old graph uses recommendations.

Its second call passes `Optional.empty()`. It does not pass an existing old
graph that is unequal to the candidate graph, so the mandatory changed-graph
branch is still unproved.

Add a distinct valid old graph and prove that the exact admitted
recommendations, not the old selections, are returned.

### L0-TM-03F-R6C2 — read classification matrix remains insufficient

`completeReadStateMatrixNeverMutatesOrCompiles` asserts only that each
source-only case is “not `ABSENT`”. It does not assert the required exact
classifications `CURRENT_VALID`, `CURRENT_UNREADABLE` and
`UNSUPPORTED_VERSION`.

The conflict loop adds only `program_guided_workspace`. It does not cover the
physical legacy `program_custom_actions` representation or an unreplayable
Guided state, both of which are explicitly part of the frozen conflict matrix.

The graph/source mismatch case is present only incidentally. Add an explicit
assertion that mismatch is not diagnosed, compiled, repaired or rewritten
during read and that the authoritative graph remains byte/component-equal.

### L0-TM-03F-R6D — handoff changed-file inventory is inaccurate

The correction materially changes
`src/test/java/com/mathmod/program/ScopedSourceWireCodecTest.java`, but the
handoff's “Testes” list names only:

```text
ScopedSourceEnvelopeTest.java
ScopedProgramComponentTransactionTest.java
ScopedProgramPersistenceTest.java
```

The required exact changed-file inventory must include
`ScopedSourceWireCodecTest.java` and any package-private production seams added
by this correction.

## Reproduced evidence

Focused suite:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.program.ScopedSourceEnvelopeTest `
  --tests com.mathmod.program.ScopedSourceWireCodecTest `
  --tests com.mathmod.program.ScopedProgramComponentTransactionTest `
  --tests com.mathmod.program.ScopedProgramPersistenceTest
```

Result: `BUILD SUCCESSFUL`; 21 active JUnit methods, 0 failures.

Dedicated server:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; `All 28 required tests passed`. Fourteen methods
belong to `L0ScopedSourcePersistenceGameTests`:

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

Standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result: `BUILD SUCCESSFUL`; 31 actionable tasks, 2 executed and 29 up-to-date.

These green results prove the delivered subset only.

## Ownership and scope

The newly observed production seams remain package-private and inside the
accepted L0-SOL-04 ownership. No change to `ProgramGraph`,
`GuidedWorkspaceState`, networking, client/UI, `ProgramSurfaceMode`, accepted
Data Component identity/schema or public API was found in the declared
L0-TM-03F correction.

The worktree still contains unrelated changes. They are not attributed to this
task without repository evidence and must remain untouched.

## Final bounded correction

Continue only L0-TM-03F in:

```text
src/test/java/com/mathmod/program/ScopedSourceWireCodecTest.java
src/test/java/com/mathmod/program/ScopedProgramComponentTransactionTest.java
src/main/java/com/mathmod/program/L0ScopedSourcePersistenceGameTests.java
docs/handoffs/L0_TM_03F_HANDOFF.md
```

No production change is expected. Stop and escalate before changing production
unless one of the remaining mandatory vectors cannot be driven through the
existing package-private seams.

Required:

1. exercise or explicitly resolve the exact expression-depth and multibyte
   boundary vectors;
2. add active no-trim/no-default assertions;
3. prove existing unequal graph uses exact recommendations;
4. assert exact source-only classifications;
5. cover both physical Guided representations and unreplayable conflict;
6. make graph/source mismatch and read non-mutation explicit;
7. correct the handoff file inventory;
8. rerun all three mandatory commands and report JUnit, L0 GameTest and global
   GameTest totals separately.
