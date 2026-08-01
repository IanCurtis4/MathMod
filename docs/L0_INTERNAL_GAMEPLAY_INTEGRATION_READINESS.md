# L0-SOL-07 — Internal Gameplay Integration Readiness

**Task:** `L0-SOL-07`  
**Date:** 2026-07-29  
**Owner:** Sol  
**Decision:** `ACCEPT`  
**Unblocks:** `L0-TM-05` only

## 1. Purpose and repository finding

This amendment resolves the last documented prerequisite for the first scoped
functional gameplay theorem.

The repository currently places:

```text
com.mathmod.screen.RuneProgrammerMenu
com.mathmod.program.ScopedFunctionalInscriptionService
```

in different packages. `ScopedFunctionalInscriptionService`,
`ScopedCommitAuthority`, `ScopedCommitResult`,
`ScopedProgramComponentTransaction` and `ScopedProgramPersistence` are
deliberately package-private. The menu therefore cannot invoke the accepted
functional transaction directly.

Moving the menu, moving the transaction, making all transactional types public,
duplicating the transaction in `com.mathmod.screen`, using reflection, or
routing through graph-only `ProgramStorage` would violate accepted ownership or
authority. This gate selects one narrow internal Java reachability bridge while
retaining the existing package-private coordinator and transaction machine.

This document authorizes implementation but changes no Java itself.

## 2. Frozen integration shape

### 2.1 One internal entry point

Create exactly:

```text
src/main/java/com/mathmod/program/ScopedFunctionalInscriptionEntryPoint.java
```

It is a non-instantiable `final` class annotated:

```java
@org.jetbrains.annotations.ApiStatus.Internal
```

Its Java visibility may be `public` only because
`com.mathmod.screen.RuneProgrammerMenu` is in another package. This mechanical
visibility is not an accepted MathMod extension API or compatibility promise.
The class must not be exported through `ProgramSurface`, KubeJS, networking,
registries, service loading or documentation for third-party callers.

It exposes exactly one production operation, semantically:

```text
tryInscribeFactoredLeap(
    ServerPlayer player,
    InteractionHand hand,
    BooleanSupplier requestStillCurrent
) -> boolean
```

The boolean is `true` only for `ScopedCommitResult.SUCCESS`. It is never a
claim that compilation alone succeeded. No public result DTO, source,
`ProgramGraph`, cost plan, resource list, player-knowledge value, registry
snapshot or transaction state is exposed.

The entry point:

1. rejects null/non-server/stale request state;
2. bootstraps and consults the live accepted rune registry;
3. constructs the frozen canonical source inside `com.mathmod.program`;
4. supplies the live held-stack target;
5. supplies `KnowledgeService.get(player)` as a live supplier;
6. wraps `!requestStillCurrent.getAsBoolean()` as cancellation;
7. invokes the existing `ScopedFunctionalInscriptionService` once;
8. maps failure to existing server feedback without reporting success;
9. returns `true` only after the existing atomic transaction returns success.

There is no overload that accepts source, graph, result, budget, knowledge,
materials or components from the menu or client.

### 2.2 Canonical theorem fixture

Create exactly:

```text
src/main/java/com/mathmod/program/FactoredLeapTheorem.java
```

It remains package-private and non-instantiable. It owns:

- semantic id `mathmod:factored_leap`;
- canonical schema-1 `ScopedProgramSource`;
- result type `Unit`;
- budget limit `24`;
- the accepted De Bruijn binder structure;
- a presentation graph oracle matching the frozen lowering result.

The source is exactly the theorem from
`docs/L0_FIRST_GAMEPLAY_THEOREM_SPECIFICATION.md`: one `halve` lambda, one
let-bound function and two applications. The built-in fixture must use binder
hints `halve`, `vector`, `self`, `forward`, and `lift`.

The presentation graph oracle exists only because the current theorem catalog
and Inspector preview consume `TalismanPreset.graph()`. It is not persisted by
the menu and is not an executable authority for inscription. Tests must compile
the canonical source and prove the compiled graph is semantically identical to
the presentation oracle.

## 3. Catalog and menu routing

### 3.1 Catalog registration

`ProgramPresets` adds one `TalismanPreset`:

| Field | Value |
|---|---|
| button | `37` |
| id | `mathmod:factored_leap` |
| category | `MOVEMENT` |
| name key | `screen.mathmod.rune_programmer.preset_factored_leap` |
| hint key | `screen.mathmod.rune_programmer.factored_leap_hint` |
| formula | `let halve(v)=v*0.5 in push(self,halve(look)+halve((0,1,0)))` |
| catalog formula | `push(halve(look)+halve(up))` |
| icon | `mathmod:scale_vector` |
| provenance | `HORIZON_MEASURERS` |
| graph supplier | `FactoredLeapTheorem` presentation oracle |

