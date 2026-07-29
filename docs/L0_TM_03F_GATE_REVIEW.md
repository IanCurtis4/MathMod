# L0-TM-03F Gate Review

**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Reviewed handoff:** `docs/handoffs/L0_TM_03F_HANDOFF.md`  
**Contracts:** `docs/L0_ATOMIC_PERSISTENCE_READINESS.md` and
`docs/L0_SCOPED_SOURCE_WIRE_FORMAT_CONTRACT.md`

## Decision

L0-TM-03F is not accepted. The production delta closes the concrete R1-R5
implementation defects and the four focused classes now exist, but R6 remains
open: the delivered tests do not exercise all mandatory readiness vectors.
Several statements in the handoff overstate the repository evidence.

`L0-TM-03` and `L0-TM-03F` remain `NEEDS_FIX`. `L0-TM-04`, `L0-LU-01` and
`L0-TM-05` remain blocked. A green build does not replace the missing
contractual coverage.

## R1-R6 disposition

### R1 — closed in implementation and evidence

`ScopedCommitAuthority` now owns a `Supplier<PlayerKnowledge>`.
`ScopedFunctionalInscriptionService` captures the live value used for
compilation and reads it again at the final precommit boundary.
`staleLiveKnowledgeMutatesNothing` changes the supplied value after compilation
and proves `KNOWLEDGE_STALE` with exact item preservation.

### R2 — closed in implementation

The canonical source envelope, accepted name, resource selection and complete
candidate `State` are constructed before the final rechecks. Cancellation,
target identity and complete components, rune generation, knowledge snapshot,
live player knowledge and material definitions are then checked immediately
before the transaction. The coordinator accepts the source and internally
obtains its compile result; it exposes no independently supplied
source/result pair.

R6 still requires direct evidence that this binding cannot be separated.

### R3 — closed in implementation and delivered GameTest subset

`ScopedProgramComponentTransaction.execute` is the single production/test
state machine. It captures rollback state, performs the complete off-item
preflight, injects before and after each of the six component applications,
verifies the candidate, restores the captured state on failure and verifies
record equality. `ScopedSourceEnvelope.equals` makes source comparison
byte-content based. Rollback failure is logged as a severe invariant breach
and the result remains failure.

`injectedBeforeAndAfterComponentFailuresRestoreCompleteSnapshot` exercises all
12 forward failure positions on the registered item path and verifies the
complete six-component snapshot. No separate rollback-failure injection is
claimed by this gate.

### R4 — closed by production inspection

Both validated `ProgramStorage` save routes now convert a false transaction
result to a `ValidationResult` containing an error, so `valid()` cannot expose
commit failure as success. Public signatures are unchanged.

R6 still requires a regression test for this behavior.

### R5 — closed in implementation and evidence

The persistent envelope codec uses `Codec.BYTE.listOf(0, 262_144)`. The focused
codec test decodes 262,144 elements successfully and rejects 262,145. The
constructor independently enforces the same outer bound.

### R6 — open

The four focused classes exist and the filtered run contains 16 passing JUnit
methods, but mandatory vectors are absent or are not isolated at their exact
boundary. The handoff's assertion that all readiness vectors are covered is
not supported by the repository.

## Blocking findings

### L0-TM-03F-R6A — strict wire-limit matrix is incomplete

`ScopedSourceWireCodecTest` has no 4,096/4,097 total JSON
value/container vector and no 1,024/1,025 total type-node vector.

It also does not exercise the required multibyte boundaries:

- literal 640/641 UTF-8 bytes independently of the 160/161 Java limit;
- hint 128/129 UTF-8 bytes independently of 32/33 Java characters;
- rune id 256/257 UTF-8 bytes;
- input name 128/129 UTF-8 bytes.

The combined `wideArguments(15/16)` case jumps from 256 expressions/255
arguments to 273 expressions/272 arguments. It therefore does not isolate AST
257 or total arguments 256. Expression depth 256/257 is also absent.

The strict JSON matrix remains incomplete for BOM, truncated JSON, explicit
multi-root input, missing and wrong-type fields. The no-trim/no-default
constructor guard and the free-parameter case are not proved.

### L0-TM-03F-R6B — authority and binding matrix is incomplete

The L0 GameTests prove stale target and live knowledge, but they contain no
stale rune-generation or stale material-catalog vector.

`cancelledFunctionalRequestMutatesNothing` supplies a cancellation that is
already true, so it returns before compilation. It does not prove cancellation
after compilation and immediately before mutation.

There is no focused test that proves:

