# P8 Constructive Regions And Transaction Contract

Status: Sol safety/planning architecture, Terra High geometry semantics, Terra
A region planning, and Terra B bounded fill runtime completed on 2026-07-22.
Dedicated-server fill acceptance, construct motion, and Luna teaching/preview
assets remain.

## Purpose

P8 turns a `Region` from a reusable spatial predicate into a source for bounded,
item-backed construction. It also introduces the first transient material
construct: a sampled solid may be compressed while retaining its abstract
mass-equivalent, given angular motion, and launched.

The governing separation is:

```text
Region                 pure geometric meaning
CandidatePlan          finite deterministic lattice enumeration
FillPlan               proposed block-state changes plus exact item payment
ConstructBody          transient material body plus conserved mass metadata
EffectPlan             explicit world mutation or entity-spawn boundary
```

A region never reads inventory, loads chunks, changes blocks, or spawns an
entity. Sampling a region never implies permission to fill it. A preview never
authorizes a cast.

## First-Slice Non-Goals

P8 does not add excavation, arbitrary block replacement, block-entity copying,
NBT-bearing material palettes, fluid placement, chunk tickets, persistent
flying structures, terrain damage, or delayed construction at projectile
impact. It does not interpret JavaScript as a region predicate.

The first projectile impact is a bounded kinetic entity effect. It cannot
break or place blocks. Settling or expanding a construct at impact requires a
later transaction design because permissions and world state may change while
the projectile is in flight.

## Architectural Layers

### 1. Pure Region Description

`Region` remains the public typed value. Existing sphere and box records retain
their ids and behavior. P8 adds bounded descriptor nodes rather than embedding
Java lambdas:

```text
union(Region, Region)                       -> Region
intersection(Region, Region)                -> Region
difference(Region, Region)                  -> Region
implicitRegion(Bounds, ClosedPurePredicate) -> Region
solidOfRevolution(Profile, AxisFrame)       -> Region
```

Every descriptor carries finite bounds. Complement without finite outer bounds
is forbidden. Region descriptors are immutable and have maximum depth 8 and
maximum 16 primitive leaves. Closed predicates use the P4 pure authoring
language under its existing step limits; they cannot contain observations,
inventory reads, random values, effects, or KubeJS callbacks.

The exact boundary convention, profile domain, axis frame, floating-point
tolerance, Boolean-law counterexamples, center of mass, and inertia are frozen
in `docs/P8_GEOMETRY_SEMANTIC_REVIEW.md` before a voxelizer is implemented.

### 2. Candidate Planning

`CandidatePlanner` converts one region into a finite immutable set of block
positions. The server owns the origin, frame, bounds, resolution, and limits.
The client may request a preview but may not submit positions or counts.

Initial hard limits:

| Limit | Value |
| --- | ---: |
| Region descriptor depth | 8 |
| Primitive leaves | 16 |
| Bounding-box lattice visits | 4,096 |
| Accepted candidate positions | 256 |
| Fill mutations per cast | 128 |
| Construct source voxels | 128 |
| Loaded-chunk radius from cast origin | 16 blocks |

Enumeration uses block centers, deduplicates positions, and emits one stable
server-defined order. It aborts as soon as any cap is exceeded. It never
truncates a shape into a smaller apparently valid shape.

Every candidate must be inside build height, world border, the declared cast
radius, and an already loaded chunk. One invalid position rejects the whole
plan. There is no chunk force-loading and no skip-unloaded mode.

### 3. Material Resolution

The player selects an exact registered block item through the existing resource
preparation flow. Tags may satisfy abstract rune attributes, but a tag alone
cannot choose the block state that will appear in the world. The server resolves
the selected item to one allowlisted simple block state.

The first slice rejects materials that require block-entity data, carry custom
components that affect placement, represent fluids, or depend on arbitrary
placement callbacks. KubeJS may later register declarative material metadata,
but never a placement or collision callback.

One changed block consumes one matching block item. Candidate positions already
containing the exact target state are satisfied without mutation or payment.
Every other candidate must be safely replaceable under the initial
`EMPTY_ONLY` policy or the whole plan fails. Mining, drops, and replacement of
solid terrain are separate future policies.

