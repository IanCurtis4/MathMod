# L0-TM-01 Final Gate Acceptance

**Gate:** `L0-TM-01` / F / F2 / F3 — Pure Compile Hardening  
**Date:** 2026-07-27  
**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Next gate:** `L0-TH-01` is `READY`

## 1. Decision

The pure compile hardening gate is accepted.

Terra Medium corrected the final four test-oracle findings without creating a
new handoff revision. The existing
`docs/handoffs/L0_TM_01F3_HANDOFF.md`, the final two-test delta, this acceptance
record, and the Delivery Board together are the complete repository evidence.
This document supersedes the handoff's stale `pending rerun` build line.

No F4 task is required.

## 2. Final corrections verified

### BOUND-1

The structural boundary now proves:

```text
256 AST nodes -> accepted
257 AST nodes -> AST_LIMIT
```

The rejected construction is exactly:

```text
1 let + 1 value literal + addTree(128) with 255 nodes = 257
```

### TAIL-2

The accepted terminal-effect graph proves:

- exactly one observation node;
- exactly one effect node;
- the observation feeds the effect's `value` socket;
- the effect is the graph output.

Compile validity also proves the socket is not multiply connected.

### OBS-SHARE-6

The source now applies a lambda whose body observes. The authoritative compile
route reports `IMPURE_LAMBDA_BODY` and returns no graph.

### Diagnostic phase ordering

The normalization regression now asserts the complete sequence:

```text
AST_LIMIT
DISABLED_RUNE
TYPE_MISMATCH
LITERAL_INVALID
LOWERED_GRAPH_INVALID
```

This proves phases 1 through 4, code ordering inside phase 2, and first-instance
duplicate retention. Numeric `arguments[2]` before `arguments[10]` remains
separately proven.

## 3. Previously accepted F3 evidence

- public compile creates a fresh meter;
- the package-private seam executes the same pipeline and changes no public
  API;
- identity application charges 17 steps, including distinct beta activation
  and administrative binding;
- exactly 4,096 charges may succeed;
- the attempted 4,097th charge returns only `COMPILE_STEP_LIMIT`, no graph, and
  leaves the meter at 4,096;
- repeated public checker/lowerer attempts detect meter reuse;
- raw NUMBER whitespace is preserved until trusted rejection;
- NUMBER descriptor identity, purity, signature, and executor are verified;
- literal canonicalization and fail-closed behavior are proven;
- authored lambda-body diagnostic paths survive beta reduction;
- OBS-SHARE-1 through 6, TAIL-1 through 8, and BOUND-1 through 8 have accepted
  test coverage under their frozen L0 classifications;
- TAIL-8 remains a pure candidate only; executable-policy and inscription
  admission remain deferred to `L0-TM-02`.

## 4. Reproduced execution evidence

Focused command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat test --tests 'com.mathmod.language.*' --rerun-tasks --no-daemon --console=plain
```

Result:

```text
BUILD SUCCESSFUL
7 suites
34 tests
0 failures
0 errors
0 skipped
```

Standard command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build --no-daemon --console=plain
```

Result:

```text
BUILD SUCCESSFUL
```

The existing deprecation warnings are unrelated to the L0 delta and do not
affect the gate.

## 5. Ownership and forbidden-boundary evidence

After `docs/L0_TM_01F3_GATE_REVIEW.md`, Terra Medium changed only:

```text
src/test/java/com/mathmod/language/ScopedStructureValidatorTest.java
src/test/java/com/mathmod/language/ScopedProgramCompilerTest.java
```

Both files were explicitly authorized.

`ScopedProgramCompiler.java` remained read-only after its seam was accepted.
The public compiler signature and default behavior remain unchanged.

No persistence, codec, Data Component, registry mutation, networking, item,
menu, client/UI, `ProgramGraph`, execution-policy, resource, localization,
Patchouli, or public KubeJS/datapack API boundary changed.

## 6. Gate transition

```text
L0-TM-01
L0-TM-01F
L0-TM-01F2
L0-TM-01F3
    -> DONE (ACCEPT)

L0-TH-01
    -> READY
    -> documentation-only semantic review
    -> output docs/L0_PURE_COMPILE_SEMANTIC_REVIEW.md
```

Terra High may now start `L0-TH-01`. Later wire, server, persistence, UI,
content, and theorem slices remain blocked.
