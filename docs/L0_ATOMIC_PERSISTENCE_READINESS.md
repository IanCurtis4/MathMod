# L0 Atomic Persistence Readiness

**Task:** `L0-SOL-04`  
**Date:** 2026-07-28  
**Owner:** Sol  
**Decision:** `ACCEPT`  
**Unblocks:** `L0-TM-03 — Scoped Source Persistence and Atomic Commit`

## 1. Purpose and precedence

This readiness assigns the first implementation of the accepted scoped-source
envelope, strict schema-1 codec, read classification and atomic component
transition.

Required reading, in order:

1. `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`;
2. `docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`;
3. `docs/L0_SCOPED_SOURCE_WIRE_FORMAT_CONTRACT.md`;
4. `docs/L0_SERVER_COMPILE_SERVICE_READINESS.md`;
5. `docs/L0_TM_02_FINAL_GATE_ACCEPTANCE.md`;
6. this readiness;
7. `docs/DELIVERY_BOARD.md`.

The authority contract controls graph/source/Guided precedence and commit
semantics. The wire contract controls every persistent identity, field, tag,
limit, classification and network exclusion. This readiness only resolves the
remaining implementation ownership and transaction boundary.

No mutable functional editor, client-to-server source request, read-only client
projection, theorem, loader, public KubeJS/datapack API or network DTO is
authorized.

## 2. Current persistence inventory

| Concern | Current owner/state | L0-TM-03 decision |
|---|---|---|
| Executable graph | `mathmod:program` / `ProgramGraph` | remains sole executable authority |
| Shared name | `mathmod:program_name` | remains separate from source |
| Resource selections | `mathmod:program_resources` | remains separate; preserve old selections only when graph equality allows |
| Guided current state | `mathmod:program_guided_workspace` | removed by functional success |
| Guided legacy actions | `mathmod:program_custom_actions` | removed by functional success |
| Guided legacy/shared name | `mathmod:program_name` | not removed merely because Guided metadata is removed |
| Scoped source | absent today | add exactly `mathmod:program_scoped_source` |
| Current save boundary | sequential `ProgramStorage` calls | replace in repository-owned inscription routes with one centralized component transaction |

`GuidedWorkspacePersistence.write/clear` and the present
`ProgramStorage.saveValidated*` sequence are not atomic. L0-TM-03 must not
compose them to implement functional persistence.

## 3. Source-to-result binding

`ScopedServerCompileResult` intentionally contains admission evidence but not
the authored source. Therefore a commit API that accepts an independently
supplied `(ScopedProgramSource, ScopedServerCompileResult)` pair is forbidden:
the pair could contain a source different from the source that produced the
admitted graph.

The only approved functional entry seam is one internal synchronous
coordinator:

```text
capture target identity and exact old state
  -> capture server PlayerKnowledge
  -> construct ScopedServerCompileRequest from the supplied source
  -> ScopedServerCompileService.compile(request)
  -> encode that exact same source instance to canonical schema 1
  -> build complete candidate component state off-item
  -> recheck all authorities and target identity
  -> apply one centralized component transaction
```

It must not expose a commit method that accepts a prebuilt compile result from a
caller. The client cannot supply source/result association, graph, generation,
resource plan, knowledge snapshot or success.

The coordinator is infrastructure only. No menu, packet, command, item use or
client surface calls it in L0-TM-03.

## 4. Envelope and strict schema-1 implementation

### 4.1 Component value

Add one immutable content-value type:

```text
ScopedSourceEnvelope(
    int schemaVersion,
    byte[] payload
)
```

It must:

- enforce payload length `0..262_144` before copying;
- defensively copy on construction and access;
- implement equality/hash by byte content;
- preserve every signed 32-bit schema value;
- expose the accepted persistent `Codec`;
- contain no parsed AST cache, mutable buffer or network codec.

The outer codec has exactly `schema_version` and `payload`. Missing, wrong-type
or over-limit values fail decoding. Platform maps cannot retain duplicate keys;
duplicate outer-field rejection is therefore codec-input/parser evidence, not
a promise that an already collapsed map can recover duplication.

### 4.2 Data Component

`ModDataComponents` adds exactly:

```text
id: mathmod:program_scoped_source
value: ScopedSourceEnvelope
persistent codec: ScopedSourceEnvelope.CODEC
cache encoding: yes
network synchronized: no
```

No `networkSynchronized`, `StreamCodec`, `CustomPacketPayload` or alias id may
be added. Existing component ids, codecs and network behavior are read-only.

### 4.3 Strict JSON parser

Schema 1 uses a streaming parser. Building a generic Gson/JSON tree before
limit enforcement is forbidden.

