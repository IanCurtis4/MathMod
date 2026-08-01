# MathMod Product, Architecture, and Delivery Source of Truth

**Status:** Initial consolidated edition — 2026-07-24  
**Current public version:** `0.2.0-beta.1`  
**Target:** a stable, supportable, extensible `1.0.0`  
**Primary use:** align four parallel Codex conversations — **Sol**, **Terra High**, **Terra Medium**, and **Luna** — around one product and technical delivery system.

---

## 1. Purpose

This document is the operational source of truth for finishing and improving MathMod.

It consolidates:

1. the technical integration assessment for advanced authorship, Disciplines, symbology, flow, and DSL;
2. the priority model used by `PRIORITY_ASSESSMENT.md`;
3. the product and versioning path from the current beta to `1.0.0`;
4. the delivery model for four parallel agents with explicit seniority, ownership, dependencies, handoffs, and acceptance gates.

This is an **ordering and coordination document**. It does not replace lower-level contracts such as:

- `P2_MODE_PERSISTENCE_CONTRACT.md`;
- `P4_FUNCTION_LANGUAGE_CONTRACT.md`;
- `P4_SEMANTIC_REVIEW.md`;
- `ADVANCED_EDITOR.md`;
- `FUNCTIONAL_LANGUAGE.md`;
- `PROGRESSION.md`;
- `SAFETY.md`;
- P8–P15 contracts and semantic reviews.

When this document conflicts with an approved technical contract, the approved contract remains authoritative and this document must be updated.

---

## 2. Product Vision

MathMod should behave as a small, bounded, typed functional language whose programs can affect the Minecraft world.

The player experience should connect six layers:

1. **Pure computation** defines values, functions, geometry, predicates, transformations, and bounded algorithms.
2. **World observation** reads server state without mutating it.
3. **Effect planning** converts computed values into a proposed world change.
4. **Resource validation** calculates and verifies the required witnesses, catalysts, attributes, and budget.
5. **Effect execution** applies the approved result atomically and authoritatively on the server.
6. **Inspection and refinement** explain what happened and allow the player to improve the proof.

The product must avoid two failure modes:

- academic names disguising opaque fixed spells;
- unrestricted programming features bypassing bounded execution, cost, authority, or world safety.

A mathematical primitive is valuable when it supports multiple unrelated proofs.

---

## 3. The 1.0 Product Promise

`1.0.0` does not mean every mathematical idea is implemented.

It means MathMod makes a stable promise:

> A player can discover mathematical knowledge, construct a typed proof, inspect and correct it, prepare its resources, inscribe it, execute it safely, and share or reopen its authoring source without requiring knowledge of the mod's Java implementation.

A `1.0.0` release must provide:

- a complete survival journey;
- stable persistent identities;
- stable versioned authoring formats;
- server-authoritative compilation and execution;
- understandable errors;
- direct graph authorship;
- bounded functional authorship;
- a textual proof language;
- three playable Disciplines;
- built-in notation profiles;
- bounded collection and decision operations;
- documented pack extension points;
- migration, reconnect, reload, dedicated-server, accessibility, and localization evidence.

The following do **not** block `1.0.0`:

- arbitrary temporal programs;
- unrestricted events;
- persistent general-purpose memory;
- arbitrary JavaScript callbacks;
- user-defined native executors;
- full TeX;
- unlimited custom operators;
- unrestricted custom classes;
- broad permanent infusions;
- wide terrain destruction;
- force-loaded automation;
- every planned mathematical field.

The goal is **depth, reliability, and composability**, not infinite feature breadth.

---

## 4. Current Architectural Baseline

MathMod already contains a substantial foundation.

### 4.1 Executable authority

`ProgramGraph` is the sole executable proof.

It contains:

- nodes;
- edges;
- one output node;
- a budget limit.

It must remain independent of:

- canvas positions;
- zoom and pan;
- notation;
- comments;
- selected tabs;
- binder display names;
- source formatting;
- guided recipe metadata.

### 4.2 Rune model

A `RuneDefinition` already carries:

- stable id;
- typed inputs;
- output type;
- budget cost;
- material requirements;
- attribute requirements;
- tier;
- purity;
- executor key;
- constant parameters;
- enabled state.

This is already a strong base for:

- direct graph editing;
- static validation;
- server cost planning;
- pack configuration;
- functional lowering.

### 4.3 Validation

The validator already handles:

- node and edge limits;
- graph budget;
- duplicate ids;
- missing runes;
- disabled runes;
- missing inputs;
- duplicate input bindings;
- exact type mismatch;
- output selection;
- cycles.

The largest costs of future work are therefore not basic runtime execution. They are:

- authoring representation;
- persistence;
- UI;
- migration;
- source compilation;
- product teaching;
- acceptance evidence.

### 4.4 Guided authoring

The current Laboratory uses Rune Forms represented mainly through `CustomSpellAction` and persisted `CustomSpellInvocation` values.

This provides:

- deterministic expansion;
- compact controls;
- exact replay;
- simple undo;
- a stable keyboard path.

It must remain the beginner-friendly authoring mode.

Its current limitation is that categories, compact notation, parameters, and authoring behaviors remain too concentrated in Java enums and switches.

### 4.5 Inspection

A read-only graph inspector already exists.

It shows:

- an auto-arranged DAG;
- nodes and edges;
- selected node details;
- purity;
- formula;
- normalized values;
- budget;
- materials;
- attributes;
- dynamic dependencies.

This is the starting point for the advanced editor, not a disposable prototype.

### 4.6 Functional source

The repository already has:

- `RuneTypeExpression`;
- value and function types;
- `ScopedExpression`;
- literals;
- De Bruijn parameter references;
- rune calls;
- lambdas;
- applications;
- lets;
- structure validation;
- type checking;
- bounded limits;
- lowering to `ProgramGraph`.

The remaining work is primarily:

- codecs;
- complete diagnostics;
- literal generalization;
- server integration;
- atomic dual persistence;
- UI;
- gameplay acceptance;
- textual syntax.

### 4.7 Progression

