# L0 Scoped Source Persistence and Compile Contract

**Task:** task 10 / `L0-SOL-01`  
**Date:** 2026-07-26  
**Owner:** Sol  
**Decision:** `ACCEPT`  
**Scope:** authority, persistence semantics, compilation, atomicity,
boundedness, reload, projection, sequencing, and ownership

## 1. Purpose

This contract resolves the eight Sol decisions raised by
`docs/L0_SCOPED_LANGUAGE_GAP_AUDIT.md`.

It turns the existing scoped-language classes into an approved architectural
boundary for future implementation. It does not make the current
`ScopedProgramLowerer` an inscription route and does not approve a persistent
or network format.

The current executable authority remains:

```text
ProgramGraph
```

A scoped source is an optional authoring artifact that may produce a candidate
graph. It is never executable, never trusted from a client, and never allowed
to replace a valid graph merely because it decodes.

## 2. Inputs and precedence

This decision consumes:

- `docs/L0_SCOPED_LANGUAGE_GAP_AUDIT.md`;
- `docs/P4_FUNCTION_LANGUAGE_CONTRACT.md`;
- `docs/P4_SEMANTIC_REVIEW.md`;
- `docs/FUNCTIONAL_LANGUAGE.md`;
- `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`;
- current `src/main/java/com/mathmod/language/**`;
- current `ProgramStorage`, `ModDataComponents`, `RuneRegistry`,
  `ProgramValidator`, and `ProgramExecutionPolicy` as read-only inputs.

Precedence:

1. this contract resolves the eight open L0 audit decisions;
2. the P4 typing, De Bruijn, purity/tail, and graph-authority rules remain
   frozen unless explicitly refined here;
3. existing `ProgramGraph`, execution policy, resource, knowledge, and
   inscription contracts remain authoritative;
4. no Java record name, constructor field, or current internal version constant
   is automatically a persistence or wire-format decision.

## 3. Non-goals and unapproved details

This contract does not approve:

- a Data Component id or declaration;
- codec field names, variant tags, discriminators, ordering, or defaults;
- a persistent source schema version;
- a StreamCodec or payload;
- an envelope byte limit;
- client-to-server source transport;
- KubeJS/datapack source builders;
- textual DSL syntax or source-map format;
- non-NUMBER literal descriptors;
- collection combinators;
- effect-plan purity reclassification;
- a functional editor or gameplay theorem.

`ScopedProgramSource.CURRENT_VERSION == 1` is an internal in-memory model
version. It is not approval to persist or transmit schema version 1.

The designated wire-format contract slice must choose all such details
explicitly before persistence or networking code starts.

## 4. Frozen authority model

### 4.1 Authorities

| Concern | Authority |
|---|---|
| Castable/executable program | persisted `ProgramGraph` |
| Rune signatures, enabled state, purity, and executors | one server-owned immutable rune snapshot |
| Functional authorship | optional scoped source, only as an editable candidate |
| Type/purity/reduction/lowering result | authoritative server compile attempt |
| Budget and graph validity | existing graph validator and executable policy |
| Resource requirements/selections | existing server resource policy recomputed from the candidate graph |
| Knowledge admission | existing server knowledge authority |
| Name | existing shared program-name authority; not duplicated inside source |
| Client editor | proposal and presentation only |

No client-provided type, purity, cost, graph, generation, compile-step count,
resource plan, or success claim is trusted.

### 4.2 Source and graph states

| State | Execution | Functional editing | Mutation on read |
|---|---|---|---|
| Graph only | Graph executes normally. | Unavailable until explicit import. | None |
| Graph + valid current source | Graph remains executable authority. | Source may reopen as an authoring candidate. | None |
| Graph + malformed/future source | Graph executes normally. | Disabled; diagnostic and bounded recovery/export only. | None |
| Graph + source that recompiles differently | Persisted graph executes. | Source is marked stale/conflicting and cannot silently replace the graph. | None |
| Source without valid graph | Not executable. | Recovery/export and explicit compile only. | None |
| Neither | Existing empty-item behavior. | New source may be created explicitly. | None |

Source-only data never triggers compile, graph repair, resource calculation, or
item rewrite during load, tooltip, render, sync, reconnect, or inspection.

### 4.3 Guided/source coexistence

