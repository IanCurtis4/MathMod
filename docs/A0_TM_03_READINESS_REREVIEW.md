# A0-TM-03 Pre-dispatch Readiness Re-review

**Reviewed task:** task 7 / `A0-TM-03`  
**Date:** 2026-07-26  
**Owner:** Sol  
**Decision:** `NOT READY`

This document supersedes the current-state decision in
`docs/A0_TM_03_READINESS_REVIEW.md` while preserving that review as history.

## Closed dependencies

- A0-3 adapter gate: `DONE`;
- terminology/content decision: `DONE`;
- `A0-LU-01`: `DONE`;
- `A0-LU-01F`: `DONE` and accepted in
  `docs/A0_LU_01F_GATE_ACCEPTANCE.md`;
- Luna content ownership: released;
- repository build and current focused screen suite: passing.

## Remaining blocker

`A1-TM-READONLY-F` did not completely close the read-only canvas gate.
`docs/A1_READONLY_CORRECTION_REVIEW.md` records three residual findings:

- edge input labels were removed while sockets were added;
- socket bindings are narrated through the Formula key and viewport values are
  unlabeled;
- reveal/pan bounds use the outer canvas size rather than the smaller rendered
  and scissored inner content rectangle.

`A1-TM-READONLY-F2` is `READY` with exact ownership.

## Task 7 ownership

No A0-TM-03 ownership is assigned while A1-TM-READONLY-F2 owns the overlapping
screen and test files. Task 7 remains `BLOCKED`.

After the F2 handoff is accepted, Sol must perform one final readiness update,
release A1 ownership, and enumerate the exact A0-TM-03 integrator files before
dispatch.

## Work allowed now

```text
A1-TM-READONLY-F2
L0-SOL-01
```

Do not start `A0-TM-03`, `A0-TM-04`, or `A0-W4-GATE`.
