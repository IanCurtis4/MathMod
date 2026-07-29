# A0-TM-03 Pre-dispatch Readiness Review

**Reviewed task:** task 7 / `A0-TM-03`  
**Date:** 2026-07-26  
**Owner:** Sol  
**Decision:** `NOT READY`

## Dependency status

| Dependency | State | Evidence |
|---|---|---|
| A0-3 adapter gate | `DONE` | `docs/A0_LEGACY_ADAPTER_GATE_ACCEPTANCE.md` |
| Terminology/content decision | `DONE` | `docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md` |
| A1 read-only canvas | `NEEDS_FIX` | `docs/A1_READONLY_GATE_REVIEW.md` |
| A1 bounded correction | `READY` | `A1-TM-READONLY-F` |
| Luna content delta | accepted | `docs/handoffs/A0_LU_01_HANDOFF.md` |
| Luna handoff evidence | `NEEDS_FIX` | `docs/A0_LU_01_GATE_REVIEW.md` |
| Luna evidence correction | `READY` | `A0-LU-01F` |
| Exact A0-TM-03 integrator ownership | not assigned | deferred until correction files are released |

## Verified repository health

- Changed language and Patchouli JSON parse as UTF-8.
- EN/PT-BR key sets are identical: 804/804.
- Focused client-screen suite executed without cache:
  119 tests, 0 failures, 0 errors, 0 skipped.
- Standard Gradle build: `BUILD SUCCESSFUL`.
- No forbidden persistent, graph, mode, Data Component, or networking file
  changed.

Build health does not override missing acceptance behavior.

## Blocking gates

### `A1-TM-READONLY-F`

Must close:

- coherent zoom geometry and hit-testing;
- bidirectional pan;
- named input sockets in addition to edge labels;
- localized narration for read-only/focus/socket state;
- viewport clipping and executable minimum/ATM10 evidence.

### `A0-LU-01F`

Must provide:

- exact 67-form icon-reuse/file-existence manifest;
- exact EN/PT-BR narrator matrix with fallback and owner;
- old/new bilingual values for every changed Inspector key.

## Ownership decision

No A0-TM-03 screen, test, or preview write ownership is assigned yet.
`A1-TM-READONLY-F` still owns the relevant read-only screen files. Assigning
them concurrently to the A0-TM-03 integrator would violate the serialization
rule.

After both correction handoffs are accepted, Sol must:

1. mark the A1 and Luna parent tasks `DONE`;
2. confirm all correction ownership is released;
3. name one A0-TM-03 integrator;
4. enumerate exact screen, test, preview, authoring-consumption, and handoff
   files;
5. keep all persistence, graph, networking, Data Component,
   `ProgramSurfaceMode`, and public API boundaries forbidden.

## Allowed work now

The following may run in parallel:

```text
A1-TM-READONLY-F
A0-LU-01F
```

`L0-SOL-01` may also proceed as independent documentation work.

## Forbidden dispatch

Do not start:

```text
A0-TM-03
A0-TM-04
A0-W4-GATE
```

The next A0-TM-03 readiness review occurs only after both correction handoffs
exist.