For the `4_096` JSON-value/container limit:

- every object, array and scalar value token counts once;
- object field names do not count;
- the root object counts;
- the 4,097th token fails before its value/container allocation.

Container depth counts the root container as depth 1. Expression depth counts
the root expression as 1. Binding depth increases only for a lambda body or let
body; a let value remains at the outer depth. Type nesting follows accepted
`RuneTypeExpression.nestingDepth()` semantics: a value type is depth 0 and each
function layer adds one.

The parser must:

- use strict UTF-8 decoding with malformed/unmappable input set to `REPORT`;
- reject BOM and trailing tokens;
- reject duplicate, missing and unknown fields at every object;
- reject booleans/null where a string, integer, object or array is required;
- accept only minimal decimal integer tokens for indices/budget;
- reject fractions, exponent notation, leading zeroes and integer overflow for
  integer fields;
- reject unpaired Unicode surrogates, including escaped JSON surrogates;
- validate Java-length and UTF-8-byte limits before constructing each model
  string;
- preflight every list/node/type/allocation counter before allocation;
- prevalidate nonblank/no-surrounding-whitespace fields before calling current
  constructors that otherwise trim/default;
- resolve `RuneType` only by its stable id;
- perform accepted structural validation after model construction;
- never resolve a rune id or compile during persistence read.

Canonical encoding is a dedicated deterministic writer, not Gson object-tree
serialization. It emits the exact field/tag order from the wire contract,
compact UTF-8 without BOM, and escapes only as required for valid JSON.

New inscriptions always encode:

```text
envelope schema_version = 1
payload = canonical schema-1 JSON for the exact compiled source
```

The internal `ScopedProgramSource.version()` is checked as current but is not
serialized as a root field.

## 5. Read classification and precedence

One read-only item boundary returns:

```text
ABSENT
CURRENT_VALID
CURRENT_UNREADABLE
UNSUPPORTED_VERSION
INVALID_ENVELOPE
CONFLICT
```

Physical Guided presence means either:

- `program_guided_workspace` is present; or
- legacy `program_custom_actions` is present.

`program_name` alone is shared metadata and does not create conflict.

If scoped source and either Guided representation are physically present,
classification is `CONFLICT` before validity-based precedence. The source
envelope remains available for bounded export, but no parsed source is exposed
as a compile candidate.

Otherwise:

- schema 1 success exposes a candidate source as `CURRENT_VALID`;
- schema 1 parse failure retains the exact envelope as `CURRENT_UNREADABLE`;
- any other schema retains it as `UNSUPPORTED_VERSION`;
- malformed outer values fail at the component codec as `INVALID_ENVELOPE`;
- absence returns `ABSENT`.

Reads never compile, compare source to graph, rewrite, canonicalize, migrate,
repair, add or remove a component. A graph remains executable regardless of
source classification. Source/graph mismatch is determined only by an explicit
authoritative compilation/projection step, never by item load or tooltip read.

## 6. Atomic component transaction

### 6.1 Exact commit set

Capture exact presence and value for all six components:

```text
mathmod:program
mathmod:program_scoped_source
mathmod:program_name
mathmod:program_resources
mathmod:program_guided_workspace
mathmod:program_custom_actions
```

The source capture must retain byte-exact envelope content. Capture also:

- exact target `ItemStack` object;
- item type, count and complete component equality via an `ItemStack.copy()`;
- server-owned request/cancellation identity;
- current player-knowledge value;
- current knowledge-definition snapshot identity;
- current material catalog value;
- current rune generation.

All candidate values and both forward/rollback component patches are built
off-item.

### 6.2 Functional success

One functional transaction:

- writes the admitted graph;
- writes the canonical schema-1 envelope for the exact compiled source;
- writes the canonical accepted name, or removes `program_name` when the
  accepted optional name is empty;
- writes admitted resources;
- removes `program_guided_workspace`;
- removes `program_custom_actions`.

Resource selection uses
`InscriptionResourcePolicy.resourcesToPersist(oldGraph, oldResources,
candidateGraph, recommendations)`:

- equal graph preserves the exact old selections;
- changed graph uses the admitted recommendations.

Removing Guided metadata never independently removes the accepted shared name.

### 6.3 Existing graph-only, Guided and clear routes

`ProgramStorage` remains the central repository authority. Existing public
method signatures and validation behavior remain source-compatible.

On successful graph-only inscription:

- write graph and resources;
- remove scoped source;
- remove both Guided representations and their legacy name as current behavior
  requires.

On successful Guided inscription:

- write graph, Guided state, shared/legacy name, legacy invocation ids and
  resources;
- remove scoped source in the same transaction.

On explicit whole-program clear:

