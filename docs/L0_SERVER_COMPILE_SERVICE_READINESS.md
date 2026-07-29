# L0 Server Compile Service Readiness

**Task:** `L0-SOL-03`  
**Date:** 2026-07-28  
**Owner:** Sol  
**Decision:** `ACCEPT`  
**Unblocks:** `L0-TM-02 — Server Compile Service and Registry Generation`

## 1. Purpose and precedence

This readiness assignment converts the accepted L0 authority and wire decisions
into one exact, non-overlapping implementation slice. It authorizes no
persistence and no item, player, world, menu, client, network, Data Component,
`ProgramGraph`, or `GuidedWorkspaceState` mutation.

Required reading, in order:

1. `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`;
2. `docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`;
3. `docs/L0_PURE_COMPILE_SEMANTIC_REVIEW.md`;
4. `docs/L0_SCOPED_SOURCE_WIRE_FORMAT_CONTRACT.md`;
5. this readiness assignment;
6. `docs/DELIVERY_BOARD.md`.

On conflict, the accepted authority contract controls identity, authority,
precedence and atomicity; the accepted wire contract controls only the future
persistent envelope. This readiness narrows the next implementation and does
not reopen either accepted decision.

## 2. Current authority inventory

| Concern | Existing authority | L0-TM-02 treatment |
|---|---|---|
| Rune definitions and enabled state | server `RuneRegistry` | add one atomic immutable capture plus a process-local generation token |
| Pure scoped compilation | accepted `ScopedProgramCompiler` and `ScopedRuneSnapshot` | consume unchanged through the captured rune definitions |
| Executability | `ProgramExecutionPolicy` | validate the candidate graph against the same captured rune definitions |
| Resource recommendation and cost | `ProgramResources`, `ProgramCosts`, `ProgramTiers` | add snapshot-explicit internal overloads; do not read an item |
| Material definitions | server KubeJS material registry | copy once into an immutable ordered list for the attempt |
| Knowledge definitions | `KnowledgeDefinitions.snapshot()` | capture the immutable snapshot once for the attempt |
| Player knowledge | immutable `PlayerKnowledge` returned by `KnowledgeService` | caller supplies a server-read value; service only reads it |
| Executable program | persisted `ProgramGraph` | remains unchanged until a later atomic commit |
| Persistence and mode transition | later `L0-TM-03` | entirely out of scope |

`ProgramStorage` is not a compile dependency for this slice. Its global registry,
resource and persistence helpers must not be called by the new service.

## 3. Frozen rune snapshot and generation ownership

### 3.1 Owner and identity

`RuneRegistry` owns both the active definition map and its generation. The
generation is:

- a process-local, non-persistent `long`;
- initialized to `0`;
- opaque to callers even though equality is represented by numeric equality;
- incremented exactly once when the active semantic definition map changes;
- unchanged by a semantic no-op or by an operation that throws;
- never accepted from a client and never encoded into source or item data;
- forbidden to wrap: an attempted change at `Long.MAX_VALUE` fails before the
  active map changes.

Replacing a definition with an equal `RuneDefinition`, enabling an already
enabled rune, disabling an already disabled rune, or an updater returning an
equal definition is a no-op. A change from A to B and back to A produces two
different generations; content hashing is therefore not a generation token.

### 3.2 Atomic capture

One synchronized registry operation captures:

```text
RuneRegistrySnapshot(
    generation,
    immutable ordered map<String, RuneDefinition>
)
```

The map is bounded by the registry's complete active definition count, rejects
duplicate ids, and is detached from later registry mutations. The server compile
attempt constructs its accepted `ScopedRuneSnapshot` from this map. Structure,
purity, type checking, literal resolution, lowering, graph validation and
executable validation use only that capture.

Calling `definitions()` and then reading a generation separately is forbidden:
it is a torn capture.

### 3.3 Complete publication

The registry receives an internal complete-publication operation. It validates
and copies the complete candidate map before taking effect, then atomically:

1. retains the old map and generation if validation fails;
2. performs no publication if the candidate is semantically equal;
3. otherwise replaces the complete active map and increments once.

This is the only approved future reload publication primitive. L0-TM-02 does
not create a datapack/KubeJS/external rune loader, does not claim active reload
coverage, and does not alter existing KubeJS public APIs. Existing startup
registration remains compatible and each real existing mutation advances the
generation.

## 4. Server compile service boundary

The service is synchronous, bounded, server-owned and stateless. It owns no
item reference, player reference, world reference, menu reference, active-task
cache, last-known-good cache, executor, thread or retry queue.

Its inputs are:

