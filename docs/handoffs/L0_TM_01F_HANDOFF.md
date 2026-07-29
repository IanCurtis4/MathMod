# Handoff: L0-TM-01F

## Completed corrections

- Public `ScopedTypeChecker.check` and `ScopedProgramLowerer.lower` now create
  a fresh meter per call; compiler-internal stages still share the one attempt
  meter.
- Application lowering charges both beta activation and its administrative
  binding.
- NUMBER resolution now requires the complete frozen descriptor: enabled
  `mathmod:constant_number`, no inputs, NUMBER output, PURE purity, and
  `constant_number` executor key.
- Type checking and lowering now use indexed `arguments[n]` structural paths,
  matching structural validation; literal failure no longer adds a spurious
  root `FUNCTION_RESULT_FORBIDDEN` issue.
- Added regressions for per-call meter freshness, descriptor purity/executor,
  representative NUMBER grammar/canonical values, literal length, stable
  diagnostic normalization, and no secondary result diagnostic.

## Blocking escalation

`L0-1-R4` requires rejection of NUMBER values with leading/trailing
whitespace. This cannot be implemented under L0-TM-01F ownership:
`ScopedExpression.Literal` (read-only in the contract and correction) trims
`encodedValue` in its public record constructor before the trusted resolver is
called. Consequently `" 1"` and `"1 "` are indistinguishable from `"1"` in
all owned production files.

Changing that behavior requires an explicit Sol decision and ownership for
`src/main/java/com/mathmod/language/ScopedExpression.java`, with compatibility
review because it changes the in-memory authored-source model. No workaround
can prove the required whitespace rejection without inventing data not present
at the resolver boundary.

Because the same mandatory vector set remains incomplete until that decision,
this handoff is an escalation rather than a request for acceptance.

## Files changed

- `src/main/java/com/mathmod/language/ScopedTypeChecker.java`
- `src/main/java/com/mathmod/language/ScopedProgramLowerer.java`
- `src/main/java/com/mathmod/language/ScopedLiteralResolver.java`
- `src/test/java/com/mathmod/language/ScopedTypeCheckerTest.java`
- `src/test/java/com/mathmod/language/ScopedProgramCompilerTest.java`
- `docs/handoffs/L0_TM_01F_HANDOFF.md`

## Tests and evidence

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.language.*' --rerun-tasks --no-daemon
```

Result: `BUILD SUCCESSFUL`; 28 tests completed, 0 failures.

## Migration impact

- None. No persistence, codec, Data Component, payload, network, item,
  registry mutation, graph semantics, client, or public API changed.

## Next owner

- Sol

## Exact next task

- Decide whether to grant a narrowly scoped `ScopedExpression.Literal`
  raw-whitespace-preservation correction, or revise the frozen literal
  whitespace requirement. Do not advance L0-TH-01 before resolving it.
