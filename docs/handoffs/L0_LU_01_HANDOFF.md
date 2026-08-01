# L0-LU-01 — Localization, Patchouli and Evidence Handoff

Status: ready for review. This handoff records the content-only execution of
L0-LU-01 after the semantic contract was frozen. It does not claim a runtime
catalog card or a successful inscription for `mathmod:factored_leap`; those
remain gated by L0-TM-05.

## Exact changed-file inventory

Only the ownership listed by `docs/L0_FIRST_GAMEPLAY_THEOREM_SPECIFICATION.md`
was used:

| File | Change |
|---|---|
| `src/main/resources/assets/mathmod/lang/en_us.json` | Added `screen.mathmod.rune_programmer.preset_factored_leap` and `screen.mathmod.rune_programmer.factored_leap_hint`. |
| `src/main/resources/assets/mathmod/lang/pt_br.json` | Added the same two keys with PT-BR values. |
| `src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json` | Revised pages 0–3 to teach the bounded example and the three read-only projections; pages 4–7 retain the existing theorem claims. |
| `src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json` | PT-BR counterpart of the same page revisions. |
| `docs/handoffs/L0_LU_01_HANDOFF.md` | This evidence and limitation record. |

No Java, tests, schema, data components, networking, item/menu/screen APIs,
semantic-id files, assets or persistent ids were changed for L0-LU-01.

## Frozen terminology

| Current term | EN proposal | PT-BR proposal | Evidence key / source |
|---|---|---|---|
| scoped source | scoped source | fonte delimitada | `screen.mathmod.rune_inspector.functional_snapshot` |
| authored source | authored source | fonte autoral | `screen.mathmod.rune_inspector.functional.panel.authored` |
| checked form | checked form | forma verificada | `screen.mathmod.rune_inspector.functional.panel.checked` |
| compiled graph | compiled graph | grafo compilado | `screen.mathmod.rune_inspector.functional.panel.graph` |
| pure function | pure function | função pura | `screen.mathmod.rune_programmer.factored_leap_hint`; Patchouli page 1/page 3 |
| parameter | parameter | parâmetro | `screen.mathmod.rune_inspector.functional.row.parameter_reference` |
| lambda | lambda | lambda | `screen.mathmod.rune_inspector.functional.row.lambda` |
| application | application | aplicação | `screen.mathmod.rune_inspector.functional.row.application` |
| let binding | let binding | vínculo `let` | `screen.mathmod.rune_inspector.functional.row.let` |
| beta reduction | beta reduction | redução beta | Patchouli page 3 |
| terminal effect | terminal effect | efeito terminal | `screen.mathmod.rune_programmer.factored_leap_hint`; Patchouli page 3 |
| Factored Leap | Factored Leap | Salto fatorado | `screen.mathmod.rune_programmer.preset_factored_leap` |

The persistent theorem id remains exactly `mathmod:factored_leap`. The existing
icon remains `mathmod:scale_vector`; no new glyph or icon was introduced.

## Patchouli claim matrix

Both locale files have exactly 8 pages. Existing catalog claims remain across
all pages; the first four pages add the following bounded teaching claims:

| Page | EN/PT-BR content responsibility | Contract boundary |
|---:|---|---|
| 0 | Existing 29-theorem catalog orientation; introduces Factored Leap as a manual teaching specification only. | Not a runtime card and not an inscription route. |
| 1 | Formula shorthand is not executable/persisted source; distinguishes authored source, checked form and compiled graph; `halve` is pure and observations are outside it. | No runtime function/closure is claimed. |
| 2 | Keyboard, focus and narrator guidance; Inspector snapshot names the three projections and diagnostics. | Read-only presentation only. |
| 3 | Retains Hop, Dash, Vector Leap, Recoil and Blink; adds Factored Leap: define `halve` once, apply twice, beta-reduce/lower before execution, and finish with `push_self`. | Errors, stale authority and failed verification do not partially inscribe. |
| 4 | Existing local-movement theorem claims retained. | No new semantics. |
| 5 | Existing higher-order movement theorem claims retained: Harmonic Step, Orthogonal Step, Quarter Turn and Quadrature Leap. | No recursion, combinator or runtime-closure teaching claim. |
| 6 | Existing Sensing claims retained. | No new sensing behavior. |
| 7 | Existing Control claims retained. | No new control behavior. |

