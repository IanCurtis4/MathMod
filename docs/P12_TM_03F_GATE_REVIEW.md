# P12-TM-03F Gate Review

**Date:** 2026-08-02  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Correction:** `P12-TM-03F2` is `READY`  
**Downstream:** `P12-FX-01` remains `NEEDS_FIX`; `P12-DS` remains `BLOCKED`

## Scope reviewed

Sol reviewed `docs/handoffs/P12_TM_03F_HANDOFF.md`, the complete production and
test delta, and R1-R4 from `docs/P12_TM_03_GATE_REVIEW.md`.

The production correction is structurally within ownership:

- the outer `KnowledgeAliases` synchronized entry points were removed;
- mutable runtime routes enter the paired coordinator before preparation and
  backing-owner commit;
- the paired generation is eagerly initialized and public readers have no null
  fallback;
- the 256/257, 1,024/1,025 and 4,096/4,097 data limits are unchanged;
- public signatures, schemas, persistence, networking, client/UI and gameplay
  authority remain unchanged.

The gate cannot be accepted because the required real listener-load evidence
depends on undeclared machine state and fails in an otherwise valid focused
execution.

## Blocking finding P12-03F-R1 — non-reproducible real-load test

`KnowledgeDefinitionReloadListenerTest` hard-codes:

```text
C:/mathmod-build/MathMod/neoForm/.../writeMinecraftClasspathJunit/classpath.txt
C:/mathmod-build/MathMod/neoForm/.../recompile/classes
```

It then dynamically writes and compiles a Java probe in an external temporary
directory. The Gradle `test` task does not declare the classpath file as an
input or depend on `writeMinecraftClasspathJunit`. The absolute path is also
wrong when the repository path is ASCII, `MATHMOD_BUILD_DIR` is set, the drive
is not `C:`, or the NeoForm step layout changes.

Sol reproduced the ordinary focused command successfully while the stale
classpath file existed. Sol then performed this reversible diagnostic:

1. resolve and verify the exact artifact under the bounded external build
   directory;
2. move only `classpath.txt` to a sibling backup;
3. run the single declared test method through `cleanTest test
   --no-build-cache`;
4. restore the artifact in a `finally` block and verify no backup remained.

Result:

```text
KnowledgeDefinitionReloadListenerTest
  > realResourceManagerLoadRejectsMalformedJsonPathAndSameResourceSchema FAILED
java.nio.file.NoSuchFileException
  at KnowledgeDefinitionReloadListenerTest.java:85

BUILD FAILED
```

Gradle did not regenerate the undeclared input. This is the expected state on
a clean clone that has not previously materialized the JUnit NeoForm launch
classpath. Therefore the handoff's 18-test claim is machine-state-dependent
and cannot close P12-03-R3.

The diagnostic backup was restored successfully. No build artifact or
repository file was deleted.

## Non-blocking review notes carried into the correction

The current concurrency test starts both operations at a `CyclicBarrier`, but
does not deterministically control the old critical lock-acquisition window.
The production lock inversion is nevertheless closed by direct inspection:
there is no longer a public aliases monitor acquired before the coordinator.
F2 must retain the bounded concurrent completion vector; it need not add a
production test hook.

The eager-holder test reads the paired holder but JUnit method order does not
guarantee that it runs before another accepted publication. Direct production
inspection confirms the null fallback was removed. F2 must retain this test;
no production reset hook or reflection is authorized.

Kube registration tests leave unique Kube records in the JUnit process because
the API has no unregister operation. They currently do not collide with other
tests, but the F2 handoff must state this limitation and must not claim complete
global-state restoration from `@AfterEach`.

## Reproduced evidence

With the residual classpath artifact present:

```text
18 focused tests passed
```

With that undeclared artifact absent:

```text
the new real-load method failed; BUILD FAILED
```

The failing focused prerequisite makes global JUnit, GameTest and build success
insufficient for acceptance. Sol does not rerun standalone DS-02 from a delta
whose required regression oracle is not reproducible.

## Bounded correction — P12-TM-03F2

**Owner:** Terra Medium  
**Status:** `READY`  
**Nature:** test/evidence correction; accepted F production is read-only

Writable files:

```text
src/test/java/com/mathmod/knowledge/KnowledgeDefinitionReloadListenerTest.java
src/test/java/com/mathmod/knowledge/KnowledgeReloadPublicationTest.java
src/main/java/com/mathmod/knowledge/P12KnowledgeReloadGameTests.java
docs/handoffs/P12_TM_03F2_HANDOFF.md
```

The new main-source class is authorized only as a NeoForge `@GameTestHolder`
test fixture in package `com.mathmod.knowledge`. It grants no gameplay or
runtime API authority.

All current production implementation files are read-only, including:

```text
KnowledgeAliases.java
KnowledgeDefinitions.java
KnowledgeDefinitionRegistry.java
KnowledgeDefinitionReloadListener.java
KnowledgeReloadPublication.java
```

`build.gradle`, Gradle configuration and public APIs are read-only. Do not fix
the test by declaring or hard-coding another external build artifact.

## Required F2 evidence

1. Remove the dynamic Java source/probe, absolute build paths, encoding
   replacements and external temporary-directory dependency from the JUnit
   test.
2. Preserve the ordinary package-private candidate/apply JUnit vectors and the
   complete paired-publication suite.
3. Add exactly one named GameTest that calls the real
   `KnowledgeDefinitionReloadListener.load(ResourceManager)` and then `apply`
   for:
   - valid same-resource replacement with `successful_casts=3`;
   - malformed schema for that same resolved resource;
   - malformed JSON;
   - invalid resource path returned by a bounded fake `ResourceManager`;
   - exact prior definition and alias object identity after every rejection;
   - restoration to built-in data before success/cleanup.
4. Use no reflection, filesystem fixture, absolute path, network, sleep or
   external process.
5. Keep the concurrent coordinator test bounded and the three accepted/rejected
   limit pairs green.
6. Report the new named L0/P12 GameTest separately and update the global
   GameTest total from the observed run; do not assume it will remain 58.
7. Run the focused JUnit command, clean global JUnit, GameTest server, build and
   `git diff --check`.

The focused JUnit total may decrease when the non-reproducible method is
removed; acceptance depends on named coverage, not preserving the number 18.

## Gate disposition

- `P12-TM-03`: remains `NEEDS_FIX`;
- `P12-TM-03F`: `NEEDS_FIX`;
- `P12-TM-03F2`: `READY`;
- `P12-FX-01`: remains `NEEDS_FIX`;
- `P12-DS`: remains `BLOCKED`;
- no standalone DS row is accepted or executed by this review.
