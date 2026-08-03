# MathMod Delivery Board

**Purpose:** operational coordination of Codex threads

**Current release:** `0.2.x` Foundation Beta

**Active architecture item:** P12 Foundation Beta survival-readiness closure

**Active delivery window:** Cycle 3 — Foundation Beta consolidation

**Board owner:** Sol

**Last updated:** 2026-08-02

---

## 1. How to Use This Board

This file answers four operational questions:

1. Which task may start now?
2. Which model owns it?
3. Which files may that thread change?
4. What evidence unblocks the next task?

The board does not replace:

- `MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`;
- `FOUNDATION_BETA_A0_ASSIGNMENT.md`;
- `A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`;
- lower-level technical contracts.

When this board conflicts with a frozen contract, the contract wins and Sol
must update this board.

### Status vocabulary

| Status | Meaning |
|---|---|
| `BACKLOG` | Known work without permission to start |
| `READY` | Dependencies are satisfied; a thread may start |
| `IN_PROGRESS` | One thread currently owns the task |
| `NEEDS_FIX` | Evidence found a concrete defect |
| `IN_REVIEW` | Implementation finished; gate review pending |
| `BLOCKED` | A named dependency or ownership boundary prevents work |
| `DONE` | Deliverables and handoff were accepted |

### Dispatch rule

- Create or reuse one thread for one active task.
- Paste the task's initial prompt into that thread.
- Do not dispatch tasks marked `BLOCKED` or `BACKLOG`.
- A thread must produce a repository handoff before its task becomes `DONE`.
- Chat summaries are not project memory.
- Sol is the only role that advances the board across contract gates.

---

## 2. Current Situation

The Sol W0 contract is complete:

- A0 identities are frozen;
- 67 current Rune Form ids are characterized;
- 11 category ids are frozen;
- `ProgramGraph` remains authoritative;
- A0 adds no persistence;
- `GuidedWorkspaceState` remains schema 1;
- external A0 APIs and external form expansion remain deferred.

Terra Medium has added the first A0 model and built-in metadata files:

```text
src/main/java/com/mathmod/authoring/AuthoringMetadata.java
src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java
src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java
```

The A0-1/A0-2 gate is accepted:

- the corrected focused suite passes with an isolated ASCII Gradle cache:
  6 tests, 0 failures, 0 errors, 0 skipped;
- the standard build passes with that cache;
- `docs/handoffs/A0_TM_01_HANDOFF.md` contains the complete file inventory;
- the prematurely created A0-3 adapter and test were removed;
- no persistence, Data Component, networking, public API, screen, graph, or
  `ProgramSurfaceMode` boundary changed;
- `GuidedWorkspaceState.CURRENT_VERSION` remains `1`.

Sol recorded the acceptance and exact evidence in
`docs/handoffs/A0_TM_01_SOL_REVIEW.md`.

Terra High subsequently completed `A0-TH-01` and recommended `REJECT` for A0-3.
Sol accepts that recommendation. The review found three blocking foundation
defects: ambiguous string-based semantic fingerprinting, missing contract
bounds before snapshot publication, and incomplete structured diagnostics.
The correction boundary is frozen in
`docs/A0_METADATA_REVIEW_SOL_RESOLUTION.md`.

`A0-TM-01F` completed those original corrections, but the delta re-review
`A0-TH-01R` still returned `REJECT`: `Snapshot` does not enforce the frozen
maximum of 2,048 Rune Presentation descriptors. Sol accepts this finding as a
single boundedness omission and records the narrowed resolution in
`docs/A0_METADATA_REREVIEW_SOL_RESOLUTION.md`.

`A0-TM-01P` closed that omission, and `A0-TH-01R2` returned `APPROVE`. Sol
accepted the complete metadata-foundation gate in
`docs/A0_METADATA_FOUNDATION_GATE_ACCEPTANCE.md`. This authorizes A0-3 as the
next bounded task but does not pre-approve its implementation.

Terra Medium completed the bounded A0-3 implementation and produced
`docs/handoffs/A0_TM_02_HANDOFF.md`. Terra High completed the deterministic
replay review with `APPROVE`, and Sol accepted the A0-3 gate after independent
inspection, uncached authoring tests, and the standard build. The decision,
remaining limitations, and exact A1 screen ownership are recorded in
`docs/A0_LEGACY_ADAPTER_GATE_ACCEPTANCE.md`.

Terra Medium produced `docs/handoffs/A1_TM_READONLY_HANDOFF.md`, but Sol's
technical review rejected the gate with five bounded findings recorded in
`docs/A1_READONLY_GATE_REVIEW.md`. Build integrity and boundary isolation pass;
zoom geometry, bidirectional pan, input sockets, narration, clipping, and
viewport evidence do not.

Sol accepted the Luna terminology/content scope in
`docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md`. Luna then delivered the bounded
content delta and `docs/handoffs/A0_LU_01_HANDOFF.md`. The production content
is accepted, but the handoff evidence is incomplete; the exact three findings
are recorded in `docs/A0_LU_01_GATE_REVIEW.md`.

Luna's correction is accepted and closes A0-LU-01. Terra Medium's second A1
correction closes the residual edge-label, narration, and inset-aware viewport
findings. The final A1 decision is recorded in
`docs/A1_READONLY_FINAL_GATE_ACCEPTANCE.md`.

The latest consolidated pre-dispatch decision for task 7 is
`docs/A0_TM_03_READINESS_ACCEPTANCE.md`: `READY` with exact ownership.

Terra Medium completed `A0-TM-04`, and all reproduced commands pass: 20 focused
tests, 11/11 dedicated-server GameTests, and the standard build. Sol's review
nevertheless found three evidence gaps: the schema-1 vector bypasses the real
persisted item read and cannot prove no rewrite; the failure vectors do not
prove the authoritative graph remains executable through the persistence
authority; and the last-known-good claim does not observe an active snapshot
or generation. The bounded evidence correction is recorded in
`docs/A0_TM_04_GATE_REVIEW.md`.

Terra Medium correctly escalated that ordinary JUnit cannot load the Minecraft
item path and that the former ownership granted no GameTest source. Sol has
resolved the ownership ambiguity by authorizing exactly
`src/main/java/com/mathmod/program/A0CompatibilityGameTests.java`, a test-only
NeoForge-discovered class with three persisted-item vectors. Existing
production files remained read-only.

The correction is now accepted. Sol independently reproduced the focused
suite, all 14 dedicated-server GameTests, and the standard build. External
loader/reload publication and active last-known-good retention do not exist in
A0 and are accepted as an explicit A0-6 deferral, not as tested behavior. The
complete decision is recorded in
`docs/A0_TM_04_FINAL_GATE_ACCEPTANCE.md`.

Sol completed `A0-W4-GATE`. The 45-item contract matrix closes with 35
`ACCEPTED`, 10 `DEFERRED` with named future owners, and 0 `REJECTED`.
A0-6 remains deferred and is not dispatchable. The cycle decision and complete
classification are recorded in `docs/A0_CYCLE_2_ACCEPTANCE.md`.

Sol completed `L0-SOL-01` and accepted
`docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`. The contract
resolves optional-source authority, atomic graph/source/name/resource/Guided
commit, effect-tail classification, 4,096-step accounting, NUMBER literal
trust, registry generation, explicit-binding sharing, and Inspector projection.
It approves no codec fields, Data Component, payload, or wire schema. Only
the pure compile slice was made dispatchable.

The first `L0-TM-01` handoff is now `NEEDS_FIX`. The focused suite passes
(24/24) and the standard build succeeds, but Sol found per-attempt meter reuse,
undercharged beta binding, incomplete trusted NUMBER descriptor validation,
non-uniform diagnostic paths, a spurious lowering diagnostic, incomplete
mandatory vector evidence, and two files outside the original exact ownership.
The complete findings and bounded correction ownership are recorded in
`docs/L0_TM_01_GATE_REVIEW.md`.

The `L0-TM-01F` escalation is valid but its handoff is still `NEEDS_FIX`.
`ScopedExpression.Literal` removes leading/trailing whitespace before the
trusted resolver can reject it, so Sol granted a narrow correction ownership
for that file. Independently, the mandatory OBS-SHARE/TAIL/BOUND matrix remains
incomplete, `.beta` is not a canonical authored-source diagnostic path, and the
handoff omits standard-build evidence. The decision and exact `L0-TM-01F2`
ownership are recorded in `docs/L0_TM_01F_GATE_REVIEW.md`.

