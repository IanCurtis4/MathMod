# Handoff: A0-LU-01F — Content Evidence Completion

**Parent task:** `A0-LU-01`  
**Status:** `DONE` for LU-R1 through LU-R3  
**Owner:** Luna  
**Scope:** documentation-only correction; no production content was changed.

## Completed

- Replaced the non-enumerating icon statement with an explicit 67-form
  manifest.
- Recorded form id, icon rune id, expected texture path, existence result,
  fallback classification, and shared-icon note for every form.
- Replaced the abbreviated narrator matrix with exact key, state, EN copy,
  PT-BR copy, substitution arguments, fallback, owner, and runtime evidence
  responsibility.
- Added old/new EN/PT-BR values for each of the five changed Inspector keys.
- Re-ran UTF-8 JSON parsing, locale key comparison, 67-form enumeration, and
  texture existence checks.

## LU-R1 — Explicit 67-form icon manifest

Source of form ids and icon values:
`src/main/java/com/mathmod/program/CustomSpellAction.java`, enum entries and
`iconRuneId()`; frozen form ids:
`docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`, Appendix A. The expected
texture rule is `textures/gui/runes/<icon-rune-id-without-namespace>.png`.

All 67 rows below were enumerated. `True` means the expected file exists in the
repository. `N/A` means no fallback is required for the current built-in row.
The fallback classification for a future missing descriptor remains the
contractual bounded technical fallback; it is not substituted into any row.

