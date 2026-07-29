# L0-TM-01 Gate Review

**Task:** `L0-TM-01` — Pure Compile Hardening  
**Date:** 2026-07-26  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Downstream:** `L0-TH-01` remains `BLOCKED`

## 1. Decision

The handoff is not accepted.

The focused language suite and standard build pass, and the implementation
correctly remains outside persistence, networking, client, item, and public API
boundaries. However, the implementation does not yet satisfy the frozen
compile-meter, trusted-literal, diagnostic, vector-evidence, and exact ownership
requirements in section 13 of
`docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`.

Terra High must not start `L0-TH-01` until the correction below has a repository
handoff and Sol accepts it.

## 2. Reproduced evidence

Focused command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat test --tests 'com.mathmod.language.*' --rerun-tasks --no-daemon
```

Result:

```text
BUILD SUCCESSFUL
7 suites
24 tests
0 failures
0 errors
0 skipped
```

Standard command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build --no-daemon
```

Result:

```text
BUILD SUCCESSFUL
```

Passing commands establish build integrity. They do not establish completion
of the mandatory semantic vectors.

## 3. Blocking findings

### L0-1-R1 — Compile meter is reused across public attempts

`ScopedTypeChecker` and `ScopedProgramLowerer` store a budget created by their
public constructor and reuse it on every later `check`/`lower` call. A second
call on the same instance therefore inherits charges from the first.

This violates section 7.2: one meter belongs to one attempt and is never reused
across attempts. `ScopedProgramCompiler.compile` already creates a fresh meter
per call; the source-compatible public checker/lowerer entry points must have
the same per-attempt property.

### L0-1-R2 — Beta reduction is undercharged

Application lowering charges `APPLICATION`, then creates the administrative
binding with `bind(...)` without charging `CLOSURE_OR_BINDING`.

Section 7.3 charges function application/beta activation and
closure/administrative binding as separate logical events. Every accepted
application must account for both.

### L0-1-R3 — NUMBER descriptor validation is incomplete

`ScopedLiteralResolver` verifies the fixed id, enabled state, empty inputs, and
NUMBER output. It does not reject a `mathmod:constant_number` definition with
non-`PURE` purity or a non-`constant_number` executor identity.

The trusted boundary must verify the complete built-in descriptor relevant to
this slice:

```text
id = mathmod:constant_number
enabled = true
inputs = []
output = NUMBER
purity = PURE
executorKey = constant_number
```

This does not move the later whole-program executable-policy gate into
`L0-TM-01`; it prevents the trusted literal resolver from accepting a different
semantic rune under the reserved id.

### L0-1-R4 — Mandatory vector evidence is incomplete

The contract requires evidence for `OBS-SHARE-1` through `OBS-SHARE-6`,
`TAIL-1` through `TAIL-8`, and `BOUND-1` through `BOUND-8`.

Current coverage establishes only a subset:

- OBS-SHARE: approximately 2, 4, and 6;
- TAIL: approximately 1, 3, 4, and 6;
- BOUND: 7; BOUND-6 only exercises the meter object directly;
- no deterministic diagnostic ordering/deduplication regression;
- NUMBER coverage freezes `-0.0`, hexadecimal, `NaN`, and unsupported BOOL,
  but not the complete accepted/rejected grammar and canonical representation.

Every vector must be mapped in the corrected handoff to an exact test method
and assertion. `TAIL-8` must prove the pure compiler does not claim inscription
or executable-policy admission; final non-Unit rejection remains owned by
`L0-TM-02`. `BOUND-8` must prove that no uncontracted combinator path exists in
this slice; implementation of a future combinator remains forbidden.

For BOUND-5/6, evidence must exercise an authoritative compile attempt or a
purpose-built package-private compile route using the same complete pipeline,
not only isolated calls to `ScopedCompileBudget`. It must establish that exactly
4,096 logical charges may succeed and the attempted 4,097th charge returns only
`COMPILE_STEP_LIMIT` with no graph.

### L0-1-R5 — Diagnostic paths are not one canonical structural scheme

Structural validation uses numeric argument positions such as
`$.arguments[n]`, while type checking and lowering build paths from socket
names. Section 7.4 requires one canonical structural path with numeric
list-index ordering.

All phases must identify the same source location with the same structural
path. Socket names may appear in messages but must not replace structural
identity.

### L0-1-R6 — Literal failure gains a spurious result diagnostic

