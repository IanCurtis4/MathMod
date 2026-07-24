# P8 Geometry Semantic Review And Counterexamples

Status: Terra High semantic review completed on 2026-07-22. This document
freezes the geometry, discrete sampling, and construct-physics meanings that
the P8 runtime must obey.

## Review Result

P8 regions are finite, immutable predicates over world-space points. A
constructive candidate is a Minecraft block position whose **block center** is
inside the predicate. The candidate lattice is not a continuous-volume
approximation and does not assign fractional item costs.

The review keeps the Sol transaction architecture and adds four decisions:

1. Every P8 primitive uses closed membership with `GEOMETRY_EPSILON = 1e-7`
   blocks; a point on a boundary is inside.
2. A rotational profile is the bounded radial band
   `[inner(t), outer(t)]`, not an unbounded arbitrary curve.
3. Compression transforms the transient body's fractional geometry. It never
   rounds the compressed body back to blocks and therefore cannot merge paid
   voxels into a smaller item count.
4. The first construct body carries scalar moment of inertia about its selected
   spin axis. It does not pretend to be a full rigid-body simulator.

Existing `sphere_region`, `box_region`, and `sample_region` remain compatible
with persisted graphs. When their implementations move to the P8 descriptor
layer, closed epsilon membership is the canonical behavior for both old and
new region consumers.

## Coordinate Convention

World positions use Minecraft's right-handed coordinates: `+X` east, `+Y` up,
and `+Z` south. Length is measured in blocks. A block position `(x, y, z)` has
candidate center:

```text
c(x, y, z) = (x + 1/2, y + 1/2, z + 1/2)
```

All region inputs, profile parameters, frame vectors, bounds, scale, and
derived coordinates must be finite. Non-finite values invalidate the entire
region evaluation; they never become an empty region or a clamped shape.

`epsilon` is used only for closed geometric comparisons. It is not a general
input correction rule and it must not turn an out-of-range profile, axis, or
limit into a valid one.

## Primitive And Boolean Membership

Let `inside(R, p)` denote P8 membership at a finite world point `p`.

```text
inside(sphere(c, r), p) iff |p-c|^2 <= r^2 + epsilon

inside(box(lo, hi), p) iff
  lo.x-epsilon <= p.x <= hi.x+epsilon and
  lo.y-epsilon <= p.y <= hi.y+epsilon and
  lo.z-epsilon <= p.z <= hi.z+epsilon
```

Box endpoints are canonicalized componentwise, so swapping `lo` and `hi`
does not change the box. A sphere requires `0 <= r <= 8`; a box requires every
extent to be at most 16 blocks. A zero-radius sphere and a zero-thickness box
are valid point/surface predicates, though a constructive candidate plan may
still be empty.

Boolean regions use ordinary set membership:

```text
inside(union(A, B), p)        iff inside(A, p) or inside(B, p)
inside(intersection(A, B), p) iff inside(A, p) and inside(B, p)
inside(difference(A, B), p)   iff inside(A, p) and not inside(B, p)
```

`union` bounds are the union of child bounds. `intersection` bounds are their
overlap and may be an explicit finite empty bound. `difference` retains the
left-hand bound. Complement is absent because it has no finite bound without
an explicit enclosing region.

The implementation must satisfy the following extensional laws for every
finite test point and for the resulting candidate set:

```text
union(A, B) = union(B, A)
intersection(A, B) = intersection(B, A)
union(A, A) = A
intersection(A, A) = A
difference(A, A) = empty
difference(A, union(B, C)) = intersection(difference(A, B), difference(A, C))
```

Tree shape may differ, but candidate ordering is imposed only after membership
and therefore cannot vary with associativity or commutativity.

## Implicit Regions

An implicit region is a bounded pure signed predicate:

```text
implicit(bounds, phi)
inside(implicit(bounds, phi), p) iff inside(bounds, p) and phi(p) <= epsilon
```

`bounds` is a finite closed AABB and is authoritative even if `phi` would say
inside outside it. `phi` is a closed P4 pure scalar expression with the
curried type:

```text
Function[Number, Function[Number, Function[Number, Number]]]
```

Its arguments are `(x, y, z)` in world blocks. It may use literals and pure
math runes only. It may not use player position, anchor state, a field,
inventory, time, random values, collection queries, effects, a KubeJS callback,
or an unlowered graph reference.

`phi` is evaluated only at bounded candidate centers. A non-finite evaluation
or an evaluation-step limit failure rejects the whole candidate plan. The first
slice makes no claim about a curve between lattice centers; the in-game preview
must use the same center test as the server.

## Axis Frames And Radial Profiles

An axis frame is `(origin, direction)`, where `direction` is finite and has
non-zero length. Its canonical unit axis is:

```text
u = direction / |direction|
```

For point `p`, define:

```text
d = p - origin
t = dot(d, u)                         axial coordinate, in blocks
w = d - t*u                           perpendicular displacement
r = |w|                               radial coordinate, in blocks
```

A radial profile is:

```text
profile(inner, outer, a, b, maxRadius)
```

where `inner` and `outer` are closed pure `Function[Number, Number]` values.
Its solid of revolution is:

```text
inside(revolve(profile, axis), p) iff
  a-epsilon <= t <= b+epsilon and
  inner(t)-epsilon <= r and r <= outer(t)+epsilon
```

The required domain invariants are:

```text
a < b
0 <= inner(t) <= outer(t) <= maxRadius <= 8
sqrt(max(|a|, |b|)^2 + maxRadius^2) <= 8
```

The final inequality keeps the conservative AABB within 16 blocks across every
world axis, even when the rotation axis is diagonal. `inner(t) > outer(t) +
epsilon`, a negative radius, an output above `maxRadius`, a non-finite result,
or an out-of-domain function evaluation rejects the whole plan. Equality between
inner and outer is allowed as a zero-thickness shell; it may yield no block
centers and therefore an `empty_region` construction result.

The profile functions are evaluated at every visited candidate center's `t`.
Their declared `maxRadius` is a planning bound, not a promise that arbitrary
continuous points between centers will be sampled. The first slice deliberately
has voxel semantics rather than interpolation or adaptive meshing.

An axis direction is oriented. Replacing `u` with `-u` changes `t` to `-t`.
To represent the same physical solid after reversing the axis, transform both
the domain and profile functions:

```text
a' = -b
b' = -a
inner'(t) = inner(-t)
outer'(t) = outer(-t)
```

## Candidate Lattice

Given a finite AABB `[lo, hi]`, the candidate integer range for one coordinate
is exactly:

```text
first = ceil(lo - 1/2 - epsilon)
last  = floor(hi - 1/2 + epsilon)
```

The planner visits `(x, y, z)` in ascending `y`, then ascending `z`, then
ascending `x`. It evaluates `inside(region, c(x, y, z))`, retains only true
positions, then enforces the fixed visit and accepted-candidate caps from the
P8 transaction contract. No result is truncated.

The visit count counts every lattice center in the conservative AABB, including
centers rejected by the predicate. It is the relevant bound for CPU cost. The
accepted candidate count is the relevant bound for item payment, fill changes,
and construct mass.

The P8 candidate lattice is distinct from the older `sample_region` helper:
the latter can use arbitrary fractional step sizes for observation, whereas P8
construction always uses one block center per possible placed block.

## Discrete Center Of Mass And Inertia

The first construct body has one selected material, so each accepted source
voxel has equal mass `mu = massUnitsPerItem`. Let `q_i` be source voxel centers
and `N` their count:

```text
M = N * mu
C = (1 / N) * sum(q_i)
```

For a finite normalized spin axis `v`, the scalar moment of inertia is:

```text
I_v = sum(mu * |(q_i - C) x v|^2)
```

This point-mass lattice model intentionally omits each cube's internal
inertia. It is exact enough for a bounded gameplay proxy and keeps the value
inspectable. Multi-material bodies and a full inertia tensor are deferred.

Compression by `s`, where `0.25 <= s <= 1`, transforms every fractional source
center without snapping to the block lattice:

```text
q_i' = C + s * (q_i - C)
M' = M
C' = C
I_v' = s^2 * I_v
```

For collision, the first projectile may use a conservative bounding sphere
from the transformed fractional body. Rendered fragments and collision bounds
must derive from the same transformed points. They must never be deduplicated
into newly rounded block positions, because that would make material count and
mass depend on visual aliasing.

## Quantity Interpretation

P8 uses the P5 metadata approach rather than migrating legacy numeric types:

| Value | Quantity |
| --- | --- |
| `t`, `r`, bounds, profile output | `BLOCK` |
| candidate count | `COUNT` |
| mass-equivalent `M` | `CONSTRUCT_MASS` (abstract) |
| moment `I` | `CONSTRUCT_MASS * BLOCK^2` |
| angular speed `omega` | radians/tick |
| launch vector | `BLOCK_PER_TICK` |
| linear/rotational work proxy | `CONSTRUCT_MASS * BLOCK^2 / TICK^2` |

Radians are dimensionless for numeric evaluation but remain a distinct displayed
annotation. Degrees are never inferred. `CONSTRUCT_MASS` is an explicitly
abstract conservation quantity and cannot be passed to ordinary entity damage,
push, or item-count inputs without a bounded P8 conversion rune.

## Counterexample Matrix

| Case | Incorrect interpretation | Frozen outcome |
| --- | --- | --- |
| Two closed spheres touch at one point | Touching shapes are treated as disjoint | Their union contains the touching point; an intersection candidate exists only if a block center lies there |
| `difference(A, B)` at B's boundary | Boundary remains because A is closed | B's closed boundary is removed from the difference |
| `union(A, B)` enumerates child order | Cost/order depends on syntax tree | Membership is evaluated first; retained positions sort by `y,z,x` |
| Implicit `phi` is negative beyond its AABB | Infinite implicit solid is scanned | The explicit bounds exclude it completely |
| Hollow profile with `inner > 0` | Revolution always fills to the axis | Points with `r < inner(t)-epsilon` are outside |
| `inner > outer` on one sampled section | The section silently becomes empty | The entire candidate plan fails as invalid geometry |
| Reverse an asymmetric axis/profile | Same `(a,b,inner,outer)` means same shape | It mirrors the shape; transform domain and functions explicitly to preserve it |
| `a = b` or zero axis vector | A degenerate solid is a free valid spell | Both are invalid descriptor inputs; no plan is made |
| Discontinuous `outer(t)` | Preview smooths it while server samples steps | Both preview and server use the same center evaluation; discontinuity is visible as voxel steps |
| Compress a two-voxel body then round centers | Two paid voxels may merge into one and refund mass | Transformed fractional centers remain distinct; source count and mass remain two voxels' worth |
| Translate a sphere by 0.5 block | Fractional coverage is charged fractionally | Only centers decide membership; count may change discretely and costs remain whole items |
| Use `90` for angular speed | Numeric angle is assumed to mean degrees | It means 90 radians/tick and fails the angular-speed bound |
| Use a full rigid-body tensor claim | Scalar inertia predicts all rotations | The first slice computes only `I_v` for the selected spin axis |

## Implementation Gate

Before Terra implementation adds region descriptors or a candidate planner, it
must provide pure tests for:

1. Every Boolean-law and boundary case above, including exact candidate order.
2. Finite implicit predicates, rejected impurity, rejected non-finite output,
   and authoritative AABB clipping.
3. Axis normalization, reversed-axis transformation, solid membership, hollow
   bands, profile inversion, radius/domain bounds, and diagonal-AABB limits.
4. Voxel-center conversion at negative and half-block coordinates.
5. Empty candidate rejection and all visit/candidate caps without truncation.
6. Center-of-mass and inertia values for symmetric, asymmetric, and compressed
   voxel fixtures, including `I' = s^2 I` and `M' = M`.
7. The same candidate result from server planning and client preview fixtures;
   client data remains non-authoritative in every world-changing path.

The runtime must not add item consumption, block mutation, material selection,
or projectile spawning until these pure semantic cases pass.
