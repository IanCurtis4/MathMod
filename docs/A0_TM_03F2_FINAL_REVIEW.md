# A0-TM-03F2 Final Review

**Task:** task 7 residual correction / `A0-TM-03F2`  
**Date:** 2026-07-26  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`

## Findings closed

- A0-4-FR1 is closed: palette row, tooltip, and narrator title share the
  registry form presentation/fallback policy.
- A0-4-FR2 is closed: all 11 registry category ids preserve the former category
  color mapping, including alchemy, metamagic, queries, and effects.
- A0-4-FR4 is closed: `UiPreviewHarness.java` is present in both the changed
  inventory and released ownership.
- The focused suite was forced: 137 tests, 0 failures, 0 errors, 0 skipped.
- The standard build completed successfully.
- No forbidden persistence, graph, mode, Data Component, networking, execution,
  Inspector, localization, or public API boundary changed.

## Residual blocking finding

### A0-4-F2R1 — Runtime preview sequence does not perform the declared search

The harness currently executes:

```text
focusPaletteAndMove(screen, 1, true)
searchLaboratory(screen, "simpson")
clickFirstCustomPaletteRow(screen)
```

Keyboard Enter on row 1 opens the numeric dialog for `Number`. Because that
dialog is modal and its first numeric field is focused, `searchLaboratory`
types into the numeric field instead of the palette search box. The following
pointer click is also consumed by the modal.

All three submitted screenshots prove the same incorrect terminal state:

```text
dialog title: Parameters: Number / Parâmetros: Número
numeric field: 1simpson
```

Therefore the run proves keyboard activation of `Number`, but does not prove a
registry search for Simpson or pointer activation of the Simpson descriptor
dialog. The source-presence test passes because it checks method-call strings,
not the runtime state reached by those calls.

Evidence:

```text
run/client/screenshots/mathmod-authoring-registry-palette-en_us-1024x800-preview.png
run/client/screenshots/mathmod-authoring-registry-palette-pt_br-1024x800-preview.png
run/client/screenshots/mathmod-authoring-registry-palette-pt_br-640x480-preview.png
```

## Final bounded correction `A0-TM-03F3`

**Owner:** Terra Medium integrator  
**Status:** `READY`

Required output:

- activate a non-parameterized registry form by keyboard Enter and assert that
  the intended Guided workspace mutation occurred before continuing;
- focus the actual Laboratory search box, enter `simpson`, and assert that the
  filtered first form is the Simpson form;
- activate that filtered form by pointer and assert that the active descriptor
  dialog belongs to Simpson;
- ensure the final screenshots show the Simpson dialog with valid numeric
  defaults and never contain `1simpson`;
- re-run the three frozen client configurations:
  EN 1024x800, PT-BR 1024x800, and PT-BR 640x480;
- retain the screenshot and log evidence for all three runs;
- replace source-string-only preview assertions with, or supplement them by,
  focused state assertions that would fail for the current modal sequencing;
- rerun the focused suite and standard build;
- update `docs/handoffs/A0_TM_03_HANDOFF.md` with exact evidence and release all
  granted files.

Exact write ownership:

```text
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/test/java/com/mathmod/client/screen/RuneProgrammerRegistrySourceTest.java
docs/UI_PREVIEWS.md
docs/handoffs/A0_TM_03_HANDOFF.md
```

The three named runtime screenshot/log artifacts may be replaced by the
corrected executions. No other source or documentation path is writable.
All forbidden boundaries remain unchanged.

## Operational result

`A0-TM-03F2` is `NEEDS_FIX`; `A0-TM-03F3` is `READY`.
`A0-TM-04` and `A0-W4-GATE` remain `BLOCKED`.
