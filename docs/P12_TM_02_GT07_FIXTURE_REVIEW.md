# P12-TM-02 GT-06/GT-07 Gate Review

**Date:** 2026-07-30  
**Reviewer:** Sol  
**Decision:** GT-07 fixture blocker is legitimate; one exact test-only class is
authorized. P12-TM-02 remains `NEEDS_FIX`.

**Authority clarification:** the direct `PhysicalProfiles` publication step
below is superseded by
`docs/P12_TM_02_GT07_RELOAD_AUTHORITY_CLARIFICATION.md`, which requires the
real public asynchronous server reload and no visibility change or bridge.

## Evidence reviewed

Sol reviewed the updated `docs/handoffs/P12_TM_02_HANDOFF.md`, the real deltas
in `P9GameTests.java` and `P10GameTests.java`, the P9/P10/P11 contracts, the
existing `P11GameTests.java`, and the package-private flight authority in
`com.mathmod.program`.

Sol also reproduced:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; all 54 required GameTests passed.

The green global count does not close absent contractual observations.

## Interim findings

### GT-05 — closed

The four named P9 GameTests collectively exercise the required self cast,
anchor rejection, missing resource, dead target and bounded repeated cast. The
successful self-player path uses only NeoForge's authorized
`configureMockConnection`, retains the real `ServerPlayer` and real
`ProgramExecutor` path, and observes exact payment/restoration. GT-05 is
accepted as complete for the automated gate.

### GT-06 — two residual gaps

The feature-flag matrix and closed-villager reconciliation test pass and are
useful, but the claim that GT-06 is complete is premature:

1. `P12_TM_01_FINAL_GATE_ACCEPTANCE.md` and the P10 contract require an open
   merchant menu to remain untouched. The current test never sets a trading
   player and therefore exercises only the closed-menu branch.
2. The retained marked offer has default price state. Identity, uses, maximum
   uses and base cost are checked, but non-default special-price and demand
   state are not established and then verified unchanged.

Terra Medium must close both points in
`src/main/java/com/mathmod/acquisition/P10GameTests.java`. The open-menu vector
must place valid, rejected-marked and unmarked offers in a villager with a
non-null trading player, invoke the real reconciler, and prove the exact offer
list/order/instances and mutable offer state are unchanged. The closed-menu
vector must establish non-default uses, special-price and demand state on the
valid marked offer before reconciliation and verify each value afterward.

No P10 production change is authorized.

## GT-07 authority decision

The blocker is real:

- `ConstructFlightManager.launch`, `tickServer`, `activeSnapshotVersion`,
  `activeFlightCount` and `clearForTests` are package-private in
  `com.mathmod.program`;
- `ConstructBody.materialize` and `VoxelCoordinate` are also package-private;
- `P11GameTests` is in `com.mathmod.physics`;
- reflection or widening production visibility would bypass the intended
  authority boundary.

Sol rejects both a production facade and adding P11 evidence to
`P8GameTests`, because either would blur ownership or subsystem counts.

Terra Medium is authorized to create exactly:

```text
src/main/java/com/mathmod/program/P11CapturedFlightGameTests.java
```

This is a GameTest-only holder in the package that already owns the
package-private flight fixture authority. It may call the existing
package-private methods but may not introduce helpers used by production or
change any existing production class.

### Required named observations

The new holder must provide three independently named tests:

1. `capturedFlightRetainsProfileVersionAcrossRealReload`
   - launch a funded flight under snapshot version N;
   - execute the real asynchronous server resource reload as frozen by the
     authority clarification;
   - prove the active flight still reports N;
   - launch a future flight for a different owner and prove it reports N+1.
2. `capturedProfileFlightStopsOnCollisionWithoutTerrainMutation`
   - launch a flight with an observed captured version;
   - tick through the real flight manager into a collision;
   - prove discard, unchanged terrain and no replacement flight/entity.
3. `capturedProfileFlightStopsBeforeUnloadedChunkWithoutTicketOrTerrainMutation`
   - launch at a loaded/unloaded boundary with an observed captured version;
   - tick through the real flight manager;
   - prove discard, the target chunk remains unloaded and loaded terrain is
     byte-for-state unchanged.

Each test must use `try/finally` or equivalent exact cleanup so the static
flight list cannot contaminate another GameTest. Payment must use a
non-creative funded `ServerPlayer`; server-derived `ConstructBody` cost remains
the authority.

The publication test is automated publication evidence, not a substitute for
the later real `/reload` dedicated-server row.

## Writable files for the resumed task

```text
src/main/java/com/mathmod/program/P9GameTests.java
src/main/java/com/mathmod/acquisition/P10GameTests.java
src/main/java/com/mathmod/physics/P11GameTests.java
src/main/java/com/mathmod/program/P11CapturedFlightGameTests.java
docs/handoffs/P12_TM_02_HANDOFF.md
```

`P9GameTests.java` is expected to remain unchanged unless a reproduced
regression requires correction. `P11GameTests.java` need not change merely to
delegate to the new holder.

## Forbidden

- edits to `ConstructFlightManager`, `ConstructBody`, `VoxelRegion`,
  `PhysicalProfiles`, `ProgramExecutor` or any other production authority;
- reflection, widened visibility, public/test production facade or P8 fixture
  reuse;
- networking, schemas, Data Components, client/UI, content or configuration;
- counting an existing P8 collision/unloaded test as the missing P11 captured
  version observation;
- advancing P12-DS or P12-M.

## Final evidence required

The corrected handoff must:

- name every P9, P10 and P11 GameTest separately;
- report subsystem counts and the new global count;
- reproduce the focused P9/P10/P11 ordinary suites;
- reproduce `runGameTestServer --no-daemon`;
- reproduce `build --no-daemon`;
- state the publication-versus-real-reload limitation explicitly.