- one already bounded, decoded `ScopedProgramSource`;
- one immutable `PlayerKnowledge` value read by the server;
- one server-owned cancellation probe.

The client cannot supply a graph, registry snapshot, generation, material
catalog, knowledge definitions, type, purity, cost, budget bonus, charged-step
count, diagnostic ordering or success claim.

The service itself captures the active rune snapshot/generation, the current
ordered immutable material list, and `KnowledgeDefinitions.snapshot()`.

### 4.1 Authoritative pipeline

One attempt executes:

```text
cancel check
  -> atomic rune snapshot + generation capture
  -> immutable material and knowledge-definition captures
  -> accepted pure ScopedProgramCompiler pipeline
  -> server resource recommendation projection
  -> executable validation with the recommendation's budget bonus
  -> resource admission
  -> knowledge admission
  -> cancel check
  -> active-generation equality check
  -> immutable successful candidate result
```

Cancellation is checked before work, after pure compilation, between admission
stages and immediately before a result is returned. Pure compilation remains
bounded by its accepted 4,096-step meter, so cancellation need not introduce
callbacks inside the accepted compiler stages.

There is no invisible retry. Cancellation, stale generation, or any rejection
returns failure and no candidate graph.

### 4.2 Executable admission

`ProgramExecutionPolicy.validateExecutable` receives a detached `RuneRegistry`
built from the captured definitions and the server-derived resource budget
bonus. It must not read `MathModRuneBootstrap.registry()` during this
validation.

All validation issues are mapped to the stable service diagnostic
`EXECUTABLE_REJECTED`; raw exception text, traversal order and internal node ids
are not result identity.

### 4.3 Resource admission

The service recomputes recommendations from:

- the candidate graph;
- the captured rune definitions;
- the captured material-definition list.

Snapshot-explicit internal overloads in `ProgramResources`, `ProgramCosts` and
`ProgramTiers` must not fall back to `ProgramStorage`, the bootstrap registry,
an item, or a newly-read material catalog.

The structural plan uses the recommendations as its selections and ignores
inventory availability, exactly as current inscription recommendation does.
It must reject bad selectors, missing attributes, missing tier or missing
budget as `RESOURCE_REJECTED`. Actual inventory availability and consumption
remain execution concerns.

The successful result contains an immutable ordered recommendation. Whether an
equal existing graph preserves the item's old selections is decided later by
`InscriptionResourcePolicy` inside the atomic persistence slice; it is not a
decision of this service.

### 4.4 Knowledge admission

For every distinct rune id in the candidate graph, in canonical rune-id order:

1. parse the rune id as a `NamespacedId`;
2. ask the captured `KnowledgeDefinitionSnapshot` for a `RUNE` requirement;
3. if a requirement exists, require it to be satisfied by the captured
   `PlayerKnowledge`.

No requirement means allowed, preserving existing `KnowledgePolicy` behavior.
An invalid id or unsatisfied requirement is `KNOWLEDGE_REJECTED`.

The service does not grant, revoke, migrate, replace or synchronize knowledge.
It does not call a global knowledge snapshot again during the attempt.

## 5. Result, cancellation and downstream recheck

Service diagnostics are separate from `ScopedLanguageIssue` and have these
stable codes:

```text
CANCELLED
EXECUTABLE_REJECTED
RESOURCE_REJECTED
KNOWLEDGE_REJECTED
REGISTRY_GENERATION_STALE
```

Pure compiler failures retain their accepted `ScopedLanguageIssue` codes. A
service result is successful only when it contains all of:

- candidate `ProgramGraph`;
- immutable recommended `ResourceSelection` list;
- captured rune generation;
- captured immutable rune, material and knowledge-definition evidence;
- captured immutable player-knowledge evidence;
- pure compiler charged-step count;
- no pure or service issues.

Any issue makes the candidate graph and recommendations unavailable. Issues are
deterministically ordered by pipeline phase, then canonical path, then code,
with duplicate `(phase, path, code)` entries collapsed.

The final generation check in L0-TM-02 proves only that the service result was
current when returned. `L0-TM-03` must still recheck, immediately before commit:

- the same active rune generation;
- cancellation/menu/request validity;
- target player, hand/slot and exact item identity;
- equality of relevant current player knowledge;
- identity of the current `KnowledgeDefinitionSnapshot`;
- equality of the current material catalog;
- all other atomic-commit preconditions.

A mismatch mutates nothing. Active last-known-good retention, loader/reload
listeners and asynchronous task cancellation do not exist in A0 and are not
required by this slice; no coverage for them may be claimed.

## 6. Exact L0-TM-02 ownership

### 6.1 Existing files writable

Only the following existing production files may be changed:

```text
src/main/java/com/mathmod/runes/RuneRegistry.java
src/main/java/com/mathmod/program/ProgramResources.java
src/main/java/com/mathmod/program/ProgramCosts.java
src/main/java/com/mathmod/program/ProgramTiers.java
```

Allowed changes in the resource files are limited to snapshot-explicit internal
overloads and delegation that preserves every existing caller's behavior and
signature.

### 6.2 Authorized new files

```text
src/main/java/com/mathmod/runes/RuneRegistrySnapshot.java
src/main/java/com/mathmod/program/ScopedServerCompileService.java
src/main/java/com/mathmod/program/ScopedServerCompileRequest.java
src/main/java/com/mathmod/program/ScopedServerCompileResult.java
src/main/java/com/mathmod/program/ScopedServerCompileIssue.java
src/main/java/com/mathmod/program/ScopedCompileCancellation.java
src/test/java/com/mathmod/runes/RuneRegistrySnapshotTest.java
src/test/java/com/mathmod/program/ScopedServerCompileServiceTest.java
docs/handoffs/L0_TM_02_HANDOFF.md
```

These are internal Java seams. They must not be exposed through KubeJS,
datapacks, commands, networking or a new supported public integration API.
No existing public method may be removed, retyped or behaviorally redirected.

### 6.3 Read-only

```text
src/main/java/com/mathmod/language/**
src/main/java/com/mathmod/knowledge/**
src/main/java/com/mathmod/kubejs/**
src/main/java/com/mathmod/program/ProgramStorage.java
src/main/java/com/mathmod/program/InscriptionResourcePolicy.java
src/main/java/com/mathmod/program/ProgramExecutionPolicy.java
src/main/java/com/mathmod/runes/ProgramGraph.java
src/main/java/com/mathmod/runes/MathModRuneBootstrap.java
src/main/java/com/mathmod/registry/**
src/main/java/com/mathmod/network/**
src/main/java/com/mathmod/screen/**
src/main/java/com/mathmod/client/**
src/main/resources/**
```

### 6.4 Forbidden

- item, player, world, inventory or attachment mutation;
- source/component codec or `mathmod:program_scoped_source` implementation;
- `ProgramStorage`, Data Components, Guided state or name/resource persistence;
- menu, packet, payload, `StreamCodec`, client or UI changes;
- `ProgramGraph` production changes or graph semantic changes;
- a rune, material, knowledge or source external loader;
- background execution, retry, task retention or last-known-good cache;
- new public KubeJS/datapack/network/API surface;
- non-NUMBER literals or reopening the accepted pure compiler.

Stop and escalate if the implementation cannot keep all policy calculations on
the captured snapshots, needs an unlisted existing file, needs a persistent
generation, or needs any forbidden authority.

## 7. Required evidence and handoff

The handoff must be written to:

```text
docs/handoffs/L0_TM_02_HANDOFF.md
```

It must include:

1. task/result and concise implementation summary;
2. exact changed-file list, separated into production, tests and documentation;
3. rune generation/no-op/overflow and complete-publication behavior;
4. proof that one attempt uses one rune/material/knowledge snapshot;
5. pipeline and stable diagnostic mapping;
6. focused test command, count and result;
7. standard build command and result;
8. explicit statement that no item/player/world/persistent mutation occurs;
9. explicit statement that no loader, active last-known-good retention,
   networking or public integration API was added or claimed;
10. limitations and escalations.

Required focused vectors:

- immutable snapshot survives later registry mutation;
- every real mutator advances generation and every semantic no-op does not;
- failed mutation/publication preserves map and generation;
- A -> B -> A cannot reuse a generation;
- complete publication is all-or-nothing and increments once;
- generation overflow fails before mutation;
- successful compile uses one capture and returns immutable evidence;
- pure compile rejection returns no service candidate;
- executable, resource and knowledge rejection each fail closed;
- absent knowledge requirement remains allowed;
- cancellation at entry and after compilation fails without retry;
- mid-attempt rune change returns `REGISTRY_GENERATION_STALE`;
- diagnostics are stable and deterministic;
- existing registry, execution and resource-policy tests remain green.

No GameTest or game client launch is required for this pure server-service
slice. Focused JUnit evidence and the repository's standard build are required.

## 8. Gate decision

This document completes `L0-SOL-03` with `ACCEPT`.

```text
L0-SOL-03 DONE (ACCEPT)
    -> L0-TM-02 READY
```

`L0-TM-03` remains `BLOCKED`. Completion of L0-TM-02 does not authorize source
persistence or item commit; Sol must accept its handoff and issue a separate
atomic-persistence readiness assignment.
