# P5 Fields And Bounded Calculus Contract

Status: Terra High mathematical-definition pass, Sol planning architecture,
and the first Terra runtime implemented on 2026-07-22. The executable surface
currently contains a scalar living-density field and centered gradient; vector
fields, divergence, curl, and general field integration remain deferred.

## Purpose And Boundary

P5 introduces **bounded numerical fields**, not symbolic calculus and not an
unrestricted simulation system. A field is an inspectable typed rule evaluated
at a supplied position or scalar parameter. It is never an implicit scan of the
loaded world and never force-loads a chunk.

The existing `finite_difference` and `simpson_integral` runes remain explicit
sample arithmetic. They are intentionally distinct from the P5 operators:
P5 applies a validated function or field at server-chosen sample points.

The first release supports only closed, pure functions constructed from P4
sources and explicit field observations. Effects, effect plans, entity mutation,
inventory reads, random values, time, and arbitrary KubeJS callbacks are not
valid function bodies. Observations are permitted only through named field
providers with a declared sampling contract.

## Types

The authoring language gains the following type expressions. They compile away
before ordinary talisman execution; they do not become runtime closures.

```text
ScalarField[Q]       -- position in block coordinates -> scalar carrying quantity Q
VectorField[Q]       -- position in block coordinates -> vector carrying quantity Q
Function[Number, Number]
```

The executable graph initially represents sampled results as existing
`Number` and `Vec3` values. `ScalarField` and `VectorField` are authoring and
planning types until a bounded sample is lowered. A field value cannot be sent
directly to an effect input, stored in anchor state, or returned as a talisman
result.

## Quantity Metadata

Minecraft's existing `Number` has no physical dimension. P5 therefore uses
**quantity metadata on field signatures and calculus operators**, rather than
migrating every old number graph to dimensional types. Metadata is checked at
authoring and server validation time and is not guessed from a numeric literal.

| Quantity | Physical reading | Unit in MathMod |
| --- | --- | --- |
| `SCALAR` | Dimensionless intensity, fraction, or pack-defined signal | 1 |
| `BLOCK` | Spatial length | block |
| `TICK` | Server time | tick |
| `BLOCK_PER_TICK` | Velocity | block/tick |
| `SIGNAL` | Redstone-like bounded intensity | 0..15 |
| `COUNT_PER_BLOCK` | Spatial density | entities/block^3 |

This metadata is deliberately a small closed vocabulary in the first slice.
Pack-defined quantity names, automatic conversion, seconds, degrees, mass, and
force are deferred. Radians remain the documented interpretation of existing
trigonometric `Number` inputs; P5 does not reinterpret historical numbers.

### Operator Quantity Rules

```text
derivative(Function[Number, Number], Number, step) -> Number
gradient(ScalarField[Q], Position, step)           -> Vec3[Q / BLOCK]
divergence(VectorField[Q], Position, step)         -> Number[Q / BLOCK]
curl(VectorField[Q], Position, step)               -> Vec3[Q / BLOCK]
integrate(Function[Number, Number], a, b, n)        -> Number
integrateField(ScalarField[Q], Region, grid)       -> Number[Q * BLOCK^3]
```

The first executable slice exposes only result quantities that can be presented
as annotated `Number` or `Vec3`; it must show the annotation in inspector and
resource explanations. A world effect still receives the underlying existing
type and must explicitly declare the accepted quantity. For example, a push
vector accepts `Vec3[BLOCK_PER_TICK]`, not an arbitrary gradient.

## Coordinate And Time Convention

All spatial positions are **world block coordinates**, measured at a block's
center for block-derived observations and at an entity's actual `Vec3` position
for entity-derived observations. `+X` is east, `+Y` is up, and `+Z` is south,
matching Minecraft's world coordinate convention. Vector components use this
same right-handed coordinate basis.

The server tick is the only time basis. A velocity field is an instantaneous
snapshot of `Entity#getDeltaMovement`, in blocks per tick. It is not a temporal
derivative calculated by P5 and makes no promise about a future tick. A field
sample records no history, has no interpolation across ticks, and must be
treated as a measurement of the cast's server state.

## Field Providers

