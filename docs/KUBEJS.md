# MathMod KubeJS Integration

MathMod exposes a small Java bridge that KubeJS scripts can call with `Java.loadClass`. This keeps KubeJS optional: MathMod still loads when KubeJS is not installed.

## Progression Status

P4 exposes declarative progression registration. Data-pack definitions replace
KubeJS definitions with the same id, and KubeJS definitions replace built-in
fallbacks. No progression API accepts a callback or mutates one player's
knowledge directly.

P6 adds no new callable API. Its two built-in study chains use this same
surface, so packs may replace their requirements or grants by stable id under
the existing precedence rules.

The future API will accept declarative ids, requirements, grants, material
selectors, counters, and caps. It will not accept arbitrary JavaScript
execution during a cast, manuscript read, login, or knowledge migration.

Use this from `kubejs/startup_scripts/mathmod.js`:

```js
const MathMod = Java.loadClass('com.mathmod.kubejs.MathModKubeJS')

MathMod.material('allthemodium', 'allthemodium:allthemodium_ingot', 24, 4)
MathMod.material('source_gem', '#forge:gems/source', 8, 2)
MathMod.setMaterialTranslationKey('source_gem', 'material.example.source_gem')
MathMod.addMaterialAttribute('source_gem', 'arcane', 3)

MathMod.setRuneBudget('mathmod:push_self', 8)
MathMod.addRuneAttributeRequirement('mathmod:push_self', 'motion', 2)
MathMod.addRuneMaterialRequirement('mathmod:push_self', '#forge:gems/diamond', 2)
MathMod.clearRuneMaterialRequirements('mathmod:blink_self_to_hit')
MathMod.disableRune('mathmod:raycast_block')

MathMod.setAnchorSacrifice('minecraft:amethyst_shard,#c:gems/amethyst,#forge:gems/amethyst', 1, 2.5)
MathMod.setOfferingSparkDrop('minecraft:glowstone_dust', 1)
MathMod.setWardingPulse(4.0, 0.8)

MathMod.rune('pack:anchor_ping')
  .input('position', 'vec3')
  .output('unit')
  .budgetCost(3)
  .executorKey('debug_marker')
  .register()

MathMod.epiphany('pack:crystalline_resonance')
  .titleKey('epiphany.pack.crystalline_resonance.title')
  .correlation('pack:crystalline_resonance')
  .study('mathmod:amethyst', 2, 3)
  .study('mathmod:allthemodium', 4, 1)
  .grantRune('pack:resonant_projection')
  .register()

MathMod.discovery('pack:surveyors_folio')
  .manuscript('pack:surveyors_folio')
  .titleKey('discovery.pack.surveyors_folio.title')
  .patchouliEntry('pack:lore/surveyors_folio')
  .grantRune('pack:ridge_projection')
  .register()

MathMod.knowledgeAlias(
  'discovery',
  'pack:old_surveyors_folio',
  'pack:surveyors_folio'
)
```

## Item Selectors

Runtime constants that consume items, such as the `item` constant on `mathmod:consume_nearby_item`, accept simple item selectors:

- `minecraft:amethyst_shard`: one exact item id.
- `#c:gems/amethyst`: one item tag.
- `minecraft:amethyst_shard,#c:gems/amethyst,#forge:gems/amethyst`: any selector in a comma-separated list.

The built-in anchor sacrifice presets use the mixed amethyst selector above, so vanilla shards still work while modpacks can provide compatible tagged gems.

The same selector format is used by rune material requirements. Talisman execution checks these requirements before casting and consumes them after a successful survival cast. Creative players do not spend the items. By default, `mathmod:blink_self_to_hit` costs `1x minecraft:ender_pearl`.

In the Resources GUI, a material backed by one exact registered item uses that item's localized client name. Use `setMaterialTranslationKey` for tag selectors, comma-separated alternatives, or any pack-authored group that needs a localized catalog name. A material without a display key, or whose key is missing from the active client language, receives a readable label derived from its id. The catalog is sorted by these active-language names, while activation maps the visible row back to the stable server id. Hover always retains that id and the full selector, so presentation does not hide pack diagnostics.

## Resource Attributes

Attributes are stable balance ids shared by rune requirements and material contributions. Built-in ids are `arcane`, `continuity`, `force`, `information`, `mechanical`, `motion`, `orientation`, `precision`, `resonance`, `spatial`, `stability`, and `symmetry`.

Advanced-mathematics defaults add four specialist mappings: quartz is a retained resonance/precision catalyst, copper is a consumed continuity witness, lapis lazuli is a retained symmetry catalyst, and prismarine crystals are a retained orientation catalyst. They are tier 2 so older tier-1 recommendations remain stable. Packs may replace all four mappings with the ordinary material API; these vanilla choices are defaults, not hard-coded mathematical semantics or shared lore.

Player-facing surfaces resolve an attribute id through `attribute.mathmod.<id>`. MathMod provides EN/PT-BR translations for every built-in id. A pack-defined attribute without a translation receives a readable id-derived fallback, so `ancient_entropy` appears as `Ancient Entropy` instead of raw snake case. A resource pack may define the matching translation key when it needs localized or setting-specific terminology.

## API

