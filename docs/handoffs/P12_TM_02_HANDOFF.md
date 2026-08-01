# Handoff: P12-TM-02

## Result

Completed the test-only GT-05, GT-06, and GT-07 automated closure. The focused
ordinary suite passes **47/47**, the dedicated GameTest server passes **58/58**,
and the standard build passes. This handoff is ready for Sol review; it does
not change the Delivery Board or claim Sol acceptance.

## GT-05 — P9 defensive transaction (4 GameTests)

- `defensiveAlchemyRejectsAnchorExecution`: free-anchor execution remains
  player-only and fails closed.
- `missingDefensiveWitnessesLeavePlayerUnchanged`: missing witnesses fail
  before effect mutation.
- `defensiveSelfCastConsumesEscrowAndBoundedlyRejectsRefresh`: the real
  `ServerPlayer` and `ProgramExecutor.execute(ProgramPresets.resistanceLemma(),
  player)` path applies bounded Resistance, consumes exactly one Vital Salt,
  retains the Homuncular Matrix catalyst, then rejects an equal refresh with
  unchanged effect state and exact restored escrow.
- `defensiveAlchemyRestoresEscrowForDeadSelfTarget`: a dead self target is
  rejected by liveness revalidation; it gains no effect and escrow remains
  exact.

The successful self-player fixture uses only NeoForge's authorized
`NetworkRegistry.configureMockConnection(player.connection.getConnection())`.
It does not alter payload registration or suppress attachment failures.

## GT-06 — P10 feature flags and reconciliation (5 GameTests)

- `acquisitionGenerationPublishesWithLore`
- `disabledFeaturesPublishIndependentSafeConfiguration`
- `acquisitionFeatureFlagsRemainIndependent`: separately disables loot,
  profession, trades, and house; effective trades require both trade and
  profession flags.
- `reconciliationRemovesOnlyRejectedMarkedOffers`: a closed Mathemagician
  retains a valid marked offer's instance, uses, max uses, base cost,
  non-default special-price adjustment, and non-default demand; exactly the
  stale marked offer is removed and an unmarked offer remains untouched.
- `openMerchantReconciliationLeavesEveryOfferUntouched`: with a non-null
  trading player, valid/rejected-marked/unmarked offers retain exact list size,
  order, instances, and mutable valid-offer state. The real reconciler does
  not mutate an open menu.

## GT-07 — P11 captured-flight/reload authority (5 GameTests)

Existing P11 tests retained:

- `canonicalStoneProfileIsFiniteAndBounded`
- `profilePublicationSwapsOnlyFutureSnapshots`

Added in `P11CapturedFlightGameTests`:

- `capturedFlightRetainsProfileVersionAcrossRealReload` runs in the dedicated
  `p12_p11_reload` batch with a 300-tick bound. It launches funded,
  non-creative owner A under snapshot N, copies selected pack ids, calls the
  public `MinecraftServer.reloadResources(...)` authority, polls the returned
  future through a non-blocking GameTest sequence, rejects exceptional
  completion, proves the physical snapshot is exactly N+1, preserves owner A's
  captured N, and proves owner B captures N+1. Flight cleanup runs in the
  sequence's exact `finally` path.
- `capturedProfileFlightStopsOnCollisionWithoutTerrainMutation` observes a
  captured version, ticks through the real flight manager into a block
  collision, then proves discard and unchanged origin/collision terrain.
- `capturedProfileFlightStopsBeforeUnloadedChunkWithoutTicketOrTerrainMutation`
  observes a captured version at a loaded/unloaded boundary, proves discard,
  no target-chunk load/ticket, and unchanged loaded terrain.

All three use the existing package-private flight authority from the authorized
test-only `com.mathmod.program` holder, fund a non-creative player, preserve
server-derived `ConstructBody` cost, and clear the static flight list before
and after each observation. No reflection, visibility change, facade, or P8
fixture reuse was introduced.

## Files changed

- `src/main/java/com/mathmod/program/P9GameTests.java`
- `src/main/java/com/mathmod/acquisition/P10GameTests.java`
- `src/main/java/com/mathmod/program/P11CapturedFlightGameTests.java`
- `docs/handoffs/P12_TM_02_HANDOFF.md`

No production runtime class or existing P11 class changed.

## Evidence

Environment: `GRADLE_USER_HOME=C:\codex-gradle-a0`, Java 21, Minecraft 1.21.1,
NeoForge 21.1.234.

Focused ordinary suites:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-daemon --no-build-cache `
  --tests com.mathmod.program.AlchemicalRuneDefinitionTest `
  --tests com.mathmod.program.ProgramExecutionPolicyTest `
  --tests com.mathmod.program.ProgramPresetsTest `
  --tests com.mathmod.acquisition.AcquisitionCodecsTest `
  --tests com.mathmod.acquisition.ManuscriptAcquisitionPublicationStoreTest `
  --tests com.mathmod.acquisition.ManuscriptAcquisitionSnapshotBuilderTest `
  --tests com.mathmod.acquisition.ManuscriptLootPlannerTest `
  --tests com.mathmod.acquisition.MathemagicianOfferReconciliationPlanTest `
  --tests com.mathmod.acquisition.MathemagicianTradeCatalogTest `
  --tests com.mathmod.acquisition.WeightedManuscriptSelectorTest `
  --tests com.mathmod.physics.DerivedPhysicsCoreTest `
  --tests com.mathmod.physics.PhysicalRuntimeIntegrationTest `
  --tests com.mathmod.program.CapturedConstructPhysicsTest
```

Result: `BUILD SUCCESSFUL`; 13 classes, 47 tests, 0 failures, 0 errors,
0 skipped.

Dedicated GameTest server:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; **58 GAME TESTS COMPLETE; all 58 required tests
passed.** P9 has 4 named methods, P10 has 5, and P11 has 5 (2 reused plus 3
new). The global count is 58 and is reported separately from the 14 named P12
methods.

Standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

## Migration impact

None. No persisted data is read, rewritten, migrated, or reconfigured by this
test-only delta.

## Limitations and next owner

The automated P11 row executes the real server reload authority, but it is not
a substitute for the later real `/reload` dedicated-server smoke row. P8, P9,
P10, and P11 remain `experimental` pending the separately blocked P12-DS and
P12-M work. Sol is the next owner for review and any Delivery Board decision.
