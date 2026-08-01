# P12 Foundation Beta Completion Contract

**Task:** `P12-SOL-01` — Foundation Beta Completion Sequencing  
**Date:** 2026-07-30  
**Owner:** Sol  
**Decision:** `ACCEPT`  
**Immediately unblocks:** `P12-TM-01` — P8 Survival Boundary Evidence Closure

**Gate update (2026-07-30):** `P12-TM-01` and `P12-TM-01F` are `DONE` with
`ACCEPT` under `docs/P12_TM_01_FINAL_GATE_ACCEPTANCE.md`. `P12-TM-02` is
`DONE` with `ACCEPT` under `docs/P12_TM_02_FINAL_GATE_ACCEPTANCE.md`.
`P12-SOL-02` is `DONE` with `ACCEPT` under
`docs/P12_DEDICATED_SERVER_FIXTURE_READINESS.md`. `P12-FX-01` is
blocked on external proof after bounded correction `P12-FX-01F` became `DONE`
with `ACCEPT` under `docs/P12_FX_01F_GATE_ACCEPTANCE.md`. `P12-DS` remains
blocked until the complete Sol/operator proof is accepted.

## 1. Product decision

MathMod must consolidate the current product before opening another authoring
API, mutable editor or destructive gameplay family.

The repository priority order is:

1. close P12 automated authority, atomicity, protection and snapshot evidence;
2. execute the real dedicated-server/reload/reconnect matrix;
3. execute first-use, ATM10 viewport and narrator observation;
4. reassess the remaining `0.3.0` Inspectable Mathematics release gap;
5. only then select a new feature or public extension contract.

This decision follows the release path in
`docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md` and the active
queue in `docs/PRIORITY_ASSESSMENT.md`. Foundation Beta promises trust,
dedicated-server behavior, reload/reconnect, first-use evidence and honest
experimental classification. Those promises have higher delivery priority than
`A0-6` external authoring sources.

`A0-6` remains `BACKLOG`. P13's implemented observational surface remains
available and experimental, but no new P13/P14/P15 authority is granted here.
P14 terrain mutation remains blocked on the P8 rows named below.

## 2. Current repository baseline

The following are accepted:

- A0 built-in authoring metadata and exact legacy replay;
- A1 read-only graph presentation;
- L0 scoped source, compilation, atomic persistence, read-only projection and
  the Factored Leap gameplay/content slice;
- the existing P8 EMPTY_ONLY fill transaction and transient construct flight;
- existing P9, P10, P11 and P13 bounded slices.

The existing GameTest inventory contains:

```text
3 P8 GameTests
2 P9 GameTests
2 P10 GameTests
2 P11 GameTests
2 P13 GameTests
29 L0 GameTests
3 A0 compatibility GameTests
43 global GameTests
```

The current three P8 GameTests prove:

- reverse rollback plus exact escrow restoration after an admitted commit
  failure;
- whole-plan rejection and zero consumption through the admission seam;
- block collision, launch-time physical snapshot capture and no terrain
  mutation.

They do not individually close the remaining P12/P8 cases for the default
unloaded, fluid and block-entity policies, a separately observable protection
denial, unloaded flight, duplicate-owner flight or absence of a client-owned
P8 authority surface.

## 3. Immediate task: P12-TM-01

**Owner:** Terra Medium  
**Status:** `NEEDS_FIX`; superseded by bounded correction `P12-TM-01F`  
**Nature:** original evidence-only slice; see the blocker review for the exact
production correction ownership

### 3.1 Exact write ownership

Terra Medium may edit only:

```text
src/main/java/com/mathmod/program/P8GameTests.java
src/test/java/com/mathmod/program/P8AuthoritySurfaceTest.java
docs/handoffs/P12_TM_01_HANDOFF.md
```

`P8AuthoritySurfaceTest.java` may be created.

All production implementation is read-only, including:

```text
src/main/java/com/mathmod/program/ConstructionFillService.java
src/main/java/com/mathmod/program/ConstructFlightManager.java
src/main/java/com/mathmod/program/ProgramExecutor.java
src/main/java/com/mathmod/program/RegionCandidatePlanner.java
src/main/java/com/mathmod/network/
```

No public API, payload, schema, Data Component, persistent identity, client/UI,
worldgen, configuration, claim-mod integration or resource file may change.

If a required vector exposes a production defect or lacks a safe observable
seam, Terra Medium must not patch production. The handoff must record a
reproducible blocker, the exact failing vector and the minimum requested file
ownership for a later Sol decision.

### 3.2 Required existing vectors

The three existing P8 GameTests must remain named and passing:

```text
fillRollbackRestoresEscrowAfterCommitFailure
fillAdmissionFailureConsumesNothing
constructBlockCollisionCapturesSnapshotAndDoesNotMutateTerrain
```

### 3.3 Required new GameTests

Add exactly these seven independently reported GameTests:

```text
fillUnloadedCandidateFailsClosedWithoutLoadingOrConsumption
fillFluidCandidateFailsClosedWithoutMutationOrConsumption
fillBlockEntityCandidateFailsClosedWithoutMutationOrConsumption
fillProtectionDenialNeverCommitsOrConsumes
constructUnloadedFlightStopsWithoutTicketOrTerrainMutation
constructSecondLaunchForSameOwnerFailsClosed
constructRejectsUnboundedMotionAndUsesServerDerivedBodyCost
```

Required results:

1. An unloaded candidate rejects the whole fill, remains unloaded, creates no
   ticket, changes no block and consumes no item.
2. A fluid position is not replaced and consumes no item.
3. A block-entity position and its data remain exact and consume no item.
4. A protection/admission denial occurs before escrow and the commit callback
   is never invoked.
5. A flight approaching an unloaded chunk is discarded without loading the
   chunk, mutating terrain or leaving an active flight.