The partial F2 delta correctly preserves raw literal whitespace and removes the
synthetic `.beta` path, but the required `L0_TM_01F2_HANDOFF.md` does not exist.
The 22 mandatory vectors, regression-sensitive meter tests, complete diagnostic
ordering evidence, and task-owned build evidence also remain absent. The
findings and exact evidence/test closure are recorded in
`docs/L0_TM_01F2_GATE_REVIEW.md`.

Terra Medium's F3 handoff now exists and accurately declares itself incomplete:
only 5 of 22 vectors are fully proven. This is not a new architecture or
ownership blocker. The same F3 task must complete the authorized tests and
package-private pipeline seam under
`docs/L0_TM_01F3_CORRECTION_GUIDANCE.md`. `L0-TM-01F3` is `NEEDS_FIX`;
`L0-TH-01` remains blocked.

The updated F3 delta now closes the compiler seam, 4,096/4,097 behavior,
beta/binding charging, and meter freshness. Sol independently reproduced 34
focused tests with zero failures and the standard build. Four oracle defects
remain: BOUND-1 tests 258 rather than 257 nodes; TAIL-2 does not assert the
observation-to-effect connection; OBS-SHARE-6 does not include the required
application; and diagnostic phase ordering does not assert phases 1/3/4.
The exact same-task correction is recorded in
`docs/L0_TM_01F3_GATE_REVIEW.md`.

Terra Medium corrected those four oracles within the two authorized test files.
Sol independently reproduced 34/34 focused tests and the standard build, and
accepted the complete pure compile gate in
`docs/L0_TM_01_FINAL_GATE_ACCEPTANCE.md`. The stale pending-build line in the
older F3 handoff is superseded by that final repository acceptance.
`L0-TH-01` is now `READY`.

Terra High completed `L0-TH-01` with `APPROVE`; Sol accepts that recommendation.
Sol then completed `L0-SOL-02` in
`docs/L0_SCOPED_SOURCE_WIRE_FORMAT_CONTRACT.md`. The contract freezes
`mathmod:program_scoped_source`, opaque bounded schema-1 payloads, strict
canonical JSON, future/malformed byte preservation, no network synchronization,
and atomic migration/rollback vectors. It authorizes no Java implementation.
Sol completed and accepted `L0-SOL-03` in
`docs/L0_SERVER_COMPILE_SERVICE_READINESS.md`. It freezes registry generation,
single-snapshot compilation, executable/resource/knowledge admission,
cancellation evidence, exact file ownership, and the no-mutation boundary.
`L0-TM-02F` closed all four findings from
`docs/L0_TM_02_GATE_REVIEW.md`. Sol independently reproduced 30/30 focused
tests and the standard build and accepted the complete server compile block in
`docs/L0_TM_02_FINAL_GATE_ACCEPTANCE.md`. Sol then completed and accepted
`L0-SOL-04` in `docs/L0_ATOMIC_PERSISTENCE_READINESS.md`. It freezes the
strict bounded schema-1 implementation, source/result binding, six-component
transaction, rechecks, rollback, GameTest evidence and exact ownership.
`L0-TM-03` and `L0-TM-03F` are `DONE` with `ACCEPT` in
`docs/L0_TM_03_FINAL_GATE_ACCEPTANCE.md`. R1-R6 are closed with 22/22 focused
JUnit methods and 28/28 global GameTests, including 14 named L0 methods.
`L0-SOL-05` is `DONE` with `ACCEPT`. Its bounded menu-opening projection DTO,
read-only server authority, precedence, limits and exact A1 ownership are
frozen in `docs/L0_READONLY_FUNCTIONAL_PROJECTION_READINESS.md`. `L0-TM-04`
and corrections F/F2/F3/F4 are `DONE` with `ACCEPT` in
`docs/L0_TM_04_FINAL_GATE_ACCEPTANCE.md`. The final F4 delta closes bounded
semantic-heading containment with production font geometry, an executable
runtime oracle and EN-US/PT-BR evidence at 1024x800 and 640x480.
`L0-SOL-06` is `DONE` with `ACCEPT` in
`docs/L0_FIRST_GAMEPLAY_THEOREM_SPECIFICATION.md`. It freezes
`mathmod:factored_leap`, its canonical pure function, two applications,
single terminal effect, lowering oracle, authority, teaching claims and exact
Luna ownership. Sol review of the Luna handoff found the content conforming but
the original PT-BR Patchouli evidence rendered EN-US. `L0-TM-04F5` corrected
and proved the preview locale barrier and is `DONE` with `ACCEPT` in
`docs/L0_TM_04F5_GATE_ACCEPTANCE.md`. Luna's bounded copy-fit correction is
accepted in `docs/L0_LU_01_FINAL_GATE_ACCEPTANCE.md`: all eight spreads are
localized, readable and unclipped, and 15/15 focused tests pass. `L0-LU-01`
and `L0-LU-01F` are `DONE` with `ACCEPT`. The remaining Sol-owned internal
integration amendment `L0-SOL-07` is accepted in
`docs/L0_INTERNAL_GAMEPLAY_INTEGRATION_READINESS.md`. It freezes one
`@ApiStatus.Internal` theorem-specific bridge into the existing compile and
six-component transaction authorities. The final theorem gate is accepted in
`docs/L0_TM_05_FINAL_GATE_ACCEPTANCE.md`: F2 closes the id/order-independent
semantic oracle and the tooltip/no-false-success packet evidence. `L0-TM-05`,
F and F2 are `DONE` with `ACCEPT`; 49/49 focused tests and 43/43 global
GameTests pass, with 29 named L0 GameTests.
The graph-oracle ambiguity raised during F is resolved in
`docs/L0_TM_05F_GRAPH_ORACLE_CLARIFICATION.md`: semantic isomorphism is
required, node ids/list order are non-semantic, and no production ownership
was expanded.

Therefore:

- A0-1/A0-2 is `DONE`;
- the initial metadata semantic review `A0-TH-01` is `DONE` with `REJECT`;
- the bounded correction `A0-TM-01F` is `DONE`;
- the delta semantic re-review `A0-TH-01R` is `DONE` with `REJECT`;
- the presentation-bound correction `A0-TM-01P` is `DONE`;
- the second delta re-review `A0-TH-01R2` is `DONE` with `APPROVE`;
- A0-3 / `A0-TM-02` is `DONE`;
- the deterministic-replay review `A0-TH-02` is `DONE` with `APPROVE`;
- the A0-3 Sol gate `A0-SOL-03` is `DONE` with `ACCEPT`;
- `A1-TM-READONLY`, `A1-TM-READONLY-F`, and `A1-TM-READONLY-F2` are `DONE`;
- the independent L0 audit is `DONE`;
- `L0-SOL-01` contract resolution is `DONE` with `ACCEPT`;
- `L0-TM-01`, F, F2, and F3 are `DONE` with final `ACCEPT`;
- `L0-TH-01` pure compile semantic review is `DONE` with `APPROVE`;
- `L0-SOL-02` wire-format contract is `DONE` with `ACCEPT`;
- `L0-SOL-03` server compile service readiness is `DONE` with `ACCEPT`;
- `L0-TM-02` server compile service and registry generation is `DONE` with `ACCEPT`;
- `L0-TM-02F` result/admission evidence correction is `DONE` with `ACCEPT`;
- `L0-SOL-04` atomic persistence readiness is `DONE` with `ACCEPT`;
- `L0-TM-03` scoped-source persistence and atomic commit is `DONE` with `ACCEPT`;
- `L0-TM-03F` bounded correction is `DONE` with `ACCEPT`;
- `L0-SOL-05` read-only functional projection readiness is `DONE` with
  `ACCEPT`;
- `L0-TM-04` read-only functional projection is `DONE` with `ACCEPT`;
- `L0-TM-04F`, F2, F3 and F4 are `DONE` with `ACCEPT`;
- `L0-SOL-06` first gameplay theorem specification is `DONE` with `ACCEPT`;
- `L0-LU-01` functional teaching and bilingual evidence is `DONE` with
  `ACCEPT`;
- `L0-TM-04F5` localized Patchouli preview-harness correction is `DONE` with
  `ACCEPT`;
- `L0-LU-01F` bilingual Patchouli copy-fit correction is `DONE` with `ACCEPT`;
- `L0-SOL-07` exact internal integration-readiness amendment is `DONE` with
  `ACCEPT`;
