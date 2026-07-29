# L0-TM-01F2 Gate Review

**Task:** `L0-TM-01F2` — Final Bounded Pure Compile Correction  
**Date:** 2026-07-26  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Downstream:** `L0-TH-01` remains `BLOCKED`

## 1. Decision

The claim of a completed final handoff is not supported by repository evidence.

The required artifact:

```text
docs/handoffs/L0_TM_01F2_HANDOFF.md
```

does not exist. The older `L0_TM_01F_HANDOFF.md` remains the whitespace
escalation reviewed previously; it is not an F2 handoff and contains neither
the final matrix nor standard-build evidence.

A partial F2 code/test delta is present. It correctly closes raw-whitespace
preservation and the artificial `.beta` diagnostic path. It does not close the
mandatory vector and regression-evidence requirements. The gate remains
`NEEDS_FIX`.

## 2. Correct partial results

### Whitespace

`ScopedExpression.Literal` now preserves the authored non-null, non-blank
payload. `ScopedLiteralResolver` can therefore reject leading and trailing
whitespace as `LITERAL_INVALID`.

The change stays inside the exact narrow ownership granted by
`docs/L0_TM_01F_GATE_REVIEW.md`.

### Authored lambda-body path

`ScopedProgramLowerer` now captures the authored lambda-body path in the
closure and reuses it during application. It no longer emits the synthetic
`.beta` location.

`appliedLambdaKeepsTheAuthoredBodyPathForLoweringDiagnostics` asserts
`$.function.body`.

No persistence, codec, registry mutation, graph, networking, client, item, or
public API change is attributed to this partial delta.

## 3. Blocking findings

### L0-1F2-R1 — Required handoff is absent

There is no F2 handoff containing:

- completed changes;
- exact file list;
- vector-to-test matrix;
- focused command and count;
- standard build;
- migration impact;
- limitations and next owner.

Repository code without the required handoff cannot advance the board.

### L0-1F2-R2 — OBS-SHARE matrix is incomplete

The current compiler test directly proves approximately OBS-SHARE-2 and
OBS-SHARE-4. It does not separately prove OBS-SHARE-1, 3, 5, and 6 with the
contract oracles.

All six vectors require labeled assertions for rune identity, purity-relevant
admission, socket connectivity, and explicit sharing/no-CSE as applicable.

### L0-1F2-R3 — TAIL matrix is incomplete

There is no complete TAIL-1 through TAIL-8 matrix. Existing structural tests
cover only a subset of terminal, application-argument, let-value, and lambda
cases.

TAIL-8 must use the already frozen classification: pure compilation must not
claim inscription/executable-policy acceptance for a concrete non-Unit root;
the final server rejection remains deferred to `L0-TM-02`.

### L0-1F2-R4 — BOUND matrix and compile-limit route are incomplete

There is no complete BOUND-1 through BOUND-8 matrix.

The existing `budgetFailsAtTheFirstChargePastItsLimit` still calls
`ScopedCompileBudget` directly. It does not establish that one shared compile
pipeline:

- permits exactly 4,096 logical charges;
- rejects the attempted 4,097th charge with only `COMPILE_STEP_LIMIT`;
- returns no graph on that rejection.

No ownership escalation is justified for this evidence. The prior review
explicitly authorized a purpose-built package-private route through the same
pipeline, and `ScopedProgramCompiler.java` is inside correction ownership.

BOUND-1, 2, and 3 also need exact boundary assertions. BOUND-8 must prove that
this slice exposes no uncontracted combinator path; it must not implement a
future combinator.

### L0-1F2-R5 — Regression tests are not sensitive enough

`publicCheckerAndLowererCreateFreshMetersForEveryAttempt` uses a trivial
literal twice. The former reused-meter implementation would also pass that
test because two tiny attempts remain far below 4,096.

Use successive near-limit attempts where each attempt is valid independently
but the second would fail if the first meter were reused.

There is also no assertion that distinguishes application charging with and
without the additional administrative binding event.

### L0-1F2-R6 — Diagnostic normalization evidence is partial

The current normalization regression proves numeric path order and duplicate
collapse for one code. It does not separately establish:

1. pipeline phase order;
2. numeric canonical path order;
3. diagnostic code order;
4. first-instance duplicate retention.

All four ordering rules require explicit assertions.

### L0-1F2-R7 — Focused and standard build evidence is absent

Because the F2 handoff does not exist, it records neither the focused suite nor
the standard build. Concurrent local Gradle attempts are not a substitute for
the task-owned evidence required in the repository.

## 4. Authorized closure — L0-TM-01F3

**Owner:** Terra Medium  
**Status:** `READY`  
**Output:** `docs/handoffs/L0_TM_01F3_HANDOFF.md`

This is an evidence/test closure. The accepted whitespace and authored-path
production corrections must be preserved.

Write ownership:

```text
src/main/java/com/mathmod/language/ScopedProgramCompiler.java
src/test/java/com/mathmod/language/ScopedStructureValidatorTest.java
src/test/java/com/mathmod/language/ScopedTypeCheckerTest.java
src/test/java/com/mathmod/language/ScopedProgramLowererTest.java
src/test/java/com/mathmod/language/ScopedProgramCompilerTest.java
docs/handoffs/L0_TM_01F3_HANDOFF.md
```

`ScopedProgramCompiler.java` may change only to expose a package-private,
production-inert test seam needed to run the same complete pipeline at the
4,096/4,097 boundary. Public signatures and default production behavior must
not change.

Required output:

1. close L0-1F2-R1 through L0-1F2-R7;
2. provide a complete 22-row OBS-SHARE/TAIL/BOUND matrix in the handoff;
3. map every row to an exact method and labeled assertion;
4. run and record the focused language suite and standard build;
5. list the exact delta and confirm all forbidden boundaries remain untouched.

All other production files are read-only. Stop and escalate if a vector exposes
a production semantic defect outside the one authorized compiler test seam.

## 5. Gate transition

```text
L0-TM-01F2
    -> NEEDS_FIX

L0-TM-01F3
    -> READY

L0-TH-01
    -> remains BLOCKED
    -> requires Sol ACCEPT of L0-TM-01F3
```
