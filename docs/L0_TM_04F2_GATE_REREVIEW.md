# L0-TM-04F2 — Gate Re-review

**Date:** 2026-07-29  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Reviewed handoff:** `docs/handoffs/L0_TM_04F2_HANDOFF.md`

## 1. Result

The F2 delta closes the implementation defects in F2R1 through F2R4 and
materially improves the functional Inspector interaction path. The exact
focused command, all 33 GameTests and the build are green.

The gate is not accepted. The real captures contradict the required readable
functional presentation, the runtime interaction audit is weaker than the
properties claimed by the handoff, and two mandatory authority/codec vectors
still lack executable evidence.

`L0-TM-04`, `L0-TM-04F` and `L0-TM-04F2` remain `NEEDS_FIX`. The next bounded
correction is `L0-TM-04F3`. All downstream L0 tasks remain blocked.

## 2. Findings closed by F2

Repository inspection confirms:

- **F2R1 closed:** the DTO now rejects the impossible source/attempt/row,
  relation and diagnostic combinations identified by the previous review;
- the 65,536-byte fixture is now a semantically valid
  `CURRENT_VALID/LANGUAGE_REJECTED` projection;
- **F2R2 closed:** the package-private seam is immediately before the same
  production compiler invocation, and the GameTest measures zero calls for
  non-current states and one for `CURRENT_VALID`;
- **F2R3 closed:** the GameTest covers same-stack component mutation, the
  receiving menu's synchronized validity value and the real Programmer clear
  branch;
- **F2R4 closed:** stale invalidation preserves known graph presence while
  removing rows and relation claims;
- `Fechar` is fully visible in both regenerated PT-BR captures;
- the F2 files changed after the preceding gate are all inside the amended
  ownership;
- no F2 change touched `ProgramGraph`, Guided/source schemas, Data Component
  identities, `ProgramStorage`, networking, a public extension API or
  persistence/migration behavior.

These closures do not waive the remaining findings below.

## 3. Remaining findings

### L0-04-F3R1 — The real selector geometry obscures functional content

`RuneInspectorScreen.init` places each functional selector at the same
vertical origin as the rendered heading. `functionalPanelButton` creates a
fixed 20-pixel `MathButton`, while `drawProjectionHeading` advances only 11
pixels before rendering the first functional row.

Consequently every selector is drawn over its heading and first content row.
The graph selector also covers the graph-state/relation line. This is visible
in all three repository captures:

```text
run/client/screenshots/mathmod-rune-inspector-functional-en_us-1024x800-preview.png
run/client/screenshots/mathmod-rune-inspector-functional-pt_br-1024x800-preview.png
run/client/screenshots/mathmod-rune-inspector-functional-pt_br-640x480-preview.png
```

The selector labels are also ellipsized in the captures. EN truncates all three
semantic labels. PT-BR truncates Checked and Graph at 1024x800 and all three at
640x480. The captures therefore do not show the complete three-layer meaning
required by `docs/UI_PREVIEWS.md`, and the compact capture does not retain a
readable functional scroll region.

**Required correction:** allocate selector controls their own layout rows and
render functional rows/state below them using one shared geometry model.
Standard and compact captures must keep the authored, checked and graph
meaning readable without relying on truncated button copy. Regenerate all
three captures and inspect the pixels, not only the widget labels.

### L0-04-F3R2 — The runtime interaction audit does not prove its claims

`auditFunctionalInspectorInteraction` is executable, but its assertions cover
only a subset of the frozen contract:

- it stops after Graph then Shift+Tab to Checked and never proves the complete
  forward/backward cycle through Close;
- repeated Down presses only assert that narration still contains the Authored
  panel label;
- production row movement uses `Math.floorMod`, so it wraps from the last row
  to the first while the handoff describes bounded navigation;
- it does not assert the selected row's structural path, kind and displayed
  value;
- it does not verify focus visibility or readable scroll content;
- it does not exercise explicit narration for mismatch, conflict, unreadable,
  unsupported, stale and graph-absent states.

The harness therefore passes while the visual overlap in F3R1 is present.

**Required correction:** make the interaction oracle assert the actual focused
control, the complete forward and backward order including Close, deterministic
row boundary behavior, selected-row identity and scroll position. Add
executable narration vectors for every frozen failure/authority state and
assert the complete semantic content, not just a panel-name substring.