`PlayerKnowledge` already stores bounded, versioned, server-owned sets for:

- materials;
- correlations;
- epiphanies;
- discoveries;
- runes;
- theorems;
- study progress.

This is suitable for permanent learned knowledge.

It must **not** become the active class/Discipline state. A separate attachment is required for active specialization.

### 4.8 Public extension boundary

KubeJS and datapack systems already establish an important safety principle:

- declarative configuration is allowed;
- runtime callbacks are not;
- executor keys must resolve to trusted Java implementations;
- source precedence and validation must be deterministic.

Future public APIs must preserve this boundary.

---

## 5. Non-Negotiable Invariants

These rules apply to every future epic.

### 5.1 Execution

- `ProgramGraph` remains the executable authority through `1.x` unless a future major version explicitly replaces it.
- Client state never authorizes execution, progression, mutation, or item writes.
- Every executable proof must be bounded.
- World effects remain explicit.
- Effects do not hide inside mathematical vocabulary.

### 5.2 Authoring

- Guided, advanced graph, functional source, and textual DSL are authoring projections.
- No authoring projection silently repairs or approximates another.
- A graph may be imported into a flat source, but lambdas and named abstractions must never be guessed from topology.
- A guided recipe is editable only when exact replay reproduces the authoritative graph.
- Advanced canvas metadata is never part of graph equality or cost.

### 5.3 Persistence

- Every persistent artifact has its own schema version.
- New persistent data fails closed when unknown or malformed.
- A valid graph remains executable when optional authoring metadata is unavailable.
- Removed definitions produce explicit diagnostics.
- Migrations preserve semantic identity, not visual representation.

### 5.4 Identity

- Rune, theorem, form, Discipline, manuscript, and notation profile identities use stable namespaced ids.
- Glyphs, labels, formulas, icons, enum ordinals, and translated names are never persistent semantic identity.
- Public ids are treated as stable now, even before `1.0.0`.

### 5.5 Safety

- No arbitrary callbacks during casting, compilation, ritual completion, reading, login, or migration.
- No unrestricted recursion.
- No graph cycles as loops.
- No unbounded iteration.
- No effectful lambda bodies in the first functional generation.
- No chunk force-loading for sustained systems.
- Terrain mutation must use explicit transactional boundaries.

### 5.6 Product

- Every major primitive must support more than one plausible use.
- Every epic must contain a player-visible vertical slice.
- Every feature must have a discovery route, success feedback, failure feedback, and teaching surface.
- A large subsystem is not complete because its classes compile.

---

## 6. Versioning Strategy

MathMod should use semantic versioning for public releases:

```text
MAJOR.MINOR.PATCH-PRERELEASE
```

Recommended artifact naming:

```text
mathmod-1.0.0-mc1.21.1.jar
```

### 6.1 Major version

Increment `MAJOR` only when a public promise is broken, for example:

- incompatible DSL without migration;
- public id removal without aliases;
- unsupported older saves;
- incompatible KubeJS API;
- a fundamentally different execution authority;
- a new concurrent or distributed runtime that invalidates the 1.x model.

### 6.2 Minor version

Increment `MINOR` for backward-compatible features:

- new runes;
- new types;
- new Disciplines;
- new notation profiles;
- new DSL constructs;
- new public declarative APIs;
- new world systems;
- material changes that meaningfully alter proof construction.

### 6.3 Patch version

Increment `PATCH` for:

- bug fixes;
- migration fixes;
- small balance corrections;
- localization;
- accessibility;
- performance;
- visual corrections;
- documentation;
- compatibility fixes.

A balance change that broadly invalidates proofs belongs in a minor release.

### 6.4 Independent schema versions

Do not couple every schema to the mod version.

Track independently:

```text
Mod version
ProgramGraph schema
GuidedWorkspace schema
AdvancedWorkspace schema
ScopedSource schema
DSL language version
PlayerKnowledge schema
DisciplineProfile schema
Datapack API version
KubeJS API version
Network protocol version
```

A future release may be:

```text
MathMod 1.3.0
ProgramGraph schema 1
DSL version 1
PlayerKnowledge schema 5
DisciplineProfile schema 2
```

---

## 7. Release Path To 1.0

## 7.1 `0.2.x` — Foundation Beta

**Product statement:** establish trust in the current product.

Primary goals:

- complete P12 acceptance evidence;
- close dedicated-server, reload, reconnect, protection, rollback, economy, narrator, ATM10 viewport, and first-use gaps;
- fix stale documentation;
- classify every existing system as survival-ready, experimental, admin-only, or disabled;
- characterize public ids and persistence behavior;
- avoid major new product systems.

**Active completion contract (2026-07-30):**
`docs/P12_FOUNDATION_BETA_COMPLETION_CONTRACT.md` accepts P12 as the next
delivery gate. Test-only `P12-TM-01` reproduced NaN-velocity and
unloaded-sweep production defects and is `NEEDS_FIX`.
`docs/P12_TM_01_BLOCKER_REVIEW.md` authorizes bounded `P12-TM-01F` as `READY`.
P12-TM-02, the real dedicated-server matrix and manual
first-use/accessibility evidence remain blocked in that order. A0-6 and
P14/P15 expansion remain deferred.

Exit:

- the current feature set has an honest support classification;
- save, authority, and migration baselines exist;
- first-spell onboarding is externally observed.

---

## 7.2 `0.3.0` — Inspectable Mathematics

**Product statement:** understand a proof before changing it.

Primary goals:

- A0 authoring metadata registry;
- data-driven presentation descriptors;
- Rune Form registry and legacy adapter;
- robust read-only canvas;
- logical canvas;
- pan and zoom;
- named sockets;
- node-local cost, type, purity, and dependency inspection;
- initial structured mathematical layout.

Exit:

- all current proofs are inspectable;
- presentation changes cannot alter execution;
- built-in guided forms no longer depend on enum ordering for persistent identity.

---

## 7.3 `0.4.0` — Direct Construction

**Product statement:** construct a primitive typed proof directly.

Primary goals:

- advanced mutable surface contract;
- typed parameter editing;
- node creation, duplication, movement, and deletion;
- typed edge creation and replacement;
- explicit output designation;
- undo/redo;
- advanced workspace persistence;
- explicit Guided-to-Advanced conversion;
- keyboard and pointer equivalence.

Exit:

- advanced proofs round-trip exactly;
- invalid local mutations never become active;
- guided proofs remain exact and safe;
- canvas state never affects execution.

---

## 7.4 `0.5.0` — Functional Proofs

**Product statement:** define an idea once and reuse it.

Primary goals:

- complete scoped-source codecs;
- complete server type/purity validation;
- generalize typed literals;
- finish lowering and adversarial tests;
- persist source and graph atomically;
- implement `let`, lambda, application, and partial application;
- inspect source, reduced form, and lowered graph;
- ship one visible reusable-function theorem.

Exit:

- no recursion;
- no effectful lambdas;
- no accidental duplication of observations;
- malformed source cannot disable a valid graph;
- bounded compile and evaluation cost.

---

## 7.5 `0.6.0` — Disciplines And Traditions

**Product statement:** become a particular kind of mathemagician.

Primary goals:

- separate `PlayerDisciplineProfile`;
- Mathematical Discipline;
- Physical Discipline;
- Computational Discipline;
- adoption rituals;
- distinct onboarding;
- progression recommendations;
- Field Ledger integration;
- built-in notation profile defaults;
- convergent cross-Discipline routes.

Exit:

- switching does not remove knowledge;
- old talismans remain executable;
- no executor contains hardcoded class checks;
- each Discipline has a complete playable route.

---

## 7.6 `0.7.0` — Proof Language

**Product statement:** write, export, and share proofs.

Primary goals:

- DSL lexer;
- parser;
- deterministic pretty-printer;
- source maps;
- textual editor;
- named rune calls;
- typed literals;
- lets;
- lambdas;
- applications;
- pipes;
- comments;
- import/export;
- LaTeX-like export.

Exit:

```text
parse(print(source)) is semantically equivalent to source
```

The requirement is semantic equivalence, not preservation of arbitrary formatting.

---

## 7.7 `0.8.0` — Bounded Programs

**Product statement:** express decisions and collection logic with visible limits.

Primary goals:

- selection;
- effect-plan selection;
- bounded map;
- bounded filter;
- bounded fold;
- bounded repeat;
- maximum-work cost;
- collection gameplay;
- non-destructive field effects where ready.

Exit:

- maximum work is known before inscription;
- callback bodies are pure;
- effects remain terminal;
- no general recursion or graph cycle exists;
- maximum configured workloads pass server evidence.

---

## 7.8 `0.9.0` — Convergence Release Candidate

**Product statement:** stop expanding and prove the product.

Freeze:

- public ids;
- core terminology;
- `ProgramGraph` semantics;
- save formats;
- DSL v1;
- KubeJS v1;
- datapack v1;
- inscription workflow;
- client/server authority boundaries;
- default execution limits.

Allowed:

- bug fixes;
- performance;
- migration;
- accessibility;
- localization;
- documentation;
- balance;
- modpack compatibility;
- content using existing primitives.

Forbidden:

- a new runtime;
- new type-system foundations;
- new temporal execution;
- unrestricted custom classes;
- arbitrary callbacks;
- broad destructive systems without already completed contracts.

Exit:

- no required `1.0` feature remains unfinished;
- release candidates focus only on stabilization.

---

## 7.9 `1.0.0` — The Convergence

The public promise begins.

Required support:

- save compatibility through 1.x;
- public id stability;
- DSL v1 stability;
- documented extension APIs;
- dedicated servers;
- reload/reconnect;
- migration;
- first-use UX;
- accessibility;
- EN/PT-BR;
- bounded failure behavior;
- explicit experimental flags.

---

## 8. Unified Development Tracks

MathMod has two top-level tracks.

### 8.1 Survival and world track

- P12: consolidation — active; `P12-TM-01F` ready;
- P13: environmental correspondence hardening;
- P14: transactional mutation;
- P15-A: entity-only field dynamics;
- P15-B: terrain effects.

### 8.2 Authoring and identity track

- A0: authoring metadata;
- A1: advanced editor;
- L0: scoped functional source;
- D0: built-in Disciplines;
- S0: notation profiles;
- L1: textual DSL;
- F0: bounded flow combinators;
- D1: custom Disciplines;
- F1: temporal flow.

These tracks may overlap only when their authority and persistence boundaries are independent.

---

## 9. Unified Priority Order

## A0. Authoring Metadata Boundary

**Goal:** remove presentation and authoring definitions from hardcoded enum ownership without changing semantic ids.

**Cycle 2 status (2026-07-26):** `ACCEPTED`. The complete gate is recorded in
`docs/A0_CYCLE_2_ACCEPTANCE.md`: 35 acceptance items are accepted, 10
external-source/migration items are deferred with named future owners, and no
item is rejected. Trusted built-in metadata, exact legacy replay,
registry-backed Guided presentation, schema-1 compatibility, bilingual
evidence, and dedicated-server isolation are complete. A0-6 external loaders,
snapshot publication/reload, and public schemas/APIs remain deferred and are
not implementation-ready.

Deliverables:

- `RunePresentationDescriptor`;
- `RuneFormDefinition`;
- `RunePresentationRegistry`;
- `RuneFormRegistry`;
- typed parameter descriptors;
- category ids;
- formula/layout templates;
- migration adapters;
- precedence and collision contract;
- characterization tests.

Benefit: **Very High**  
Cost: **Medium**  
Risk: **Medium**  
Dependency leverage: **Very High**

Blocks or simplifies:

- A1;
- D0;
- S0;
- L1 presentation;
- pack-defined guided authoring.

---

## A1. Advanced First-Order Editor

**Goal:** evolve the inspector into a direct typed graph editor.

Delivery order:

1. logical read-only canvas;
2. pan/zoom;
3. sockets and edge metadata;
4. typed parameter editing;
5. working-copy validation;
6. history;
7. node mutation;
8. edge mutation;
9. advanced persistence;
10. migration and acceptance.

Benefit: **Very High**  
Cost: **High**  
Risk: **Medium to High**  
Dependency leverage: **Very High**

---

## L0. Scoped Functional Source Completion

**Goal:** make the existing functional AST a supported persisted authoring source.

**Contract status (2026-07-29):** source authority and pure compilation are
accepted in `docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`,
`docs/L0_TM_01_FINAL_GATE_ACCEPTANCE.md`, and
`docs/L0_PURE_COMPILE_SEMANTIC_REVIEW.md`. The persistent component identity,
opaque envelope, schema 1, strict JSON payload, decode limits, future/malformed
preservation, network exclusion, migration, and rollback vectors are accepted
in `docs/L0_SCOPED_SOURCE_WIRE_FORMAT_CONTRACT.md`. Server compile ownership,
registry generation, single-snapshot admission and cancellation evidence are
accepted in `docs/L0_SERVER_COMPILE_SERVICE_READINESS.md`. The implementation
and bounded correction are accepted in
`docs/L0_TM_02_FINAL_GATE_ACCEPTANCE.md`, with 30/30 focused tests. Exact
codec/component, source/result binding, atomic item transition, rollback and
GameTest ownership are accepted in
`docs/L0_ATOMIC_PERSISTENCE_READINESS.md` and
`docs/L0_TM_03_FINAL_GATE_ACCEPTANCE.md`. The bounded read-only projection is
accepted in `docs/L0_TM_04_FINAL_GATE_ACCEPTANCE.md`. The first gameplay theorem
identity, source semantics, lowering oracle, player outcome, teaching scope and
Luna ownership are frozen in
`docs/L0_FIRST_GAMEPLAY_THEOREM_SPECIFICATION.md`. The first Luna content delta
is conforming, but its original PT-BR Patchouli captures rendered EN-US. The
bounded preview-harness correction `L0-TM-04F5` is accepted in
`docs/L0_TM_04F5_GATE_ACCEPTANCE.md`. The resulting localized captures expose
copy overflow in EN-US and PT-BR p0/p2; Luna's bounded correction and all eight
final captures are accepted in `docs/L0_LU_01_FINAL_GATE_ACCEPTANCE.md`.
The remaining boundary is accepted in
`docs/L0_INTERNAL_GAMEPLAY_INTEGRATION_READINESS.md`. The final theorem gate is
accepted in `docs/L0_TM_05_FINAL_GATE_ACCEPTANCE.md`. The F2 evidence closes
the id/order-independent semantic graph oracle and executes tooltip-equivalent
reads plus actual outbound-packet observation for failure paths. `L0-TM-05`,
F and F2 are `DONE` with `ACCEPT`; the accepted transaction machine remains
the sole commit authority and networking remains unchanged.

Delivery order:

1. persistence/compile authority contract — complete;
2. pure compile budget, NUMBER literal boundary, diagnostics, and adversarial
   tests — complete;
3. independent semantic review of the pure compiler — complete (`APPROVE`);
4. wire-format/codec contract — complete;
5. server compile service readiness, immutable registry generation and server
   service implementation — complete;
6. approved codec, optional source component and atomic
   graph/source/name/resource/Guided integration — complete;
7. persistence acceptance and projection readiness — complete;
8. read-only source/reduced/graph Inspector projection — complete;
9. first-theorem specification and bilingual teaching/evidence — complete;
10. exact internal integration-readiness amendment — complete;
11. one explicit-function gameplay theorem — complete (`ACCEPT`);
12. post-implementation bilingual content alignment — complete (`ACCEPT`) in
    `docs/L0_LU_02_GATE_ACCEPTANCE.md`.

Benefit: **Very High**  
Cost: **High**  
Risk: **High but bounded**  
Dependency leverage: **Very High**

---

## D0. Built-In Disciplines

**Goal:** add identity and specialization without corrupting permanent knowledge.

Architecture:

```text
PlayerKnowledge = permanent learned knowledge
PlayerDisciplineProfile = active specialization
```

Built-ins:

- Mathematical;
- Physical;
- Computational.

First version affects:

- onboarding;
- progression routes;
- recommendations;
- palette ordering;
- notation defaults;
- Field Ledger presentation.

Runtime modifiers come later and must reuse immutable cast-plan boundaries.

Benefit: **High**  
Cost: **Medium to High**  
Risk: **Medium**  
Dependency leverage: **High**

---

## S0. Notation Profiles

**Goal:** allow multiple presentations of the same proof.

Built-ins:

- classical mathematical;
- functional programming;
- physical/engineering;
- convergent arcane.

Use a controlled layout tree:

- symbol;
- sequence;
- grouping;
- fraction;
- superscript;
- subscript;
- radical;
- integral;
- summation;
- matrix;
- cases;
- function application.

Benefit: **High**  
Cost: **Medium to High**  
Risk: **Medium**  
Dependency leverage: **High**

---

## L1. Textual Proof DSL

**Goal:** expose `ScopedProgramSource` through stable text.

The DSL must compile to the existing AST.

It does not directly execute.

Initial features:

- proof declaration;
- types;
- literals;
- rune calls;
- named arguments;
- let;
- lambda;
- application;
- pipe;
- comments;
- namespaces;
- source maps;
- deterministic formatting.

Benefit: **Very High**  
Cost: **High**  
Risk: **High**  
Dependency leverage: **Very High**

---

## F0. Bounded Functional Flow

**Goal:** deliver useful flow semantics without an imperative runtime.

First slice:

- select;
- bounded map;
- bounded filter;
- bounded fold;
- bounded repeat;
- effect-plan selection;
- effect-plan composition.

Rules:

- maximum work is charged;
- function bodies are pure;
- effects remain terminal;
- no graph cycles;
- no unrestricted recursion.

Benefit: **High**  
Cost: **High**  
Risk: **High**  
Dependency leverage: **High**

---

## D1. Custom Disciplines And Rituals