- remove graph, source, name, resources and both Guided representations in one
  transaction.

Validation failure changes nothing. These routes must use the same centralized
transaction primitive; calls to `GuidedWorkspacePersistence.write/clear` or
`ProgramResources.set/clear` must not be interleaved as the commit mechanism.

### 6.4 Precommit rechecks

Immediately before the first target mutation, require all:

- server request/cancellation authority is still active;
- the current target supplier returns the identical `ItemStack` object;
- item is still `ProgrammedTalismanItem`;
- count, item type and complete components equal the precompile item copy;
- all six captured component presence/value pairs still match;
- active rune generation equals the result generation;
- current `KnowledgeDefinitions.snapshot()` is the identical captured object;
- current player knowledge equals captured player knowledge;
- current ordered material catalog equals captured material evidence.

Failure returns a stable issue and mutates nothing. No retry and no transfer to
another stack/slot/hand are permitted.

Stable commit codes:

```text
REQUEST_CANCELLED
TARGET_STALE
REGISTRY_GENERATION_STALE
KNOWLEDGE_STALE
MATERIALS_STALE
COMMIT_FAILED
```

Compiler and admission failures retain their accepted pure/service diagnostics.
Raw exceptions are log detail only.

### 6.5 Application and rollback

The complete candidate patch is first applied to an off-item copy and verified.
Only then may the target be mutated.

Production applies the complete six-component patch through one centralized
primitive. If application, item component verification or post-application
equality checking throws/fails:

1. apply the prebuilt rollback patch;
2. verify exact six-component equality with the captured state;
3. return `COMMIT_FAILED`;
4. expose no candidate as active success.

The transaction test seam may inject failure before/after each of the six
logical component applications, but must exercise the same state machine as the
production adapter. Rollback failure is logged as a severe invariant breach;
the result remains `COMMIT_FAILED` and must never claim success.

No observer callback, network send, inventory sync or player message occurs
inside the transaction.

## 7. Migration and recovery

L0-TM-03 implements no read-time migration:

- no schema 0;
- no Guided-to-source conversion;
- no graph-to-source invention;
- no alias;
- no malformed repair;
- no future-version rewrite;
- no automatic compile.

Future/current-unreadable envelopes survive item copy and server persistence
roundtrip byte-for-byte. Explicit migration and flat graph import remain
blocked.

## 8. Exact L0-TM-03 ownership

### 8.1 Existing files writable

```text
src/main/java/com/mathmod/registry/ModDataComponents.java
src/main/java/com/mathmod/program/ProgramStorage.java
```

Restrictions:

- `ModDataComponents` may only add the accepted scoped-source declaration;
- no existing component id/codec/network behavior changes;
- `ProgramStorage` may centralize atomic transitions and add package-private
  functional infrastructure entry points;
- existing public signatures remain unchanged;
- no new public functional inscription API is authorized.

### 8.2 Authorized new production/internal files

```text
src/main/java/com/mathmod/program/ScopedSourceEnvelope.java
src/main/java/com/mathmod/program/ScopedSourceWireCodec.java
src/main/java/com/mathmod/program/ScopedSourceRead.java
src/main/java/com/mathmod/program/ScopedCommitAuthority.java
src/main/java/com/mathmod/program/ScopedCommitResult.java
src/main/java/com/mathmod/program/ScopedProgramComponentTransaction.java
src/main/java/com/mathmod/program/ScopedProgramPersistence.java
src/main/java/com/mathmod/program/ScopedFunctionalInscriptionService.java
```

All new types are package-private except the immutable envelope and the minimum
visibility required by `ModDataComponents`. Public visibility is not permission
to expose a supported integration API.

### 8.3 Authorized tests

```text
src/test/java/com/mathmod/program/ScopedSourceEnvelopeTest.java
src/test/java/com/mathmod/program/ScopedSourceWireCodecTest.java
src/test/java/com/mathmod/program/ScopedProgramComponentTransactionTest.java
src/test/java/com/mathmod/program/ScopedProgramPersistenceTest.java
src/main/java/com/mathmod/program/L0ScopedSourcePersistenceGameTests.java
docs/handoffs/L0_TM_03_HANDOFF.md
```

`L0ScopedSourcePersistenceGameTests` is test-only despite residing in the main
source set for NeoForge discovery. It adds no reusable production authority.

### 8.4 Read-only