Guided workspace and scoped source are alternative authoring projections over
one authoritative graph.

- A successful functional inscription writes the new graph and scoped source
  and removes current and legacy Guided authoring metadata in the same commit.
- A successful Guided or graph-only inscription after L0 persistence exists
  must remove the scoped source in the same commit.
- Name and resource components remain shared and are not duplicated in either
  authoring projection.
- If foreign, old, or manually edited data contains both Guided workspace and
  scoped source, neither projection receives silent precedence. The graph
  remains executable; both authoring projections are read-only/conflicted until
  the player explicitly reinscribes through one mode.
- Reads never resolve the conflict by deleting either representation.

The persistence slice must centralize these transitions. It may not add an L0
write beside the current piecemeal `ProgramStorage.saveValidated*` behavior and
leave the opposite authoring mode unaware.

## 5. Atomic compile and commit

### 5.1 Attempt lifecycle

One authoritative server attempt is:

```text
bounded decode
  -> structural validation
  -> registry/purity/type validation
  -> sharing-preserving reduction/lowering
  -> ProgramGraph validation
  -> executable policy
  -> resource and knowledge admission
  -> immutable commit plan
  -> one atomic item commit
```

Every stage before commit is pure with respect to the player, item, world, and
persistent authoring state.

### 5.2 Commit set

A functional inscription commit treats these values as one semantic unit:

- compiled graph;
- scoped source;
- shared authored name;
- resource selections/recommendations;
- current Guided workspace component;
- legacy Guided action/name fields affected by the mode transition.

The source must not carry an independent copy of the name or resources.

### 5.3 Commit rule

Before mutation, the server must:

1. capture the exact old values of every component in the commit set;
2. construct the complete candidate component state off-item;
3. validate graph, executable policy, resources, knowledge, target item, and
   captured registry generation;
4. apply the complete candidate once;
5. restore the exact old component state if any application step throws or
   fails.

No observable intermediate state may contain:

- new source with old graph;
- new graph with old or absent source;
- cleared Guided data before source/graph success;
- new name/resources with old graph;
- source or graph from different registry generations.

`ProgramStorage.saveValidated` and `saveValidatedCustom` are not the functional
commit API. They may be reused internally only after a later implementation
proves the whole commit set remains atomic; calling them piecemeal is
forbidden.

### 5.4 Failure and rollback

Any failure through resource/knowledge validation produces:

- no item mutation;
- no player-data mutation;
- no graph/source/name/resource/Guided partial write;
- no partial graph returned as active;
- stable diagnostics for the candidate attempt.

A commit-time exception restores the exact captured state and reports
`COMMIT_FAILED`. Java exception text is log detail, not diagnostic identity or
player protocol.

Disconnect, menu close, stale request, registry-generation change, or item-slot
change before commit invalidates the attempt. It does not transfer the commit
to another item and does not retry invisibly.

## 6. Effect-plan purity decision

The existing first-generation classification is retained:

- all runes currently classified `EFFECT`, including inert-looking
  effect-plan constructors, remain `EFFECT` for L0;
- an effect may appear only as the final first-order tail;
- effects remain forbidden in lambda bodies, `let` values, application
  function/argument positions, and nested rune arguments;
- no accepted source may construct, return, discard, duplicate, or capture an
  effect plan inside reusable functional code.

This is intentionally conservative. The first L0 theorem must use pure
functions/observations feeding an already valid terminal effect route; it may
not require effect-plan values inside lambdas.

Reclassifying an effect-plan constructor as a pure inert value belongs to a
later F0 semantic review. That review must distinguish plan construction from
plan execution, freeze the plan type and resource behavior, and prove that
reduction cannot duplicate or discard world effects. It is not authorized by
this contract.

## 7. Compile-step accounting and diagnostics

### 7.1 Limit and identity

The existing maximum remains:

```text
MAX_EVALUATION_STEPS = 4_096
```

The stable diagnostic code for exceeding it is:

```text
COMPILE_STEP_LIMIT
```

The code describes authoritative source compilation, not runtime spell
execution. It is an internal stable diagnostic identity; localization maps the
code to player text later.

Exactly 4,096 charged steps may succeed. The attempted charge that would
produce step 4,097 fails the entire attempt with `COMPILE_STEP_LIMIT`.
Compilation does not truncate, approximate, or return a partial graph.