| # | Form id | Icon rune id | Expected texture | Exists | Fallback classification | Shared icon note |
|---:|---|---|---|---|---|---|
| 1 | `mathmod:self` | `mathmod:self_player` | `src/main/resources/assets/mathmod/textures/gui/runes/self_player.png` | True | N/A | unique |
| 2 | `mathmod:number_one` | `mathmod:constant_number` | `src/main/resources/assets/mathmod/textures/gui/runes/constant_number.png` | True | N/A | unique |
| 3 | `mathmod:add_one` | `mathmod:number_add` | `src/main/resources/assets/mathmod/textures/gui/runes/number_add.png` | True | N/A | unique |
| 4 | `mathmod:subtract_one` | `mathmod:number_subtract` | `src/main/resources/assets/mathmod/textures/gui/runes/number_subtract.png` | True | N/A | unique |
| 5 | `mathmod:double_number` | `mathmod:number_multiply` | `src/main/resources/assets/mathmod/textures/gui/runes/number_multiply.png` | True | N/A | unique |
| 6 | `mathmod:halve_number` | `mathmod:number_divide` | `src/main/resources/assets/mathmod/textures/gui/runes/number_divide.png` | True | N/A | unique |
| 7 | `mathmod:clamp_number` | `mathmod:number_clamp` | `src/main/resources/assets/mathmod/textures/gui/runes/number_clamp.png` | True | N/A | unique |
| 8 | `mathmod:up_vector` | `mathmod:vector_from_numbers` | `src/main/resources/assets/mathmod/textures/gui/runes/vector_from_numbers.png` | True | N/A | unique |
| 9 | `mathmod:look_vector` | `mathmod:look_vector` | `src/main/resources/assets/mathmod/textures/gui/runes/look_vector.png` | True | N/A | unique |
| 10 | `mathmod:scale_vector` | `mathmod:scale_vector` | `src/main/resources/assets/mathmod/textures/gui/runes/scale_vector.png` | True | N/A | unique |
| 11 | `mathmod:vector_add_up` | `mathmod:vector_add` | `src/main/resources/assets/mathmod/textures/gui/runes/vector_add.png` | True | N/A | unique |
| 12 | `mathmod:vector_subtract_up` | `mathmod:vector_subtract` | `src/main/resources/assets/mathmod/textures/gui/runes/vector_subtract.png` | True | N/A | unique |
| 13 | `mathmod:normalize_vector` | `mathmod:vector_normalize` | `src/main/resources/assets/mathmod/textures/gui/runes/vector_normalize.png` | True | N/A | unique |
| 14 | `mathmod:vector_length` | `mathmod:vector_length` | `src/main/resources/assets/mathmod/textures/gui/runes/vector_length.png` | True | N/A | unique |
| 15 | `mathmod:dot_with_look` | `mathmod:vector_dot` | `src/main/resources/assets/mathmod/textures/gui/runes/vector_dot.png` | True | N/A | unique |
| 16 | `mathmod:distance_to_self` | `mathmod:vector_distance` | `src/main/resources/assets/mathmod/textures/gui/runes/vector_distance.png` | True | N/A | unique |
| 17 | `mathmod:sphere_region` | `mathmod:sphere_region` | `src/main/resources/assets/mathmod/textures/gui/runes/sphere_region.png` | True | N/A | unique |
| 18 | `mathmod:box_region` | `mathmod:box_region` | `src/main/resources/assets/mathmod/textures/gui/runes/box_region.png` | True | N/A | unique |
| 19 | `mathmod:region_contains_self` | `mathmod:region_contains` | `src/main/resources/assets/mathmod/textures/gui/runes/region_contains.png` | True | N/A | unique |
| 20 | `mathmod:sample_region` | `mathmod:sample_region` | `src/main/resources/assets/mathmod/textures/gui/runes/sample_region.png` | True | N/A | unique |
| 21 | `mathmod:raycast` | `mathmod:raycast_block` | `src/main/resources/assets/mathmod/textures/gui/runes/raycast_block.png` | True | N/A | unique |
| 22 | `mathmod:ray_hit_position` | `mathmod:ray_hit_position` | `src/main/resources/assets/mathmod/textures/gui/runes/ray_hit_position.png` | True | N/A | unique |
| 23 | `mathmod:nearby_living` | `mathmod:nearby_entities` | `src/main/resources/assets/mathmod/textures/gui/runes/nearby_entities.png` | True | N/A | unique |
| 24 | `mathmod:filter_non_players` | `mathmod:filter_entities` | `src/main/resources/assets/mathmod/textures/gui/runes/filter_entities.png` | True | N/A | unique |
| 25 | `mathmod:filter_targets_region` | `mathmod:filter_entities_in_region` | `src/main/resources/assets/mathmod/textures/gui/runes/filter_entities_in_region.png` | True | N/A | unique |
| 26 | `mathmod:nearest_targets` | `mathmod:nearest_entities` | `src/main/resources/assets/mathmod/textures/gui/runes/nearest_entities.png` | True | N/A | unique |
| 27 | `mathmod:nearby_blocks` | `mathmod:nearby_blocks` | `src/main/resources/assets/mathmod/textures/gui/runes/nearby_blocks.png` | True | N/A | unique |
| 28 | `mathmod:filter_blocks_region` | `mathmod:filter_blocks_in_region` | `src/main/resources/assets/mathmod/textures/gui/runes/filter_blocks_in_region.png` | True | N/A | unique |
| 29 | `mathmod:block_positions` | `mathmod:block_positions` | `src/main/resources/assets/mathmod/textures/gui/runes/block_positions.png` | True | N/A | unique |
| 30 | `mathmod:average_position` | `mathmod:average_position` | `src/main/resources/assets/mathmod/textures/gui/runes/average_position.png` | True | N/A | unique |
| 31 | `mathmod:push_self` | `mathmod:push_self` | `src/main/resources/assets/mathmod/textures/gui/runes/push_self.png` | True | N/A | unique |
| 32 | `mathmod:debug_marker` | `mathmod:debug_marker` | `src/main/resources/assets/mathmod/textures/gui/runes/debug_marker.png` | True | N/A | unique |
| 33 | `mathmod:blink` | `mathmod:blink_self_to_hit` | `src/main/resources/assets/mathmod/textures/gui/runes/blink_self_to_hit.png` | True | N/A | unique |
| 34 | `mathmod:push_targets_plan` | `mathmod:push_entities_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/push_entities_plan.png` | True | N/A | unique |
| 35 | `mathmod:execute_plan` | `mathmod:execute_effect_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/execute_effect_plan.png` | True | N/A | unique |
| 36 | `mathmod:right_basis_vector` | `mathmod:right_basis_vector` | `src/main/resources/assets/mathmod/textures/gui/runes/right_basis_vector.png` | True | N/A | distinct basis glyph |
| 37 | `mathmod:forward_basis_vector` | `mathmod:forward_basis_vector` | `src/main/resources/assets/mathmod/textures/gui/runes/forward_basis_vector.png` | True | N/A | distinct basis glyph |
| 38 | `mathmod:oblique_basis_vector` | `mathmod:oblique_basis_vector` | `src/main/resources/assets/mathmod/textures/gui/runes/oblique_basis_vector.png` | True | N/A | distinct basis glyph |
| 39 | `mathmod:sine_number` | `mathmod:number_sin` | `src/main/resources/assets/mathmod/textures/gui/runes/number_sin.png` | True | N/A | unique |
| 40 | `mathmod:cosine_number` | `mathmod:number_cos` | `src/main/resources/assets/mathmod/textures/gui/runes/number_cos.png` | True | N/A | unique |
| 41 | `mathmod:cross_with_up` | `mathmod:vector_cross` | `src/main/resources/assets/mathmod/textures/gui/runes/vector_cross.png` | True | N/A | unique |
| 42 | `mathmod:project_onto_look` | `mathmod:vector_project` | `src/main/resources/assets/mathmod/textures/gui/runes/vector_project.png` | True | N/A | unique |
| 43 | `mathmod:reflect_across_up` | `mathmod:vector_reflect` | `src/main/resources/assets/mathmod/textures/gui/runes/vector_reflect.png` | True | N/A | unique |
| 44 | `mathmod:quarter_turn_vector` | `mathmod:cyclic_rotate_y` | `src/main/resources/assets/mathmod/textures/gui/runes/cyclic_rotate_y.png` | True | N/A | unique |
| 45 | `mathmod:heal_self` | `mathmod:heal_entities_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/heal_entities_plan.png` | True | N/A | unique |
| 46 | `mathmod:speed_self` | `mathmod:speed_entities_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/speed_entities_plan.png` | True | N/A | unique |
| 47 | `mathmod:invisibility_self` | `mathmod:invisibility_entities_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/invisibility_entities_plan.png` | True | N/A | unique |
| 48 | `mathmod:night_vision_self` | `mathmod:night_vision_entities_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/night_vision_entities_plan.png` | True | N/A | unique |
| 49 | `mathmod:wither_hostiles` | `mathmod:wither_entities_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/wither_entities_plan.png` | True | N/A | unique |
| 50 | `mathmod:soul_bind_hostiles` | `mathmod:soul_bind_entities_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/soul_bind_entities_plan.png` | True | N/A | unique |
| 51 | `mathmod:vital_infusion_self` | `mathmod:vital_infusion_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/vital_infusion_plan.png` | True | N/A | unique |
| 52 | `mathmod:alchemical_mantle` | `mathmod:combine_effect_plans` | `src/main/resources/assets/mathmod/textures/gui/runes/combine_effect_plans.png` | True | N/A | unique |
| 53 | `mathmod:parsimony_self` | `mathmod:parsimony_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/parsimony_plan.png` | True | N/A | unique |
| 54 | `mathmod:conservation_self` | `mathmod:conservation_plan` | `src/main/resources/assets/mathmod/textures/gui/runes/conservation_plan.png` | True | N/A | unique |
| 55 | `mathmod:finite_difference` | `mathmod:finite_difference` | `src/main/resources/assets/mathmod/textures/gui/runes/finite_difference.png` | True | N/A | unique |
| 56 | `mathmod:simpson_integral` | `mathmod:simpson_integral` | `src/main/resources/assets/mathmod/textures/gui/runes/simpson_integral.png` | True | N/A | unique |
| 57 | `mathmod:abs_number` | `mathmod:number_abs` | `src/main/resources/assets/mathmod/textures/gui/runes/number_abs.png` | True | N/A | unique |
| 58 | `mathmod:min_number` | `mathmod:number_min` | `src/main/resources/assets/mathmod/textures/gui/runes/number_min.png` | True | N/A | unique |
| 59 | `mathmod:max_number` | `mathmod:number_max` | `src/main/resources/assets/mathmod/textures/gui/runes/number_max.png` | True | N/A | unique |
| 60 | `mathmod:power_number` | `mathmod:number_power` | `src/main/resources/assets/mathmod/textures/gui/runes/number_power.png` | True | N/A | unique |
| 61 | `mathmod:sqrt_number` | `mathmod:number_sqrt` | `src/main/resources/assets/mathmod/textures/gui/runes/number_sqrt.png` | True | N/A | unique |
| 62 | `mathmod:log_number` | `mathmod:number_log` | `src/main/resources/assets/mathmod/textures/gui/runes/number_log.png` | True | N/A | unique |
| 63 | `mathmod:exp_number` | `mathmod:number_exp` | `src/main/resources/assets/mathmod/textures/gui/runes/number_exp.png` | True | N/A | unique |
| 64 | `mathmod:atan2_number` | `mathmod:number_atan2` | `src/main/resources/assets/mathmod/textures/gui/runes/number_atan2.png` | True | N/A | unique |
| 65 | `mathmod:lerp_number` | `mathmod:number_lerp` | `src/main/resources/assets/mathmod/textures/gui/runes/number_lerp.png` | True | N/A | unique |
| 66 | `mathmod:at_least_number` | `mathmod:number_at_least` | `src/main/resources/assets/mathmod/textures/gui/runes/number_at_least.png` | True | N/A | unique |
| 67 | `mathmod:select_number` | `mathmod:number_select` | `src/main/resources/assets/mathmod/textures/gui/runes/number_select.png` | True | N/A | unique |