```text
src/main/java/com/mathmod/language/**
src/main/java/com/mathmod/runes/ProgramGraph.java
src/main/java/com/mathmod/runes/RuneRegistry.java
src/main/java/com/mathmod/program/ScopedServerCompile*.java
src/main/java/com/mathmod/program/ScopedCompileCancellation.java
src/main/java/com/mathmod/program/ProgramResources.java
src/main/java/com/mathmod/program/ProgramCosts.java
src/main/java/com/mathmod/program/ProgramTiers.java
src/main/java/com/mathmod/program/ProgramNames.java
src/main/java/com/mathmod/program/InscriptionResourcePolicy.java
src/main/java/com/mathmod/program/GuidedWorkspaceState.java
src/main/java/com/mathmod/program/GuidedWorkspacePersistence.java
src/main/java/com/mathmod/program/ProgramSurface.java
src/main/java/com/mathmod/program/ProgramSurfaceMode.java
src/main/java/com/mathmod/knowledge/**
src/main/java/com/mathmod/kubejs/**
src/main/java/com/mathmod/item/**
src/main/java/com/mathmod/screen/**
src/main/java/com/mathmod/client/**
src/main/java/com/mathmod/network/**
src/main/resources/**
```

### 8.5 Forbidden

- `ProgramGraph` or execution semantic changes;
- Guided state/schema changes;
- client, menu, item-use or network route;
- StreamCodec or source Data Component synchronization;
- source repair, automatic compile or read-time mutation;
- external loader, KubeJS/datapack source builder or API;
- public mutable functional editor/API;
- background task, retry or last-known-good cache;
- theorem/content/localization/UI work.

Stop and escalate if strict streaming decode requires an unlisted dependency,
if a real item transaction cannot be proven through the authorized GameTest
source, if rollback needs an unlisted component owner, or if source/result
binding cannot remain inside the coordinator.

## 9. Required evidence

### 9.1 Focused unit vectors

All adversarial vectors in section 13 of the wire contract are mandatory,
including:

- 262,144/262,145 outer bytes;
- defensive copying and byte-content equality/hash;
- future/current-unreadable byte preservation;
- UTF-8, BOM, empty/truncated/trailing/multi-root JSON;
- duplicate/missing/extra/wrong-type fields;
- every type/expression tag and unknown tags;
- deterministic canonical encode;
- every 256/257, 255/256, 16/17, 64/65, 4/5, 1,024/1,025,
  Java-length/UTF-8-byte, budget and parameter-index boundary;
- no trim/default introduced by model constructors;
- read classifications and physical Guided/source conflict;
- no compile or mutation on read;
- source/result binding cannot be separated;
- target/request/generation/knowledge/material recheck failures mutate nothing;
- exact resource-preservation rule;
- fault injection around every logical component application restores the
  complete six-component state and source bytes;
- successful functional, Guided, graph-only and clear transitions.

Focused tests must use no build-cache reuse.

### 9.2 Dedicated GameTests

`L0ScopedSourcePersistenceGameTests` must prove on the real registered
`ItemStack`/Data Component path:

1. functional success writes graph/source/name/resources and clears current and
   legacy Guided data;
2. Guided success and graph-only success each remove scoped source;
3. valid, unreadable and future source reads preserve the authoritative graph
   and perform no rewrite;
4. physical Guided/source conflict is independent of validity;
5. item copy plus server item-codec save/load roundtrip preserves unreadable and
   future envelope bytes exactly;
6. injected commit failure restores exact component presence/value and leaves
   the old graph executable;
7. stale target/request/generation/knowledge/material state mutates nothing.

Use the existing generated `empty` fixture and normal
`runGameTestServer --no-daemon`. No GameTest structure, build script or run
configuration change is authorized.

This evidence proves the dedicated server and server persistence boundary. It
does not claim a real client reconnect or network visibility; the source
component is intentionally not synchronized.

### 9.3 Standard verification and handoff

Required commands:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache <focused filters>
.\gradlew.bat runGameTestServer --no-daemon
.\gradlew.bat build
```

The handoff must include:

1. result and implementation summary;
2. exact production/test/documentation changed files;
3. envelope/component identity and explicit network exclusion;
4. strict parser and canonical encoder design;
5. complete focused vector count/result;
6. named L0 GameTests plus total discovered result;
7. standard build result;
8. transition and rollback evidence;
9. proof of no read-time mutation/migration;
10. limitations, unclaimed real-client/reconnect behavior and escalations.

Required output:

```text
docs/handoffs/L0_TM_03_HANDOFF.md
```

## 10. Gate transition

This document completes `L0-SOL-04` with `ACCEPT`.

```text
L0-SOL-04 DONE (ACCEPT)
    -> L0-TM-03 READY
```

`L0-TM-04`, `L0-LU-01` and `L0-TM-05` remain blocked. Completion of
L0-TM-03 does not authorize a client projection or functional editor; Sol must
accept its handoff and issue the next exact projection/content readiness.