- source/result binding cannot be separated;
- equal-graph commit preserves the exact old resource selections;
- changed-graph commit uses admitted recommendations;
- `ProgramStorage` returns an invalid result when the component transaction
  fails.

### L0-TM-03F-R6C — read-state matrix is incomplete

The delivered read tests cover graph plus valid/unreadable/future source,
one Guided/future conflict and a null item. They do not cover the complete
mandatory state matrix:

- valid, malformed and future source-only;
- neither component on a real registered stack;
- source/graph mismatch without compilation or mutation;
- Guided plus source for valid, unreadable, future and unreplayable Guided
  combinations;
- direct proof that read invokes no compiler, migration or mutation authority.

The existing implementation is read-only by inspection, but the readiness
made these vectors mandatory evidence.

## Reproduced commands

Focused suite:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.program.ScopedSourceEnvelopeTest `
  --tests com.mathmod.program.ScopedSourceWireCodecTest `
  --tests com.mathmod.program.ScopedProgramComponentTransactionTest `
  --tests com.mathmod.program.ScopedProgramPersistenceTest
```

Result: `BUILD SUCCESSFUL`; 16 JUnit methods are declared by the four filtered
classes and all passed.

Dedicated server:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; `All 23 required tests passed`. Nine methods belong
to `L0ScopedSourcePersistenceGameTests`:

1. `validFutureAndConflictReadsNeverRewriteGraph`
2. `existingInscriptionRoutesClearScopedSourceAtomically`
3. `injectedBeforeAndAfterComponentFailuresRestoreCompleteSnapshot`
4. `staleLiveKnowledgeMutatesNothing`
5. `cancelledFunctionalRequestMutatesNothing`
6. `functionalSuccessWritesCompleteStateAndClearsGuided`
7. `staleFunctionalTargetMutatesNothing`
8. `explicitClearRemovesCompleteProgramState`
9. `itemCodecRoundTripRetainsOpaqueUnreadableAndFutureBytes`

An initial isolated-shell attempt without reapplying `GRADLE_USER_HOME` failed
before test discovery because the JVM received duplicate
`cpw.mods.bootstraplauncher` locations from two Gradle homes. Repeating in the
required `C:\codex-gradle-a0` environment completed successfully.

Standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result: `BUILD SUCCESSFUL`; 31 actionable tasks, 30 up-to-date and `test`
restored from cache.

## Ownership and prohibited-surface review

The files declared by the L0-TM-03F handoff are inside the accepted
L0-SOL-04 ownership. Repository inspection found no L0-TM-03F claim of changes
to `ProgramGraph`, `GuidedWorkspaceState`, networking, client/UI,
`ProgramSurfaceMode`, public APIs or existing Data Component identities and
schemas. `mathmod:program_scoped_source` remains persistent, cache-encoded and
not network-synchronized.

The worktree also contains many unrelated tracked and untracked changes. Their
presence is not attributed to L0-TM-03F without repository evidence. They must
remain untouched by the bounded correction.

## Bounded correction

Continue only L0-TM-03F. Do not start a downstream task.

Writable files remain limited to the accepted L0-SOL-04 ownership, with the
smallest expected delta in:

```text
src/test/java/com/mathmod/program/ScopedSourceWireCodecTest.java
src/test/java/com/mathmod/program/ScopedProgramComponentTransactionTest.java
src/test/java/com/mathmod/program/ScopedProgramPersistenceTest.java
src/main/java/com/mathmod/program/L0ScopedSourcePersistenceGameTests.java
docs/handoffs/L0_TM_03F_HANDOFF.md
```

Package-private test seams may be added only inside the already-authorized L0
production files when a mandatory stale authority or transaction-failure
vector cannot otherwise be driven. No public signature, component identity,
schema, network/client behavior or new dependency is authorized.

Required correction:

1. add every missing exact-boundary and strict JSON vector in R6A;
2. prove post-compilation request cancellation, stale generation and stale
   materials without mutation;
3. prove inseparable source/result binding and exact resource precedence;
4. prove `ProgramStorage` cannot report commit failure as valid;
5. complete the read/classification/no-compile/no-mutation matrix in R6C;
6. report focused JUnit methods and named L0 GameTests separately from global
   totals;
7. rerun the three mandatory commands with `C:\codex-gradle-a0`;
8. update the handoff without overstating client, reconnect or network
   coverage.

Stop and escalate if these vectors require an unlisted dependency, a public
API, a network/client route, a schema/identity change or an edit outside the
accepted ownership.
