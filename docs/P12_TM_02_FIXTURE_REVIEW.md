# P12-TM-02 GT-05 Fixture Review

**Date:** 2026-07-30  
**Reviewer:** Sol  
**Decision:** legitimate `ENVIRONMENT_FAILURE`; bounded test-fixture correction
authorized

## Repository evidence reviewed

Sol reviewed:

- `docs/handoffs/P12_TM_02_HANDOFF.md`;
- the real delta in
  `src/main/java/com/mathmod/program/P9GameTests.java`;
- `ProgramExecutor` and its real `DefensiveStatusEffectPlan`;
- the GameTest server log and a fresh reproduction;
- the Minecraft/NeoForge sources resolved by the repository's declared
  Minecraft 1.21.1 / NeoForge 21.1.234 build.

The added test remains inside the P12-TM-02 ownership frozen in
`docs/P12_TM_01_FINAL_GATE_ACCEPTANCE.md`. No P10/P11 or production file was
changed.

## Reproduced result

Command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat runGameTestServer --no-daemon
```

Result:

```text
51 GAME TESTS COMPLETE
50 passed
1 required test failed
defensiveselfcastconsumesescrowandboundedlyrejectsrefresh
Payload neoforge:sync_attachments may not be sent to the client!
```

The failure occurs at the real `LivingEntity.addEffect` call after target
resolution and escrow. `GameTestHelper.makeMockServerPlayerInLevel()` creates
an embedded serverbound connection but does not negotiate NeoForge payload
channels. `NetworkRegistry.checkPacket` therefore rejects
`neoforge:sync_attachments`. This is a fixture incompatibility and is not
evidence that an ordinarily negotiated NeoForge client rejects the payload.

## Sol decision

No production-observation seam is justified. NeoForge 21.1.234 already exposes
the test-specific API:

```java
NetworkRegistry.configureMockConnection(connection)
```

Its dependency-source contract says that it configures a mock connection for
GameTests as a fully compatible NeoForge server/client pair and installs the
registered payload setup. Terra Medium may configure the connection returned by
the existing mock player's server listener immediately after player creation
and before the first real P9 execution.

This is fixture configuration, not networking production behavior. It is
authorized only in:

```text
src/main/java/com/mathmod/program/P9GameTests.java
```

The existing P12-TM-02 ownership of `P10GameTests.java`, `P11GameTests.java`
and `docs/handoffs/P12_TM_02_HANDOFF.md` remains unchanged.

## Required correction evidence

1. Keep the target as the same real `ServerPlayer`; a non-player target is not
   an acceptable substitute for self-cast.
2. Keep the real `ProgramExecutor.execute(ProgramPresets.resistanceLemma(),
   player)` route.
3. Configure only the mock connection through NeoForge's
   `configureMockConnection`; do not modify payload registration, production
   networking or attachment behavior.
4. Do not catch, suppress or special-case
   `UnsupportedOperationException`/`sync_attachments`.
5. Assert the successful first effect, exact Vital Salt consumption, retained
   Homuncular Matrix, bounded duration/amplifier, rejected equal refresh,
   unchanged effect state and exact escrow restoration.
6. Add the remaining dead-target GT-05 observation without weakening the
   liveness recheck.
7. Continue GT-06 and GT-07 only after the corrected GT-05 group passes.
8. Reproduce focused relevant tests, the complete GameTest server and the
   standard build. Report P9/P10/P11 and global GameTest names/counts
   separately.

## Forbidden

- production Java changes, including `ProgramExecutor`;
- a new public or production test seam;
- networking or attachment changes;
- schemas, Data Components, client/UI, content or configuration;
- replacing the self-player with a mob;
- treating the current failed run as a pass or optional row.

## Gate state

- `P12-TM-02`: `NEEDS_FIX`, authorized to resume under this fixture decision;
- `P12-DS`, `P12-M` and later expansion remain blocked;
- acceptance still requires the complete GT-05, GT-06 and GT-07 evidence.

