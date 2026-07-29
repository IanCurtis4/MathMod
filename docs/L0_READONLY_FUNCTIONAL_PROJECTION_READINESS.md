# L0-SOL-05 — Read-only Functional Projection Readiness

**Date:** 2026-07-28  
**Owner:** Sol  
**Decision:** `ACCEPT`  
**Unblocks:** `L0-TM-04 — Read-only Functional Projection`

## 1. Gate basis

This readiness is based on the repository state and these accepted contracts and
gates:

- `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`;
- `docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`;
- `docs/L0_SCOPED_SOURCE_WIRE_FORMAT_CONTRACT.md`;
- `docs/L0_ATOMIC_PERSISTENCE_READINESS.md`;
- `docs/L0_TM_03_FINAL_GATE_ACCEPTANCE.md`;
- `docs/A1_READONLY_FINAL_GATE_ACCEPTANCE.md`.

`L0-TM-03` and `L0-TM-03F` are accepted. The repository now has a strict,
non-mutating persisted-source read and a server-owned compile service, but the
source component intentionally has no network synchronization. The existing
client Inspector is graph-only. A client must therefore not attempt to read,
decode, infer, or compile the persisted source locally.

This document freezes the first read-only projection route, precedence,
bounded DTO, labels, evidence and exact ownership. It authorizes no mutable
functional editing or inscription route.

### 1.1 Repository facts used

- `ModDataComponents.PROGRAM_SCOPED_SOURCE` is persistence/cache encoded and is
  not network synchronized.
- `ProgrammedTalismanItem.openProgrammer` already writes contextual data through
  the Rune Programmer menu-opening buffer.
- `RuneProgrammerMenu` currently decodes only `InteractionHand`; it has no
  source projection.
- `RuneProgrammerScreen` currently opens `RuneInspectorScreen` from a local
  `ProgramSurface`.
- `RuneInspectorScreen` and `ProgramInspectorPresentation` currently present
  the compiled graph only.
- `ModNetworking` registers only existing C2S payloads under protocol `1`; no
  projection payload exists.
- `ScopedProgramPersistence.read` supplies the accepted non-mutating source
  classification.
- `ScopedServerCompileService` supplies a stateless server compile/admission
  attempt using a captured rune generation, material definitions, knowledge
  definitions and player knowledge.
- `ScopedServerCompileResult` exposes the candidate graph and structured
  language/service issues, but no separately reduced AST. Therefore this slice
  may truthfully present a checked canonical-binding view, not claim an
  evaluator normal form.

## 2. Architectural decision

### 2.1 One server-built menu snapshot

`L0-TM-04` shall add one immutable, server-built functional projection snapshot
to the existing Rune Programmer menu-opening data.

The sequence is:

1. on the server thread, capture the exact held programmed talisman selected by
   the opening hand;
2. call `ScopedProgramPersistence.read` exactly once for source
   classification;
3. retain the captured authoritative `ProgramGraph`, if present;
4. only for `CURRENT_VALID`, perform one explicit, read-only call to
   `ScopedServerCompileService` using `KnowledgeService.get(serverPlayer)`;
5. derive authored rows, checked canonical-binding rows, structural
   diagnostics and the candidate/current-graph relation;
6. recheck the held item, hand, relevant menu-opening eligibility, live player
   knowledge, rune generation, knowledge-definition snapshot and material
   definitions;
7. completely construct and size-check one immutable projection candidate;
8. write that candidate with the hand into the existing menu-opening buffer;
9. construct the server and client `RuneProgrammerMenu` instances from that
   same snapshot and expose it read-only to the screen.

The call to `ScopedProgramPersistence.read` remains a read: it must not compile,
migrate, repair or mutate. Compilation is a separate projection-service step.
The projection service, compiler and menu-open path must not mutate the item,
inventory, knowledge, registries, resources, source, graph, Guided state or any
other game state.

All rechecks occur after the projection candidate is built and immediately
before it is accepted for encoding. A failed recheck replaces the candidate
with a bounded stale diagnostic and no authored or checked rows. There is no
retry and no cached last-known-good projection.

### 2.2 No general network payload

The approved transport is the existing server-to-client menu-opening data
channel. It is not a new `CustomPacketPayload`.

Therefore `L0-TM-04` shall add:

- no C2S request;
- no client-authored source, graph, type, purity, cost, knowledge, material or
  generation claim;
- no `playToServer` or `playToClient` registration;
- no `ModNetworking` change or protocol-version change;
- no `StreamCodec` or `networkSynchronized` codec on
  `mathmod:program_scoped_source`;
- no raw `ScopedSourceEnvelope`, source JSON, source bytes or opaque future
  payload on the wire.

