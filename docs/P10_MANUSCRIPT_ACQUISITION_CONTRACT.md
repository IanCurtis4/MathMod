# P10 Manuscript Acquisition, Economy, And Worldgen Contract

## Status And Scope

Status: Sol architecture authority frozen on 2026-07-22.

P10 turns the existing manuscript item, immutable lore snapshot, and first
cartographer-chest source into a configurable survival acquisition system. It
defines three separately shippable layers:

1. validated loot and surplus policy;
2. a Mathemagician profession with bounded trades;
3. an optional village house containing a Demonstration Table.

This contract does not implement profession behavior or village structures.
The initial loot assets and content are now present, while the profession,
structure template, and world generation remain separate runtime slices. It
also does not make baseline casting depend on random discovery.

## Non-Negotiable Invariants

- The server owns manuscript eligibility, loot selection, trades, duplicate
  exchange, and world-generation configuration.
- Baseline theorems, the Programmer, and first-use documentation remain
  available without loot, a profession, or a generated structure.
- Every offered or generated manuscript id resolves through the published
  `ManuscriptSnapshot`; rejected and missing records are never selected.
- Reading is idempotent and never consumes a manuscript. Acquisition policy
  does not change the knowledge schema or grant knowledge without a read.
- A structure is optional content, not the authority for the profession. The
  Demonstration Table must be craftable when profession support is enabled.
- Disabling generation never unregisters blocks, POIs, professions, items, or
  data components that may already exist in a world.
- No client packet may request a manuscript id, force an offer reroll, select
  loot, or claim that an item is a duplicate.
- KubeJS remains declarative. It may contribute acquisition candidates in a
  future extension, but never callbacks, commands, trade execution, or direct
  player-knowledge mutation.

## Ownership And Snapshot Pipeline

P10 adds an immutable `ManuscriptAcquisitionSnapshot`. It is built only after a
candidate `ManuscriptSnapshot` has passed its existing validation.

```text
traditions + manuscripts + aliases
  -> candidate ManuscriptSnapshot
  -> acquisition pool decode
  -> canonicalize manuscript references through aliases
  -> reject missing/ineligible references and invalid economy values
  -> apply server feature configuration
  -> publish lore + acquisition snapshots as one generation
  -> loot/trade factories read that generation only
```

Lore publication must not expose a new generation while acquisition still
points at the previous one. Implementation may publish one aggregate holder or
publish both under one synchronized generation swap. A fatal acquisition-file
decode keeps the previous aggregate generation active. Invalid individual
entries are omitted with source-aware diagnostics.

Runtime consumers receive immutable views. They do not parse data packs,
inspect client assets, or cache mutable registry builders.

## Acquisition Data Model

Acquisition policy is separate from `ManuscriptDefinition`. Narrative records
remain reusable even when a pack disables every survival source.

Data lives under:

```text
data/<namespace>/mathmod/manuscript_acquisition/*.json
```

Each schema-1 record has:

| Field | Type | Bounds | Meaning |
| --- | --- | --- | --- |
| `schema_version` | integer | exactly `1` | acquisition schema |
| `id` | namespaced id | 128 characters | stable acquisition entry id |
| `manuscript` | namespaced id | canonical or valid alias | resulting record |
| `loot_pools` | id list | 0-8 unique values | eligible configured pools |
| `loot_weight` | integer | 0-1024 | relative weight in each pool |
| `trade` | optional object | bounded below | Mathemagician sale policy |

The optional `trade` object contains:

| Field | Bounds |
| --- | --- |
| `level` | `2..5` |
| `emerald_cost` | `6..24` |
| `requires_book` | boolean, default `true` |
| `max_uses` | `1..4` |
| `villager_xp` | `5..30` |
| `weight` | `1..1024` |

Two acquisition entries may not claim the same `(manuscript, loot_pool)` or
the same manuscript trade at the same level. Data-pack priority resolves a
duplicate acquisition resource id before semantic validation. Semantic
collisions are deterministic: the lexicographically first acquisition id wins
and every loser is diagnosed.

