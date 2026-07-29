# A0 Post-Adapter Delivery Plan

**Date:** 2026-07-26

**Owner:** Sol

**Planning input:** completed `A0-TM-02` implementation handoff

**Purpose:** define the next tasks after the legacy adapter implementation,
without introducing new prompts or authorizing blocked work prematurely.

## 1. Priority decision

The next delivery sequence is:

```text
A0-TM-02 implementation
    ->
A0-TH-02 deterministic-replay review
    ->
A0-SOL-03 adapter gate
    ->
A1-TM-READONLY screen-ownership handoff
    ->
A1-TM-READONLY-F gate correction
    ->
A1-TM-READONLY-F2 residual correction
    ->
A0-TM-03 Guided palette consumption
    ->
A0-5 compatibility hardening
    ->
A0-W4-GATE
```

Parallel documentation/content work:

```text
A0-SOL-LU-01 terminology decision
    ->
A0-LU-01 localization/documentation preparation
    ->
A0-LU-01F evidence completion

L0-SOL-01 audit resolution and contract
    ->
future L0 implementation, only after its own gate
```

This order follows:

- `A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md` section 16;
- the A0 dependency graph in section 14;
- the A1 delivery order in
  `MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`;
- the explicit rule that A0-4 is serialized after conflicting A1 screen work;
- the Foundation Beta assignments for A1 read-only, L0 audit, and Luna
  inventory.

External A0 loaders, public APIs, nonnumeric Guided parameters, mutable
Advanced editing, Advanced persistence, L0 codecs, and L0 Data Components are
not authorized by this plan.

---

## 2. Task `A0-TH-02` — Legacy Adapter Deterministic-Replay Review

**Owner:** Terra High  
**Status:** `DONE` — `APPROVE` accepted by Sol  
**Task:** review the completed A0-TM-02 adapter without changing Java.

### Required documentation

- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`, especially sections 6.3,
  6.6, 10, 11.3–11.4, 12.4–12.6, 16/A0-3, and 17.3–17.5;
- `docs/A0_METADATA_FOUNDATION_GATE_ACCEPTANCE.md`;
- `docs/handoffs/A0_TM_02_HANDOFF.md`;
- `docs/DELIVERY_BOARD.md`;
- `src/main/java/com/mathmod/authoring/AuthoringMetadata.java`;
- `src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java`;
- `src/main/java/com/mathmod/authoring/TrustedLegacyExpansionAdapter.java`;
- corresponding A0 tests;
- `CustomSpellAction`, `CustomSpellInvocation`, and `CustomSpellWorkspace` as
  read-only compatibility sources;
- `ProgramGraph` and its equality semantics as read-only authority.

### Required output

Create:

```text
docs/A0_LEGACY_ADAPTER_SEMANTIC_REVIEW.md
```

The review must contain:

- `APPROVE` or `REJECT`;
- verification of all 67 form-to-adapter mappings;
- default and parameterized canonicalization matrix;
- non-finite, out-of-range, missing-key, and unknown-key behavior;
- representative sequence replay and node-id/order stability;
- exact `ProgramGraph` equality assessment;
- unknown-form and replay-mismatch closed-failure assessment;
- proof that adapter ids are not persisted;
- forbidden-dependency inspection;
- missing adversarial vectors;
- issues requiring Sol.

### Gate result

The review returned `APPROVE`; its former block on `A0-SOL-03` and
`A1-TM-READONLY` is closed. `A0-TM-03` and A0-5 retain their separate
downstream dependencies.

---

## 3. Task `A0-SOL-03` — A0-3 Adapter Gate

**Owner:** Sol  
**Status:** `DONE` — `ACCEPT`

### Required documentation

- frozen A0 contract;
- `docs/handoffs/A0_TM_02_HANDOFF.md`;
- `docs/A0_LEGACY_ADAPTER_SEMANTIC_REVIEW.md`;
- prior A0 foundation gate decisions;
- current `DELIVERY_BOARD.md`.

### Required output

Create:

```text
docs/A0_LEGACY_ADAPTER_GATE_ACCEPTANCE.md
```

The decision must record:

- accepted or rejected exact-replay evidence;
- accepted file inventory;
- identity and persistence impact;
- authority and failure behavior;
- test/build evidence;
- remaining limitations;
- release of A0 core file ownership;
- whether A1 read-only may take screen ownership;
- whether A0-4 planning may advance.

### Blocks

Completed after `A0-TH-02` returned `APPROVE`. The decision and executable
evidence are recorded in `docs/A0_LEGACY_ADAPTER_GATE_ACCEPTANCE.md`.

---

## 4. Task `A1-TM-READONLY` — Read-only Canvas Hardening

**Owner:** Terra Medium  
**Status:** `DONE` — final gate accepted

### Required documentation

- `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`, sections
  A1, 20–23, 27, and 28/Cycle 1;
- `docs/FOUNDATION_BETA_A0_ASSIGNMENT.md`, section 6.3;
- `docs/ADVANCED_EDITOR.md`, read-only rollout and acceptance sections;
- `docs/P2_MODE_PERSISTENCE_CONTRACT.md`;
- `docs/UI_PREVIEWS.md`;
- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`, sections 13.1, 15.2–15.5,
  and 16/A0-4;
