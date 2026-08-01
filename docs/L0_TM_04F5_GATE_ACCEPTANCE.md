# L0-TM-04F5 — Technical Gate Acceptance

**Reviewed task:** `L0-TM-04F5`  
**Date:** 2026-07-29  
**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Reviewed handoff:** `docs/handoffs/L0_TM_04F5_HANDOFF.md`

## 1. Decision

The bounded Patchouli locale-harness correction is accepted. The matrix now
configures the requested locale, starts the resource reload and does not open
or capture Patchouli until the reload future has completed.

`L0-TM-04F5` is `DONE` with `ACCEPT`. This acceptance is technical only; it
does not accept `L0-LU-01` and does not authorize `L0-TM-05`.

`L0-LU-01F`, owned by Luna, is now `READY` for the bounded EN-US/PT-BR copy
fit correction specified below.

## 2. Accepted delta

The F5 production/test delta is limited to:

```text
src/main/java/com/mathmod/client/UiPreviewHarness.java
src/test/java/com/mathmod/client/PatchouliPreviewMatrixTest.java
docs/handoffs/L0_TM_04F5_HANDOFF.md
```

The implementation:

- routes the Patchouli matrix through `previewLocaleReady(minecraft)` before
  `runPatchouliMatrixTick(minecraft)`;
- selects `MATHMOD_UI_PREVIEW_LOCALE` before reloading resources;
- records reload completion through a volatile flag;
- keeps navigation and capture blocked until the reload completes and the
  selected language matches;
- reuses the same barrier for ordinary localized previews;
- adds a focused regression assertion for ordering and reload completion.

No gameplay theorem, compiler, persistence, schema, Data Component, networking,
public API or GameTest changed.

## 3. Reproduced verification

The required focused command completed successfully:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.assets.PortugueseLocalizationQualityTest `
  --tests com.mathmod.client.PatchouliPreviewMatrixTest `
  --tests com.mathmod.integration.patchouli.PatchouliFieldManualTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.ServerSideIsolationTest
```

The repository result is 15/15, not the 14 stated by the handoff:

| Suite | Passing methods |
|---|---:|
| `PortugueseLocalizationQualityTest` | 3 |
| `PatchouliPreviewMatrixTest` | 2 |
| `PatchouliFieldManualTest` | 3 |
| `UiPreviewMatrixTest` | 5 |
| `ServerSideIsolationTest` | 2 |
| **Total** | **15** |

Failures, errors and skipped methods are all zero.

The required standard command also passed:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result: `BUILD SUCCESSFUL`.

## 4. Visual evidence and corrected escalation

Direct inspection confirms:

- all four `pt_br` spreads render Portuguese rather than EN-US;
- all four `en_us` spreads render English;
- p4 and p6 are readable in both locales;
- no locale fallback, mojibake or raw semantic id was observed.

The handoff reports overflow only for PT-BR p0 and p2. Sol inspection shows
that EN-US p0 and p2 also exceed the lower page boundary. This is an incomplete
handoff limitation report, but not a remaining harness defect: both locales
are selected correctly and the overflow follows the localized content length.

The repository gate, rather than the handoff count or limitation wording, is
the authority for the next correction.

## 5. Luna authorization

`L0-LU-01F` is `READY`, owned by Luna.

Exact write ownership:

```text
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json
docs/handoffs/L0_LU_01F_HANDOFF.md
```

Generated evidence may be replaced only under:

```text
run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-*
```

Luna may shorten or redistribute only pages 0–3. Luna must:

1. retain exactly eight pages in each locale;
2. preserve every existing theorem catalog claim;
3. preserve all frozen `factored_leap` teaching claims and terminology;
4. keep EN-US/PT-BR semantic parity without requiring literal sentence parity;
5. avoid runtime-card, inscription, client-authority or new-semantics claims;
6. regenerate all four spreads in both locales at 1024x800;
7. prove no clipping, overlap, mojibake, untranslated ordinary copy or raw
   semantic ids in any of the eight resulting images;
8. map each required claim to its retained page in the handoff.

All Java, tests, locale-key files, gameplay/compiler/persistence files,
networking, schemas, components, APIs and semantic ids are read-only.

## 6. Transition

```text
L0-TM-04F5 DONE (ACCEPT)
    -> L0-LU-01F READY
    -> L0-LU-01 remains NEEDS_FIX pending Luna evidence

L0-TM-05 BLOCKED
    -> accepted L0-LU-01
    -> exact internal integration-readiness amendment
```

No other downstream task becomes ready.
