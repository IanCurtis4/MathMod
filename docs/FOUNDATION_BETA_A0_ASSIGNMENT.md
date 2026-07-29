# Foundation Beta — A0 Alignment Assignment

**Status:** Cycle 1 Sol W0 completed; parallel handoffs pending

**Release window:** `0.2.x` Foundation Beta

**Architecture item:** A0 Authoring Metadata Boundary

**Delivery cycle:** Cycle 1 — Alignment

**Primary owner:** Sol

**Parallel owners:** Terra High, Terra Medium, Luna

**Source of truth:** `MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`, especially sections 7.1, 9/A0, 14, 19–23, and 27–28

**Sol W0 output:** `A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`

---

## 1. Assignment Decision

The Foundation Beta will use A0 as a **contract and characterization track**, not
as a broad implementation track.

During `0.2.x`:

- Sol freezes the authoring metadata boundary;
- Terra High audits the independent L0 language foundation and may add pure,
  schema-neutral tests;
- Terra Medium hardens the existing read-only inspector/canvas without adding
  persistence or graph mutation;
- Luna inventories terminology, documentation, previews, accessibility copy,
  and reusable assets;
- P12 survival-readiness evidence may continue independently.

The implementation of the A0 registries, external loaders, and legacy adapters
belongs to `0.3.0` after the W0 contract is accepted.

This separation resolves the apparent overlap between:

- section 7.1, which forbids major new product systems during Foundation Beta;
- section 9/A0, which makes the metadata boundary the first architecture
  priority;
- section 27, which starts the four-conversation alignment with the A0 contract.

No Cycle 1 conversation is authorized to introduce a new public authoring API,
new persistent authoring schema, or a mutable Advanced surface.

---

## 2. Product Outcome

At the end of this assignment, the team can begin the next delivery cycle
without guessing:

- which identifiers are semantic and persistent;
- which values are presentation-only;
- where Rune Form identity and expansion belong;
- how current enum-authored saves remain replayable;
- which source wins when built-in and future external metadata collide;
- which files and sensitive boundaries have one writer;
- what each conversation may implement now and what must wait.

For the player, Foundation Beta remains a trust release. Existing Guided proofs
must retain the same graph, formula, category, icon, parameter behavior, cost,
and execution result.

---

## 3. Current Characterized Baseline

The following baseline is already present and must be treated as compatibility
input:

1. `ProgramGraph` is the sole executable proof.
2. `GuidedWorkspaceState` schema version 1 persists a bounded list of encoded
   Rune Form invocations separately from the graph.
3. Guided reopening is allowed only when exact replay equals the authoritative
   graph.
4. `CustomSpellAction` currently owns several concerns at once:
   - a stable namespaced form id derived from the enum name;
   - translation key;
   - icon rune id;
   - compact notation;
   - typed numeric parameter declarations;
   - category membership;
   - input and output descriptions;
   - the legacy expansion choice used by `CustomSpellWorkspace`.
5. `CustomSpellInvocation` encodes the form id and its parameter values.
6. Legacy names and enum ordinals still have compatibility paths and tests.
7. `RuneProgrammerScreen` currently iterates enum and category ordering directly.
8. `RuneDefinition` and `RuneRegistry` own executable rune semantics. They are
   not presentation registries.
9. KubeJS may currently replace executable rune definitions by id. A0 must not
   silently inherit that collision behavior for presentation or Rune Forms.
10. `ProgramSurfaceMode` has no mutable `ADVANCED` mode, and Cycle 1 must not add
    one.

This assignment does not declare the current coupling desirable. It declares it
observable and migration-sensitive.

---

## 4. W0 Contract Questions Owned by Sol

Sol must close the following questions in
`A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md` before Cycle 2 implementation.

### 4.1 Identity

- Confirm that a rune presentation is addressed by the rune's existing
  namespaced semantic id and does not create a second persistent rune identity.
- Confirm that each Rune Form has its own stable namespaced id.
- Define the canonical mapping from every current `CustomSpellAction` to a Rune
  Form id without changing existing encoded invocation strings.
- Define alias handling for legacy enum names and ordinal-era data.
- State that translation keys, glyphs, formula text, icons, categories, layout,
  sort order, and enum ordinals are never semantic identity.

### 4.2 Data model

Define the minimum immutable contracts for:

- `RunePresentationDescriptor`;
- `RuneFormDefinition`;
- `RunePresentationRegistry`;
- `RuneFormRegistry`;
- typed parameter descriptors;
- category ids;
- formula/layout templates;
- legacy adapter lookup results and diagnostics.

The contract must distinguish:

- executable rune semantics;
- presentation metadata;
- Guided form identity;
- Guided form expansion;
- notation-profile projection;
- client layout state.

### 4.3 Authority and safety

- Registries may describe and select authoring projections; they may not
  authorize casting or item writes.
- Server-side graph validation remains mandatory after Rune Form expansion.
- A presentation descriptor may not change rune inputs, output type, purity,
  budget, resources, executor key, or enabled state.
- A Rune Form may expand only through a bounded declarative template or a
  trusted built-in adapter approved by the contract.
- No datapack or KubeJS callback may execute during form expansion, compilation,
  casting, reading, login, or migration.
- Missing or malformed optional presentation metadata must not disable an
  otherwise valid executable graph.
- Missing or malformed Rune Form metadata must make Guided editing unavailable
  rather than partially replaying or repairing a proof.

### 4.4 Source precedence and collisions

Freeze separate precedence policies for:

- built-in descriptors;
- datapack descriptors;
- KubeJS declarations, if exposed in a later slice;
- server snapshot sent to clients;
- resource-pack-only localized assets.

For every registry, specify:

- whether replacement is allowed;
- which namespaces a source may define or override;
- whether equal duplicate definitions are accepted;
- whether conflicting duplicates reject one entry or the entire snapshot;
- how deterministic ordering is produced;
- how reload failure preserves or replaces the last known-good snapshot;
- which diagnostics are logged and exposed to pack authors.

Cycle 1 must not assume that `RuneRegistry.registerOrReplace` is the A0 policy.

### 4.5 Persistence and migration

- A0 metadata does not enter `ProgramGraph`.
- Cycle 1 adds no persistent data.
- Current `GuidedWorkspaceState` version 1 remains readable and writable during
  Foundation Beta.
- Migration must preserve the exact invocation sequence and authoritative graph.
- Unknown form ids fail closed for Guided editing while leaving a valid graph
  inspectable and executable.
- The contract must state whether Cycle 2 can use an in-memory adapter without a
  schema bump, and what future event would require `GuidedWorkspaceState`
  version 2.
- A presentation-only reload must not rewrite saved items.

### 4.6 Boundaries with later epics

Freeze these exclusions:

- A1 owns mutable graph working copies, history, edge mutation, output changes,
  and future Advanced workspace persistence.
- D0 may reorder or recommend forms by Discipline but may not change form or
  rune identity.
- S0 projects alternate notation from controlled layout data but may not change
  lookup, graph equality, or execution.
- L1 renders and parses source that targets `ScopedProgramSource`; it does not
  execute presentation templates.
- L0 source codecs and atomic source/graph writes are independent of A0
  presentation persistence.

---

## 5. Shared Cycle 1 Invariants

All four conversations must preserve:

- exact `ProgramGraph` equality for every existing Guided replay;
- existing persistent rune and Rune Form ids;
- `GuidedWorkspaceState.CURRENT_VERSION == 1`;
- the existing client/server authority boundary;
- current inscription atomicity;
- current execution allowlist;
- bounded payload and graph limits;
- EN/PT-BR behavior where a touched player-facing surface already supports it;
- inspector and Guided operation when A0 metadata is absent.

The following are forbidden in Cycle 1:

- adding `ADVANCED` to `ProgramSurfaceMode`;
- adding canvas metadata to `ProgramGraph`;
- adding a new Data Component for authoring metadata;
- changing `program_guided_workspace` encoding;
- changing a public id or enum ordinal;
- adding mutable graph networking;
- adding arbitrary callbacks or executor registration;
- making presentation metadata a prerequisite for execution;
- introducing the L1 grammar;
- implementing Discipline state;
- editing the same schema or sensitive boundary from two conversations.

---

## 6. Conversation Assignments

Each conversation must use the handoff structure from section 22 of the roadmap.
Chat history is not an accepted deliverable.

### 6.1 Conversation 1 — Sol

#### Task

Produce the A0 authoring metadata and extension-boundary contract.

#### Owner and window

- Owner: Sol
- Window: W0

#### Exact deliverables