### 7.2 Meter ownership

- One monotonic meter belongs to one server compile attempt.
- The client cannot supply or reset it.
- All compiler stages share the same meter.
- Failure or cancellation destroys the meter; it is never reused across
  attempts.
- Integer arithmetic is checked/saturating for reservations so overflow fails
  rather than wrapping.

### 7.3 Charged logical events

Charging is defined by logical work, not incidental helper calls:

| Event | Charge |
|---|---:|
| Visit one AST node during structural validation | 1 |
| Visit one AST node during registry/purity/type checking | 1 |
| Enter one expression during authoritative reduction/lowering | 1 |
| Create one closure/administrative binding | 1 |
| Perform one function application/beta-to-let activation | 1 |
| Resolve and canonicalize one literal | 1 |
| Create one graph node | 1 |
| Create one graph edge | 1 |
| Future bounded combinator | reserve its contract maximum before expansion |

Decode byte/scalar/list limits run before this meter and must reject hostile
envelopes without constructing an unbounded AST.

A refactor that changes method-call count but not these logical events must not
change the charge.

### 7.4 Diagnostic ordering

Diagnostics are ordered deterministically by:

1. pipeline phase;
2. canonical structural path, using numeric list-index ordering;
3. diagnostic code.

Duplicate `(phase, path, code)` entries collapse to the first instance.
Messages and binder hints are non-semantic details and do not participate in
identity.

The future compile service must additionally reserve stable codes for decode,
unsupported version, literal invalid/unsupported, lowered graph invalid,
executable/resource/knowledge rejection, stale registry generation, and commit
failure. Exact enum additions belong to the pure/server slices, not a wire
protocol.

## 8. Trusted literal-resolution boundary

### 8.1 Authority

Literal resolution is trusted common/server Java selected by the literal's
server-checked `RuneType`.

A resolver:

- accepts a bounded decoded literal candidate;
- validates and canonicalizes its payload;
- produces a trusted first-order lowering value using existing rune identity
  and bounded constants;
- never accepts a client-selected executor, class, callback, script, graph
  fragment, or purity declaration;
- cannot access player, world, item, random, clock, filesystem, commands,
  networking, or client classes.

Literal resolution uses the same immutable rune snapshot as the rest of the
compile attempt and verifies that its selected constant rune exists, is
enabled, has the expected signature, and is executable under existing policy.

### 8.2 First slice: NUMBER only

The first implementation supports only `RuneType.NUMBER`.

NUMBER rules:

- payload length remains bounded by `MAX_LITERAL_LENGTH`;
- parsing is locale-independent;
- accepted syntax is a signed decimal form with optional fraction and decimal
  exponent;
- `NaN`, infinities, hexadecimal floating syntax, units, commas, and trailing
  tokens are rejected;
- the parsed value must be finite;
- negative zero canonicalizes to positive zero;
- the graph constant uses one deterministic finite representation;
- invalid values report `LITERAL_INVALID`;
- every other `RuneType` reports `LITERAL_UNSUPPORTED`.

The implementation slice must freeze the exact canonical finite representation
in tests before use. That representation is graph-lowering behavior, not an
approval of a source codec field.

No implicit conversion or fallback to NUMBER is allowed.

### 8.3 Later literal types

Each additional literal type requires a separately reviewed trusted descriptor
with:

- accepted grammar/value domain;
- canonicalization;
- exact target rune/signature;
- size and numeric bounds;
- diagnostics;
- replay and migration tests.

External literal resolvers and callbacks remain forbidden.

## 9. Registry snapshot and reload behavior

### 9.1 Single snapshot

At attempt start, the server captures one immutable rune-definition snapshot
and one generation token. Structure/purity/type checking, literal resolution,
lowering, graph validation, and executable validation all use that same
snapshot.

The current mutable `RuneRegistry` is not itself an approved compile snapshot.
The server compile slice must introduce a bounded immutable capture and
generation ownership before becoming an inscription route.

### 9.2 Generation check

Immediately before commit, the server compares the active registry generation
with the attempt's captured generation.

- Equal generation: commit may continue if every other precondition remains
  valid.
