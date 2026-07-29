# Handoff: L0-TM-01

## Completed

- Added a monotonic per-attempt `ScopedCompileBudget` and the stable
  `COMPILE_STEP_LIMIT` failure identity. Structural validation, type checking,
  lowering, closure/binding creation, beta activation, literal resolution, and
  graph node/edge creation charge the same meter in the compiler route.
- Canonicalized diagnostics by phase, numeric structural path, and code, with
  duplicate phase/path/code identities collapsed.
- Added the trusted NUMBER-only `ScopedLiteralResolver`. It accepts only
  bounded locale-independent decimal syntax, rejects hexadecimal, NaN and
  infinities, canonicalizes negative zero to `0.0`, and verifies the trusted
  `mathmod:constant_number` descriptor before lowering.
- Added immutable `ScopedRuneSnapshot`, pure `ScopedProgramCompiler`, and
  `ScopedCompileResult`. The compiler captures one local rune view, checks,
  lowers, validates the resulting graph with `ProgramValidator`, and returns
  no graph on any issue.
- Preserved explicit-binding sharing and added regression evidence that
  repeated observations without a binder are not common-subexpression
  eliminated.
- Added pure tests for literal failure/canonicalization, the 4,097th logical
  charge boundary (via a one-step meter vector), graph-validation fail-closed
  behavior, and explicit observation sharing/no-CSE.

## Decisions implemented

- This is a pure compile boundary, not an inscription route. It does not call
  `ProgramStorage`, mutate a registry, touch an item/player/world, calculate
  resources or knowledge, or execute a graph.
- `ScopedProgramLowerer` keeps its source-compatible public entry point. The
  new compiler is the checked pure orchestration entry point; no existing
  caller is silently converted into persistence or gameplay behavior.
- NUMBER is the sole supported literal type. Additional literal types require
  the separate trusted descriptor review specified by the L0 contract.

## Files changed

- `src/main/java/com/mathmod/language/ScopedLanguageIssue.java`
- `src/main/java/com/mathmod/language/ScopedStructureValidator.java`
- `src/main/java/com/mathmod/language/ScopedTypeChecker.java`
- `src/main/java/com/mathmod/language/ScopedProgramLowerer.java`
- `src/main/java/com/mathmod/language/ScopedLoweringResult.java`
- `src/main/java/com/mathmod/language/ScopedTypeCheckResult.java`
- `src/main/java/com/mathmod/language/ScopedValidationResult.java`
- `src/main/java/com/mathmod/language/ScopedCompileBudget.java`
- `src/main/java/com/mathmod/language/ScopedLiteralResolver.java`
- `src/main/java/com/mathmod/language/ScopedRuneSnapshot.java`
- `src/main/java/com/mathmod/language/ScopedProgramCompiler.java`
- `src/main/java/com/mathmod/language/ScopedCompileResult.java`
- `src/test/java/com/mathmod/language/ScopedProgramCompilerTest.java`
- `docs/handoffs/L0_TM_01_HANDOFF.md`

## Contracts referenced

- `docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`, especially
  sections 4, 5, 6, 7, 8, 9, 10, and 13.
- `docs/L0_SCOPED_LANGUAGE_GAP_AUDIT.md`.
- `docs/P4_FUNCTION_LANGUAGE_CONTRACT.md`.
- `docs/P4_SEMANTIC_REVIEW.md`.
- `docs/FUNCTIONAL_LANGUAGE.md`.

## Tests and evidence

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.language.*' --rerun-tasks --no-daemon
```

Result: `BUILD SUCCESSFUL`; 24 tests completed, 0 failures.

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

## Migration impact

- None. No codec, persistent source schema, Data Component, StreamCodec,
  payload, network path, item component, or public API was introduced.

## Known limitations

- This is not the authoritative server compile service: registry generation,
  executable policy, resources, knowledge admission, target-item checks, and
  atomic commit are deliberately deferred to L0-TM-02/L0-TM-03.
- `ScopedRuneSnapshot` is a pure immutable definition capture only; active
  registry generation/reload publication remains a later server-owned scope.
- The existing effect-tail structural tests remain the authority for effect
  placement; this slice does not reclassify effect-plan constructors.

## Unresolved questions

- No escalation. Any need for a literal other than NUMBER, mutable registry
  generation behavior, graph semantics change, persistence, client code, or
  inscription behavior must be escalated under the later L0 slices.

## Next owner

- Terra High

## Exact next task

- `L0-TH-01`: read-only semantic review of the pure compile hardening.

## Files the next owner may edit

- `docs/L0_PURE_COMPILE_SEMANTIC_REVIEW.md` only, unless Sol dispatches a
  correction with separate ownership.

## Files the next owner must not edit

- `ProgramGraph`, `ProgramStorage`, `ModDataComponents`, all persistence and
  codec code, networking/payloads, items, screens, preview harnesses,
  resources/localization/Patchouli, public KubeJS/datapack APIs, and mutable
  registry implementation.
