# P12-TM-03F2 Handoff

**Result:** `DELIVERED — awaiting Sol review`

## Scope

F2 removes the non-reproducible external NeoForm/JVM probe from JUnit and adds
one self-contained NeoForge GameTest. Accepted F production remains unchanged.
No `DELIVERY_BOARD`, schema, persistence, networking, UI, gameplay, or public
API file changed.

## Real load evidence

`P12KnowledgeReloadGameTests.p12KnowledgeReloadRealLoadRejectsMalformedResourcesAtomically`
is the single new P12 GameTest. It calls the real package-local
`KnowledgeDefinitionReloadListener.load(ResourceManager)` followed by `apply`
using a bounded in-memory fake resource manager. It proves, in order:

1. valid same-resource replacement publishes `successful_casts=3`;
2. malformed schema for the same resolved resource retains the exact prior
   definition and alias objects;
3. malformed JSON retains both exact prior objects;
4. an invalid path returned by the fake manager retains both exact prior
   objects;
5. cleanup publishes empty datapack data and restores the built-in value `2`.

It uses no reflection, filesystem fixture, absolute path, external process,
network, or sleep.

## Preserved F evidence

- Alias outer synchronized entry points remain removed; mutable routes enter
  `KnowledgeReloadPublication` first.
- Eager paired generation remains the sole public reader authority.
- The bounded concurrent Kube-alias/reload vector remains.
- The accepted/rejected 256/257, 1,024/1,025 and 4,096/4,097 paired-limit
  vectors remain.
- Definition and alias Kube precedence vectors remain.

The JUnit suite contains 17 focused methods: 4 listener candidate/apply
methods, 9 paired-publication methods, 2 definition-registry methods, and 2
alias-registry methods. The removed dynamic real-load JUnit method is
intentionally replaced by the named runtime GameTest above.

## Files changed by F2

- `src/test/java/com/mathmod/knowledge/KnowledgeDefinitionReloadListenerTest.java`
- `src/test/java/com/mathmod/knowledge/KnowledgeReloadPublicationTest.java`
- `src/main/java/com/mathmod/knowledge/P12KnowledgeReloadGameTests.java` (new
  test fixture only)
- `docs/handoffs/P12_TM_03F2_HANDOFF.md`

## Commands and results

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.knowledge.KnowledgeDefinitionReloadListenerTest `
  --tests com.mathmod.knowledge.KnowledgeDefinitionRegistryTest `
  --tests com.mathmod.knowledge.KnowledgeAliasRegistryTest `
  --tests com.mathmod.knowledge.KnowledgeReloadPublicationTest
# BUILD SUCCESSFUL — 17 focused tests

.\gradlew.bat cleanTest test --no-daemon
# BUILD SUCCESSFUL — 526 JUnit tests

.\gradlew.bat runGameTestServer --no-daemon
# 59/59 required GameTests passed in 4.903 s

.\gradlew.bat build --no-daemon
# BUILD SUCCESSFUL

git diff --check
# passed; only Windows line-ending warnings emitted
```

## Limitations

P12-TM-03F2 does not claim a dedicated-server row or an external DS-02 rerun.
Unique Kube records added by the existing JUnit precedence test remain in that
JUnit process because the production API has no unregister operation; they use
non-colliding ids and this handoff does not claim global-state restoration.
Sol must review this delta and rerun the frozen external valid -> malformed ->
removal sequence from a clean immutable revision.