- Different generation: fail with `REGISTRY_GENERATION_STALE`; mutate nothing.
- No invisible retry: the client/player may initiate a new explicit compile.

Item identity/slot and relevant server admission state are also rechecked at
commit.

### 9.3 Reload

A reload publishes a complete immutable registry generation atomically or
retains the previous active generation. An in-flight compile never mixes
generations.

If a reload removes or changes a rune used only by persisted source:

- the valid persisted graph remains executable under its existing authority
  and compatibility rules;
- functional editing/compilation reports the source diagnostic;
- reading does not rewrite source or graph.

This contract does not approve an authoring-metadata external loader or reuse
A0-6 implicitly.

## 10. Sharing and CSE decision

Sharing in first-generation L0 is explicit-binding only.

- A `let` lowers its value once and reuses the same lowered value for every
  reference.
- Function application performs the reviewed administrative beta-to-let step;
  its argument lowers once.
- Two syntactically repeated expressions without a shared explicit binder
  lower independently.
- No common-subexpression elimination is performed, even for pure terms.
- Observations are never deduplicated by syntax, equality, hash, or node id.
- Two observation occurrences produce two observations; one explicitly bound
  observation reused twice produces one observation node with multiple edges.
- Effects remain terminal and are never deduplicated, shared, copied, or
  discarded.

Internal node ids and list order are not semantic test oracles. Tests compare
rune identity, purity, socket connectivity, and explicit sharing.

Future pure CSE requires a separate semantic contract covering cost,
diagnostics, floating-point behavior, registry generation, and strict
exclusion of observations/effects. It is not an optimization permitted by L0.

## 11. Inspector, source, and reduced-form projection

Three projections remain distinct:

| Projection | Meaning | Authority/persistence |
|---|---|---|
| Authored source | Player-authored scoped term with non-semantic binder hints. | Optional authoring artifact; never executable. |
| Checked/reduced form | Ephemeral server compile explanation using canonical binding structure. | Not persisted and not an authority. |
| Compiled graph | Existing first-order proof inspected by the current graph Inspector. | Executable authority. |

Rules:

- The existing graph Inspector always labels the compiled graph as
  authoritative.
- Source/reduced-form panels are read-only in their first UI slice.
- Binder name hints may improve narration but never identify variables; De
  Bruijn structure remains semantic.
- Before L1 textual source maps, diagnostics use canonical structural paths,
  not invented line/column positions.
- Raw Java exception text, internal node ids, and traversal order are not
  player protocol.
- A malformed/future/stale source cannot obscure or disable inspection of a
  valid graph.
- A source/graph mismatch displays the graph and a source-conflict diagnostic;
  it does not present the source as the origin of the active graph.
- Reduced form is an explanation of one compile attempt, not a saved canonical
  source and not input to execution.
- The client may receive a future bounded projection/diagnostic DTO only after
  its payload contract is approved.
- Graph-to-source import is explicit and flat. It may create first-order rune
  calls but must not invent lambdas, lets, binder names, or a claim of original
  authorship.

Hints, localized labels, notation, pretty-printing, and layout remain
presentation. Changing them cannot alter alpha-equivalence, compile result,
graph equality, cost, or persistence identity.

## 12. Complete failure matrix

| Failure/event | Active graph | Source/editor | Persistent mutation |
|---|---|---|---|
| Envelope/decode failure | Existing valid graph remains active. | Disabled; bounded diagnostic/recovery. | None |
| Unsupported source version | Existing valid graph remains active. | Disabled; version diagnostic. | None |
| Structure/type/purity failure | Existing state remains active. | Candidate diagnostics. | None |
| `COMPILE_STEP_LIMIT` | Existing state remains active. | Candidate rejected. | None |
| Literal invalid/unsupported | Existing state remains active. | Candidate rejected. | None |
| Lowered graph invalid | Existing state remains active. | Candidate rejected. | None |
| Executable/resource/knowledge rejection | Existing state remains active. | Candidate rejected. | None |
| Registry generation changes | Existing state remains active. | Stale-generation diagnostic. | None |
| Target item/slot changes | No different item is touched. | Attempt invalidated. | None |
| Disconnect/menu close before commit | Existing state remains active. | Attempt cancelled. | None |
| Commit application throws | Exact prior component set restored. | `COMMIT_FAILED`. | Rollback only |
| Source removed explicitly | Graph remains unless whole inscription is explicitly cleared. | Functional editing unavailable. | Explicit source removal |
| Graph absent, source present | Not executable. | Recovery/export/compile candidate. | None on read |
| Guided and source both present | Graph remains active. | Both projections conflict/read-only. | None on read |

