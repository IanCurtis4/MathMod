# Handoff: A0-TM-04F

## Completed

- Closed A0-5-R1 and A0-5-R2 through the real dedicated-server item/Data
  Component path, using a programmed talisman and
  `GuidedWorkspacePersistence.read`.
- Added the three Sol-authorized GameTest vectors in
  `A0CompatibilityGameTests`: valid schema 1, current schema with unknown
  form, and future schema.
- Valid schema 1 proves `AVAILABLE`, exact state/replay/authoritative-graph
  equality, executable graph validation, read-only inspection, and no item
  component rewrite.
- Unknown and future typed workspace metadata prove `UNREPLAYABLE`, unchanged
  authoritative graph, executable validation, read-only inspection, and no
  item component rewrite.
- Malformed serialized input remains separately covered at the codec boundary
  by `AuthoringSchema1CompatibilityTest.legacyUnknownAndMalformedRecipesStayUnreplayableWithoutPartialReplay`.
  A malformed serialized value cannot correctly be installed as a typed Data
  Component, so it is intentionally not faked in GameTest.
- The accepted missing-presentation fallback evidence remains
  `BuiltInAuthoringMetadataTest.formulaAndFallbackRemainBounded`, which
  exercises `RunePresentation.technicalFallback` and its bounded fallback
  formula. No presentation diagnostic or repair behavior was added.

## A0-5-R3 classification

Built-in reconstruction/reconnect remains the applicable A0 guarantee and is
covered by the existing deterministic reconstruction characterization.
External loader/reload publication, an active snapshot reference, and
last-known-good retention state do not exist in A0. They are explicitly
deferred, not claimed as tested product behavior. No test-local publisher or
production publication mechanism was introduced.

## Files changed

- `src/main/java/com/mathmod/program/A0CompatibilityGameTests.java`
- `src/test/java/com/mathmod/program/AuthoringSchema1CompatibilityTest.java`
- `docs/handoffs/A0_TM_04_HANDOFF.md`

The remaining previously authorized focused tests remain unchanged by this
correction.

## Tests and evidence

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.authoring.*' --tests 'com.mathmod.program.GuidedWorkspaceStateTest' --tests 'com.mathmod.program.AuthoringSchema1CompatibilityTest' --tests 'com.mathmod.ServerSideIsolationTest' --rerun-tasks --no-daemon
```

Result: `BUILD SUCCESSFUL`; 20 tests completed with 0 failures, 0 errors, and
0 skipped.

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; the dedicated GameTest server discovered 14 tests
(the existing 11 plus the three A0-TM-04F vectors), and reported `All 14
required tests passed`.

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

## Migration impact

- None. `GuidedWorkspaceState` remains schema 1 and reads do not rewrite the
  talisman.
- No production authority, persistence codec, Data Component declaration,
  `ProgramGraph`, networking/payload path, execution/inscription path, stable
  id, or public API changed. The new main-source class is NeoForge-discovered
  dedicated-server test evidence only, as authorized by Sol.

## Limitations and deferred work

- External loader/reload ingestion and external-source last-known-good
  publication remain deferred because the product has no such publisher or
  active snapshot state in A0.
- Unknown, malformed, and future workspace recipes intentionally remain
  unreplayable; no automatic repair, substitution, or persisted diagnostic was
  introduced. Their authoritative graph stays inspectable and executable.

## Next owner

- Sol

## Exact next task

- Review A0-TM-04F against `docs/A0_TM_04_READINESS_ACCEPTANCE.md` and
  `docs/A0_TM_04_GATE_REVIEW.md`; if accepted, decide A0 W4 / task 9.

## Released file ownership

- `src/test/java/com/mathmod/authoring/AuthoringCompatibilityHardeningTest.java`
- `src/test/java/com/mathmod/program/AuthoringSchema1CompatibilityTest.java`
- `src/test/java/com/mathmod/ServerSideIsolationTest.java`
- `src/main/java/com/mathmod/program/A0CompatibilityGameTests.java`
- `docs/handoffs/A0_TM_04_HANDOFF.md`

## Files the next owner must not edit

- Production authoring/persistence/graph code, `ProgramSurfaceMode`, Data
  Components, networking/payloads, client screens and preview harness,
  execution/inscription authority, stable ids, localization/Patchouli content,
  public KubeJS/datapack APIs, and external loaders.
