# Handoff: A1-TM-READONLY-F

## Completed

- Corrected the A1 read-only canvas gate findings without changing graph, persistence, mode, network, execution, or inscription state.
- Kept canvas transform, pan, focus, geometry, sockets, and rendering client-side only.

## Gate finding closure

- **A1-R1:** `ProgramGraphPresentation.Viewport` now supplies the sole logical-to-screen transform for node position, dimensions, socket positions, edge endpoints, clipping checks, and hit-testing. `RuneInspectorScreen` renders and selects through `Rect`; labels use the same zoom scale. Tests cover non-overlap and exact hit bounds at minimum, default, and maximum zoom.
- **A1-R2:** middle-button drag stores pointer state and applies bounded positive and negative deltas. The viewport test reaches both extrema, returns to origin, and then verifies focused-node reveal.
- **A1-R3:** `ProgramInspectorPresentation.Node` exposes deterministic declared input names. The screen renders each named input socket, anchors each edge to its matching socket, and keeps the output socket visible. Tests cover a two-input `number_add` node, deterministic `a`, `b` order, bindings, and socket geometry.
- **A1-R4:** presentation narration now contains read-only state, selected identity, ordered focus position, localized formula/binding context, output socket/type, and zoom/pan state. The presentation-model test verifies the focus position, output socket, and named bindings. Existing localization keys are used; socket and node identifiers remain canonical data values rather than player-facing copy.
- **A1-R5:** canvas drawing is bounded by `GuiGraphics.enableScissor`/`disableScissor`; pointer selection first rejects positions outside the same canvas rectangle. Viewport-model tests cover 320x240 at minimum zoom and 1920x1080 at maximum zoom, including clipping state, reveal, non-overlap, socket alignment, and hit bounds.

## Files changed

- `src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java`
- `src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java`
- `src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java`
- `src/test/java/com/mathmod/client/screen/ProgramGraphPresentationTest.java`
- `src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java`
- `src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java`
- `docs/handoffs/A1_TM_READONLY_F_HANDOFF.md`

## Verification

- `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.client.screen.*' --no-daemon`
  - `BUILD SUCCESSFUL` (27 actionable tasks; 2 executed, 25 up-to-date).
- `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon`
  - `BUILD SUCCESSFUL` (31 actionable tasks; 2 executed, 29 up-to-date).

## Boundaries and migration impact

- None. No change was made to `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, Data Components, payloads, networking, execution, inscription, or canvas persistence.
- No preview harness or localization file was edited; both are outside this task's exact write ownership.

## Known limitations

- This remains the bounded read-only canvas slice: direct graph editing, advanced mode, undo/redo, and persisted canvas arrangement are intentionally deferred.
- The default Windows Gradle cache under the non-ASCII user profile can fail worker bootstrap; verification uses `C:\codex-gradle-a0`.

## Unresolved questions

- None within the granted correction scope.

## Next owner

- Sol

## Exact next task

- Review `A1-TM-READONLY-F` against `docs/A1_READONLY_GATE_REVIEW.md` and accept or reject the bounded correction. On acceptance, release the listed screen ownership so `A0-TM-03` can proceed.

## Released file ownership

- Released changed: `src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java`
- Released changed: `src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java`
- Released changed: `src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java`
- Released changed: `src/test/java/com/mathmod/client/screen/ProgramGraphPresentationTest.java`
- Released changed: `src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java`
- Released changed: `src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java`
- Released changed: `docs/handoffs/A1_TM_READONLY_F_HANDOFF.md`

## Files explicitly untouched

- `src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java`
- all preview-harness and localization files
- `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, Data Components, networking, execution, and inscription files.
