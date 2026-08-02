# P12-TM-03 Final Gate Acceptance

**Date:** 2026-08-02  
**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Tasks:** `P12-TM-03`, `P12-TM-03F`, `P12-TM-03F2` are `DONE`

## Scope accepted

Sol reviewed the complete production and test delta from the initial FX-R3
correction, the blocker resolution, F review and F2 test-only correction:

```text
docs/handoffs/P12_TM_03_HANDOFF.md
docs/handoffs/P12_TM_03F_HANDOFF.md
docs/handoffs/P12_TM_03F2_HANDOFF.md
docs/P12_TM_03_BLOCKER_RESOLUTION.md
docs/P12_TM_03_GATE_REVIEW.md
docs/P12_TM_03F_GATE_REVIEW.md
```

The accepted implementation provides one package-private paired knowledge
publication authority. It prepares complete immutable definition and alias
candidates, validates both before mutation, commits private backing owners
under one coordinator order and exposes one eagerly initialized volatile
generation to public readers.

No public signature, schema, Data Component, persistence, networking,
client/UI, content, KubeJS precedence, gameplay theorem, `ProgramGraph`,
`GuidedWorkspaceState` or `ProgramSurfaceMode` boundary changed.

## Finding closure

### FX-R3 — closed in automated/common authority

A failed resource decode, schema check, path check, grant validation, collision,
alias validation or global limit no longer publishes a reduced definitions map
or a mixed definitions/aliases generation. Rejected candidates preserve the
exact prior snapshot objects and no success log is emitted.

The valid same-resource `vital_correspondence` replacement publishes
`successful_casts=3`; malformed candidates retain it; a later fully valid
removal publishes the legitimate built-in fallback value `2`.

### P12-03-R1 — closed

The public alias registration/publication entry points no longer acquire the
aliases class monitor before entering the coordinator. All mutable static
routes use one coordinator-first order. The bounded opposing Kube/reload vector
completes under timeout and both readers resolve the final paired generation.

### P12-03-R2 — closed

The paired generation is initialized eagerly from the two raw initial
snapshots. Public readers no longer have independent null fallbacks, so backing
owner writes before a later volatile swap are not reader authorities.

### P12-03-R3 / P12-03F-R1 — closed

The dynamic external Java probe, hard-coded build paths, encoding replacement,
filesystem input and child process were removed.

The new named NeoForge GameTest is:

```text
P12KnowledgeReloadGameTests
  .p12KnowledgeReloadRealLoadRejectsMalformedResourcesAtomically
```

It calls the real package-local
`KnowledgeDefinitionReloadListener.load(ResourceManager)` and `apply` with
bounded in-memory resources. Runtime logs independently showed:

- valid replacement publication;
- `Unsupported schema_version` rejection without publication;
- malformed JSON/EOF rejection without publication;
- invalid resource-path rejection without publication;
- final empty-data publication restoring built-in state.

The GameTest asserts exact prior definition and alias object identities after
each rejected load and restores built-in data before success.

### P12-03-R4 — closed

Paired-authority JUnit vectors exercise:

```text
epiphanies: 256 accepted / 257 rejected
discoveries: 1,024 accepted / 1,025 rejected
aliases: 4,096 accepted / 4,097 rejected
```

Every rejected boundary preserves both exact prior live objects. Definition
and alias Kube precedence are observed through the paired coordinator and the
existing public reader methods.

## Reproduced commands

Focused uncached JUnit:

```text
KnowledgeDefinitionReloadListenerTest: 4
KnowledgeReloadPublicationTest: 9
KnowledgeDefinitionRegistryTest: 2
KnowledgeAliasRegistryTest: 2
total: 17 passed; 0 failures; 0 errors; 0 skipped
BUILD SUCCESSFUL
```

Clean global JUnit:

```text
526 passed; 0 failures; 0 errors; 0 skipped
BUILD SUCCESSFUL
```

Global GameTest server:

```text
59/59 required GameTests passed in 4.323 s
BUILD SUCCESSFUL
```

The increase from 58 to 59 is exactly the single named P12 knowledge-reload
GameTest above. P12-TM-03F2 adds no second hidden GameTest.

Standard build and whitespace:

```text
build: BUILD SUCCESSFUL
git diff --check: pass; Windows line-ending warnings only
```

## Known limitation

The JUnit Kube precedence/concurrency vectors add unique Kube records to their
test JVM because the production API intentionally has no unregister operation.
They use isolated ids and the global suite passes, but `@AfterEach` restores
data-pack state rather than claiming to undo Kube startup registration.

## External gate disposition

This acceptance closes the automated/common correction only. It does not claim
the standalone dedicated-server DS-02 rerun.

The current working tree is not an immutable accepted source revision: the
accepted implementation and gate documents are not yet part of `HEAD`, and
runtime logs are also dirty/untracked. Therefore:

- `P12-FX-01` moves from product `NEEDS_FIX` to `BLOCKED` on a clean immutable
  revision and corrected standalone valid -> malformed -> removal recheck;
- `P12-DS` remains `BLOCKED`;
- no model task is dispatchable until the accepted delta is committed and the
  repository is clean;
- DS-06/two-client evidence remains `BACKLOG` and is not inferred from this
  acceptance.

After a clean commit, Sol must run a fresh clean build, freeze the new JAR hash,
replace the standalone fixture JAR and repeat DS-02. Only a retained
`successful_casts=3` through the malformed generation can close FX-R3 at the
external runtime gate.
