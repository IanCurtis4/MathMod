# P12 Factored Leap Statement Presentation Contract

**Task:** `P12-SOL-03`  
**Date:** 2026-08-02  
**Owner:** Sol  
**Decision:** `ACCEPT`  
**Dispatch unlocked:** `P12-TM-05`

## 1. Decision

The exact Factored Leap statement remains:

```text
let halve(v)=v*0.5 in push(self,halve(look)+halve((0,1,0)))
```

The exact catalog formula remains:

```text
push(halve(look)+halve(up))
```

Neither string, its localization, theorem identity, graph, inscription
semantics nor accepted Data Component identity may be changed to make the text
fit. The failure recorded in
`docs/P12_FACTORED_LEAP_STATEMENT_PRESENTATION_FINDING.md` is a bounded client
layout defect.

The accepted correction is a dynamic, bounded third statement line. Presets
whose statements fit in one or two lines retain the current two-line header
geometry. A statement that requires three lines grows the statement region by
exactly one production line height and moves the graph viewport origin down by
that same amount.

This contract authorizes implementation only; it does not claim the defect
closed. `P12-TM-05` must provide the repository handoff and evidence below for
Sol review before a new immutable P12-DS artifact is generated.

## 2. Frozen presentation invariants

1. The maximum supported statement presentation is three complete rendered
   lines. Ellipsis, clipping, hidden tails and semantic abbreviation are not
   accepted.
2. The visible rendered text, reconstructed in line order, must equal the exact
   statement above. Whitespace introduced only at visual wrap boundaries must
   not alter the statement.
3. Every rendered line must fit the measured production statement width using
   the real Minecraft font.
4. One- and two-line statements keep the existing statement height and graph
   viewport origin. The Factored Leap correction must not shift unrelated
   presets.
5. Three-line statements add exactly one `LINE_HEIGHT` to the effective
   statement region and graph viewport origin.
6. Rendering, graph clipping, scroll range, scrollbar placement, mouse
   hit-testing and node hover must use the same computed graph viewport origin.
   A parallel or hard-coded geometry authority is forbidden.
7. Preset selection and resize must recompute the statement lines, hitbox and
   graph viewport in the same state transition. Stale geometry for the
   previously selected preset or viewport is forbidden.
8. The complete statement remains available to mouse, keyboard focus,
   narration and tooltip/accessibility paths.
9. At the minimum supported layout, the resulting graph viewport must remain
   positive, visible and scrollable; statement and graph content must not
   overlap.
10. If any built-in theorem requires more than three lines at a required
    viewport, the gate fails. The implementation must not silently cap it.

`TheoremStatementPresentation` already owns font-aware line construction and
is read-only for this task. If Terra Medium proves it cannot satisfy these
invariants without changing that class, it must stop and escalate a concrete
counterexample to Sol rather than broadening ownership.

## 3. Preflight authority and temporary-debt retirement

The temporary exemptions authorized for `laboratory-self-repeat` were evidence
unblockers, not product policy. `P12-TM-05` must retire them as follows:

- `requireTheoremStatementFit` must accept at most three complete lines and run
  for every preview mode, including `laboratory-self-repeat`;
- `requireTheoremCatalogFormulaFit` must continue checking every ordinary
  catalog formula while encoding the already accepted exact Factored Leap
  catalog-formula exception; skipping the entire preflight for
  `laboratory-self-repeat` is forbidden;
- the exception must be identity-specific and exact. It must not weaken the
  width rule for another theorem or another string;
- the Factored Leap runtime preview must select the real registered preset. A
  copied formula, synthetic graph or alternate theorem is not evidence.

The accepted catalog-formula clipping behavior is not expanded or redesigned
by this task. This task closes only the full selected-statement failure.

## 4. Exact ownership for P12-TM-05

### Production write ownership

```text
src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java
```

### Harness, tests, evidence and handoff write ownership

```text
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/main/java/com/mathmod/client/UiPreviewMatrix.java
src/test/java/com/mathmod/client/UiPreviewMatrixTest.java
src/test/java/com/mathmod/client/screen/ProgrammerLayoutTest.java
src/test/java/com/mathmod/client/screen/FactoredLeapStatementPresentationTest.java
docs/UX_AUDIT.md
docs/handoffs/P12_TM_05_HANDOFF.md
```