The source Data Component remains persistence/cache encoded only. The bounded
projection is a disposable presentation DTO and cannot be written back,
executed or used as compile input.

### 2.3 Snapshot binding

The hand and projection are fields of one menu-opening record. The snapshot
must describe the same captured target used to obtain both source and graph.
Source state, compile-attempt state, diagnostics and graph relation are
inseparable fields of that record; callers may not assemble them from separate
reads.

The server menu receives the exact in-memory snapshot used by the opening-data
writer. The client menu receives its decoded copy. A different item, hand,
source envelope, active graph or authority recheck produces `STALE` and no
functional rows. The client never substitutes a local source claim.

The existing locally available graph remains the executable inspection
surface. When the projection reports a graph/source mismatch, graph inspection
stays available and is labeled authoritative. Functional rows are explicitly
labelled as belonging to the server snapshot and not as the origin of the
active graph.

## 3. Frozen projection model

The immutable DTO has these logical fields:

```text
schema = 1
sourceState
attemptState
graphState
graphRelation
authoredRows[]
checkedRows[]
diagnostics[]
chargedSteps
```

The names may be implemented as one record and nested enums/records, but their
meaning and cardinality are frozen here.

### 3.1 Source state

```text
ABSENT
CURRENT_VALID
CURRENT_UNREADABLE
UNSUPPORTED_VERSION
CONFLICT
STALE
```

This is the exact `ScopedProgramPersistence.read` classification, plus
`STALE` for a failed post-build binding/authority recheck. `CONFLICT` has
precedence over every source validity state. No projection layer may decode
around the persistence classification.

### 3.2 Compile-attempt state

```text
NOT_RUN
SUCCESS
LANGUAGE_REJECTED
ADMISSION_REJECTED
AUTHORITY_STALE
```

Only `CURRENT_VALID` may run the server compile service.

- `NOT_RUN`: absent, unreadable, future or conflicting source;
- `SUCCESS`: one successful server compile attempt;
- `LANGUAGE_REJECTED`: structured `ScopedLanguageIssue` result;
- `ADMISSION_REJECTED`: structured server service issue other than stale
  generation;
- `AUTHORITY_STALE`: generation or a final live-authority recheck changed.

`chargedSteps` is informational and must be zero for `NOT_RUN`. A candidate
graph is never transported as source authority or persisted by this route.

### 3.3 Graph state and relation

```text
graphState    = ABSENT | PRESENT
graphRelation = NOT_COMPARABLE | MATCH | MISMATCH
```

Relation is computed server-side against the graph captured from the same item:

- graph absent or compile unsuccessful: `NOT_COMPARABLE`;
- successful candidate exactly equals the active graph: `MATCH`;
- successful candidate differs from the active graph: `MISMATCH`.

`MISMATCH` never replaces, disables or repairs the active graph. Source-only
success remains non-executable and uses `graphState=ABSENT` with
`graphRelation=NOT_COMPARABLE`.

### 3.4 Authored and checked rows

Rows are a presentation projection, not serialized source. Each row contains:

```text
structuralPath
kind
primaryToken
secondaryToken
bindingIndex
depth
```

- `kind` is a closed enum for literal, parameter reference, rune call, rune
  argument, lambda, application, let and result;
- `primaryToken`/`secondaryToken` contain only the bounded semantic value
  needed for presentation, such as a rune id, input name, type id, literal
  spelling or binder hint;
- unused token fields are empty;
- `bindingIndex` is `-1` except for a parameter reference;
- `depth` is structural indentation, not semantic identity.

Authored rows preserve binder hints as narration aids. Checked rows exist only
after `SUCCESS`; they present the same successfully checked term with canonical
De Bruijn references (`#0`, `#1`, ...) and may show a binder hint only as
non-semantic annotation. The first slice does not claim full beta
normalization, an evaluator normal form or inferred per-node types. Its UI
heading is **Checked form — canonical binding**, not simply **Reduced source**.

Changing row labels, hints, localization or layout cannot change
alpha-equivalence, compilation, graph equality, cost or persistence.

### 3.5 Diagnostics

Each diagnostic contains only:

```text
phase
code
structuralPath
```

`phase` and `code` are closed DTO enums mapped from persistence, language,
server-admission, mismatch and stale states. Raw Java messages, exception
class names, stack traces, internal graph node ids and fabricated line/column
locations are forbidden.

Diagnostics retain the compiler/service canonical order. Projection-owned
diagnostics follow this precedence:

1. `STALE`;
2. `CONFLICT`;
3. `CURRENT_UNREADABLE`;
4. `UNSUPPORTED_VERSION`;
5. language issues;
6. admission issues;
7. graph/source `MISMATCH`.

The graph panel remains usable for every source diagnostic when a graph is
present.

