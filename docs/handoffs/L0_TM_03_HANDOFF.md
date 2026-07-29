# L0-TM-03 Handoff — Scoped Source Persistence and Atomic Commit

## Result

Implemented the L0 scoped-source persistence boundary. `ProgramGraph` remains
the sole executable item authority; `mathmod:program_scoped_source` is bounded,
optional authoring metadata and has no network synchronization.

## Changed files

Production:

- `src/main/java/com/mathmod/registry/ModDataComponents.java`
- `src/main/java/com/mathmod/program/ProgramStorage.java`
- `src/main/java/com/mathmod/program/ScopedSourceEnvelope.java`
- `src/main/java/com/mathmod/program/ScopedSourceWireCodec.java`
- `src/main/java/com/mathmod/program/ScopedSourceRead.java`
- `src/main/java/com/mathmod/program/ScopedCommitAuthority.java`
- `src/main/java/com/mathmod/program/ScopedCommitResult.java`
- `src/main/java/com/mathmod/program/ScopedProgramComponentTransaction.java`
- `src/main/java/com/mathmod/program/ScopedProgramPersistence.java`
- `src/main/java/com/mathmod/program/ScopedFunctionalInscriptionService.java`

Tests:

- `src/test/java/com/mathmod/program/ScopedSourceEnvelopeTest.java`
- `src/test/java/com/mathmod/program/ScopedSourceWireCodecTest.java`
- `src/main/java/com/mathmod/program/L0ScopedSourcePersistenceGameTests.java`

Documentation:

- `docs/handoffs/L0_TM_03_HANDOFF.md`

## Persistence and read boundary

- Registered exactly `mathmod:program_scoped_source` as persistent and cached
  `ScopedSourceEnvelope`; it deliberately has neither `networkSynchronized`,
  `StreamCodec`, nor packet/payload use.
- The envelope preserves every signed schema integer, bounds payloads at
  262,144 bytes, defensively copies bytes, and uses byte-content equality.
- Schema-1 decoding is streaming, strict UTF-8, rejects BOM/trailing roots,
  duplicate/missing/unknown/inapplicable fields, invalid tags and non-minimal
  integers. It performs no rune lookup and no compile.
- The deterministic writer emits contract field/tag order and compact UTF-8.
- Reads are non-mutating and classify absent/current-valid/current-unreadable,
  unsupported schema and physical Guided/source conflict. A future or unreadable
  envelope remains byte-exact recovery data; it cannot replace the graph.

## Commit boundary

- Functional inscription has one internal source-bound coordinator; no API
  accepts a caller-provided `(source, compile result)` pair.
- It captures target/copy/six-component state, invokes server compile for that
  source, encodes that same source, rechecks target identity/components, rune
  generation, knowledge snapshot/value and material catalog, then applies one
  six-component transaction.
- The transaction validates its full candidate state on an off-item copy,
  applies the same fixed six-component state machine to the target, verifies
  equality and restores the exact snapshot after an injected/application failure.
- Functional success writes graph, source, accepted name and resources while
  removing both Guided representations. Graph-only, Guided and clear routes
  use the same transaction and clear scoped source where required.

## Verification

Focused command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat cleanTest test --tests com.mathmod.program.ScopedSourceEnvelopeTest --tests com.mathmod.program.ScopedSourceWireCodecTest --no-build-cache
```

Result: `BUILD SUCCESSFUL`; 11 JUnit methods, 0 failures/errors/skips. Vectors
cover defensive copy/content equality, maximum envelope size, signed schema
round-trip, malformed UTF-8/JSON/fields/tags/integers, all six expression tags,
canonical encoding, literal and per-call argument bounds, combined 256-node /
255-argument boundary, budget, type-depth, binding-depth, application and
rune/input/binder identifier limits.

Dedicated server command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; **22/22** required GameTests passed. The L0 cases
exercise current/unreadable/future/conflict read preservation, graph-only and
Guided clearing, functional success, cancellation/stale target rejection,
six-position injected rollback, explicit clear, and real `ItemStack` codec
round-trip retaining unreadable/future envelope bytes.

Standard command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build
```

Result: `BUILD SUCCESSFUL` (31 actionable tasks; 1 executed, 30 up-to-date).

## Scope and limitations

No client projection, reconnect claim, mutable editor, source request packet,
network payload, UI/menu/item-use route, public API, Data Component alias,
`ProgramGraph` semantic change, or Guided schema change was added. The evidence
is dedicated-server/item-codec evidence only; it intentionally does not claim
client network visibility or reconnect behavior.