- accepted A0-3 gate document.

### Required output

Create:

```text
docs/handoffs/A1_TM_READONLY_HANDOFF.md
```

Required implementation/evidence:

- logical graph coordinates independent of screen pixels;
- bounded pan and zoom;
- named input sockets and edge input labels;
- deterministic layout;
- keyboard focus traversal and focused-node reveal;
- pointer/keyboard selection equivalence;
- narration for node, socket, and focus state;
- minimum and maximum supported viewport coverage, including ATM10 targets;
- focused presentation/screen tests and standard build;
- exact changed-file inventory;
- explicit release of every granted file and confirmation that
  `RuneProgrammerScreen` and the preview harness remained untouched.

### Forbidden

- graph mutation;
- canvas persistence;
- `ADVANCED` mode;
- Data Components;
- networking;
- changes to `ProgramGraph`, `GuidedWorkspaceState`, execution, or inscription.

### Exact write ownership

```text
src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java
src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java
src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java
src/test/java/com/mathmod/client/screen/ProgramGraphPresentationTest.java
src/test/java/com/mathmod/client/screen/ProgramInspectorPresentationTest.java
src/test/java/com/mathmod/client/screen/RuneInspectorScreenSourceTest.java
docs/handoffs/A1_TM_READONLY_HANDOFF.md
```

`RuneProgrammerScreen` and the preview harness are not granted to this task.
No new helper, production, screen, test, localization, or preview file may be
created or changed until Sol adds its exact path to the board.

### Gate result

The original and first correction handoffs required bounded fixes.
`A1-TM-READONLY-F2` closed them and the complete A1 read-only gate is accepted
in `docs/A1_READONLY_FINAL_GATE_ACCEPTANCE.md`.

---

## 4A. Task `A1-TM-READONLY-F` — Read-only Canvas Gate Correction

**Owner:** Terra Medium  
**Status:** `DONE` — superseded and closed by accepted F2

### Required documentation

- `docs/A1_READONLY_GATE_REVIEW.md`;
- `docs/handoffs/A1_TM_READONLY_HANDOFF.md`;
- all task 4 contract inputs.

### Required output

```text
docs/handoffs/A1_TM_READONLY_F_HANDOFF.md
```

The correction must close A1-R1 through A1-R5: coherent zoom geometry and
hit-testing, bidirectional pan, named input sockets, complete localized
narration, clipping, and real minimum/ATM10 viewport evidence.

Exact correction ownership and forbidden boundaries are defined in
`docs/A1_READONLY_GATE_REVIEW.md`. Residual findings were closed by F2.

---

## 4B. Task `A1-TM-READONLY-F2` — Residual Canvas Gate Correction

**Owner:** Terra Medium  
**Status:** `DONE` — `ACCEPT`

### Required documentation

- `docs/A1_READONLY_CORRECTION_REVIEW.md`;
- `docs/handoffs/A1_TM_READONLY_F_HANDOFF.md`;
- all task 4 contract inputs.