Built-in pool ids begin with:

- `mathmod:village_cartographer_chest`;
- `mathmod:mathemagician_common`;
- `mathmod:mathemagician_rare`.

Pool ids are policy selectors, not Minecraft loot-table ids. Their mapping to
world loot sources stays in bounded Java logic so a data file cannot inject a
manuscript into arbitrary tables.

## Feature Configuration

P10 uses server-owned feature configuration with independent controls:

| Setting | Default | Semantics |
| --- | --- | --- |
| `manuscriptLootEnabled` | `true` | permits configured manuscript loot |
| `mathemagicianProfessionEnabled` | `true` | permits occupation and career progression |
| `mathemagicianTradesEnabled` | `true` | permits MathMod offer creation/restock |
| `mathemagicianHouseEnabled` | `false` | permits the optional standalone field house structure set |
| `surplusPolicy` | `KEEP` | behavior for spare manuscript items |
| `villageLootChanceNumerator` | `1` | numerator, bounded `0..1000` |
| `villageLootChanceDenominator` | `3` | denominator, bounded `1..1000` |

`mathemagicianTradesEnabled` is effective only when the profession is enabled;
an inconsistent combination is normalized to disabled trades and logged once.
The house flag never enables the profession or trades implicitly.

Registries are always installed when MathMod loads. Configuration gates
behavior and data injection, not registry identity. Settings that affect
worldgen are snapshotted before world-generation data is applied and require a
server restart to change. Loot and trade settings may refresh with server
configuration, but all consumers switch to one complete policy generation.

## Loot Contract

The current static `village_discoveries` table is a compatibility source for
the pre-P10 slice. Terra replaces its hard-coded manuscript entries with a
custom bounded loot entry/function that selects from the current acquisition
snapshot.

The first dynamic loot policy is:

- at most one MathMod manuscript is added to one eligible chest;
- the configured chance defaults to one in three;
- selection is weighted only among valid entries in the named pool;
- weights use bounded integer accumulation and deterministic id ordering;
- an empty pool produces no item and no fallback manuscript;
- the resulting stack receives exactly one canonical `manuscript_id`
  component;
- loot generation does not inspect the opener, player knowledge, teams, or
  client state.

Per-player duplicate suppression is explicitly forbidden for chest loot. It
is ill-defined for unopened containers, exploitable in multiplayer, and would
make world state depend on who opened a chest first.

## Duplicate And Surplus Policy

A duplicate means a player already knows the discovery referenced by another
copy. The manuscript remains readable and unchanged. P10 does not add a
second per-item claimed flag or alter `KnowledgeDiscoveryService`.

The initial policies are:

- `KEEP`: no conversion; the item remains a readable collectible;
- `TRADE_BACK`: the Mathemagician may buy any valid Field Manuscript as
  surplus, independent of the seller's knowledge ledger.

`TRADE_BACK` is intentionally item-based rather than duplicate-aware. Villager
offers cannot safely infer that the selling player has read the exact copy,
and such a check would create confusing multiplayer ownership behavior.

The buyback offer is bounded to one emerald per manuscript, at most four uses
per restock, and can never be worth as much as the cheapest manuscript sale.
This prevents a purchase/buyback profit loop. Missing-record manuscripts are
not accepted by the generated buyback listing.

Paper recovery and automatic consumption on duplicate read are deferred. Both
would need an explicit item transaction and automation policy.

## Mathemagician Profession

The profession uses a `mathmod:demonstration_table` POI. The block, POI, and
profession are registered regardless of configuration so old worlds remain
loadable. When profession behavior is disabled, unemployed villagers do not
claim the POI and existing Mathemagicians retain identity but do not receive
new MathMod offers.

The Demonstration Table must have a normal crafting recipe. A generated house
is therefore one discovery route for the workstation, not a prerequisite.

Trade architecture uses a server-side `MathemagicianTradeCatalog` built from
the acquisition snapshot. Offer factories receive only validated immutable
entries. Content boundaries are:

- novice level teaches the material culture through bounded vanilla/MathMod
  supply exchanges and sells no random-gated core theorem;
- manuscript sales begin at apprentice level;
- at most two manuscript offers are selected per level and six across one
  villager's complete career;
- one sale yields one manuscript stack with a canonical component;
- emerald cost is `6..24`, use count `1..4`, and villager XP `5..30`;
- offer selection is stable for a villager UUID and acquisition generation,
  so reopening a menu does not reroll it;
- restock may reconcile offers but cannot reset uses beyond vanilla rules.

Existing MathMod offers carry an internal source marker sufficient for server
reconciliation. On reload/restock, valid offers preserve use counts and price
state; removed or rejected manuscript offers are deleted and replacements are
drawn from the new generation. An already open merchant menu must not mint a
new offer from client data. The Terra slice must verify whether closing menus
on acquisition-generation change is required by NeoForge's merchant lifecycle.

Profession offers supplement ordinary crafting and chest discovery. They do
not sell an otherwise unavailable baseline Programmer, talisman, chalk, or
Field Manual prerequisite.

## Optional Village Structure

The P10 structure is a small Mathemagician field house for plains-village
biomes. It contains a Demonstration Table and one chest that uses a named
validated acquisition pool.

Implementation note: NeoForge 1.21.1 does not expose a safe append-only hook
for already bootstrapped vanilla village template pools. Rather than replace a
vanilla pool and conflict with a pack's other village additions, P10 ships the
house as an independently placed rare structure in the same biome family. It
does not claim a road connection or a resident villager. A future jigsaw API or
an explicitly approved mixin may promote it to an attached village piece.

Worldgen rules:

- house generation is independently disabled by default;
- one independent custom structure set has bounded random-spread placement;
- placement is limited to the explicit vanilla plains-village biome tag;
- no vanilla template pool is replaced or mutated;
- no chunk tickets, runtime block placement, or post-generation scanning;
- the generated piece contains no fixed manuscript component ids;
- its chest resolves through the same acquisition snapshot boundary;
- disabling future generation never removes existing houses or POIs.

Biome/modded-village support is data-pack extension work, not inferred from
tags or namespace matching. A pack may replace or extend the explicit pool
allowlist through documented data after the vanilla implementation is stable.

## Reload, Persistence, And Migration

Acquisition records and economy configuration are server state, not player
save data. Villagers and generated structures are persistent world state.

- Manuscript aliases are canonicalized while building the acquisition
  snapshot; generated items always store canonical ids.
- Old item stacks keep their stored id and use normal manuscript migration on
  read.
- Existing Mathemagician villagers keep their profession when features are
  disabled, avoiding destructive world migration.
- Existing offers are reconciled only by a server-owned restock/reload path.
- Existing Demonstration Tables and houses remain functional blocks when new
  generation is disabled.
- No P10 setting rewrites player knowledge or previously generated chests.

Loot containers already generated with a loot-table reference follow vanilla
lazy generation. The implementation must document whether they observe the
current acquisition generation when first opened; P10 does not promise to
freeze an acquisition snapshot per unopened chest.

## Security And Failure Behavior

- Candidate counts are bounded to 2,048 acquisition entries, 8 loot pools per
  entry, and 1,024 candidates per resolved pool.
- Integer weights are accumulated in `long` and checked before random choice.
- Invalid costs, levels, ids, aliases, or pool names omit the individual entry.
- Empty loot or trade pools fail closed and never substitute a default id.
- Merchant output stacks are constructed server-side at offer creation.
- Client rendering receives display information already covered by the
  manuscript reader protocol, not acquisition weights or hidden trade pools.
- Commands and reload logs expose aggregate counts and diagnostics only; no
  player knowledge is logged.

## Delivery Slices

### P10-Sol: Boundaries And Economy

- Completed by this document: immutable acquisition snapshot, data schema,
  feature flags, duplicate/surplus policy, loot selection, profession economy,
  reload reconciliation, optional structure boundary, and failure rules.
