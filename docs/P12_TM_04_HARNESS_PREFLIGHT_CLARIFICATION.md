# P12-TM-04 Harness Preflight Clarification

**Date:** 2026-08-02  
**Owner:** Sol  
**Decision:** `ACCEPT ESCALATION`  
**Task state:** `P12-TM-04` is `DONE` with `ACCEPT` under
`docs/P12_TM_04_FINAL_GATE_ACCEPTANCE.md`  
**Scope:** runtime evidence R6 only  
**Second escalation:** `ACCEPT ESCALATION` — statement preflight

## 1. Claim reviewed

The real-client `laboratory-self-repeat` scenario terminates in
`UiPreviewHarness.openPreview` before reaching the Laboratory action:

```text
Theorem catalog formulas exceed 78 px:
mathmod:factored_leap=push(halve(look)+halve(up))
```

Repository control flow confirms that `openPreview` runs
`requireTheoremCatalogFormulaFit` for almost every Rune Programmer preview.
The check executes immediately after constructing the screen and before the
new self-repeat scenario selects the Laboratory tab or invokes Self. This is
therefore not evidence against the P12-TM-04 correction and is not a premature
click.

## 2. Contract resolution

`docs/L0_INTERNAL_GAMEPLAY_INTEGRATION_READINESS.md` freezes
`push(halve(look)+halve(up))` as the exact Factored Leap catalog formula and
explicitly classifies it as an exception to the legacy compact-formula bound.
The same contract states that catalog rows are clipped to their bounds and
authorizes no theorem UI change.

Consequently, the 78-pixel complete-fit assertion is a theorem-catalog oracle.
It is not a valid global precondition for a Laboratory-only runtime scenario.
Changing the formula, its localization, the catalog layout or production
rendering in P12-TM-04 would violate the frozen L0 decision.

## 3. Exact authorization

Terra Medium may modify the already conditionally owned file:

```text
src/main/java/com/mathmod/client/UiPreviewHarness.java
```

The authorized delta is limited to preventing
`requireTheoremCatalogFormulaFit` from running when and only when
`selfRepeatPreview()` / `laboratory-self-repeat` is active. The existing
preflight must remain unchanged and continue to execute for every other mode
for which it currently executes.

### 3.1 Second preflight clarification

The first authorized exclusion was reproduced successfully: the EN-US run
passed the 78-pixel catalog check and then stopped at the next global preflight:

```text
Theorem statements exceed two lines at 141 px:
mathmod:factored_leap=let halve(v)=v*0.5 in push(self,halve(look)+halve((0,1,0)))
```

Repository control flow confirms that this check also runs before the harness
opens the Laboratory. For R6 only, Terra Medium may therefore prevent
`requireTheoremStatementFit` from running when and only when
`selfRepeatPreview()` / `laboratory-self-repeat` is active. The method body and
its execution in every other currently covered mode must remain unchanged.

This supersedes only the earlier sentence requiring the statement preflight to
remain active in `laboratory-self-repeat`; all other restrictions remain in
force.

Acceptable shape:

```text
existing authoring-registry exclusion
    + exact laboratory-self-repeat exclusion from both theorem-only preflights
    -> both preflight method bodies and all other call coverage remain unchanged
```

This clarification does not authorize:

- changing `ProgramPresets`, either Factored Leap formula or any localization;
- changing `RuneProgrammerScreen`, layout, clipping or tooltip behavior;
- weakening/removing `requireTheoremCatalogFormulaFit` globally;
- weakening/removing `requireTheoremStatementFit` globally;
- exempting another preview mode;
- changing public APIs, networking, schemas or Data Components.

## 4. Required evidence

The final `P12_TM_04_HANDOFF.md` must record:

1. the exact conditional delta and this authorization;
2. a focused ordinary test or source-level oracle proving that
   `laboratory-self-repeat` bypasses only the two theorem-only preflights;
3. proof that at least one existing theorem preview still reaches both
   unchanged preflight paths;
4. successful EN-US and PT-BR 1024x800 `laboratory-self-repeat` runs reaching
   the real click, repeated Self, hover/render and capture boundary;
5. confirmation that no theorem formula, localization, production UI or other
   preview-mode exemption changed.

The screenshots do not by themselves accept R6. Sol will inspect their logs,
the real delta and the remaining P12-TM-04 evidence before deciding the gate.

The statement failure is also an independent product presentation finding:
the production widget renders at most two lines while Factored Leap requires
more than two at this width. It is recorded without waiver in
`docs/P12_FACTORED_LEAP_STATEMENT_PRESENTATION_FINDING.md`. The R6 exemption
does not accept or close that defect.

## 5. Disposition

The escalation is valid and unblocked by this document. Terra Medium may
continue P12-TM-04 within the exact boundary above. `P12-DS` remains
`NEEDS_FIX`, and downstream execution remains blocked pending the complete
P12-TM-04 handoff and Sol acceptance.
