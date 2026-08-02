# P12-TM-03 Handoff

**Task:** P12-TM-03  
**Owner:** Terra Medium  
**Result:** `DELIVERED — awaiting Sol review`

## Scope and result

This delta closes FX-R3 only. A malformed or globally invalid knowledge reload
now rejects the complete candidate before a live publication, preserving the
exact prior definition and alias snapshots. The earlier blocked escalation was
accepted by Sol and resolved with the authorized package-private paired
publication coordinator.

`DELIVERY_BOARD.md` was not edited by this task.

## Authority and publication design

`KnowledgeReloadPublication` is the sole package-private live publication
authority for a paired generation. It prepares immutable definition and alias
candidates (including existing built-in and Kube precedence), commits their
private backing maps under one coordinator lock, then performs one `volatile`
replacement of a `Generation(definitions, aliases)` pair.

Public `KnowledgeDefinitions.snapshot()` and `KnowledgeAliases.current()`
resolve through that same generation after its first publication. The backing
owner writes happen before the volatile swap and are not public reader
authorities. There is no sequential reader-visible definition/alias commit.

Kube registration and individual package-private compatibility publication
routes also prepare the unchanged opposite half and use the same paired swap.
Consequently valid Kube precedence and later valid datapack removal retain
their prior behavior without a competing live state.

## Required semantics closure

1. **Complete candidate first:** listener collection records every parse/path/
   schema failure; `CandidatePublication` validates failures and grants before
   invoking the paired coordinator.
2. **Any invalidity unpublishable:** parser, alias, definition, limit,
   collision and alias-cycle failures all occur during immutable preparation.
3. **No mixed generations:** only the coordinator's final volatile `Generation`
   swap is reader-visible; rejected candidates retain both prior objects.
4. **Same-resource valid -> malformed:** focused test retains the exact prior
   `successful_casts=3` snapshot; no reduced map or built-in fallback occurs.
5. **Later removal:** an empty, fully valid later candidate legitimately
   publishes the built-in fallback (`successful_casts=2`).
6. **Diagnostics:** rejected candidates log `rejected before publication`; the
   success log is emitted only after the paired swap.
7. **Immutability:** all prepared map records use `Map.copyOf`; caller mutation
   after candidate construction cannot alter prepared or live data.

## Changed files

Production:

- `src/main/java/com/mathmod/knowledge/KnowledgeDefinitionReloadListener.java`
- `src/main/java/com/mathmod/knowledge/KnowledgeDefinitions.java`
- `src/main/java/com/mathmod/knowledge/KnowledgeDefinitionRegistry.java`
- `src/main/java/com/mathmod/knowledge/KnowledgeAliases.java`
- `src/main/java/com/mathmod/knowledge/KnowledgeReloadPublication.java` (new,
  package-private coordinator authorized by blocker resolution)

Tests:

- `src/test/java/com/mathmod/knowledge/KnowledgeDefinitionReloadListenerTest.java` (new)
- `src/test/java/com/mathmod/knowledge/KnowledgeReloadPublicationTest.java` (new)

Documentation:

- `docs/handoffs/P12_TM_03_HANDOFF.md`

No schema, Data Component, migration, persistence, networking, client, UI,
content, KubeJS precedence, gameplay theorem, `ProgramGraph`,
`GuidedWorkspaceState`, or `DELIVERY_BOARD` delta is included.

## Focused tests — 11 passed

`KnowledgeDefinitionReloadListenerTest` (4):

1. `sameResourceValidThenMalformedRetainsBothExactSnapshots`
2. `malformedJsonPathAndRuntimeValidationRetainBothSnapshots`
3. `invalidAliasAndGlobalLimitRetainBothSnapshots`
4. `validRemovalPublishesFallbackAndCandidateMutationCannotLeak`

`KnowledgeReloadPublicationTest` (3):

1. `definitionPreparationFailureLeavesBothLiveObjectsUntouched`
2. `aliasPreparationFailureLeavesBothLiveObjectsUntouched`
3. `successfulPublicationReplacesBothReaderAuthoritiesAsOneGeneration`

Existing invariant/precedence tests retained (4):

1. `KnowledgeDefinitionRegistryTest.dataPackOverridesKubeAndBuiltInDefinitionsByStableId`
2. `KnowledgeDefinitionRegistryTest.grantsAutomaticallyBecomeConstructionRequirements`
3. `KnowledgeAliasRegistryTest.resolvesAliasChainsWithinOneKnowledgeKind`
4. `KnowledgeAliasRegistryTest.rejectsSelfAliasesConflictsAndCycles`

The paired-publication test obtains the actual `Generation` holder and proves
both public reader authorities return its exact definition and alias members.
The two preparation-failure tests retain exact prior object identities.

## Global validation

- Global JUnit: **520 passed** (`test --no-daemon`).
- Global GameTests: **58/58 passed** in 4.457 s. The server logged batches
  `zz_l0_factored_leap_commit_faults:0`, `defaultBatch:0`, `defaultBatch:1`,
  and `p12_p11_reload:0`, followed by `All 58 required tests passed`.
- P12-TM-03 adds no GameTest; no dedicated-server claim is made.

## Commands and results

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.knowledge.KnowledgeDefinitionReloadListenerTest `
  --tests com.mathmod.knowledge.KnowledgeDefinitionRegistryTest `
  --tests com.mathmod.knowledge.KnowledgeAliasRegistryTest `
  --tests com.mathmod.knowledge.KnowledgeReloadPublicationTest
# BUILD SUCCESSFUL — 11 focused tests

.\gradlew.bat test --no-daemon
# BUILD SUCCESSFUL — 520 tests

.\gradlew.bat runGameTestServer --no-daemon
# 58/58 required GameTests passed

.\gradlew.bat build --no-daemon
# BUILD SUCCESSFUL

git diff --check
# clean (only line-ending warnings from pre-existing Windows working copies)
```

## Limitations and next gate

This is common/server reload correctness only. It does not claim the external
DS-02 fixture rerun, any dedicated-server row, multiplayer evidence, UI
behavior, persistence migration or a new reload framework. `P12-FX-01` remains
`NEEDS_FIX` and `P12-DS` remains blocked until Sol reviews this delta and
reruns the frozen valid -> malformed -> removal fixture from a clean immutable
revision. No further escalation is identified within the amended ownership.
