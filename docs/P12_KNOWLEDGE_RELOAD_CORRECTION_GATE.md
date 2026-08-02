# P12 Knowledge Reload Atomicity Correction Gate

**Task:** `P12-TM-03`  
**Date:** 2026-08-01  
**Owner:** Terra Medium  
**Status:** `P12-TM-03/P12-TM-03F/P12-TM-03F2 DONE` with `ACCEPT`

**Ownership amendment:** the concrete listener-only blocker is accepted in
`docs/P12_TM_03_BLOCKER_RESOLUTION.md`. That document expands the exact
production/test ownership and requires one paired atomic publication point.
It does not weaken any semantic or command below.

**Current review:** the delivered paired-publication delta has four remaining
findings in `docs/P12_TM_03_GATE_REVIEW.md`. That review is the writable scope
and dispatch authority for P12-TM-03F.

**F review:** production closes the structural findings, but its real-load
JUnit oracle depends on undeclared external build state. The reproducible
GameTest correction and exact test-only ownership are frozen in
`docs/P12_TM_03F_GATE_REVIEW.md`, which is now the dispatch authority.

**Final acceptance:** F2 replaces that oracle with one self-contained NeoForge
GameTest. The complete automated gate and reproduced evidence are accepted in
`docs/P12_TM_03_FINAL_GATE_ACCEPTANCE.md`. All TM ownership is released.

## Purpose

Close only FX-R3 from `docs/P12_FX_01_AUTONOMOUS_EVIDENCE.md`: a rejected
knowledge resource currently allows a reduced data generation to publish and
replace the previous valid snapshot.

This task is a bounded server/common reload correction. It does not execute or
claim any dedicated-server row and does not reopen content, persistence,
networking, client or UI work.

## Required semantics

1. Parse and validate the complete resolved knowledge candidate before any
   live mutation.
2. Any parse, schema, path, definition-validation, alias-validation or global
   publication failure makes that knowledge reload generation unpublishable.
3. An unpublishable generation changes neither the live definition snapshot
   nor the live alias snapshot; mixed definition/alias generations are
   forbidden.
4. The same-resource valid -> malformed sequence retains the exact previously
   published `successful_casts=3` definition. It must not fall back to the
   built-in value `2` and must not publish a reduced map.
5. Removing the malformed pack in a later fully valid reload is not an error:
   normal resolved-resource removal may publish the resulting fallback
   generation.
6. Diagnostics must distinguish rejection/no-publication from successful
   publication and must not log a false success.
7. Candidate maps and the live snapshot must never be mutated in place.

## Ownership

Initially authorized production file:

```text
src/main/java/com/mathmod/knowledge/KnowledgeDefinitionReloadListener.java
```

Focused test ownership:

```text
src/test/java/com/mathmod/knowledge/KnowledgeDefinitionReloadListenerTest.java
src/test/java/com/mathmod/knowledge/KnowledgeDefinitionRegistryTest.java
src/test/java/com/mathmod/knowledge/KnowledgeAliasRegistryTest.java
```

Handoff:

```text
docs/handoffs/P12_TM_03_HANDOFF.md
```

The original listener-only boundary proved insufficient. The superseding exact
ownership is frozen in `docs/P12_TM_03_BLOCKER_RESOLUTION.md`. No file beyond
that amendment is authorized. Stop and escalate with a new concrete
counterexample if the corrected implementation still requires more ownership.

## Required focused vectors

- valid same-resource replacement publishes `successful_casts=3`;
- malformed schema for that exact resolved id retains the prior definition
  snapshot and alias snapshot;
- malformed JSON, invalid path/id and runtime definition validation each
  retain both previous snapshots;
- invalid alias retains both previous snapshots;
- a globally rejected candidate does not produce a success publication;
- valid later removal publishes the legitimate fallback generation;
- accepted publication remains immutable relative to candidate-map mutation;
- existing size limits and precedence remain unchanged.

Tests must use the real listener candidate/apply boundary. A test that calls
only `KnowledgeDefinitionRegistry.publishData` is insufficient for FX-R3.

## Mandatory commands

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.knowledge.KnowledgeDefinitionReloadListenerTest `
  --tests com.mathmod.knowledge.KnowledgeDefinitionRegistryTest `
  --tests com.mathmod.knowledge.KnowledgeAliasRegistryTest

.\gradlew.bat test --no-daemon
.\gradlew.bat runGameTestServer --no-daemon
.\gradlew.bat build --no-daemon
git diff --check
```

The handoff must state the focused test count and the named vectors, plus the
separate global GameTest count. A green build without listener-boundary
coverage is not acceptance evidence.

## Forbidden

- public API or visibility expansion;
- Data Components, schemas, migrations or persisted identities;
- networking, client or UI changes;
- manuscript/KubeJS precedence changes;
- `ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode` or gameplay
  theorem changes;
- weakening a rejected generation into per-record partial publication.

## Acceptance and next gate

Sol will review the handoff and real delta, reproduce the commands, then rerun
the standalone DS-02 fixture sequence from a clean immutable revision.
`P12-FX-01`, `P12-DS` and all later claims remain blocked until that runtime
recheck retains `successful_casts=3` through the malformed generation.
