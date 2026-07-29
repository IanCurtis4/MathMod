# A0-TM-04 Gate Review

**Task:** task 8 / `A0-TM-04` — A0 Compatibility Hardening  
**Date:** 2026-07-26  
**Owner reviewed:** Terra Medium  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX` — superseded by
`docs/A0_TM_04_FINAL_GATE_ACCEPTANCE.md`

## Outcome

The implementation boundary, dedicated-server isolation, focused suite,
GameTests, and standard build are conformant. The handoff is not yet accepted
because three claims required by
`docs/A0_TM_04_READINESS_ACCEPTANCE.md` are not demonstrated through the
authoritative persistence and execution paths.

No production defect is established by this review. The findings are evidence
gaps and the correction remains test/documentation-only.

## Evidence independently reproduced

Focused command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.authoring.*' --tests 'com.mathmod.program.GuidedWorkspaceStateTest' --tests 'com.mathmod.program.AuthoringSchema1CompatibilityTest' --tests 'com.mathmod.ServerSideIsolationTest' --rerun-tasks --no-daemon
```

Result: `BUILD SUCCESSFUL`; 20 tests, 0 failures, 0 errors, 0 skipped.

Dedicated-server command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; the dedicated GameTest server reported
`All 11 required tests passed`.

Standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

## Accepted portions

- All task changes are confined to the authorized tests and handoff.
- No production Java, schema, Data Component, graph, networking, client,
  execution, or public API boundary changed.
- The frozen schema-1 JSON vector decodes and re-encodes identically and its
  replay produces the expected exact graph.
- Unknown and malformed current recipes and a future recipe fail closed at the
  `GuidedWorkspaceState`/surface level.
- An unknown trusted-adapter form does not mutate the supplied workspace.
- The common authoring and Guided persistence authorities have no
  `net.minecraft.client` import, and the real dedicated server passes.
- The task introduces no migration and no automatic repair.

## Blocking findings

### A0-5-R1 — Persisted schema-1 read and no-rewrite are not evidenced

`AuthoringSchema1CompatibilityTest` exercises
`GuidedWorkspaceState.CODEC` against an isolated `JsonElement`. It never places
the vector on an item, calls `GuidedWorkspacePersistence.read`, or compares the
item's Data Components before and after the read. Therefore it does not prove
the required real persisted-read path or the claim that reading does not
rewrite the item.

### A0-5-R2 — Persisted failure retains an executable graph is not evidenced

`AuthoringCompatibilityHardeningTest` constructs states in memory and checks
that `ProgramSurface.reopenGuided` is empty and that an independently created
Inspector surface still contains the graph. It does not read the failing
metadata from the persisted path, recover the authoritative graph through
`ProgramStorage`, or validate that graph through
`ProgramStorage.validateExecutable`. Consequently the test proves
inspectability only, not the required persisted graph retention and
executability for unknown, malformed, and future metadata.

The correction must also assert the existing unreplayable read result and
explicitly cite the already accepted missing-presentation fallback evidence.
It must not invent repair, substitution, or a new persisted diagnostic.

### A0-5-R3 — Last-known-good publication/retention is overstated

`builtInSnapshotReconstructsTheSameGraphAfterARejectedCandidate` stores one
built-in snapshot, invokes a separate invalid constructor, then constructs a
new built-in snapshot and compares the two by value. No active snapshot
reference or generation is observed, and no publication attempt can replace
either one. This does not prove the contract's last-known-good retention or
unchanged active generation.

The correction must distinguish:

- the applicable built-in reconstruction/reconnect guarantee, which can be
  evidenced now; and
- external loader/reload publication and its last-known-good state, which does
  not exist in A0 and must be classified as deferred rather than claimed as
  tested.

A test-local publication mechanism is not acceptable evidence of product
behavior. If stronger proof requires a production publisher or external
loader, stop and retain the explicit deferral.

## Bounded correction `A0-TM-04F`

**Owner:** Terra Medium  
**Status:** `READY`

Required outputs:

1. Exercise the actual existing item/Data Component read path for a schema-1
   save and compare all relevant item components before and after reading.