6. One owner cannot create a second concurrent flight; the rejected launch
   consumes nothing and does not replace the first flight.
7. Non-finite, zero or over-limit motion is rejected. Accepted launch payment
   is derived from the server-owned `ConstructBody.massEquivalent`, never from
   a client count, preview, physical mass or presentation value.

Each test must snapshot relevant inventory, blocks/block entity, chunk loaded
state and active-flight state before the action and compare the postcondition.
Broad exception catches, test-owned state substituted for the production path,
or a green assertion that never reaches the intended rejection are not
evidence.

After this task the exact expected inventory is:

```text
10 P8 GameTests
50 global GameTests
```

The global count is not a substitute for the ten named P8 cases.

### 3.4 Client-authority negative proof

`P8AuthoritySurfaceTest` must inspect the registered payload surface and prove
that no accepted client payload field directly supplies:

- candidate positions;
- candidate/fill count;
- block state;
- physical mass or snapshot version;
- active flight state;
- chunk-loading choice.

The test must also prove from the real server path that material resolution,
candidate planning, item count, body mass-equivalent, velocity bounds and
one-flight-per-owner checks occur on the server. It must not freeze source text
or private method formatting as an oracle when behavioral or structural
reflection evidence is available.

Client-authored proof parameters may propose bounded source values. They do not
authorize positions, payment, snapshots, mutation or flight state.

## 4. Required verification

Focused ordinary command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache --no-daemon `
  --tests com.mathmod.program.P8AuthoritySurfaceTest `
  --tests com.mathmod.program.RegionCandidatePlannerTest `
  --tests com.mathmod.program.ConstructBodyTest `
  --tests com.mathmod.program.CapturedConstructPhysicsTest `
  --tests com.mathmod.program.ProgramRegionPipelineTest `
  --tests com.mathmod.program.ConstructiveRegionRuneTest `
  --tests com.mathmod.ServerSideIsolationTest
```

Runtime and standard commands:

```powershell
.\gradlew.bat runGameTestServer --no-daemon
.\gradlew.bat build --no-daemon
```

The handoff must report:

- exact changed-file inventory;
- exact focused class/method counts;
- all ten P8 GameTest names and results;
- P8, P9, P10, P11, P13, L0, A0 and global GameTest counts separately;
- evidence that each new case reaches the intended production boundary;
- before/after inventory, world, chunk and active-flight observations;
- registered payload inventory and the authority-negative result;
- limitations and escalations;
- confirmation that production and every forbidden boundary stayed unchanged.

Build success alone does not close a P12 row.

## 5. Gate after P12-TM-01

Sol must review `docs/handoffs/P12_TM_01_HANDOFF.md` and the real repository
delta.

If all ten P8 cases and the negative authority proof are sufficient:

- `P12-TM-01` becomes `DONE` with `ACCEPT`;
- automated P8 GT-01 through GT-04 become closed for the currently implemented
  non-destructive P8 surface;
- P8 still remains `experimental` until its dedicated-server smoke row passes;
- `P12-TM-02` may become `READY` for the remaining P9/P10/P11 automated rows.

If a concrete defect exists:

- `P12-TM-01` becomes `NEEDS_FIX`;
- production correction remains blocked until Sol freezes exact ownership;
- P14 remains blocked;
- no failed row may be reclassified as optional or not applicable without a
  repository-backed Sol decision.

## 6. Ordered completion program

### Now — P12-TM-01

Close the P8 automated survival boundary without production edits.

### Next — P12-TM-02

Close remaining automated P9 target/refresh, P10 reconciliation/configuration
and P11 captured-flight/reload/unloaded variants. Exact ownership will be
frozen only after P12-TM-01 acceptance.

### Then — P12-DS

Execute DS-01 through DS-09 on a real dedicated server with the exact
NeoForge/Minecraft version, intended data/KubeJS/configuration and required
one/two-client actors. Preserve sanitized logs and evidence records. A
singleplayer or GameTest run cannot substitute for this gate.

### Then — P12-M

Execute M-01 through M-03:

- unaided first-spell completion by an independent player;
- actual ATM10 GUI-scale/JEI observation in EN-US and PT-BR;
- keyboard and real narrator pass.

Manual failure is product evidence, not permission to waive the row.

### After Foundation Beta consolidation

Create a Sol-owned `0.3.0` gap audit against the Inspectable Mathematics exit
criteria. That audit must reconcile the accepted A1 read-only surface with the
remaining typed-literal, local-cost, dependency, accessibility and release
evidence before mutable A1, A0-6, D0, L1 or broad new gameplay is prioritized.

## 7. Explicitly blocked work

This contract does not authorize:

- P14 block breaking, replacement, drops or terrain damage;
- P15 entity or terrain dynamics expansion;
- mutable A1 graph editing or advanced persistence;
- A0-6 datapack/KubeJS authoring metadata loaders or public schemas;
- D0/D1 Disciplines;
- L1 textual DSL;
- new networking;
- new persistent components or migrations;
- claim-mod compatibility claims;
- survival-ready labeling before all applicable P12 gates pass.

```text
P12-SOL-01 DONE (ACCEPT)
    -> P12-TM-01 DONE (ACCEPT)
    -> P12-TM-01F DONE (ACCEPT)
    -> P12-TM-02 DONE (ACCEPT)
    -> P12-SOL-02 DONE (ACCEPT)
    -> P12-FX-01F DONE (ACCEPT)
    -> P12-FX-01 BLOCKED (ENVIRONMENT_FAILURE)
    -> P12-DS BLOCKED
    -> P12-M BLOCKED
    -> Foundation Beta consolidation gate
    -> 0.3.0 Inspectable Mathematics gap audit

A0-6 BACKLOG
P14/P15 BLOCKED
```
