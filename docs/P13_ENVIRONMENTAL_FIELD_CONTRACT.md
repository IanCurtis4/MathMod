# P13 Environmental Correspondence Field Contract

Status: Sol architecture, Terra High semantic review, and Terra Medium runtime
completed on 2026-07-22. Built-in static providers, persistent world-secret
storage, golden-noise tests, declarative datapack publication, generation
capture, typed projection, and Dimensional Survey now run server-side. Luna
content/evidence remains. The mathematical review is
`docs/P13_ENVIRONMENTAL_FIELD_SEMANTIC_REVIEW.md`.

## Purpose

P13 introduces a deterministic, server-owned field of environmental
correspondences over loaded world positions. It gives later mathematics an
inspectable world input without adding mana, passive income, arbitrary world
scans, or permission to mutate entities or terrain.

The first slice is observational. A sampled value may select a branch, produce
a report, or drive a bounded redstone signal. It cannot pay item costs, replace
a witness or catalyst, alter escrow, grant knowledge, damage an entity, or
authorize a block change.

## Mathematical Objects

For dimension `d`, loaded position `p`, active snapshot `s`, and channel set
`C_s`, the correspondence field is:

```text
A_s,d(p) in R^|C_s|

A_s,d(p)[c] = clamp_c(
    dimension_s(d, c)
  + biome_s(biomeAt(d, p), c)
  + height_s(d, normalizedHeight(d, p), c)
  + noise_s(worldSecret, worldSeed, d, p, c)
)
```

`A_s,d(p)` is an **attribute-space vector**. Its coordinates are named channels
such as `mathmod:spatial`, `mathmod:stability`, `mathmod:vitality`, and
`mathmod:decay`. It is not a direction, velocity, force, or Minecraft `Vec3`.
Changing channel order never changes channel identity.

The typed boundary is:

```text
AttributeField              Position -> AttributeVector[channelId -> Number]
project(AttributeField, c)  Position -> ScalarField[CORRESPONDENCE]
gradient(ScalarField, p, h)             -> Vec3[CORRESPONDENCE / BLOCK]
VectorField[Q]              Position -> Vec3[Q]
```

`AttributeField`, `ScalarField`, and `VectorField` are distinct graph values.
Projection is explicit. A gradient is a spatial vector derived from one scalar
channel; the complete attribute vector cannot be silently coerced to `Vec3`.
P13 may establish the `VectorField` type and provider interfaces, but its first
gameplay slice does not attach force or movement semantics. P15 owns those
effects.

## Coordinate And Sampling Convention

- Positions use Minecraft dimension-local block coordinates in the X/Y/Z
  basis.
- A block sample is evaluated at its center `(x + 0.5, y + 0.5, z + 0.5)`.
- Height normalization is
  `(sampleY - minBuildHeight) / max(1, logicalHeight - 1)` and is clamped to
  `[0, 1]`.
- Biome lookup uses the server's biome holder at the sampled position.
- Dimension and biome selectors use canonical registry ids and validated tags.
- A calculus operation uses the fixed positions in its P5 `SamplePlan`; the
  provider cannot move, skip, or add samples after cost validation.
- Any unloaded, out-of-bounds, over-radius, or unavailable sample fails the
  complete operation before resource consumption.

The static P13-A field does not depend on game time, weather, moon phase,
redstone, nearby entities, mutable blocks, player identity, or inventory.
Those are potential dynamic P13-B layers and require separate cache and
snapshot rules before implementation.

## Server Authority And Privacy

The server owns definitions, registry resolution, noise derivation, sample
positions, sample counts, results, cost, report data, and redstone output. A
client may request an allowed theorem action or inspector page; it cannot send
an accepted field value, channel coefficient, noise coordinate, sample count,
or result.

Seed-derived noise uses both the world seed and a private 256-bit
`fieldSecret` generated once per world and stored in server `SavedData`.
The secret is included in backups and world copies, is never synchronized, and
is not available to runes, KubeJS, commands below permission level 2, logs, or
player-facing diagnostics.

This makes the field stable for the lifetime of a world without publishing a
simple reversible hash of its seed. It does not claim cryptographic secrecy
against an administrator with world-file access. If legacy world data has no
secret, the server creates one before publishing P13 providers. If stored data
is malformed or lost, P13 fails closed until an administrator explicitly
regenerates it; silent regeneration would move every noisy correspondence.

