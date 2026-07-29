# A0-TM-03 Gate Review

**Task:** task 7 / `A0-TM-03`  
**Date:** 2026-07-26  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`

## Accepted evidence

- The handoff exists at `docs/handoffs/A0_TM_03_HANDOFF.md` and its changed-file
  inventory matches the ownership granted for the original task.
- The immutable projection reports 11 ordered categories and 67 ordered forms.
- Focused tests cover descriptor defaults/bounds/canonicalization, ignored
  unknown keys, all built-in default replay vectors, parameterized replay, and
  representative sequence replay with exact `ProgramGraph` equality.
- The focused command was re-run with `--rerun-tasks`: 135 tests, 0 failures,
  0 errors, 0 skipped.
- The standard `build` completed successfully: 437 tests in the resulting full
  report, 0 failures, 0 errors, 0 skipped.
- No A0-TM-03 change was made to persistence codecs, schema, Data Components,
  networking, `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, or
  public APIs. Existing unrelated accepted A1/Luna changes remain in the shared
  working tree and are not attributed to this handoff.
- The first forced verification attempt encountered a transient Windows file
  lock in NeoForm recompilation. After the prior Gradle process exited, the same
  forced command and the standard build succeeded. This is environmental and
  is not itself a product defect.

## Blocking findings

### A0-4-R1 — Category enumeration is still legacy-authoritative

`RuneProgrammerScreen` still iterates
`CustomSpellAction.Category.values()` when rendering the custom palette,
calculating content height, and revealing the keyboard cursor. It then filters
registry forms back through `action.category()`.

This does not satisfy the frozen requirement that
`BuiltInAuthoringMetadata.snapshot()` be the source of category enumeration and
ordering. Passing a registry-derived form list through the legacy category enum
does not transfer category authority to the registry.

### A0-4-R2 — Form presentation, search, and narration are still legacy-derived

The custom palette continues to obtain category labels, form labels, compact
notation, icons, result presentation, search terms, narrator titles, and
tooltips from `CustomSpellAction`. The new `technicalName()` fallback exists
only in `AuthoringPalettePresentation` and has no screen consumer.

This contradicts the required registry-backed presentation behavior and leaves
the required bounded technical fallback unexercised. It also means the source
test proves the presence of selected registry calls, not that the visible and
narrated client surface consumes the registry projection.

### A0-4-R3 — The registered preview mode is not executable

`authoring-registry-palette` was added to `UiPreviewMatrix` and
`docs/UI_PREVIEWS.md`, but `UiPreviewHarness` has no branch for that mode. A
matrix entry without harness setup does not exercise the declared Guided
palette state.

Consequently there is no executable evidence for the required EN/PT-BR,
minimum/ATM10 viewport, pointer/keyboard equivalence, search, narrator, numeric
descriptor dialog, or technical fallback review. The absence of a real-client
capture is acceptable only after the mode itself is wired and executed; in the
current delta it masks an unimplemented preview path.

## Correction task

Create bounded correction task `A0-TM-03F`, owned by Terra Medium.

Required documentation:

- `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`;
- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`, especially A0-4;
- `docs/A0_TM_03_READINESS_ACCEPTANCE.md`;
- this review;
- `docs/handoffs/A0_TM_03_HANDOFF.md`;
- `docs/UI_PREVIEWS.md`;
- accepted A0-3 adapter and Luna evidence.

Required output:

- use registry category descriptors for category enumeration/order in render,
  hit-testing geometry, scrolling, and keyboard reveal;
- use registry form/category presentation for visible labels, compact formula,
  icon id, search terms, narration, and tooltips while retaining the existing
  legacy action only as bounded mutation/replay authority;
- connect and test the bounded technical fallback without substituting another
  form;
- wire `authoring-registry-palette` in the real preview harness;
- execute and record EN/PT-BR standard and 640x480 previews, including
  pointer/keyboard equivalence, search, narration inspection, descriptor
  dialog, and fallback;
- retain exact legacy graph replay and unchanged packet/mutation behavior;
- rerun focused tests and the standard build;
- update `docs/handoffs/A0_TM_03_HANDOFF.md` with the correction delta,
  evidence, limitations, exact changed files, and released ownership.

Exact write ownership:

```text
src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java
src/main/java/com/mathmod/client/screen/AuthoringPalettePresentation.java
src/main/java/com/mathmod/client/UiPreviewMatrix.java
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/test/java/com/mathmod/client/UiPreviewMatrixTest.java
src/test/java/com/mathmod/client/screen/PaletteSearchTest.java
src/test/java/com/mathmod/client/screen/AuthoringPalettePresentationTest.java
src/test/java/com/mathmod/client/screen/RuneProgrammerRegistrySourceTest.java
src/test/java/com/mathmod/authoring/TrustedLegacyExpansionAdapterTest.java
docs/UI_PREVIEWS.md
docs/handoffs/A0_TM_03_HANDOFF.md
```

No other file may be created or changed without a prior Sol ownership update.

Forbidden boundaries remain:

```text
ProgramGraph
GuidedWorkspaceState
ProgramSurfaceMode
Data Components
networking and payloads
persistence codecs and schema
execution/inscription authority
public APIs
stable ids
RuneInspectorScreen and accepted A1 presentation helpers
localization and Patchouli content
```

## Operational result

`A0-TM-03` is `NEEDS_FIX`; `A0-TM-03F` is `READY`.
`A0-TM-04` (task 8) and `A0-W4-GATE` remain `BLOCKED`.