2. Exercise persisted unknown, malformed, and future Guided metadata as far as
   the existing codec permits; prove `UNREPLAYABLE`, unchanged authoritative
   graph, read-only inspection, and
   `ProgramStorage.validateExecutable(...).valid()`.
3. Keep malformed serialized-input evidence separate when the codec correctly
   prevents constructing a typed Data Component.
4. Cite the accepted missing-presentation fallback test/evidence and verify
   the unreplayable-form failure without changing diagnostics.
5. Correct the reload/reconnect and last-known-good claims according to
   A0-5-R3.
6. Re-run the focused suite, applicable GameTests/dedicated server, and build.
7. Update `docs/handoffs/A0_TM_04_HANDOFF.md` with exact results, limitations,
   escalations, and released ownership.

Exact write ownership:

```text
src/test/java/com/mathmod/authoring/AuthoringCompatibilityHardeningTest.java
src/test/java/com/mathmod/program/AuthoringSchema1CompatibilityTest.java
src/test/java/com/mathmod/ServerSideIsolationTest.java
src/main/java/com/mathmod/program/A0CompatibilityGameTests.java
docs/handoffs/A0_TM_04_HANDOFF.md
```

The accepted missing-presentation tests and all production files are
read-only. The newly authorized `A0CompatibilityGameTests` class is test-only
despite residing in the NeoForge-discovered main source set. No existing
production Java change is authorized. If the correction cannot close R1 or R2
within these files, it must stop and escalate rather than widen ownership.

## Sol resolution of the GameTest escalation

Terra Medium's escalation is valid. The ordinary JUnit runtime cannot load the
Minecraft `ItemStack` path required by R1 and R2, while this repository
discovers dedicated-server GameTests from annotated classes under
`src/main/java`. The former ownership granted no GameTest source file.

Sol therefore authorizes exactly one new file:

```text
src/main/java/com/mathmod/program/A0CompatibilityGameTests.java
```

It must follow the existing `P8GameTests`/`P9GameTests` discovery pattern:

- `@GameTestHolder(MathMod.MOD_ID)`;
- `@PrefixGameTestTemplate(false)`;
- `@GameTest(template = "empty")`;
- no world mutation beyond ordinary isolated test setup;
- no production entry point or reusable product authority.

Required GameTest vectors:

1. A schema-1 `GuidedWorkspaceState` and valid authoritative `ProgramGraph`
   stored on a programmed talisman. Reading through
   `GuidedWorkspacePersistence.read` must return `AVAILABLE`, preserve exact
   replay/graph equality, remain executable, and leave
   `ItemStack.isSameItemSameComponents(before, after)` true.
2. A current-schema workspace containing an unknown form on an item with a
   valid graph. Reading must return `UNREPLAYABLE`; `ProgramStorage.get` must
   return the unchanged graph; executable validation must remain valid; the
   item components must remain identical.
3. A future-schema workspace on an item with the same valid graph guarantees
   as vector 2.

Malformed serialized input remains a focused codec test because an invalid
serialized value cannot correctly be installed as a typed Data Component.
That separation is evidence of the codec boundary, not a missing GameTest.

The existing generated `empty` structure is sufficient. No change to
`GameTestFixtureGenerator`, `build.gradle`, Data Components, persistence,
`ProgramGraph`, `GuidedWorkspaceState`, or any production authority is
authorized.

Acceptance evidence must include:

- the focused JUnit command and result;
- `runGameTestServer` discovering the three new tests in addition to the
  existing 11;
- the standard build;
- an updated handoff that removes the now-resolved escalation and accurately
  retains the external loader/last-known-good deferral.

## Downstream state

- `A0-TM-04`: `DONE` (`ACCEPT`).
- `A0-TM-04F`: `DONE` (`ACCEPT`).
- `A0-W4-GATE` / task 9: `READY`.

The corrected handoff was accepted in
`docs/A0_TM_04_FINAL_GATE_ACCEPTANCE.md`. The findings in this document remain
the historical reason for the correction.