No duplicate icon rune id was found across the 67 rows. The three basis icons
are intentionally distinct; visual family resemblance is not identity.

## LU-R2 — Exact narrator-copy matrix

Substitution arguments are `%s` unless noted. `fallback` is the expected
bounded behavior when the translation/presentation is missing; it is not a
new key and does not authorize Java changes.

| Key/state | EN exact copy | PT-BR exact copy | Args | Fallback | Current/future owner | Runtime evidence |
|---|---|---|---|---|---|---|
| Inspector open action | `screen.mathmod.rune_inspector.open_hint` = “Open a read-only graph inspection. This never changes the proof.” | `screen.mathmod.rune_inspector.open_hint` = “Abre uma inspeção somente leitura. Isto nunca altera a prova.” | none | Technical label `Rune Inspector`; remain read-only | A0-TM-03 | A0-TM-03 focused EN/PT-BR Inspector preview |
| Inspector read-only state | `screen.mathmod.rune_inspector.read_only` = “Read-only projection of the authoritative proof” | `screen.mathmod.rune_inspector.read_only` = “Projeção somente leitura da prova autoritativa” | none | Technical state text; no Advanced label | A0-TM-03 | A0-TM-03 focused EN/PT-BR Inspector preview |
| Inspector empty selection | `screen.mathmod.rune_inspector.empty` = “No node is selected.” | `screen.mathmod.rune_inspector.empty` = “Nenhum nó está selecionado.” | none | Technical empty-state text | A0-TM-03 | A0-TM-03 focus/empty-state evidence |
| Inspector selected rune | `screen.mathmod.rune_inspector.rune` = “Rune: %s” | `screen.mathmod.rune_inspector.rune` = “Runa: %s” | rune label | Technical rune id | A0-TM-03 | A0-TM-03 selected-node evidence |
| Inspector formula | `screen.mathmod.rune_inspector.formula` = “Formula: %s” | `screen.mathmod.rune_inspector.formula` = “Fórmula: %s” | compact formula | Technical formula/empty bounded value | A0-TM-03 | A0-TM-03 selected-node evidence |
| Inspector normalized value | `screen.mathmod.rune_inspector.normalized` = “Normalized: %s” | `screen.mathmod.rune_inspector.normalized` = “Normalizado: %s” | normalized value | Technical normalized value | A0-TM-03 | A0-TM-03 selected-node evidence |
| Inspector node budget | `screen.mathmod.rune_inspector.budget` = “Node budget: %s” | `screen.mathmod.rune_inspector.budget` = “Orçamento do nó: %s” | budget | Technical numeric budget | A0-TM-03 | A0-TM-03 selected-node evidence |
| Inspector dynamic inputs | `screen.mathmod.rune_inspector.dependencies` = “Dynamic inputs: %s” | `screen.mathmod.rune_inspector.dependencies` = “Entradas dinâmicas: %s” | dependency summary | Technical input names/ids | A0-TM-03 | A0-TM-03 named-input evidence |
| Inspector fixed materials | `screen.mathmod.rune_inspector.materials` = “Fixed materials: %s” | `screen.mathmod.rune_inspector.materials` = “Materiais fixos: %s” | material summary | Technical material id | A0-TM-04 | A0-TM-04 fallback/resource evidence |
| Inspector witness attributes | `screen.mathmod.rune_inspector.attributes` = “Witness attributes: %s” | `screen.mathmod.rune_inspector.attributes` = “Atributos testemunha: %s” | attribute summary | Technical attribute id | A0-TM-04 | A0-TM-04 fallback/resource evidence |
| Guided palette narration | `screen.mathmod.rune_programmer.palette_narration` = “Rune form palette. %s” | `screen.mathmod.rune_programmer.palette_narration` = “Paleta de formas rúnicas. %s” | selected palette content | Technical category/form id | A0-TM-03 | A0-TM-03 keyboard/narrator evidence |
| Guided palette position | `screen.mathmod.rune_programmer.palette_position` = “%s of %s” | `screen.mathmod.rune_programmer.palette_position` = “%s de %s” | position, total | Numeric position plus technical form id | A0-TM-03 | A0-TM-03 keyboard/viewport evidence |
| Guided palette usage | `screen.mathmod.rune_programmer.palette_usage` = “Use Up and Down to choose. Press Enter or Space to apply.” | `screen.mathmod.rune_programmer.palette_usage` = “Use as setas para cima e para baixo. Pressione Enter ou Espaço para aplicar.” | none | Technical keyboard instruction | A0-TM-03 | A0-TM-03 keyboard activation evidence |
| Saved proof narration | `screen.mathmod.rune_programmer.saved_palette_narration` = “Inscribed proof details.” | `screen.mathmod.rune_programmer.saved_palette_narration` = “Detalhes da prova inscrita.” | none | Technical saved-proof heading | A0-TM-04 | A0-TM-04 saved-proof evidence |
| Saved proof usage | `screen.mathmod.rune_programmer.saved_palette_usage` = “Use Up and Down, Page Up and Page Down, Home or End to read all details.” | `screen.mathmod.rune_programmer.saved_palette_usage` = “Use as setas para cima e para baixo, Page Up, Page Down, Home ou End para ler todos os detalhes.” | none | Technical keyboard instruction | A0-TM-04 | A0-TM-04 saved-proof navigation evidence |