## 4. Wire bounds and decode rules

The menu projection codec is separate from `ScopedSourceWireCodec` and has
these exact limits:

```text
schema version                         1
maximum encoded projection bytes       65,536
maximum authored rows                  256
maximum checked rows                   256
maximum diagnostics                    256
maximum total rows + diagnostics       768
maximum UTF-8 structural path bytes    512
maximum UTF-8 token bytes              256
maximum structural depth               16
maximum binding index                  15
maximum charged steps                  4,096
```

Before allocating any list, the decoder reads its VarInt count and rejects
negative, malformed or over-limit values. Strings use bounded UTF-8 reads with
the limits above. Enum ordinals, schema versions, depths, binding indexes and
charged steps are validated before DTO construction. Trailing bytes in the
projection frame are rejected.

The projection is encoded into a temporary bounded buffer first. Only a
successfully encoded frame of at most 65,536 bytes may be copied to the
menu-opening buffer as:

```text
projectionLength VarInt
projectionBytes[projectionLength]
```

The receiver validates `projectionLength` before allocation. Length 65,536 is
accepted; 65,537 is rejected. Encode overflow fails closed to a minimal bounded
`STALE`/projection-unavailable snapshot and never truncates rows or strings.

Malformed projection data closes/rejects the menu construction through the
normal network decode failure path. It never falls back to interpreting source
bytes and never mutates local or server state.

## 5. UI and accessibility contract

The functional Inspector is read-only and distinguishes three panels:

1. **Authored source — not executable**;
2. **Checked form — canonical binding, one server attempt, not persisted**;
3. **Compiled graph — executable authority**.

Required behavior:

- graph-only items preserve the existing graph Inspector behavior;
- valid source with no graph shows source/checked panels and an explicit
  non-executable graph-absent state;
- malformed, future and conflicting source show a bounded diagnostic and keep
  a present graph inspectable;
- compile rejection shows authored rows plus structural diagnostics and no
  checked rows;
- mismatch shows authored and checked rows, the authoritative graph, and an
  explicit conflict diagnostic;
- hints never replace `#index` in checked semantics;
- no edit, save, apply, migrate, repair, import or compile button exists;
- no panel claims that opening the Inspector persisted or changed anything.

Keyboard/narrator requirements:

- every panel selector and every focusable row is reachable in a deterministic
  Tab/Shift+Tab order;
- arrow-key row navigation remains bounded and deterministic;
- Escape returns to the exact parent screen;
- the selected panel, source/attempt state, graph authority and diagnostic
  count are narrated;
- each row narration includes panel, structural path, kind and displayed value;
- mismatch, conflict, unreadable, unsupported, stale and graph-absent states
  have explicit narration;
- no meaning relies only on color;
- both standard `1024x800` and compact `640x480` at GUI scale 2 must retain
  focus visibility and readable scroll regions.

Production copy added by Terra Medium may use temporary precise EN/PT-BR
translations for this slice. Luna owns later teaching polish, not the semantic
labels frozen above.

## 6. Exact L0-TM-04 ownership

No implementation may start outside this list.

### 6.1 Existing production files

```text
src/main/java/com/mathmod/item/ProgrammedTalismanItem.java
src/main/java/com/mathmod/screen/RuneProgrammerMenu.java
src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java
src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java
src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/main/java/com/mathmod/client/UiPreviewMatrix.java
src/main/resources/assets/mathmod/lang/en_us.json
src/main/resources/assets/mathmod/lang/pt_br.json
docs/UI_PREVIEWS.md
```

`ProgramGraphPresentation.java` is read-only. The new functional presentation
must compose with its accepted graph model rather than change graph semantics.

### 6.2 Authorized new production files

```text
src/main/java/com/mathmod/program/ScopedFunctionalProjection.java
src/main/java/com/mathmod/program/ScopedFunctionalProjectionService.java
src/main/java/com/mathmod/program/ScopedFunctionalProjectionWireCodec.java
```

These types are internal game implementation despite the Java visibility
needed across existing packages. They are not KubeJS, datapack, registry or
extension APIs and must not expose mutation callbacks or source reconstruction.

### 6.3 Existing tests

```text
src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java
src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java
src/test/java/com/mathmod/client/UiPreviewMatrixTest.java
src/test/java/com/mathmod/ServerSideIsolationTest.java
```

### 6.4 Authorized new tests and handoff

```text
src/test/java/com/mathmod/program/ScopedFunctionalProjectionTest.java
src/test/java/com/mathmod/program/ScopedFunctionalProjectionWireCodecTest.java
src/test/java/com/mathmod/screen/RuneProgrammerProjectionTest.java
src/main/java/com/mathmod/program/L0FunctionalProjectionGameTests.java
docs/handoffs/L0_TM_04_HANDOFF.md
docs/handoffs/L0_TM_04F_HANDOFF.md
```

