# P12-TM-02 Final Gate Acceptance

**Date:** 2026-07-30  
**Reviewer:** Sol  
**Decision:** `ACCEPT`

## Scope reviewed

Sol reviewed the final handoff and the real repository delta for:

```text
src/main/java/com/mathmod/program/P9GameTests.java
src/main/java/com/mathmod/acquisition/P10GameTests.java
src/main/java/com/mathmod/program/P11CapturedFlightGameTests.java
docs/handoffs/P12_TM_02_HANDOFF.md
```

The delta is within the ownership frozen by:

- `docs/P12_TM_01_FINAL_GATE_ACCEPTANCE.md`;
- `docs/P12_TM_02_FIXTURE_REVIEW.md`;
- `docs/P12_TM_02_GT07_FIXTURE_REVIEW.md`;
- `docs/P12_TM_02_GT07_RELOAD_AUTHORITY_CLARIFICATION.md`.

No production runtime authority, visibility modifier, networking, schema, Data
Component, client/UI, content, configuration or public API changed.

## Contract closure

### GT-05 — accepted

Four P9 GameTests close self cast, anchor rejection, missing resources, dead
target and bounded repeated defensive cast. The successful self-player path
uses the real `ServerPlayer`, real `ProgramExecutor`, exact escrow and only
NeoForge's test-specific mock-connection configuration.

### GT-06 — accepted

Five P10 GameTests close:

- initial publication;
- safe independently disabled configuration;
- independent loot/profession/trade/house flags;
- closed-menu reconciliation preserving a valid marked offer's exact instance,
  uses, maximum uses, base cost, non-default special price and non-default
  demand while removing the rejected marked offer and retaining the unmarked
  offer;
- open-menu deferral preserving exact list size, order, instances and mutable
  valid-offer state.

### GT-07 — accepted

Five P11 GameTests close canonical profile/publication evidence and three
captured-flight cases:

- a dedicated asynchronous batch launches under N, executes the real public
  server resource reload, proves exact publication N+1, retains N for the
  active flight and captures N+1 for a future owner's flight;
- collision discards the captured-profile flight without terrain mutation or
  replacement flight/entity;
- an unloaded boundary discards the captured-profile flight without loading or
  ticketing the chunk and without terrain mutation.

The new holder resides in `com.mathmod.program` only to use existing
package-private flight fixture authority. It introduces no reflection, facade
or visibility change. Static flight state is cleared around every observation.

## Reproduced evidence

Focused suite, forced to execute rather than rely on incremental state:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat test --no-daemon --no-build-cache --rerun-tasks `
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

Result: 13 suites, 47 tests, 0 failures, 0 errors, 0 skipped.

```powershell
.\gradlew.bat runGameTestServer --no-daemon
```

Result: all 58 required GameTests passed. Counts are P9 4, P10 5, P11 5,
other GameTests 44, global 58.

The runtime log proves the separate `p12_p11_reload` batch executed the reload
and published the next physical snapshot before completing.

```powershell
.\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

## Gate decision

- `P12-TM-02`: `DONE` with `ACCEPT`;
- automated GT-05, GT-06 and GT-07 are closed for the currently implemented
  P9/P10/P11 surfaces;
- P8, P9, P10 and P11 remain `experimental`;
- GameTests do not substitute for the real dedicated-server or manual rows;
- `P12-DS` remains blocked because its documented dependency also requires a
  proven real-server fixture and actor/evidence protocol.

## Next eligible task

Only the Sol-owned documentary readiness gate below becomes `READY`:

```text
P12-SOL-02 — Dedicated-Server Fixture And Evidence Readiness
```

It must inventory and freeze, from repository evidence:

- the exact Minecraft 1.21.1 / NeoForge 21.1.234 server launch artifact and
  configuration;
- required built-in data, KubeJS/data-pack inputs and clean-world policy;
- one-client and two-client actor requirements for DS-01 through DS-09;
- operator commands, checkpoints, sanitized log paths and failure recording;
- which rows require fresh world, restart, `/reload`, reconnect or two
  independent accounts;
- evidence that no GameTest/singleplayer result is substituted;
- exact ownership for execution records and any fixture-only files.

Output:

```text
docs/P12_DEDICATED_SERVER_FIXTURE_READINESS.md
```

This gate is documentation/read-only with respect to Java and runtime content.
It may not execute or claim DS-01 through DS-09 before the fixture is proven.

