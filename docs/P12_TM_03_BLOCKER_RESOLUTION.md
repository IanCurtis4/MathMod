# P12-TM-03 Blocker Resolution

**Date:** 2026-08-02  
**Reviewer:** Sol  
**Decision:** `VALID ESCALATION`  
**Task disposition:** `P12-TM-03` remains `READY` with expanded ownership  
**Downstream:** `P12-FX-01` remains `NEEDS_FIX`; `P12-DS` remains `BLOCKED`

## Repository review

Sol reviewed `docs/handoffs/P12_TM_03_HANDOFF.md`, the absence of a production
or test delta, and the current publication authorities:

```text
KnowledgeDefinitionReloadListener
    -> KnowledgeDefinitions / KnowledgeDefinitionRegistry
    -> KnowledgeAliases
```

The handoff is honest about its non-delivery. The focused result from the
withdrawn exploratory patch is diagnostic only and is not acceptance evidence.
The mandatory global commands were correctly not claimed.

Unrelated tracked and untracked runtime logs are outside this decision and
must not be included in the P12-TM-03 delta.

## Counterexample confirmation

The current listener publishes definitions first and aliases second.
Therefore this concrete sequence is possible:

1. a valid definition candidate successfully replaces the live definition
   state;
2. the alias candidate fails while combining with the private Kube alias state,
   for example because it creates a cycle;
3. the method returns with new definitions and old aliases active.

Changing the order does not solve the defect. If aliases publish first, a
valid alias candidate can become live before a definition candidate fails
while combining with private built-in/Kube state, for example because two
discoveries claim the same manuscript id.

Both individual owners construct their own immutable next snapshot before
mutating themselves. However, neither exposes a listener-authorized way to
prepare the complete state, and there is no single atomic authority for the
pair. The listener-only ownership is therefore insufficient for required
semantics 2 and 3 of the correction gate.

The escalation is accepted. Merely adding two sequential prepared commits is
not sufficient: it removes the exception window but still creates two live
publication points. The accepted correction must expose one paired immutable
publication state through one atomic swap, or an implementation with formally
equivalent one-point visibility.

## Expanded production ownership

P12-TM-03 may now edit only:

```text
src/main/java/com/mathmod/knowledge/KnowledgeDefinitionReloadListener.java
src/main/java/com/mathmod/knowledge/KnowledgeDefinitions.java
src/main/java/com/mathmod/knowledge/KnowledgeDefinitionRegistry.java
src/main/java/com/mathmod/knowledge/KnowledgeAliases.java
```

One new package-private coordinator is authorized only if the paired state
cannot be expressed clearly in those files:

```text
src/main/java/com/mathmod/knowledge/KnowledgeReloadPublication.java
```

The new type, if used, must remain package-private and must not become an API,
service locator or general reload framework. `KnowledgeAliasRegistry.java` and
`KnowledgeDefinitionSnapshot.java` remain read-only unless Terra Medium stops
with a new concrete counterexample.

## Required implementation shape

1. Build complete definition and alias candidates, including built-in and Kube
   precedence, without changing live state.
2. Validate all limits, collisions, cycles, grants and structural invariants
   before publication.
3. Represent the accepted definition snapshot, accepted alias snapshot and
   their backing data maps as one immutable prepared generation.
4. Publish that generation through one atomic live-state replacement. Public
   `KnowledgeDefinitions` and `KnowledgeAliases` reads must resolve through
   that same paired authority while preserving their existing signatures.
5. A rejected candidate must preserve the exact previous definition snapshot,
   alias snapshot and backing data maps; rollback must not be the normal commit
   strategy.
6. Valid Kube registration and valid datapack removal must retain their current
   precedence and behavior without introducing a second competing live state.
7. No partial-success log is permitted. A log may claim publication only after
   the paired swap succeeds.

Package-private preparation and commit helpers are allowed. Public method
signatures, visibility and semantics must not change.

## Expanded test ownership

P12-TM-03 may edit or create only:

```text
src/test/java/com/mathmod/knowledge/KnowledgeDefinitionReloadListenerTest.java
src/test/java/com/mathmod/knowledge/KnowledgeDefinitionRegistryTest.java
src/test/java/com/mathmod/knowledge/KnowledgeAliasRegistryTest.java
src/test/java/com/mathmod/knowledge/KnowledgeReloadPublicationTest.java
```

The fourth class is optional and may exist only when the authorized production
coordinator exists.

If either optional class is created, Terra Medium must add its test class to
the focused Gradle command instead of relying on the later global `test` task.

In addition to the original gate vectors, focused evidence must prove:

- definition preparation failure leaves both prior snapshot objects active;
- alias preparation failure leaves both prior snapshot objects active;
- no reader-visible live holder contains definition and alias generations from
  different accepted publications;
- candidate and caller-owned map mutation cannot change a prepared or live
  generation;
- a successful paired publication changes both authorities at the same
  publication point;
- Kube precedence and the existing 256/1,024/4,096 limits remain unchanged.

Tests that merely assert final values after two sequential commits do not prove
the single-publication requirement.

## Status and handoff

The blocked handoff is accepted as a valid escalation, not as task completion.
Terra Medium should resume the same `P12-TM-03` task; no F task is created.
The final implementation handoff remains:

```text
docs/handoffs/P12_TM_03_HANDOFF.md
```

It must replace its blocked result with the delivered delta, exact focused
method names/count, separate global GameTest names/count, mandatory commands,
limitations and ownership inventory.

All forbidden boundaries and mandatory commands in
`docs/P12_KNOWLEDGE_RELOAD_CORRECTION_GATE.md` remain in force.
