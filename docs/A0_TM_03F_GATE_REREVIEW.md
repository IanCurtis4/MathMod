# A0-TM-03F Gate Re-review

**Task:** task 7 correction / `A0-TM-03F`  
**Date:** 2026-07-26  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`

## Evidence accepted

- A0-4-R1 is closed: render, hit testing, scroll geometry, filtered ordering,
  and keyboard reveal now enumerate `authoringPalette.categories()` and
  registry forms.
- Form label, compact symbol, icon id, category search label, output hint, and
  form identity search candidates now originate in the metadata projection.
- `authoring-registry-palette` now has a real `UiPreviewHarness` branch.
- The focused suite was forced with `--rerun-tasks`: 135 tests, 0 failures,
  0 errors, 0 skipped.
- The standard build passed: 437 tests, 0 failures, 0 errors, 0 skipped.
- Exact replay, numeric canonicalization, stable ids, and the forbidden
  persistence/network/public-API boundaries remain unchanged.

## Residual blocking findings

### A0-4-FR1 — Narration and tooltip names remain legacy-derived

`paletteNarrationTitle()` still returns
`Component.translatable(action.translationKey())`, and
`customActionTooltip()` still constructs its primary line from the same legacy
action key. This contradicts the handoff claim that narrator and tooltip
presentation consume `AuthoringPalettePresentation.Form`, and it leaves the
technical fallback absent from those two player-visible paths.

The correction must resolve the selected registry form by stable form id and
use the same bounded `formDisplayName(form)` policy for visible row, tooltip,
and narrator title. `CustomSpellAction` remains permitted only for the existing
preview/mutation/knowledge behavior after that resolution.

### A0-4-FR2 — Category appearance is not preserved

The new id-based color switch changes established appearance:

| Category | Required existing color | Current corrected color |
|---|---|---|
| `alchemy` | `GREEN` | `CORAL` |
| `metamagic` | `GOLD` | `MUTED` |
| `queries` | `GREEN` | `MUTED` |
| `effects` | `CORAL` | `CORAL_SOFT` |

The registry category id must choose the same color previously associated with
the corresponding legacy category. Color remains presentation only and must
not become semantic identity.

### A0-4-FR3 — Preview coverage and recorded execution are incomplete

The new harness branch focuses and moves the keyboard cursor without activation,
then performs a search and pointer click. It does not exercise keyboard Enter
activation, a numeric descriptor dialog, technical fallback, or narrator
inspection. No EN/PT-BR or 640x480 execution artifact is present, and the
handoff explicitly defers the real-client run.

Registering one mode and taking one path does not satisfy the frozen matrix
requiring pointer/keyboard equivalence, search, narrator, descriptor dialog,
fallback, locales, and viewport boundaries. The residual correction must either
add bounded subcases or add explicit runtime assertions to existing authorized
cases, and must record actual execution of the three matrix entries. Synthetic
missing-presentation fallback may be proven in the package-private projection
test because built-in metadata is structurally complete; it must still cover
row, tooltip, and narration presentation policy.

### A0-4-FR4 — Released ownership inventory is incomplete

The corrected handoff lists `UiPreviewHarness.java` under changed files but
omits it from released file ownership. Every changed path must be released
explicitly before the gate can close.

## Bounded correction `A0-TM-03F2`

**Owner:** Terra Medium integrator  
**Status:** `READY`

Required documentation:

- `docs/A0_TM_03_GATE_REVIEW.md`;
- this re-review;
- `docs/A0_TM_03_READINESS_ACCEPTANCE.md`;
- frozen A0 contract, A0-4;
- `docs/UI_PREVIEWS.md`;
- current `docs/handoffs/A0_TM_03_HANDOFF.md`.

Required output:

- close A0-4-FR1 through A0-4-FR4 only;
- add focused assertions for registry-backed tooltip/narrator fallback and exact
  legacy category-color parity;
- provide executable preview coverage and actual run evidence for EN/PT-BR
  1024x800 and PT-BR 640x480;
- rerun the 135-test focused boundary and the standard build;
- update the existing handoff with the exact delta, commands/results, preview
  artifact or log paths, limitations, and complete released ownership.

Exact write ownership:

```text
src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java
src/main/java/com/mathmod/client/screen/AuthoringPalettePresentation.java
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/main/java/com/mathmod/client/UiPreviewMatrix.java
src/test/java/com/mathmod/client/UiPreviewMatrixTest.java
src/test/java/com/mathmod/client/screen/PaletteSearchTest.java
src/test/java/com/mathmod/client/screen/AuthoringPalettePresentationTest.java
src/test/java/com/mathmod/client/screen/RuneProgrammerRegistrySourceTest.java
docs/UI_PREVIEWS.md
docs/handoffs/A0_TM_03_HANDOFF.md
```

No other path is writable without a prior board update. The forbidden
boundaries in `docs/A0_TM_03_GATE_REVIEW.md` remain unchanged.

## Operational result

`A0-TM-03F` remains `NEEDS_FIX`; `A0-TM-03F2` is `READY`.
`A0-TM-04` and `A0-W4-GATE` remain `BLOCKED`.
