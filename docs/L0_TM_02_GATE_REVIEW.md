# L0-TM-02 Gate Review

**Task:** `L0-TM-02 — Server Compile Service and Registry Generation`  
**Date:** 2026-07-28  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Correction:** `L0-TM-02F`  
**Downstream:** `L0-TM-03` remains `BLOCKED`

## 1. Evidence reviewed

- `docs/L0_SERVER_COMPILE_SERVICE_READINESS.md`;
- `docs/handoffs/L0_TM_02_HANDOFF.md`;
- every production and test file listed by the handoff;
- repository working-tree scope;
- focused JUnit execution without build-cache reuse;
- standard build.

The implementation stays inside the production/test ownership assigned by
`L0-SOL-03`. No `ProgramGraph`, `ProgramStorage`, Data Component,
`GuidedWorkspaceState`, networking, item, player, world, menu, client or
persistent-state mutation was added.

The registry generation, immutable capture, snapshot-explicit resource
calculation, bounded synchronous service, cancellation checks and final
stale-generation rejection are directionally compatible with the readiness.

## 2. Blocking findings

### L0-TM02-R1 — Successful-result invariant is not enforced

**Severity:** blocking

The accepted readiness says that a successful result exists only with the
candidate, recommendations, rune generation, rune/material/knowledge-definition
snapshots, player-knowledge evidence, charged steps and no issues.

`ScopedServerCompileResult` currently:

- converts null rune/material collections to empty values;
- accepts null `knowledgeDefinitions`;
- accepts null `playerKnowledge`;
- defines success only as candidate present plus empty issue lists.

Therefore direct construction can produce `successful() == true` without the
mandatory admission evidence. It can also claim a candidate whose rune ids are
not represented by its captured rune-definition map. This violates the
server-owned success boundary even though the current service path normally
passes the missing values.

Required correction:

- make success and failure construction fail closed;
- a successful result must reject null mandatory evidence;
- every candidate node's rune id must exist in the captured rune definitions;
- no issue may coexist with an exposed candidate or recommendations;
- failure remains allowed to omit unavailable pre-capture evidence;
- add focused constructor/factory invariant tests, including an attempted
  fabricated success.

### L0-TM02-R2 — Required rejection and diagnostic oracles are absent

**Severity:** blocking

The readiness explicitly requires focused evidence that executable, resource
and knowledge rejection each fail closed, that an absent knowledge requirement
remains allowed, and that service diagnostics are stable and deterministic.

`ScopedServerCompileServiceTest` currently has only three test methods:

1. successful capture;
2. pure rejection plus cancellation;
3. stale rune generation.

There is no focused oracle for:

- `EXECUTABLE_REJECTED`;
- `RESOURCE_REJECTED`;
- `KNOWLEDGE_REJECTED`;
- explicit no-requirement knowledge admission;
- deterministic phase/path/code ordering and deduplication;
- mandatory success-result evidence.

Passing production code is not evidence for unexercised fail-closed branches.
All listed vectors must be added.

### L0-TM02-R3 — Diagnostic identity does not model pipeline phase

**Severity:** blocking

The accepted identity and normalization rule is:

```text
phase -> canonical path -> code
deduplicate by (phase, path, code)
```

`ScopedServerCompileIssue` stores only `(code, path)` and normalizes by code
ordinal before path. `CANCELLED` may arise at multiple pipeline boundaries, so
code ordinal is not a complete phase identity.

Required correction:

- represent stable service phase explicitly, or provide an equally explicit
  immutable phase mapping that distinguishes cancellation boundaries;
- normalize by phase, canonical path and code;
- deduplicate by the same triple;
- test input-order independence, numeric/canonical path behavior where
  applicable, and duplicate collapse.

### L0-TM02-R4 — Handoff test count and coverage claim are inaccurate

**Severity:** blocking evidence defect

Independent result XML reports:

| Suite | Tests | Failures |
|---|---:|---:|
| `RuneRegistrySnapshotTest` | 3 | 0 |
| `ScopedServerCompileServiceTest` | 3 | 0 |
| `ProgramCostsTest` | 17 | 0 |
| `ProgramExecutionPolicyTest` | 5 | 0 |
| **Total** | **28** | **0** |

The handoff claims 29 vectors and 7 new service/registry vectors. The repository
contains 28 total and 6 new vectors. The corrected handoff must report the
actual executed count and enumerate the new vectors after R1–R3 are closed.

## 3. Non-blocking hardening required within the correction

The correction must also make the registry oracle complete:

- assert a real `registerOrReplace` change advances exactly once;
- assert a real `update` change advances exactly once;
- assert updater failure/wrong-id preserves map and generation;
- assert equal complete publication is a no-op;
- assert complete publication changes the whole map and increments exactly
  once;
- retain A -> B -> A and overflow-before-mutation evidence.

`publishComplete` is an internal future-loader primitive and does not need to be
public for this slice. Reduce it to the narrowest visibility compatible with
its current owner/tests. Do not add or wire a loader.

## 4. Independent verification

Focused command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.runes.RuneRegistrySnapshotTest `
  --tests com.mathmod.program.ScopedServerCompileServiceTest `
  --tests com.mathmod.program.ProgramCostsTest `
  --tests com.mathmod.program.ProgramExecutionPolicyTest
```

Result:

```text
BUILD SUCCESSFUL
28 tests / 0 failures / 0 errors / 0 skipped
```

Standard command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result:

```text
BUILD SUCCESSFUL
31 actionable tasks; 1 from cache, 30 up-to-date
```

The green build proves compilation/regression status; it does not close the
missing contractual oracles or the successful-result invariant.

## 5. Correction assignment

```text
L0-TM-02F — Server Compile Result and Admission Evidence Correction
Owner: Terra Medium
Status: READY
```

Required reading:

1. `docs/L0_SERVER_COMPILE_SERVICE_READINESS.md`;
2. this gate review;
3. `docs/handoffs/L0_TM_02_HANDOFF.md`.

Writable files:

```text
src/main/java/com/mathmod/runes/RuneRegistry.java
src/main/java/com/mathmod/program/ScopedServerCompileIssue.java
src/main/java/com/mathmod/program/ScopedServerCompileResult.java
src/main/java/com/mathmod/program/ScopedServerCompileService.java
src/test/java/com/mathmod/runes/RuneRegistrySnapshotTest.java
src/test/java/com/mathmod/program/ScopedServerCompileServiceTest.java
docs/handoffs/L0_TM_02_HANDOFF.md
```

All other L0-TM-02 production files are read-only for the correction. The
handoff is updated in place; it must include the exact changed-file delta,
actual focused count, standard build result, closed findings R1–R4, limitations
and escalations.

No persistence, item/player/world mutation, loader, networking, client/UI,
public KubeJS/datapack integration, `ProgramGraph`, `ProgramStorage`, Data
Component or Guided-state change is authorized.

## 6. Gate state

```text
L0-TM-02 NEEDS_FIX
    -> L0-TM-02F READY

L0-TM-03 BLOCKED
```

Sol will accept L0-TM-02 only after the corrected handoff and repository
evidence close R1–R4. No atomic persistence work may start before that
acceptance and a separate exact `L0-TM-03` readiness assignment.
