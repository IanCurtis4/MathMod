# Handoff: A0-LU-01

## Completed

- Applied the bounded EN/PT-BR terminology corrections approved by
  `docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md`.
- Kept current player-facing Guided labels as `Theorems` and `Laboratory`.
- Kept `Rune Inspector` explicitly read-only and distinct from future
  `Advanced` editing.
- Clarified that compact formulas are not persisted Source or Function source.
- Removed the Patchouli ambiguity that made `Advanced Movement` look like an
  Advanced editor surface by renaming the content heading to
  `Higher-order Movement` / `Movimento de ordem superior`.
- Added current-surface preview/content requirements to `docs/UI_PREVIEWS.md`.
- Produced the icon-reuse and fallback manifest below; no texture or glyph was
  renamed, replaced, collapsed, or added.

## Approved bilingual glossary

| Concept | EN | PT-BR | Current surface/boundary |
|---|---|---|---|
| Guided | `Guided` (architecture term) | `Guiado` (architecture term) | Player labels remain `Theorems` / `Laboratory`; no new mode or id. |
| Inspector | `Rune Inspector` / `Inspector` | `Inspetor de runas` / `Inspetor` | Read-only graph projection. |
| Advanced | `Advanced` | `Avançado` | Future direct typed graph editor only. |
| Source | `Source` | `Fonte` | Future textual/functional source; `SOURCES`/`FONTES` remains a category. |
| Function | `Function` | `Função` | Concept/type; `f(x)` remains compact notation. |
| Discipline | `Discipline` | `Disciplina` | Future D0 specialization; ordinary lore prose is not a selector. |
| Notation | `Notation` | `Notação` | Presentation; selectable profiles are future S0. |

Authority: `docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md`, section
“Frozen editorial meanings”.

## EN/PT-BR parity matrix

| Check | Result | Evidence |
|---|---|---|
| Locale key-set parity | PASS — 804 keys in each locale | `src/main/resources/assets/mathmod/lang/en_us.json`, `pt_br.json` |
| Changed language keys are bilateral | PASS — four existing Inspector keys corrected in PT-BR | `screen.mathmod.rune_inspector.open_hint`, `.read_only`, `.empty`, `.formula`, `.dependencies` |
| No new translation key | PASS | Same two locale files; no Java consumption changed |
| Changed Patchouli entries are bilateral | PASS | `programming/inspector.json` and `programming/beta_theorems.json` in both locales |
| Future surface promise introduced | PASS — none | Existing Theorems/Laboratory/Inspector boundaries retained |
| UTF-8 source validation | PASS | JSON parse check performed after edits; terminal rendering is not used as evidence |

## Exact files and keys changed

### Language

- `src/main/resources/assets/mathmod/lang/pt_br.json`
  - `screen.mathmod.rune_inspector.open_hint`
  - `screen.mathmod.rune_inspector.read_only`
  - `screen.mathmod.rune_inspector.empty`
  - `screen.mathmod.rune_inspector.formula`
  - `screen.mathmod.rune_inspector.dependencies`

No EN language value or key was changed. No key was added or removed.

### Patchouli

- `.../field_manual/en_us/entries/programming/inspector.json`, first page text:
  read-only boundary and future Advanced distinction.
- `.../field_manual/pt_br/entries/programming/inspector.json`, first page text:
  matching read-only boundary, `fórmula`, and future Avançada distinction.
- `.../field_manual/en_us/entries/programming/beta_theorems.json`, Formula
  Shorthand page text and `Higher-order Movement` title.
- `.../field_manual/pt_br/entries/programming/beta_theorems.json`, Notação das
  fórmulas page text and `Movimento de ordem superior` title.

Entry ids, links, categories, recipes, anchors, page order, and page structure
remain unchanged.

### Documentation

- `docs/UI_PREVIEWS.md`, new section `A0-LU-01 Current-Surface Content
  Requirements`.
- `docs/handoffs/A0_LU_01_HANDOFF.md`.

## Icon-reuse manifest

The manifest is keyed by the existing `CustomSpellAction.iconRuneId()` / A0
`RuneIcon(runeId)` value. The complete 67-form key set is the frozen Appendix A
of `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`; the icon value is the
second argument of each existing enum entry in
`src/main/java/com/mathmod/program/CustomSpellAction.java`.