The GameTest file was authorized by the repository-backed L0-TM-04 review in
`docs/L0_TM_04_GATE_REVIEW.md` after ordinary JUnit proved insufficient to load
the transformed Minecraft menu/buffer runtime. It is limited to the projection
codec, server snapshot/rechecks, exact no-mutation evidence and menu binding.
It may not modify or absorb the existing L0 persistence GameTests.

### 6.5 Read-only dependencies

```text
src/main/java/com/mathmod/program/ScopedProgramPersistence.java
src/main/java/com/mathmod/program/ScopedSourceRead.java
src/main/java/com/mathmod/program/ScopedServerCompileService.java
src/main/java/com/mathmod/program/ScopedServerCompileRequest.java
src/main/java/com/mathmod/program/ScopedServerCompileResult.java
src/main/java/com/mathmod/program/ScopedServerCompileIssue.java
src/main/java/com/mathmod/program/ProgramStorage.java
src/main/java/com/mathmod/language/**
src/main/java/com/mathmod/knowledge/**
src/main/java/com/mathmod/runes/**
src/main/java/com/mathmod/registry/ModDataComponents.java
```

## 7. Explicitly forbidden

```text
ProgramGraph production changes
GuidedWorkspaceState or Guided schema changes
ProgramSurfaceMode changes
ProgramStorage changes
ModDataComponents changes
Scoped source envelope, JSON codec, schema or component identity changes
network package or ModNetworking changes
CustomPacketPayload or source Data Component StreamCodec
C2S source/compile/projection request
raw source/envelope transport
mutable functional editor, save, inscription or migration
graph-to-source import
client compilation or client authority
KubeJS/datapack/public extension API
Patchouli content or gameplay theorem
```

Stop and escalate if the implementation needs any forbidden file or behavior,
cannot stay within the 65,536-byte frame, needs a second transport message,
needs to change an existing public method signature, or cannot distinguish a
server snapshot from a current client graph without inventing persistence.

## 8. Required acceptance evidence

The handoff must enumerate changed files and prove each item below against the
real delta.

### 8.1 Focused unit/integration vectors

- every source, attempt, graph and relation enum state;
- physical conflict precedence over source validity;
- graph-only, source-only, malformed-current, future, conflict, compile
  rejection, admission rejection, match, mismatch and stale snapshots;
- live `KnowledgeService` capture and post-build recheck;
- rune generation, knowledge-definition and material rechecks;
- exact held item/hand recheck;
- no compile for absent, malformed, future or conflict;
- one compile only for current-valid source;
- authored hints versus canonical `#index` checked rows;
- structural diagnostic paths and canonical ordering;
- no raw exception text or graph node id in DTO/player copy;
- source/attempt/relation inseparability;
- source, graph, name, resources, Guided state and every other component exactly
  unchanged before/after projection, including on all failures;
- list/string/enum/depth/index/step pre-allocation rejection;
- encoded lengths 65,536 accepted and 65,537 rejected;
- malformed/truncated/trailing projection frame rejected;
- encode overflow fails closed without truncation;
- server/client menu instances receive the same logical snapshot;
- no new networking registration or source component network codec;
- graph Inspector remains available for every source failure/mismatch;
- deterministic keyboard order, bounded scrolling, Escape and narrator copy;
- EN/PT-BR standard and compact preview cases;
- dedicated-server classloading isolation.

### 8.2 Mandatory commands

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.program.ScopedFunctionalProjectionTest `
  --tests com.mathmod.program.ScopedFunctionalProjectionWireCodecTest `
  --tests com.mathmod.screen.RuneProgrammerProjectionTest `
  --tests com.mathmod.client.screen.ProgramInspectorPresentationTest `
  --tests com.mathmod.client.screen.RuneInspectorScreenSourceTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.ServerSideIsolationTest

.\gradlew.bat runGameTestServer --no-daemon
.\gradlew.bat build
```

The handoff must report each focused class's test count, the names and count of
L0 GameTests separately from the global GameTest total, and preview ids with
their capture/verification evidence. A green build does not replace
contractual coverage.

## 9. Gate result and downstream

`L0-SOL-05` is `DONE` with `ACCEPT`.

`L0-TM-04` may move from `BLOCKED` to `READY` under the exact ownership and
contract above. No other downstream task becomes ready:

- `L0-LU-01` still requires an accepted read-only projection and a separately
  frozen first-theorem specification;
- `L0-TM-05` still requires accepted UI, Luna and theorem gates.

Sol shall review `docs/handoffs/L0_TM_04_HANDOFF.md` and the real repository
delta before advancing either downstream task.