The new focused test file may be created. Existing evidence filenames may be
added only under the repository's established preview-evidence location.

### Read-only inputs

```text
src/main/java/com/mathmod/client/screen/TheoremStatementPresentation.java
src/main/java/com/mathmod/client/screen/TheoremFormulaBreaks.java
src/main/java/com/mathmod/program/ProgramPresets.java
src/main/resources/assets/mathmod/lang/en_us.json
src/main/resources/assets/mathmod/lang/pt_br.json
docs/L0_FIRST_GAMEPLAY_THEOREM_SPECIFICATION.md
docs/L0_INTERNAL_GAMEPLAY_INTEGRATION_READINESS.md
```

No other production or test file is writable without a new repository-recorded
Sol authorization. In particular, this task may not change `ProgramGraph`,
`GuidedWorkspaceState`, `ProgramSurfaceMode`, menus, networking, Data Components
or schemas, persistence, server behavior, public APIs, theorem ids, formulas or
localizations.

## 5. Required acceptance evidence

The handoff must identify every changed file and map evidence to each invariant.
It must include real client previews, measured with the production font, for:

| Vector | Locale | Window / GUI scale | Required proof |
|---|---|---|---|
| FS-01 | EN-US | 1024x800 / 2 | full exact statement, three bounded lines, graph below it |
| FS-02 | PT-BR | 1024x800 / 2 | same geometry and complete statement |
| FS-03 | PT-BR | 640x480 / 2 | minimum-layout fit, positive graph viewport and no overlap |
| FS-04 | EN-US or PT-BR | minimum supported width / 2 | second-node hover aligns with the displaced graph |
| FS-05 | EN-US | 1024x800 / 2 | a legacy one/two-line preset retains its prior graph origin |

Each retained image filename must include its locale and vector identity so one
run cannot overwrite another. Logs must report available width, exact line
count, width of every line, statement hitbox, computed graph origin and selected
theorem id. The handoff must also state that the visible lines reconstruct the
exact frozen formula.

Focused tests must prove at least:

- Factored Leap produces exactly three fitting lines at the standard and
  minimum required widths;
- all registered built-in statements produce no more than three lines;
- legacy one/two-line geometry is unchanged;
- three-line geometry moves the graph, clipping, scrollbar, scroll range and
  hit-testing together;
- selection and resize cannot retain stale statement or graph geometry;
- the exact catalog exception is narrow and all other catalog formulas remain
  checked;
- `laboratory-self-repeat` no longer bypasses either theorem preflight;
- client-only classes remain absent from dedicated-server load paths.

## 6. Mandatory commands

Run from the repository root with the established isolated Gradle home:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.program.ProgramPresetsTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.client.screen.ProgrammerLayoutTest `
  --tests com.mathmod.client.screen.FactoredLeapStatementPresentationTest `
  --tests com.mathmod.ServerSideIsolationTest

.\gradlew.bat cleanTest test --no-build-cache
.\gradlew.bat build
```

Run the required client-preview vectors separately and preserve their named
images and logs. No GameTest is required: the correction is client presentation
only. A green build does not replace the runtime font, geometry, hover and
localization evidence.

## 7. Gate and sequencing

`P12-SOL-03` is `DONE` with `ACCEPT`. This decision originally made
`P12-TM-05` `READY` for Terra Medium under this exact ownership. `P12-DS`
remains blocked; no new immutable JAR or continuation of its runtime vectors is
authorized until Sol accepts the resulting implementation gate.

`P12-DS-MP` remains a separate `BACKLOG` item. This contract neither passes nor
waives multiplayer evidence.

> Implementation review, 2026-08-03: the first `P12-TM-05` handoff is
> `NEEDS_FIX`. `P12-TM-05F` is `READY` under
> `docs/P12_TM_05_GATE_REVIEW.md`. This does not change the frozen presentation
> decision above.

> Final implementation acceptance, 2026-08-03: `P12-TM-05` and
> `P12-TM-05F` are `DONE` with `ACCEPT` under
> `docs/P12_TM_05F_FINAL_GATE_ACCEPTANCE.md`. The single-client `P12-DS` task is
> `READY` for a new immutable artifact and clean DS-01 rerun.
