# P12-TM-01 / P12-TM-01F Final Gate Acceptance

**Date:** 2026-07-30  
**Reviewer:** Sol  
**Decision:** `ACCEPT`

## Scope reviewed

Sol reviewed the repository handoffs, the real production/test delta and the
compiled/runtime evidence for:

- `docs/handoffs/P12_TM_01_HANDOFF.md`;
- `docs/P12_TM_01_BLOCKER_REVIEW.md`;
- `docs/handoffs/P12_TM_01F_HANDOFF.md`;
- `src/main/java/com/mathmod/program/ConstructFlightManager.java`;
- `src/main/java/com/mathmod/program/P8GameTests.java`;
- `src/test/java/com/mathmod/program/P8AuthoritySurfaceTest.java`.

The correction remains inside the ownership frozen by the blocker review. It
does not change networking, schemas, Data Components, client/UI, content,
configuration or public APIs.

## Findings closed

1. Every velocity component is required to be finite before item resolution,
   physical-snapshot capture, payment or active-flight insertion. NaN, zero and
   over-limit motion reject without consumption or flight creation.
2. Every chunk intersected by the collision-radius-inflated swept AABB is
   checked with the server's loaded-chunk authority before block clipping,
   entity lookup, particles or position advance. An unavailable chunk discards
   the flight without loading/ticketing it or mutating terrain.
3. All ten named P8 GameTests pass. They cover rollback/escrow, default
   admission, unloaded/fluid/block-entity/protection rejection, collision,
   unloaded flight, one-flight-per-owner and server-derived motion/body cost.
4. `P8AuthoritySurfaceTest` supplies two compiled-class structural checks:
   the five registered server payload records expose none of the forbidden P8
   authority fields/types, and the server fill/planner/launch descriptors retain
   server-owned region, body and payment authority.
5. The correction makes no claim that GameTest substitutes for the later real
   dedicated-server gate. P8 remains `experimental`.

## Reproduced evidence

With `GRADLE_USER_HOME=C:\codex-gradle-a0`, Sol reproduced:

- focused ordinary suite: 7 classes, 16 tests, 0 failures, 0 errors, 0 skipped;
- `runGameTestServer --no-daemon`: all 50 required GameTests passed;
- exact GameTest partition: P8 10, P9 2, P10 2, P11 2, P13 2, L0 29,
  A0 compatibility 3; global 50;
- `build --no-daemon`: `BUILD SUCCESSFUL`.

The focused classes were:

- `P8AuthoritySurfaceTest` (2);
- `RegionCandidatePlannerTest` (4);
- `ConstructBodyTest` (2);
- `CapturedConstructPhysicsTest` (1);
- `ProgramRegionPipelineTest` (3);
- `ConstructiveRegionRuneTest` (2);
- `ServerSideIsolationTest` (2).

## Gate decision

- `P12-TM-01`: `DONE` with `ACCEPT`;
- `P12-TM-01F`: `DONE` with `ACCEPT`;
- automated GT-01 through GT-04 are closed for the current non-destructive P8
  surface;
- `P12-TM-02` is the only downstream task that may become `READY`;
- `P12-DS`, `P12-M`, P14/P15 expansion and external authoring loaders remain
  blocked.

## Frozen readiness for P12-TM-02

**Owner:** Terra Medium  
**Purpose:** close only the remaining automated GT-05, GT-06 and GT-07 variants
from `docs/P12_SURVIVAL_READINESS_CONTRACT.md`.

### Writable files

```text
src/main/java/com/mathmod/program/P9GameTests.java
src/main/java/com/mathmod/acquisition/P10GameTests.java
src/main/java/com/mathmod/physics/P11GameTests.java
docs/handoffs/P12_TM_02_HANDOFF.md
```

All production Java is read-only. If a required scenario exposes a production
defect or cannot be observed through these fixtures, Terra Medium must stop,
record the smallest reproducible counterexample and request a Sol ownership
decision. A passing substitute or a reclassification as optional is forbidden.

### Required observations

- GT-05: self cast, anchor cast, missing resource, dead target and repeated
  defensive cast; failed paths mutate neither effect nor escrow and refresh is
  bounded.
- GT-06: enabled/disabled loot, profession, trades and house combinations plus
  marked-offer reconciliation; flags remain independent, valid marked state and
  uses/prices survive, rejected marked offers alone change, and unmarked/open
  offers remain untouched.
- GT-07: publication/reload with an already captured flight, collision and
  unloaded chunk; the active flight keeps its captured profile/version, a
  future flight uses the replacement snapshot, and neither path mutates terrain
  or loads/tickets the unavailable chunk.

The handoff must name every added or reused GameTest, report P9/P10/P11 counts
separately from the new global count, and reproduce the focused relevant
ordinary suites, `runGameTestServer --no-daemon` and `build --no-daemon` under
the prescribed Gradle environment.

