# P12-TM-03 Gate Review

**Date:** 2026-08-02  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Correction:** `P12-TM-03F` is `READY`  
**Downstream:** `P12-FX-01` remains `NEEDS_FIX`; `P12-DS` remains `BLOCKED`

## Scope reviewed

Sol reviewed `docs/handoffs/P12_TM_03_HANDOFF.md`, every production and test
file in its inventory, the original correction gate, and the paired-publication
ownership amendment. The review used the repository delta rather than the
handoff claims.

The delta stays within the amended ownership. `KnowledgeReloadPublication` is
package-private, public method signatures remain unchanged, and no schema,
Data Component, persistence, networking, client/UI, content, theorem,
`ProgramGraph`, `GuidedWorkspaceState` or `ProgramSurfaceMode` file changed.

The implementation correctly improves the original state: complete definition
and alias candidates are prepared before normal publication, caller maps are
copied, rejected preparation leaves the final paired holder unchanged, and the
listener no longer reports partial success. Those facts are not sufficient for
acceptance because the following concrete defects remain.

## Findings

### P12-03-R1 — inverse lock order can deadlock

`KnowledgeAliases.registerKube` and `KnowledgeAliases.publishData` remain
`static synchronized`. They acquire the `KnowledgeAliases.class` monitor and
then enter a synchronized `KnowledgeReloadPublication` method.

The definition/reload path acquires the locks in the opposite order: it enters
the synchronized coordinator first and later calls synchronized
`KnowledgeAliases.prepareData`, `prepareCurrent` or `commit`.

A reproducible schedule is:

1. thread B enters `KnowledgeReloadPublication.publish` and owns the
   coordinator monitor;
2. thread A enters `KnowledgeAliases.registerKube` and owns the aliases class
   monitor, then waits for the coordinator;
3. thread B reaches `KnowledgeAliases.prepareData` and waits for the aliases
   class monitor.

Both threads now wait permanently. The handoff claims one coordinator but does
not identify this second outer lock authority. Passing sequential tests cannot
close this concurrency defect.

### P12-03-R2 — first publication is not a single visibility point

`KnowledgeReloadPublication.current` starts as `null`. Before its first volatile
generation swap, public readers fall back independently to
`KnowledgeDefinitionRegistry.snapshot` and `KnowledgeAliases.current`.

The first `commit` mutates the definition backing owner, then the alias backing
owner, and only afterward assigns `current`. Between the first two writes, a
reader can observe new definitions through the null fallback and old aliases
through the other fallback. This is the exact mixed-generation window the
amendment forbids.

`successfulPublicationReplacesBothReaderAuthoritiesAsOneGeneration` inspects
only the final state. It neither forces a fresh `current == null` publication
nor observes the commit window, so the stated oracle does not prove the claim.

The correction must establish the prior paired holder before any backing-owner
mutation, or use an equivalent initialization design that makes the paired
holder the sole authority from the first publication onward.

### P12-03-R3 — malformed/path tests bypass the real load boundary

The focused listener tests construct `CandidatePublication.LoadResult`
directly. Their malformed JSON, invalid path and malformed same-resource cases
insert prewritten strings into `failures`; none invokes
`KnowledgeDefinitionReloadListener.load(ResourceManager)` or causes a real
resource open/parse/path failure.

Production inspection suggests that the listener records those failures, but
the required listener-boundary regression evidence is absent. A future change
could stop adding a load failure while all four current tests remain green.

The correction must exercise `load` with bounded fake resources/resource
manager inputs and then apply the returned candidate. Reflection is not
permitted.

### P12-03-R4 — required limit and Kube coordinator vectors are incomplete

The amended gate explicitly retains the 256 epiphany, 1,024 discovery and
4,096 alias limits plus Kube precedence through the paired authority.

The new tests exercise only 257 epiphanies. The two existing registry tests
exercise a standalone `KnowledgeDefinitionRegistry`, not the static paired
coordinator. There is no 1,024/1,025 discovery vector, 4,096/4,097 alias vector,
or Kube registration/precedence assertion through
`KnowledgeReloadPublication` and both public readers.

All three boundaries and both definition/alias Kube paths must be tested at
the paired publication authority. Rejected over-limit candidates must retain
both exact prior live objects.

## Reproduced commands

Focused command, including the optional coordinator test:

```text
11 tests, 0 failures, 0 errors, 0 skipped
BUILD SUCCESSFUL
```

Global JUnit:

```text
520 tests, 0 failures, 0 errors, 0 skipped
BUILD SUCCESSFUL
```

Global GameTest server:

```text
58/58 required GameTests passed in 4.350 s
BUILD SUCCESSFUL
```

Standard build and whitespace check:

```text
build: BUILD SUCCESSFUL
git diff --check: pass; line-ending warnings only
```

These results confirm the handoff's counts but do not override R1-R4.

## Bounded correction — P12-TM-03F

**Owner:** Terra Medium  
**Status:** `READY`

Writable production files:

```text
src/main/java/com/mathmod/knowledge/KnowledgeAliases.java
src/main/java/com/mathmod/knowledge/KnowledgeDefinitions.java
src/main/java/com/mathmod/knowledge/KnowledgeReloadPublication.java
```

`KnowledgeDefinitions.java` is writable only if a package-private raw initial
snapshot accessor is necessary to seed the first paired holder. No public
signature or visibility change is allowed.

Writable tests:

```text
src/test/java/com/mathmod/knowledge/KnowledgeDefinitionReloadListenerTest.java
src/test/java/com/mathmod/knowledge/KnowledgeReloadPublicationTest.java
```

Handoff:

```text
docs/handoffs/P12_TM_03F_HANDOFF.md
```

Read-only production files include
`KnowledgeDefinitionReloadListener.java`, `KnowledgeDefinitionRegistry.java`,
`KnowledgeAliasRegistry.java` and `KnowledgeDefinitionSnapshot.java`. Stop and
escalate with a concrete counterexample before editing any read-only production
file.

Required correction evidence:

1. one lock order for every public/package-private production entry point;
2. a bounded concurrent test that completes both opposing operations under a
   timeout and proves final paired authority without sleeps as its oracle;
3. first-publication evidence beginning before any accepted paired generation,
   proving no independent fallback can expose mixed authority;
4. real `load(ResourceManager)` malformed JSON, invalid path and same-resource
   malformed vectors followed by `apply`;
5. accepted/rejected boundary vectors for 256/257 epiphanies, 1,024/1,025
   discoveries and 4,096/4,097 aliases through the paired coordinator;
6. definition Kube and alias Kube precedence through the coordinator, observed
   through both public reader authorities;
7. exact prior object identity after every rejected candidate;
8. the original 11 focused methods remain green, with corrected oracles rather
   than count-only additions.

Use latches/barriers for concurrency sequencing. A timeout alone, a sleep-based
test, or inspection only after publication does not prove R1/R2 closure.

Run the original mandatory focused/global/GameTest/build commands, adding the
coordinator test to the focused command. Report exact focused method names and
counts plus the separate global GameTest count.

All original forbidden boundaries remain in force. Do not execute or claim the
standalone DS-02 row; Sol performs that rerun only after P12-TM-03F acceptance.
