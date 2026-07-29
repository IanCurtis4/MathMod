# L0-TM-04F — Gate Re-review

**Date:** 2026-07-29  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Reviewed handoff:** `docs/handoffs/L0_TM_04F_HANDOFF.md`

## 1. Result

The correction materially improves the implementation and closes parts of
L0-04-R1 through L0-04-R7. The focused tests, 33 GameTests and build are green.
The gate is nevertheless not accepted because the handoff overstates several
contractual properties that the real delta and evidence do not prove.

`L0-TM-04` and `L0-TM-04F` remain `NEEDS_FIX`. The next bounded correction is
`L0-TM-04F2`. All downstream L0 tasks remain blocked.

## 2. Conforming delta

Repository inspection confirms:

- the authorized `L0FunctionalProjectionGameTests.java` exists with exactly
  five `@GameTest` methods;
- the menu-opening buffer is still the only projection transport;
- no `ModNetworking`, C2S, source component StreamCodec or source-envelope
  transport was added;
- PRESETS/CUSTOM now receive `graphOnly()` rather than the held item's source
  snapshot;
- common projection files were added to dedicated-server isolation checks;
- the functional preview now enters through an integrated-server Programmer
  menu;
- the three claimed preview PNGs exist at their claimed dimensions;
- closed functional enums have EN/PT-BR translation keys.

These improvements do not override the findings below.

## 3. Remaining findings

### L0-04-F2R1 — The DTO still accepts impossible source/row combinations

The handoff claims that DTO combinations are inseparable. The constructor only
forbids authored/checked rows for `STALE`; it still accepts:

- `ABSENT`, `CURRENT_UNREADABLE`, `UNSUPPORTED_VERSION` or `CONFLICT` with
  authored rows;
- checked rows with `NOT_RUN`, language/admission rejection or a non-current
  source;
- `MATCH`/`MISMATCH` without `SUCCESS` and a present graph;
- `SUCCESS` without authored rows;
- diagnostics unrelated to the declared state.

The exact 65,536-byte GameTest demonstrates the defect: its supposedly valid
fixture uses `SourceState.ABSENT` with 64 authored rows. That projection cannot
exist under the frozen model.

**Required correction:** enforce the logical state matrix in
`ScopedFunctionalProjection` and add rejection tests for every invalid
combination. Rebuild the 65,536-byte fixture from a semantically valid state,
for example a current-valid language rejection with authored rows or a
current-valid success with both authored and checked rows.

### L0-04-F2R2 — Compile cardinality is alleged but not measured

`projectionReadCompileMatrixMutatesNothing` verifies read classifications and
no item mutation. For a current-valid source it only checks that
`attemptState != NOT_RUN`. It does not prove:

- exactly one compile for current-valid source;
- zero compile calls for absent, unreadable, future and conflict;
- the expected successful/rejected compile outcome.

The handoff's statement that the matrix proves the one/zero compile rule is
therefore unsupported.

**Required correction:** use a package-private counting seam around the exact
production projection build path, or equivalent repository evidence that
executes the same branch. Assert zero for every non-current state and exactly
one for current-valid. Do not add a second production compiler or change the
accepted compile service.

### L0-04-F2R3 — Menu binding evidence covers only a trivial item replacement

`projectionMenuBindingInvalidatesAfterTargetChange` replaces the programmed
talisman with a stick and then calls the server menu accessor. It does not
exercise:

- an in-place component change on the same talisman;
- an existing successful Programmer save/clear mutation;
- the `DataSlot` value transition used by the client menu;
- the client-side withholding of rows after that transition.

The production comparison can detect same-item component changes, but the
required server/client menu-binding evidence is absent and the handoff claims
more than the test executes.

**Required correction:** add same-talisman component mutation and one real
Programmer mutation vector. Exercise the synchronized validity value or the
closest transformed-runtime menu synchronization seam, and prove that the
client-facing projection becomes stale with no functional rows.

### L0-04-F2R4 — Stale projection falsely declares the compiled graph absent

`ScopedFunctionalProjection.unavailable()` always returns
`GraphState.ABSENT`. `RuneProgrammerMenu.functionalProjection()` uses that
value whenever a previously bound snapshot becomes stale.