### Required output

```text
docs/handoffs/A1_TM_READONLY_F2_HANDOFF.md
```

The correction must close A1-F-R1 through A1-F-R3: restored transformed edge
labels, semantically correct bilingual socket/viewport narration, and one
inset-aware inner content rectangle used consistently by render, clipping,
pan, zoom, reveal, and hit-testing.

Exact write ownership, the only three authorized new localization keys, and
all forbidden boundaries are defined in
`docs/A1_READONLY_CORRECTION_REVIEW.md`.

Accepted in `docs/A1_READONLY_FINAL_GATE_ACCEPTANCE.md`; ownership is released.

---

## 5. Task `A0-SOL-LU-01` — Terminology and Content Scope Decision

**Owner:** Sol  
**Status:** `DONE` — `ACCEPT` with bounded package scope

### Required documentation

- `docs/A0_AUTHORING_TERMINOLOGY_AND_EVIDENCE_INVENTORY.md`;
- frozen A0 contract, especially identity and presentation isolation;
- `docs/ADVANCED_EDITOR.md`;
- `docs/UI_PREVIEWS.md`;
- existing EN/PT-BR language files and Patchouli paths cited by the inventory;
- accepted A0 foundation decisions.

### Required output

Create:

```text
docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md
```

It must:

- approve or defer each LU-1 through LU-6 package;
- freeze editorial meanings for Guided, Inspector, Advanced, Source, Function,
  Discipline, and Notation without creating semantic ids;
- separate current product terms from future-surface terms;
- list allowed translation/Patchouli/preview files;
- list terminology that must remain unchanged until A1, L0, D0, or S0 gates;
- define EN/PT-BR parity and narrator evidence required by A0-4/A0-5.

### Blocks

- `A0-LU-01`;
- final content portion of A0-4 and A0-5.

---

## 6. Task `A0-LU-01` — Registry-owned Terminology Preparation

**Owner:** Luna  
**Status:** `DONE`

### Required documentation

- `docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md`;
- Luna inventory;
- frozen A0 contract;
- accepted A0-3 gate;
- `docs/UI_PREVIEWS.md`;
- exact EN/PT-BR and Patchouli files authorized by Sol.

### Required output

Create:

```text
docs/handoffs/A0_LU_01_HANDOFF.md
```

Required delivery:

- approved EN/PT-BR glossary and parity matrix;
- only the authorized localization/Patchouli corrections;
- icon-reuse manifest keyed by existing rune ids;
- narrator-copy matrix for current Guided/Inspector behavior;
- preview cases or preview requirements tied to existing surfaces;
- exact file/key inventory;
- no new semantic identity or behavior.

### Gate result

The production content delta and the `A0-LU-01F` evidence correction are
accepted. See `docs/A0_LU_01F_GATE_ACCEPTANCE.md`.

---

## 6A. Task `A0-LU-01F` — Content Evidence Completion

**Owner:** Luna  
**Status:** `DONE` — `ACCEPT`

### Required documentation

- `docs/A0_LU_01_GATE_REVIEW.md`;
- `docs/handoffs/A0_LU_01_HANDOFF.md`;
- `docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md`.

### Required output

```text
docs/handoffs/A0_LU_01F_HANDOFF.md
```

This is documentation-only. It must provide the explicit 67-form icon manifest,
the exact narrator matrix, and the bilingual old/new key audit required by
LU-R1 through LU-R3. Its only writable file is the correction handoff.

Accepted in `docs/A0_LU_01F_GATE_ACCEPTANCE.md`. Luna ownership is released.

---

## 7. Task `A0-TM-03` — Guided Palette Registry Consumption

**Owner:** Terra Medium integrator  
**Status:** `DONE` (`ACCEPT`)

### Required documentation

- frozen A0 contract, especially sections 12, 13, 15.2–15.5, 16/A0-4, and
  17.7;
