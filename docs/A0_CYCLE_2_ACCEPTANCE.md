# A0 Cycle 2 Acceptance

**Gate:** task 9 / `A0-W4-GATE`  
**Date:** 2026-07-26  
**Owner:** Sol  
**Decision:** `ACCEPT`  
**Cycle result:** A0 metadata foundation closed; A0-6 remains deferred

## 1. Decision

Cycle 2 A0 is accepted.

The accepted slice establishes immutable built-in authoring metadata, stable
Rune Form/category identity, exact trusted legacy expansion, registry-backed
Guided presentation, compatibility hardening, bilingual content evidence, and
dedicated-server persistence vectors without changing the authoritative
`ProgramGraph` or schema-1 Guided persistence.

This decision does not claim or authorize external metadata loading. A0 has no
datapack/KubeJS loader, network snapshot payload, active multi-source publisher,
or reload generation. Acceptance items that require those surfaces are
classified below as deferred with an explicit future owner.

## 2. Gate inputs

Contract and planning authority:

- `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`;
- `docs/FOUNDATION_BETA_A0_ASSIGNMENT.md`;
- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`;
- `docs/A0_POST_ADAPTER_DELIVERY_PLAN.md`;
- `docs/DELIVERY_BOARD.md`.

Accepted implementation and review gates:

- `docs/A0_METADATA_FOUNDATION_GATE_ACCEPTANCE.md`;
- `docs/A0_LEGACY_ADAPTER_GATE_ACCEPTANCE.md`;
- `docs/A1_READONLY_FINAL_GATE_ACCEPTANCE.md`;
- `docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md`;
- `docs/A0_LU_01F_GATE_ACCEPTANCE.md`;
- `docs/A0_TM_03_FINAL_GATE_ACCEPTANCE.md`;
- `docs/A0_TM_04_FINAL_GATE_ACCEPTANCE.md`.

Accepted repository handoffs:

- `docs/handoffs/A0_TM_01_HANDOFF.md`;
- `docs/handoffs/A0_TM_01F_HANDOFF.md`;
- `docs/handoffs/A0_TM_01P_HANDOFF.md`;
- `docs/handoffs/A0_TM_02_HANDOFF.md`;
- `docs/handoffs/A1_TM_READONLY_HANDOFF.md`;
- `docs/handoffs/A1_TM_READONLY_F_HANDOFF.md`;
- `docs/handoffs/A1_TM_READONLY_F2_HANDOFF.md`;
- `docs/handoffs/A0_LU_01_HANDOFF.md`;
- `docs/handoffs/A0_LU_01F_HANDOFF.md`;
- `docs/handoffs/A0_TM_03_HANDOFF.md`;
- `docs/handoffs/A0_TM_04_HANDOFF.md`.

Cross-cutting evidence:

- `docs/P0_ACCEPTANCE.md`;
- `docs/P2_MODE_PERSISTENCE_CONTRACT.md`;
- `docs/UI_PREVIEWS.md`;
- the retained integrated-client screenshots and logs under
  `run/client/screenshots` and `run/client/logs`;
- focused JUnit, dedicated GameTest server, and standard-build evidence
  recorded by the accepted gates.

## 3. Classification vocabulary

- `ACCEPTED`: implemented or preserved behavior has repository evidence.
- `DEFERRED`: the required product surface does not exist in A0; a named future
  contract owner must decide and authorize it before implementation.
- `REJECTED`: evidence demonstrates a contract violation that prevents cycle
  closure.

Absence of an unimplemented external surface is not treated as successful test
coverage. It is recorded as a deferral.

## 4. Acceptance matrix

### 4.1 Identity and registry — 7 accepted

| Contract item | Status | Evidence/decision |
|---|---|---|
| All 67 current enum values map to existing canonical ids. | `ACCEPTED` | Metadata foundation characterization and the 67-row Luna manifest agree exactly. |
| All canonical ids are unique and namespaced. | `ACCEPTED` | Frozen table and duplicate-id candidate diagnostics. |
| All 11 categories map to frozen category ids. | `ACCEPTED` | Built-in snapshot characterization and registry palette projection. |
| Enum reordering does not change registry lookup or saved identity. | `ACCEPTED` | Frozen id table and registry-order tests do not derive identity from enum ordinal. |
| Registry iteration is deterministic without localized sorting. | `ACCEPTED` | Snapshot ordering tests and A0-TM-03 registry palette evidence. |
| Returned snapshots and collections are immutable. | `ACCEPTED` | Metadata foundation focused tests. |
| Duplicate and limit diagnostics use stable codes. | `ACCEPTED` | `DUPLICATE_ID`/`LIMIT_EXCEEDED` tests, including the 2,048/2,049 presentation bound. |

### 4.2 Presentation isolation — 4 accepted, 1 deferred

| Contract item | Status | Evidence/decision |
|---|---|---|
| Removing optional rune presentation leaves graph validation/execution unchanged. | `ACCEPTED` | Presentation is a separate immutable projection with no executor/signature fields; technical fallback tests do not touch graph authority. |
| Missing descriptor produces technical fallback. | `ACCEPTED` | `BuiltInAuthoringMetadataTest.formulaAndFallbackRemainBounded` and A0-TM-03 presentation projection evidence. |
| Icon, formula, category, translation key, or order cannot change graph semantics. | `ACCEPTED` | Structured semantic fingerprint excludes presentation; exact replay remains `ProgramGraph.equals`. |
| Orphan rune presentation is rejected locally. | `DEFERRED — A0-6-CONTRACT / Sol` | No external rune-presentation ingestion/assembler exists. The current built-in snapshot supplies no independent presentation records; A0-6 must validate orphan overlays before publication. |
| Formula templates obey node/depth/token/reference limits. | `ACCEPTED` | Bounded formula tests and snapshot construction limits. |

### 4.3 Rune Form compatibility — 5 accepted, 2 deferred

| Contract item | Status | Evidence/decision |
|---|---|---|
| Every built-in form preserves presentation, parameters, and palette order. | `ACCEPTED` | 67-form characterization, Luna manifest, and registry-backed Guided captures. |
| Every current invocation string decodes to the same form and arguments. | `ACCEPTED` | Schema-1 and adapter focused vectors, including numeric canonicalization. |
| Every built-in form expands to the exact legacy graph. | `ACCEPTED` | All 67 default forms compare with `ProgramGraph.equals`. |
| Representative multi-form sequences produce equal graphs. | `ACCEPTED` | Growing-sequence adapter tests. |
| Unknown forms preserve text and disable complete Guided replay. | `ACCEPTED` | `GuidedWorkspaceStateTest` and A0 compatibility failure vectors. |
| Conflicting semantic fingerprint rejects the candidate. | `DEFERRED — A0-6-CONTRACT / Sol` | Structured fingerprints distinguish replay semantics, but no multi-source candidate assembler currently compares colliding records. |
| Presentation-only compatible metadata does not alter replay. | `DEFERRED — A0-6-CONTRACT / Sol` | Presentation is excluded from the semantic fingerprint, but applying a lower/higher-source overlay belongs to the deferred multi-source assembler. |

### 4.4 Persistence and migration — 6 accepted, 1 deferred

| Contract item | Status | Evidence/decision |
|---|---|---|
| `GuidedWorkspaceState.CURRENT_VERSION` remains 1. | `ACCEPTED` | Verified by every A0 implementation gate. |
| `mathmod:program_guided_workspace` codec is unchanged. | `ACCEPTED` | No codec delta; frozen schema-1 vector round-trips identically. |
| No new A0 Data Component exists. | `ACCEPTED` | Changed-file inventories and build inspection. |
| Reads never rewrite an item. | `ACCEPTED` | Three dedicated-server item vectors compare exact item/components before and after read. |
| Explicit resave canonicalizes aliases only after equal replay. | `DEFERRED — A0-MIGRATION-CONTRACT / Sol` | A0 adds no alias migration/resave flow. Existing reads preserve unknown text and fail closed. A future migration contract is required before legacy-field removal or external aliases. |
| Valid graphs remain inspectable/executable with unknown, malformed, or future metadata. | `ACCEPTED` | Codec boundary plus unknown/future typed-item GameTests retain and validate the authoritative graph. |
| No invocation is dropped, substituted, or partially replayed. | `ACCEPTED` | Unknown/malformed vectors return no replayable invocation list and never synthesize repair. |

### 4.5 Authority and safety — 5 accepted, 1 deferred

| Contract item | Status | Evidence/decision |
|---|---|---|
| Server resolves and validates form ids/arguments independently. | `ACCEPTED` | Existing server mutation authority remains unchanged; adapter resolution is common-side trusted Java. |
| Stale client snapshot requests fail closed. | `DEFERRED — A0-6-CONTRACT / Sol` | A0 has no snapshot request or network snapshot payload. Any future payload must define generation checking and fail-closed behavior before implementation. |
| Adapter cannot access world/player/item/random/clock/file/command/JavaScript. | `ACCEPTED` | Adapter boundary inspection and focused server-isolation tests. |
| Expanded graphs pass existing validation before activation. | `ACCEPTED` | Exact replay and unchanged server validation/inscription authority. |
| Presentation cannot register an executor or change a rune signature. | `ACCEPTED` | Metadata types contain no executor/signature authority; production rune registry remains authoritative. |
| Dedicated-server execution has no client presentation dependency. | `ACCEPTED` | Common-source isolation plus 14/14 real dedicated-server GameTests. |

### 4.6 Candidate and reload — 2 accepted, 5 deferred

| Contract item | Status | Evidence/decision |
|---|---|---|
| Candidate publication is atomic. | `DEFERRED — A0-6-CONTRACT / Sol` | Snapshot construction fails before returning a candidate, but A0 has no active multi-source publisher. Product-level publication must be contracted with A0-6. |
| Fatal reload retains last-known-good snapshot and generation. | `DEFERRED — A0-6-CONTRACT / Sol` | No external loader, active snapshot reference, or reload generation exists. The A0-TM-04 handoff correctly makes no coverage claim. |
| Record-local presentation failure falls back to a lower source. | `DEFERRED — A0-6-CONTRACT / Sol` | A0 has only trusted built-ins and technical fallback; no layered source precedence is active. |
| Duplicate ids within a source are not resolved by load order. | `ACCEPTED` | Candidate construction rejects duplicates with `DUPLICATE_ID`. |
| Conflicting form semantics never use precedence. | `DEFERRED — A0-6-CONTRACT / Sol` | The semantic fingerprint and prohibition are frozen, but no multi-source merge exists. A0-6 must enforce fatal collision before publication. |
| Alias cycles and canonical shadowing are rejected. | `DEFERRED — A0-6-CONTRACT / Sol` | Diagnostic identities are reserved, but external alias ingestion/validation is not implemented. Textual DSL aliases also remain explicitly deferred. |
| Snapshot count limits fail before publication. | `ACCEPTED` | Form/category/diagnostic/presentation bounds fail candidate construction, including the accepted 2,048 boundary test. |

### 4.7 UI and product — 6 accepted

| Contract item | Status | Evidence/decision |
|---|---|---|
| Guided player-visible behavior remains compatible. | `ACCEPTED` | A0-TM-03 final gate, 137 focused tests, and three integrated registry-palette captures. |
| Search uses stable entries/presentation without making text identity. | `ACCEPTED` | Registry search assertions and stable form-id activation. |
| Pointer and keyboard activate the same form id. | `ACCEPTED` | State-asserted Self keyboard and Simpson pointer sequence. |
| Narration exposes required semantic/presentation detail. | `ACCEPTED` | A1 narrator/socket gate, Luna narrator matrix, registry palette narration tests, and locale parity. |
| EN/PT-BR and missing-translation fallbacks are evidenced. | `ACCEPTED` | Accepted Luna content/evidence, locale parity, bilingual captures, and technical fallback tests. |
| ATM10 viewport and first-use journey are evidenced. | `ACCEPTED` | Accepted A1 viewport geometry, retained `frame-theorem` artifact, and retained first-spell inscription/ready/cast artifacts under `run/client/screenshots`. |

## 5. Matrix result

| Classification | Count |
|---|---:|
| `ACCEPTED` | 35 |
| `DEFERRED` | 10 |
| `REJECTED` | 0 |
| **Total** | **45** |

The seven deferrals do not expose a partially implemented permissive path.
Their product surfaces do not exist, and current behavior remains built-in,
server-authoritative, and fail-closed.

## 6. A0-6 decision

External-source contract work remains `DEFERRED`.

A0-6 is not ready for implementation. Promotion requires a new Sol-owned
contract that freezes, at minimum:

- source schemas and versioning;
- datapack/KubeJS authority and deterministic precedence;
- candidate assembly and atomic publication;
- active snapshot/generation ownership;
- fatal-reload last-known-good retention;
- record-local versus candidate-fatal diagnostics;
- alias-cycle and canonical-shadow validation;
- network snapshot generation checks;
- rollback and migration behavior;
- declarative expansion bounds, if external forms are pursued.

Future ownership sequence:

1. `A0-6-CONTRACT` — Sol, `BACKLOG`, activated only by a roadmap reassessment
   trigger or explicit product decision.
2. Semantic/adversarial review — Terra High, blocked on the frozen contract.
3. Implementation — Terra Medium, blocked on accepted semantic review and
   exact file ownership.
4. Content/API documentation — Luna, blocked on an approved public surface.

No A0-6 task is dispatchable from this gate.

## 7. Remaining limitations

- Metadata and adapters are trusted built-ins only.
- `GuidedWorkspaceState` remains schema 1 with numeric parameters only.
- Legacy `CustomSpellAction` and legacy Guided fields remain in place.
- External datapack/KubeJS schemas, builders, loaders, aliases, callbacks, and
  snapshot payloads remain unavailable.
- There is no active authoring snapshot reload generation or last-known-good
  publisher.
- Malformed serialized workspace input is proven at the codec boundary; typed
  item GameTests cover valid schema 1, unknown form, and future schema.
- Mutable Advanced editing, canvas persistence, Discipline semantics, notation
  profiles, and textual source aliases remain outside A0.
- Existing deprecation warnings are non-blocking and unrelated to the A0
  boundary.

## 8. Verification

Latest A0 compatibility verification independently reproduced by Sol:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests 'com.mathmod.authoring.*' --tests 'com.mathmod.program.GuidedWorkspaceStateTest' --tests 'com.mathmod.program.AuthoringSchema1CompatibilityTest' --tests 'com.mathmod.ServerSideIsolationTest' --rerun-tasks --no-daemon
```