Clients receive only bounded theorem reports. Ordinary reports identify the
channel, qualitative contributing layers, quantity, sample count, estimate
status, and quantized result needed by the UI. They never include raw seed,
secret, internal hash state, noise-lattice values, or enough hidden-layer data
to reconstruct the configured noise source directly.

## Data Schema

P13-A publishes one immutable `EnvironmentalFieldSnapshot` containing:

```text
schemaVersion
generation
channels: ChannelDefinition[]
dimensions: DimensionFieldDefinition[]
biomeLayers: BiomeFieldDefinition[]
heightCurves: HeightCurveDefinition[]
noisePolicy: NoisePolicy
diagnostics: FieldDiagnostic[]
```

Recommended Java ownership boundaries:

```text
EnvironmentalFieldSnapshotStore
EnvironmentalFieldSnapshotBuilder
EnvironmentalFieldReloadListener
WorldFieldSecretData
EnvironmentalFieldProvider
EnvironmentalSampleContext
EnvironmentalSampleReport
```

Definitions are declarative records. No record contains a Java callback,
script function, mutable world reference, registry holder retained across
reload, or effect executor.

### Schema-One Datapack Files

Files live under `data/<namespace>/mathmod/environment/`:

```text
channels/*.json      { schema_version, id, minimum, maximum, noise_amplitude, noise_scale, report_scale }
dimensions/*.json    { schema_version, dimension, values: { channelId: number } }
biomes/*.json        { schema_version, biome, overrides: { channelId: number }, additives: { channelId: number } }
height_curves/*.json { schema_version, dimension, channel, points: [{ x, y }, ...] }
aliases/*.json       { schema_version, from, to }
```

`schema_version` is `1`. The listener accepts only bounded data records: no
script sampler, arbitrary noise function, world access, or effect callback can
be introduced through a datapack. A malformed local record is rejected with a
server diagnostic; an invalid complete candidate retains the previous
publication.

### Projection And Inspection

The authoring boundary is explicit:

```text
Environmental Correspondence -> Attribute Field
Attribute Field + channel constant -> Project Correspondence Channel -> Scalar Field
Scalar Field + point + step -> Field Gradient -> Vec3
```

An `Attribute Field` cannot connect to a scalar or `Vec3` input. The projection
node requires a namespaced `channel` constant and resolves aliases server-side.
Each published channel receives a bounded scalar provider; no datapack supplies
the sampler implementation.

After a successful Dimensional Survey, sneak-use the Rune Anchor to inspect its
persisted report. It contains only generation, fixed sample count, redstone
signal, dominant channel, and qualitative `quiet`/`variable`/`intense` readings.
It never stores or displays raw samples, seed, secret, lattice values, or layer
coefficients.

### Schema-One Bounds

| Element | Bound |
| --- | ---: |
| active channels | 32 |
| dimension definitions | 64 |
| biome layer definitions | 256 |
| height curves | 64 |
| points per height curve | 8 |
| channel contributions per definition | 16 |
| aliases | 256 |
| absolute layer coefficient | 16 |
| final channel range | `[-16, 16]` |
| spatial noise scales | 4 predefined bands |
| provider samples per operation | inherited P5 cap |

Every channel declares a stable namespaced id, localized display key, clamp,
default value, and closed quantity `CORRESPONDENCE`. Schema one does not allow
pack-defined physical units. Coefficients and curve points must be finite.
Curve X coordinates are strictly increasing in `[0, 1]`; interpolation is
piecewise linear and clamped outside the authored domain.

Biome definitions select exact biome ids or biome tags. Conflicting exact
selectors in the same source layer are rejected. Matching tags combine only
when each record explicitly declares additive composition; otherwise the
highest-precedence match wins. Terra High must freeze the remaining
composition counterexamples before codecs are implemented.

## Noise Contract

Schema one accepts only named built-in algorithms. The initial algorithm id is
`mathmod:salted_value_v1`; KubeJS and datapacks may choose its predefined
spatial band and bounded amplitude but cannot supply an implementation,
octave loop, seed salt, or arbitrary function.

The algorithm key is derived server-side from:

```text
K = keyedHash(fieldSecret, worldSeed, dimensionId, channelId, algorithmId)
```

