# P12-TM-01 — Blocker Review And Correction Authorization

**Date:** 2026-07-30  
**Reviewer:** Sol  
**Reviewed handoff:** `docs/handoffs/P12_TM_01_HANDOFF.md`  
**Decision:** `NEEDS_FIX`  
**Correction task:** `P12-TM-01F` — `READY`

## 1. Decision

The Terra Medium escalation is valid.

`P12-TM-01` remains `NEEDS_FIX`. The seven required GameTests exist, but two
reproduce production defects in the accepted P8 flight boundary:

| Finding | Classification | Reproduced result |
|---|---|---|
| P12-P8-BOUND-01 | `BOUND_FAILURE` | a velocity containing `NaN` is accepted |
| P12-P8-PROTECTION-01 | `PROTECTION_FAILURE` | a flight reaches an unloaded boundary and remains active |

The other eight named P8 GameTests pass. No P8 automated survival gate is
accepted while either finding remains open.

## 2. Repository cause

### P12-P8-BOUND-01

`ConstructFlightManager.launch` currently rejects only:

```text
velocity.length() > 2.0
velocity.lengthSqr() <= EPSILON
```

For a `NaN` component, both comparisons evaluate false. The method then reaches
material resolution, payment and flight creation. This violates the frozen
finite/bounded motion rule.

### P12-P8-PROTECTION-01

`ConstructFlightManager.tick` checks `hasChunkAt` only for the current flight
position. It then computes `next` and performs block clipping, entity queries,
particle work and `advance(next)` without first proving that the complete swept
collision volume stays in already loaded chunks.

The required fixture begins at a loaded boundary with the east neighbor
unloaded. The production flight remains active after two explicit server ticks.
The current order therefore does not enforce the no-unloaded-travel contract
before world queries and advancement.

## 3. Independent reproduction

Sol reproduced:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat runGameTestServer --no-daemon
```

Result:

```text
50 GAME TESTS COMPLETE
48 passed
2 required tests failed

constructrejectsunboundedmotionandusesserverderivedbodycost
constructunloadedflightstopswithoutticketorterrainmutation
```

The failure messages match the handoff:

```text
non-finite, zero, or over-limit motion must reject: (NaN, 0.0, 0.0)
flight approaching unloaded terrain must be discarded
```

## 4. P12-TM-01F exact ownership

Terra Medium may edit only:

```text
src/main/java/com/mathmod/program/ConstructFlightManager.java
src/main/java/com/mathmod/program/P8GameTests.java
src/test/java/com/mathmod/program/P8AuthoritySurfaceTest.java
docs/handoffs/P12_TM_01F_HANDOFF.md
```

`P8AuthoritySurfaceTest.java` may be created. `P8GameTests.java` may be changed
only to correct a fixture or strengthen before/after assertions; the seven
required names and intended semantics may not be weakened.

No other production file is authorized. In particular, networking,
`ProgramExecutor`, `ConstructionFillService`, `RegionCandidatePlanner`, public
APIs, schemas, Data Components, client/UI, content and configuration remain
read-only.

## 5. Required correction semantics

### F-R1 — finite launch preflight

Before material resolution, snapshot capture, payment or flight creation:

- every velocity component must be finite;
- zero and over-limit velocity must retain their current rejection;
- rejected velocity must consume nothing and create no flight.

Do not canonicalize, clamp or replace a non-finite component. Reject it.

### F-R2 — loaded swept-volume preflight

Before `clip`, entity lookup, particles or `advance`:

- compute the movement from current position to `next`;
- include the construct collision radius in the swept volume;
- prove that every chunk intersected by that swept volume is already loaded;
- if any required chunk is unloaded, discard the flight immediately;
- do not create a ticket, load a chunk, query unloaded collision/entity state,
  emit particles into it or mutate terrain.

Checking only the endpoint is insufficient when the collision radius crosses a
chunk boundary.

The correction must preserve:

- maximum velocity and lifetime;
- one active flight per owner;
- server-derived payment from `ConstructBody.massEquivalent`;
- launch-time physical snapshot capture;
- bounded collision target/impulse behavior;
- no terrain mutation;
- no persistence or new entity.

## 6. Required completion evidence

`P12-TM-01F` must complete the work left intentionally unfinished by the
blocked handoff:

1. all ten named P8 GameTests pass;
2. P8 total is 10 and global total is 50;
3. `P8AuthoritySurfaceTest` exists and proves the registered-payload negative
   authority boundary;
4. rejected NaN/zero/over-limit launches prove no payment and no flight;
5. the unloaded sweep proves unchanged loaded state, terrain and active-flight
   state after rejection;
6. the focused command from
   `docs/P12_FOUNDATION_BETA_COMPLETION_CONTRACT.md` passes with exact
   per-class counts;
7. `runGameTestServer --no-daemon` and `build --no-daemon` pass;
8. the handoff reports limitations, escalations and exact changed files.

Build success alone is not acceptance.

```text
P12-TM-01 NEEDS_FIX
    -> P12-TM-01F READY

P12-TM-02 BLOCKED
P12-DS BLOCKED
P12-M BLOCKED
P14/P15 BLOCKED
```
