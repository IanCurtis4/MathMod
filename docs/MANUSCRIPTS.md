# Manuscript System Plan

Status: three manuscript variants and one village source implemented. The P3
Sol slice implements the immutable lore snapshot, source precedence, bounded
aliases, pure reference migration, and atomic publication boundary. The Terra
slice adds Mojang codecs, the server reload listener, runtime item/theorem
validation, and four built-in display records. The Luna slice adds bilingual
keys, a conjecture spread, rejected-record fixtures, and preview coverage.
The first P6 Terra slice now provides a dedicated paginated reading screen. Its
bounded display view is encoded by the server when the player opens a held
manuscript, so aliases and missing records are resolved authoritatively at the
moment of reading. M3 navigation is now present: the Manual action opens the
declared Patchouli entry and the theorem action opens a local read-only graph
inspection. A full display-catalog synchronization after login/reload and
dedicated-server smoke verification remain planned.

This document is the implementation contract for MathMod manuscripts. It turns
the narrative direction in `LORE.md` into bounded technical slices and records
which item, reader, acquisition, villager, trade, and structure surfaces are
implemented versus still awaiting live acceptance.

## 1. Player Contract

A manuscript is a readable field record from one mathematical tradition. It may describe a conjecture, compare two equivalent constructions, or point to a theorem that already exists.

A manuscript may:

- Present localized pages and provenance.
- Link to a relevant Field Manual entry.
- Link to an existing theorem demonstration.
- Be acquired through configured loot or trades in a later slice.
- Grant validated `discovery`, `rune`, or `theorem` knowledge declared by its
  data record in a later progression slice.

A manuscript must not:

- Execute a graph, command, or JavaScript callback.
- Inscribe or mutate a talisman when opened.
- Claim an unimplemented conjecture is castable.
- Hide a core spell solely behind random discovery.
- Treat one fictional tradition as the sole inventor of mathemagic.
- Run arbitrary grant callbacks or bypass the server-owned knowledge service.

The implemented `mathmod:field_manuscript` stores only a manuscript id. The
three current records resolve declarative grants from the active P4 discovery
snapshot, route through the server-owned knowledge service, and open the
bilingual Patchouli entry declared by that definition.
Duplicate reads are idempotent and do not consume the item. Unknown ids show a
localized missing-record state.

## 2. Resource Model

### Traditions

Traditions are lightweight data records so modpacks can add local schools of notation without replacing MathMod lore.

Location:

`data/<namespace>/mathmod/traditions/<path>.json`

The resource id becomes the tradition id. Example:

```json
{
  "schema_version": 1,
  "name_key": "tradition.mathmod.horizon_measurers.name",
  "summary_key": "tradition.mathmod.horizon_measurers.summary",
  "icon": "minecraft:compass"
}
```

Required fields:

- `schema_version`: integer, initially `1`.
- `name_key`: localized display-name key.
- `summary_key`: localized one-line provenance key.
- `icon`: valid item id used by reading and discovery surfaces.

No chronology, faction ownership, color-coded type meaning, or executable behavior belongs in a tradition record.

### Manuscripts

Location:

`data/<namespace>/mathmod/manuscripts/<path>.json`

The resource id becomes the manuscript id. Example:

```json
{
  "schema_version": 1,
  "tradition": "mathmod:horizon_measurers",
  "title_key": "manuscript.mathmod.rotated_horizon.title",
  "page_keys": [
    "manuscript.mathmod.rotated_horizon.page.1",
    "manuscript.mathmod.rotated_horizon.page.2"
  ],
  "icon": "minecraft:compass",
  "rarity": "uncommon",
  "patchouli_entry": "mathmod:lore/field_fragments",
  "theorem": "mathmod:right_angle"
}
```

Required fields:

- `schema_version`: integer, initially `1`.
- `tradition`: namespaced id of a validated tradition.
- `title_key`: localization key, never inline player-facing prose.
- `page_keys`: one to eight localization keys, in reading order.
- `icon`: valid item id.
- `rarity`: `common`, `uncommon`, `rare`, or `epic`.