A field provider has a stable id, result kind, quantity, spatial support, and
one sample cost. It must be deterministic for the same server world state in a
single cast.

Implemented initial provider:

| Provider | Type | Meaning | Support |
| --- | --- | --- | --- |
| `living_density_field` | `ScalarField[COUNT_PER_BLOCK]` | Living entities in a fixed 4x4x4 cube, divided by its volume | loaded sample chunk only |

No provider may add implicit entity/global scans. Each provider declares a
maximum radius and obtains a `FieldSampleContext` containing the level,
origin, loaded-chunk predicate, target cap, and deterministic query budget.

If any requested sample position is outside the allowed radius, in an unloaded
chunk, or fails a protection/claim policy exposed by a future provider, the
whole calculus operation fails before resource consumption. It returns neither
a fabricated zero nor a partially integrated result.

## Numerical Definitions

All calculations reject non-finite inputs and non-finite intermediate or output
values. `h` is a finite positive step in blocks; it is selected from a bounded
allowlist rather than an arbitrary microscopic decimal.

### Derivative

For a scalar function `f` and scalar point `x`, use the centered difference:

```text
D_h f(x) = (f(x + h) - f(x - h)) / (2h)
```

This evaluates exactly two function samples. It is a local estimate, not a
symbolic derivative. The first function slice permits only pure closed scalar
functions; it does not use world time or player state.

### Gradient

For scalar field `phi` at point `p`, use centered differences on the three
world axes:

```text
grad_h(phi)(p) = (
  (phi(p + hX) - phi(p - hX)) / (2h),
  (phi(p + hY) - phi(p - hY)) / (2h),
  (phi(p + hZ) - phi(p - hZ)) / (2h)
)
```

It evaluates six field samples. The output points in the direction of greatest
increase **of the measured field**, not necessarily a physically meaningful
force. A gameplay theorem must name the field it follows; it may not call the
result “gravity” or “magnetism” without a provider whose definition supports
that claim.

### Divergence

For vector field `F = (Fx, Fy, Fz)`, use:

```text
div_h(F)(p) =
  (Fx(p+hX)-Fx(p-hX) + Fy(p+hY)-Fy(p-hY) + Fz(p+hZ)-Fz(p-hZ)) / (2h)
```

This uses six vector field samples. Positive values mean the sampled vector
field points net outward locally; negative values mean net inward. It is not a
count of entities and cannot alone authorize spawning, deletion, or block
changes.

### Curl

Use the centered finite-difference approximation:

```text
curl_h(F)(p) = (
  dFz/dy - dFy/dz,
  dFx/dz - dFz/dx,
  dFy/dx - dFx/dy
)
```

The implementation must reuse the same six `p +/- h axis` samples obtained for
the operator, rather than issue twelve provider calls. Curl is an oriented
local rotation estimate in Minecraft's X/Y/Z basis. It is not an angular
velocity unless the provider itself documents a velocity field.

### One-Dimensional Integration

`integrate(f, a, b, n)` uses composite Simpson's rule:

```text
n is even, 2 <= n <= 32
dx = (b-a)/n
integral ~= dx/3 * [f(a) + f(b) + 4*sum(f(a+(2k-1)dx)) + 2*sum(f(a+2k dx))]
```

It evaluates exactly `n + 1` samples. Reversed bounds are legal and negate the
oriented integral. Equal bounds are rejected in this first slice to avoid a
misleading “free successful cast”; a later zero-measure policy may soften this
only with explicit UI language.

### Spatial Field Integration

`integrateField(phi, region, grid)` is deferred until P8 supplies constructive
regions and candidate-block planning. P5 may define it but must not implement a
voxel scan against generic `Region` values. Its eventual grid must be bounded,
deterministic, loaded-chunk-safe, and report candidate/sample counts before
casting.

## Bounds, Cost, And Caching

| Operator | Maximum provider samples | Required base work |
| --- | ---: | --- |
| derivative | 0 world, 2 function | 2 evaluations |
| gradient | 6 | 6 scalar samples |
| divergence | 6 | 6 vector samples |
| curl | 6 | 6 vector samples |
| integrate | 0 world, 33 function | `n + 1` evaluations |