- `material(id, itemOrTag, budgetBonus, tier)`: registers a material definition for future crafting/budget systems.
- `setMaterialTranslationKey(id, translationKey)`: assigns the client translation key used by the material catalog, tooltip, and narration.
- `setMaterialTier(id, tier)`: changes a material tier. Built-in rune tiers use levels 1 through 4.
- `addMaterialAttribute(id, attribute, amount)`: adds an abstract attribute contribution to a registered material.
- `setRuneBudget(runeId, budgetCost)`: changes the compile-time budget cost of an existing rune.
- `setRuneTier(runeId, tier)`: changes the tier required by an existing rune.
- `addRuneAttributeRequirement(runeId, attribute, amount)`: adds an abstract attribute requirement to an existing rune.
- `addRuneMaterialRequirement(runeId, itemOrTag, quantity)`: adds a material requirement to a rune definition. Talisman programs consume these requirements when a cast succeeds.
- `clearRuneMaterialRequirements(runeId)`: removes all material requirements from a rune definition.
- `enableRune(runeId)` / `disableRune(runeId)`: toggles an existing rune for validation and GUI use.
- `setAnchorSacrifice(selector, count, radius)`: configures the sacrifice selector used by built-in sacrifice anchor presets.
- `setOfferingSparkDrop(itemId, count)`: configures the item conjured by the Offering Spark preset.
- `setWardingPulse(radius, strength)`: configures the non-player entity push preset.
- `rune(id)`: starts a chainable rune definition builder.
- `epiphany(id)`: starts a declarative epiphany builder.
- `discovery(id)`: starts a declarative discovery-grant builder.
- `knowledgeAlias(kind, alias, target)`: registers a migration alias for one
  knowledge kind.

Epiphany builder methods:

- `titleKey(key)`
- `correlation(id)`
- `study(materialId, tier, successfulCasts)`
- `grantRune(id)` / `grantTheorem(id)`
- `register()`

Discovery builder methods:

- `manuscript(id)`
- `titleKey(key)`
- `patchouliEntry(id)`
- `grantRune(id)` / `grantTheorem(id)`
- `register()`

Epiphanies require two to eight distinct materials spanning at least two tiers.
Definitions allow one to sixteen grants. Grant references must exist after all
startup scripts have registered their runes. `grantTheorem` currently accepts
existing MathMod theorem ids; P4 does not add a KubeJS theorem-graph registry.

## Rune Types

Valid type ids are:

- `unit`
- `bool`
- `number`
- `vec3`
- `vec3_list`
- `entity`
- `entity_list`
- `player`
- `block_pos`
- `block_pos_list`
- `ray_hit`
- `effect_plan`

## Runtime Safety

KubeJS can register and tune definitions, but it does not execute spell logic in the MVP. `executorKey` must point at a Java-side executor supported by MathMod, such as `debug_marker`, `scale_vector`, `raycast_block`, `ray_hit_position`, or `blink_self_to_hit`. Unsupported executor keys can be registered for future planning, but execution will fail until Java implements them.

## Manuscript Display Surface

P4 registers discovery requirements and grants, not complete readable
manuscript display records. P7 now adds callable
`MathMod.manuscript(...)` and `MathMod.tradition(...)` builders for bounded
reader data.

P7 exposes the following entry points:
`tradition(String)`, `manuscript(String)`, and
`manuscriptAlias(int, String, String)`, with chainable declarative builders.
Exact required methods and examples live in
`docs/P7_KUBEJS_MANUSCRIPT_API_CONTRACT.md`.

Declarations are startup-only, freeze before the first manuscript reload, and
are reused unchanged by `/reload`. The precedence is now fixed as
`built-in < KubeJS < data pack`; duplicate KubeJS ids are rejected rather than
resolved by script call order. Builders, registry validation, diagnostics, and
reload candidate assembly are implemented.

The API will not accept JavaScript callbacks, executor keys, commands, grants,
loot mutation, direct talisman writes, or arbitrary code on read. Discovery
grants remain a separate explicit `MathMod.discovery(...)` declaration.

The reference startup script at `docs/examples/kubejs/mathmod_manuscripts.js`
registers one tradition, one manuscript, and a migration alias. Copy it to
`kubejs/startup_scripts` in a modpack and replace the example ids and
translation keys. The surface is declarative: it cannot execute spells, grant
knowledge, mutate loot, or access players and worlds.

## Built-In Alchemical Reagents

MathMod registers these defaults through the same material API available to packs:

| Id | Selector | Consumed | Attributes |
| --- | --- | --- | --- |
| `vital_salt` | `mathmod:vital_salt` | yes | restoration 3, vitality 1 |
| `mercurial_draught` | `mathmod:mercurial_draught` | yes | haste 3, transmutation 1 |
| `umbral_powder` | `mathmod:umbral_powder` | yes | concealment 3, decay 1 |
| `noctilucent_lens` | `mathmod:noctilucent_lens` | no | sight 3, precision 1 |
| `grave_salt` | `mathmod:grave_salt` | yes | decay 3, soul 1 |
| `binding_resin` | `mathmod:binding_resin` | yes | binding 3, soul 2, stability 1 |
| `homuncular_matrix` | `mathmod:homuncular_matrix` | no | infusion 3, vitality 3, stability 2 |
| `axiomatic_ink` | `mathmod:axiomatic_ink` | yes | metamagic 3, economy 3 |
| `recursive_seal` | `mathmod:recursive_seal` | no | metamagic 3, conservation 3, stability 2 |

Packs may replace these selectors with tags or cross-mod items, change their values, or change consumption. Prefer attribute requirements on alchemical runes over fixed item requirements so alternate material systems remain viable.

KubeJS still does not choose potion effects, attribute ids, durations, target policies, or callbacks. The alchemical executor keys refer to bounded Java implementations documented in `docs/ALCHEMICAL_EFFECTS.md`.
