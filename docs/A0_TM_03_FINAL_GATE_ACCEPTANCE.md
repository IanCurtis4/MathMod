# A0-TM-03 Final Gate Acceptance

**Task:** task 7 / `A0-TM-03` with corrections F, F2, and F3  
**Date:** 2026-07-26  
**Owner:** Sol  
**Decision:** `ACCEPT`

## Accepted behavior

- `BuiltInAuthoringMetadata.snapshot()` is authoritative for all 11 category
  descriptors and all 67 ordered forms consumed by the Guided palette.
- Registry metadata supplies category/form ordering, labels, compact notation,
  icon ids, search candidates, numeric descriptors, tooltip title, narrator
  title, and bounded technical fallback.
- Existing category colors and player-visible ordering remain compatible.
- Existing stable form ids remain invocation identity.
- Legacy actions remain only the bounded Guided mutation, knowledge, packet,
  and exact replay authority.
- Numeric inputs use descriptor defaults, bounds, and canonicalization.
- Missing presentation never substitutes another form.
- Existing pointer and keyboard paths retain the same stable-form result.
- No persistence, schema, graph, mode, Data Component, networking/payload,
  execution/inscription, Inspector, localization, or public API boundary
  changed.

## Final correction evidence

The F3 harness now:

1. activates non-parameterized `Self` with keyboard Enter;
2. asserts the Guided workspace contains exactly `Self`;
3. focuses the actual Laboratory search field and enters `simpson`;
4. asserts Simpson is the first filtered registry form;
5. pointer-activates that row;
6. asserts the active action is `SIMPSON_INTEGRAL`;
7. asserts five visible finite numeric defaults before capture.

The three real integrated-client captures show the correct Simpson descriptor
dialog and no `1simpson` contamination:

```text
run/client/screenshots/mathmod-authoring-registry-palette-en_us-1024x800-preview.png
run/client/screenshots/mathmod-authoring-registry-palette-pt_br-1024x800-preview.png
run/client/screenshots/mathmod-authoring-registry-palette-pt_br-640x480-preview.png
```

Matching logs contain the successful harness screenshot result:

```text
run/client/logs/authoring-registry-palette-en_us-1024x800.log
run/client/logs/authoring-registry-palette-pt_br-1024x800.log
run/client/logs/authoring-registry-palette-pt_br-640x480.log
```

## Verification

- Forced focused boundary: 137 tests, 0 failures, 0 errors, 0 skipped.
- Standard build: `BUILD SUCCESSFUL`.
- F3 changed only its four granted paths and released all four.

## Superseded findings

- A0-4-R1 through A0-4-R3: closed.
- A0-4-FR1 through A0-4-FR4: closed.
- A0-4-F2R1: closed.

The earlier review documents remain historical evidence and do not represent
the current gate state.

## Operational result

`A0-TM-03`, `A0-TM-03F`, `A0-TM-03F2`, and `A0-TM-03F3` are complete.
Task 7 is accepted. Its ownership is released.

The A0-TM-03 dependency of `A0-TM-04` is satisfied. With the already accepted
Luna evidence, task 8 may transition from `BLOCKED` to `READY` under the
separate ownership in `docs/A0_TM_04_READINESS_ACCEPTANCE.md`.
