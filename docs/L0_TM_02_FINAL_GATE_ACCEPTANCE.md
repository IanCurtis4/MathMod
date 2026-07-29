# L0-TM-02 Final Gate Acceptance

**Task:** `L0-TM-02` / `L0-TM-02F`  
**Date:** 2026-07-28  
**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Next gate:** `L0-SOL-04 — Atomic Persistence Readiness`

## 1. Decision

The updated `docs/handoffs/L0_TM_02_HANDOFF.md` and repository delta close all
blocking findings R1–R4 from `docs/L0_TM_02_GATE_REVIEW.md`.

`L0-TM-02` and its bounded correction `L0-TM-02F` are accepted as one completed
server compile service and registry-generation block.

## 2. Closed findings

### R1 — Successful-result invariant

Closed.

`ScopedServerCompileResult` now fails closed:

- successful candidates require non-null recommendations, rune definitions,
  material definitions, knowledge-definition evidence and player-knowledge
  evidence;
- generation must be non-negative;
- every candidate rune id must exist in the captured rune definitions;
- any language or service issue removes the candidate and recommendations;
- focused tests reject fabricated success without mandatory evidence.

### R2 — Admission rejection oracles

Closed.

Focused tests now prove fail-closed behavior for:

- `EXECUTABLE_REJECTED`;
- `RESOURCE_REJECTED`;
- `KNOWLEDGE_REJECTED`;
- absent knowledge requirement remaining allowed;
- candidate and recommendations absent on rejection.

### R3 — Diagnostic phase identity

Closed.

`ScopedServerCompileIssue` now carries an explicit phase. Normalization is by:

```text
phase -> canonical numeric path -> code
```

Duplicate `(phase, path, code)` triples collapse, and input-order independence
is covered.

### R4 — Handoff evidence

Closed.

The updated handoff explicitly supersedes its old 29-vector statement with the
correction result:

```text
30 tests / 0 failures / 0 errors / 0 skipped
```

The retained historical sentence is editorially stale but unambiguous because
the correction statement names it as superseded. It is not a gate blocker.

## 3. Registry and scope verification

The correction additionally proves:

- real replace and update mutations advance generation once;
- semantic no-ops do not advance generation;
- A -> B -> A receives a new generation;
- failed updater and wrong-id updater preserve map and generation;
- invalid complete publication is atomic;
- equal complete publication is a no-op;
- changed complete publication advances once;
- overflow fails before mutation;
- `publishComplete` is package-private.

The changed-file delta matches the authorized `L0-TM-02F` list. No
`ProgramGraph`, `ProgramStorage`, Data Component, `GuidedWorkspaceState`,
network, item, player, world, menu, client, persistent-state or loader change
was introduced.

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

| Suite | Tests | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| `RuneRegistrySnapshotTest` | 3 | 0 | 0 | 0 |
| `ScopedServerCompileServiceTest` | 5 | 0 | 0 | 0 |
| `ProgramCostsTest` | 17 | 0 | 0 | 0 |
| `ProgramExecutionPolicyTest` | 5 | 0 | 0 | 0 |
| **Total** | **30** | **0** | **0** | **0** |

Standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result:

```text
BUILD SUCCESSFUL
31 actionable tasks; 1 from cache, 30 up-to-date
```

## 5. Gate transition

```text
L0-TM-02 DONE (ACCEPT)
L0-TM-02F DONE (ACCEPT)
    -> L0-SOL-04 READY

L0-TM-03 BLOCKED
```

`L0-TM-03` does not become directly dispatchable from this acceptance. The
original L0 authority contract requires a separate Sol-owned readiness
assignment with exact non-overlapping ownership for `ProgramStorage`, the
approved component/codec, Guided/source precedence, item identity rechecks and
rollback. `L0-SOL-04` is now the only dispatchable L0 task.