- accepted A0-3 adapter gate;
- `docs/handoffs/A1_TM_READONLY_HANDOFF.md`;
- `docs/handoffs/A1_TM_READONLY_F_HANDOFF.md`;
- accepted `docs/handoffs/A1_TM_READONLY_F2_HANDOFF.md`;
- `docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md`;
- `docs/handoffs/A0_LU_01_HANDOFF.md`;
- accepted `docs/handoffs/A0_LU_01F_HANDOFF.md`;
- `docs/P2_MODE_PERSISTENCE_CONTRACT.md`;
- `docs/ADVANCED_EDITOR.md`;
- `docs/UI_PREVIEWS.md`.

### Required output

Create:

```text
docs/handoffs/A0_TM_03_HANDOFF.md
```

Required implementation/evidence:

- registry-backed category and form enumeration;
- current category/form ordering and appearance preserved;
- registry-backed numeric parameter dialogs;
- technical fallback for missing presentation;
- search, pointer, keyboard, narrator, and viewport regressions;
- existing Guided graph mutation behavior only;
- exact replay through the accepted adapter boundary;
- focused tests and standard build;
- no persistence or schema change.

### Blocks

Blocked until:

- `A0-SOL-03 == DONE`;
- `A1-TM-READONLY-F2 == DONE` and screen ownership is released (satisfied);
- `A0-SOL-LU-01 == DONE` (satisfied);
- `A0-LU-01F == DONE` (satisfied);
- exact screen/test/preview ownership is assigned to one integrator
  (satisfied in `docs/A0_TM_03_READINESS_ACCEPTANCE.md`).

It must not start concurrently with another writer of
`RuneProgrammerScreen`, `RuneInspectorScreen`, or the preview harness.

### Exact write ownership

The complete exact file list, read-only inputs, forbidden boundaries, required
integration behavior, evidence, and handoff contract are frozen in
`docs/A0_TM_03_READINESS_ACCEPTANCE.md`. No package glob is authorized.

### Gate review and bounded correction

Sol review found A0-4-R1 through A0-4-R3: legacy-authoritative category
enumeration, legacy-derived visible/search/narrator presentation with an unused
technical fallback, and a preview matrix mode without an executable harness
branch. The evidence, exact correction ownership, outputs, and forbidden
boundaries are frozen in `docs/A0_TM_03_GATE_REVIEW.md`.

The first correction closed registry category enumeration but left
registry-backed tooltip/narrator fallback, category-color parity, executed
preview coverage, and released-file inventory incomplete. The residual findings
and exact ownership are frozen in `docs/A0_TM_03F_GATE_REREVIEW.md`.

F2 closed presentation, category-color, and ownership findings, but its client
sequence opened the Number dialog before attempting the Simpson search. The
three captures show `1simpson` in the Number numeric field, so search and
pointer activation were not exercised. The exact final correction is frozen in
`docs/A0_TM_03F2_FINAL_REVIEW.md`.

F3 closed the remaining runtime sequence with state assertions and corrected
EN/PT-BR standard/compact captures. The consolidated acceptance is recorded in
`docs/A0_TM_03_FINAL_GATE_ACCEPTANCE.md`.

---

## 8. Task `A0-TM-04` — A0 Compatibility Hardening

**Owner:** Terra Medium  
**Status:** `DONE` (`ACCEPT`); bounded correction `A0-TM-04F` is also `DONE`

### Required documentation

- frozen A0 contract, sections 9, 11, 12, 16/A0-5, 17, 18, and 21–22;
- accepted handoffs for A0-TM-02, A1 read-only, corrected A0-TM-03, and A0-LU-01;
- `docs/P2_MODE_PERSISTENCE_CONTRACT.md`;
- Foundation Beta acceptance and dedicated-server documentation;
- current reload/reconnect, preview, and GameTest documentation.

### Required output

Create:

```text
docs/handoffs/A0_TM_04_HANDOFF.md
```

Required evidence:

- old schema-1 Guided save replay;
- unknown/malformed/future metadata leaves valid graph inspectable and
  executable;
- missing descriptor fallback and unreplayable-form failure;
- reload/reconnect and last-known-good behavior applicable to built-ins;
- dedicated-server independence from client presentation;
- focused tests, applicable GameTests, and standard build;
- no automatic item rewrite on read;
- complete known-limitations list.

