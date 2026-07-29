# L0-TM-03F Gate Rereview 2

**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Reviewed handoff:** `docs/handoffs/L0_TM_03F_HANDOFF.md`  
**Previous review:** `docs/L0_TM_03F_GATE_REREVIEW.md`

## Decision

The third L0-TM-03F handoff is not accepted. R6B2 and R6C2 are now closed, and
the required commands are green. Two narrowly bounded evidence/documentation
findings remain.

`L0-TM-03` and `L0-TM-03F` remain `NEEDS_FIX`. `L0-TM-04`, `L0-LU-01` and
`L0-TM-05` remain blocked.

## Closed in this correction

- expression depth 256 is accepted and 257 is rejected directly by the real
  `Limits.expression` counter;
- active blank/surrounding-whitespace vectors cover rune id, binder hint,
  input name and type id;
- literal Java-length precedence over the unreachable nominal UTF-8 ceiling is
  explicitly exercised with three-byte characters;
- an existing unequal old graph uses the exact recommendations;
- source-only reads assert `CURRENT_VALID`, `CURRENT_UNREADABLE` and
  `UNSUPPORTED_VERSION` exactly;
- conflicts cover current Guided workspace, legacy custom actions and
  unreplayable Guided state;
- graph/source mismatch explicitly preserves the authoritative graph and
  performs no repair or rewrite;
- `ScopedSourceWireCodecTest.java` is now named in the handoff's test inventory.

## Remaining blocking findings

### L0-TM-03F-R6A3 — binder-hint multibyte precedence is still not active

The previous rereview explicitly identified the absence of an active
multibyte binder-hint assertion. The new
`noTrimOrDefaultPrecedenceIsExplicit` test adds a three-byte literal vector,
but binder hints are still exercised only with ASCII 32/33 and
blank/whitespace values.

Because the accepted Java-length ceiling of 32 makes the nominal 128/129 UTF-8
boundary unreachable for a valid Java string, add the analogous active
precedence proof:

- 32 three-byte binder-hint characters are accepted;
- 33 three-byte binder-hint characters are rejected with the stable field
  diagnostic;
- the assertion message records that Java length controls before the nominal
  UTF-8 ceiling.

The old private method with two-byte hint assertions has no `@Test` and does
not close this finding.

### L0-TM-03F-R6D2 — production changed-file inventory remains incomplete

The handoff now lists `ScopedSourceWireCodecTest.java`, but its production list
still omits:

```text
src/main/java/com/mathmod/program/ScopedSourceWireCodec.java
```

The handoff itself states that the missing-`kind` case revealed and fixed a
`NullPointerException`; the current production codec contains that correction.
Therefore this file is part of the correction delta and must be present in the
exact production changed-file inventory.

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

Result: `BUILD SUCCESSFUL`; 22 active JUnit methods, 0 failures.

Dedicated server:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; `All 28 required tests passed`. Fourteen methods
belong to `L0ScopedSourcePersistenceGameTests`, with the same names reported by
the handoff.

Standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result: `BUILD SUCCESSFUL`; 31 actionable tasks, 1 restored from cache and 30
up-to-date.

Green execution proves the delivered subset only.

## Final bounded correction

Continue only L0-TM-03F in:

```text
src/test/java/com/mathmod/program/ScopedSourceWireCodecTest.java
docs/handoffs/L0_TM_03F_HANDOFF.md
```

No production edit is authorized or expected.

Required:

1. add the active 32/33 three-byte binder-hint precedence assertions;
2. add `ScopedSourceWireCodec.java` to the production changed-file inventory;
3. rerun the focused command, GameTests and build;
4. preserve the separate JUnit, L0 GameTest and global GameTest totals.