The matrix uses only existing keys. The `Current/future owner` column is
explicit: A0-TM-03 owns current Guided palette and Inspector presentation
consumption; A0-TM-04 owns later compatibility/fallback hardening. Missing
translations must not create a
new parser alias or semantic identity; the owner must expose bounded technical
content and retain the valid graph. A0-TM-03 owns current Guided palette
label/category/parameter/narration evidence. A0-TM-04 owns hardening for
missing descriptors, saved proofs, fallback, reload/reconnect, and
dedicated-server independence.

## LU-R3 — Bilingual old/new changed-key audit

The EN locale was unchanged for all five keys. The PT-BR old values below are
the values present before the accepted LU-01 content delta; the new values are
the values now in the repository.

| Key | Old EN | New EN | Old PT-BR | New PT-BR |
|---|---|---|---|---|
| `screen.mathmod.rune_inspector.open_hint` | `Open a read-only graph inspection. This never changes the proof.` | unchanged | `Abre uma inspecao somente leitura. Isto nunca altera a prova.` | `Abre uma inspeção somente leitura. Isto nunca altera a prova.` |
| `screen.mathmod.rune_inspector.read_only` | `Read-only projection of the authoritative proof` | unchanged | `Projecao somente leitura da prova autoritativa` | `Projeção somente leitura da prova autoritativa` |
| `screen.mathmod.rune_inspector.empty` | `No node is selected.` | unchanged | `Nenhum no esta selecionado.` | `Nenhum nó está selecionado.` |
| `screen.mathmod.rune_inspector.formula` | `Formula: %s` | unchanged | `Expressão: %s` | `Fórmula: %s` |
| `screen.mathmod.rune_inspector.dependencies` | `Dynamic inputs: %s` | unchanged | `Entradas dinamicas: %s` | `Entradas dinâmicas: %s` |