- `L0-TM-05`, `L0-TM-05F` and `L0-TM-05F2` are `DONE` with final `ACCEPT`;
- `L0-LU-02` post-implementation content alignment is `DONE` with `ACCEPT`;
- `P12-SOL-01` Foundation Beta completion contract is `DONE` with `ACCEPT`;
- `P12-TM-01` and bounded correction `P12-TM-01F` are `DONE` with `ACCEPT`
  under `docs/P12_TM_01_FINAL_GATE_ACCEPTANCE.md`;
- the Luna terminology/evidence inventory is `DONE`;
- `A0-SOL-LU-01` terminology/content decision is `DONE`;
- `A0-LU-01` is `DONE`;
- the documentation-only correction `A0-LU-01F` is `DONE` with `ACCEPT`;
- A0-4 / `A0-TM-03` and corrections F/F2/F3 are `DONE` with final `ACCEPT`;
- A0-5 / `A0-TM-04` is `DONE` with `ACCEPT`;
- the evidence-only correction `A0-TM-04F` is `DONE` with `ACCEPT`;
- the A0 W4 gate is `DONE` with `ACCEPT`;
- A0 Cycle 2 is closed;
- `P12-TM-02` is `DONE` with `ACCEPT` under
  `docs/P12_TM_02_FINAL_GATE_ACCEPTANCE.md`; automated GT-05/06/07 are closed;
- documentary `P12-SOL-02` is `DONE` with `ACCEPT` under
  `docs/P12_DEDICATED_SERVER_FIXTURE_READINESS.md`;
- `P12-FX-01F` is `DONE` with `ACCEPT` under
  `docs/P12_FX_01F_GATE_ACCEPTANCE.md`;
- Sol closed the immutable build, standalone baseline, optional-KubeJS and all
  five configuration-parser observations in
  `docs/P12_FX_01_AUTONOMOUS_EVIDENCE.md`;
- the corrected DS-02 fixture exposed FX-R3: a rejected same-resource override
  published a reduced knowledge generation and originally put `P12-FX-01` in
  `NEEDS_FIX` with a concrete `SNAPSHOT_FAILURE`;
- `P12-TM-03` was dispatched under
  `docs/P12_KNOWLEDGE_RELOAD_CORRECTION_GATE.md`;
- Terra Medium's listener-only blocker was accepted in
  `docs/P12_TM_03_BLOCKER_RESOLUTION.md`; R1-R4 from the first delivered delta
  were recorded in `docs/P12_TM_03_GATE_REVIEW.md` and are now closed by F2;
- `P12-TM-03`, F and F2 are `DONE` with `ACCEPT` under
  `docs/P12_TM_03_FINAL_GATE_ACCEPTANCE.md`: 17 focused JUnit, 526 global
  JUnit and 59/59 global GameTests pass, including the named real-load test;
- `P12-FX-01` is `DONE` with `ACCEPT` under
  `docs/P12_FX_01_FINAL_GATE_ACCEPTANCE.md`. Commit
  `ce64b9bbc1d3ef48d3231be13ebad1203d9eb7e7` produced the immutable JAR
  SHA-256 `9FF1CFE7D094BBB8E86E5739E9600C954A42ED9DC164EFD49EC6F6B74CFC725F`;
  the corrected standalone DS-02 recheck rejected the malformed candidate
  before publication and retained the previous paired snapshot;
- DS-06 and any truly two-client evidence are `BACKLOG`, not passed or waived,
  under `docs/P12_MULTIPLAYER_EVIDENCE_DEFERRAL.md`;
- `P12-DS` was put in `NEEDS_FIX`: the authenticated DS-01 run reproduced the same
  `Self player` Laboratory render-thread crash twice, and repository control
  flow also proves ordinary Laboratory mutation is not component-exactly bound
  to the captured target. The findings and bounded correction are frozen in
  `docs/P12_DS_01_GATE_REVIEW.md`;
- `P12-TM-04` is `DONE` with `ACCEPT` under
  `docs/P12_TM_04_FINAL_GATE_ACCEPTANCE.md`: 28/28 focused tests, 529/529 global
  JUnit, the new named GameTest within 60/60 global GameTests, both localized
  real-client completion logs and the standard build pass. Its R6 harness
  escalation is accepted under
  `docs/P12_TM_04_HARNESS_PREFLIGHT_CLARIFICATION.md`: only
  `laboratory-self-repeat` may bypass the two unrelated theorem-only
  preflights; formulas, localization and production UI remain read-only;
- the second preflight exposed an independent Factored Leap statement
  `PRESENTATION_FAILURE`, recorded in
  `docs/P12_FACTORED_LEAP_STATEMENT_PRESENTATION_FINDING.md`. `P12-SOL-03` is
  `DONE` with `ACCEPT` under
  `docs/P12_FACTORED_LEAP_STATEMENT_PRESENTATION_CONTRACT.md`. `P12-TM-05F`
  closes the first handoff's three findings and is `DONE` with `ACCEPT` under
  `docs/P12_TM_05F_FINAL_GATE_ACCEPTANCE.md`. Single-client `P12-DS` is `READY`
  to create a new immutable artifact/batch and rerun DS-01 from a clean checkpoint.
  A full ATM10 manual-test candidate is installed under
  `docs/P12_ATM10_CANDIDATE_AND_WARNING_REVIEW.md`; its AllTheTweaks warning is
  an intentional pack-owned `allthetweaks -> AFTER bcc` override, not a MathMod
  incompatibility. The candidate is not immutable because the repository is
  not yet clean/committed.

---

## 3. Active Cycle Dashboard