### Blocks

Formerly blocked on accepted A0-TM-03 and A0-LU-01 handoffs.

Both dependencies are satisfied. Exact evidence-only ownership, stop
conditions, and forbidden production boundaries are frozen in
`docs/A0_TM_04_READINESS_ACCEPTANCE.md`.

The first handoff passes its focused suite, dedicated-server GameTests, and
build, but does not demonstrate the persisted item read/no-rewrite path,
persisted failure with executable graph retention, or actual active
last-known-good publication/generation retention. Findings A0-5-R1 through
A0-5-R3 and the exact test/document-only correction are frozen in
`docs/A0_TM_04_GATE_REVIEW.md`. The same review resolves the JUnit runtime
escalation by authorizing one NeoForge-discovered, test-only GameTest source:
`src/main/java/com/mathmod/program/A0CompatibilityGameTests.java`. No existing
production change is authorized.

The correction closed R1 and R2 through three real persisted-item GameTests.
R3 is accepted as an explicit A0-6 deferral: no external loader, publisher, or
active last-known-good state exists in A0, and the handoff makes no coverage
claim for it. Focused tests, all 14 dedicated-server GameTests, and the standard
build pass. Final acceptance is recorded in
`docs/A0_TM_04_FINAL_GATE_ACCEPTANCE.md`.

---

## 9. Task `A0-W4-GATE` — A0 Cycle Acceptance

**Owner:** Sol  
**Status:** `DONE` (`ACCEPT`)

### Required documentation

- all accepted A0 implementation, review, A1 ownership, and Luna handoffs;
- A0 acceptance matrix;
- Foundation Beta acceptance documents;
- dedicated-server, reload/reconnect, viewport, narrator, EN/PT-BR, Patchouli,
  and migration evidence.

### Required output

Create:

```text
docs/A0_CYCLE_2_ACCEPTANCE.md
```

The gate must classify each A0 acceptance item as accepted, deferred with owner,
or rejected; record remaining limitations; decide whether A0-6 external-source
contract work remains deferred; and update the delivery roadmap.

### Blocks

The corrected `A0-TM-04` handoff and Luna evidence are accepted. The gate is
complete. The contract matrix closes with 35 accepted items, 10 explicit
deferrals with future owners, and no rejected items. A0-6 external sources
remain deferred and are not authorized. See
`docs/A0_CYCLE_2_ACCEPTANCE.md`.

---

## 10. Task `L0-SOL-01` — Scoped Source Contract Resolution

**Owner:** Sol  
**Status:** `DONE` (`ACCEPT`)

### Required documentation

- `docs/L0_SCOPED_LANGUAGE_GAP_AUDIT.md`;
- `docs/P4_FUNCTION_LANGUAGE_CONTRACT.md`;
- `docs/P4_SEMANTIC_REVIEW.md`;
- `docs/FUNCTIONAL_LANGUAGE.md`;
- roadmap L0 order and interdependence rules;
- `ProgramStorage`, `ModDataComponents`, and language code as read-only input.

### Required output

Create:

```text
docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md
```

It must resolve the audit's eight Sol decisions:

- optional-source persistence authority and source-only behavior;
- atomic source/graph/name/resource commit and rollback;
- effect-plan purity classification;
- compile-step accounting and diagnostic identity;
- trusted literal-resolution boundary;
- registry snapshot/reload behavior;
- explicit-binding sharing versus CSE;
- inspector/source/reduced-form projection boundary.

It must also define implementation slices, file ownership, acceptance, and
explicitly keep codec field names, Data Components, payloads, and schema
versions unapproved until their designated slice.

### Blocks

No L0 production implementation may start before this contract is accepted.
This task must not delay the immediate A0-TH-02 review or take ownership of A0
files.

The eight decisions are resolved in
`docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`. The contract keeps
wire/schema details unapproved, freezes the safe implementation sequence, and
authorizes only `L0-TM-01` pure compile hardening as the next `READY` task.