No translation key was added, removed, renamed, or consumed differently.

## Commands and results

Commands were read-only audits after the LU-01 production delta. The commands
used were:

```powershell
$env:PYTHONIOENCODING='utf-8'
@'
from pathlib import Path
import json
en=json.loads(Path('src/main/resources/assets/mathmod/lang/en_us.json').read_text(encoding='utf-8'))
pt=json.loads(Path('src/main/resources/assets/mathmod/lang/pt_br.json').read_text(encoding='utf-8'))
print('locale_keys',len(en),len(pt),'equal',set(en)==set(pt))
'@ | python -
```

Result: `locale_keys 804 804 equal True`.

```powershell
@'
from pathlib import Path
import json
for rel in [
 'src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/inspector.json',
 'src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/inspector.json',
 'src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json',
 'src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json']:
 json.loads(Path(rel).read_text(encoding='utf-8')); print('json_ok',rel)
'@ | python -
```

Result: all four changed Patchouli JSON files parsed successfully as UTF-8.

```powershell
@'
from pathlib import Path
import re
root=Path('.')
source=(root/'src/main/java/com/mathmod/program/CustomSpellAction.java').read_text(encoding='utf-8')
rows=re.findall(r'^\s*([A-Z0-9_]+)\("([^"]+)", "([^"]+)", "([^"]*)"\)(?:,|;)$',source,re.M)
print('count',len(rows))
for _,_,icon,_ in rows:
 path=root/'src/main/resources/assets/mathmod/textures/gui/runes'/(icon.split(':',1)[1]+'.png')
 assert path.exists(), path
print('all_texture_paths_exist',True)
'@ | python -
```