| Task | Owner | Status | Depends on | Unblocks |
|---|---|---|---|---|
| `A0-SOL-W0` | Sol | `DONE` | — | A0 implementation |
| `A0-TM-01` | Terra Medium | `DONE` | `A0-SOL-W0` | `A0-TH-01` |
| `A0-TH-01` | Terra High | `DONE` (`REJECT`) | accepted `A0-TM-01` handoff | `A0-TM-01F` |
| `A0-TM-01F` | Terra Medium | `DONE` | accepted semantic findings | `A0-TH-01R` |
| `A0-TH-01R` | Terra High | `DONE` (`REJECT`) | accepted `A0-TM-01F` handoff | `A0-TM-01P` |
| `A0-TM-01P` | Terra Medium | `DONE` | accepted presentation-bound finding | `A0-TH-01R2` |
| `A0-TH-01R2` | Terra High | `DONE` (`APPROVE`) | accepted `A0-TM-01P` handoff | `A0-TM-02` |
| `A0-TM-02` | Terra Medium | `DONE` | accepted metadata foundation | `A0-TH-02` |
| `A0-TH-02` | Terra High | `DONE` (`APPROVE`) | `A0-TM-02` handoff | `A0-SOL-03` |
| `A0-SOL-03` | Sol | `DONE` (`ACCEPT`) | `A0-TH-02` `APPROVE` | A1/Luna/A0-4 sequencing |
| `A1-TM-READONLY` | Terra Medium | `DONE` | accepted final A1 gate | `A0-TM-03` |
| `A1-TM-READONLY-F` | Terra Medium | `DONE` | superseded by accepted F2 | `A1-TM-READONLY-F2` |
| `A1-TM-READONLY-F2` | Terra Medium | `DONE` (`ACCEPT`) | A1-F-R1–A1-F-R3 | `A0-TM-03` |
| `A0-TM-03` | Terra Medium integrator | `DONE` (`ACCEPT`) | accepted F3 evidence | A0-5 |
| `A0-TM-03F` | Terra Medium integrator | `DONE` | superseded by accepted F3 | A0-5 |
| `A0-TM-03F2` | Terra Medium integrator | `DONE` | superseded by accepted F3 | A0-5 |
| `A0-TM-03F3` | Terra Medium integrator | `DONE` (`ACCEPT`) | finding A0-4-F2R1 closed | A0-5 |
| `A0-TM-04` | Terra Medium | `DONE` (`ACCEPT`) | accepted corrected evidence | `A0-W4-GATE` |
| `A0-TM-04F` | Terra Medium | `DONE` (`ACCEPT`) | findings A0-5-R1–R3 closed | `A0-W4-GATE` |
| `A0-W4-GATE` | Sol | `DONE` (`ACCEPT`) | 35 accepted / 10 deferred / 0 rejected | Cycle 2 closed |
| `L0-TH-AUDIT` | Terra High | `DONE` | functional contracts | `L0-SOL-01` |
| `L0-SOL-01` | Sol | `DONE` (`ACCEPT`) | completed L0 audit | `L0-TM-01` |
| `L0-TM-01` | Terra Medium | `DONE` (`ACCEPT`) | final F3 evidence accepted | `L0-TH-01` |
| `L0-TM-01F` | Terra Medium | `DONE` | superseded by final acceptance | `L0-TH-01` |
| `L0-TM-01F2` | Terra Medium | `DONE` | superseded by final acceptance | `L0-TH-01` |
| `L0-TM-01F3` | Terra Medium | `DONE` (`ACCEPT`) | four final oracles closed | `L0-TH-01` |
| `L0-TH-01` | Terra High | `DONE` (`APPROVE`) | accepted pure compile implementation | `L0-SOL-02` |
| `L0-SOL-02` | Sol | `DONE` (`ACCEPT`) | `L0-TH-01` `APPROVE` | `L0-SOL-03` |
| `L0-SOL-03` | Sol | `DONE` (`ACCEPT`) | accepted wire-format contract | `L0-TM-02` |
| `L0-TM-02` | Terra Medium | `DONE` (`ACCEPT`) | final correction evidence accepted | `L0-SOL-04` |
| `L0-TM-02F` | Terra Medium | `DONE` (`ACCEPT`) | R1–R4 closed; 30/30 focused tests | L0-TM-02 acceptance |
| `L0-SOL-04` | Sol | `DONE` (`ACCEPT`) | accepted wire + server gates | `L0-TM-03` |
| `L0-TM-03` | Terra Medium integrator | `DONE` (`ACCEPT`) | R1-R6 closed | `L0-SOL-05` |
| `L0-TM-03F` | Terra Medium integrator | `DONE` (`ACCEPT`) | final bounded correction accepted | atomic persistence gate |
| `L0-SOL-05` | Sol | `DONE` (`ACCEPT`) | accepted persistence/authority gate | `L0-TM-04` |
| `L0-TM-04` | Terra Medium integrator | `DONE` (`ACCEPT`) | L0-04-R1–R7 closed | accepted read-only projection gate |
| `L0-TM-04F` | Terra Medium integrator | `DONE` (`ACCEPT`) | bounded correction accepted through F4 | accepted read-only projection gate |
| `L0-TM-04F2` | Terra Medium integrator | `DONE` (`ACCEPT`) | F2R1–F2R6 closed | accepted read-only projection gate |
| `L0-TM-04F3` | Terra Medium integrator | `DONE` (`ACCEPT`) | F3R1–F3R5 closed through F4 | accepted read-only projection gate |
| `L0-TM-04F4` | Terra Medium integrator | `DONE` (`ACCEPT`) | F4R1 bounded heading containment closed | accepted read-only projection gate |
| `L0-SOL-06` | Sol | `DONE` (`ACCEPT`) | accepted projection + repository theorem capabilities | `L0-LU-01` |
| `L0-LU-01` | Luna | `DONE` (`ACCEPT`) | frozen theorem content and bilingual evidence | `L0-SOL-07` |
| `L0-TM-04F5` | Terra Medium integrator | `DONE` (`ACCEPT`) | `L0-LU-01-R1` localized preview defect | `L0-LU-01F` |
| `L0-LU-01F` | Luna | `DONE` (`ACCEPT`) | accepted `L0-TM-04F5` harness correction | `L0-SOL-07` |
| `L0-SOL-07` | Sol | `DONE` (`ACCEPT`) | accepted Luna content/evidence | `L0-TM-05` |
| `L0-TM-05` | Terra Medium integrator | `DONE` (`ACCEPT`) | R1-R5 and F residuals closed | L0 gameplay closure |
| `L0-TM-05F` | Terra Medium integrator | `DONE` (`ACCEPT`) | F-R1/F-R2 closed through F2 | L0 gameplay closure |
| `L0-TM-05F2` | Terra Medium integrator | `DONE` (`ACCEPT`) | 49 focused; 29 L0 / 43 global GameTests | `L0-LU-02` |
| `L0-LU-02` | Luna | `DONE` (`ACCEPT`) | bilingual p0 content and visual evidence accepted | L0 content closure |
| `P12-SOL-01` | Sol | `DONE` (`ACCEPT`) | repository priority and P8 evidence boundary frozen | `P12-TM-01` |
| `P12-TM-01` | Terra Medium | `DONE` (`ACCEPT`) | ten P8 GameTests + authority proof accepted | `P12-TM-01F` |
| `P12-TM-01F` | Terra Medium | `DONE` (`ACCEPT`) | finite launch + loaded swept-volume correction accepted | `P12-TM-02` |
| `P12-TM-02` | Terra Medium | `DONE` (`ACCEPT`) | 47 focused; P9 4, P10 5, P11 5; 58 global GameTests | `P12-SOL-02` |
| `P12-SOL-02` | Sol | `DONE` (`ACCEPT`) | fixture/evidence contract frozen | `P12-FX-01` |
| `P12-FX-01` | Sol + operator | `DONE` (`ACCEPT`) | immutable JAR plus corrected standalone DS-02 accepted | `P12-DS` single-client execution |
| `P12-FX-01F` | Terra Medium | `DONE` (`ACCEPT`) | FX-R1/FX-R2 closed | Sol/operator external continuation |
| `P12-TM-03` | Terra Medium | `DONE` (`ACCEPT`) | paired atomic publication accepted | external runtime recheck |
| `P12-TM-03F` | Terra Medium | `DONE` (`ACCEPT`) | R1-R4 closed through F2 | external runtime recheck |
| `P12-TM-03F2` | Terra Medium | `DONE` (`ACCEPT`) | reproducible real-load GameTest; 59/59 global | external runtime recheck |
| `P12-DS-MP` | Sol + operator | `BACKLOG` | two distinct authenticated accounts | DS-06 multiplayer evidence |
| `P12-DS` | Sol + operator | `READY` | accepted P12-TM-05F | new immutable artifact and clean DS-01 rerun |
| `P12-TM-04` | Terra Medium | `DONE` (`ACCEPT`) | DS01-R1/R2 and R6 closed | `P12-SOL-03` presentation contract |
| `P12-SOL-03` | Sol | `DONE` (`ACCEPT`) | accepted `P12-TM-04` handoff | bounded third-line presentation contract |
| `P12-TM-05` | Terra Medium | `DONE` (`ACCEPT`) | R1-R3 closed through F | statement presentation gate |
| `P12-TM-05F` | Terra Medium | `DONE` (`ACCEPT`) | R1-R3 closed; 40 focused / 533 global | single-client `P12-DS` |
| `P12-M` | Sol + independent player | `BLOCKED` | stable build and observation fixture | manual first-use/accessibility evidence |
| `A0-LU-INVENTORY` | Luna | `DONE` | frozen current ids | `A0-SOL-LU-01` |
| `A0-SOL-LU-01` | Sol | `DONE` (`ACCEPT`) | completed Luna inventory | `A0-LU-01` |
| `A0-LU-01` | Luna | `DONE` | accepted content and evidence | A0-4/A0-5 content |
| `A0-LU-01F` | Luna | `DONE` (`ACCEPT`) | findings LU-R1–LU-R3 | A0-4/A0-5 content |

### Tasks that may be dispatched now

```text
P12-DS (single-client only; begin with new artifact and clean DS-01)
```

### Tasks that must not be dispatched yet

```text
A0-6 external sources
P12-DS
P12-DS-MP
P12-M
P14/P15 terrain or dynamics expansion
```

---

## 4. Thread Map

Use these four role threads as the stable pipeline:

| Thread | Current assignment | Next assignment |
|---|---|---|
| Sol | `P12-TM-05F` accepted | create new immutable batch and execute clean DS-01 rerun |
| Terra High | `L0-TH-01` complete with `APPROVE` | wait for later semantic gate |
| Terra Medium | `P12-TM-05F` `DONE` with `ACCEPT` | wait for DS evidence findings, if any |
| Luna | `L0-LU-02` accepted | wait for a later content gate |

Do not manually switch the model within a thread. Keep the role stable and
change only the assignment after the previous handoff is accepted.

---

## 5. Archived Initial Prompt — Sol Thread

The prompt below records the state before the A0-TM-01 gate. It is retained for
audit history and must not be dispatched again. The active state is sections 2,
3, 4, 9, and 13.

### Assignment

Operational coordination and gate ownership.

### Copyable prompt

