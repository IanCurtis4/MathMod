# A0-3 Legacy Adapter Gate Acceptance

**Task:** `A0-SOL-03`  
**Date:** 2026-07-26  
**Owner:** Sol  
**Decision:** `ACCEPT`

## Gate inputs

- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`;
- `docs/A0_METADATA_FOUNDATION_GATE_ACCEPTANCE.md`;
- `docs/handoffs/A0_TM_02_HANDOFF.md`;
- `docs/A0_LEGACY_ADAPTER_SEMANTIC_REVIEW.md`;
- implementation and focused tests under `com.mathmod.authoring`;
- current `GuidedWorkspaceState`, `CustomSpellInvocation`, and
  `ProgramGraph` as read-only authority references.

Terra High returned `APPROVE`. Sol independently inspected the implementation,
the persistence boundary, the changed-file inventory, and reran the relevant
tests and build before accepting the gate.

## Exact replay evidence

Accepted.

- The adapter resolves a frozen form id to one trusted built-in adapter id.
- Supplied arguments are rebuilt from the declared parameter descriptors:
  missing keys use defaults, unknown keys are ignored, and descriptor
  canonicalization handles non-finite and out-of-range values.
- All 67 built-in forms are exercised at defaults through
  `CustomSpellAction.values()`.
- Parameterized vectors and a growing representative sequence compare the
  mediated result with the legacy expansion.
- Acceptance is based on `ProgramGraph.equals`, so node and edge order,
  identities, rune ids, constants, input names, output node, and budget must
  all match.
- Unknown forms, untrusted/mismatched adapter ids, more than 128 invocations,
  and graph replay mismatch fail closed.

The already accepted metadata foundation still accounts for all 67 forms and
11 categories. This gate does not reopen those identities.

## Accepted file inventory

Production:

```text
src/main/java/com/mathmod/authoring/TrustedLegacyExpansionAdapter.java
```

Focused test:

```text
src/test/java/com/mathmod/authoring/TrustedLegacyExpansionAdapterTest.java
```

Handoff and semantic review:

```text
docs/handoffs/A0_TM_02_HANDOFF.md
docs/A0_LEGACY_ADAPTER_SEMANTIC_REVIEW.md
```

No A0-TM-02 change was found in `ProgramGraph`, `GuidedWorkspaceState`,
`ModDataComponents`, networking, `ProgramSurfaceMode`, client screens, or a
public API.

## Identity, persistence, and migration impact

- Canonical persisted identity remains the Rune Form id.
- Adapter ids are internal trusted implementation identifiers and are not
  written by `CustomSpellInvocation` or `GuidedWorkspaceState`.
- `GuidedWorkspaceState.CURRENT_VERSION` remains `1`.
- `ProgramGraph` remains the authoritative executable artifact.
- No Data Component, codec, payload, migration, automatic item rewrite, or
  public loader/API was added.
- Migration impact: none.

## Authority and failure behavior

The adapter is package-private common-side Java with no player, level, item,
network, random, clock, file, callback, command, or executor dependency.
Metadata selects only a trusted built-in mapping; it cannot supply executable
code or override graph authority.

Replay is all-or-nothing. A missing form, unexpected adapter identity,
invocation bound violation, or graph mismatch prevents acceptance of a
different graph. Player-facing mapping of `UNKNOWN_FORM` and
`GRAPH_REPLAY_MISMATCH` remains integration work for A0-4/A0-5 and does not
change this gate's fail-closed result.

## Tests and build

Executed with an ASCII Gradle cache:

```text
GRADLE_USER_HOME=C:\codex-gradle-a0
.\gradlew.bat test --tests 'com.mathmod.authoring.*' --rerun-tasks --no-daemon
```

Result: `BUILD SUCCESSFUL`; 10 tests, 0 failures, 0 errors, 0 skipped:

- `BuiltInAuthoringMetadataTest`: 6;
- `TrustedLegacyExpansionAdapterTest`: 4.

Standard build:

```text
GRADLE_USER_HOME=C:\codex-gradle-a0
.\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`. Existing deprecation warnings are non-blocking and
unrelated to A0-3.

## Remaining limitations and assigned follow-up

These items do not reject A0-3:

- integrated player-facing diagnostics for unknown form and replay mismatch:
  A0-4/A0-5;
- raw negative-infinity, positive-overflow, partial-missing-key, 128/129
  invocation, and individual graph-mismatch adversarial vectors: A0-5;
- save/reload/reconnect and item-level atomicity: A0-5;
- legacy qualified/unqualified alias behavior when the persisted reader is
  connected: A0-5 or a separately approved migration slice;
- external adapters, loaders, aliases, and public APIs: deferred and not
  authorized.

## Ownership release and next gates

A0-TM-02 releases write ownership of the A0 adapter implementation and tests.
They return to read-only status until a specifically approved correction or
A0-5 hardening slice names them.

`A1-TM-READONLY` may start. Its exact write ownership is:

```text
src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java
src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java
src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java
src/test/java/com/mathmod/client/screen/ProgramGraphPresentationTest.java
src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java
src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java
docs/handoffs/A1_TM_READONLY_HANDOFF.md
```

No additional production, screen, test, localization, or preview file is
authorized without a prior board ownership update. In particular,
`RuneProgrammerScreen`, the preview harness, all A0 authoring files,
`ProgramGraph`, `GuidedWorkspaceState`, Data Components, networking,
`ProgramSurfaceMode`, and public APIs remain read-only or forbidden.

A0-4 planning may advance using this accepted boundary, but A0-TM-03
implementation remains blocked until:

1. `A1-TM-READONLY` is accepted and releases screen ownership;
2. `A0-SOL-LU-01` freezes the terminology/content decision;
3. Sol assigns the exact A0-TM-03 screen, test, and preview ownership.

## Board transition

```text
A0-TM-02       IN_REVIEW -> DONE
A0-TH-02       READY     -> DONE (APPROVE)
A0-SOL-03      BLOCKED   -> DONE (ACCEPT)
A1-TM-READONLY BLOCKED   -> READY
```

`A0-TM-03`, `A0-TM-04`, and `A0-W4-GATE` remain `BLOCKED`.
