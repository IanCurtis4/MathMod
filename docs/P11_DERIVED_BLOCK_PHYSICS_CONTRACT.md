# P11 Derived Block Physics Contract

## Status And Purpose

Status: P11-Sol authority, data, reload, and migration architecture frozen on
2026-07-22. Geometry semantics and implementation remain assigned to the Terra
slices below.

P11 introduces a bounded, server-authoritative physical interpretation for
Minecraft `BlockState` values and transient constructs. It is based on the
player-provided derived-mass proposal, but it is deliberately not a conversion
from Minecraft into real-world units.

The system exists to make material choice, center of mass, rotational inertia,
and bounded impact behavior more legible and more extensible. It must not turn
hardness, blast resistance, or pack metadata into unbounded damage, terrain
destruction, or a new arbitrary JavaScript execution surface.

P11 begins only after the remaining P8 dedicated-server and claim/protection
acceptance tests have passed. It precedes physics-dependent P9 effects and any
future terrain-impact work.

## Non-Negotiable Separation

Four quantities remain distinct:

| Quantity | Authority | Purpose |
| --- | --- | --- |
| exact item count | inventory transaction | escrow and cast payment |
| `massEquivalent` | P8 balance contract | legacy compatibility and bounded balance proxy |
| physical mass | P11 server resolver | center of mass, inertia, momentum, bounded presentation |
| density and occupied volume | P11 server resolver | inputs to physical mass |

`massEquivalent` is not renamed, reinterpreted, or removed by P11. A block may
have a high physical mass while still costing one exact item. The reverse is
also possible for a pack-defined balance item. This preserves P8 talismans and
prevents a datapack reload from silently changing the economic cost of a spell.

## Physical Profile

The pure domain contract is:

```text
BlockPhysicalProfile(
  density,
  occupiedVolume,
  physicalMass,
  structuralStrength,
  brittleness,
  elasticity,
  thermalResistance,
  magicalResistance,
  source
)

ConstructPhysicalProfile(
  totalPhysicalMass,
  centerOfMass,
  inertiaTensor,
  scalarInertiaAboutSpinAxis,
  compressionMassExponent
)
```

All values are finite, non-negative gameplay quantities. `source` records
whether a value came from an explicit override, registry entry, physical tag,
common tag, heuristic, or fallback. It is presentation/debug information and
does not expose protected-world data.

### Resolver Order

1. Explicit block-state or block override.
2. Reloaded MathMod physical-profile declaration.
3. MathMod physical material tag.
4. Pack/common material tag.
5. Bounded hardness and blast-resistance heuristic.
6. Stable fallback density.

Later sources never partially merge over an earlier complete explicit property.
Every resolver result is normalized, capped, deterministic, and cached by the
canonical default `BlockState` plus the active physical-profile snapshot
version. Schema-1 does not cache neighbor-dependent world shapes; see
`docs/P11_PHYSICS_SEMANTIC_REVIEW.md`.

## Exact Declarative Data Surface

P11 schema version 1 uses two reloadable record kinds. Profile resources live
at:

```text
data/<namespace>/mathmod/physics/profiles/<path>.json
data/<namespace>/mathmod/physics/policies/<path>.json
```

A profile resource has exactly one selector:

```json
{
  "schema_version": 1,
  "selector": {
    "type": "block",
    "id": "minecraft:obsidian"
  },
  "priority": 0,
  "density": 4.0,
  "structural_strength": 8.0,
  "brittleness": 0.2,
  "elasticity": 0.05,
  "thermal_resistance": 6.0,
  "magical_resistance": 2.0,
  "compression_mass_exponent": 0.0
}
```

The selector is either an exact registered block id (`block`) or one block tag
id (`tag`). A profile never names an item, entity, fluid, block-entity type,
NBT/component predicate, `BlockState` property expression, Java class, or
callback. State-sensitive occupied volume remains derived from the actual
server `BlockState`; profile declarations classify material properties.

Only `schema_version`, `selector`, `priority`, and `density` are required.
Omitted mechanical properties use the active policy's bounded defaults.
Unknown JSON fields are rejected in schema 1 so misspelled physical properties
cannot silently fall back.

One logical policy id is active in the first release:

```text
mathmod:default
```

Its resource shape is:

```json
{
  "schema_version": 1,
  "default_density": 1.0,
  "hardness_weight": 0.15,
  "blast_resistance_weight": 0.05,
  "fallback_base_mass": 0.2,
  "fallback_hardness_weight": 0.5,
  "fallback_blast_weight": 0.15,
  "default_compression_mass_exponent": 0.0,
  "shape_resolution": 16
}
```