```text
Você é o Sol responsável pela coordenação arquitetural do MathMod.

Leia, nesta ordem:
1. docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md
2. docs/FOUNDATION_BETA_A0_ASSIGNMENT.md
3. docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md
4. docs/DELIVERY_BOARD.md

Situação atual:
- O contrato A0 W0 está congelado.
- A0-TM-01 está em NEEDS_FIX.
- L0-TH-AUDIT e A0-LU-INVENTORY podem ocorrer em paralelo.
- A0-TH-01 e A0-TM-02 permanecem bloqueados.

Sua função nesta thread:
- manter o DELIVERY_BOARD como visão operacional;
- revisar handoffs;
- resolver somente ambiguidades de identidade, persistência, autoridade,
  precedência, migração e ownership;
- impedir que tarefas bloqueadas iniciem prematuramente;
- atualizar estados apenas com evidência;
- autorizar o próximo gate quando as dependências forem satisfeitas.

Não implemente Java por padrão.
Não altere ProgramGraph, GuidedWorkspaceState, Data Components, networking,
ProgramSurfaceMode ou APIs públicas.

Próxima ação:
aguarde e revise o handoff de A0-TM-01. Quando ele existir, verifique:
- canonicalização de não finitos conforme o contrato;
- 67 forms e 11 categorias;
- testes focados;
- build;
- ausência de mudança persistente;
- arquivos alterados;
- limitações e escalations.

Se o handoff for aceito, altere A0-TM-01 para DONE e A0-TH-01 para READY.
Registre a decisão no repositório; não dependa do histórico do chat.
```

---

## 6. Task `A0-TM-01` — Close A0-1/A0-2

### Owner

Terra Medium

### Status

`DONE`

### Goal

Close the pure metadata and built-in characterization gate without starting the
legacy expansion adapter.

### Exact deliverables

1. Correct contract-inconsistent numeric canonicalization test expectations.
2. Confirm finite values clamp to min/max.
3. Confirm `NaN`, positive infinity, and negative infinity return the declared
   default.
4. Preserve the 67-form compatibility table.
5. Preserve all 11 category mappings.
6. Add or complete tests for:
   - duplicate ids;
   - unknown category;
   - immutable snapshots;
   - deterministic ordering;
   - semantic fingerprint excluding presentation;
   - semantic fingerprint including replay-sensitive parameter fields;
   - bounded formula validation;
   - technical presentation fallback.
7. Run focused tests and the standard build.
8. Produce `docs/handoffs/A0_TM_01_HANDOFF.md`.

### Owned files

```text
src/main/java/com/mathmod/authoring/AuthoringMetadata.java
src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java
src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java
docs/handoffs/A0_TM_01_HANDOFF.md
```

### Read-only files

```text
docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md
docs/FOUNDATION_BETA_A0_ASSIGNMENT.md
docs/DELIVERY_BOARD.md
src/main/java/com/mathmod/program/CustomSpellAction.java
src/main/java/com/mathmod/program/CustomNumericParameter.java
src/main/java/com/mathmod/runes/**
```

### Forbidden files

```text
src/main/java/com/mathmod/runes/ProgramGraph.java
src/main/java/com/mathmod/program/GuidedWorkspaceState.java
src/main/java/com/mathmod/program/ProgramSurfaceMode.java
src/main/java/com/mathmod/registry/ModDataComponents.java
src/main/java/com/mathmod/network/**
src/main/java/com/mathmod/client/screen/**
```

### Stop conditions

Stop and escalate if:

- a canonical form id differs from the contract;
- a fix requires changing `CustomSpellAction`;
- a test requires a persistence/schema change;
- Gradle remains unable to start the test worker after ordinary diagnostics;
- replay/expansion code becomes necessary.

### Acceptance

`A0-TM-01` becomes `DONE` only after:

- focused tests pass;
- build passes or a repository-independent environment failure is fully
  evidenced and accepted by Sol;
- no forbidden file changed;
- the handoff exists.

### Latest Sol review

`ACCEPT` on 2026-07-26. Numeric behavior, the 67/11 characterization, focused
tests, build, persistence isolation, complete file inventory, and removal of
premature A0-3 files are evidenced. See
`docs/handoffs/A0_TM_01_SOL_REVIEW.md`.

### Copyable prompt

```text
Você é o Terra Medium, responsável pela implementação Java ordinária e pela
evidência técnica do MathMod.

Leia:
1. docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md
2. docs/FOUNDATION_BETA_A0_ASSIGNMENT.md
3. docs/DELIVERY_BOARD.md, especialmente a tarefa A0-TM-01

Implemente exclusivamente A0-TM-01.

Estado encontrado:
- AuthoringMetadata.java, BuiltInAuthoringMetadata.java e
  BuiltInAuthoringMetadataTest.java já existem.
- O teste de canonicalização atualmente espera 1024 para positive infinity.
- O contrato determina que todo valor não finito retorna ao default declarado;
  para mathmod:number_one isso é 1.0.
- Ainda não existe handoff de A0-1/A0-2.

Faça:
- corrija e complete os testes conforme o contrato;
- valide 67 forms e 11 categorias;
- valide bounds, imutabilidade, ordering e semantic fingerprint;
- execute os testes focados;
- execute o build padrão;
- crie docs/handoffs/A0_TM_01_HANDOFF.md usando o protocolo de handoff.

Não implemente A0-3.
Não altere CustomSpellAction, GuidedWorkspaceState, ProgramGraph,
ProgramSurfaceMode, Data Components, networking ou telas.
Não invente comportamento se o contrato for insuficiente; registre a escalation.

Ao terminar, informe comandos exatos, resultados, arquivos alterados e
limitações. A tarefa só está concluída com o handoff no repositório.
```

---

## 7. Completed Task `L0-TH-AUDIT` — Scoped Language Gap Audit

### Owner

Terra High

### Status

`DONE` — repository audit accepted as input to `L0-SOL-01`

### Goal

Audit the existing scoped functional language without freezing an unapproved
persistent schema.

### Exact deliverables

1. Implemented-versus-missing matrix covering:
   - AST;
   - structural validation;
   - type checking;
   - purity/effect tail;
   - lowering;
   - codecs;
   - persistence;
   - server integration;
   - inspection;
   - gameplay.
2. Proposed codec schema clearly marked as unapproved.
3. Literal-lowering generalization recommendation.
4. Checker and diagnostic gaps.
5. Lowering counterexamples.
6. Observation-sharing test vectors.
7. Effect-tail test vectors.
8. Bounded compile-step limits and tests.
9. Atomic source/graph failure matrix.
10. Sol escalations.
11. `docs/L0_SCOPED_LANGUAGE_GAP_AUDIT.md`.

### Owned files

```text
docs/L0_SCOPED_LANGUAGE_GAP_AUDIT.md
```

Pure schema-neutral tests may be added only after exact file ownership is
declared and confirmed not to overlap another thread.

### Read-only files

```text
src/main/java/com/mathmod/language/**
src/test/java/com/mathmod/language/**
src/main/java/com/mathmod/runes/**
src/main/java/com/mathmod/program/ProgramStorage.java
src/main/java/com/mathmod/registry/ModDataComponents.java
docs/P4_FUNCTION_LANGUAGE_CONTRACT.md
docs/P4_SEMANTIC_REVIEW.md
docs/FUNCTIONAL_LANGUAGE.md
```

### Forbidden files

```text
src/main/java/com/mathmod/authoring/**
src/test/java/com/mathmod/authoring/**
src/main/java/com/mathmod/program/GuidedWorkspaceState.java
src/main/java/com/mathmod/program/CustomSpellAction.java
src/main/java/com/mathmod/program/ProgramSurfaceMode.java
src/main/java/com/mathmod/registry/ModDataComponents.java
src/main/java/com/mathmod/network/**
```

### Copyable prompt

