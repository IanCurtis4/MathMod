# Sol Acceptance — A0 Metadata Foundation Gate

**Date:** 2026-07-26

**Decision:** `ACCEPT`

**Scope accepted:** A0-1/A0-2 metadata foundation, including corrections
`A0-TM-01F` and `A0-TM-01P`.

## Evidence

- Terra High issued `APPROVE` for A0-3 in
  `A0_METADATA_SEMANTIC_REREVIEW_2.md`.
- `MAX_RUNE_PRESENTATIONS` is explicitly `2_048`.
- The immutable `Snapshot` invariant rejects a larger presentation map with
  structured `DiagnosticCode.LIMIT_EXCEEDED`.
- Boundary tests accept 2,048 presentations and reject 2,049 without returning
  a snapshot.
- Sol reran:

  ```text
  GRADLE_USER_HOME=C:\codex-gradle-a0
  .\gradlew.bat cleanTest test --tests com.mathmod.authoring.BuiltInAuthoringMetadataTest --no-daemon
  BUILD SUCCESSFUL
  ```

- Focused result: 6 tests, 0 failures, 0 errors, 0 skipped.
- Sol reran:

  ```text
  GRADLE_USER_HOME=C:\codex-gradle-a0
  .\gradlew.bat build --no-daemon
  BUILD SUCCESSFUL
  ```

- `GuidedWorkspaceState.CURRENT_VERSION` remains `1`.
- No `ProgramGraph`, `GuidedWorkspaceState`, Data Component, networking,
  `ProgramSurfaceMode`, client-screen, public-API, adapter, or replay change is
  part of this accepted block.

## Gate decision

- `A0-TM-01P`: `DONE`
- `A0-TH-01R2`: `DONE` with `APPROVE`
- `A0-TM-02`: `READY`, but not started by this acceptance

This approval authorizes only the bounded legacy-expansion-adapter task already
defined in the Delivery Board. It does not pre-approve its implementation or
its exact `ProgramGraph` replay evidence.

## Non-blocking documentation note

The 9C handoff names `A0_METADATA_PRESENTATION_BOUND_REREVIEW.md` in its
next-owner file list, while the Delivery Board required and Terra High produced
`A0_METADATA_SEMANTIC_REREVIEW_2.md`. The required artifact exists and its
scope is correct, so the naming discrepancy does not block this gate.

