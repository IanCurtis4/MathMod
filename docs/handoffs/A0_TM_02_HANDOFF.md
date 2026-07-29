# Handoff: A0-TM-02

## Completed
- Added the package-private trusted built-in legacy expansion adapter.
- Added an immutable explicit form-id to adapter-id registry derived from the frozen built-in metadata snapshot.
- Canonicalized supplied numeric arguments through `AuthoringMetadata.Parameter` descriptors before constructing the legacy invocation.
- Added exact `ProgramGraph` replay verification that fails closed on an unknown form or graph mismatch.

## Decisions implemented
- Persisted identity is always the canonical form id. Adapter ids remain internal trusted implementation checks and are never encoded into `CustomSpellInvocation`.
- The adapter has no dependency on players, levels, items, networking, clocks, random sources, files, commands, callbacks, or executors.
- Unknown supplied parameter keys are ignored; missing keys use descriptor defaults; non-finite values return descriptor defaults; finite values clamp inclusively.
- Exact replay constructs a fresh workspace and compares exact `ProgramGraph` equality before returning the proposed graph. A mismatch raises the closed `GRAPH_REPLAY_MISMATCH` path.

## Files changed
- `src/main/java/com/mathmod/authoring/TrustedLegacyExpansionAdapter.java`
- `src/test/java/com/mathmod/authoring/TrustedLegacyExpansionAdapterTest.java`
- `docs/handoffs/A0_TM_02_HANDOFF.md`

## Contracts referenced
- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`, especially sections 6.3, 6.6, 10, and 11.4.
- `docs/handoffs/A0_TM_01_HANDOFF.md`
- `docs/A0_METADATA_REVIEW_SOL_RESOLUTION.md`
- `docs/handoffs/A0_TM_01F_HANDOFF.md`
- `docs/A0_METADATA_REREVIEW_SOL_RESOLUTION.md`
- `docs/handoffs/A0_TM_01P_HANDOFF.md`
- `docs/A0_METADATA_SEMANTIC_REREVIEW_2.md`
- `docs/DELIVERY_BOARD.md`, `A0-TM-02`.

## Tests and evidence
- command: `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests com.mathmod.authoring.TrustedLegacyExpansionAdapterTest --no-daemon`
  result: `BUILD SUCCESSFUL`.
- command: `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.authoring.*' --no-daemon`
  result: `BUILD SUCCESSFUL`.
- command: `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon`
  result: `BUILD SUCCESSFUL`.
- Coverage: every built-in form at defaults, numeric forms with raw non-finite/out-of-range/unknown arguments, representative growing sequences, unknown form rejection, and replay-mismatch rejection.

## Migration impact
- None. `GuidedWorkspaceState` remains schema 1 and no persistence encoding changes. No Data Component, payload, public API, `ProgramGraph`, or stable form id changed.

## Known limitations
- The adapter is an internal characterization boundary only; it is not yet wired into Guided persistence, networking, or UI flows.
- The default `%USERPROFILE%\.gradle` cache may fail to bootstrap test workers on this profile; verification used the accepted ASCII cache `C:\codex-gradle-a0`.

## Unresolved questions
- None for this bounded adapter slice.

## Next owner
- Sol

## Exact next task
- Review A0-TM-02 for exact graph-equality evidence, canonicalization, closed failure paths, and forbidden-boundary isolation before deciding the next A0 gate.

## Files the next owner may edit
- `docs/DELIVERY_BOARD.md`
- Sol-owned gate-review documents.

## Files the next owner must not edit
- `src/main/java/com/mathmod/runes/ProgramGraph.java`
- `src/main/java/com/mathmod/program/GuidedWorkspaceState.java`
- `src/main/java/com/mathmod/program/ProgramSurfaceMode.java`
- `src/main/java/com/mathmod/registry/ModDataComponents.java`
- `src/main/java/com/mathmod/network/**`
- `src/main/java/com/mathmod/client/screen/**`