The Inspector can still be rendering a valid authoritative graph from the
current `ProgramSurface`, so its functional graph summary then says the graph
is absent while the graph canvas is visibly present. This violates the rule
that stale source must not obscure or contradict graph authority.

**Required correction:** preserve/derive the graph-presence state when
invalidating a snapshot. Stale must remove authored/checked rows and relation
claims, but it must not state `ABSENT` when the Inspector is displaying a
compiled graph. Add match/mismatch/stale-with-graph tests.

### L0-04-F2R5 — Keyboard and pointer focus evidence remains insufficient

The implementation adds internal panel/row state, but:

- Tab is intercepted solely to rotate Authored/Checked/Graph and never reaches
  the existing Close button;
- the three panels and rows are not real focusable GUI children;
- `functionalPanelAt` uses fixed 44/44/24-pixel bands even though each section
  height depends on whether it contains zero, one, two or three visible rows;
  with the two-row preview fixture, clicking the Checked heading is classified
  as Authored;
- no executable test or preview interaction exercises Tab, Shift+Tab,
  functional arrow navigation, scroll bounds, selected-row narration or
  Escape-to-parent.

Source-text assertions and resting screenshots do not prove the required
keyboard/narrator behavior.

**Required correction:** derive hit regions from the same layout geometry used
for rendering; keep the Close control reachable in deterministic focus order;
and add real harness evidence for forward/backward panel focus, row navigation,
scroll bounds, narration content and Escape return in standard and compact
layouts.

### L0-04-F2R6 — Preview evidence contradicts the handoff's PT-BR claim

All three PNG files exist and the route is server-backed. Visual inspection of
both PT-BR captures shows the fixed 48-pixel Close button rendered as
`Fech...`, not `Fechar`. The handoff explicitly states that both PT-BR captures
display `Fechar`.

The captures also show only one resting panel state; they do not supply the
keyboard/narrator evidence required by L0-SOL-05.

**Required correction:** make the localized close action readable at
1024x800 and 640x480, regenerate both PT-BR captures, and report separate
interaction evidence rather than treating a resting PNG as keyboard/narrator
proof.

## 4. Reproduced commands and counts

### Focused tests

The exact seven-class command completed successfully:

```text
BUILD SUCCESSFUL
```

The declared class counts are accurate: 4 + 2 + 1 + 4 + 4 + 5 + 2 = 22.
Green execution does not cover F2R1-F2R6.

### GameTests

The required command completed normally in this review:

```text
33 tests are now running
33 GAME TESTS COMPLETE
All 33 required tests passed
BUILD SUCCESSFUL
```

Counts:

```text
L0 functional projection GameTests: 5
pre-existing L0 persistence GameTests: 14
global executed GameTests: 33
```

### Build

The required build completed successfully:

```text
BUILD SUCCESSFUL
```

The build reported `test FROM-CACHE`; the focused no-build-cache command above
is the applicable ordinary-test evidence.

## 5. Ownership and next handoff

`L0-TM-04F2` retains the exact amended ownership from:

- `docs/L0_READONLY_FUNCTIONAL_PROJECTION_READINESS.md`;
- `docs/L0_TM_04_GATE_REVIEW.md`.

No new file or boundary is authorized. The existing authorized GameTest file
may be corrected. The next handoff is:

```text
docs/handoffs/L0_TM_04F2_HANDOFF.md
```

Forbidden boundaries remain unchanged: no `ProgramGraph`, Guided schema,
`ProgramStorage`, Data Component, networking, source schema, mutable functional
editing, persistence, migration or public extension API change.

## 6. Exit conditions

Acceptance requires:

1. individual repository evidence closing L0-04-F2R1 through F2R6;
2. semantically valid 65,536/65,537 vectors;
3. measured zero/one compile cardinality;
4. same-item and real-menu binding invalidation evidence;
5. stale-with-graph authority consistency;
6. executable keyboard/narrator/compact evidence;
7. exact focused, 33-or-updated GameTest total and build reproduction;
8. real delta confined to ownership.

