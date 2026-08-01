# L0-LU-01 — Content and Evidence Gate Review

**Reviewed task:** `L0-LU-01`  
**Date:** 2026-07-29  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Reviewed handoff:** `docs/handoffs/L0_LU_01_HANDOFF.md`

## 1. Result

The content delta is bounded and conforming, but acceptance does not yet fit.
The four Patchouli captures named as PT-BR evidence visibly render the English
book. This leaves an explicit requirement of
`docs/L0_FIRST_GAMEPLAY_THEOREM_SPECIFICATION.md` unproved.

`L0-LU-01` becomes `NEEDS_FIX`. No rollback of the accepted content delta is
requested. `L0-TM-05` and all later gameplay implementation remain blocked.

The defect is in a Java preview harness outside Luna ownership. This review
therefore authorizes the separate bounded correction `L0-TM-04F5`, owned by
Terra Medium. It is preview infrastructure repair, not theorem implementation.

## 2. Conforming repository evidence

The real Luna delta is limited to the five authorized files:

```text
src/main/resources/assets/mathmod/lang/en_us.json
src/main/resources/assets/mathmod/lang/pt_br.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json
docs/handoffs/L0_LU_01_HANDOFF.md
```

Repository inspection establishes:

- both locale JSON files parse as UTF-8;
- EN-US and PT-BR contain 854 keys each, with identical key sets;
- the two frozen `factored_leap` presentation keys and values are present;
- both `beta_theorems` entries remain exactly eight pages;
- pages 0–3 preserve the existing catalog claims and add the bounded teaching
  claims without claiming runtime inscription;
- no semantic id, Java, test, schema, Data Component, networking, public API,
  item, menu or screen file was changed by Luna;
- the handoff records the changed-file inventory, terminology, page matrix,
  parity, narrator audit, capture inventory, commands and limitations;
- the handoff correctly declines to claim a runtime catalog card or successful
  `mathmod:factored_leap` inscription.

The focused Gradle selection was reproduced from a fully regenerated test
output and produced 14/14 passing test methods:

| Suite | Methods |
|---|---:|
| `PortugueseLocalizationQualityTest` | 3 |
| `PatchouliPreviewMatrixTest` | 1 |
| `PatchouliFieldManualTest` | 3 |
| `UiPreviewMatrixTest` | 5 |
| `ServerSideIsolationTest` | 2 |
| **Total** | **14** |

The standard `build` also completed successfully. These green results do not
replace the missing localized visual evidence.

## 3. Blocking finding

### L0-LU-01-R1 — PT-BR Patchouli captures render EN-US

Direct inspection of:

```text
run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p0-pt_br-preview.png
```

shows the English headings `Beta Theorems` and `Formula Shorthand` and English
body copy. The handoff explicitly reports the same limitation for all four
PT-BR Patchouli spread captures.

The cause is reproducible in
`src/main/java/com/mathmod/client/UiPreviewHarness.java`:

1. `onClientTick()` enters `patchouliMatrixPreview()`;
2. it calls `runPatchouliMatrixTick(minecraft)` and returns;
3. only after that branch, the ordinary preview path applies
   `MATHMOD_UI_PREVIEW_LOCALE`, selects the language and reloads resources.

Consequently, filenames containing `pt_br` do not prove localized Patchouli
rendering. This violates the frozen requirement for all four
`beta_theorems` spreads in PT-BR at 1024x800 and for absence of untranslated
ordinary player copy.

## 4. Bounded correction authorization

Create `L0-TM-04F5`, owned by Terra Medium.

Exact write ownership:

```text
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/test/java/com/mathmod/client/PatchouliPreviewMatrixTest.java
docs/handoffs/L0_TM_04F5_HANDOFF.md
```

Generated evidence may be replaced only under:

```text
run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-*
```

All production language/Patchouli content, functional Inspector code,
gameplay/compiler/persistence code, schemas, components, networking, public
APIs and semantic ids are read-only for this correction.

The correction must:

1. apply the configured preview locale before the Patchouli matrix opens or
   captures a book;
2. wait for the locale resource reload before beginning matrix navigation;
3. preserve the existing EN-US matrix behavior and capture naming;
4. add a focused regression assertion that the Patchouli matrix cannot bypass
   locale configuration;
5. regenerate all four EN-US and all four PT-BR `beta_theorems` spreads at
   1024x800;
6. visually verify that every PT-BR spread contains Portuguese ordinary copy
   and report any content-layout defect without editing localized content;
7. report the exact capture commands, eight filenames and focused counts in
   `docs/handoffs/L0_TM_04F5_HANDOFF.md`.

Required focused command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.assets.PortugueseLocalizationQualityTest `
  --tests com.mathmod.client.PatchouliPreviewMatrixTest `
  --tests com.mathmod.integration.patchouli.PatchouliFieldManualTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.ServerSideIsolationTest
```

Required standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

No GameTest is required or authorized. `L0-TM-04F5` may not add theorem
runtime behavior or touch `L0-TM-05` ownership.

### 4.1 Ownership clarification after localized capture

The localized F5 captures prove that the harness now selects the requested
locale before opening Patchouli. They also expose ordinary-copy overflow in
the EN-US and PT-BR p0 and p2 spreads. That overflow is a content defect, not
a harness defect.

Therefore:

- Terra Medium must not edit either `beta_theorems.json`;
- the overflow does not block technical review of the bounded F5 harness
  correction when F5 reports it explicitly;
- absence of clipping remains mandatory for final `L0-LU-01` acceptance;
- shortening or redistributing PT-BR prose belongs to Luna under the bounded
  `L0-LU-01F` task below.

## 5. Re-review transition

After `L0-TM-04F5` is accepted, create `L0-LU-01F`, owned by Luna. Luna may
update only:

```text
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json
docs/handoffs/L0_LU_01F_HANDOFF.md
run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-*
```

Locale keys, Java, tests, semantic ids and all gameplay files are read-only.
Luna may shorten or redistribute only pages 0–3 in both book locales, must
retain exactly eight pages and every frozen teaching/catalog claim, and must
not claim runtime inscription. The handoff must map each frozen claim to its
retained page and inventory all eight regenerated spreads.

The Luna correction then returns to Sol review. Acceptance requires direct
visual inspection of all four PT-BR spreads, not only filenames or harness
counts.

```text
L0-LU-01 NEEDS_FIX
    -> L0-TM-04F5 READY
    -> L0-TM-04F5 review
    -> L0-LU-01F READY after F5 acceptance
    -> L0-LU-01 re-review

L0-TM-05 BLOCKED
    -> accepted L0-LU-01
    -> exact internal integration-readiness amendment
```

No other downstream task becomes ready.
