# L0-LU-02 — Post-implementation Factored Leap Content Alignment

Status: ready for Sol review.

## Scope and ownership

This task applied only the post-implementation content correction authorized by
`docs/L0_TM_05_FINAL_GATE_ACCEPTANCE.md`. The obsolete pre-implementation
statement was replaced with the accurate bilingual availability statement.

Exact changed-file inventory:

- `src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json`
- `src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json`
- `docs/handoffs/L0_LU_02_HANDOFF.md`

The only content key changed was `pages[0].text` in each Patchouli entry. No
locale key, Java, test source, schema, networking, Data Component, semantic id,
public API, gameplay or compiler file was changed. No persistent id was renamed.

## Content correction

| Locale | Previous obsolete claim | Current claim |
|---|---|---|
| EN-US | `Factored Leap` was “not a runtime card or inscription route.” | `Factored Leap is available through the Rune Programmer.` |
| PT-BR | `Salto fatorado` was “não um card em tempo de execução nem uma rota de inscrição.” | `Salto fatorado está disponível pelo Programador rúnico.` |

The current statement is limited to the accepted runtime availability fact. It
does not invent additional semantics, implementation details or client
authority. The existing Factored Leap teaching claims on pages 1 and 3 remain
unchanged, as do the existing theorem catalog claims.

## Structural preservation

- EN-US `beta_theorems.json`: exactly 8 pages.
- PT-BR `beta_theorems.json`: exactly 8 pages.
- The 29-theorem catalog and its existing categories remain present.
- Persistent theorem id remains `mathmod:factored_leap`.
- Existing Factored Leap terminology remains `Factored Leap` / `Salto fatorado`.
- No runtime card, inscription or semantic behavior was described beyond the
  authorized availability statement.

## Generated evidence

The two affected p0 spreads were regenerated at 1024×800 after the final
content edit and visually inspected:

- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p0-en_us-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p0-pt_br-preview.png`

The EN-US spread visibly contains “Factored Leap is available through the Rune
Programmer.” The PT-BR spread visibly contains “Salto fatorado está disponível
pelo Programador rúnico.” Both spreads retain the adjacent Formula Shorthand
page, show the expected locale, and have no clipping, overlap, mojibake,
untranslated ordinary copy or raw semantic id in the affected content.

Capture environment used for both locale runs:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
$env:MATHMOD_UI_PREVIEW='patchouli-matrix'
$env:MATHMOD_UI_PREVIEW_WORLD='New World'
$env:MATHMOD_UI_PREVIEW_LOCALE='<en_us or pt_br>'
$env:MATHMOD_UI_PREVIEW_WIDTH='1024'
$env:MATHMOD_UI_PREVIEW_HEIGHT='800'
.\gradlew.bat runClient --no-daemon
```

Each run completed with `BUILD SUCCESSFUL` and the matrix harness reported its
81/81 capture set; only the authorized p0 files are listed as L0-LU-02 output.

## Verification

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

Result: `BUILD SUCCESSFUL`. Exact selected methods: 15 total, with 0 failures,
0 errors and 0 skipped:

| Suite | Methods |
|---|---:|
| `PortugueseLocalizationQualityTest` | 3 |
| `PatchouliPreviewMatrixTest` | 2 |
| `PatchouliFieldManualTest` | 3 |
| `UiPreviewMatrixTest` | 5 |
| `ServerSideIsolationTest` | 2 |
| **Total** | **15** |

Required standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result: `BUILD SUCCESSFUL`.

## Limitations and escalation

This correction is content-only and does not alter runtime implementation or
semantic ownership. `A0-6` remains `BACKLOG` and requires its separate
Sol-owned contract. No additional follow-up was inferred or introduced.
