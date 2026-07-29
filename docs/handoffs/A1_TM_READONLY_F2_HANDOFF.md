# Handoff: A1-TM-READONLY-F2

## Completed

- Closed the residual A1 read-only canvas findings without changing graph, persistence, mode, networking, execution, inscription, or preview behavior.
- Retained the inspector as a client-only, read-only presentation surface.

## Residual finding closure

- **A1-F-R1 — transformed edge labels:** every edge again renders its input name. The label derives from the transformed target socket/edge geometry, scales with the canvas, is bounded to the inner content rectangle, and is rendered under that rectangle's scissor. Geometry tests exercise two socket rows and bounded labels at minimum, default, and maximum zoom.
- **A1-F-R2 — bilingual semantic narration:** added only the authorized EN/PT-BR keys: `screen.mathmod.rune_inspector.input_sockets`, `screen.mathmod.rune_inspector.output_socket`, and `screen.mathmod.rune_inspector.viewport`. Narration now names bindings as input sockets, names the output socket, and labels zoom and pan through the dedicated viewport component while retaining read-only state and focus position. Tests verify narration data, screen composition keys, JSON parsing, and EN/PT-BR key/format-token parity.
- **A1-F-R3 — one inset-aware content rectangle:** `contentRect()` is the sole inner rectangle for scissor, render origin, edge labels, pointer hit-testing, pan, zoom, reveal, and content bounds. Inset/nonzero-origin vectors verify focused node and socket visibility at the minimum and maximum supported zoom.

## Files changed

- `src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java`
- `src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java`
- `src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java`
- `src/test/java/com/mathmod/client/screen/ProgramGraphPresentationTest.java`
- `src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java`
- `src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java`
- `src/main/resources/assets/mathmod/lang/en_us.json` — only the three authorized keys
- `src/main/resources/assets/mathmod/lang/pt_br.json` — only the three authorized keys
- `docs/handoffs/A1_TM_READONLY_F2_HANDOFF.md`

## Verification

- `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.client.screen.*' --no-daemon`
  - `BUILD SUCCESSFUL` (27 actionable tasks; 2 executed, 25 up-to-date).
- `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon`
  - `BUILD SUCCESSFUL` (31 actionable tasks; 2 executed, 29 up-to-date).

## Boundaries and migration impact

- None. `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, Data Components, payloads, networking, execution, inscription, preview harness, and canvas persistence remain untouched.
- This task added exactly the three authorized presentation keys. The shared working tree already contained separate Portuguese terminology edits before this task; they were preserved and not modified by this correction.

## Known limitations

- This remains the bounded read-only canvas slice: editing, advanced mode, undo/redo, and persisted canvas arrangements are deferred.
- The default Windows Gradle cache under the non-ASCII user profile can fail worker bootstrap; verification uses `C:\codex-gradle-a0`.

## Unresolved questions

- None within the granted residual-correction scope.

## Next owner

- Sol

## Exact next task

- Review `A1-TM-READONLY-F2` against `docs/A1_READONLY_CORRECTION_REVIEW.md`. On acceptance, release the granted screen and locale ownership so the blocked `A0-TM-03` integration may be scheduled.

## Released file ownership

- Released changed: `src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java`
- Released changed: `src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java`
- Released changed: `src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java`
- Released changed: `src/test/java/com/mathmod/client/screen/ProgramGraphPresentationTest.java`
- Released changed: `src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java`
- Released changed: `src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java`
- Released changed: `src/main/resources/assets/mathmod/lang/en_us.json` (three authorized keys only)
- Released changed: `src/main/resources/assets/mathmod/lang/pt_br.json` (three authorized keys only)
- Released changed: `docs/handoffs/A1_TM_READONLY_F2_HANDOFF.md`

## Files explicitly untouched

- `src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java`
- all preview-harness files
- `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, Data Components, networking, execution, and inscription files.
