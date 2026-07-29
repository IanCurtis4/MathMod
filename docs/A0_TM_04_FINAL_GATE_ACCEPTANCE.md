# A0-TM-04 Final Gate Acceptance

**Task:** task 8 / `A0-TM-04` — A0 Compatibility Hardening  
**Correction:** `A0-TM-04F`  
**Date:** 2026-07-26  
**Reviewer:** Sol  
**Decision:** `ACCEPT`

## Decision

A0-5 is accepted. Findings A0-5-R1 and A0-5-R2 are closed by the three
dedicated-server persisted-item vectors. A0-5-R3 is closed by correcting the
scope claim: built-in reconstruction is the applicable A0 behavior, while
external loader/reload publication and active last-known-good state remain an
explicit A0-6 deferral.

The deferral is not a defect in the implemented A0 slice because no external
publisher or active reload state exists. This acceptance does not claim that
last-known-good publication has been implemented or tested, and it does not
authorize external loaders.

## Findings closure

### A0-5-R1 — Closed

`A0CompatibilityGameTests.schemaOneReadReplaysExactlyWithoutRewritingTalisman`
uses a real programmed talisman with typed Data Components and reads through
`GuidedWorkspacePersistence.read`. It proves:

- schema 1 returns `AVAILABLE`;
- state and exact replay are preserved;
- `ProgramStorage.get` retains the authoritative graph;
- the graph remains executable and inspectable;
- `ItemStack.isSameItemSameComponents` remains true across the read.

### A0-5-R2 — Closed

The unknown-current-schema and future-schema GameTests prove:

- `UNREPLAYABLE`;
- no replacement or mutation of the authoritative graph;
- executable validation remains valid;
- read-only inspection remains available;
- no item component rewrite.

Malformed serialized input remains correctly tested at the codec boundary. It
is not installed synthetically as an invalid typed Data Component.

The accepted missing-presentation fallback remains covered by
`BuiltInAuthoringMetadataTest.formulaAndFallbackRemainBounded`.

### A0-5-R3 — Closed by explicit deferral

The corrected handoff no longer claims active last-known-good retention. The
current product has deterministic trusted built-in reconstruction but no
external metadata loader, candidate publisher, or active reload generation.

The frozen contract places external sources in deferred A0-6 and requires the
A0 W4 gate to decide their future contract status. Therefore:

- built-in reconstruction/reconnect evidence is accepted for A0-5;
- external publication atomicity and last-known-good retention remain
  unimplemented and untested;
- task 9 must carry this as a named deferral owned by a future separately
  approved A0-6 contract;
- no public datapack/KubeJS schema, loader, callback, or API is authorized.

## Independently reproduced evidence

Focused suite:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.authoring.*' --tests 'com.mathmod.program.GuidedWorkspaceStateTest' --tests 'com.mathmod.program.AuthoringSchema1CompatibilityTest' --tests 'com.mathmod.ServerSideIsolationTest' --rerun-tasks --no-daemon
```

Result: `BUILD SUCCESSFUL`; the handoff records 20 focused tests with zero
failures, errors, or skipped tests.

Dedicated GameTest server:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat runGameTestServer --no-daemon
```

Result independently reproduced: `BUILD SUCCESSFUL`;
`All 14 required tests passed`, including the three new A0 persisted-item
vectors.

Standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon
```

Result independently reproduced: `BUILD SUCCESSFUL`.

## Accepted file inventory

```text
src/main/java/com/mathmod/program/A0CompatibilityGameTests.java
src/test/java/com/mathmod/authoring/AuthoringCompatibilityHardeningTest.java
src/test/java/com/mathmod/program/AuthoringSchema1CompatibilityTest.java
src/test/java/com/mathmod/ServerSideIsolationTest.java
docs/handoffs/A0_TM_04_HANDOFF.md
```

`A0CompatibilityGameTests` resides in the main source set solely for NeoForge
GameTest discovery. No existing production authority was changed.

## Boundary verification

- `GuidedWorkspaceState` remains schema 1.
- No persistence codec or Data Component declaration changed.
- No item is rewritten during read.
- No `ProgramGraph`, `ProgramStorage`, `ProgramSurfaceMode`, networking,
  payload, client screen, execution, inscription, stable-id, or public API
  boundary changed.
- Dedicated-server evidence has no client-presentation dependency.
- Unknown, malformed, and future metadata are not repaired or partially
  replayed.

## Downstream authorization

- `A0-TM-04`: `DONE` (`ACCEPT`).
- `A0-TM-04F`: `DONE` (`ACCEPT`).
- `A0-W4-GATE` / task 9: `READY`.

Task 9 is documentation/gate work only. It must classify external A0 loaders,
candidate publication, and last-known-good retention as deferred with a future
owner unless a separate contract explicitly promotes A0-6.