Result: `BUILD SUCCESSFUL`; handoff count 20 focused tests, zero failures.

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; `All 14 required tests passed`.

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon
```

Result: `BUILD SUCCESSFUL`.

Previously accepted UI/content evidence remains valid and was not regenerated
by this documentation-only gate.

Final repository audit:

- 15 required contract/gate/evidence documents present;
- 11 accepted handoffs present;
- EN/PT-BR locale parity: 807/807 keys, zero locale-only keys;
- 82 Patchouli JSON files parsed, zero invalid;
- the seven sampled A0/ATM10/first-spell preview artifacts named by this gate
  are present.

## 9. Boundary and rollback

This gate changes documentation only. It does not change Java, resources,
schemas, Data Components, networking, public APIs, or saved data.

The accepted implementation remains independently reversible by A0 slice.
Rollback must preserve the existing authoritative graph and schema-1 Guided
workspace, never reinterpret an adapter id as persistent identity, and never
rewrite items during read.

## 10. Operational closure

- `A0-W4-GATE`: `DONE` (`ACCEPT`).
- A0 Cycle 2 metadata foundation: closed.
- A0-6 external sources: `BACKLOG`/deferred and not dispatchable.
- Existing task `L0-SOL-01` remains `READY` as the next already-defined
  documentation-only architecture task.
- No A1 mutable implementation or L0 production implementation is authorized
  by this acceptance.