1. Decision summary.
2. Presentation identity model.
3. Rune Form registry model.
4. Legacy enum adapter policy.
5. Source precedence and collision table.
6. Migration and failure behavior.
7. Exact boundaries with A1, D0, S0, L0, and L1.
8. Dependency graph.
9. File ownership proposal for Cycle 2.
10. Staged implementation plan.
11. Acceptance matrix.
12. Explicit deferrals.
13. Start/stop gate for Cycle 2.

#### Owned files

- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md` (new)
- `docs/FOUNDATION_BETA_A0_ASSIGNMENT.md`

#### Read-only files

- `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`
- `docs/P2_MODE_PERSISTENCE_CONTRACT.md`
- `docs/ADVANCED_EDITOR.md`
- `src/main/java/com/mathmod/program/**`
- `src/main/java/com/mathmod/runes/**`
- `src/main/java/com/mathmod/client/screen/**`
- `src/main/java/com/mathmod/kubejs/**`

#### Forbidden files

- all production Java and resource files during W0;
- existing persistence codecs;
- existing network payloads;
- existing public ids.

#### Acceptance

The contract is accepted only when every Definition of Ready item is answered
and the other three assignments can proceed without inventing persistence,
authority, identity, precedence, or migration behavior.

#### Stop conditions

Escalate instead of assuming when:

- current saved data contradicts the proposed canonical form id;
- external authoring definitions would require runtime callbacks;
- a precedence rule would replace executable rune semantics indirectly;
- the contract would require a `GuidedWorkspaceState` schema bump in `0.2.x`;
- A0 and L0 would need to write the same Data Component or codec.

---

### 6.2 Conversation 2 — Terra High

#### Task

Audit the L0 scoped-language implementation gap and adversarial behavior without
freezing an unapproved schema.

#### Owner and window

- Owner: Terra High
- Window: W1 audit running in parallel with A0 W0

#### Exact deliverables

1. Implemented-versus-missing matrix for AST, validation, checking, lowering,
   codecs, persistence, server integration, inspection, and gameplay.
2. Codec schema recommendation clearly marked as proposed.
3. Literal-lowering generalization recommendation.
4. Checker diagnostic gaps.
5. Lowering counterexamples.
6. Observation-sharing test vectors.
7. Effect-tail test vectors.
8. Bounded compile-step test vectors and limits.
9. Atomic source/graph failure matrix.
10. Decisions requiring Sol approval.
11. Terra Medium handoff for later L0 implementation.

#### Owned files

- `docs/L0_SCOPED_LANGUAGE_GAP_AUDIT.md` (new)
- pure L0 test files only when they are schema-neutral and do not overlap
  another conversation's files.

#### Read-only files

- `src/main/java/com/mathmod/language/**`
- `src/main/java/com/mathmod/runes/**`
- `src/main/java/com/mathmod/program/ProgramStorage.java`
- `src/main/java/com/mathmod/registry/ModDataComponents.java`
- functional-language contracts and semantic reviews in `docs/`

#### Forbidden files

- `GuidedWorkspaceState`;
- `CustomSpellAction`;
- `ProgramSurfaceMode`;
- `ProgramGraph` schema;
- Data Component registrations;
- networking;
- production codec implementation;
- A0 production packages.

#### Required evidence

- exact class and test references for every matrix row;
- counterexamples expressed as minimal ASTs or executable test vectors;
- explicit maximum-size or maximum-step assumptions;
- no claim that a proposed schema has Sol approval.

#### Stop conditions

Escalate instead of inventing behavior when:

- purity or effect-tail rules are ambiguous;
- observation sharing changes graph semantics or cost;
- a codec choice creates a persistent public format;
- atomicity requires changing inscription behavior;
- a test would freeze field names, tags, or schema versions.

---

### 6.3 Conversation 3 — Terra Medium

#### Task

Deliver an A1 robust read-only canvas vertical slice using the existing
authoritative graph and inspector data.

#### Owner and window

- Owner: Terra Medium
- Window: bounded W2/W3 hardening slice with no persistent or mutable boundary

#### Exact deliverables

1. Logical canvas coordinates derived independently from screen pixels.
2. Bounded pan and zoom.
3. Named input sockets.
4. Edge input labels.
5. Keyboard focus traversal and focused-node reveal.
6. Narration for the new focus and socket information.
7. Viewport tests, including the ATM10 target matrix already required by P12.
8. Build verification.
9. Implementation report and Luna handoff.

#### Owned files

- existing read-only graph/inspector presentation classes under
  `src/main/java/com/mathmod/client/screen/`;
- corresponding tests under `src/test/java/com/mathmod/client/screen/`;
- preview harness cases only when file ownership is explicitly uncontested.

The conversation must declare the exact file list before editing. A package glob
does not authorize unrelated screen rewrites.

#### Read-only files

- `ProgramGraph`;
- `ProgramNode`;
- `ProgramEdge`;
- `RuneDefinition`;
- `RuneRegistry`;
- `ProgramSurface`;
- `ProgramSurfaceMode`;
- `docs/ADVANCED_EDITOR.md`;
- `docs/P2_MODE_PERSISTENCE_CONTRACT.md`;
- this assignment and the roadmap.

#### Forbidden files

- all codecs and Data Components;
- `CustomSpellAction` and `GuidedWorkspaceState`;
- network payloads;
- `ProgramGraph`, graph equality, and execution policy;
- mutable graph or item-write services;
- new A0 registry implementation.

#### Required tests

- deterministic layout for the same graph;
- pan and zoom clamping;
- focused node remains revealable after viewport changes;
- named sockets map to exact `RuneInput` names;
- edge labels preserve `ProgramEdge.inputName`;
- keyboard and pointer selection reach the same read-only node;
- small and maximum supported viewport coverage;
- no graph mutation and no persistence side effect.

#### Acceptance command

Run the focused client-screen tests and the repository's standard test/build
tasks. Report exact commands and results; do not infer success from compilation
alone.

#### Stop conditions

Escalate instead of inventing behavior when:

- the slice requires `ADVANCED` persistence;
- logical coordinates would enter `ProgramGraph`;
- the client would need to authorize a graph change;
- A0 descriptors become required before the current fallback presentation works;
- another conversation owns the same screen, preview harness, or test file.

---

### 6.4 Conversation 4 — Luna

#### Task

Produce the authoring terminology, documentation, asset, and acceptance
inventory using only current behavior and frozen ids.

#### Owner and window

- Owner: Luna
- Window: W1 inventory in parallel; no semantic implementation

#### Exact deliverables

1. EN/PT-BR terminology table for Guided, Inspector, Advanced, Source,
   Function, Discipline, and Notation.
2. Current Patchouli pages that require future updates.
3. Preview matrix and missing cases.
4. Icon and glyph reuse audit.
5. Missing or inconsistent translation keys.
6. Narrator and accessibility-copy gaps.
7. Stale or contradictory documentation.
8. Proposed Luna work packages grouped by their frozen-id dependency.
9. Handoff to Sol and Terra Medium.

#### Owned files

- `docs/A0_AUTHORING_TERMINOLOGY_AND_EVIDENCE_INVENTORY.md` (new)

Inventory findings must be recorded in the report first. Cycle 1 does not
authorize broad localization, Patchouli, texture, or preview rewrites.

#### Read-only files

- `src/main/resources/assets/mathmod/lang/**`;
- `src/main/resources/assets/mathmod/patchouli_books/**`;
- `src/main/resources/assets/mathmod/textures/gui/runes/**`;
- current preview harness and UI tests;
- authoring and progression documentation;
- current screen and program models.

#### Forbidden files

- Java semantics;
- persistent ids;
- codecs and network payloads;
- graph and Rune Form expansion;
- new Discipline or notation behavior;
- production A0 registries.

#### Required evidence

- exact file/key reference for every finding;
- whether the issue affects EN, PT-BR, or both;
- current versus proposed terminology kept in separate columns;
- asset reuse recommendations tied to existing resource ids;
- no invented semantic definition.

#### Stop conditions

Escalate instead of inventing behavior when:

- EN and PT-BR imply different semantics;
- a rename would change a stable id;
- a glyph is being treated as semantic lookup;
- proposed copy depends on an unresolved A0, A1, D0, S0, or L1 decision.

---

## 7. Sensitive-Boundary Ownership for Cycle 1

| Boundary | Writer | Other conversations |
|---|---|---|
| A0 contract and precedence policy | Sol | read-only review |
| Persistent ids | none | characterize only |
| `GuidedWorkspaceState` schema | none | read-only |
| `ProgramGraph` schema/semantics | none | read-only |
| `ProgramSurfaceMode` | none | read-only |
| L0 production schema | none | proposal/audit only |
| Read-only canvas implementation | Terra Medium | Sol/Terra High/Luna read-only |
| Terminology/evidence inventory report | Luna | input and review |
| P12 acceptance evidence | existing P12 owner | independent if files do not overlap |

If an exact file is needed by two writers, the work must be serialized or one
conversation must be named integrator before either edit begins.

---

## 8. Dependency and Handoff Graph

```text
Foundation Beta support classification and P12 evidence
                         |
                         v
             Cycle 1 shared baseline
              /       |        \
             v        v         v
       Sol A0 W0   TH L0 audit   Luna inventory
             \        |         /
              \       |        /
               v      v       v
                Sol resolution gate
                         |
          TM read-only canvas evidence ------+
                         |                    |
                         v                    |
              Cycle 1 acceptance             |
                         |                    |
                         v                    |
               Cycle 2 A0 implementation <---+
```

Terra Medium's read-only slice is independent of the future A0 registry as long
as it retains current fallback presentation. L0 audit is independent of A0 as
long as it does not freeze persistence. Luna may inventory current terminology
before names are approved but may only migrate production content after Sol
freezes terminology and ids.

---

## 9. Cycle 1 Acceptance Matrix

| Evidence | Sol | Terra High | Terra Medium | Luna |
|---|---:|---:|---:|---:|
| Decision and boundary report | required | escalation input | implementation constraints | terminology constraints |
| Existing Guided ids characterized | required | — | read-only | referenced |
| Exact Guided replay unchanged | contract invariant | — | regression evidence if touched | — |
| No persistent schema change | verify | verify | verify | verify |
| No public API introduced | verify | verify | verify | verify |
| L0 implemented/missing matrix | review | required | — | — |
| L0 adversarial vectors | review | required | — | — |
| Logical read-only canvas | constraint review | — | required | evidence inventory |
| Pan/zoom/socket/focus tests | — | — | required | preview gaps |
| EN/PT-BR terminology inventory | approve/defer | — | consume | required |
| Documentation/preview/asset inventory | review | — | consume | required |
| Exact handoff document | required | required | required | required |

### Cycle 1 gate

Cycle 1 closes only when:

- all four repository handoffs exist;
- no persistent authoring change was introduced;
- no public authoring API was introduced;
- the A0 contract answers all Definition of Ready items;
- overlapping file ownership is resolved;
- existing Guided saves still replay exactly;
- the current graph remains executable without optional authoring metadata;
- remaining questions are assigned to Sol rather than hidden in implementation.

Failure of the gate keeps A0 implementation out of Cycle 2. It does not block
independent P12 evidence work that respects file ownership.

---

## 10. Foundation Beta Release Relationship

This assignment contributes to `0.2.x` by characterizing:

- public Rune Form and rune ids;
- persistence and migration behavior;
- client/server authority;
- Guided replay failure behavior;
- current authoring support classification;
- narrator, viewport, documentation, and first-use evidence gaps.

It does not by itself satisfy the complete Foundation Beta exit. The release
still requires the P12 and product evidence named in section 7.1:

- dedicated server;
- reload and reconnect;
- protection and rollback;
- economy;
- narrator;
- ATM10 viewport;
- first-use observation;
- stale-documentation closure;
- honest classification of every existing system.

The Foundation Beta exit and the A0 W0 exit are related but independently
verifiable. A0 may be ready for Cycle 2 only after its contract gate; `0.2.x`
may ship only after its trust and acceptance gate.

---

## 11. Explicit Deferrals

The following are outside this assignment:

- production A0 registry classes and loaders;
- migration of UI code from enum iteration to registries;
- pack-defined Rune Form graph templates;
- KubeJS A0 API;
- mutable Advanced editor;
- Advanced workspace schema;
- L0 codecs and source Data Component;
- L1 lexer, parser, or syntax;
- Discipline attachment and rituals;
- notation profile rendering;
- changes to execution, cost, resources, or world effects;
- removal of legacy Guided fields or adapters.

These deferrals may be reconsidered only through the roadmap lifecycle and the
appropriate Sol contract.

---

## 12. Required Handoff Format

Each conversation must finish with:

```markdown
## Handoff

### Completed
- ...

### Decisions implemented
- ...

### Files changed
- ...

### Contracts referenced
- ...

### Tests and evidence
- ...

### Known limitations
- ...

### Unresolved questions
- ...

### Next owner
- Sol / Terra High / Terra Medium / Luna

### Exact next task
- ...

### Files the next owner may edit
- ...

### Files the next owner must not edit
- ...
```

The handoff must be committed to a repository document or to the task's named
report. A conversation summary alone is not sufficient project memory.
