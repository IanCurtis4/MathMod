# Handoff: A0-TM-01

## Completed
- Completed the focused A0 metadata characterization coverage without starting A0-3.
- Corrected the numeric contract expectation: `NaN`, positive infinity, and negative infinity canonicalize to the declared default; for `mathmod:number_one`, that value is `1.0`.
- Added checks for finite inclusive clamping, all 67 frozen form ids, all 11 frozen category ids, immutable metadata collections, deterministic form ordering, duplicate and unknown-category rejection, replay-sensitive fingerprints, formula bounds, and the technical presentation fallback.
- Removed the prematurely started A0-3 adapter and its test from the shared worktree. They had no authorization or handoff for blocked `A0-TM-02`.

## Decisions implemented
- Non-finite numeric input never clamps to a bound; it uses the descriptor default, as required by section 6.3 of the A0 contract.
- Presentation-only changes do not change a form semantic fingerprint. Parameter default, bounds, order, and legacy adapter identity do change it.
- Form ordering resolves identical sort orders by stable form id.
- This task did not implement legacy expansion adapters, replay handling, persistence, UI integration, or external metadata sources.

## Files changed
- `src/main/java/com/mathmod/authoring/AuthoringMetadata.java`
- `src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java`
- `src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java`
- `docs/handoffs/A0_TM_01_HANDOFF.md`

## Scope reconciliation
- Removed from the shared worktree because they are A0-3 work outside this task and `A0-TM-02` remains blocked:
  - `src/main/java/com/mathmod/authoring/TrustedLegacyExpansionAdapter.java`
  - `src/test/java/com/mathmod/authoring/TrustedLegacyExpansionAdapterTest.java`

## Contracts referenced
- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md` sections 5.2, 5.3, 6.3, 6.5, 6.7, and 7.
- `docs/FOUNDATION_BETA_A0_ASSIGNMENT.md`
- `docs/DELIVERY_BOARD.md`, task `A0-TM-01`.

## Tests and evidence
- command: `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat cleanTest test --tests com.mathmod.authoring.BuiltInAuthoringMetadataTest --no-daemon --rerun-tasks`
  result: `BUILD SUCCESSFUL`; 6 tests, 0 failures, 0 errors, 0 skipped. This accepted verification is recorded in `docs/handoffs/A0_TM_01_SOL_REVIEW.md`.
- command: `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon`
  result: `BUILD SUCCESSFUL`. This accepted verification is recorded in `docs/handoffs/A0_TM_01_SOL_REVIEW.md`.
- diagnostic: the same commands using `%USERPROFILE%\.gradle` reproducibly fail before JUnit startup with `ClassNotFoundException: worker.org.gradle.process.internal.worker.GradleWorkerMain`; the isolated ASCII cache classifies that as local Gradle cache/bootstrap state, not a repository test failure.

## Migration impact
- None. No persistence schema, Data Component, graph, network payload, or stable form id changed.

## Known limitations
- The default `%USERPROFILE%\.gradle` cache remains unable to start Gradle test workers on this Windows profile. Use an ASCII `GRADLE_USER_HOME`, such as `C:\codex-gradle-a0`, for local verification until the cache/bootstrap issue is repaired.

## Unresolved questions
- None for A0-TM-01. The local cache workaround is documented above.

## Next owner
- Sol

## Exact next task
- Re-review the reconciled A0-TM-01 scope and, if accepted, transition it to `DONE` and make `A0-TH-01` `READY`.

## Files the next owner may edit
- `docs/DELIVERY_BOARD.md`
- Sol-owned coordination and gate-decision documents.

## Files the next owner must not edit
- `src/main/java/com/mathmod/runes/ProgramGraph.java`
- `src/main/java/com/mathmod/program/GuidedWorkspaceState.java`
- `src/main/java/com/mathmod/program/ProgramSurfaceMode.java`
- `src/main/java/com/mathmod/registry/ModDataComponents.java`
- `src/main/java/com/mathmod/network/**`
- `src/main/java/com/mathmod/client/screen/**`
