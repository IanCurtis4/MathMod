# Handoff: L0-TM-01F3

## Completed

- Preserved raw NUMBER literal payloads and rejects surrounding whitespace at
  the trusted resolver.
- Preserved authored lambda-body diagnostic paths through beta reduction.
- Added the package-private `ScopedProgramCompiler.compile(source, budget)`
  seam; the public compile method remains unchanged and delegates to it with a
  fresh meter.
- Proved the real pipeline cost of identity application is 17 charges, exact
  4,096 success, and 4,097 failure with only `COMPILE_STEP_LIMIT` and no graph.
- Added exact AST, binding, application and literal boundary tests, effect-tail
  nesting tests, explicit sharing/no-CSE tests, missing-combinator failure, and
  regression-sensitive repeated public checker/lowerer attempts.
- Added phase/path/code/duplicate diagnostic-normalization assertions.

## Vector matrix

| Vector(s) | Exact test method | Oracle |
|---|---|---|
| OBS-SHARE-1, 3, 5, 6 | `observationSharingVectorsOneThreeFiveAndSixAreExplicitOnly` | Explicit binders share one source; literal shares one constant; lambda observation rejects/no graph. |
| OBS-SHARE-2, 4 | `explicitLetSharesObservationButRepeatedTermsDoNotUseCse` | Let shares; unbound repeated observation remains two nodes. |
| TAIL-1, 2, 8; BOUND-8 | `tailOneTwoEightAndBoundEightHavePureCompilerClassifications` | Unit candidates compile; non-Unit remains pure candidate only; unknown combinator fails closed. |
| TAIL-3, 4 | `effectsMayOnlyOccupyTheTailPosition` | Let-value/application-argument effects reject. |
| TAIL-5, 7 | `tailFiveAndSevenRejectNestedEffectsAndTailEightRemainsDeferred` | Nested effects reject. |
| TAIL-6 | `lambdaBodiesAcceptPureCallsAndRejectObservationsOrEffects` | Effect/observation lambda body rejects. |
| BOUND-1–4 | `boundOneTwoThreeAndFourUseExactStructuralBoundaries` | Exact 256/257, 16/17, 64/65, 160/161 boundaries. |
| BOUND-5–6 | `boundFiveAndSixUseTheCompleteSharedPipelineAt4096And4097` | Same complete pipeline at exact meter boundary. |
| BOUND-7 | `graphValidationFailureNeverReturnsAPartialGraph` | Invalid lowered graph returns no graph. |

## Files changed

- `src/main/java/com/mathmod/language/ScopedProgramCompiler.java`
- `src/test/java/com/mathmod/language/ScopedStructureValidatorTest.java`
- `src/test/java/com/mathmod/language/ScopedProgramCompilerTest.java`
- `docs/handoffs/L0_TM_01F3_HANDOFF.md`

## Tests and evidence

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.language.*' --rerun-tasks --no-daemon
```

Result: `BUILD SUCCESSFUL`.

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon
```

Result: pending rerun; the first final-state attempt exceeded the local 64s tool window before producing a result.

## Migration impact

- None. No persistence, codec, component, registry mutation, networking,
  item, graph semantics, client, execution-policy, or public API changed.

## Known limitations

- TAIL-8 deliberately does not claim executable-policy or inscription
  admission; server admission remains L0-TM-02 work.

## Next owner

- Sol

## Exact next task

- Re-run the standard build, then review L0-TM-01F3 against the two L0 gate
  reviews before unblocking L0-TH-01.