- **Model:** Sol.

### P10-Terra: Runtime Implementation

- Implemented Terra Medium core: schema-1 acquisition codecs, immutable
  in-memory candidate snapshot, alias canonicalization, deterministic
  collision handling, pure feature configuration, and bounded weighted loot
  selection with unit tests. This does not yet load data packs or alter loot.
- Implemented Terra High reload boundary: data under
  `data/<namespace>/mathmod/manuscript_acquisition` is decoded alongside the
  lore definitions, and both validated snapshots plus the server configuration
  publish atomically under one generation. Bootstrap uses configuration defaults
  until the `SERVER` config becomes available, so dedicated-server datapack
  loading cannot read an unloaded config.
- A dedicated-server GameTest proves the initial published generation is
  coherent. No acquisition candidates ship yet, and no loot or villager
  behavior has changed.
- Implemented the first runtime consumer: `mathmod:manuscript_loot` replaces
  the static cartographer-table injection. It adds at most one canonical Field
  Manuscript only after the configured chance passes and only from the named
  validated pool. Empty pools and disabled loot add nothing. Loading or
  reloading `mathmod-server.toml` republishes the unchanged data with a new
  configuration generation before future loot is generated.
- Luna content now publishes four built-in candidates with cartographer loot
  weights and bounded future trade metadata. The pool is live in the baseline;
  cartographer chests can select those canonical manuscripts.
- Implemented the first independent profession runtime: the craftable
  Demonstration Table registers its POI and the Mathemagician profession, with
  no village structure dependency. Unemployed villagers only acquire the role
  when the server enables it. Novice paper buyback permits progression; levels
  two through five create deterministic canonical manuscript sales from the
  validated acquisition generation, and disabled trades create no new offers.
- Implemented bounded offer reconciliation: MathMod manuscript offers carry a
  private candidate marker. Every short server interval, unloaded worlds aside,
  loaded Mathemagicians without an open trade menu remove markers rejected by
  the current snapshot, preserve valid offer use/price state, and fill missing
  career slots from the current deterministic generation. Unmarked offers and
  open merchant menus are never mutated.
- Remaining P10 work is dedicated-server economy coverage with live trade
  progression and world-generation smoke coverage. The optional standalone
  house is implemented but remains disabled by default.
- Keep the house disabled by default until loot and profession work without it.
- **Model:** Terra with High effort for reload/economy tests, then Terra Medium
  for bounded NeoForge integration if split execution is needed.

### P10-Luna: Content And Presentation

- Completed Luna content: built-in acquisition data for four manuscripts,
  bounded trade metadata, bilingual Patchouli teaching, loot assets, and
  preview-matrix coverage.
- Completed presentation/content support for the optional field house; it uses
  the same validated acquisition pool and does not claim village-road linkage.
- **Model:** Luna.

## Acceptance Matrix

Unit tests:

- codec bounds, aliases, collisions, precedence, empty pools, and overflow;
- deterministic weighted selection with a supplied random source;
- feature-flag normalization and independent house control;
- sale/buyback economy cannot produce an emerald loop;
- offer selection is stable for UUID plus generation.

Dedicated-server tests:

- initial reload and `/reload` publish matching lore/acquisition generations;
- rejected manuscript ids never enter loot or offers;
- loot disabled, profession disabled, trades disabled, and house disabled each
  behave independently;
- profession and trades work with structure generation off;
- offer reconciliation removes invalid records without resetting valid uses;
- two players cannot use client state to alter loot or reroll offers;
- existing POIs, villagers, items, and houses survive feature disablement;
- generated structures stay within configured pools and contain no fixed
  manuscript ids.

Release gate:

1. Baseline onboarding is demonstrably completable with every P10 feature off.
2. No loot/trade output references an absent published manuscript.
3. The profession is usable in a fresh world with house generation off.
4. Duplicate handling creates no repeat-read or emerald-profit loop.
5. Reload never publishes mixed lore/acquisition generations.
6. Worldgen can be disabled without unregistering persistent content.
