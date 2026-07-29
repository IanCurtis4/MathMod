# Handoff: A1-TM-READONLY

## Completed
- Added a logical, transient canvas viewport with bounded pan and zoom in the read-only presentation layer.
- Added focus reveal so keyboard selection cannot leave the focused node off-canvas.
- Added edge input-name labels and visible output socket markers to the inspector canvas.
- Added inspector focus narration containing node identity/type/purity and zoom state.
- Added deterministic-layout, viewport, focus-reveal, and source-level keyboard/pointer coverage.

## Decisions implemented
- Canvas coordinates and viewport state remain client-only presentation values; they do not enter `ProgramGraph`, persistence, cost, execution, or inscription.
- The inspector remains read-only: no packets, mutation service, mode change, or persistence path was added.
- Middle-click pans within bounded content extents; keyboard +/- changes bounded zoom; arrow keys retain selection traversal and reveal the focused node.

## Files changed
- `src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java`
- `src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java`
- `src/test/java/com/mathmod/client/screen/ProgramGraphPresentationTest.java`
- `src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java`
- `src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java`
- `docs/handoffs/A1_TM_READONLY_HANDOFF.md`

## Contracts referenced
- `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`, A1 and Cycle 1 requirements.
- `docs/FOUNDATION_BETA_A0_ASSIGNMENT.md`, section 6.3.
- `docs/ADVANCED_EDITOR.md`, read-only rollout and acceptance.
- `docs/P2_MODE_PERSISTENCE_CONTRACT.md`.
- `docs/UI_PREVIEWS.md`.
- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`, A0-4 ordering boundary.
- `docs/A0_LEGACY_ADAPTER_GATE_ACCEPTANCE.md`.

## Tests and evidence
- command: `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.client.screen.*' --no-daemon`
  result: `BUILD SUCCESSFUL`.
- command: `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon`
  result: `BUILD SUCCESSFUL`.
- Tests cover stable graph presentation, named input bindings, deterministic inspector layout, bounded viewport zoom/pan/reveal at 320x240 and 1920x1080 logical targets, and source evidence for read-only pointer/keyboard/focus/narration behavior.

## Migration impact
- None. No graph, canvas, mode, Data Component, payload, network, execution, or inscription persistence changed.

## Known limitations
- This is the read-only A1 slice only. Direct node/edge/parameter mutation, advanced mode, undo/redo, and canvas persistence remain deferred.
- No preview-harness file was edited; visual preview capture remains owned by its designated future task.

## Unresolved questions
- None for the granted read-only files.

## Next owner
- Sol

## Exact next task
- Review A1-TM-READONLY and, if accepted, release the granted screen ownership for serialized A0-TM-03 planning.

## Released file ownership
- Released changed: `src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java`
- Released unchanged: `src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java`
- Released changed: `src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java`
- Released changed: `src/test/java/com/mathmod/client/screen/ProgramGraphPresentationTest.java`
- Released changed: `src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java`
- Released changed: `src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java`
- No further edits are retained by this task.

## Files explicitly untouched
- `src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java`
- all preview-harness files
- `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, Data Components, networking, execution, and inscription files.
