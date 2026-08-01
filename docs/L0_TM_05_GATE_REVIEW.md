# L0-TM-05 Gate Review

**Reviewer:** Sol  
**Date:** 2026-07-29  
**Decision:** `NEEDS_FIX`  
**Correction:** `L0-TM-05F`

## 1. Decision

The implementation is not accepted yet.

The bounded correction in `ScopedFunctionalInscriptionService` is conforming:
absent name and resource values are now represented canonically, all immediate
authority rechecks remain after complete candidate construction, and
`ScopedProgramComponentTransaction` is unchanged.

The required commands are green:

```text
focused nine-class command: BUILD SUCCESSFUL
runGameTestServer --no-daemon: 43/43 required GameTests passed
build: BUILD SUCCESSFUL
```

The source contains exactly the ten required Factored Leap GameTest method
names. Together with the existing 14 persistence and five projection L0
methods, the source count is 29 L0 GameTests. This does not replace the missing
contractual behavior below.

## 2. Blocking findings

### L0-TM-05-R1 — The named success vector bypasses the real menu route

`factoredLeapMenuRoutePersistsExactSourceGraphAndResources` calls the local
`inscribe` helper, which constructs `ScopedFunctionalInscriptionService`
directly. It never constructs `RuneProgrammerMenu`, never calls
`clickMenuButton(..., 37)`, and therefore does not exercise:

- server-side button-to-semantic-id resolution;
- the internal entry point from the menu;
- request-current binding to the live menu;
- return before graph-only `ProgramStorage.saveValidated`;
- success-only projection invalidation, held-stack synchronization and saved
  feedback.

`factoredLeapMissingKnowledgeRejectsWithoutMutation` likewise uses a directly
constructed service and compiler. The readiness contract explicitly requires
the real menu route for success and missing knowledge. Source-text assertions
in `RuneProgrammerFunctionalTheoremTest` are not runtime evidence for that
route.

### L0-TM-05-R2 — Successful persistence is not checked exactly

The success vector checks:

- graph equality through `ProgramStorage.get`;
- `CURRENT_VALID` read status;
- only non-null resources.

It does not prove:

- byte-exact equality with the schema-1 canonical source envelope;
- exact equality with the accepted live-policy resource list;
- absence of `PROGRAM_NAME`;
- absence of Guided and custom-action components;
- that the menu-persisted graph is the compile result from the same
  source/result-bound attempt.

The method name and handoff claim exact source, graph and resources, but the
assertions do not.

### L0-TM-05-R3 — Reload/read and no-false-success coverage is absent

`factoredLeapReloadReadsWithoutMutationAndFailuresNeverReportSuccess` creates
an empty talisman, copies it, calls only
`ScopedProgramPersistence.read(stack)`, and compares the stack.

It does not:

- begin with a successfully inscribed Factored Leap item;
- perform a persisted item codec/reload round trip;
- inspect the reloaded graph/source/resource state;
- exercise tooltip-equivalent or projection/inspection reads;
- prove no compile, migration, repair or mutation on those reads;
- observe menu feedback or synchronization for any rejected or rollback-failed
  attempt.

Consequently the method proves neither half of its declared name beyond one
ordinary empty-item persistence read.

### L0-TM-05-R4 — Empty-resource canonicalization has no regression vector

The Sol blocker amendment required proof that an empty live-policy resource
result becomes an absent component, or an equivalent focused regression
vector. The current candidate-copy helper uses the theorem's non-empty
recommendations, and no focused test exercises the empty-resource branch.

The production line is plausible and bounded, but the accepted preflight
defect was representational; both absence branches require executable
evidence.

### L0-TM-05-R5 — The focused theorem tests do not cover their frozen contract

`FactoredLeapTheoremTest` does not assert the required:

- 113 charged compile steps;
- semantic graph isomorphism with the presentation oracle, independent of
  node ids and list order;
- complete De Bruijn indices and all five binder hints;
- observation placement outside the lambda;
- pure lambda body and single terminal effect;
- exact per-rune counts, five constant values, socket wiring and shared
  `self`;
- schema-1 wire encode/decode round trip.

`ScopedFunctionalInscriptionEntryPointTest` and
`RuneProgrammerFunctionalTheoremTest` primarily search Java source text. They
do not execute failure-to-false behavior, saved-feedback exclusion, or the
menu authority boundary.

`ProgramPresetsTest` proves current catalog validity and uniqueness but does
not freeze the identities and exact graphs of the pre-existing 33 presets as
required. Build success cannot substitute for these assertions.

The later clarification in
`docs/L0_TM_05F_GRAPH_ORACLE_CLARIFICATION.md` corrects the original
overconstraint in this finding: Java `ProgramGraph.equals` is not required
between the compiled graph and the presentation oracle. Exact equality remains
required only between the authoritative compiled graph and the graph persisted
from that same attempt.

## 3. Correction boundary — L0-TM-05F

`L0-TM-05F` is `READY` for Terra Medium.

It may modify only:

```text
src/main/java/com/mathmod/program/L0FactoredLeapGameTests.java
src/test/java/com/mathmod/program/FactoredLeapTheoremTest.java
src/test/java/com/mathmod/program/ScopedFunctionalInscriptionEntryPointTest.java
src/test/java/com/mathmod/screen/RuneProgrammerFunctionalTheoremTest.java
src/test/java/com/mathmod/program/ProgramPresetsTest.java
docs/handoffs/L0_TM_05F_HANDOFF.md
```

Production Java is read-only, including:

```text
src/main/java/com/mathmod/program/ScopedFunctionalInscriptionService.java
src/main/java/com/mathmod/program/ScopedProgramComponentTransaction.java
src/main/java/com/mathmod/program/ScopedFunctionalInscriptionEntryPoint.java
src/main/java/com/mathmod/program/FactoredLeapTheorem.java
src/main/java/com/mathmod/program/ProgramPresets.java
src/main/java/com/mathmod/screen/RuneProgrammerMenu.java
```

All prior prohibitions on public APIs, networking, client/UI Java,
`ProgramGraph`, `GuidedWorkspaceState`, `ProgramSurfaceMode`, Data Component
identities/codecs, schemas, persistence migration and Patchouli remain in
force.

Terra Medium must stop and escalate if a real menu-route GameTest cannot be
constructed without a new production seam or a file outside this list. It may
not replace the runtime vector with reflection or source-text matching.

## 4. Required correction evidence

The correction must close R1 through R5 individually, reproduce the exact
nine-class focused command, run all 43 GameTests, run the standard build, and
produce:

```text
docs/handoffs/L0_TM_05F_HANDOFF.md
```

The handoff must report exact test names and counts, distinguish runtime
evidence from source-shape checks, and disclose any remaining harness
limitation.

No downstream task is authorized before Sol accepts L0-TM-05F.