```text
Você é o Terra High, responsável por semântica, casos adversariais,
boundedness e recomendações de implementação do MathMod.

Leia:
1. docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md
2. docs/P4_FUNCTION_LANGUAGE_CONTRACT.md
3. docs/P4_SEMANTIC_REVIEW.md
4. docs/FUNCTIONAL_LANGUAGE.md
5. docs/FOUNDATION_BETA_A0_ASSIGNMENT.md
6. docs/DELIVERY_BOARD.md, tarefa L0-TH-AUDIT

Execute exclusivamente a auditoria L0.

Produza docs/L0_SCOPED_LANGUAGE_GAP_AUDIT.md com:
- matriz exata de implementado versus ausente;
- proposta de codec marcada como NÃO APROVADA;
- gaps do checker;
- contraprovas de lowering;
- testes vetoriais de compartilhamento de observações;
- testes de effect tail;
- limites de passos de compilação;
- falhas de atomicidade source/graph;
- decisões que exigem Sol;
- handoff para futura implementação Terra Medium.

Você pode inspecionar todo o código relevante, mas não altere produção.
Não congele nomes de campos, tags, schema version, Data Component ou payload.
Não edite arquivos A0.
Se adicionar algum teste puro, declare ownership exato antes e garanta que ele
não fixa schema persistente.
```

---

## 8. Completed Task `A0-LU-INVENTORY` — Terminology and Evidence Inventory

### Owner

Luna

### Status

`DONE` — repository inventory accepted as input to `A0-SOL-LU-01`

### Goal

Inventory current authoring terminology, documentation, assets, previews, and
accessibility evidence without changing semantics.

### Exact deliverables

1. EN/PT-BR terminology table for:
   - Guided;
   - Inspector;
   - Advanced;
   - Source;
   - Function;
   - Discipline;
   - Notation.
2. Patchouli pages requiring updates.
3. Preview matrix gaps.
4. Icon and glyph reuse audit.
5. Missing/inconsistent translation keys.
6. Narrator and accessibility-copy gaps.
7. Stale or contradictory documentation.
8. Work packages grouped by frozen-id dependency.
9. `docs/A0_AUTHORING_TERMINOLOGY_AND_EVIDENCE_INVENTORY.md`.

### Owned files

```text
docs/A0_AUTHORING_TERMINOLOGY_AND_EVIDENCE_INVENTORY.md
```

### Read-only files

```text
src/main/resources/assets/mathmod/lang/**
src/main/resources/assets/mathmod/patchouli_books/**
src/main/resources/assets/mathmod/textures/gui/runes/**
src/main/java/com/mathmod/client/**
src/test/java/com/mathmod/client/**
docs/**
```

### Forbidden files

```text
all Java production files
all persistence codecs
all network payloads
all stable ids
all graph expansion behavior
all production localization/asset rewrites during this inventory task
```

### Copyable prompt

```text
Você é a Luna, responsável por terminologia, localização, documentação,
previews, assets e evidência de acessibilidade do MathMod após a semântica estar
estável.

Leia:
1. docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md
2. docs/FOUNDATION_BETA_A0_ASSIGNMENT.md
3. docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md
4. docs/DELIVERY_BOARD.md, tarefa A0-LU-INVENTORY

Execute somente um inventário read-only.

Produza docs/A0_AUTHORING_TERMINOLOGY_AND_EVIDENCE_INVENTORY.md com:
- tabela EN/PT-BR de Guided, Inspector, Advanced, Source, Function,
  Discipline e Notation;
- páginas Patchouli que precisarão mudar;
- gaps da matriz de previews;
- auditoria de reúso de ícones/glyphs;
- translation keys ausentes ou inconsistentes;
- gaps de narrator e acessibilidade;
- documentação obsoleta ou contraditória;
- work packages futuros dependentes apenas de ids congelados.

Para cada finding, cite arquivo e key exatos.
Separe claramente o termo atual da proposta.
Não altere Java, assets, traduções ou Patchouli neste passo.
Não invente comportamento semântico.
Não renomeie ids persistentes.
```

---

## 9. Completed Task `A0-TH-01` — Metadata Semantic Review

### Owner

Terra High

### Status

`DONE` — recommendation `REJECT` for A0-3 accepted by Sol

### Dependency

Accepted `docs/handoffs/A0_TM_01_HANDOFF.md`.

### Goal

Review A0-1/A0-2 before the legacy adapter is allowed to touch replay behavior.

### Review scope

- numeric canonicalization;
- formula bounds;
- snapshot immutability;
- deterministic ordering;
- semantic fingerprint completeness;
- duplicate/collision behavior;
- frozen identity table;
- missing adversarial tests;
- implementation recommendation for A0-3.

### Output

```text
docs/A0_METADATA_SEMANTIC_REVIEW.md
```

### Dispatch condition

Completed on 2026-07-26. Do not redispatch the initial review. Use
`A0-TH-01R` after `A0-TM-01F`.

### Future copyable prompt

```text
Você é o Terra High. A0-TM-01 foi aceito pelo Sol e o handoff está em
docs/handoffs/A0_TM_01_HANDOFF.md.

Leia:
1. docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md
2. docs/handoffs/A0_TM_01_HANDOFF.md
3. os arquivos A0 listados no handoff
4. docs/DELIVERY_BOARD.md, tarefa A0-TH-01

Faça uma revisão read-only de:
- canonicalização numérica;
- bounds;
- imutabilidade;
- ordering determinístico;
- semantic fingerprint;
- colisões e duplicatas;
- tabela de 67 forms e 11 categorias;
- suficiência dos testes adversariais.

Produza docs/A0_METADATA_SEMANTIC_REVIEW.md com:
- findings priorizados;
- contraprovas;
- testes ausentes;
- alternativas rejeitadas;
- recomendação APPROVE ou REJECT para A0-3;
- questões que exigem Sol.

Não implemente correções e não altere arquivos Java.
```

---

## 9A. Completed Task `A0-TM-01F` — Metadata Foundation Corrections

### Owner

Terra Medium

### Status

`DONE`

### Goal

Correct only the semantic-foundation defects accepted in
`A0_METADATA_REVIEW_SOL_RESOLUTION.md`, without starting A0-3.

### Owned files

```text
src/main/java/com/mathmod/authoring/AuthoringMetadata.java
src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java
src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java
docs/handoffs/A0_TM_01F_HANDOFF.md
```

### Required outcome

- structured, unambiguous, non-persisted semantic compatibility value;
- expansion-consumed identities separated from descriptive hints;
- contract bounds enforced before publication;
- structured stable diagnostics;
- deterministic category/form queries;
- enum-order-independent identity characterization;
- adversarial vectors required by the Sol resolution;
- focused tests and standard build.

### Forbidden

- legacy expansion adapter or replay implementation;
- `ProgramGraph`, `GuidedWorkspaceState`, Data Components, networking,
  `ProgramSurfaceMode`, client screens, or public APIs.

---

## 9B. Completed Task `A0-TH-01R` — Metadata Delta Re-review

### Owner

Terra High

### Status

`DONE` — recommendation `REJECT` for A0-3 accepted by Sol

### Dependency

Accepted `docs/handoffs/A0_TM_01F_HANDOFF.md`.

### Goal

Review only the A0-TM-01F delta and issue `APPROVE` or `REJECT` for A0-3.

### Output

```text
docs/A0_METADATA_SEMANTIC_REREVIEW.md
```

No Java changes are authorized.

---

## 9C. Completed Task `A0-TM-01P` — Rune Presentation Bound Correction

### Owner

Terra Medium

### Status

`DONE`

### Goal

Close the single remaining section 10 boundedness omission without reopening
the already accepted A0-TM-01F semantics and without starting A0-3.

### Owned files

```text
src/main/java/com/mathmod/authoring/AuthoringMetadata.java
src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java
docs/handoffs/A0_TM_01P_HANDOFF.md
```

`BuiltInAuthoringMetadata.java` is read-only unless the new boundary test cannot
be expressed without it; any need to edit that file must be escalated to Sol.

### Exact deliverables

1. Add an explicit maximum of 2,048 Rune Presentation descriptors.
2. Enforce the maximum in the `Snapshot` invariant before publication.
3. Prove that 2,048 presentations are accepted.
4. Prove that 2,049 presentations fail with structured
   `DiagnosticCode.LIMIT_EXCEEDED`.
5. Prove that the rejected construction returns no snapshot.
6. Run the focused A0 metadata suite and standard build.
7. Produce `docs/handoffs/A0_TM_01P_HANDOFF.md`.

### Forbidden

- semantic fingerprint redesign;
- adapter or replay implementation;
- alias/external-source candidate assembly;
- `ProgramGraph`, `GuidedWorkspaceState`, Data Components, networking,
  `ProgramSurfaceMode`, client screens, or public APIs.

### Stop conditions

Stop and escalate if the correction requires a persistence/schema change, a
public loader/API decision, or any file outside the owned list.

### Copyable prompt

