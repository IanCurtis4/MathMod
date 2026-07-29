# L0-TM-04 — Gate Review

**Date:** 2026-07-28  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Reviewed handoff:** `docs/handoffs/L0_TM_04_HANDOFF.md`

## 1. Result

The handoff correctly declines to request acceptance. A dedicated
Minecraft-runtime GameTest is necessary, and this review grants the exact
authorization below. The missing GameTest is not the only blocker: the real
delta does not yet satisfy the frozen UI/accessibility, preview, snapshot
binding or evidence contract.

`L0-TM-04` remains `NEEDS_FIX`. The bounded correction is `L0-TM-04F`.
`L0-LU-01`, `L0-TM-05` and every later L0 task remain blocked.

## 2. Repository evidence reviewed

The review inspected the handoff and the actual production/test delta,
including:

```text
src/main/java/com/mathmod/program/ScopedFunctionalProjection.java
src/main/java/com/mathmod/program/ScopedFunctionalProjectionService.java
src/main/java/com/mathmod/program/ScopedFunctionalProjectionWireCodec.java
src/main/java/com/mathmod/item/ProgrammedTalismanItem.java
src/main/java/com/mathmod/screen/RuneProgrammerMenu.java
src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java
src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/main/java/com/mathmod/client/UiPreviewMatrix.java
src/test/java/com/mathmod/program/ScopedFunctionalProjectionTest.java
src/test/java/com/mathmod/program/ScopedFunctionalProjectionWireCodecTest.java
src/test/java/com/mathmod/screen/RuneProgrammerProjectionTest.java
src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java
src/test/java/com/mathmod/client/UiPreviewMatrixTest.java
src/test/java/com/mathmod/ServerSideIsolationTest.java
```

The implementation stays outside `ModNetworking`, does not synchronize the
source Data Component and introduces no C2S source/compile request. Those
boundaries are conforming.

## 3. Is the GameTest necessary?

Yes.

The projection frame uses `RegistryFriendlyByteBuf`, `FriendlyByteBuf`, Netty
buffers, the real menu constructor, `ServerPlayer`, registered items,
components and live reload/knowledge authorities. The ordinary JUnit runtime
does not load the complete transformed Minecraft runtime for those paths. The
current ordinary tests consequently assert DTO constants and source text but
do not execute:

- a real menu-buffer round trip;
- the exact 65,536/65,537 boundary;
- malformed, truncated and trailing frame rejection;
- the live server read/compile state matrix;
- final target/knowledge/generation/definition/material rechecks;
- exact before/after component equality.

These are acceptance requirements, not optional duplication of unit tests.
They must execute under `runGameTestServer`.

### 3.1 Exact authorization

`L0-TM-04F` may create exactly:

```text
src/main/java/com/mathmod/program/L0FunctionalProjectionGameTests.java
```

It may use the existing generated `empty` template and the existing
`mathmod` GameTest namespace. It may not modify
`L0ScopedSourcePersistenceGameTests.java`, GameTest registration, generated
structures, networking, components or schemas.

The class must contain separately named vectors for at least:

```text
projectionMenuCodecRoundTripAndBounds
projectionMalformedFramesFailClosed
projectionReadCompileMatrixMutatesNothing
projectionAuthorityRechecksBecomeStale
projectionMenuBindingInvalidatesAfterTargetChange
```

The handoff must report these names and the new L0 projection count separately
from both the existing 14 persistence GameTests and the global runtime total.
Counting `@GameTestHolder` as a test is forbidden.

## 4. Blocking findings

### L0-04-R1 — GameTest/runtime evidence is absent

The three newly authorized JUnit classes contain five tests in total, but they
do not execute the real wire codec or server assembler:

- `ScopedFunctionalProjectionWireCodecTest` only checks constants/DTO values;
- `RuneProgrammerProjectionTest` constructs only a DTO and never constructs or
  round-trips a menu;
- no test calls `ScopedFunctionalProjectionService.openingSnapshot`.

**Required correction:** implement the exact GameTest class authorized in
section 3.1 and cover all frozen runtime vectors. Ordinary tests must still
cover constructor invariants and pure presentation behavior where possible.

### L0-04-R2 — Required functional navigation and narration do not exist

`RuneInspectorScreen.renderFunctionalProjection` draws headings and at most
three rows from each list. There are no panel selectors, functional-row focus,
functional scrolling, Tab/Shift+Tab sequence, arrow-key functional navigation
or selected-row narration. `keyPressed` continues to move graph-node selection
only.

`functionalNarration` is hard-coded English and reports enum names rather than
localized panel/state/diagnostic/row narration. Rendered graph states,
diagnostic codes and row kinds also use raw enum names.

**Required correction:** implement the exact keyboard/narrator contract from
L0-SOL-05, including deterministic panel/row focus, bounded scrolling, visible
focus, localized states and per-row structural narration in standard and
compact layouts. Raw enum names may not be player copy.

### L0-04-R3 — The functional preview is not functional or server-backed