Optional fields:

- `patchouli_entry`: navigation target only.
- `theorem`: stable namespaced theorem id, not a button index.

Limits are part of validation: ids at most 128 characters, localization keys at most 160, no empty pages, no more than eight pages, and a bounded total synchronized payload.

## 3. Stable Theorem References

`TalismanPreset` ids are now stable namespaced strings such as
`mathmod:right_angle`. Laboratory Forms also persist namespaced ids and decode
their former enum names.

The implemented identity slice:

1. Adds a namespaced id to every `TalismanPreset`.
2. Preserves button ids only for menu protocol compatibility.
3. Rejects duplicate theorem ids while creating the immutable theorem index.
4. Provides canonical and legacy lookup without changing graph equality.
5. Keeps save data independent of preset availability; the graph remains the
   inscribed authority.

A manuscript theorem action resolves only a validated built-in theorem and
opens its `ProgramSurface.theorem(...).inspect()` graph locally. It has no
packet, held-item requirement, inscription operation, or editable workspace.

## 4. Loading And Precedence

`ManuscriptReloadListener` builds a new immutable snapshot on every server data reload:

1. Decode all traditions.
2. Validate tradition fields and item icons.
3. Decode manuscripts.
4. Validate local fields and tradition references.
5. Validate theorem references against the stable theorem index.
6. Resolve aliases.
7. Publish the snapshot atomically only if loading completes.

Normal data-pack priority decides duplicate resource ids. Diagnostics include the id and winning resource-pack source. Invalid definitions are omitted individually; a malformed record must not leave a partial object in the published snapshot.

Patchouli entries live in client assets and cannot be authoritatively proven by a dedicated server data reload. Therefore:

- The server validates only the syntax of `patchouli_entry`.
- The client resolves the entry after synchronization.
- A missing Patchouli mod or missing entry disables that navigation action with a localized explanation.
- A missing Patchouli target does not invalidate otherwise readable manuscript pages.

This split is intentional and must not be replaced by server guesses about client resource packs.

## 5. Aliases And Migration

Aliases use:

`data/<namespace>/mathmod/manuscript_aliases/<path>.json`

```json
{
  "schema_version": 1,
  "from": "mathmod:old_record",
  "to": "mathmod:rotated_horizon"
}
```

Rules:

- Alias chains resolve to one current id.
- Cycles, self-aliases, and two aliases competing for the same `from` id are rejected.
- An alias is supported for one documented schema migration window.
- The held item is normalized to the current id only during an explicit server-side inventory interaction or data-fix pass, never by client rendering.
- If neither definition nor alias exists, the item shows its id and a localized missing-record page. It does not silently become another manuscript.

## 6. Server And Client State

The server owns:

- Validated definitions and aliases.
- Item manuscript ids.
- Loot and trade eligibility.
- Any future discovery or unlock state.

The client receives one bounded synchronized display snapshot after login and after successful reload:

- Manuscript id, title key, page keys, tradition display data, icon, rarity, and validated navigation references.
- No loot selectors, trade weights, callbacks, commands, or executable graph payloads.

Packet decoding rejects excessive record counts, page counts, string lengths, and unknown schema versions. A reload replaces the client snapshot as one unit so a reading screen never combines old traditions with new manuscripts.

The schema-3 player attachment stores discoveries with bounded persistence,
owner-only synchronization, aliases, and migration. The first manuscript read
routes through this same server-owned knowledge service; there is no second
discovery protocol.

## 7. Item And Reading Surface

The first item slice adds:

- `FieldManuscriptItem`.
- A Data Component containing only the namespaced manuscript id.
- A server-authorized open action.
- A read-only Patchouli entry for the implemented record.
- A localized missing-definition fallback.

The first dedicated paginated reader is implemented as a read-only menu. It
receives only the bounded resolved view needed for the opened item: title,
tradition, rarity, page localization keys, and optional navigation references.
An alias therefore keeps the held id visible to server logic while displaying
the canonical record, and an unresolved id opens a localized missing-record
page rather than selecting a replacement.