When literal resolution records `LITERAL_INVALID` or `LITERAL_UNSUPPORTED` and
returns no value, `lowerChecked` also adds `FUNCTION_RESULT_FORBIDDEN`.

The second issue is not the cause of the failure. A failed subexpression must
return its existing stable diagnostic set without inventing a root result
failure.

### L0-1-R7 — Two files were edited outside exact ownership

The handoff lists:

```text
src/main/java/com/mathmod/language/ScopedTypeCheckResult.java
src/main/java/com/mathmod/language/ScopedValidationResult.java
```

Neither file was included in the original exact existing-file ownership.
Because deterministic normalization belongs at result boundaries, Sol grants
write ownership for these two files only to the correction task below. This is
a prospective ownership resolution, not retroactive acceptance of the initial
scope deviation.

## 4. Conforming evidence

- `ScopedCompileResult` and `ScopedLoweringResult` erase a graph when issues
  exist.
- `ScopedProgramCompiler` invokes `ProgramValidator` before returning a
  successful graph.
- explicit `let` sharing and absence of syntactic CSE are present in the
  lowering algorithm.
- the NUMBER grammar is locale-independent, finite-only, and canonicalizes
  negative zero.
- no production `ProgramGraph`, persistence, Data Component, networking,
  client, resource, item, or public API change is attributed to this slice.

## 5. Authorized correction — L0-TM-01F

**Owner:** Terra Medium  
**Status:** `READY`  
**Output:** `docs/handoffs/L0_TM_01F_HANDOFF.md`

Required corrections:

1. make every public compile/check/lower call own a fresh attempt meter while
   preserving one shared meter across stages inside that attempt;
2. charge beta activation and its administrative binding separately;
3. validate the complete trusted NUMBER descriptor frozen in L0-1-R3;
4. use one indexed canonical structural-path scheme in every phase;
5. avoid secondary `FUNCTION_RESULT_FORBIDDEN` issues after a prior lowering
   failure;
6. add deterministic phase/path/code ordering and deduplication tests;
7. add and map the complete OBS-SHARE, TAIL, and BOUND matrices under the
   TAIL-8/BOUND-8 ownership clarifications above;
8. freeze representative accepted NUMBER spellings and canonical graph values,
   plus rejection of infinity, commas, whitespace, units/suffixes, trailing
   input, overlength input, wrong purity, and wrong executor identity;
9. run the focused language suite and standard build;
10. update the handoff with exact files, commands, counts, limitations, and
    confirmation that no persistent or public boundary changed.

Exact write ownership:

```text
src/main/java/com/mathmod/language/ScopedLanguageIssue.java
src/main/java/com/mathmod/language/ScopedStructureValidator.java
src/main/java/com/mathmod/language/ScopedTypeChecker.java
src/main/java/com/mathmod/language/ScopedProgramLowerer.java
src/main/java/com/mathmod/language/ScopedLoweringResult.java
src/main/java/com/mathmod/language/ScopedTypeCheckResult.java
src/main/java/com/mathmod/language/ScopedValidationResult.java
src/main/java/com/mathmod/language/ScopedCompileBudget.java
src/main/java/com/mathmod/language/ScopedLiteralResolver.java
src/main/java/com/mathmod/language/ScopedRuneSnapshot.java
src/main/java/com/mathmod/language/ScopedProgramCompiler.java
src/main/java/com/mathmod/language/ScopedCompileResult.java
src/test/java/com/mathmod/language/ScopedStructureValidatorTest.java
src/test/java/com/mathmod/language/ScopedTypeCheckerTest.java
src/test/java/com/mathmod/language/ScopedProgramLowererTest.java
src/test/java/com/mathmod/language/ScopedProgramCompilerTest.java
docs/handoffs/L0_TM_01F_HANDOFF.md
```

Everything else remains read-only. The original forbidden list remains in
force. In particular, do not change `ProgramGraph`, `ProgramStorage`,
`ModDataComponents`, runes/registry production, persistence/codecs, networking,
items, menus, screens, previews, assets, localization, Patchouli, executable
policy, or public KubeJS/datapack APIs.

Stop and escalate if a correction requires a public signature break, registry
mutation/generation, whole-program executable-policy ownership, persistence,
client code, graph semantics, or a literal beyond NUMBER.

## 6. Gate transition

```text
L0-TM-01 IN_REVIEW
    -> NEEDS_FIX
    -> L0-TM-01F READY

L0-TH-01
    -> remains BLOCKED
    -> requires Sol ACCEPT of L0-TM-01F
```