**Goal:** permit bounded specialization composition.

First public layer should be modpack-defined, not unrestricted player creation.

Allowed modules:

- category affinity;
- starting route;
- notation profile;
- progression grant;
- bounded modifier;
- limitation;
- ritual requirement.

Forbidden:

- callbacks;
- commands;
- executor registration;
- direct player mutation;
- safety-limit increases.

Benefit: **High**  
Cost: **High**  
Risk: **High**  
Dependency leverage: **Medium**

---

## F1. Temporal Flow

**Goal:** sustained bounded programs.

This requires a separate runtime contract.

Potential concepts:

- events;
- delay;
- previous value;
- typed state;
- hysteresis;
- reevaluation;
- finite leases.

Required constraints:

- no force-loading;
- bounded memory;
- bounded duration;
- explicit ownership;
- unload behavior;
- expiry;
- versioned persistence;
- server scheduler authority.

Priority: **Deferred beyond the initial 1.0 path unless product evidence promotes it.**

---

## 10. Product Prioritization Method

Every candidate must pass mandatory gates before scoring.

### 10.1 Authority gate

- Is the decision server-owned?
- Does the client propose or authorize?
- Does it mutate the world?
- Does it execute new logic?
- Can the action be rolled back?

### 10.2 Persistence gate

- Does it create a schema?
- Does it alter ids?
- Does it require migration?
- What happens when a definition disappears?
- What happens when a future version reads it?

### 10.3 Boundedness gate

- Is maximum work known?
- Is memory bounded?
- Is collection size bounded?
- Is world scope bounded?
- Is unload behavior defined?

### 10.4 Product gate

- What player problem does this solve?
- How is it discovered?
- How is success understood?
- How is failure corrected?
- Is there a playable vertical slice?
- Does it support more than one use?

### 10.5 Rollback gate

- Can the change be independently reverted?
- Does removal preserve older saves?
- Does it share a file or protocol boundary with unrelated work?
- Is the feature behind a safe gate or configuration?

A proposal failing a mandatory gate cannot become a high-priority implementation epic.

---

## 11. Product Score

Use 1–5 ratings.

### Positive value

| Criterion | Weight |
| --- | ---: |
| Player value | 25% |
| Alignment with vision | 15% |
| Dependency leverage | 15% |
| Gameplay reach | 10% |
| Modpack value | 10% |
| Learning value | 10% |
| Estimate confidence | 15% |

### Cost and risk

| Criterion | Weight |
| --- | ---: |
| Effort | 50% |
| Authority/save/API risk | 30% |
| Irreversibility | 20% |

Conceptual score:

```text
Value =
    0.25 * player value
  + 0.15 * vision alignment
  + 0.15 * dependency leverage
  + 0.10 * gameplay reach
  + 0.10 * modpack value
  + 0.10 * learning value
  + 0.15 * confidence

Relative priority =
    Value / (
        0.50 * effort
      + 0.30 * risk
      + 0.20 * irreversibility
    )
```

The score informs judgment. It does not override dependency gates.

---

## 12. Product Investment Portfolio

Do not place every task in one undifferentiated queue.

For every ten completed delivery slices, target approximately:

| Portfolio | Slices |
| --- | ---: |
| Player journeys and vertical features | 3–4 |
| Reliability and compatibility | 2–3 |
| Architectural leverage | 2 |
| Content, lore, and presentation | 1 |
| Experiments | 1 |

This prevents:

- architecture without gameplay;
- content on an unstable foundation;
- endless safety work with no player-visible result;
- experimental systems becoming accidental commitments.

---

## 13. Four-Agent Operating Model

The four Codex conversations form one virtual team.

| Agent | Equivalent role | Primary responsibility |
| --- | --- | --- |
| Sol | Senior / staff architect | contracts, cross-system design, migration, dependency sequencing, approval gates |
| Terra High | Pleno III / senior specialist | semantic review, difficult algorithms, adversarial cases, high-risk implementation |
| Terra Medium | Pleno I / main implementer | bounded Java/NeoForge implementation, tests, integration, builds |
| Luna | Junior / delivery support | localization, Patchouli, fixtures, assets, mechanical tests, previews, documentation updates |

These roles are capability boundaries, not status labels. Each agent should stay inside its best leverage.

---

## 14. Sol Operating Contract

Sol owns decisions where a mistake causes broad rework.

### Sol should do

- define the epic boundary;
- identify sources of truth;
- map dependencies;
- freeze authority and persistence;
- define schemas and migrations;
- define API precedence;
- identify rollback boundaries;
- write exit conditions;
- identify explicit deferrals;
- review Terra High findings;
- approve handoff to implementation.

### Sol should not do by default

- broad mechanical implementation;
- large localization work;
- texture manifests;
- repetitive test fixtures;
- UI pixel adjustments;
- direct implementation before the contract is stable.

### Sol output

Every Sol task should produce:

1. decision summary;
2. invariants;
3. schema or interface;
4. dependency graph;
5. migration behavior;
6. failure behavior;
7. implementation slices;
8. acceptance matrix;
9. explicit deferrals;
10. handoff instructions.

---

## 15. Terra High Operating Contract

Terra High owns high-complexity semantic and technical work.

### Terra High should do

- mathematical semantics;
- typed-language semantics;
- compiler/reducer design;
- adversarial examples;
- counterexamples;
- transaction and balance policies;
- performance models;
- concurrency and world-safety reasoning;
- difficult core implementation;
- independent contract review.

### Terra High should not do by default

- unreviewed architecture changes;
- public API invention without Sol;
- large mechanical content batches;
- final visual-polish work.

### Terra High output

Every Terra High task should produce:

1. assumptions;
2. formal or executable semantics;
3. adversarial cases;
4. rejected alternatives;
5. complexity and cost bounds;
6. test vectors;
7. implementation recommendation;
8. issues requiring Sol resolution;
9. handoff to Terra Medium.

---

## 16. Terra Medium Operating Contract

Terra Medium is the main implementation engine.

### Terra Medium should do

