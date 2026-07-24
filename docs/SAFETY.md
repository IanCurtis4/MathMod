# MathMod Safety And Balance Notes

The MVP execution model is intentionally constrained.

## Progression Authority

- Player knowledge is a server-owned, schema-versioned attachment.
- Clients receive only their own synchronized snapshot and never authorize a
  grant, unlock, or migration.
- Administrative mutations require permission level 2 and pass through the
  same bounded knowledge service used by future gameplay events.
- Alias cycles, conflicting aliases, invalid namespaced ids, and oversized
  knowledge sets are rejected.
- Study progress is capped at 4,096 counters with values no greater than 64;
  only successful server-side casts advance active epiphanies.
- Manuscript grants are declarative and idempotent. Duplicate reading neither
  duplicates grants nor consumes the held manuscript.
- Progression gates construction/editing only. Execution validates the
  inscribed graph and resources, not current discovery state.
- The first village source injects a bounded subtable only into cartographer
  chests. Its three explicit manuscript components share one one-in-three
  outcome; it does not replace the vanilla table or gate starter content.
- Progression definitions use immutable snapshots with data-pack over KubeJS
  over built-in precedence. Definition and alias publication are independently
  transactional.
- Data-pack caps are 256 epiphanies, 1,024 discoveries, and 4,096 aliases.
  Individual definitions retain their study and grant caps.
- The Field Ledger receives a bounded server-authored display view. Its screen
  cannot grant knowledge, inscribe a graph, or execute an effect.
- P6 manuscript use builds a bounded reader view from the validated active
  manuscript definition. The Manual action accepts no target from client state;
  it uses only that server-resolved view.
- Schema-2 migration grants only newly gated P6 constructions; it does not
  erase or auto-complete deliberate P2/P3 progress.

## Graph Limits

- Maximum nodes: 64
- Maximum edges: 128
- Maximum carrier budget limit: 128
- Executable item and anchor programs must output `unit`.
- Executable programs must use Java-side executor keys supported by MathMod.

## Scoped Function Limits

- Functional authoring source is optional and never replaces the authoritative
  executable `ProgramGraph`.
- Persisted binders use De Bruijn indices; display names never determine scope.
- Lambda bodies may call only pure runes. Runtime observations and effects are
  not reducible function bodies in the first functional slice.
- Effect runes are valid only in the terminal proof tail; they cannot be a
  `let` value, application value, or nested rune argument where a reduction
  could move, duplicate, or discard them.
- Source is capped at 256 nodes, 16 lexical binders, 64 applications, four
  function-type levels, and 4,096 compile/evaluation steps.
- Higher-order collection cost uses the declared maximum bound, capped at 64,
  rather than trusting the observed list size.
- Reading old, malformed, or future functional source never rewrites an item;
  a valid compiled graph remains executable under the existing policy.

## Runtime Limits

- Push vectors are clamped to length `1.5`.
- Raycast range is clamped to `32` blocks.
- Program validation always runs again server-side before saving or executing.
- KubeJS can tune definitions, but cannot execute spell logic in the MVP.

## Constructive Region Boundary

- P8 keeps pure regions, candidate enumeration, fill plans, transient bodies,
  and world effects as separate types. A region or preview never authorizes a
  mutation.
- The server recomputes candidate positions and exact item requirements. Client
  positions, counts, block states, mass, scale, spin, and velocity are advisory
  inputs at most and never authoritative.
- Initial fills visit at most 4,096 lattice positions, accept at most 256
  candidates, and change at most 128 simple replaceable blocks in loaded chunks.
- Fill payment uses exact block items in escrow. Every state and permission is
  revalidated before commit; handled failure rolls back admitted simple states
  before refunding items.
- Initial construct projectiles use at most 128 source voxels, create no chunk
  tickets, cannot mutate terrain, and have bounded scale, speed, spin, lifetime,
  targets, and per-owner concurrency.
- P8 transaction guarantees do not claim process-crash atomicity. Persistent or
  multi-tick construction requires a write-ahead recovery design first.
- Full policy and acceptance tests are defined in
  `docs/P8_CONSTRUCTIVE_REGIONS_CONTRACT.md`.

## Client / Server Split

- Common menu and runtime code must not import `net.minecraft.client`.
- Client screens live under `com.mathmod.client`.
- `ServerSideIsolationTest` fails if common sources import client-only Minecraft classes.

## Future Narrative Data

- `docs/MANUSCRIPTS.md` is the authoritative technical plan for narrative data limits, reload publication, aliases, synchronization, acquisition, and migration.
- The implemented lore snapshot is immutable and published as one atomic
  generation. A fatal candidate build leaves the previous generation active;
  individual invalid cross-references and aliases are omitted with source-aware
  diagnostics.
- Lore aliases are capped at 2,048, resolve through at most 16 hops during
  snapshot construction, and are flattened before runtime lookup. Client
  rendering never persists an alias migration.
- Manuscript definitions, aliases, loot eligibility, trades, and any discovery state are validated and owned by the server.
- Clients receive only the validated display data and references required to render a manuscript or contextual provenance.
- Patchouli and theorem references are navigation targets. The theorem route
  opens a client-local `ProgramSurface.theorem(...).inspect()` graph only;
  reading a manuscript must never execute a graph, write a talisman, grant
  items, or run a KubeJS callback.
- Patchouli asset availability is resolved by the client; missing Patchouli content disables navigation without invalidating synchronized manuscript pages.
- Invalid or removed definitions use an explicit unavailable/missing-record state; clients must not substitute another manuscript or theorem.
- Data-pack and future KubeJS duplicate precedence must be deterministic and logged with the winning source.
- Villager trades and structure loot must use configurable allowlists or validated manuscript ids and must survive dedicated-server data reloads.
- P10 acquisition is published as the same logical generation as the validated
  manuscript snapshot. Loot and offer factories may read only canonical ids
  from that immutable acquisition view; an empty or rejected pool fails closed.
- Chest selection never inspects opener knowledge. Mathemagician surplus
exchange is a bounded item sink, not a client or per-player duplicate claim.

## P13-P15 Boundaries

The cycle is ordered in `docs/P12_P15_EVOLUTION_PLAN.md`. P13 is now frozen by
`docs/P13_ENVIRONMENTAL_FIELD_CONTRACT.md`; P14 and P15 retain only their plan
boundaries until their own contracts are completed:

- environmental field samples are server-owned observations, never mana or a
  substitute for witnesses, catalysts, exact items, or escrow;
- seed-derived values use the world seed plus a private, persistent 256-bit
  world secret; neither value, nor their hash key, is exposed to a client,
  rune, KubeJS callback, or ordinary diagnostic;
- a missing or malformed persisted field secret fails P13 closed instead of
  silently regenerating and moving every noisy correspondence;
- vector-field evaluation inherits P5 sample plans, loaded-chunk checks, and
  per-cast caches;
- P8 fill authority does not authorize breaking, drops, replacement, or
  explosion;
- P11 mass and resistance metadata do not directly grant damage, explosion
  power, penetration, or block-mutation permission;
- destructive field effects remain disabled until P14 supplies protection,
  revalidation, rollback, drop, and dedicated-server acceptance.
- Demonstration Table, POI, profession, and persistent content remain
  registered when their behavior is disabled. Optional village-pool injection
  is independently gated and cannot be required for profession access.
- Full acquisition bounds, reload reconciliation, economy limits, and
  worldgen acceptance live in
  `docs/P10_MANUSCRIPT_ACQUISITION_CONTRACT.md`.
