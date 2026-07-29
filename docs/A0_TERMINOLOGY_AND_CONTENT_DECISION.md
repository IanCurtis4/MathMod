# A0 Terminology and Content Scope Decision

**Task:** `A0-SOL-LU-01`  
**Date:** 2026-07-26  
**Owner:** Sol  
**Decision:** `ACCEPT` with bounded package scope

## Inputs and authority

This decision consumes:

- `docs/A0_AUTHORING_TERMINOLOGY_AND_EVIDENCE_INVENTORY.md`;
- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`;
- `docs/A0_LEGACY_ADAPTER_GATE_ACCEPTANCE.md`;
- `docs/ADVANCED_EDITOR.md`;
- `docs/UI_PREVIEWS.md`;
- the current EN/PT-BR language files and Patchouli entries cited below.

Localized text, icons, formulas, categories, layout, and sort order remain
presentation. They do not identify a form or rune, authorize graph mutation,
become parser aliases, or alter replay. `ProgramGraph` remains authoritative;
Rune Form and category ids remain frozen.

The current language files contain the same 804 keys in EN and PT-BR. This is
key-set parity only, not proof of correct or complete localization.

## Frozen editorial meanings

| Term | Current approved meaning | PT-BR | Boundary |
|---|---|---|---|
| Guided | The current Rune Form authoring surface. Player-visible subareas remain **Theorems** and **Laboratory**. | `Guiado` in architecture/glossary; `Teoremas` and `Laboratório` remain the current player labels. | It is not a new mode id and does not rename persisted data. |
| Inspector | The read-only projection of the authoritative graph/proof. | `Inspetor de runas` in the current title; `Inspetor` in bounded prose. | It never means mutable Advanced editing. |
| Advanced | The future direct typed graph-editing surface with working-copy mutation. | `Avançado`. | Do not apply this product label to the current Inspector or Laboratory. |
| Source | Authored textual/functional source governed by L0/L1. The existing `SOURCES` palette category remains a distinct Rune Form category. | `Fonte`; category `FONTES`. | Compact formulas and `f(x)` are not persisted source. |
| Function | A mathematical/program function concept or type. | `Função`. | Current notation does not claim a supported textual source editor. |
| Discipline | A future D0 specialization/projection. Lowercase lore uses such as “typed discipline” are ordinary prose. | `Disciplina`. | No D0 selector, state, ordering authority, or product surface is implied. |
| Notation | Presentation of a proof or formula. A selectable notation profile belongs to future S0. | `Notação`. | Symbols, formulas, and glyphs are not identity or executable aliases. |

These meanings are editorial vocabulary, not semantic ids.

## Package decisions

### LU-1 — EN/PT-BR surface glossary

**Decision:** `APPROVE`.

The A0-LU-01 handoff must contain a complete bilingual glossary using the
meanings above and a key-by-key parity report. Existing Theorems/Laboratory
labels remain unchanged unless an exact correction is listed in that handoff.
No new `GUIDED` mode, id, category, or persistence field may be introduced.

### LU-2 — bilateral Guided/Inspector Patchouli update

**Decision:** `APPROVE WITH BOUNDS`.

The authorized entries may be corrected only to:

- distinguish current Theorems/Laboratory authoring from the Inspector;
- state that the Inspector is read-only;
- distinguish compact notation from persisted Source/Function;
- prevent “Advanced Movement” from implying that an Advanced editor exists;
- distinguish ordinary discipline prose from future D0;
- keep first-use and current-state claims accurate.

Every edit must be bilateral EN/PT-BR and preserve entry ids, links, recipes,
advancements, anchors, and page structure unless a text-only structure change
is required for equivalent wrapping.

### LU-3 — icon reuse manifest

**Decision:** `APPROVE FOR DOCUMENTATION ONLY`.

The handoff may map current form/rune ids to existing rune textures and record
technical fallback behavior. Texture renames, replacements, new glyph kinds,
new assets, and collapsing distinct basis glyphs are deferred.

### LU-4 — EN/PT-BR preview matrix

**Decision:** `APPROVE FOR REQUIREMENTS ONLY`.

Luna may update `docs/UI_PREVIEWS.md` with current-surface EN/PT-BR cases and
requirements. `UiPreviewMatrix.java`, preview runtime code, screenshots, and
new harness cases remain outside Luna ownership. Their implementation belongs
to a later exact integrator assignment.

The matrix must not request previews of mutable Advanced editing, textual
Source, Discipline selection, or notation-profile selection as if those
surfaces existed.

### LU-5 — current Inspector narrator copy

**Decision:** `APPROVE WITH CONTENT-ONLY BOUNDS`.

Luna may correct current Inspector text in both locale files and provide a
narrator-copy matrix. This includes spelling, accents, read-only wording, node
selection, dynamic-input wording, and consistent Formula/Expressão usage.

Only existing translation keys may be changed in this task. New keys and Java
consumption require a later exact integrator assignment so unused or
semantically premature narration is not created.

### LU-6 — technical fallback proof

**Decision:** `APPROVE AS AN EVIDENCE SPECIFICATION`.

The handoff must define expected EN/PT-BR and technical-fallback cases for
missing presentation. Luna may document the cases but may not implement Java
fallbacks or tests. A0-TM-03/A0-TM-04 own executable evidence after exact file
assignment.

Fallback must expose a bounded technical rune/form/category identity without
inventing localized identity, silently selecting another form, or changing the
graph.

## Exact A0-LU-01 write ownership

Language files:

```text
src/main/resources/assets/mathmod/lang/en_us.json
src/main/resources/assets/mathmod/lang/pt_br.json
```

Patchouli entries, always as EN/PT-BR pairs:

```text
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/basics/can_i_make_spell.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/basics/can_i_make_spell.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/basics/current_state.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/basics/current_state.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/custom_programmer.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/custom_programmer.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/inspector.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/inspector.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/typed_graphs.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/typed_graphs.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/alchemical_effects.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/alchemical_effects.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/lore/runes_and_types.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/lore/runes_and_types.json
```

Documentation:

```text
docs/UI_PREVIEWS.md
docs/handoffs/A0_LU_01_HANDOFF.md
```

No other file is authorized. In particular, all Java, tests, textures, binary
assets, `ProgramGraph`, `GuidedWorkspaceState`, Data Components, networking,
`ProgramSurfaceMode`, public APIs, stable ids, and expansion behavior remain
read-only or forbidden.

## Terms that remain unchanged until later gates

- `Rune Inspector` must not become `Advanced Editor` before the A1 mutable
  editor contract and implementation gate.
- `Theorems` and `Laboratory` remain the current player-visible Guided labels.
- `SOURCES`/`FONTES` remains the current category label and must not be used as
  the name of L0/L1 source persistence.
- `Advanced Movement` may be clarified as content, but must not be promoted to
  an Advanced surface or mode.
- D0 `Discipline` and S0 notation profiles must not gain UI selectors, product
  promises, ids, or state before their own gates.
- `f(x)` remains compact notation; it must not claim parsing, round-trip source,
  or a persisted Function surface.
- Technical ids remain technical fallback/inspection material and must not be
  localized into new identity aliases.

## Required parity and narrator evidence

### A0-LU-01 handoff

- both locale JSON files parse;
- EN and PT-BR key sets remain identical;
- every changed translation key lists old and new values in both locales;
- every changed Patchouli entry has the matching locale pair and parses;
- spelling/encoding is checked from UTF-8 source, not terminal rendering;
- no raw future-surface promise is introduced;
- icon manifest references only existing rune ids and files;
- narrator matrix identifies current key, focus/state, EN copy, PT-BR copy,
  fallback, and future owner.

### A0-4 integration

- registry presentation and missing-presentation fallback do not change form
  identity or ordering;
- pointer and keyboard activation resolve the same form id;
- narration exposes current label, category, parameter bounds, and technical id
  where the contract requires it;
- Inspector narration identifies read-only state, focused node, named input or
  socket context, and viewport/focus state without relying on color;
- focused tests cover EN, PT-BR, and missing translation/presentation.

### A0-5 hardening

- current Guided and Inspector journeys are evidenced in EN and PT-BR at the
  required minimum/ATM10 viewport targets;
- Patchouli pairs render without clipping, mojibake, raw unresolved keys, or
  contradictory availability claims;
- missing descriptors and missing translations degrade to bounded technical
  fallback while the valid graph remains inspectable;
- dedicated-server execution remains independent of client presentation;
- reload/reconnect and last-known-good evidence does not rewrite persisted
  identity or graph data.

## Operational result

`A0-SOL-LU-01` is `DONE`.

`A0-LU-01` is `READY` because both dependencies are now satisfied:

- A0-3 gate accepted;
- terminology/content scope frozen by this decision.

The A1 read-only handoff exists and releases its granted files, but it remains
`IN_REVIEW` until Sol performs its technical acceptance. Therefore
`A0-TM-03` remains `BLOCKED` on accepted A1 evidence and a later exact
integrator ownership assignment.