## Planning Records

The common-side implementation should use immutable records with no client
imports:

```text
RegionDescriptor
CandidatePlan(origin, bounds, orderedPositions, visitCount, fingerprint)
BlockMaterial(itemId, blockStateId, massUnitsPerItem)
BlockChange(position, expectedState, targetState)
FillPlan(candidatePlan, material, changes, satisfied, requirements, fingerprint)
ConstructBody(shape, material, sourceVoxels, massEquivalent,
              centerOfMass, inertia, visualScale, spin)
ConstructionIssue(code, optionalPosition)
```

Fingerprints are diagnostics and stale-plan guards, not security tokens. They
include the program identity, dimension, origin/frame, ordered candidates,
expected states, target state, and active material-definition generation.

Planning issue codes are stable and bounded. Player-facing components localize
them; they do not expose claim-owner names or other server-only policy details.

## Static And Dynamic Costs

P8 adds a cast-time cost layer without weakening the existing inscription
plan:

```text
static cost  = rune budget + tier + abstract witnesses/catalysts
dynamic cost = exact changed blocks + bounded planning work + motion work
```

The Resources screen may show an upper bound before a target world position is
known. An anchor-local or cast preview may show an exact current estimate, but
execution always recomputes the plan on the server.

For fills:

```text
required block items = number of BlockChange records
planning surcharge   = base + latticeVisits + candidateCount
```

Metamagic may discount abstract attribute payment under the existing snapshot
rules. It cannot discount exact block items, increase caps, change a material,
or alter a prepared candidate plan.

## Fill Transaction

Block construction does not run inside an ordinary effect callback. It uses a
dedicated server coordinator with the following states:

```text
PLANNED -> REVALIDATED -> ESCROWED -> COMMITTING -> COMMITTED
                    \-> REJECTED
                                  \-> ROLLED_BACK
                                  \-> FAILED_CLOSED
```

### Preflight

1. Decode and validate the authoritative graph.
2. Evaluate only the pure region/material inputs needed for planning.
3. Enumerate candidates and resolve exact block changes.
4. Check loaded chunks, world border, build height, replaceability, and the
   active protection adapter for every position.
5. Calculate static and dynamic requirements from the completed plan.
6. Verify inventory without consuming it.

No world or inventory mutation occurs during preflight.

### Revalidation And Escrow

Immediately before commit, the coordinator rechecks the dimension, origin,
program/material generations, expected block states, permissions, and exact
inventory stacks. Any mismatch makes the plan stale and restarts planning at
most once; a second mismatch fails the cast.

Required consumed items are then removed into an in-memory escrow record.
Catalysts are verified but not escrowed. Escrow is associated with one cast and
cannot be reused by another effect plan.

### Commit And Rollback

Changes apply in deterministic order during one server task. Neighbor updates
and success audiovisuals occur only after all placements succeed. If a
placement fails, MathMod restores changed positions in reverse order before
returning escrowed items.

The first slice admits only simple target and previous states so rollback does
not need to reconstruct block entities or fluid schedules. If restoration
cannot reproduce every original state, the transaction enters `FAILED_CLOSED`:
it logs a bounded administrative diagnostic and does not automatically refund
items, preventing item duplication against surviving world changes.

This is atomic against validation failures and handled runtime failures inside
one running server. It does not claim crash atomicity across a process or
machine failure. A persistent write-ahead journal is required before larger,
multi-tick, or delayed construction transactions are allowed.

## Protection Adapter

Construction permission is a common-side service, not a client flag. The
default policy requires loaded chunks, world/build limits, and ordinary player
interaction permission. Optional claim integrations may contribute fail-closed
adapters. A missing optional integration must not silently claim compatibility.

The same permission policy runs in planning and revalidation. A preview may
report `protected_position` but must not identify owners or reveal private claim
metadata.

## Conserved Construct Bodies

A materialized transient body records source voxel count and abstract
mass-equivalent before compression:

```text
massEquivalent = sourceVoxels * massUnitsPerItem
```

`massUnitsPerItem` is bounded declarative material metadata. The vanilla
fallback is 1. Pack values are integers in `1..16`; total mass-equivalent is
capped at 2,048. This quantity is a MathMod balance/physics descriptor, not a
claim that Minecraft blocks have real-world kilograms.

Compression is a homothety about the center of mass:

```text
p' = center + scale * (p - center),  0.25 <= scale <= 1
massEquivalent' = massEquivalent
inertiaAboutAxis' = scale^2 * inertiaAboutAxis
```

Compression happens after materialization. It does not reduce item payment,
refund duplicate visual voxels, or permit expansion above scale 1 in the first
slice. The visual/collision body may become smaller while its mass-equivalent
remains unchanged.

Spin uses a finite normalized axis and angular speed in radians per tick.
Launch uses a finite linear velocity in blocks per tick. Initial limits are:

| Property | Limit |
| --- | ---: |
| Scale | `0.25..1.0` |
| Angular speed | `pi / 4` rad/tick |
| Launch speed | `2.0` blocks/tick |
| Lifetime | 100 ticks |
| Impact targets | 8 living entities |
| Concurrent construct per owner | 1 |

The transient entity creates no chunk ticket. It uses swept collision rather
than trusting one endpoint per tick. Unloaded travel stops the construct. The
first impact effect is clamped knockback derived from mass-equivalent and
linear speed; rotational motion affects presentation and a bounded surcharge,
not terrain damage or an unbounded damage multiplier.

The implementation may use physically recognizable planning terms:

```text
linear work proxy     = 0.5 * massEquivalent * |velocity|^2
rotational work proxy = 0.5 * inertiaAboutAxis * omega^2
compression surcharge = massEquivalent * (1 / scale^3 - 1)
```

These values are clamped and converted to integer resource requirements by a
documented monotonic schedule. They are balance proxies, not SI joules.

## P8 Throughline: Cavalieri Projectile

The final preset working name is **Cavalieri Projectile** / **Projétil de
Cavalieri**. Its intended graph is visibly compositional:

```text
profile = bounded_profile(f, lower, upper)
solid   = solid_of_revolution(profile, player_or_anchor_axis)
body    = materialize_construct(solid, prepared_block_material)
small   = compress_construct(body, scale)
spun    = spin_construct(small, axis, angular_speed)
plan    = launch_construct(spun, origin, launch_velocity)
result  = launch_construct(spun, origin, launch_velocity)
```

No node named `cavalieri_projectile` may hide the implementation. The preset is
a saved theorem assembled from reusable runes.

The name refers to constructing and comparing solids by their cross-sections;
it does not claim Cavalieri's principle proves mass conservation under magical
compression. The manuscript must distinguish the geometric theorem from the
mod's explicit conservation rule.

## Delivery Slices

### P8-Sol: Safety And Transaction Architecture (This Document)

- Freeze the pure-region/candidate/fill/body/effect boundaries.
- Freeze server ownership, caps, dynamic payment, escrow, rollback, stale-plan,
  protection, chunk, and crash-honesty policies.
- Define the Cavalieri Projectile throughline and forbid an opaque preset rune.

### P8-Terra High: Geometry Semantics

- Completed in `docs/P8_GEOMETRY_SEMANTIC_REVIEW.md`: Boolean boundary laws,
  finite implicit predicates, axis frames, radial-band profiles, solids of
  revolution, voxel-center inclusion, tolerances, center of mass, scalar
  inertia, quantity annotations, and counterexamples for touching boundaries,
  hollow profiles, reversed axes, discontinuities, degenerate intervals, and
  compression aliasing.

### P8-Terra A: Pure Regions And Candidate Planner

- Implemented immutable Boolean and constant radial-band revolution regions,
  deterministic voxel-center candidates, explicit visit/candidate rejection,
  and pure boundary/order tests. The pure geometry has no Minecraft runtime
  dependency; the existing runtime adapter exposes the new region values.