Result: `count 67`; all 67 expected texture paths returned `True`; no duplicate
icon rune id was found.

The exact source paths used by the audit were:

- `src/main/java/com/mathmod/program/CustomSpellAction.java`
- `src/main/resources/assets/mathmod/textures/gui/runes/**`
- `src/main/resources/assets/mathmod/lang/en_us.json`
- `src/main/resources/assets/mathmod/lang/pt_br.json`
- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md`
- `docs/A0_TERMINOLOGY_AND_CONTENT_DECISION.md`
- `docs/A0_LU_01_GATE_REVIEW.md`
- `docs/handoffs/A0_LU_01_HANDOFF.md`

## Files changed

- `docs/handoffs/A0_LU_01F_HANDOFF.md` only.

## Migration impact

None. This correction changes no production file, resource, texture, stable
id, schema, graph, adapter, networking, or Java consumer.

## Known limitations

- Runtime narrator and fallback behavior remain executable evidence owned by
  A0-TM-03/A0-TM-04.
- This handoff does not authorize new preview modes or future Advanced,
  Source/Function, Discipline, or Notation surfaces.

## Unresolved questions

- None for LU-R1 through LU-R3.

## Next owner

- Sol, for gate acceptance of `A0-LU-01F`.

## Exact next task

- Review this handoff against `docs/A0_LU_01_GATE_REVIEW.md` and, if accepted,
  mark `A0-LU-01F` and the parent `A0-LU-01` complete.

## Files the next owner may edit

- Delivery-board state and gate decision documents according to Sol ownership.

## Files the next owner must not edit for this correction

- Production localization/Patchouli, Java, tests, textures, preview runtime,
  stable ids, persistence, networking, `ProgramGraph`, or graph expansion.
