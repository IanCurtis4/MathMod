# P12-TM-01F — P8 flight-boundary correction handoff

**Result:** implementation and required verification complete. `P12-TM-01F` is ready for Sol review; this handoff does not change any acceptance or board state.

## Scope and changed files

Production:

- `src/main/java/com/mathmod/program/ConstructFlightManager.java`

Tests:

- `src/main/java/com/mathmod/program/P8GameTests.java`
- `src/test/java/com/mathmod/program/P8AuthoritySurfaceTest.java` (new)

Documentation:

- `docs/handoffs/P12_TM_01F_HANDOFF.md`

No other file was edited for this correction. In particular, networking, `ProgramExecutor`, `ConstructionFillService`, `RegionCandidatePlanner`, public APIs, schemas, Data Components, client/UI, content and configuration remain unchanged. `DELIVERY_BOARD.md` was not edited.

## F-R1 — finite launch preflight

`ConstructFlightManager.launch` now rejects a non-finite `x`, `y`, or `z` velocity component before item resolution, physical snapshot capture, payment, or insertion in the active-flight list. Existing zero and maximum-length checks remain in the same preflight. No non-finite value is clamped, canonicalized, or replaced.

`constructRejectsUnboundedMotionAndUsesServerDerivedBodyCost` exercises NaN, zero, and over-limit launches. For every rejection it observes the exact same inventory count and zero active flights. Its funded mock explicitly disables `instabuild`, then verifies a valid launch consumes exactly `ConstructBody.massEquivalent()`; the test therefore observes the survival path rather than a creative exemption.

## F-R2 — loaded swept-volume preflight

Each flight tick constructs the segment AABB from current position to `next`, inflates it by the construct collision radius, and checks every intersected X/Z chunk with `hasChunkAt` before `clip`, entity lookup, particles, or `advance`. If a required chunk is unavailable, the tick returns false and the existing server iterator discards the flight. It creates no ticket and does not load the missing chunk.

`constructUnloadedFlightStopsWithoutTicketOrTerrainMutation` begins with a loaded boundary block and an unloaded east neighbor. Before/after observations prove: active-flight count becomes zero; that neighbor stays unloaded; and the loaded boundary block remains air. The successful 50-test run proves this previously failing vector now reaches its intended production boundary.

## P8 authority-negative proof

`P8AuthoritySurfaceTest` has two passing tests (2/2). It reads compiled class metadata rather than loading Minecraft classes unavailable to the ordinary test runtime and does not use source text as an oracle.

- It verifies `ModNetworking` references the five registered server payload classes, then checks their record fields exclude candidate positions/counts, fill/block/state, mass/snapshot, active-flight, and chunk-loading authority names/types.
- It verifies bytecode method descriptors for the actual server boundary: fill requires `ServerLevel`, `ServerPlayer`, `SpatialRegion`, and material id; planning accepts the server-owned region; launch requires `ServerPlayer` and `ConstructBody`, accepts no integer payment/count, and `massEquivalent` remains in `ConstructBody`.

Registered payload inventory:

1. `ApplyCustomSpellInvocationPayload`
2. `UpdateCustomSpellNamePayload`
3. `OpenProgrammerHelpPayload`
4. `OpenResourceHelpPayload`
5. `OpenManuscriptManualPayload`

## GameTest evidence

All ten P8 GameTests passed:

1. `fillRollbackRestoresEscrowAfterCommitFailure`
2. `fillAdmissionFailureConsumesNothing`
3. `constructBlockCollisionCapturesSnapshotAndDoesNotMutateTerrain`
4. `fillUnloadedCandidateFailsClosedWithoutLoadingOrConsumption`
5. `fillFluidCandidateFailsClosedWithoutMutationOrConsumption`
6. `fillBlockEntityCandidateFailsClosedWithoutMutationOrConsumption`
7. `fillProtectionDenialNeverCommitsOrConsumes`
8. `constructUnloadedFlightStopsWithoutTicketOrTerrainMutation`
9. `constructSecondLaunchForSameOwnerFailsClosed`
10. `constructRejectsUnboundedMotionAndUsesServerDerivedBodyCost`

The GameTest server reported **50/50 passed**. Counts are separate: P8 10; P9 2; P10 2; P11 2; P13 2; L0 29; A0 3; global 50.

Focused ordinary-test counts, all passing (16 total):

| Class | Tests |
| --- | ---: |
| `P8AuthoritySurfaceTest` | 2 |
| `RegionCandidatePlannerTest` | 4 |
| `ConstructBodyTest` | 2 |
| `CapturedConstructPhysicsTest` | 1 |
| `ProgramRegionPipelineTest` | 3 |
| `ConstructiveRegionRuneTest` | 2 |
| `ServerSideIsolationTest` | 2 |

## Commands and results

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

Result: `BUILD SUCCESSFUL` (16 focused tests).

```powershell
.\gradlew.bat runGameTestServer --no-daemon
```

Result in the prescribed Gradle environment: `BUILD SUCCESSFUL`; `All 50 required tests passed`.

```powershell
.\gradlew.bat build --no-daemon
```

Result in the prescribed Gradle environment: `BUILD SUCCESSFUL`.

The first GameTest invocation in an isolated shell lacked the prior `GRADLE_USER_HOME` assignment and failed before tests started because its generated classpath mixed the default cache with `C:\codex-gradle-a0`. The repeated invocation above used the same session environment prescribed by the focused command and is the passing evidence. Compilation emits only the pre-existing deprecated `makeMockServerPlayerInLevel()` warnings.

## Limitations and escalation

This correction closes only the two reproduced P8 flight defects. It does not claim dedicated-server, reload/reconnect, claim-mod, persistence, terrain mutation, entity creation, or survival-ready product evidence. P8 remains experimental until the remaining P12 gates are accepted.

No further escalation is required within P12-TM-01F ownership.