## 13. Implementation slices and ownership

Only the first slice becomes dispatchable from this contract. Later slices
remain blocked even when an anticipated owner is named.

### L0-TM-01 — Pure Compile Hardening

**Owner:** Terra Medium  
**Status:** `READY`

Required documentation:

- this contract;
- `docs/L0_SCOPED_LANGUAGE_GAP_AUDIT.md`;
- `docs/P4_FUNCTION_LANGUAGE_CONTRACT.md`;
- `docs/P4_SEMANTIC_REVIEW.md`;
- `docs/FUNCTIONAL_LANGUAGE.md`.

Required output:

```text
docs/handoffs/L0_TM_01_HANDOFF.md
```

Required implementation/evidence:

- shared compile-step meter and `COMPILE_STEP_LIMIT`;
- deterministic diagnostic ordering/deduplication;
- trusted NUMBER-only literal resolution;
- no graph returned on any lowering issue;
- existing `ProgramValidator` validation before a graph is returned as a
  successful pure compile result;
- OBS-SHARE-1 through OBS-SHARE-6;
- TAIL-1 through TAIL-8;
- BOUND-1 through BOUND-8;
- lowering counterexamples from the audit;
- explicit-binding sharing and no CSE;
- focused tests and standard build.

Exact existing-file ownership:

```text
src/main/java/com/mathmod/language/ScopedLanguageIssue.java
src/main/java/com/mathmod/language/ScopedStructureValidator.java
src/main/java/com/mathmod/language/ScopedTypeChecker.java
src/main/java/com/mathmod/language/ScopedProgramLowerer.java
src/main/java/com/mathmod/language/ScopedLoweringResult.java
src/test/java/com/mathmod/language/ScopedStructureValidatorTest.java
src/test/java/com/mathmod/language/ScopedTypeCheckerTest.java
src/test/java/com/mathmod/language/ScopedProgramLowererTest.java
```

Authorized new pure files:

```text
src/main/java/com/mathmod/language/ScopedCompileBudget.java
src/main/java/com/mathmod/language/ScopedLiteralResolver.java
src/main/java/com/mathmod/language/ScopedRuneSnapshot.java
src/main/java/com/mathmod/language/ScopedProgramCompiler.java
src/main/java/com/mathmod/language/ScopedCompileResult.java
src/test/java/com/mathmod/language/ScopedProgramCompilerTest.java
docs/handoffs/L0_TM_01_HANDOFF.md
```

Read-only:

```text
src/main/java/com/mathmod/language/ScopedExpression.java
src/main/java/com/mathmod/language/ScopedProgramSource.java
src/main/java/com/mathmod/language/RuneTypeExpression.java
src/main/java/com/mathmod/language/ScopedLanguageLimits.java
src/main/java/com/mathmod/language/ScopedDeBruijn.java
src/main/java/com/mathmod/language/BoundedFunctionCost.java
src/main/java/com/mathmod/language/FunctionalProgramMigrationPolicy.java
src/main/java/com/mathmod/runes/**
src/main/java/com/mathmod/program/**
```

Forbidden:

```text
ProgramGraph production changes
ProgramStorage
ModDataComponents
networking/payloads
items, menus, screens, preview harness
resources/localization/Patchouli
public KubeJS/datapack APIs
codecs or persistent schema
```

Stop and escalate if the pure slice requires persistence, client code, mutable
registry changes, graph semantics changes, or a literal beyond NUMBER.
Existing public entry points must remain source-compatible; the slice may add
bounded overloads/orchestration but must not silently change callers into an
inscription route.

### L0-TH-01 — Pure Compile Semantic Review

**Owner:** Terra High  
**Status:** `BLOCKED` on accepted `L0-TM-01` handoff

Output:

```text
docs/L0_PURE_COMPILE_SEMANTIC_REVIEW.md
```

Review compile-step charging, fail-closed lowering, literal canonicalization,
effect tail, observation sharing, absence of CSE, diagnostic determinism, and
all audit vectors. Documentation-only by default.