| Existing rune-id key | Existing asset rule | Status |
|---|---|---|
| `mathmod:self_player` through all existing `mathmod:*` values in `CustomSpellAction` | `src/main/resources/assets/mathmod/textures/gui/runes/<path>.png` where the path is the existing rune presentation asset | Reuse only |
| `mathmod:right_basis_vector`, `mathmod:forward_basis_vector`, `mathmod:oblique_basis_vector` | `right_basis_vector.png`, `forward_basis_vector.png`, `oblique_basis_vector.png` | Distinct glyphs retained; shared family is visual only |
| Missing/invalid icon reference | Contract technical fallback, not another form | Documentation/evidence only; Java fallback unchanged |

No icon is used as persistent identity, parser alias, adapter id, or semantic
lookup. Source: A0 contract §§5.1, 6.4, 11.1–11.2, 13.3.

## Narrator-copy matrix

| Current key/state | EN copy source | PT-BR copy source | Required reading |
|---|---|---|---|
| `screen.mathmod.rune_inspector.open_hint` | Existing EN key | Corrected PT-BR key | Open read-only graph; never changes proof |
| `screen.mathmod.rune_inspector.read_only` | Existing EN key | Corrected PT-BR key | Authoritative proof projection is read-only |
| `screen.mathmod.rune_inspector.empty` | Existing EN key | Corrected PT-BR key | No node selected |
| `screen.mathmod.rune_inspector.rune`, `.formula`, `.normalized`, `.budget`, `.dependencies`, `.materials`, `.attributes` | Existing EN keys | Existing PT-BR keys; formula terminology aligned | Read selected node details completely |
| `screen.mathmod.rune_programmer.palette_narration`, `.palette_position`, `.palette_usage` | Existing EN keys | Existing PT-BR keys | Current Guided palette label, position, and activation keys |

Future Advanced canvas, Source/Function editor, Discipline selector, and
Notation-profile selector are intentionally not assigned narrator keys here.

## Preview requirements delivered

`docs/UI_PREVIEWS.md` now requires current-surface evidence for:

- Theorems/Laboratory as the current Guided surface;
- read-only Inspector wording and focus/narration content;
- compact formula/`f(x)` wording without Source/Function claims;
- EN/PT-BR parity, no raw keys, no mojibake, and no clipping;
- bounded technical fallback without form substitution or graph change.

No `UiPreviewMatrix.java`, preview runtime, screenshot, or harness case was
added or changed.

## Files changed

- `src/main/resources/assets/mathmod/lang/pt_br.json`
- `src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/inspector.json`
- `src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/inspector.json`
- `src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json`
- `src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json`
- `docs/UI_PREVIEWS.md`
- `docs/handoffs/A0_LU_01_HANDOFF.md`

## Contracts referenced

- `docs/A0_POST_ADAPTER_DELIVERY_PLAN.md`, task `A0-LU-01`
- `docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md`
- `docs/A0_AUTHORING_TERMINOLOGY_AND_EVIDENCE_INVENTORY.md`
- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`
- `docs/A0_LEGACY_ADAPTER_GATE_ACCEPTANCE.md`
- `docs/UI_PREVIEWS.md`

## Tests and evidence

- JSON parsing and EN/PT-BR key-set comparison: PASS, 804 keys per locale.
- Patchouli JSON parsing and EN/PT-BR entry-pair comparison: PASS for changed
  `inspector` and `beta_theorems` entries.
- Icon files: read-only path audit; no asset changes.
- Java/build/UI tests: not run; this handoff changed no Java and does not claim
  executable preview coverage.

## Migration impact

None. No persistent id, schema, graph, invocation, adapter id, or Data
Component changed.

## Known limitations

- The complete runtime fallback behavior remains owned by A0-TM-03/A0-TM-04.
- Future Advanced, L0 Source/Function, D0 Discipline, and S0 Notation surfaces
  remain outside this task.
- Screenshot and runtime narrator evidence remains an integrator responsibility.

## Unresolved questions

- None for the bounded LU-1 through LU-6 content scope. Runtime fallback and
  executable preview ownership remain downstream as specified above.

## Next owner

- Terra Medium integrator / A0-TM-03 after its independent screen-ownership
  gate.

## Exact next task

- Preserve this content handoff while completing the accepted A1 read-only
  technical review and later A0-TM-03 integration.

## Files the next owner may edit

- Only files explicitly assigned by the delivery board for the next task.

## Files the next owner must not edit

- Stable ids, persistence codecs, Data Components, networking, `ProgramGraph`,
  `GuidedWorkspaceState`, `ProgramSurfaceMode`, or assets outside an explicit
  later assignment.