The catalog grows from 33 to 34 entries. Button `37` remains internal transport
and must not become a public constant, persistent identity, knowledge id,
narrated identity or compatibility promise.

The frozen functional formulas are an intentional theorem-specific exception
to the existing compact graph-only preset assertions (`no spaces`, `no =`,
outer-effect regex and `catalogFormula.length() <= 18`). `ProgramPresetsTest`
may scope those legacy compactness assertions to the pre-existing 33 entries,
but may not weaken them for those entries. The functional theorem instead
asserts exact equality with the two frozen formulas. Existing rendering already
clips catalog rows to their bounds and wraps the selected statement; no UI code
change is authorized.

The existing generic `RuneProgrammerScreen` catalog path consumes the new
entry. No client/screen Java change is authorized.

### 3.2 Server route

`RuneProgrammerMenu.clickMenuButton` continues to resolve the received numeric
button through `ProgramPresets.presetForButton`.

After knowledge-oriented early feedback and before the generic graph-only save:

1. the server compares the resolved semantic id to
   `mathmod:factored_leap`;
2. it requires `ServerPlayer`;
3. it calls only `ScopedFunctionalInscriptionEntryPoint`;
4. `requestStillCurrent` checks that the player's current container is this
   menu and that `stillValid(player)` remains true;
5. success invalidates the opening projection, synchronizes the held stack and
   emits the existing saved feedback;
6. failure emits no saved feedback and performs no synchronization that could
   imply a committed result.

For this semantic id the method must return before:

```text
ProgramStorage.saveValidated(ItemStack, ProgramGraph)
```

That graph-only route remains unchanged for every existing preset.

The existing client sends only button `37` through the vanilla menu-button
transport. No source, AST, graph, knowledge, result, cost or resource claim
crosses the network, and no custom payload is added.

## 4. Authority and transaction invariants

The following path is the only successful path:

```text
button 37
    -> server resolves mathmod:factored_leap
    -> internal entry point constructs canonical source
    -> ScopedFunctionalInscriptionService
    -> ScopedServerCompileService
    -> complete off-item candidate
    -> immediate final rechecks
    -> ScopedProgramComponentTransaction
```

The accepted responsibilities remain unchanged:

- `ScopedServerCompileService` is the single compile/admission authority;
- `ScopedFunctionalInscriptionService` is the single source/result-bound
  coordinator;
- `ScopedProgramComponentTransaction` is the single six-component mutation and
  rollback machine in production and tests;
- `ProgramGraph` remains the sole executable authority after compilation;
- `ScopedProgramPersistence` remains read-only;
- `ProgramStorage.saveValidated` remains graph-only and must never handle this
  theorem.

The complete candidate is built before any target mutation. Immediately before
the single transaction, the existing service rechecks:

1. request cancellation/current-menu state;
2. target object identity and `ProgrammedTalismanItem` type;
3. stack count and exact item/components snapshot;
4. six-component captured state;
5. rune-registry generation;
6. knowledge-definition snapshot;
7. captured player knowledge against the live supplier;
8. live material-definition list.

Any stale target, request, generation, knowledge or material state returns
failure with no mutation and no saved feedback.

## 5. Persistence, resources and name

Successful inscription persists through the existing transaction:

```text
PROGRAM                       = exact compiled graph
PROGRAM_SCOPED_SOURCE         = exact schema-1 canonical source envelope
PROGRAM_NAME                  = absent
PROGRAM_RESOURCES             = accepted live-policy result, when non-empty
PROGRAM_GUIDED_WORKSPACE      = absent
PROGRAM_CUSTOM_ACTIONS        = absent
```

`PROGRAM_NAME` remains absent. The localized catalog name is presentation copy;
the server must not persist an EN-US or PT-BR rendering as semantic identity.
Saved-item presentation may recognize the compiled graph/source pairing, but
neither name nor graph matching replaces the scoped source authority.

Resource behavior is unchanged:

- the graph requires existing `motion = 1`;
- the compile service calculates recommendations from the captured live
  catalog;
- `InscriptionResourcePolicy.resourcesToPersist` preserves compatible prepared
  resources under the accepted precedence rules;
- no particular material item is hardcoded by the theorem;
- stale material definitions reject without mutation.

No migration is created. Existing graph-only items remain graph-only, and no
existing graph is retroactively assigned this source.

## 6. Failure and feedback policy

The internal entry point maps every non-success result to existing failure
feedback. It must not add or send `item.mathmod.programmed_talisman.saved` for:

- request cancellation;
- stale target;
- stale rune generation;
- stale knowledge;
- stale materials;
- compile/admission rejection;
- commit failure or rollback failure.