External packs override `mathmod:default` as one complete record. Policy fields
do not merge across packs. Additional policy ids may be stored for future use,
but schema 1 runes cannot select them and they do not affect runtime.

### Numerical Limits

The candidate snapshot validates all values before publication:

| Property | Inclusive range |
| --- | ---: |
| profile count | `0..4096` |
| policy count | `1..64` |
| priority | `-1000..1000` |
| density | `0.01..64` |
| occupied volume | `0..1` |
| compactness | `1..8` |
| physical mass per block state | `0..256` |
| structural strength | `0..64` |
| brittleness | `0..1` |
| elasticity | `0..1` |
| thermal resistance | `0..64` |
| magical resistance | `0..64` |
| compression mass exponent | `0..3` |
| hardness weight | `0..1` |
| blast-resistance weight | `0..0.25` |
| shape resolution | `4..32`, power of two |
| cached resolved states | `32768` |
| construct physical mass | `0..32768` |

Input values are rejected rather than silently clamped. Only values read from
untrusted Minecraft block properties are normalized by the runtime fallback
adapter. Final formula outputs are finite-clamped to their result caps.

## Precedence And Ambiguity

The publication order is:

```text
built-in < frozen KubeJS startup generation < active data packs
```

Minecraft resource-pack priority selects the winning resource for an identical
profile or policy resource id. For profiles that use different resource ids but
the same selector, the highest source layer removes all lower-layer candidates.
More than one candidate for that selector inside the winning layer is invalid.
This lets a datapack replace a built-in or KubeJS selector without retaining a
hidden competing value.

After layering, exact block selectors always beat tag selectors. Matching tag
selectors are ranked by source layer and integer priority. Two matching tag
selectors with the same winning layer and priority are an ambiguity and make
the candidate snapshot unpublishable; no resource-id lexical last-wins rule is
used for physical behavior.

Unknown exact block ids, unknown selector tags after tag binding, unresolved
required policy ids, duplicate selectors, ambiguous matching tags, unsupported
schema versions, and non-finite or out-of-range values are publication errors.
An empty known tag is valid and contributes no profiles.

## Snapshot And Reload Lifecycle

The architecture follows the existing immutable registry pattern:

```text
staged built-ins
  + frozen KubeJS generation
  + decoded datapack resources
  -> candidate validation and tag binding on the server game thread
  -> immutable PhysicalProfileSnapshot(version, policy, profiles)
  -> one volatile publication
  -> new empty per-snapshot BlockState cache
```

The candidate is built without mutating the active registry or cache. Any
unpublishable candidate leaves both the previous snapshot and its cache active.
Successful publication increments a monotonic runtime version and swaps the
snapshot and cache together. Cache entries never cross snapshot versions.
The cache is server-thread-owned access-order LRU with a hard 32,768-state cap.
Eviction changes performance only; resolution is pure with respect to the
snapshot, so recomputing an evicted state produces the same profile.

Profile resolution happens only on the logical server thread. Worker code may
perform context-free JSON decoding, but it cannot touch block registries, tags,
`VoxelShape`, worlds, entities, inventories, or the active cache. A reload does
not wait for, rewrite, or cancel an already launched construct. Each flight
captures its resolved immutable construct profile at launch and completes
under that profile.

The complete profile registry is not synchronized to clients. A later preview
payload may expose only the bounded resolved projection for the currently
inscribed material and must include the snapshot version plus an `estimate`
marker. The server recomputes the authoritative value at cast time.

### Stable Diagnostic Codes

Candidate failures use source-aware diagnostics with stable codes:

```text
unsupported_schema
unknown_field
invalid_id
unknown_block
unknown_tag
duplicate_selector
ambiguous_tag_match
missing_default_policy
invalid_number
out_of_range
too_many_profiles
too_many_policies
```

Diagnostics may identify resource ids, selector ids, fields, and pack source.
They never reveal claim ownership, player inventory, world coordinates, or
private server data.

## KubeJS Declaration Boundary

The future public startup-only surface is frozen as:

```text
MathMod.physicsProfile(id)
  .schemaVersion(1)
  .block(blockId) | .tag(blockTagId)
  .priority(int)
  .density(double)
  .structuralStrength(double)
  .brittleness(double)
  .elasticity(double)
  .thermalResistance(double)
  .magicalResistance(double)
  .compressionMassExponent(double)
  .register()

MathMod.physicsPolicy("mathmod:default")
  .schemaVersion(1)
  .defaultDensity(double)
  .hardnessWeight(double)
  .blastResistanceWeight(double)
  .fallbackBaseMass(double)
  .fallbackHardnessWeight(double)
  .fallbackBlastWeight(double)
  .defaultCompressionMassExponent(double)
  .shapeResolution(int)
  .register()
```

