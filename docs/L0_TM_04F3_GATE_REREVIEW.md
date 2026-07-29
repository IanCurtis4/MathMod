# L0-TM-04F3 — Gate Re-review

**Date:** 2026-07-29  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Reviewed handoff:** `docs/handoffs/L0_TM_04F3_HANDOFF.md`

## 1. Result

F3 closes F3R2 through F3R5. The focused suite, all 33 GameTests and the build
are green, and the delta remains inside the authorized ownership.

The gate is not accepted because F3R1 remains open. The selector/content
overlap was removed, but the replacement renders the full semantic headings
without wrapping or bounding them to the details panel. The real repository
captures show those headings crossing the panel boundary and being cut off,
especially at 640x480.

`L0-TM-04`, `L0-TM-04F`, `L0-TM-04F2` and `L0-TM-04F3` remain `NEEDS_FIX`.
The next correction is the single bounded task `L0-TM-04F4`. All downstream L0
tasks remain blocked.

## 2. Findings closed by F3

Repository inspection confirms:

- **F3R2 closed:** the runtime harness checks the actual focused widgets,
  complete forward/backward order through Close, non-wrapping row boundaries,
  structural path, row kind, displayed value, click routing and Escape;
- explicit runtime narration vectors now cover mismatch, conflict, unreadable,
  unsupported, stale and graph-absent states;
- diagnostics narrate their bounded code and structural path;
- **F3R3 closed:** the integrated-server preview invokes the real
  `openingSnapshot` seam, counts the production compile branch, mutates live
  `PlayerKnowledge` only after candidate construction, requires stale with no
  rows and restores knowledge before the normal menu open;
- **F3R4 closed:** `writeFailClosed` first encodes in the bounded temporary
  buffer and the GameTest proves that overflow decodes as one stale projection
  with no rows and no remaining bytes;
- **F3R5 closed:** the handoff accurately reports the focused counts as
  `7 + 2 + 1 + 4 + 4 + 5 + 2 = 25`, separately from runtime UI and PNG
  evidence;
- the five functional GameTests remain separate from the fourteen persistence
  GameTests and the global total remains 33;
- the changed production/test/preview files are inside the existing amended
  ownership;
- no `ProgramGraph`, Guided/source schema, Data Component, `ProgramStorage`,
  networking, persistence, migration or public extension API change occurred.

## 3. Remaining finding

### L0-04-F4R1 — Full semantic headings are still outside the bounded panel

`RuneInspectorScreen` now gives each selector its own 20-pixel row and uses
short selector labels. This removes the previous overlap.

However, `drawProjectionHeading` renders the complete semantic label with one
unbounded `drawString` call. It does not use `font.split`, clipping, a bounded
substring or a height calculation based on wrapped lines. `Section.rows`
reserves exactly 11 pixels for that heading regardless of its rendered width.

The resulting defect is visible in all regenerated captures:

```text
run/client/screenshots/mathmod-rune-inspector-functional-en_us-1024x800-preview.png
run/client/screenshots/mathmod-rune-inspector-functional-pt_br-1024x800-preview.png
run/client/screenshots/mathmod-rune-inspector-functional-pt_br-640x480-preview.png
```

At 1024x800, the Checked and Graph meanings extend beyond the gold panel
boundary and are cut by the window. At 640x480, the narrow details column cuts
the semantic headings and functional rows even more aggressively. The compact
capture therefore does not retain the readable scroll region or show the
complete three-layer meaning required by `docs/UI_PREVIEWS.md`.

The runtime harness still passes because it checks focus and narration, not
pixel/text containment.

**Required correction:** use one bounded layout calculation for the selector,
wrapped semantic heading and visible rows. The heading height must derive from
the exact wrapped lines rendered within the details width. No semantic label
or row may cross the panel/window boundary at either required viewport. Add an
executable containment oracle based on the same layout/font measurements, then
regenerate and visually inspect all three captures.

## 4. Reproduced commands

### Focused tests

The exact clean, no-build-cache seven-class command completed successfully:

```text
BUILD SUCCESSFUL
```

Focused source counts:

```text
ScopedFunctionalProjectionTest             7
ScopedFunctionalProjectionWireCodecTest    2
RuneProgrammerProjectionTest               1
ProgramInspectorPresentationTest           4
RuneInspectorScreenSourceTest              4
UiPreviewMatrixTest                        5
ServerSideIsolationTest                    2
Total                                     25
```

### GameTests

The required GameTest server completed normally:

```text
33 tests are now running
33 GAME TESTS COMPLETE
All 33 required tests passed
BUILD SUCCESSFUL
```

Counts:

```text
L0 functional projection GameTests: 5
L0 persistence GameTests:          14
global GameTests:                  33
```

### Build

The required build completed successfully:

```text
BUILD SUCCESSFUL
```

It reported `test FROM-CACHE`; the clean focused command above is the ordinary
test evidence. Green execution does not make the captured overflow acceptable.

## 5. Ownership and next handoff

`L0-TM-04F4` is limited to closing F4R1 using the already authorized Inspector,
preview, locale/test and handoff files. No new Java file is authorized.

The next handoff is:

```text
docs/handoffs/L0_TM_04F4_HANDOFF.md
```

All forbidden boundaries remain unchanged: no `ProgramGraph`,
`GuidedWorkspaceState`, Data Component/schema, `ProgramStorage`, networking,
source transport, persistence/migration, mutable functional editing or public
extension API change.

## 6. Exit conditions

Acceptance requires:

1. bounded wrapped semantic headings and rows at both required viewports;
2. an executable containment oracle using the production layout measurements;
3. visually readable EN 1024x800, PT-BR 1024x800 and PT-BR 640x480 captures;
4. the exact focused command, GameTests and build reproduced successfully;
5. the delta confined to existing authorized files.