The menu invalidates its captured projection and synchronizes the held stack
only on `true`. A compile candidate, graph oracle match or successful preflight
alone cannot cause saved feedback.

Rollback failure remains explicitly logged by the accepted transaction. It
still returns false and cannot become user-visible success.

## 7. Exact L0-TM-05 ownership

Terra Medium may create:

```text
src/main/java/com/mathmod/program/FactoredLeapTheorem.java
src/main/java/com/mathmod/program/ScopedFunctionalInscriptionEntryPoint.java
src/main/java/com/mathmod/program/L0FactoredLeapGameTests.java
src/test/java/com/mathmod/program/FactoredLeapTheoremTest.java
src/test/java/com/mathmod/program/ScopedFunctionalInscriptionEntryPointTest.java
src/test/java/com/mathmod/screen/RuneProgrammerFunctionalTheoremTest.java
docs/handoffs/L0_TM_05_HANDOFF.md
```

Terra Medium may modify only:

```text
src/main/java/com/mathmod/program/ProgramPresets.java
src/main/java/com/mathmod/screen/RuneProgrammerMenu.java
src/test/java/com/mathmod/program/ProgramPresetsTest.java
```

All other files are read-only, including:

```text
src/main/java/com/mathmod/program/ScopedFunctionalInscriptionService.java
src/main/java/com/mathmod/program/ScopedServerCompileService.java
src/main/java/com/mathmod/program/ScopedProgramComponentTransaction.java
src/main/java/com/mathmod/program/ScopedProgramPersistence.java
src/main/java/com/mathmod/program/ProgramStorage.java
src/main/java/com/mathmod/client/**
src/main/java/com/mathmod/network/**
src/main/java/com/mathmod/registry/ModDataComponents.java
src/main/resources/assets/mathmod/lang/**
src/main/resources/assets/mathmod/patchouli_books/**
```

Also read-only are `ProgramGraph`, `GuidedWorkspaceState`,
`ProgramSurfaceMode`, accepted Data Component identities/codecs, schemas,
networking and all existing public supported APIs.

The one `@ApiStatus.Internal` bridge is the exact cross-package exception. It
must not change any existing public signature or claim supported API status.

### 7.1 Bounded operational amendment after server reproduction

The later repository-backed blocker decision in
`docs/L0_TM_05_BLOCKER_REVIEW.md` authorizes Terra Medium to modify exactly:

```text
src/main/java/com/mathmod/program/ScopedFunctionalInscriptionService.java
```

This exception is limited to canonicalizing absent candidate values for
`PROGRAM_NAME` and `PROGRAM_RESOURCES`. It does not authorize any change to
`ScopedProgramComponentTransaction.java`, its exact state equality, the
single transaction machine, recheck ordering, source/result binding or public
surface. All other read-only boundaries above remain in force.

## 8. Focused ordinary tests

### 8.1 `FactoredLeapTheoremTest`

Must prove:

- semantic id, result type and budget;
- exact De Bruijn structure and binder hints;
- one lambda definition and two applications of the same bound function;
- observations outside the lambda and one terminal effect;
- canonical source wire encode/decode round trip;
- 113 charged compile steps;
- 12 nodes, 12 edges and budget 21/24;
- exact per-rune counts, constants, sockets, sharing and output;
- semantic equality between compiled result and presentation graph oracle.

### 8.2 `ScopedFunctionalInscriptionEntryPointTest`

Must prove:

- the type is annotated `@ApiStatus.Internal`;
- the only production operation is theorem-specific;
- no overload accepts client source, graph, result, cost, knowledge, material
  or component state;
- `true` is possible only for transaction success;
- every internal failure result maps to false and never to saved feedback;
- the entry point delegates to the existing service rather than
  `ProgramStorage`.

### 8.3 `RuneProgrammerFunctionalTheoremTest`

Must prove:

- button `37` resolves server-side to `mathmod:factored_leap`;
- the menu routes that id before generic graph-only saving;
- the request-current probe binds the attempt to the live menu and target;
- success alone invalidates projection, synchronizes and reports saved;
- every failure avoids false saved feedback;
- all 33 existing presets retain the previous graph-only route.

### 8.4 `ProgramPresetsTest`

Must prove:

- exactly 34 unique theorem ids and buttons;
- `factored_leap` metadata matches section 3.1;
- its presentation graph passes existing executable validation;
- its graph oracle is not used as the inscription authority;
- existing 33 theorem identities and graphs remain unchanged.

## 9. Dedicated-server GameTests

Create exactly ten separately named `@GameTest` methods in
`L0FactoredLeapGameTests`:

```text
factoredLeapMenuRoutePersistsExactSourceGraphAndResources
factoredLeapExecutesForwardAndUpwardOutcome
factoredLeapMissingKnowledgeRejectsWithoutMutation
factoredLeapStaleTargetRejectsWithoutMutation
factoredLeapCancelledRequestRejectsWithoutMutation
factoredLeapStaleGenerationRejectsWithoutMutation
factoredLeapStaleKnowledgeRejectsWithoutMutation
factoredLeapStaleMaterialsRejectsWithoutMutation
factoredLeapAllCommitFaultsRollbackExactSourceBytes
factoredLeapReloadReadsWithoutMutationAndFailuresNeverReportSuccess
```

The class uses the existing generated `empty` structure and `mathmod`
namespace. It must exercise the real menu route for success and missing
knowledge, the real compile/coordinator/transaction authority for every stale
vector, and the real executor for the movement outcome.

The rollback vector injects before and after each of all six components:
12 distinct attempts. Every attempt verifies exact equality with the prior
six-component state and exact scoped-source envelope bytes.

The reload/read vector must prove that inspection, tooltip-equivalent reads and
reloaded item reads do not compile, migrate, repair or mutate. It must also
prove that no rejected or rollback-failed attempt emits saved feedback.

Current repository baseline:

```text
14 L0 scoped persistence GameTests
5 L0 functional projection GameTests
0 L0 factored leap GameTests
33 global GameTests
```

Expected after this exact task:

```text
10 new L0 factored leap GameTests
29 total L0 GameTests
43 global GameTests
```

The handoff reports all ten names, the new theorem count, total L0 count and
global count separately. Counting a holder annotation as a test is forbidden.

## 10. Required commands

Focused ordinary tests:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.program.FactoredLeapTheoremTest `
  --tests com.mathmod.program.ScopedFunctionalInscriptionEntryPointTest `
  --tests com.mathmod.screen.RuneProgrammerFunctionalTheoremTest `
  --tests com.mathmod.program.ProgramPresetsTest `
  --tests com.mathmod.program.ScopedSourceEnvelopeTest `
  --tests com.mathmod.program.ScopedSourceWireCodecTest `
  --tests com.mathmod.program.ScopedProgramComponentTransactionTest `
  --tests com.mathmod.program.ScopedProgramPersistenceTest `
  --tests com.mathmod.ServerSideIsolationTest
```

Dedicated server:

```powershell
.\gradlew.bat runGameTestServer --no-daemon
```

Standard build:

```powershell
.\gradlew.bat build
```

Build green does not replace the semantic, atomic, stale-state, rollback,
resource, execution or no-false-success evidence above.

## 11. Handoff and gate transition

The required handoff is:

```text
docs/handoffs/L0_TM_05_HANDOFF.md
```

It must include:

- exact changed-file inventory;
- canonical source and graph-oracle evidence;
- compile steps, nodes, edges, budget and per-rune counts;
- proof that button `37` carries no client authority;
- proof that graph-only `ProgramStorage` is bypassed for this theorem;
- authority/recheck matrix;
- source/result/component binding;
- resource preservation evidence;
- rollback matrix for all 12 injection points;
- exact GameTest names and separated counts;
- focused test names/counts and command results;
- confirmation that all existing preset routes remain unchanged;
- confirmation that no supported public API, networking, schema, component
  identity, client or UI Java changed;
- limitations and escalations.

The accepted Patchouli copy correctly says that `factored_leap` is not yet a
runtime card in the pre-implementation slice. Once the theorem implementation
exists, that sentence becomes a post-implementation content follow-up. It is
outside Terra Medium ownership: the TM handoff must flag it for Sol/Luna and
must not edit Patchouli. Acceptance of the implementation does not silently
rewrite the already accepted content history.

Final transition:

```text
L0-SOL-07 DONE (ACCEPT)
    -> L0-TM-05 DONE (ACCEPT)
    -> L0-TM-05F DONE (ACCEPT)
    -> L0-TM-05F2 DONE (ACCEPT)
    -> L0-LU-02 DONE (ACCEPT)
    -> L0 GAMEPLAY/CONTENT SEQUENCE CLOSED
```

The post-handoff findings and exact test-only correction boundary are recorded
in `docs/L0_TM_05_GATE_REVIEW.md` and
`docs/L0_TM_05F_GATE_REVIEW.md`. Final acceptance is recorded in
`docs/L0_TM_05_FINAL_GATE_ACCEPTANCE.md`. The only later L0 task made ready is
the already identified post-implementation Patchouli correction, bounded as
content-only `L0-LU-02` and accepted in
`docs/L0_LU_02_GATE_ACCEPTANCE.md`. No later L0, Java or gameplay follow-up is
authorized.