Builders stage context-free scalar data. The first physics reload freezes the
KubeJS generation; later `/reload` calls reuse it without invoking JavaScript.
Changing startup declarations requires a server restart. Duplicate KubeJS ids,
post-freeze registration, callbacks, suppliers, predicates, Java objects,
world/player references, and mutation handlers are rejected.

## Compatibility And Migration Matrix

| Existing state | P11 behavior |
| --- | --- |
| P8 graph without physics metadata | Deserializes unchanged and resolves current server profile at cast time. |
| Cavalieri Projectile | Keeps stone, item count, `massEquivalent`, scale, speed, and `gamma = 0`. |
| Stored selected resources | Never rewritten or re-priced by profile reload. |
| Construct already in flight | Finishes with the immutable profile captured at launch. |
| Server restart | Flights remain transient and are not restored; definitions rebuild before new casts. |
| Unknown modded block | Uses the active bounded fallback after exact/tag resolution misses. |
| Historical numeric rune | Remains an unannotated `Number`; no inferred mass unit is persisted. |
| P8 preview | Remains valid as a balance preview; P11 adds a separately labeled physical estimate. |

P11 adds no Data Component and no program-graph schema version in the Sol
slice. If a future rune lets an author select `gamma` or a named policy, that
rune must carry an explicit constant and receive its own graph migration review.

### Mass Formula

For a resolved density, the initial derived mass is:

```text
compactness = 1
  + hardnessWeight * log1p(max(0, hardness))
  + blastWeight * log1p(max(0, blastResistance))

physicalMass = occupiedVolume * density * compactness
```

All configurable coefficients and result caps live in data, not in rune code.
Negative, non-finite, unavailable, or extreme block properties must resolve to
documented bounded fallback behavior. When both hardness and blast resistance
are unavailable, `default_density` supplies the fallback density; otherwise the
bounded fallback heuristic supplies it.

## Geometry And Construct Aggregation

Occupied volume is derived from a `VoxelShape`. P11 must choose and document
one deterministic union strategy before runtime integration. The first release
may use bounded voxel discretization or precomputed shape classes; it may not
naively sum overlapping AABBs.

For voxel centers `r_i` and resolved masses `m_i`:

```text
M = sum(m_i)
c = sum(m_i * r_i) / M
I = sum(m_i * (|d_i|^2 * identity - outer(d_i, d_i)))
d_i = r_i - c
```

The existing scalar inertia for a chosen spin axis remains a derived projection
of the tensor. Source voxel order never changes these results.

## Compression And Motion

P8 behavior is the compatibility default:

```text
gamma = 0
physicalMassAfterCompression = physicalMassBeforeCompression * scale^gamma
massEquivalentAfterCompression = massEquivalentBeforeCompression
```

P11 may support a bounded declared `gamma` in `[0, 3]` for newly authored
physical constructs. `gamma = 3` conserves density; intermediate values are a
pack-controlled magical conservation rule. Existing P8 presets, saved graphs,
and previews use `gamma = 0` unless an explicit future migration opts in.

Momentum, energy, and angular quantities are planning inputs only:

```text
p = M * v
E = 0.5 * M * |v|^2
L = I * omega
```

They feed monotonic, capped gameplay schedules. They do not directly grant
terrain damage, uncapped entity damage, penetration, explosion power, or chunk
loading. P8's one-flight-per-owner, swept collision, unloaded-chunk stop, and
target cap remain authoritative.

## Retroactive Rules

1. Existing talismans deserialize unchanged. No graph schema migration is
   required merely to add physical profiles.
2. Existing P8 item payment, escrow, `massEquivalent`, source-voxel cap, and
   resource-plan rules remain unchanged.
3. Physical profiles are resolved only on the server at planning/execution
   time. Client previews are advisory and must label their values as estimates.
4. Reloading physical data may change future physical behavior, but never
   modifies stored graph constants, selected resources, or past payment.
5. The P8 equal-mass center/inertia model is retained for legacy balance
   display. New physical mechanics use the mass-weighted aggregate only after
   server resolution succeeds.
6. A missing profile uses the bounded fallback; it does not reject a valid
   simple `BlockItem` solely because another mod has no integration.
7. `CONSTRUCT_MASS` and `DENSITY` extend P5 quantity annotations. They do not
   retroactively type historical `Number` nodes or add automatic conversions.
8. P7/KubeJS may declare profiles, tags, overrides, and coefficients through
   validated data. It may not supply callbacks, predicates, impact handlers,
   or direct entity/world mutation.