A full synchronized display registry and record schema component remain later
slices. The Luna navigation slice now exposes the declared Field Manual entry
and a theorem inspection action. The latter is deliberately read-only: a valid
referenced theorem opens the existing Inspector from its immutable built-in
graph, without requiring or opening a talisman.

Expected controls:

- Previous/next page icons.
- Close.
- `Open Field Manual` when the Patchouli reference resolves.
- `Demonstrate Theorem` when the theorem reference resolves.

The screen shows title, tradition, page text, and navigation. It does not show execution controls, costs, or an editable graph. Long EN/PT-BR text must paginate or scroll within stable bounds rather than shrink with viewport width.

## 8. Initial Content

The current P3/P6 progression ships three bilingual playable records:

- `mathmod:rotated_horizon`: a Horizon Measurer record that grants the cyclic
  element and Y-rotation runes plus the Quarter Turn theorem.
- `mathmod:bound_measure`: a Compounder record that grants the Soul Constraint
  plan and theorem, leading into the Vital Correspondence study.
- `mathmod:ledger_of_remainders`: a Keeper record that grants the Parsimony
  plan and theorem, leading into the Conserved Remainder study.

The future data-model slice adds three more lore-first records:

- `mathmod:weighted_gathering`: a Gatherer Of Means conjecture about distributing an effect across weighted targets.
- `mathmod:repeating_boundary`: a Boundary Builder conjecture about repeating an anchor-relative pattern.
- `mathmod:equivalent_routes`: an unattributed comparison in which two traditions reach equivalent constructions through different notation.

The latter three remain conjectures until their generic runes, costs, limits,
and server execution exist. `equivalent_routes` must not nominate either
notation as the original or superior form.

### Alternate Demonstration Rules

An alternate demonstration is meaningful only when the player can inspect how it differs. A manuscript comparison may vary:

- Rune decomposition: one proof uses several primitive steps while another uses a later reusable form.
- Coordinate frame: world-relative and observer-relative routes reach a comparable result under stated premises.
- Query strategy: a direct target and a filtered collection produce the same selected entity in the documented example.
- Material preparation: two valid witness plans satisfy the same requirements with different pack-local materials.

The record must state the premises under which the results are equivalent. A future `Demonstrate Theorem` action may preselect either graph for read-only comparison, but equivalence is never inferred from title or tradition. Structural comparison uses stable theorem ids and typed graphs; runtime equivalence outside bounded examples is a later research feature, not an M0-M3 promise.

Tradition provenance on built-in theorems is already structured in `TalismanPreset` and exposed through one localized Programmer tooltip/narration line. Manuscript data must reference a tradition id independently; it must not parse that visible line or assume that one theorem has only one historical route.

## 9. Acquisition Roadmap

The first acquisition source is implemented as an add-table global loot
modifier for `minecraft:chests/village/village_cartographer`; its subtable has
a one-in-three manuscript outcome and selects the three current component ids
with equal weight. Core onboarding is not in this loot pool.

Later acquisition remains separate from the current cartographer pool:

1. Configurable structure loot using validated manuscript ids.
2. Archaeology-style sources only after duplicate behavior is defined.
3. Mathemagician trades after the profession works without a custom house.
4. Optional village structure after profession, trades, and loot are independently testable.

Duplicate manuscripts remain readable. A future collection index may mark known records, but duplicates must still have a pack-configurable use such as trade-back or paper recovery before broad loot injection.

Core theorem examples remain available in the Programmer. Loot and trades provide narrative discovery and optional teaching routes, not mandatory access to baseline casting.

## 10. KubeJS Boundary

The P7 manuscript KubeJS API is callable from startup scripts. It mirrors the
codecs, accepts declarative values only, and follows the contract in
`P7_KUBEJS_MANUSCRIPT_API_CONTRACT.md`:

```javascript
MathMod.tradition("pack:surveyors", {
  schemaVersion: 1,
  nameKey: "tradition.pack.surveyors.name",
  summaryKey: "tradition.pack.surveyors.summary",
  icon: "minecraft:spyglass"
})

MathMod.manuscript("pack:ridge_measurement", {
  schemaVersion: 1,
  tradition: "pack:surveyors",
  titleKey: "manuscript.pack.ridge_measurement.title",
  pageKeys: ["manuscript.pack.ridge_measurement.page.1"],
  icon: "minecraft:paper",
  rarity: "common"
})
```

The final API uses `built-in < KubeJS < data pack`, freezes one startup
generation before reload, and rejects duplicate KubeJS ids. It never accepts
callbacks, commands, executor keys, grant lists, direct loot mutation, or
talisman writes.

## 11. Implementation Slices

### M0a: Stable Ids (Implemented)

- Add namespaced theorem ids and duplicate validation.
- Add namespaced Laboratory Form ids and decode former enum-name saves.
- Add kind-aware alias validation and migration primitives.

Acceptance: stable identities exist without an item, screen, packet, or
gameplay unlock.

### M0b: Lore Records And Codecs (Implemented)

- Implemented: validated tradition/manuscript/alias Java records, schema
  boundary, Mojang codecs, and pure decode fixtures.

Acceptance: no item, screen, packet, or gameplay unlock exists.

### M1: Reloadable Lore Data (In Progress)

- Implemented: immutable snapshot builder/store, deterministic source
  precedence, source-aware diagnostics, bounded flattened aliases, pure
  reference migration, resource reload adapter, registry-backed icon/theorem
  validation, and four built-in lore-only records.
- Implemented: bilingual fixture coverage, rejected-record matrix, and
  Patchouli/preview coverage.
- Next: complete a clean dedicated-server reload smoke test in the local
  NeoForge runtime.

Acceptance: deterministic reload, bilingual keys, invalid records omitted, no execution path.

### M2: Synchronization And Reading (Partially Implemented)

- Implemented: manuscript Data Component and item.
- Implemented: server-authorized, bounded on-demand reader view with aliases
  and missing-record fallback.
- Implemented: read-only EN/PT-BR paginated screen and compact/missing preview
  cases.
- Remaining: optional bounded login/reload display catalog, reconnect test,
  and a real narrator pass.

Acceptance: reader views remain server-resolved; EN/PT-BR mouse, keyboard,
compact layout, missing-record behavior, reconnect, reload, and narration all
remain covered.

### M3: Demonstration Links (Implemented)

- Implemented: explicit Field Manual action through the declared, validated
  Patchouli entry.
- Implemented: theorem action opens `ProgramSurface.theorem(...).inspect()`
  locally, rather than the mutable Programmer.
- Implemented: stable disabled/unavailable states for absent Patchouli and
  absent or invalid theorem references.
- Implemented prerequisite: built-in theorems carry structured provenance and expose one localized hover/narration line without changing catalog layout.
- Deferred: extend provenance from the current built-in enum to synchronized
  tradition ids only after pack-defined theorem data exists.

Acceptance: no navigation action inscribes, edits, writes, or executes; compact
theorem inspection remains bounded in EN/PT-BR.

### M4: Acquisition

- Sol contract completed in `docs/P10_MANUSCRIPT_ACQUISITION_CONTRACT.md`:
  acquisition data remains separate from narrative definitions, loot/trades
  consume only a validated immutable snapshot, and surplus exchange is bounded
  without inspecting player knowledge during loot generation.
- Implemented Terra Medium core: acquisition codecs, immutable candidate
  snapshot builder, canonical alias resolution, feature configuration, and
  deterministic weighted selection.
- Implemented Terra High publication: reload builds acquisition candidates next
  to manuscript lore and atomically publishes both snapshots with one server
  configuration snapshot. The startup path safely uses defaults before the
  `SERVER` config loads, and a GameTest covers the initial generation.
- Implemented the first configurable loot selector: the cartographer chest
  modifier reads only the published named pool, observes the chance/feature
  policy, and adds one canonical manuscript at most. Config loading and reload
  republish a complete policy generation.
