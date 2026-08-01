# L0-LU-01F — Bilingual Patchouli Copy-Fit Handoff

Status: ready for Sol re-review. This bounded editorial correction is
authorized by `docs/L0_TM_04F5_GATE_ACCEPTANCE.md` after L0-LU-01-R1.

## Exact ownership and changed files

| File | Change |
|---|---|
| `src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json` | Shortened/redistributed only pages 0–3; pages 4–7 unchanged. |
| `src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json` | Shortened/redistributed only pages 0–3; pages 4–7 unchanged. |
| `docs/handoffs/L0_LU_01F_HANDOFF.md` | This handoff. |

Locale keys, Java, tests, assets, gameplay/compiler/persistence code, schemas,
components, networking, APIs, semantic ids and all other files were read-only.
No runtime card, inscription path, or new semantic behavior is claimed.

## Copy-fit correction

The correction shortened ordinary prose and page headings without removing the
frozen claims. Page 0 keeps the 29-theorem catalog, five catalog areas, hover
guidance, and the teaching-only/non-runtime boundary. Page 1 keeps formula
shorthand, distinct authored source/checked form/compiled graph, pure `halve`,
two applications and lowering. Page 2 keeps keyboard, narrator, Inspector
projections, diagnostics and compact-view readability. Page 3 keeps Hop, Dash,
Vector Leap, Recoil, Blink and all Factored Leap execution claims.

## Frozen claim map

| Required claim | Retained location |
|---|---|
| `mathmod:factored_leap` and `Factored Leap` / `Salto fatorado` | EN/PT-BR pages 0 and 3; no id file changed. |
| 29 editable theorem catalog and five catalog areas | EN/PT-BR page 0. |
| teaching example is not a runtime card or inscription route | EN/PT-BR page 0. |
| formula is not executable/persisted Function source | EN/PT-BR page 1. |
| authored source, checked form and compiled graph are distinct | EN/PT-BR pages 1 and 2. |
| `halve` is pure, defined once and applied to look and authored up | EN/PT-BR pages 1 and 3. |
| observations remain outside the lambda | EN/PT-BR page 3. |
| beta reduction/lowering removes functional structure before execution | EN page 3; PT-BR page 3 says “redução beta e compilação”. |
| graph executes and `push_self` is terminal | EN/PT-BR page 3. |
| errors, stale authority or failed verification do not partially inscribe | EN/PT-BR page 3. |
| keyboard, narrator and focused-row readability | EN/PT-BR page 2. |
| existing theorem claims | EN/PT-BR pages 4–7 unchanged. |

Forbidden claims remain absent: recursion, collection combinators, runtime
closures, effect values inside functions, automatic CSE, client authority,
graph-to-lambda reconstruction, source migration/repair on read and new public
API.

## Structural and localization checks

Both locale JSON files parse successfully and contain exactly 8 pages. Locale
key files were not changed by L0-LU-01F; their previous 854-key parity and the
exact frozen presentation keys remain intact. The PT-BR captures show
Portuguese ordinary copy (`Teoremas da beta`, `Notação das fórmulas`, `Teclas e
narrador`, `Salto fatorado`, `Leitura`, `Controle`) rather than EN-US fallback.
No raw `mathmod:` semantic id, mojibake, or untranslated ordinary copy was
observed in the eight inspected spreads.

## Regenerated evidence inventory

Both complete Patchouli matrices were run at 1024×800. The generic harness
outputs were copied immediately after each locale run into these eight
authorized locale-qualified evidence paths:

- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p0-en_us-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p2-en_us-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p4-en_us-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p6-en_us-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p0-pt_br-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p2-pt_br-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p4-pt_br-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p6-pt_br-preview.png`

All eight files were visually inspected after final regeneration. The p0 and
p2 copy fits without lower-boundary clipping or heading collision; p4 and p6
retain readable existing claims. Both locales show the expected language.

Capture environment for each run:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
$env:MATHMOD_UI_PREVIEW='patchouli-matrix'
$env:MATHMOD_UI_PREVIEW_WORLD='New World'
$env:MATHMOD_UI_PREVIEW_LOCALE='<en_us or pt_br>'
$env:MATHMOD_UI_PREVIEW_WIDTH='1024'
$env:MATHMOD_UI_PREVIEW_HEIGHT='800'
.\gradlew.bat runClient --no-daemon
```

Each complete matrix reported 81/81 captures and `BUILD SUCCESSFUL`.

## Verification

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

Result: `BUILD SUCCESSFUL`; 15 focused test methods, 0 failures, 0 errors and
0 skipped. The suite composition is 3 + 2 + 3 + 5 + 2 across the five classes.

The required standard build also completed successfully:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result: `BUILD SUCCESSFUL`.

## Boundary and downstream state

This handoff supplies the copy-fit correction and eight localized visual
captures for Sol review; it does not accept L0-LU-01 itself. L0-TM-05 remains
blocked until the L0-LU-01 re-review accepts this content and evidence. No
persistent id was renamed, no semantic behavior was invented, and the runtime
`mathmod:factored_leap` card/inscription gate remains L0-TM-05.