9. P9 may consume physical-profile outputs only through bounded temporary
   plans. Permanent attributes, terrain effects, and material transmutation
   need separate contracts.
10. Any future physical damage or terrain interaction requires a new explicit
    transaction, protection, rollback, and dedicated-server contract.

## Delivery Slices And Model Assignment

### P11-Sol: Authority, Data, And Migration Contract

- Completed: profile records, resolver precedence, snapshot/reload behavior,
  numerical caps, compatibility, P8/P5/P7/P9 retroactive rules, stable
  diagnostics, and the public datapack/KubeJS declarative surfaces are frozen.
- **Model:** Sol.

### P11-Terra High: Semantics And Counterexamples

- Completed in `docs/P11_PHYSICS_SEMANTIC_REVIEW.md`: sampled-union canonical
  collision volume, canonical/contextual shape split, mass-weighted
  center/inertia tensor, compression identities, legacy launch boundary, and
  counterexamples for slabs, stairs, fences, doors, overlap, unknown blocks,
  extreme resistance, mixed constructs, and reload races.
- **Model:** Terra with High effort.

### P11-Terra: Pure Core And Data Snapshot

- Implemented the Minecraft-free `com.mathmod.physics` core: sampled canonical
  shape volume, validated policy/declarations, source precedence, bounded
  fallback, immutable declarations, snapshot-local LRU cache, weighted center,
  tensor, and compression aggregate. `DerivedPhysicsCoreTest` covers pure
  volume, precedence, ambiguity, fallback, permutation, gamma, and zero-mass
  behavior before any Minecraft-world effect uses the new values.
- **Model:** Terra with Medium effort.

### P11-Terra: Runtime Integration And Server Verification

- Implemented the server-only canonical `BlockState` adapter and one atomic
  NeoForge reload listener for profile/policy data. It retains the old snapshot
  after a parse, binding, or publication failure and starts a fresh cache only
  after successful publication. This first runtime integration intentionally
  does not alter legacy `launch_construct` knockback.
- Implemented launch-time capture: each new construct flight stores the
  resolved material and aggregated physical profile with its snapshot version.
  Reloads therefore cannot alter a flight already in progress, and legacy
  payment, `massEquivalent`, collision, and knockback remain untouched.
- Completed Luna surface: bilingual Patchouli teaching, six starter material
  declarations plus the default policy, preview-matrix coverage, and a
  resource-panel line that labels physical readings as estimates resolved
  again by the server at launch.
- Remaining future hardening: broaden the dedicated-server coverage to
  reload/flight/collision/unloaded-chunk cases.
  The reusable `empty.nbt` GameTest fixture is generated as part of the build;
  the first dedicated test validates canonical adaptation and the published
  fallback snapshot.
- Preserve P8 payment/transaction behavior; expose no client-selected mass.
- Add dedicated-server tests for reload, fallback, cache, launch, collision,
  unloaded chunks, and optional claim integration.
- **Model:** Terra with Medium effort.

### P11-Luna: Teaching, Data Examples, And Previews

- Completed bilingual Patchouli material-physics pages, profile examples,
  preview-matrix coverage, and resource-panel copy distinguishing estimates
  from exact server resolution.
- Completed starter declarations for wool, wood, stone, glass, common metal,
  and obsidian; unknown blocks continue using the documented fallback.
- **Model:** Luna.

## Acceptance Criteria

1. The same `BlockState` and snapshot always resolve to the same finite profile.
2. Explicit data wins over tags and heuristics; invalid data is rejected before
   snapshot publication.
3. Cached profile lookup does not repeatedly recompute static shape volume.
4. Slabs and full blocks of the same material produce distinguishable volumes
   and bounded masses.
5. A mixed construct has a mass-weighted center and inertia independent of
   source ordering.
6. P8 saved talismans retain exact payment, `massEquivalent`, and executable
   behavior under the default `gamma = 0` policy.
7. Clients cannot choose physical mass, density, inertia, or impact values.
8. Unknown modded blocks receive a stable fallback and do not crash planning.
9. Reload changes apply atomically to future casts without mutating saved data.
10. Construct effects remain bounded and do not force-load chunks or modify
    terrain.

## Explicit Deferrals

- real-world unit conversion;
- arbitrary material callbacks or JavaScript predicates;
- fluid, block-entity, multipart, or placement-callback mass simulation;
- terrain destruction, block breakage, explosions, and penetration;
- continuous rigid-body simulation, gyroscopic precession, and fragmentation;
- permanent physical mutation of blocks or entities.
