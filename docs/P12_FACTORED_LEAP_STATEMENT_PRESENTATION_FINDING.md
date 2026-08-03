# P12 Factored Leap Statement Presentation Finding

**Date:** 2026-08-02  
**Owner:** Sol  
**Finding:** `PRESENTATION_FAILURE`  
**Resolution contract:** `docs/P12_FACTORED_LEAP_STATEMENT_PRESENTATION_CONTRACT.md`  
**Status:** `P12-SOL-03`, `P12-TM-05` and `P12-TM-05F` are `DONE` with `ACCEPT`; single-client `P12-DS` is `READY`

## 1. Reproduced counterexample

The real EN-US client preview at 1024x800, GUI scale 2, measured the exact
frozen Factored Leap statement at the production header width and reported:

```text
available width: 141 px
formula: let halve(v)=v*0.5 in push(self,halve(look)+halve((0,1,0)))
result: more than two rendered lines
```

This is not a localization error. Both
`docs/L0_FIRST_GAMEPLAY_THEOREM_SPECIFICATION.md` and
`docs/L0_INTERNAL_GAMEPLAY_INTEGRATION_READINESS.md` freeze that exact formula.
Changing it would change the accepted theorem presentation identity.

## 2. Production evidence

`TheoremStatementPresentation.lines` falls back to font wrapping when its
preferred semantic split cannot fit into two lines. `RuneProgrammerScreen` then
iterates only through `Math.min(2, formulaLines.size())`. At 141 px the current
Factored Leap statement therefore has content beyond the two lines that
production draws.

The earlier L0 integration contract correctly required the exact formula and
forbade an incidental screen change, but its acceptance did not include a
Factored Leap-specific runtime oracle at this width. The new client result is a
real presentation counterexample, not permission to weaken the formula or
pretend that the hidden tail is visible.

## 3. Ownership decision

This is not Luna work: copy or localization cannot change a frozen semantic
formula. It is also outside P12-TM-04, whose client ownership is evidence
harness-only.

Sol completed `P12-SOL-03` in
`docs/P12_FACTORED_LEAP_STATEMENT_PRESENTATION_CONTRACT.md`. The frozen smallest
correction is a dynamic, bounded third statement line whose extra height and
graph viewport displacement share the same production line-height authority.
The correction must preserve:

- the exact full and catalog formulas;
- theorem id, graph and inscription semantics;
- complete mouse, keyboard and narration access to the statement;
- existing graph viewport, scrolling and hit-testing authority;
- EN-US/PT-BR behavior at standard and minimum supported viewports.

The contract authorizes `P12-TM-05` to modify only the enumerated client screen,
harness, tests and evidence files. `TheoremStatementPresentation`, formulas and
localizations remain read-only.

## 4. Sequencing

The exact `laboratory-self-repeat` harness mode may bypass theorem-only
preflights under
`docs/P12_TM_04_HARNESS_PREFLIGHT_CLARIFICATION.md`; this allows R6 to test its
actual Laboratory boundary without claiming theorem presentation acceptance.

Do not generate the next immutable P12-DS artifact until Sol accepts the
`P12-TM-05` implementation handoff. This avoids executing the remaining DS
batch on an artifact already known to contain a presentation failure.
