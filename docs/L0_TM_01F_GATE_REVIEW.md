# L0-TM-01F Gate Review

**Task:** `L0-TM-01F` — Bounded Pure Compile Correction  
**Date:** 2026-07-26  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Escalation decision:** whitespace ownership request is valid  
**Downstream:** `L0-TH-01` remains `BLOCKED`

## 1. Decision

The handoff cannot be accepted.

Terra Medium's specific escalation is correct: the owned resolver cannot reject
leading or trailing whitespace because `ScopedExpression.Literal` trims the
payload in its constructor before the resolver observes it. The task correctly
stopped instead of editing a read-only file.

This is not a reason to waive the frozen NUMBER grammar. The raw literal value
must remain available until the trusted resolver validates it. Sol therefore
grants one narrow correction ownership for `ScopedExpression.java` below.

Resolving whitespace alone is insufficient. The mandatory OBS-SHARE, TAIL, and
BOUND evidence is still incomplete, the compiler still invents a `.beta`
diagnostic path, and the handoff does not record the required standard build.
`L0-TH-01` must not start.

## 2. Evidence

`ScopedExpression.Literal` currently performs:

```java
encodedValue = encodedValue.trim();
```

`ScopedLiteralResolver` later receives only `literal.encodedValue()`. Therefore
`" 1"` and `"1 "` are indistinguishable from `"1"` inside every file previously
owned by `L0-TM-01F`.

The focused command was independently reproduced:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat test --tests 'com.mathmod.language.*' --rerun-tasks --no-daemon
```

Result:

```text
BUILD SUCCESSFUL
7 suites
28 tests
0 failures
0 errors
0 skipped
```

This proves build integrity for the focused source set, not completion of the
semantic gate.

## 3. Closed findings

Code inspection confirms:

- L0-1-R1: public checker/lowerer calls now create fresh meters;
- L0-1-R2: application and administrative binding are charged separately;
- L0-1-R3: NUMBER requires the frozen id, enabled state, empty input signature,
  NUMBER output, PURE purity, and `constant_number` executor;
- L0-1-R6: a literal failure no longer gains
  `FUNCTION_RESULT_FORBIDDEN`;
- the `L0-TM-01F` delta stays inside its assigned files and does not attribute
  persistence, registry mutation, graph, client, networking, or public API
  changes to this slice.

These findings remain subject to regression evidence in the final focused
suite.

## 4. Remaining blocking findings

### L0-1F-R1 — Raw NUMBER whitespace is destroyed before validation

Preserve the exact non-null, non-blank literal payload in
`ScopedExpression.Literal`; do not trim it. Existing blank-only rejection may
remain. The trusted NUMBER resolver must then reject leading/trailing
whitespace as `LITERAL_INVALID` and return no graph.

This changes no persistence or wire format. `L0-SOL-02` will later decide how a
wire decoder reports the same invalid payload.

### L0-1F-R2 — Required vector matrix remains incomplete

The handoff does not map `OBS-SHARE-1..6`, `TAIL-1..8`, or `BOUND-1..8` to exact
test methods and assertions. Only four tests were added to the prior 24-test
suite.

In particular:

- OBS-SHARE-1, 3, and 5 are not directly proven;
- several effect-tail combinations remain absent;
- BOUND-1, 2, and 3 have no exact boundary regression;
- BOUND-5/6 still exercise only `ScopedCompileBudget`, not the complete shared
  compile pipeline at 4,096/4,097;
- TAIL-8 and BOUND-8 are not classified under the explicit deferred-ownership
  rules in `docs/L0_TM_01_GATE_REVIEW.md`.

The next handoff must contain a table with one row per vector, exact test method,
oracle, and result. Grouping vectors in one method is allowed only when every
vector has a separate assertion and readable failure label.

The per-attempt freshness test must be regression-sensitive: two successive
calls on one public checker/lowerer instance must each consume enough work that
a reused meter would fail the second call.

The beta accounting test must assert the logical charge difference, not only
inspect the implementation.

Diagnostic normalization must separately prove phase order, numeric structural
path order, code order, and duplicate collapse.

### L0-1F-R3 — Beta diagnostics lose their source path

`ScopedProgramLowerer` lowers an applied closure body using:

```text
path + ".beta"
```

`.beta` is an administrative evaluation path, not the canonical structural
location of the authored lambda body. The closure must retain the original
lambda-body structural path, and diagnostics produced while evaluating it must
use that source path. Administrative reduction steps must not invent player
diagnostic locations.

Add a regression with an invalid literal or other stable lowering failure
inside an applied lambda body and assert the original indexed source path.

### L0-1F-R4 — Standard build evidence is absent from the handoff

The handoff records only the focused suite. The final correction handoff must
also record:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build --no-daemon
```

with its result.

## 5. Authorized correction — L0-TM-01F2

**Owner:** Terra Medium  
**Status:** `READY`  
**Output:** `docs/handoffs/L0_TM_01F2_HANDOFF.md`

Write ownership is the complete `L0-TM-01F` list in
`docs/L0_TM_01_GATE_REVIEW.md`, plus exactly:

```text
src/main/java/com/mathmod/language/ScopedExpression.java
docs/handoffs/L0_TM_01F2_HANDOFF.md
```

The `ScopedExpression.java` grant is restricted to preserving the exact
`Literal.encodedValue` payload until resolver validation. Do not change any
other expression variant, record shape, public signature, De Bruijn semantics,
bounds, or name normalization.

Required output:

1. close L0-1F-R1 through L0-1F-R4;
2. preserve every correction already present in `L0-TM-01F`;
3. provide the complete vector-to-test matrix;
4. run the focused language suite and standard build;
5. list exact files and confirm no persistence, codec, registry mutation,
   networking, client, item, graph, or public API boundary changed.

All other files remain read-only. The original forbidden boundaries and stop
conditions remain in force.

## 6. Gate transition

```text
L0-TM-01F
    -> NEEDS_FIX

L0-TM-01F2
    -> READY

L0-TH-01
    -> remains BLOCKED
    -> requires Sol ACCEPT of L0-TM-01F2
```
