# A0-TM-04 Dispatch Readiness Acceptance

**Task:** task 8 / `A0-TM-04` — A0 Compatibility Hardening  
**Date:** 2026-07-26  
**Owner:** Terra Medium  
**Decision:** `READY`

## Required documentation

Read in this order:

1. `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`;
2. `docs/FOUNDATION_BETA_A0_ASSIGNMENT.md`;
3. `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`, especially sections 9,
   11, 12, 16/A0-5, 17, 18, and 21–22;
4. `docs/A0_LEGACY_ADAPTER_GATE_ACCEPTANCE.md`;
5. `docs/A0_TM_03_FINAL_GATE_ACCEPTANCE.md`;
6. `docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md`;
7. `docs/A0_LU_01F_GATE_ACCEPTANCE.md`;
8. `docs/P2_MODE_PERSISTENCE_CONTRACT.md`;
9. `docs/UI_PREVIEWS.md`;
10. `docs/DELIVERY_BOARD.md`.

Also inspect the current Foundation Beta dedicated-server, GameTest,
reload/reconnect, and migration evidence referenced by those documents.

## Required output

Create:

```text
docs/handoffs/A0_TM_04_HANDOFF.md
```

The handoff must provide:

- a real schema-1 Guided save vector and proof that it decodes and replays to
  the exact existing graph without rewriting the item;
- unknown, malformed, and future metadata vectors proving that an otherwise
  valid persisted graph remains inspectable and executable;
- missing-presentation technical fallback evidence;
- unknown/unreplayable form failure with the frozen structured diagnostic and
  read-only graph inspection retained;
- built-in snapshot reconstruction/reload and reconnect evidence, including
  equal active identity and last-known-good retention on a rejected candidate;
- dedicated-server evidence proving no dependency on client presentation;
- focused tests, applicable GameTests, dedicated-server command/result, and
  standard build;
- exact changed-file inventory, migration impact, known limitations,
  escalations, next owner, and released ownership.

## Exact write ownership

Existing tests:

```text
src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java
src/test/java/com/mathmod/authoring/TrustedLegacyExpansionAdapterTest.java
src/test/java/com/mathmod/program/GuidedWorkspaceStateTest.java
src/test/java/com/mathmod/ServerSideIsolationTest.java
```

Authorized new focused tests:

```text
src/test/java/com/mathmod/authoring/AuthoringCompatibilityHardeningTest.java
src/test/java/com/mathmod/program/AuthoringSchema1CompatibilityTest.java
```

Documentation:

```text
docs/handoffs/A0_TM_04_HANDOFF.md
```

No production Java change is authorized by default. The accepted A0
implementation is expected to satisfy this hardening task through migration,
failure-path, reload/reconnect, and server-isolation evidence.

## Read-only implementation inputs

```text
src/main/java/com/mathmod/authoring/**
src/main/java/com/mathmod/program/ProgramStorage.java
src/main/java/com/mathmod/program/GuidedWorkspaceState.java
src/main/java/com/mathmod/program/ProgramGraph.java
src/main/java/com/mathmod/program/CustomSpellInvocation.java
src/main/java/com/mathmod/client/screen/AuthoringPalettePresentation.java
src/main/java/com/mathmod/registry/ModDataComponents.java
src/main/java/com/mathmod/network/**
build.gradle
run configuration and existing GameTests
```

## Forbidden boundaries

Do not change:

```text
ProgramGraph
GuidedWorkspaceState production code or schema
ProgramSurfaceMode
ProgramStorage or persistence codecs
Data Components
networking or payloads
client screens or preview harness
execution/inscription authority
stable ids
localization or Patchouli content
public KubeJS/datapack APIs
external loaders
```

Do not automatically rewrite any item or player data during read, reload,
reconnect, or test setup.

## Stop conditions

Stop and escalate to Sol if any required evidence cannot be produced without:

- a production-code change;
- a persistent/schema change;
- changing graph or execution semantics;
- adding client authority to a dedicated-server path;
- inventing recovery for an unreplayable form;
- enabling external sources or public APIs.

Sol will decide whether the finding is an A0 defect, an evidence correction, or
an explicitly deferred limitation before granting any additional path.

## Operational result

All task 8 dependencies are satisfied. `A0-TM-04` is `READY`.
`A0-W4-GATE` remains `BLOCKED` until the A0-TM-04 handoff is accepted.