### L0-04-F3R3 — Live opening-snapshot authority ordering is not exercised

`projectionAuthorityRechecksBecomeStale` constructs
`AuthoritySnapshot` values directly and invokes `acceptCandidate`. No test
calls the package-private `openingSnapshot(..., beforeFinalRecheck)` seam.

The production code visibly captures `KnowledgeService.get(player)`, builds
the candidate, executes the hook and then obtains live authorities again, but
the mandatory evidence requires the live player-knowledge capture and
post-build recheck path itself. The synthetic lower-level call does not prove
that the menu-opening path consults live knowledge or that the candidate is
complete before its final rechecks.

**Required correction:** exercise the real `openingSnapshot` path with a mock
server player and the existing post-build hook. Prove live player knowledge is
captured, a post-build live change produces stale, no retry occurs and no
functional rows survive.

### L0-04-F3R4 — Encode-overflow fallback has no executable vector

The exact 65,536-byte encode and receiver-side 65,537 length rejection are now
valid. They do not exercise encode overflow.

`ScopedFunctionalProjectionWireCodec.write` throws after its temporary bounded
encode overflows; `ProgrammedTalismanItem.openProgrammer` catches that exception
and writes `unavailable()`. This is a plausible fail-closed production path,
but no focused test or GameTest sends a valid over-limit DTO through that
writer and proves:

- no partial frame was copied;
- exactly one minimal stale frame was emitted;
- the receiving menu decoded only that fallback.

This vector is mandatory in section 8.1 of
`docs/L0_READONLY_FUNCTIONAL_PROJECTION_READINESS.md`.

**Required correction:** add an executable over-limit writer vector through
the same production helper/path, then decode the resulting menu data and
assert one bounded stale projection with no truncated rows or trailing bytes.
Do not add a new transport or public API.

### L0-04-F3R5 — The handoff omits required focused counts and overstates UI evidence

The handoff reports the seven-class command but not each class's count. The
repository counts are:

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

It also states that the harness proves bounded scrolling and that the captures
retain the required presentation, neither of which follows from the current
oracles and pixels.

**Required correction:** report the exact per-class counts and distinguish
runtime assertions, static source tests and visual inspection. Do not claim a
property that the cited evidence does not assert.

## 4. Reproduced commands

### Focused tests

The exact clean, no-build-cache seven-class command completed successfully:

```text
BUILD SUCCESSFUL
```

The executed source count is 25 focused `@Test` methods:

```text
7 + 2 + 1 + 4 + 4 + 5 + 2 = 25
```

### GameTests

The required GameTest server completed normally:

```text
33 tests are now running
33 GAME TESTS COMPLETE
All 33 required tests passed
BUILD SUCCESSFUL
```

Repository and runtime counts:

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
test evidence. Green execution does not close F3R1 through F3R5.

## 5. Ownership and next handoff

`L0-TM-04F3` retains exactly the amended ownership from:

- `docs/L0_READONLY_FUNCTIONAL_PROJECTION_READINESS.md`;
- `docs/L0_TM_04_GATE_REVIEW.md`;
- `docs/L0_TM_04F_GATE_REREVIEW.md`.

No new Java file or boundary is authorized. Existing authorized production,
test, GameTest, locale, preview and handoff files may be corrected.

The next handoff is:

```text
docs/handoffs/L0_TM_04F3_HANDOFF.md
```

Forbidden boundaries remain unchanged: no `ProgramGraph`,
`GuidedWorkspaceState`, Data Component identity/schema, `ProgramStorage`,
networking, client-to-server source request, mutable functional editing,
persistence/migration or public API change.

## 6. Exit conditions

Acceptance requires:

1. individual repository evidence closing F3R1 through F3R5;
2. readable, non-overlapping EN/PT-BR standard and compact captures;
3. complete executable focus, row-boundary and narrator assertions;
4. live `openingSnapshot` player-knowledge/recheck evidence;
5. actual encode-overflow fallback evidence without partial output;
6. exact focused counts, GameTest names/counts and global total;
7. successful reproduction of the focused command, GameTests and build;
8. delta confined to the existing amended ownership.