### L0-SOL-02 — Scoped Source Wire-Format Contract

**Owner:** Sol  
**Status:** `BLOCKED` on `L0-TH-01 == APPROVE`

Output:

```text
docs/L0_SCOPED_SOURCE_WIRE_FORMAT_CONTRACT.md
```

This is the designated slice that may freeze:

- persistent schema version;
- Data Component id;
- codec variant/tag/field names;
- envelope byte/list/string limits;
- unknown/future-version preservation behavior;
- StreamCodec and payload shape, if a payload is justified;
- migration and rollback vectors.

It remains documentation-only. No codec or Data Component implementation is
authorized merely by completing it.

### L0-TM-02 — Server Compile Service and Registry Generation

**Owner:** Terra Medium  
**Status:** `BLOCKED` on accepted `L0-SOL-02` and a separate Sol readiness gate

Scope:

- immutable rune snapshot/generation;
- authoritative compile pipeline;
- final graph/executable/resource/knowledge admission;
- stale-generation cancellation;
- no item mutation.

Potential shared files, including `RuneRegistry`, remain Sol-owned and
read-only until the readiness gate assigns an exact non-overlapping file list.
This contract does not grant write ownership now.

### L0-TM-03 — Persistence and Atomic Commit

**Owner:** Terra Medium integrator  
**Status:** `BLOCKED` on accepted `L0-SOL-02` wire contract and `L0-TM-02`

Scope:

- approved codec/component only;
- graph/source/name/resource/Guided atomic commit;
- rollback;
- graph-only, malformed/future, source-only, and conflict reads;
- dedicated-server persistence and reconnect evidence.

`ProgramStorage`, `ModDataComponents`, Guided persistence, item/menu, and
network files remain Sol-owned until one exact integration ownership document
is accepted. No concurrent writer may touch them.

### L0-TM-04 — Read-only Functional Projection

**Owner:** Terra Medium integrator  
**Status:** `BLOCKED` on accepted persistence/authority gate

Scope:

- authored-source, checked/reduced, and compiled-graph distinction;
- structural-path diagnostics;
- source conflict/unavailable states;
- standard/compact keyboard and narrator evidence.

All A1 screen and preview files require a new exact ownership assignment.
Mutable functional editing is not included.

### L0-LU-01 — Functional Teaching and Bilingual Evidence

**Owner:** Luna  
**Status:** `BLOCKED` on accepted read-only projection scope and a frozen first
theorem specification

Scope:

- EN/PT-BR terminology;
- Patchouli scoped-function explanation;
- narrator/error copy;
- preview requirements;
- no semantic ids, code, schema, or public API.

### L0-TM-05 — First Gameplay Theorem

**Owner:** Terra Medium integrator  
**Status:** `BLOCKED` on accepted server, persistence, UI, and Luna gates

The theorem must visibly reuse an explicit pure function, compile to the
existing graph, end in one allowed effect tail, pass resource/knowledge policy,
and execute on a dedicated server. It must not require F0 collection
combinators or effect-plan reclassification.

## 14. Gate transitions

Current transition:

```text
L0-TH-AUDIT DONE
    -> L0-SOL-01 DONE (ACCEPT)
    -> L0-TM-01 READY
```

Blocked sequence:

```text
L0-TM-01
    -> L0-TH-01
    -> L0-SOL-02
    -> L0-TM-02
    -> L0-TM-03
    -> L0-TM-04 + L0-LU-01
    -> L0-TM-05
```

No later step becomes ready merely because an earlier implementation thread
starts. Sol advances each gate only from repository evidence.

## 15. Acceptance for this contract

`L0-SOL-01` is complete when:

- all eight audit decisions are resolved;
- graph/source/name/resource/Guided authority and rollback are explicit;
- compile-step identity and charging are frozen;
- NUMBER literal trust boundary is frozen;
- registry snapshot/reload and explicit sharing are frozen;
- inspector projections are separated;
- wire-format details remain unapproved;
- only the pure compile slice is dispatchable;
- the Delivery Board and roadmap point to this contract.

This document satisfies those conditions. `L0-SOL-01` is `DONE` with
`ACCEPT`; `L0-TM-01` is `READY`.