- Java implementation;
- NeoForge integration;
- codecs;
- Data Components;
- networking;
- menus and screens;
- registry wiring;
- ordinary tests;
- GameTests;
- build verification;
- focused refactors;
- documentation progress updates.

### Terra Medium must not

- silently change a frozen contract;
- broaden a public API;
- combine unrelated persistence boundaries;
- invent fallback semantics;
- merge client authority into server decisions;
- claim a feature complete without acceptance evidence.

### Terra Medium output

Every Terra Medium task should produce:

1. changed files;
2. implemented contract clauses;
3. tests added;
4. build commands and results;
5. migration impact;
6. remaining gaps;
7. known limitations;
8. Luna handoff;
9. Sol escalation if a contract is insufficient.

---

## 17. Luna Operating Contract

Luna closes bounded delivery work after semantics are stable.

### Luna should do

- EN/PT-BR localization;
- Patchouli content;
- deterministic preview cases;
- icon and asset manifests;
- bounded mechanical tests;
- rejected-data fixtures;
- documentation examples;
- terminology consistency;
- accessibility copy;
- simple non-architectural refactors.

### Luna must not

- define core semantics;
- change persistent ids;
- invent server behavior;
- broaden callbacks;
- decide migration policy;
- create new authority paths.

### Luna output

Every Luna task should produce:

1. updated content list;
2. terminology used;
3. preview/test matrix;
4. missing assets or translations;
5. contract references;
6. visual or documentation gaps;
7. final acceptance evidence.

---

## 18. Epic Lifecycle

Every epic moves through seven stages.

```text
Conjecture
    ->
Product hypothesis
    ->
Sol contract
    ->
Terra High semantic review
    ->
Terra Medium vertical implementation
    ->
Luna delivery closure
    ->
Acceptance and reassessment
```

Not every epic requires all four agents at once.

The standard lifecycle is:

### Stage 0 — Conjecture

A player or system opportunity is stated.

Example:

> Players need to reuse a transformation without duplicating graph branches.

### Stage 1 — Product hypothesis

Define:

- target player;
- problem;
- expected behavior;
- measurable outcome;
- smallest useful vertical slice;
- reason this belongs in MathMod.

### Stage 2 — Sol contract

Freeze:

- authority;
- persistence;
- identity;
- dependency;
- rollback;
- implementation slices;
- exit conditions.

### Stage 3 — Terra High review

Prove:

- semantics;
- boundedness;
- counterexamples;
- cost;
- failure behavior;
- compatibility with existing contracts.

### Stage 4 — Terra Medium implementation

Implement the smallest end-to-end slice.

### Stage 5 — Luna closure

Complete:

- localization;
- teaching;
- previews;
- content fixtures;
- mechanical coverage.

### Stage 6 — Acceptance

Run:

- unit tests;
- build;
- GameTests;
- dedicated server;
- reload/reconnect where relevant;
- GUI and narrator checks;
- product journey.

### Stage 7 — Reassessment

Decide:

- expand;
- stabilize;
- defer;
- redesign;
- remove.

---

## 19. Dependency Windows

An epic is divided into windows that allow controlled parallelism.

## Window W0 — Product and contract

Owner: **Sol**  
Parallel support: Terra High may perform a read-only audit.

Outputs:

- problem statement;
- non-goals;
- dependency map;
- authority;
- persistence;
- schema;
- migration;
- risk;
- slice plan;
- exit conditions.

No implementation starts before W0 is accepted when persistence, authority, or public APIs are involved.

---

## Window W1 — Pure semantic foundation

Owner: **Terra High**  
Parallel support: Terra Medium may add characterization tests.

Outputs:

- pure model;
- type rules;
- counterexamples;
- complexity limits;
- reference operations;
- test vectors.

Allowed parallel Luna work:

- terminology inventory;
- current asset audit;
- placeholder documentation plan.

---

## Window W2 — Core implementation

Owner: **Terra Medium**  
Sol availability: escalation only.  
Terra High: review difficult portions.

Outputs:

- core Java;
- codecs;
- registries;
- server services;
- unit tests;
- graph validation;
- build pass.

Luna may begin only on stable ids and terminology.

---

## Window W3 — Integration and vertical slice

Owner: **Terra Medium**  
Parallel owner: **Luna** for presentation closure.

Outputs:

- networking;
- item/menu/screen integration;
- server authority;
- first player-visible path;
- Patchouli;
- localization;
- assets;
- previews.

---

## Window W4 — Acceptance and hardening

Owners:

- Terra Medium: runtime and GameTests;
- Luna: preview and documentation matrix;
- Terra High: ambiguous failures;
- Sol: release gate and roadmap update.

Outputs:

- evidence matrix;
- migration evidence;
- dedicated-server results;
- product journey;
- final known limitations;
- priority reassessment.

---

## 20. Interdependence Rules

### 20.1 Allowed parallelism

Safe examples:

- P12 evidence beside A0 contract;
- A0 registry implementation beside L0 codec work after identities are frozen;
- A1 read-only canvas beside D0 product/contract work;
- Luna terminology inventory beside Terra High semantic review;
- P13 observational hardening beside authoring-only UI work.

### 20.2 Forbidden parallelism

Do not run concurrently without an explicit shared contract:

- A0 enum migration and A1 mutable persistence in the same files;
- L0 source persistence and L1 grammar invention;
- D0 attachment design and D1 unrestricted custom composition;
- F0 bounded combinators and F1 temporal runtime;
- P14 terrain transaction and P15-B terrain effects;
- two agents editing the same codec or schema;
- two agents changing stable ids;
- two agents changing `ProgramSurfaceMode`;
- two agents changing inscription atomicity.

### 20.3 One writer per boundary

At any moment, exactly one conversation owns each sensitive boundary:

- schemas;
- public APIs;
- persistent ids;
- registry precedence;
- networking payloads;
- item Data Components;
- `ProgramGraph`;
- `ProgramSurface`;
- execution allowlist;
- knowledge attachment;
- future Discipline attachment.

Other agents may review but not independently modify that boundary.

---