The copy explicitly avoids recursion, collection combinators, effect values in
functions, automatic CSE, client authority, graph-to-lambda reconstruction and
source migration/repair on read.

## Translation parity and accessibility audit

`en_us.json` and `pt_br.json` each contain 854 keys and their key sets are
identical. The two required keys are present with these exact values:

| Key | EN | PT-BR |
|---|---|---|
| `screen.mathmod.rune_programmer.preset_factored_leap` | `Factored Leap` | `Salto fatorado` |
| `screen.mathmod.rune_programmer.factored_leap_hint` | `A bounded functional example: define halve once, apply it twice, and finish with one terminal push.` | `Um exemplo funcional delimitado: defina halve uma vez, aplique-a duas vezes e termine com um único empurrão terminal.` |

Functional Inspector copy is already present in both locales for
`functional_snapshot`, `authored_source`, `checked_binding`, `functional_graph`,
`functional_diagnostic`, `functional_row`, `functional_narration`,
`functional_selected_row`, the `functional.panel.*` projection labels, all
`functional.source.*` states, all `functional.attempt.*` states,
`functional.graph.*`, `functional.relation.*` and `functional.diagnostic.*`
states. Narrator-facing row labels cover `literal`, `result`,
`parameter_reference`, `rune_call`, `rune_argument`, `lambda`, `application` and
`let`. No diagnostic mapping or runtime narrator implementation was added.

The Patchouli page 2 copy documents Tab, arrows, Enter/Space, Home/End,
focused-row legibility and the Inspector projections. The compact functional
PT-BR capture visibly shows `Fechar`, `fonte autoral`, `forma verificada` and
`grafo compilado`.

## Preview and glyph evidence

The existing icon/glyph system was reused: the theorem entry continues to use
`mathmod:programmed_talisman`, and the frozen Factored Leap icon reference is
`mathmod:scale_vector`. No new asset, glyph, or icon id was created.

Authorized generated evidence:

- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p0-en_us-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p2-en_us-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p4-en_us-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p6-en_us-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p0-pt_br-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p2-pt_br-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p4-pt_br-preview.png`
- `run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-p6-pt_br-preview.png`
- `run/client/screenshots/mathmod-rune-inspector-functional-en_us-1024x800-preview.png`
- `run/client/screenshots/mathmod-rune-inspector-functional-pt_br-1024x800-preview.png`
- `run/client/screenshots/mathmod-rune-inspector-functional-pt_br-640x480-preview.png`

The EN Patchouli matrix completed 81/81 captures and the PT-BR matrix completed
81/81 harness captures. Representative EN Patchouli, PT-BR functional and
compact PT-BR images were visually inspected. The PT-BR Patchouli image files
are retained as harness evidence, but are not claimed as valid localized visual
evidence: `UiPreviewHarness.java` checks the `patchouli-matrix` branch before
applying `MATHMOD_UI_PREVIEW_LOCALE` (exact constants/branch:
`UiPreviewHarness.PREVIEW_LOCALE`, `UiPreviewHarness.patchouliMatrixPreview()`
and `UiPreviewHarness.onClientTick()`), so that branch falls back to EN. Fixing
that would be a Java/harness change outside L0-LU-01 ownership.

## Verification

Required focused command:

```text
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache --tests com.mathmod.assets.PortugueseLocalizationQualityTest --tests com.mathmod.client.PatchouliPreviewMatrixTest --tests com.mathmod.integration.patchouli.PatchouliFieldManualTest --tests com.mathmod.client.UiPreviewMatrixTest --tests com.mathmod.ServerSideIsolationTest
```

Result: `BUILD SUCCESSFUL`; 14 focused test methods selected, 0 failures and
0 errors. Standard build also passed:

```text
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

Result: `BUILD SUCCESSFUL`.

## Limitations and escalations

- L0-TM-05 remains the gate for any runtime catalog card or successful
  inscription capture for `mathmod:factored_leap`.
- The Patchouli locale-preview harness must apply the selected locale before
  opening the book; this is an escalation, not a permitted L0-LU-01 fix.
- No new GameTest, semantic behavior, public API, schema, persistence,
  networking, menu/screen, or diagnostic mapping was introduced.
- Future implementation work may depend only on the frozen ids and keys listed
  above; it must not invent behavior or rename persistent ids.

This handoff separates current terms from proposals, records exact files and
keys, and intentionally leaves runtime semantic ownership with the downstream
L0-TM work.
