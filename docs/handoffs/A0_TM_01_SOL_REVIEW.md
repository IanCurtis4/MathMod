# Sol Gate Review: A0-TM-01

**Decision:** `ACCEPT` — `A0-TM-01` is `DONE`

**Reviewed:** 2026-07-26

**Reviewer:** Sol

This acceptance supersedes the earlier rejection recorded in this same review.
The earlier findings are retained below as gate history.

## Evidence accepted

- Numeric canonicalization conforms to section 6.3 of the frozen A0 contract:
  `NaN`, positive infinity, and negative infinity return the declared default;
  finite values clamp to the inclusive bounds.
- The focused suite characterizes all 67 frozen Rune Form ids and all 11
  frozen category ids.
- Focused verification passed with an isolated ASCII Gradle cache:

  ```text
  GRADLE_USER_HOME=C:\codex-gradle-a0
  .\gradlew.bat cleanTest test --tests com.mathmod.authoring.BuiltInAuthoringMetadataTest --no-daemon --rerun-tasks
  BUILD SUCCESSFUL
  6 tests, 0 failures, 0 errors, 0 skipped
  ```

- The standard build passed with the same isolated cache:

  ```text
  GRADLE_USER_HOME=C:\codex-gradle-a0
  .\gradlew.bat build --no-daemon
  BUILD SUCCESSFUL
  ```

- The original `%USERPROFILE%\.gradle` cache reproducibly fails before JUnit
  startup with `ClassNotFoundException:
  worker.org.gradle.process.internal.worker.GradleWorkerMain`. Successful
  isolated-cache executions classify this as a local Gradle cache/bootstrap
  problem rather than repository test failure.
- No tracked diff or worktree status was found for `ProgramGraph`,
  `GuidedWorkspaceState`, `ProgramSurfaceMode`, `ModDataComponents`,
  networking, or client screens. `GuidedWorkspaceState.CURRENT_VERSION`
  remains `1`.

## Previous blocking findings — resolved

1. The repository contains A0-3 implementation files even though A0-3 and
   `A0-TM-02` are still blocked:

   ```text
   src/main/java/com/mathmod/authoring/TrustedLegacyExpansionAdapter.java
   src/test/java/com/mathmod/authoring/TrustedLegacyExpansionAdapterTest.java
   ```

   The production file resolves a form and calls
   `CustomSpellWorkspace.apply(...)`; the test compares legacy and
   registry-mediated `ProgramGraph` expansion. This is legacy expansion
   adapter work, not A0-1/A0-2 metadata characterization.

2. `docs/handoffs/A0_TM_01_HANDOFF.md` states that A0-3 was not implemented and
   lists only the focused metadata test and handoff as changed files. That file
   inventory does not account for the two adapter files present in the shared
   worktree and does not provide ownership or provenance for them.

These findings caused the initial rejection. They no longer describe the
current worktree.

## Resolution verified

- The premature `TrustedLegacyExpansionAdapter.java` and
  `TrustedLegacyExpansionAdapterTest.java` files were removed from the shared
  worktree.
- The handoff now lists the complete A0-TM-01 delivery:

  ```text
  src/main/java/com/mathmod/authoring/AuthoringMetadata.java
  src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java
  src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java
  docs/handoffs/A0_TM_01_HANDOFF.md
  ```

- Sol independently reran the focused suite:

  ```text
  GRADLE_USER_HOME=C:\codex-gradle-a0
  .\gradlew.bat cleanTest test --tests com.mathmod.authoring.BuiltInAuthoringMetadataTest --no-daemon
  BUILD SUCCESSFUL
  6 tests, 0 failures, 0 errors, 0 skipped
  ```

- Sol independently reran the standard build:

  ```text
  GRADLE_USER_HOME=C:\codex-gradle-a0
  .\gradlew.bat build --no-daemon
  BUILD SUCCESSFUL
  ```

- No A0 reference or worktree change exists in `ProgramGraph`,
  `GuidedWorkspaceState`, `ProgramSurfaceMode`, `ModDataComponents`,
  networking, or client screens.
- `GuidedWorkspaceState.CURRENT_VERSION` remains `1`; no Data Component,
  payload, public API, or other persistent boundary was introduced.

## Gate outcome

- `A0-TM-01`: `DONE`
- `A0-TH-01`: `READY`
- `A0-TM-02`: remains `BLOCKED` pending completion and Sol acceptance of
  `A0-TH-01`
- `L0-TH-AUDIT` and `A0-LU-INVENTORY`: remain independent