## 21. File Ownership Protocol

Before implementation, every task declares:

```text
Owned files
Read-only files
Forbidden files
Expected new files
```

Example:

```text
Owned:
- src/main/java/com/mathmod/presentation/**
- src/test/java/com/mathmod/presentation/**

Read-only:
- src/main/java/com/mathmod/runes/ProgramGraph.java
- docs/P2_MODE_PERSISTENCE_CONTRACT.md

Forbidden:
- src/main/java/com/mathmod/network/**
- src/main/java/com/mathmod/program/ProgramStorage.java
```

When two tasks need the same file:

1. serialize the tasks;
2. split the file first;
3. assign one integrator;
4. or move the shared change into an earlier foundation task.

Never let two parallel conversations produce competing complete-file rewrites.

---

## 22. Handoff Protocol

Every handoff uses the following structure.

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

A handoff is incomplete when the next agent must reconstruct hidden context from the conversation.

The repository documents and committed code are the memory. Chat history is not the source of truth.

---

## 23. Definition of Ready

A task is ready for implementation when:

- the player outcome is stated;
- dependencies are named;
- authority is known;
- persistence impact is known;
- stable ids are known;
- limits are known;
- non-goals are explicit;
- owned files are declared;
- acceptance tests are listed;
- rollback behavior is described;
- the correct agent role is assigned.

A task involving new persistence, public APIs, world mutation, or a compiler boundary is not ready without Sol approval.

---

## 24. Definition of Done

A slice is done only when applicable evidence exists for:

- pure unit tests;
- graph validation;
- codec round-trip;
- migration;
- server authority;
- resource planning;
- execution policy;
- GameTest;
- dedicated-server behavior;
- reload/reconnect;
- accessibility;
- EN/PT-BR;
- Patchouli;
- assets;
- previews;
- player-visible vertical slice;
- documentation progress update.

“Implemented” and “survival-ready” are different labels.

---

## 25. Epic Template

```markdown
# Epic: <name>

## Product outcome
What can the player do after this epic?

## User problem
What current limitation does it remove?

## Product hypothesis
Why should this improve the product?

## Non-goals
What is deliberately excluded?

## Current foundation
Which existing classes, contracts, registries, and tests are reused?

## Sources of truth
Which artifacts remain authoritative?

## Persistent identities
Which ids are introduced or changed?

## Authority
What is client-proposed and what is server-owned?

## Boundedness
What are the maximum work, memory, size, radius, duration, and payload limits?

## Dependencies
What must be complete first?

## Parallel windows
What can Sol, Terra High, Terra Medium, and Luna do concurrently?

## Slices
### Sol
...
### Terra High
...
### Terra Medium
...
### Luna
...

## Acceptance
- pure tests
- codec tests
- graph tests
- server tests
- UI tests
- product journey

## Migration
...

## Rollback
...

## Risks
...

## Exit condition
...

## Reassessment trigger
...
```

---

## 26. Task Template

```markdown
# Task: <name>

## Parent epic
...

## Owner
Sol / Terra High / Terra Medium / Luna

## Window
W0 / W1 / W2 / W3 / W4

## Goal
...

## Inputs
...

## Exact deliverables
...

## Owned files
...

## Read-only files
...

## Forbidden files
...

## Required tests
...

## Acceptance command
...

## Stop conditions
Escalate rather than invent behavior when:
- ...
```

---

## 27. Initial Four-Conversation Alignment

The first synchronized cycle should not start four unrelated large epics.

It should align the foundation.

## Conversation 1 — Sol

### Assignment

**A0 authoring metadata and extension boundary contract**

Deliver:

- presentation identity model;
- Rune Form registry model;
- legacy enum adapter policy;
- source precedence;
- collision behavior;
- migration behavior;
- exact boundaries with A1, D0, S0, and L1;
- file ownership proposal;
- staged implementation plan;
- acceptance matrix.

Do not implement broad Java changes yet.

---

## Conversation 2 — Terra High

### Assignment

**L0 scoped-language implementation gap and adversarial audit**

Deliver:

- exact implemented versus missing matrix;
- codec schema recommendation;
- literal-lowering generalization;
- checker gaps;
- lowering counterexamples;
- observation-sharing tests;
- effect-tail tests;
- bounded compile-step tests;
- atomic source/graph failure cases;
- recommendations requiring Sol approval.

May add pure tests only if they do not freeze an unapproved schema.

---

## Conversation 3 — Terra Medium

### Assignment

**A1 robust read-only canvas vertical slice**

Deliver:

- logical canvas coordinates;
- bounded pan and zoom;
- socket presentation;
- edge input labels;
- keyboard focus and reveal;
- no persistence;
- no graph mutation;
- viewport tests;
- build verification;
- implementation report.

Must not add `ADVANCED` persistence or mutable graph packets before the Sol contract.

---

## Conversation 4 — Luna

### Assignment

**Authoring terminology, documentation, and acceptance inventory**

Deliver:

- EN/PT-BR terminology table for Guided, Inspector, Advanced, Source, Function, Discipline, and Notation;
- Patchouli pages requiring future updates;
- current preview matrix gaps;
- icon/glyph reuse audit;
- missing translation keys;
- documentation consistency issues;
- proposed Luna work packages that depend only on frozen ids.

Must not invent semantic behavior.

---

## 28. First Six Delivery Windows

## Cycle 1 — Alignment

- Sol: A0 contract.
- Terra High: L0 gap audit.
- Terra Medium: A1 read-only canvas hardening.
- Luna: terminology and evidence inventory.

Gate:

- no persistent authoring change;
- no public API;
- all four handoffs completed.

---

## Cycle 2 — Metadata foundation

**Status:** closed with `ACCEPT` on 2026-07-26. See
`docs/A0_CYCLE_2_ACCEPTANCE.md`. This closure does not promote deferred A0-6
external sources.

- Sol: approve A0 contract and resolve audit escalations.
- Terra High: A0 precedence/migration review and L0 test vectors.
- Terra Medium: implement presentation and form registries plus legacy adapters.
- Luna: migrate localization and docs to registry-owned terminology.