- The current revolution rune represents the bounded constant-profile case.
  General closed P4 profile functions and the guided profile preview remain
  with the next authoring/UI pass.
- No item selection, inventory access, permission query, block mutation, or
  projectile spawning occurs in this slice.

### P8-Terra B: Item-Counted Fill Plans

- Implemented the terminal `fill_region` rune with an exact `BlockItem` id,
  `EMPTY_ONLY` planning, center-derived candidates, loaded-chunk/world-border
  checks, ordinary interaction permission, item escrow, commit-state
  revalidation, reverse rollback, and delayed neighbor updates.
- The selected fill item is intentionally a node constant in this first runtime
  slice; tags, NBT, fluids, block entities, and placement callbacks are
  rejected. A Resources-screen picker is a later UI slice over the same exact
  id contract.
- A consumed abstract cost may not match the selected fill item, preventing the
  ordinary cost path from competing with escrow. Dedicated-server and claim-mod
  GameTests remain required before the fill is survival-ready.
- Anchor-local fills remain deferred: an anchor has no player inventory from
  which exact block items can be escrowed.

### P8-Terra C: Compression And Motion

- Implemented transient `ConstructBody` data with immutable source voxels,
  mass-equivalent, center of mass, spin-axis inertia, and a compression range
  from `0.25` to `1.0` that preserves the source item count.
- Implemented a server-owned transient flight, rather than a persistent
  Minecraft entity: it consumes exact block items at launch, allows one flight
  per owner, expires after 100 ticks, uses swept block/entity collision, caps
  target count and impulse, emits block particles, neither force-loads chunks
  nor changes terrain, and is discarded on an unloaded chunk.
- Added the reusable `materialize_construct`, `compress_construct`,
  `spin_construct`, and `launch_construct` runes plus the executable Cavalieri
  Projectile theorem. Its material is exact `minecraft:stone` in this first
  preset; authored material picking remains a Luna UI concern.

### P8-Luna: Teaching, Assets, And Previews

- Implemented bilingual names, Patchouli pages, formulas, material-selection
  guidance, construct icons, candidate/cost preview data, and resource-panel
  preview rendering for construct proofs.
- The preview exposes material id, scale, mass/candidate cap, angular speed,
  launch cap, lifetime cap, and server authority. It deliberately presents
  these as policy bounds rather than pretending to know the exact runtime
  voxel count before the server plans the cast.
- Taught why geometric volume, item count, mass-equivalent, visual scale, and
  work surcharge are related but not interchangeable.
- Remaining Luna follow-up: a visual 3D/entity presentation and an inventory or
  JEI-backed material picker that replaces the exact material constant safely.

## Acceptance Matrix

1. Boolean and revolution geometry pass pure law and boundary tests.
2. Candidate order/count is deterministic and over-cap shapes fail wholly.
3. Client-supplied positions, counts, block states, mass, and motion are ignored.
4. Unloaded, protected, stale, fluid, or block-entity positions consume nothing.
5. Insufficient exact block items change nothing.
6. Mid-commit failure restores all admitted simple states and refunds escrow.
7. Compression retains source item count and mass-equivalent at every scale.
8. Projectile speed, spin, lifetime, targets, and concurrency are server-clamped.
9. Projectile travel neither force-loads chunks nor mutates terrain.
10. The final preset contains ordinary revolution, materialization,
    compression, spin, and launch nodes.
11. Resource and inspector views show upper-bound versus exact cast cost.
12. Dedicated-server tests cover planning, rollback, spoofed payloads, and
    projectile unload/collision behavior before P8 is survival-ready.

## Explicit Deferrals

- excavation, drops, transmutation, arbitrary replacement predicates;
- block entities, fluids, multipart blocks, custom placement callbacks;
- multi-material palettes, gradients, schematics, and copied structures;
- persistent or multi-tick fills and crash-recovery journals;
- expansion, placement, explosion, or terrain damage at projectile impact;
- physical damage derived directly from pack-defined mass metadata;
- multiple projectiles per owner, autonomous anchor turrets, and chunk tickets;
- arbitrary JavaScript predicates, samplers, placement hooks, or impact code.