```text
Você é o Terra Medium. Execute exclusivamente A0-TM-01P.

Leia:
1. docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md, seção 10
2. docs/A0_METADATA_SEMANTIC_REREVIEW.md
3. docs/A0_METADATA_REREVIEW_SOL_RESOLUTION.md
4. docs/DELIVERY_BOARD.md, tarefa 9C

Corrija somente o limite de Rune Presentation descriptors:
- máximo explícito de 2.048;
- 2.048 aceitos;
- 2.049 rejeitados com LIMIT_EXCEEDED estruturado;
- nenhum Snapshot retornado no caso rejeitado.

Execute os testes A0 focados e o build padrão.
Produza docs/handoffs/A0_TM_01P_HANDOFF.md.

Não altere fingerprint, adapters, replay, persistência, Data Components,
networking, ProgramGraph, ProgramSurfaceMode, telas ou APIs públicas.
```

---

## 9D. Completed Task `A0-TH-01R2` — Presentation Bound Delta Re-review

### Owner

Terra High

### Status

`DONE` — recommendation `APPROVE` for A0-3 accepted by Sol

### Dependency

Accepted `docs/handoffs/A0_TM_01P_HANDOFF.md`.

### Goal

Review only the presentation-count invariant and its two boundary vectors.
Previously closed findings remain closed unless the 9C delta regresses them.

### Output

```text
docs/A0_METADATA_SEMANTIC_REREVIEW_2.md
```

The output must recommend `APPROVE` or `REJECT` for A0-3. No Java changes are
authorized.

### Future copyable prompt

```text
Você é o Terra High. O Sol aceitou o handoff de A0-TM-01P.

Leia:
1. docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md, seção 10
2. docs/A0_METADATA_SEMANTIC_REREVIEW.md
3. docs/A0_METADATA_REREVIEW_SOL_RESOLUTION.md
4. docs/handoffs/A0_TM_01P_HANDOFF.md
5. o delta Java/teste listado no handoff
6. docs/DELIVERY_BOARD.md, tarefa 9D

Revise somente:
- o invariante máximo de 2.048 rune presentations;
- o vetor aceito de 2.048;
- o vetor rejeitado de 2.049;
- o DiagnosticCode.LIMIT_EXCEEDED estruturado;
- ausência de regressão causada pelo delta.

Produza docs/A0_METADATA_SEMANTIC_REREVIEW_2.md com recomendação APPROVE ou
REJECT para A0-3. Não altere Java.
```

---

## 10. Accepted — `A0-TM-02` Legacy Expansion Adapter

### Owner

Terra Medium

### Status

`DONE`

### Dependencies

- `A0-TM-01 == DONE`;
- `A0-TM-01F == DONE`;
- `A0-TM-01P == DONE`;
- `A0-TH-01R2 == DONE`;
- latest Terra High recommendation is `APPROVE` and is accepted by Sol.

### Goal

Route legacy Rune Form resolution and expansion through a trusted adapter
boundary while preserving exact `ProgramGraph` equality.

### Exact deliverables

1. Trusted adapter registry.
2. Explicit mapping from form id to adapter id.
3. Server/common-side canonical argument validation.
4. Legacy and adapter expansion comparison path.
5. Exact graph-equality tests for:
   - every built-in form in accepted contexts;
   - representative multi-form sequences;
   - parameterized forms;
   - unknown form;
   - replay mismatch.
6. No schema or Data Component change.
7. `docs/handoffs/A0_TM_02_HANDOFF.md`.

### Initially owned files

```text
src/main/java/com/mathmod/authoring/**
src/test/java/com/mathmod/authoring/**
src/main/java/com/mathmod/program/CustomSpellAction.java
src/main/java/com/mathmod/program/CustomSpellInvocation.java
src/main/java/com/mathmod/program/CustomSpellWorkspace.java
focused tests for those classes
docs/handoffs/A0_TM_02_HANDOFF.md
```

Exact existing files must be declared before editing.

### Forbidden files

```text
ProgramGraph
GuidedWorkspaceState
ModDataComponents
ProgramSurfaceMode
network payloads
client screens
execution allowlist
public KubeJS/datapack APIs
```

### Dispatch condition

Implementation handoff exists at `docs/handoffs/A0_TM_02_HANDOFF.md`.
Terra High's `APPROVE` and Sol's `ACCEPT` are recorded in
`docs/A0_LEGACY_ADAPTER_SEMANTIC_REVIEW.md` and
`docs/A0_LEGACY_ADAPTER_GATE_ACCEPTANCE.md`. Do not redispatch this
implementation.

### Future copyable prompt

```text
Você é o Terra Medium. A0-TM-01P e A0-TH-01R2 foram aceitos com recomendação
APPROVE, e o Sol autorizou A0-TM-02.

Leia:
1. docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md, especialmente A0-3
2. docs/handoffs/A0_TM_01_HANDOFF.md
3. docs/A0_METADATA_REVIEW_SOL_RESOLUTION.md
4. docs/handoffs/A0_TM_01F_HANDOFF.md
5. docs/A0_METADATA_REREVIEW_SOL_RESOLUTION.md
6. docs/handoffs/A0_TM_01P_HANDOFF.md
7. docs/A0_METADATA_SEMANTIC_REREVIEW_2.md
8. docs/DELIVERY_BOARD.md, tarefa A0-TM-02

Implemente exclusivamente o trusted legacy expansion adapter.

Requisitos:
- persistência continua usando form id, nunca adapter id;
- adapters são Java confiável e não acessam world/player/item/random/callback;
- argumentos são canonicalizados conforme os descriptors;
- a expansão antiga e a mediada por registry devem gerar ProgramGraph
  exatamente iguais;
- unknown form e replay mismatch falham fechados;
- GuidedWorkspaceState permanece schema 1;
- não há Data Component, payload ou API pública nova.

Adicione testes por form e por sequências representativas.
Execute testes focados e build.
Produza docs/handoffs/A0_TM_02_HANDOFF.md.

Pare e escale se igualdade exata exigir alterar ProgramGraph, ids persistentes,
codec, networking ou semântica de expansão.
```

---

## 10A. Active Corrections — Guided Registry Client Completion

### Owner

Terra Medium integrator.

### Status

`A0-TM-03`, F, F2, and F3 are `DONE`; the consolidated decision is `ACCEPT`.

### Goal

F3 closed A0-4-F2R1 with state-asserted keyboard, search, and pointer sequencing
plus corrected EN/PT-BR standard and compact captures.

### Exact ownership and outputs

The final acceptance and consolidated evidence are recorded in
`docs/A0_TM_03_FINAL_GATE_ACCEPTANCE.md`. All task 7 ownership is released.

### Downstream state

`A0-TM-04` was dispatched under `docs/A0_TM_04_READINESS_ACCEPTANCE.md`.
Its current review state is recorded in section 10B.

---

## 10B. Active Correction — A0 Compatibility Evidence

### Owner

Terra Medium.

### Status

`A0-TM-04` and `A0-TM-04F` are `DONE` with `ACCEPT`.

### Goal

Close A0-5-R1 through A0-5-R3 without changing production: use the real
persisted read authority, prove graph executability and no rewrite, and stop
overstating last-known-good behavior that belongs to deferred external reload.

### Exact ownership and outputs

The findings, exact test/document ownership, requirements, and stop conditions
are frozen in `docs/A0_TM_04_GATE_REVIEW.md`. Following Terra Medium's valid
runtime escalation, that review now authorizes one new test-only GameTest
class, `src/main/java/com/mathmod/program/A0CompatibilityGameTests.java`.

The corrected evidence and A0-6 deferral classification are accepted in
`docs/A0_TM_04_FINAL_GATE_ACCEPTANCE.md`. All task 8 ownership is released.

### Downstream state

`A0-W4-GATE` is `DONE` with `ACCEPT`. The complete 45-item classification is
recorded in `docs/A0_CYCLE_2_ACCEPTANCE.md`.

---

## 10C. Active L0 Sequence — Scoped Functional Source

### Contract result

`L0-SOL-01` is `DONE` with `ACCEPT`.

The complete authority, compile, persistence, atomicity, literal, reload,
sharing, projection, slice, and ownership decision is
`docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`.

### Dispatchable task

The L0 sequence is closed and the next product priority is frozen in
`docs/P12_FOUNDATION_BETA_COMPLETION_CONTRACT.md`. Only:

```text
Single-client `P12-DS` is `READY`; DS-06 remains in the separate
`P12-DS-MP` backlog.
```