Gate:

- existing guided saves replay exactly;
- current formulas and categories remain unchanged to the player;
- characterization tests pass.

---

## Cycle 3 — First editable slice and source codecs

- Sol: freeze A1 mutable-surface contract.
- Terra High: review advanced working-copy/history semantics and source codec bounds.
- Terra Medium A: typed parameter editing with local validation.
- Terra Medium B, only after file ownership split: L0 codec/Data Component implementation.
- Luna: parameter and inspector previews, documentation.

If only one Terra Medium conversation exists, serialize A1 parameter editing before L0 integration.

Gate:

- no edge mutation yet;
- source metadata is optional;
- graph remains authoritative.

---

## Cycle 4 — Disciplines and server compilation

- Sol: D0 knowledge-versus-Discipline contract.
- Terra High: Discipline balance and L0 atomic compile review.
- Terra Medium: server compile service and source/graph atomic write.
- Luna: Discipline lore and onboarding drafts using frozen ids.

Gate:

- one scoped proof round-trips;
- no runtime Discipline modifier yet;
- old talismans execute.

---

## Cycle 5 — Direct edges and built-in Disciplines

- Sol: approve Guided-to-Advanced conversion and Discipline ritual boundary.
- Terra High: edge mutation counterexamples and Discipline route review.
- Terra Medium: typed edge editing, output selection, history, built-in Discipline attachment.
- Luna: advanced editor and Discipline acceptance content.

Gate:

- pointer and keyboard construct the same minimal graph;
- Discipline switching preserves knowledge;
- server validates all writes.

---

## Cycle 6 — Notation and DSL contract

- Sol: L1 grammar ownership and S0 sign-versus-semantic contract.
- Terra High: parser ambiguity, precedence, partial application, and pretty-printer review.
- Terra Medium: notation layout foundation; parser prototype only after grammar freeze.
- Luna: built-in notation profile assets and examples.

Gate:

- notation cannot alter lookup;
- parser targets `ScopedProgramSource`;
- no separate runtime is introduced.

---

## 29. Post-1.0 Product Horizons

### Horizon 1 — Committed

One to two releases.

Contains:

- accepted contracts;
- known dependencies;
- staffed slices;
- measurable exit conditions.

### Horizon 2 — Probable

Two to four releases.

Contains:

- understood systems;
- unresolved product evidence;
- dependencies that may change scope.

### Horizon 3 — Options

No version promise.

Examples:

- sustained anchors;
- general events;
- stateful controllers;
- custom operators;
- unrestricted player-defined Disciplines;
- shared proof libraries;
- anchor networks;
- numerical solvers;
- optimization;
- distributed rituals.

Horizon 3 items must not force present architecture unless an approved experiment justifies it.

---

## 30. Reassessment Triggers

Reassess priority when:

- a minor release ships;
- a contract freezes;
- a real migration runs;
- a player test completes;
- a dedicated-server test reveals a new class of failure;
- performance exceeds a budget;
- a modpack integration reveals a missing API;
- a product hypothesis fails;
- a system becomes survival-ready;
- a roadmap item no longer justifies its cost.

Every reassessment asks:

1. What did we learn?
2. What became cheaper?
3. What became riskier?
4. Which dependency disappeared?
5. Which assumption was wrong?
6. Which item should leave the roadmap?

The roadmap must be allowed to shrink.

---

## 31. Product Metrics

### 31.1 Comprehension

- first-spell completion;
- time to first cast;
- inspector usage;
- most common validation failures;
- correction-after-error rate;
- abandonment by screen.

### 31.2 Expressiveness

- custom versus built-in proofs;
- graph structural diversity;
- rune reuse;
- function reuse;
- average depth;
- unique combinations.

### 31.3 Realization

Coverage across:

- movement;
- exploration;
- building;
- mining;
- combat;
- farming;
- logistics;
- redstone;
- support;
- area control.

### 31.4 Progression and identity

- Discipline selection;
- Discipline switching;
- epiphany completion;
- manuscript usage;
- convergence routes;
- progression stalls.

### 31.5 Reliability

- failures per cast;
- maximum tick cost;
- rollback failures;
- reload errors;
- migration errors;
- desync;
- duplication incidents;
- corrupted-world incidents.

### 31.6 Ecosystem

- external definitions loaded;
- KubeJS/datapack use;
- validation failures;
- requested APIs;
- integrations;
- features requiring a new trusted executor.

---

## 32. Explicit Deferrals

Do not promote these before their dependencies:

- full mutable editor before the mutable-surface contract;
- DSL syntax before scoped-source semantics and persistence;
- custom operators before DSL precedence and ambiguity rules;
- arbitrary custom Disciplines before built-in Discipline evidence;
- temporal programs before a sustained-runtime contract;
- general loops through graph cycles;
- effectful higher-order callbacks;
- arbitrary executor registration;
- JavaScript during casts;
- notation as semantic identity;
- TeX macro execution;
- terrain field effects before P14;
- physical damage derived directly from P11 metadata;
- ambient fields as mana or free payment;
- authoring metadata inside `ProgramGraph`.

---

## 33. Decision Summary

The shortest credible path to `1.0.0` is:

```text
0.2  Trust the current foundation
0.3  Understand proofs
0.4  Construct graphs directly
0.5  Reuse functions
0.6  Specialize through Disciplines
0.7  Write and share proofs
0.8  Program bounded decisions and collections
0.9  Freeze and validate
1.0  Begin the compatibility promise
```

The execution strategy is:

```text
Sol defines the boundary
    ->
Terra High proves the semantics
    ->
Terra Medium implements the vertical slice
    ->
Luna closes teaching and evidence
    ->
The team reassesses the product
```

The four conversations should never behave as four independent developers.

They are one pipeline with:

- one source of truth;
- one owner per sensitive boundary;
- explicit dependency windows;
- written handoffs;
- independent rollback units;
- product outcomes;
- technical acceptance;
- version gates.

That operating model is itself part of the MathMod product strategy.