- Luna added four built-in acquisition records, cartographer pool weights,
  bounded future trade metadata, and bilingual Patchouli guidance. Cartographer
  chests can now yield the four canonical manuscript routes.
- Terra added the craftable Demonstration Table, its POI, and the
  config-gated Mathemagician. Paper buyback permits novice progression, while
  levels two through five offer deterministic canonical manuscript records.
- Terra Medium added safe reconciliation for marked manuscript offers. It skips
  open menus and unmarked offers, retains use and price state for records that
  remain valid, removes rejected records, and fills missing deterministic
  career slots for loaded Mathemagicians.
- Next: add live dedicated-server economy and world-generation coverage before
  enabling the field house by default.
- Terra Medium added the optional config-gated field house. It generates rarely
  in plains-village biomes with a Demonstration Table and one chest using the
  same validated acquisition boundary. It is intentionally independent rather
  than a replacement for a vanilla village jigsaw pool.
- Test dedicated-server reload and multiplayer authority.

Acceptance: baseline theorems remain accessible without random discovery.

### M5: Optional Structure

- P10 freezes the Demonstration Table as a craftable point of interest so the
  profession remains usable without world generation.
- Implemented: registered table/POI and an independently placed rare field
  house in plains-village biomes, with a validated acquisition chest.
- The structure deliberately does not replace vanilla jigsaw pools and does
  not claim a village-road connection or resident villager.
- Keep structure generation independently disableable and disabled by default.
- Future: use a safe append-only pool API or explicitly approved mixin before
  attempting a road-attached village piece.

Acceptance: profession and manuscripts still work when structure generation is off.

## 12. Test Matrix

Unit tests:

- Valid and invalid tradition/manuscript codecs.
- Unknown schema, rarity, icon, tradition, and theorem.
- Page/string/count limits.
- Duplicate ids and resource precedence.
- Alias resolution, chains, cycles, and missing targets.
- Stable theorem id uniqueness.
- Packet size and decode limits.

Integration tests:

- Dedicated-server initial load and `/reload`.
- Client login after server reload.
- Removed definition and alias fallback.
- Missing Patchouli mod and missing client entry.
- Multiplayer item open authority.
- Loot/trade selectors never reference rejected ids.

Visual tests:

- EN/PT-BR standard and compact reading.
- Long title, maximum pages, and final-page bounds.
- Missing definition.
- Missing Patchouli reference.
- Available and unavailable theorem actions.
- Keyboard focus, narration, and JEI coexistence.

## 13. Required Documentation Updates

Each implementation slice updates these files in the same change:

- `docs/LORE.md`: canon, voice, implemented traditions, and conjecture status.
- `docs/EPICS.md`: completed slice, next dependency, and acceptance.
- `docs/KUBEJS.md`: exact callable signatures only when available.
- `docs/MATHEMATICAL_GAMEPLAY_ROADMAP.md`: conjecture promotion and required generic runes.
- `docs/UI_PREVIEWS.md`: deterministic preview modes and expected artifacts.
- `docs/UX_AUDIT.md`: discovery, readability, controls, narration, and GUI restraint.
- `docs/SAFETY.md`: authority, reload, packet, loot, trade, and migration boundaries.

The Patchouli Field Manual must distinguish present mechanics from planned conjectures in every release.

### Release Gate

Before starting the next manuscript slice, the current slice must leave a dated or release-scoped status in `EPICS.md`, preserve the current/next/future distinction in `LORE.md`, and update every document whose responsibility changed. Planning prose alone never marks a player-visible system as implemented. In particular:

- M0-M1 do not add an item, profession, trade, structure, unlock, or execution path.
- M2 must update reader UI previews, accessibility findings, protocol limits, and missing-definition behavior together.
- M3 must update conjecture-promotion rules and prove that navigation cannot inscribe or mutate a talisman.
- M4 must freeze duplicate, loot, trade, configuration, and server-authority policies before survival acquisition ships.
- M5 must remain independently disableable and pass dedicated-server generation tests with manuscripts and the profession still usable when world generation is off.
