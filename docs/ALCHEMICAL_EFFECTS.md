# Alchemical Effects

## Purpose

This slice expands MathMod from movement and detection into temporary living-entity transformation. Alchemical runes remain part of the same typed graph language: they construct `effect_plan` values, and only `execute_effect_plan` may mutate the world.

The beta deliberately excludes arbitrary effect ids, JavaScript callbacks, permanent attribute mutation, player ownership, and unrestricted durations. KubeJS configures evidence and cost; Java owns bounded execution.

P9 player authorization, payment ordering, defensive-effect balance, P11
physical separation, and the permanent-infusion gate are frozen in
`docs/P9_ALCHEMICAL_PLAYER_POLICY.md`. When this document and that contract
disagree, the P9 policy controls new effects.

## Rune Contract

| Rune | Inputs | Output | Default evidence |
| --- | --- | --- | --- |
| `player_as_entity_list` | `player` | `entity_list` | none |
| `heal_entities_plan` | `entities`, `amount` | `effect_plan` | restoration 2 |
| `speed_entities_plan` | `entities`, `duration`, `level` | `effect_plan` | haste 2 |
| `invisibility_entities_plan` | `entities`, `duration`, `level` | `effect_plan` | concealment 2 |
| `night_vision_entities_plan` | `entities`, `duration`, `level` | `effect_plan` | sight 2 |
| `wither_entities_plan` | `entities`, `duration`, `level` | `effect_plan` | decay 2 |
| `soul_bind_entities_plan` | `entities`, `anchor`, `duration` | `effect_plan` | binding 2, soul 1 |
| `vital_infusion_plan` | `entities`, `duration`, `level` | `effect_plan` | infusion 2, vitality 2 |
| `combine_effect_plans` | `first`, `second` | `effect_plan` | none |

`combine_effect_plans` is composition, not execution. Composite plans still have one explicit effect boundary.

## Runtime Bounds

- Healing is clamped to 8 health points per target per cast.
- Beneficial durations are clamped to 60 seconds.
- Harmful durations are clamped to 15 seconds.
- Effect amplifiers are clamped to `0..2`, corresponding to levels I through III.
- Wither and Soul Constraint skip `ServerPlayer` targets.
- Soul Constraint has a three-block free radius and a bounded corrective pull.
- Vital Infusion uses registered temporary modifiers: `+4` max health, `+2` armor, and `+1` attack damage per effect level.
- The normal Minecraft effect lifecycle removes infusion modifiers when the effect expires.

## Default Reagents

| Material id | Item | Role | Attributes | Budget | Tier |
| --- | --- | --- | --- | --- | --- |
| `vital_salt` | `mathmod:vital_salt` | consumed | restoration 3, vitality 1 | 4 | 2 |
| `mercurial_draught` | `mathmod:mercurial_draught` | consumed | haste 3, transmutation 1 | 4 | 2 |
| `umbral_powder` | `mathmod:umbral_powder` | consumed | concealment 3, decay 1 | 4 | 2 |
| `noctilucent_lens` | `mathmod:noctilucent_lens` | catalyst | sight 3, precision 1 | 4 | 2 |
| `grave_salt` | `mathmod:grave_salt` | consumed | decay 3, soul 1 | 4 | 2 |
| `binding_resin` | `mathmod:binding_resin` | consumed | binding 3, soul 2, stability 1 | 6 | 3 |
| `homuncular_matrix` | `mathmod:homuncular_matrix` | catalyst | infusion 3, vitality 3, stability 2 | 8 | 3 |

The recipes encode correspondences recognizable from Minecraft ingredients: glistering melon and sugar for restoration, redstone and iron for acceleration, fermentation and ink for concealment, golden carrot and prismarine for sight, and soul sand for decay or binding. They are pack-editable defaults rather than universal lore.

## Theorems

The Alchemy catalog category contains eight executable examples:

1. Restoration Equation
2. Mercurial Step
3. Umbral Veil
4. Noctilucent Sight
5. Withering Corollary
6. Soul Constraint
7. Vital Infusion
8. Alchemical Mantle

Alchemical Mantle demonstrates composition by combining Speed and Night Vision plans before one execution node. Every example can be loaded into the Laboratory and rebuilt from the matching Rune Forms.

## Future Slices

1. Add explicit target selection for beneficial entity support without allowing hostile player targeting.
2. Add cleansing, resistance, absorption, and controlled transmutation through the same plan boundary.
3. Define opt-in permanent infusion with attribute allowlists, reversible item-bound state, ownership rules, and migration before implementing it.
4. Add altar or anchor delivery for timed area infusion only after effect persistence and chunk-load budgets are specified.
5. Add JEI recipe categories only when ordinary crafting display no longer communicates a process; the current reagents intentionally use standard crafting recipes.

## Acceptance

- All eight theorems validate to one `unit` output and contain `execute_effect_plan`.
- Every new rune executor is in the server allowlist.
- Reagents have custom 16x16 textures, item models, recipes, bilingual names, and KubeJS material definitions.
- Patchouli documents plans, bounds, recipes, consumption roles, Soul Constraint, and Vital Infusion in both languages.
- Client gameplay validation proves at least one classic effect and both custom registered effects before permanent infusion is considered.