Lattice coordinates are integer and overflow-stable. Interpolation and output
clamping must be specified with golden vectors before Terra implementation.
Changing the algorithm or its numeric behavior requires a new id. Existing ids
are never silently reinterpreted by a mod update.

## Source Precedence And Publication

The source order is:

```text
built-in < KubeJS startup < datapack pack order
```

Within one source layer, records use explicit non-negative priority followed by
stable source id as the deterministic tie-breaker. Filesystem enumeration order
is never authority.

The builder decodes all candidates, resolves current registry ids and tags on
the server game thread, validates cross-references and caps, flattens aliases,
and creates one candidate snapshot. Publication is one atomic reference swap.
A fatal candidate failure retains the previous complete generation; runtime
readers never observe a half-published channel or layer.

Local malformed records are omitted with diagnostics when omission leaves a
coherent snapshot. Global failures include invalid noise policy, duplicate
canonical channel ownership, exceeded global caps, alias cycles, and a snapshot
with no valid channels. Bootstrap publishes a small built-in safe snapshot only
after the world secret is available.

## Migration And Persistence

Field definitions are reloadable data, not item persistence. Talismans and
anchors store stable provider/channel ids and graph parameters, never sampled
values, coefficients, registry holders, or an entire field snapshot.

Aliases migrate references during server validation:

```text
old channel/provider id -> canonical current id
```

Alias chains are flattened at publication, capped at 16 hops, and reject
cycles, missing targets, or aliases shadowing canonical ids. A validated saved
graph may be re-saved with canonical ids, but reload never rewrites player
inventories or chunks in bulk.

An execution captures the snapshot generation at preflight. All samples,
costs, reports, and effects for that execution use the captured generation.
Reload affects only future executions. Multi-tick P13-B jobs must persist their
captured algorithm id and generation or cancel safely; schema one creates no
such job.

The world secret uses its own persistence version. Definition schema migration
must never rotate or derive a replacement secret. Removing a channel makes a
dependent graph unavailable with a stable diagnostic; it does not substitute a
different channel by presentation order.

## P5 Compatibility

P13 extends P5 through named providers and existing planning, not through a
second sampling engine.

- Channel projection publishes a P5-compatible `ScalarField` provider.
- Centered gradients use the existing six-position P5 plan and per-cast cache.
- Cache keys include snapshot generation, dimension id, exact sample position,
  provider id, and channel id.
- Cache lifetime is one execution; P13-A adds no global sampled-value cache.
- Provider cost is declared in the snapshot and contributes monotonically to
  the server-built `SamplePlan`.
- Non-finite provider output, missing channels, and generation mismatches fail
  before payment or effect execution.
- Client previews may display the server plan but never reduce its sample count
  or cost.

P13 does not change the semantics of `living_density_field`, historical scalar
numbers, existing gradients, or saved P5 graphs. It adds provider ids and types
without reinterpreting old graph nodes.

## KubeJS Boundary

KubeJS startup declarations may add or replace bounded declarative records:

- channel display metadata and clamps;
- dimension and biome coefficients;
- piecewise-linear height curves;
- selection of a built-in noise algorithm and spatial band;
- aliases and source priority.

KubeJS cannot receive the world seed or secret, sample a world, register a
callback sampler, inspect players or inventories, execute an effect, mutate a
snapshot after startup freeze, bypass caps, or convert an ambient value into
items or payment. Datapack reload may replace data records; KubeJS remains a
startup source generation until a later explicit reload contract exists.

## Failure And Diagnostics

Stable server diagnostic categories begin with:

```text
unknown_channel
unknown_provider
invalid_definition
invalid_curve
invalid_noise_policy
snapshot_unavailable
generation_mismatch
sample_unloaded
sample_out_of_bounds
sample_out_of_radius
sample_budget_exceeded
non_finite_sample
world_secret_unavailable
```

Diagnostics include severity, stable code, record id, source coordinate, and a
bounded technical message. Player text uses localized categories and a useful
next action. It must not reveal claim details, secret material, pack filesystem
paths, hidden coefficients, or internal exception text.

No failure may force-load a chunk, consume resources, partially update an
anchor, publish part of a snapshot, or return a fabricated zero. Zero is a
valid sample only when the provider actually evaluates to zero.

## First Theorem: Dimensional Survey

The first theorem is anchor-only and non-mutating. It selects three configured
channels, projects each to a scalar field, evaluates centered gradient
magnitude at the anchor, and produces:

- a read-only report naming the dominant local gradient;
- exact planned sample count and estimate labels;
- a bounded `0..15` redstone signal derived from a declared clamp schedule.

The three gradients require at most 18 provider samples before cache reuse.
The server owns channel availability, sample positions, cost, dominant-channel
selection, and signal. A failed sample leaves the previous anchor signal and
all resources unchanged. The theorem does not reveal the noise decomposition,
scan entities, move anything, or run continuously.

Terra High must freeze the signal formula, ties, gradient units, and flat-field
behavior. Terra Medium then implements the provider snapshot, runes, inspector,
anchor theorem, and GameTests.

## Compatibility And Non-Claims

- P13 adds no mana bar, regeneration loop, cooldown, or free-cast region.
- Ambient values cannot satisfy fixed items, attributes, catalysts, or escrow.
- Existing talismans execute under their existing graph and cost contracts.
- P13 grants no block placement, breaking, replacement, drops, or chunk ticket.
- Attribute gradients are not automatically gravity, magnetism, velocity,
  acceleration, or force.
- Similar values do not prove biome identity or expose a world seed.
- P13-A is static for one snapshot and world secret, but it does not promise
  continuity across biome boundaries or datapack reload.
- P14 may observe P13 reports but receives no mutation permission from them.
- P15 may consume projected scalar fields and explicit spatial vector fields
  only through separately capped effect plans.

## Delivery Slices

### Sol: Architecture (Completed)

- Freeze objects, authority, schemas, precedence, publication, persistence,
  privacy, migration, P5 compatibility, KubeJS limits, and the first theorem
  boundary.

### Terra High: Mathematical Review (Completed)

- Frozen in `docs/P13_ENVIRONMENTAL_FIELD_SEMANTIC_REVIEW.md`: layer
  composition, continuity boundaries, finite-difference quantities, signal and
  tie formulas, `salted_value_v1` byte encoding and golden vectors, clamps,
  and adversarial counterexamples.

### Terra Medium: Runtime

- Implemented first runtime slice: persistent secret `SavedData`, immutable
  built-in environmental snapshot, three P5 scalar providers, channel sampling,
  frozen `salted_value_v1` vectors, and anchor-only Dimensional Survey with a
  GameTest.
- Implemented second runtime slice: JSON datapack reload publication for
  channels, dimensions, exact-biome values, curves, and aliases; aliases are
  flattened at publication and each execution captures one generation.
- Completed final runtime slice: typed attribute-field projection, dynamic
  scalar providers for published channels, and a persisted player-safe anchor
  inspection report.

### Luna: Content And Evidence (Completed)

- Added starter `mathmod:resonance` channel data for overworld, Nether,
  plains, Deep Dark, and normalized height.
- Added bilingual Patchouli teaching for correspondence layers, projection,
  anchor inspection, privacy, and experimental status.
- Added a dedicated resonance glyph and registered compact/standard preview
  targets for the new entry.
- Marked the content as experimental in the contract; no claim of universal
  physical law is made by the starter values.

## Acceptance Matrix

| ID | Requirement |
| --- | --- |
| P13-01 | Same world secret, seed, snapshot, dimension, channel, and position produce bit-stable golden results. |
| P13-02 | Different channel or dimension keys do not accidentally reuse noise streams. |
| P13-03 | Reload publishes atomically; an active execution keeps its captured generation. |
| P13-04 | Missing, unloaded, out-of-radius, and over-budget samples fail before payment and do not force-load chunks. |
| P13-05 | Client-provided values, counts, channels, and coefficients cannot influence server acceptance. |
| P13-06 | Reports and synchronization contain no raw seed, world secret, hash key, or hidden lattice data. |
| P13-07 | Attribute vectors cannot connect directly to `Vec3` inputs; explicit projection/gradient is required. |
| P13-08 | KubeJS and datapacks cannot install executable sampler or effect callbacks. |
| P13-09 | Ambient values never replace item witnesses, catalysts, attributes, or escrow. |
| P13-10 | Dimensional Survey is bounded, anchor-only, non-mutating, and preserves resources/output on failed preflight. |

P13 remains `experimental` until its runtime GameTests, dedicated-server
reload/reconnect smoke, bilingual previews, and player-facing inspector checks
pass under the P12 evidence vocabulary.
