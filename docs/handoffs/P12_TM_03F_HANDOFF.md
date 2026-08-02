# P12-TM-03F Handoff

**Result:** `DELIVERED — awaiting Sol review`

## Scope and closure

This bounded correction closes the four findings in
`P12_TM_03_GATE_REVIEW.md`; it does not alter `DELIVERY_BOARD.md`.

- **R1 lock order:** `KnowledgeAliases` no longer takes its class monitor
  before entering `KnowledgeReloadPublication`. All mutable alias/definition
  operations enter the package-private coordinator first; preparation and
  commit helpers have no competing outer alias monitor. The focused opposing
  Kube-alias/reload test starts both operations behind a `CyclicBarrier`, uses
  bounded `Future.get`, and confirms both public readers resolve the final
  paired generation.
- **R2 first publication:** the coordinator initializes its immutable paired
  generation from raw initial definition and alias snapshots at class
  initialization. Public reads have no independent null fallback; the initial
  reader vector proves both resolve the same holder before an accepted reload.
- **R3 real listener boundary:** the listener remains read-only. The focused
  test builds a temporary package-local Java probe and runs it in a child JVM
  with the generated Minecraft classpath. It calls real
  `load(ResourceManager)` and `apply` with fake bounded resources, proving
  valid -> same-id malformed-schema, malformed JSON, and invalid-path cases
  retain the exact previous definition and alias objects. No reflection is
  used. The temporary probe avoids Gradle discovering Minecraft-dependent
  helper classes in the ordinary JUnit classpath.
- **R4 limits and precedence:** paired-authority vectors cover accepted/rejected
  256/257 epiphanies, 1,024/1,025 discoveries, and 4,096/4,097 aliases, each
  rejected case retaining both exact live objects. Definition and alias Kube
  precedence are observed through `KnowledgeDefinitions` and
  `KnowledgeAliases` public readers.

## Final authority

`KnowledgeReloadPublication` remains package-private and holds the one
volatile `Generation(definitions, aliases)`. Candidate preparation is immutable
and precedes all backing writes; only the final generation replacement is
reader-visible. There is no rollback path or partial-success log.

## Files changed

- `src/main/java/com/mathmod/knowledge/KnowledgeAliases.java`
- `src/main/java/com/mathmod/knowledge/KnowledgeDefinitions.java`
- `src/main/java/com/mathmod/knowledge/KnowledgeReloadPublication.java`
- `src/test/java/com/mathmod/knowledge/KnowledgeDefinitionReloadListenerTest.java`
- `src/test/java/com/mathmod/knowledge/KnowledgeReloadPublicationTest.java`
- `docs/handoffs/P12_TM_03F_HANDOFF.md`

No public API, schema, Data Component, persistence, networking, client/UI,
content, KubeJS precedence, `ProgramGraph`, `GuidedWorkspaceState`, or
`DELIVERY_BOARD` change is included.

## Focused evidence — 18 passed

`KnowledgeDefinitionReloadListenerTest` (5):

1. `sameResourceValidThenMalformedRetainsBothExactSnapshots`
2. `malformedJsonPathAndRuntimeValidationRetainBothSnapshots`
3. `invalidAliasAndGlobalLimitRetainBothSnapshots`
4. `validRemovalPublishesFallbackAndCandidateMutationCannotLeak`
5. `realResourceManagerLoadRejectsMalformedJsonPathAndSameResourceSchema`

`KnowledgeReloadPublicationTest` (9):

1. `definitionPreparationFailureLeavesBothLiveObjectsUntouched`
2. `aliasPreparationFailureLeavesBothLiveObjectsUntouched`
3. `successfulPublicationReplacesBothReaderAuthoritiesAsOneGeneration`
4. `initialReadersAlreadyResolveOnePairedHolder`
5. `epiphanyLimitsAccept256AndReject257WithoutChangingEitherAuthority`
6. `discoveryLimitsAccept1024AndReject1025WithoutChangingEitherAuthority`
7. `aliasLimitsAccept4096AndReject4097WithoutChangingEitherAuthority`
8. `kubeDefinitionsAndAliasesRetainPrecedenceThroughPairedReaders`
9. `opposingKubeAndReloadOperationsCompleteWithOneCoordinatorOrder`

Existing registry/alias tests retained (4):

1. `dataPackOverridesKubeAndBuiltInDefinitionsByStableId`
2. `grantsAutomaticallyBecomeConstructionRequirements`
3. `resolvesAliasChainsWithinOneKnowledgeKind`
4. `rejectsSelfAliasesConflictsAndCycles`

## Commands and results

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.knowledge.KnowledgeDefinitionReloadListenerTest `
  --tests com.mathmod.knowledge.KnowledgeDefinitionRegistryTest `
  --tests com.mathmod.knowledge.KnowledgeAliasRegistryTest `
  --tests com.mathmod.knowledge.KnowledgeReloadPublicationTest
# BUILD SUCCESSFUL — 18 focused tests

.\gradlew.bat cleanTest test --no-daemon
# BUILD SUCCESSFUL — 527 global JUnit tests

.\gradlew.bat runGameTestServer --no-daemon
# 58/58 required GameTests passed in 4.727 s

.\gradlew.bat build --no-daemon
# BUILD SUCCESSFUL

git diff --check
# passed; only Windows line-ending warnings were emitted
```

The global GameTest server ran 58 tests; P12-TM-03F adds none and makes no DS
or external-fixture claim. Sol must review this delta and rerun the frozen
DS-02 valid -> malformed -> removal fixture from a clean immutable revision.