All P12-TM-03 ownership is released. Networking, schemas, content and public
APIs remain read-only.

### Current downstream sequence

```text
L0-TM-01 NEEDS_FIX
    -> L0-TM-01F NEEDS_FIX
    -> L0-TM-01F2 NEEDS_FIX
    -> L0-TM-01F3 DONE (ACCEPT)
    -> L0-TH-01 DONE (APPROVE)
    -> L0-SOL-02 DONE (ACCEPT)
    -> L0-SOL-03 DONE (ACCEPT)
    -> L0-TM-02 DONE (ACCEPT)
    -> L0-TM-02F DONE (ACCEPT)
    -> L0-SOL-04 DONE (ACCEPT)
    -> L0-TM-03 DONE (ACCEPT)
    -> L0-TM-03F DONE (ACCEPT)
    -> L0-SOL-05 DONE (ACCEPT)
    -> L0-TM-04 DONE (ACCEPT)
    -> L0-TM-04F DONE (ACCEPT)
    -> L0-TM-04F2 DONE (ACCEPT)
    -> L0-TM-04F3 DONE (ACCEPT)
    -> L0-TM-04F4 DONE (ACCEPT)
    -> L0-SOL-06 DONE (ACCEPT)
    -> L0-LU-01 DONE (ACCEPT)
    -> L0-TM-04F5 DONE (ACCEPT)
    -> L0-LU-01F DONE (ACCEPT)
    -> L0-SOL-07 DONE (ACCEPT)
    -> L0-TM-05 DONE (ACCEPT)
    -> L0-TM-05F DONE (ACCEPT)
    -> L0-TM-05F2 DONE (ACCEPT)
    -> L0-LU-02 DONE (ACCEPT)
    -> L0 GAMEPLAY/CONTENT SEQUENCE CLOSED

P12-SOL-01 DONE (ACCEPT)
    -> P12-TM-01 DONE (ACCEPT)
    -> P12-TM-01F DONE (ACCEPT)
    -> P12-TM-02 DONE (ACCEPT)
    -> P12-SOL-02 DONE (ACCEPT)
    -> P12-FX-01F DONE (ACCEPT)
    -> P12-TM-03 DONE (ACCEPT)
    -> P12-TM-03F DONE (ACCEPT)
    -> P12-TM-03F2 DONE (ACCEPT)
    -> P12-FX-01 DONE (ACCEPT)
    -> P12-DS NEEDS_FIX (DS-01)
    -> P12-TM-04 DONE (ACCEPT)
    -> P12-SOL-03 DONE (ACCEPT)
    -> P12-TM-05 DONE (ACCEPT)
    -> P12-TM-05F DONE (ACCEPT)
    -> P12-DS READY (NEW IMMUTABLE ARTIFACT + CLEAN DS-01 RERUN)
    -> P12-DS-MP BACKLOG
    -> P12-M BLOCKED
```

The wire contract alone authorizes no Java implementation, persistence path,
client surface or gameplay theorem. The later accepted gates now authorize
only the completed slices shown above.

---

## 11. Handoff Template

Every active task must produce:

```markdown
# Handoff: <task id>

## Completed
- ...

## Decisions implemented
- ...

## Files changed
- ...

## Contracts referenced
- ...

## Tests and evidence
- command:
- result:

## Migration impact
- none / details

## Known limitations
- ...

## Unresolved questions
- ...

## Next owner
- Sol / Terra High / Terra Medium / Luna

## Exact next task
- ...

## Files the next owner may edit
- ...

## Files the next owner must not edit
- ...
```

Create the directory when the first implementation handoff is written:

```text
docs/handoffs/
```

---

## 12. Board Update Protocol

### When a thread starts

Update:

```text
READY -> IN_PROGRESS
```

Record the thread name or identifier in the task section if available.

### When implementation finishes

Update:

```text
IN_PROGRESS -> IN_REVIEW
```

Only after the repository handoff exists.

### When review finds a defect

Update:

```text
IN_REVIEW -> NEEDS_FIX
```

Add the exact failing evidence and keep downstream tasks blocked.

### When Sol accepts the gate

Update:

```text
IN_REVIEW -> DONE
dependent BLOCKED task -> READY
```

### When a task is blocked

The task must name:

- the dependency;
- the missing artifact;
- the owner who can unblock it;
- the next read-only action, if any.

“Waiting” without a named dependency is not an accepted board state.

---

## 13. Current Recommended Dispatch

Open or update the threads in this order:

1. Complete the manual full-ATM10 candidate check recorded in
   `docs/P12_ATM10_CANDIDATE_AND_WARNING_REVIEW.md`.
2. Commit the accepted repository state, prove a clean tree, then create the
   new immutable JAR/batch and rerun DS-01 from a clean checkpoint.
3. Do not start DS-02 or any later single-client row until the new DS-01 result
   is accepted.
4. Keep DS-06/two-client evidence in `P12-DS-MP BACKLOG`; it is neither a pass
   nor a prerequisite for the single-client recheck.
5. Keep Luna and Terra High idle.
6. Keep A0-6 external loaders in `BACKLOG`; the W4 decision requires a future
   Sol-owned contract before any semantic review or implementation.

Do not start external loaders, persistence/schema changes, mutable functional
editing, theorem implementation or public extension APIs.

The next board transition should be:

```text
A0-TM-04F DONE (ACCEPT)
    -> A0-W4-GATE DONE (ACCEPT)
    -> A0 CYCLE 2 CLOSED

L0-SOL-01 DONE (ACCEPT)
    -> L0-TM-01 NEEDS_FIX

L0-TM-01 NEEDS_FIX
    -> L0-TM-01F NEEDS_FIX
    -> L0-TM-01F2 NEEDS_FIX
    -> L0-TM-01F3 DONE (ACCEPT)
    -> L0-TH-01 DONE (APPROVE)
    -> L0-SOL-02 DONE (ACCEPT)
    -> L0-SOL-03 DONE (ACCEPT)
    -> L0-TM-02 DONE (ACCEPT)
    -> L0-TM-02F DONE (ACCEPT)
    -> L0-SOL-04 DONE (ACCEPT)
    -> L0-TM-03 DONE (ACCEPT)
    -> L0-TM-03F DONE (ACCEPT)
    -> L0-SOL-05 DONE (ACCEPT)
    -> L0-TM-04 DONE (ACCEPT)
    -> L0-TM-04F DONE (ACCEPT)
    -> L0-TM-04F2 DONE (ACCEPT)
    -> L0-TM-04F3 DONE (ACCEPT)
    -> L0-TM-04F4 DONE (ACCEPT)
    -> L0-SOL-06 DONE (ACCEPT)
    -> L0-LU-01 DONE (ACCEPT)
    -> L0-TM-04F5 DONE (ACCEPT)
    -> L0-LU-01F DONE (ACCEPT)
    -> L0-SOL-07 DONE (ACCEPT)
    -> L0-TM-05 DONE (ACCEPT)
    -> L0-TM-05F DONE (ACCEPT)
    -> L0-TM-05F2 DONE (ACCEPT)
    -> L0-LU-02 DONE (ACCEPT)
    -> L0 GAMEPLAY/CONTENT SEQUENCE CLOSED

P12-SOL-01 DONE (ACCEPT)
    -> P12-TM-01 DONE (ACCEPT)
    -> P12-TM-01F DONE (ACCEPT)
    -> P12-TM-02 DONE (ACCEPT)
    -> P12-SOL-02 DONE (ACCEPT)
    -> P12-FX-01F DONE (ACCEPT)
    -> P12-TM-03 DONE (ACCEPT)
    -> P12-TM-03F DONE (ACCEPT)
    -> P12-TM-03F2 DONE (ACCEPT)
    -> P12-FX-01 DONE (ACCEPT)
    -> P12-DS NEEDS_FIX (DS-01)
    -> P12-TM-04 DONE (ACCEPT)
    -> P12-SOL-03 DONE (ACCEPT)
    -> P12-TM-05 DONE (ACCEPT)
    -> P12-TM-05F DONE (ACCEPT)
    -> P12-DS READY (NEW IMMUTABLE ARTIFACT + CLEAN DS-01 RERUN)
    -> P12-DS-MP BACKLOG
    -> P12-M BLOCKED

A0-6 BACKLOG
    -> requires a new Sol-owned contract
```

Detailed post-adapter task definitions, required documentation, outputs, and
blockers are in `docs/A0_POST_ADAPTER_DELIVERY_PLAN.md`.