`UiPreviewHarness` routes both `rune-inspector` and
`rune-inspector-functional` through the two-argument `RuneInspectorScreen`
constructor. That constructor supplies `ScopedFunctionalProjection.unavailable`.
The advertised functional capture therefore contains no authored/checked
server projection and does not exercise the menu-opening transport.

`docs/UI_PREVIEWS.md` calls this a read-only server projection, which the
harness does not produce.

**Required correction:** the functional preview must open the real
server-backed Rune Programmer path with a persisted current-valid functional
source and then enter the Inspector. It must prove the three panels, canonical
`#index`, graph authority and one required mismatch or failure state. Add
matrix assertions for EN/PT-BR `1024x800` and PT-BR `640x480`, and report actual
capture ids/results.

### L0-04-R4 — Graph-only and non-saved routes receive a false stale snapshot

The existing two-argument `RuneInspectorScreen` constructor injects
`ScopedFunctionalProjection.unavailable`, whose source/attempt state is
`STALE/AUTHORITY_STALE`. Graph-only theorem/manuscript inspection therefore no
longer preserves its previous behavior: it displays a false functional stale
condition.

`RuneProgrammerScreen.openInspector` also passes the held item's menu-opening
functional snapshot for `SAVED`, `PRESETS` and `CUSTOM`. For the latter two the
displayed graph is not the target from which the functional snapshot was
captured.

**Required correction:** graph-only routes use an explicit
`ABSENT/NOT_RUN/PRESENT/NOT_COMPARABLE` presentation or hide unavailable
functional panels without claiming stale. Only `SAVED` may consume the held
item's functional snapshot.

### L0-04-R5 — The opening snapshot can outlive its target binding

The menu captures the projection once when it opens. Existing Programmer
actions can subsequently save, clear or otherwise synchronize a different held
item state while the same menu remains open. The menu then continues to expose
the old projection, and `openInspector` can pair it with the new graph.

This violates the frozen source/result/graph inseparability even though the
pre-encode rechecks passed at menu-open time.

**Required correction:** add a server-owned menu validity flag for the captured
projection, synchronized through ordinary menu data (not a custom payload).
The server must invalidate it when the held target/component state differs
from the captured snapshot or after any Programmer mutation. The client must
withhold authored/checked rows and report `STALE` once invalid. Prove the
transition in the dedicated GameTest.

No new source transport, source component synchronization or retry is
authorized.

### L0-04-R6 — DTO/codec invariants and player tokens are under-specified in code

The DTO constructor does not reject null state enums or null row kinds and does
not enforce basic logical combinations such as `SUCCESS` requiring
`CURRENT_VALID`. The checked lambda token uses Java `toString()` for its type
instead of a frozen semantic presentation token. These values cross the menu
wire and feed player presentation.

**Required correction:** fail closed on null/invalid logical combinations and
emit deterministic bounded semantic type tokens. Do not transmit Java record
`toString()` output or raw exception messages. Add ordinary tests for every
constructor invariant and GameTests for codec rejection.

### L0-04-R7 — Dedicated-server isolation evidence does not include the new authorities

`ServerSideIsolationTest` was part of required ownership, but its common
authority inventory does not include the three new projection production
classes. The reported two passing isolation tests therefore do not prove the
new service/codec are free of client imports.

**Required correction:** include the new common projection classes in the
isolation inventory and retain the two passing dedicated-server checks.

## 5. Reproduced commands

### Focused command

The exact seven-class focused command exited successfully:

```text
BUILD SUCCESSFUL
```

This is consistent with the handoff's declared 18 tests, but the current new
tests do not cover the runtime or UI behaviors listed above.

### GameTest server

The required command exited successfully:

```text
28 tests are now running
All 28 required tests passed
BUILD SUCCESSFUL
```

No L0-TM-04 GameTest exists in that total. The existing
`L0ScopedSourcePersistenceGameTests` file contains 14 `@GameTest` methods.

### Build

The required build exited successfully:

```text
BUILD SUCCESSFUL
```

The build used cached/up-to-date work, including `test FROM-CACHE`. A green
build does not close R1-R7.

## 6. L0-TM-04F ownership

The correction retains every existing/new production, test, locale, preview
and handoff file assigned by
`docs/L0_READONLY_FUNCTIONAL_PROJECTION_READINESS.md`, plus only the new
GameTest file authorized in section 3.1.

The correction handoff is:

```text
docs/handoffs/L0_TM_04F_HANDOFF.md
```

Forbidden boundaries from L0-SOL-05 remain unchanged. In particular:

```text
ProgramGraph production changes
GuidedWorkspaceState or schema changes
ProgramStorage or ModDataComponents changes
network package or ModNetworking changes
CustomPacketPayload or source StreamCodec
mutable functional editing or inscription
public extension API
```

## 7. Exit conditions

Sol may accept L0-TM-04/F only when:

1. L0-04-R1 through L0-04-R7 are individually mapped to repository evidence;
2. the dedicated GameTest names/counts and global runtime total are reported
   accurately;
3. the exact focused command, `runGameTestServer --no-daemon` and `build` are
   reproduced successfully;
4. real EN/PT-BR standard/compact functional preview evidence exists;
5. the delta remains inside the amended ownership;
6. no downstream task has started.