The server calculates a `SamplePlan` before resource deduction. It includes
operator, fixed sample positions/count, field provider query cost, function
body cost, result quantity, and any clamped output bound. The plan is derived
from the decoded source and active registry, never client preview state.

Within one operator invocation, identical `(provider, position, context)`
queries are memoized. Caches never span casts or server ticks. Integrals do not
memoize arbitrary function applications unless the lowerer proves that the
function is pure and closed.

Resource cost is monotonic in the maximum planned work:

```text
cost = operator base + sampleCount * (field provider cost + function body cost)
```

`sampleCount` is the planned upper bound, not a smaller observed entity count.
Metamagic may discount payment, but cannot increase caps, enable unloaded
samples, or alter a `SamplePlan` after validation.

## Failure And Atomicity

Failures include unsupported field/provider, non-finite values, invalid step,
odd/out-of-range Simpson panels, over-budget plan, unloaded sample chunk,
sample-radius violation, provider target-cap breach, and unavailable required
knowledge. Every failure occurs before resource consumption and before a world
effect plan executes. A field provider must not mutate the world as a side
effect of sampling.

There is no silent chunk loading, no “skip bad samples,” no random resampling,
and no partial integration. The inspector may show a safe diagnostic category
but must not expose server-only claim/protection details to a client.

## First Theorem: Gradient Lantern

The first P5 theorem should be an **anchor-local Gradient Lantern**, not a
movement spell. It samples a bounded scalar field around the anchor, computes
the centered gradient with an explicit `h`, maps its magnitude through existing
`clamp` and `round`, then sends an existing anchor redstone effect a `0..15`
signal. The signal represents local spatial change in the documented provider,
not an unexplained magical force.

Acceptance evidence:

1. Its graph exposes the provider, six sample positions, `h`, quantity, and
   exact material/sample estimate.
2. It never samples or loads outside the configured anchor radius.
3. Flat-field fixture returns zero; a linear fixture returns its known gradient.
4. Missing/unloaded samples leave anchor output and materials unchanged.
5. A server-only test proves client-provided field results and sample counts are
   ignored.

## Delivery Slices

### P5-Terra High: Definitions And Physical Interpretation (This Document)

- Freeze field meanings, coordinates, time convention, quantity metadata,
  finite-difference formulas, sample caps, cost ownership, and failure policy.
- Review non-claims: estimates are not symbolic identities, curl is not always
  angular velocity, and a field gradient is not an implicit force.

### P5-Sol: Planning And Server Architecture (Implemented)

- Implemented immutable provider snapshots and atomic publication, declarative
  metadata separate from server samplers, complete `SamplePlan` construction,
  loaded/radius boundaries, monotonic worst-case costs, bounded Simpson and
  centered-axis plans, one-cast sample caching, and stable failure codes.
- The first concrete provider and integration with `ProgramExecutor`, resource
  persistence, and dedicated-server data access belong to P5-Terra.

### P5-Terra: Bounded Runtime And Theorem (Implemented)

- Implemented `living_density_field`, transient scalar-field values, server
  `SamplePlan` validation inside `ProgramExecutor`, a per-cast sample cache,
  `field_gradient`, static budget/attribute accounting, and the Gradient
  Lantern anchor preset. The Lantern clamps `round(120 * |grad density|)` to a
  ten-second `0..15` anchor signal.
- Added pure linear-field, sample-boundary, cache, typed-graph, cost, and
  server-side isolation coverage. The project now has a reusable GameTest
  fixture, but a live dedicated-server smoke run for P5 remains an acceptance
  gate.

### P5-Luna: Teaching And Inspection

- Add bilingual Patchouli material, compact inspector/sample-plan previews,
  formula tooltips, and standard/error viewport captures.

## Explicit Deferrals

- temporal derivatives, history, rate measurement, and control loops (P17);
- arbitrary world predicates, region volume integration, fills, and solids
  (P8);
- dimensions on every legacy `Number`, automatic unit conversion, and seconds;
- divergence/curl gameplay effects before the provider and Gradient Lantern
  acceptance are complete;
- unbounded interpolation, symbolic algebra, automatic differentiation,
  Jacobians, Hessians, PDE solving, and arbitrary KubeJS executable callbacks.
