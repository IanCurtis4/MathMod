# P11 Derived Physics Semantic Review

## Status

This is the P11-Terra High review. It freezes the geometric approximation,
mass-weighted aggregate semantics, compression laws, and counterexamples that
the P11-Terra implementation must satisfy. It refines the P11 Sol contract; it
does not implement NeoForge adapters, JSON codecs, reload listeners, or
gameplay effects.

## 1. Canonical Shape Domain

P11 has two distinct shape domains:

| Domain | Input | Use in P11 schema 1 |
| --- | --- | --- |
| canonical material shape | `BlockState` evaluated at `BlockPos.ZERO` with `EmptyBlockGetter` | cached material volume for a `BlockItem` construct |
| contextual world shape | `BlockState`, world, position, neighbors | deferred from schema 1 |

This split is intentional. Fences, walls, redstone wire, multipart blocks, and
other neighbor-dependent shapes cannot truthfully use a cache keyed only by
`BlockState`. A construct is made from an exact `BlockItem`, not copied from
arbitrary world terrain, so schema 1 resolves the item's canonical default
state and its canonical empty-context shape. A later terrain-physics epic may
introduce a position/context cache with separate authority and performance
rules.

The canonical shape is taken from the server collision shape. Visual shape,
outline shape, culling shape, and client model geometry are not interchangeable
with occupied material volume. The canonical default state is the registered
block's default state; a profile does not select state properties in schema 1.

## 2. Occupied Volume: Sampled Union

The exact schema-1 volume rule is a sampled union of the canonical shape's
axis-aligned boxes. The active policy supplies a power-of-two resolution `N`,
initially `16`.

For each integer cell `(i, j, k)` in `[0, N)^3`, sample its center:

```text
q(i,j,k) = ((i + 0.5) / N, (j + 0.5) / N, (k + 0.5) / N)
```

The cell is occupied if `q` belongs to the union of all normalized shape boxes.
The occupied volume is:

```text
V = occupiedCells / N^3
```

Each source box is clamped to `[0, 1]^3` before testing. A box with no positive
extent after clamping contributes nothing. Membership uses lower-inclusive,
upper-exclusive bounds; sample centers never lie on `0` or `1`, so this gives
stable behavior at a shared box boundary.

This rule intentionally samples the union, not a sum of AABB volumes. Overlap
cannot inflate volume, box ordering cannot change volume, and the result is
bounded in `[0, 1]`. Resolution `16` is a deterministic approximation, not an
assertion that a slab or fence has exact continuous volume.

### Required Invariants

```text
empty shape                         => V = 0
full unit cube                      => V = 1
reordered equal boxes               => unchanged V
duplicate box                       => unchanged V
adding a contained box              => unchanged V
adding a disjoint occupied box      => V does not decrease
clamping outside-cube box portions  => V remains in [0, 1]
```

## 3. Block Mass And Fallback Semantics

For canonical volume `V`, resolved density `rho`, hardness `H`, and blast
resistance `R`:

```text
C = clamp(1 + a * log1p(max(0, H)) + b * log1p(max(0, R)), 1, 8)
m = clamp(V * rho * C, 0, 256)
```

The adapter must treat a negative destroy speed, an unavailable value, `NaN`,
and infinity as unavailable rather than as a literal physical value. The
policy fallback is then used. A declared density is never inferred from
hardness; compactness is only a bounded correction after density resolution.

Zero-volume/zero-mass profiles are valid resolved profiles. They must not
crash material selection, preview, cache, or construct planning. In the first
runtime integration, they produce zero physical contribution and retain the
existing P8 abstract payment and `massEquivalent` behavior. Rejecting all
non-solid modded `BlockItem`s is a later gameplay policy decision, not an
implicit consequence of the resolver.

## 4. Construct Aggregate

Consider source points `r_i` and resolved physical masses `m_i`, after any
configured compression mass rule. All operations are over finite doubles in a
stable source order, with compensated summation required if the implementation
can accumulate more than 32 terms.

```text
M = sum(m_i)
c = sum(m_i * r_i) / M, when M > epsilon
d_i = r_i - c
I = sum(m_i * (dot(d_i,d_i) * Identity - outer(d_i,d_i)))
```

For `M <= epsilon`, the aggregate is the explicit zero-mass case:

```text
M = 0
c = arithmetic mean of source points, or origin for an empty source list
I = zero tensor
```

The fallback center exists only to make a display and later transform stable;
it does not invent physical momentum. The current P8 source list is non-empty,
so origin is defensive only.

`I` is symmetric and positive semidefinite up to a documented numerical
tolerance. For a normalized spin axis `n`, scalar inertia is:

```text
I_n = max(0, transpose(n) * I * n)
```

It is invariant under source permutation and global translation, and changes
under rotation exactly as the tensor transforms. The old P8 equal-mass scalar
inertia remains a legacy balance/display value; P11 physical inertia is a
separate value.

## 5. Compression Law

Let `s` be visual scale and `gamma` be the declared compression mass exponent:

```text
0.25 <= s <= 1
0 <= gamma <= 3
r'_i = c + s * (r_i - c)
m'_i = m_i * s^gamma
M' = M * s^gamma
c' = c, when M > epsilon
I' = I * s^(gamma + 2)
rho' = rho * s^(gamma - 3), for a uniform material interpretation
```

Schema-1 P8 compatibility fixes `gamma = 0`: total physical mass is conserved,
the center remains fixed, and inertia scales by `s^2`. `massEquivalent` is
always conserved independently of `gamma`.

No transformed point is rounded to block centers. Rounding would merge visual
points, change the sampled source count, and create false refunds or false
mass changes. Physical transforms operate on continuous source centers;
placement remains a separate transactional operation.

## 6. Motion Interpretation And Legacy Boundary

The physical observables are:

```text
p = M' * v
E_linear = 0.5 * M' * dot(v,v)
L = I' * omega
E_rotation = 0.5 * dot(omega, L)
```

They are finite planning quantities, never direct terrain-damage or entity-
damage formulas. Any schedule consuming them must be monotonic, capped, and
server-owned.

To preserve P8 gameplay while the physical system is introduced, the first
P11 runtime integration computes and exposes these values to the server-side
construct profile, preview, and future bounded resource schedules, but does
not alter the existing `launch_construct` knockback formula. A future rune or
effect-plan contract may opt into a physical impact schedule only after it
defines target policy, ownership, protection, caps, and dedicated-server
acceptance. This is an explicit compatibility boundary, not a statement that
physical impact is permanently deferred.

The existing P8 rules remain: one construct flight per owner, maximum speed,
swept collision, target cap, no chunk ticket, unload stop, and no terrain
mutation. Reload cannot mutate a flight's captured physical aggregate.

## 7. Counterexample Matrix

| Case | Incorrect shortcut | Required result |
| --- | --- | --- |
| Full block | assume every block volume is 1 | sampled canonical cube gives `V = 1` |
| Slab | use material density without shape | `V` is approximately `0.5` at N=16 |
| Stairs | sum overlapping shape boxes | sampled union stays within `[0,1]`; no double count |
| Fence | cache world-connected shape by state only | canonical empty-context shape; contextual arms deferred |
| Door | use visual model thickness | collision shape only; canonical default state is deterministic |
| Empty shape | divide by zero for center | zero mass/inertia and stable fallback center |
| Duplicate boxes | sum each AABB | duplicate does not change volume |
| Box outside cube | trust raw coordinates | clamp before sampling |
| Obsidian | call blast resistance density | declared/tag density plus capped compactness |
| Unknown modded block | reject because no known family | deterministic fallback profile |
| Extreme custom resistance | unbounded `log1p` result | compactness/result caps apply |
| Mixed stone/iron body | equal-weight centroid | center shifts toward iron's resolved mass |
| Same body, reordered sources | order-dependent floating point drift | same aggregate within tolerance |
| Compression, gamma 0 | lower mass with volume | mass remains, inertia scales `s^2` |
| Compression, gamma 3 | keep mass | density remains, mass scales `s^3` |
| Compressed coordinates | round centers | retain continuous transformed points |
| Reload during flight | re-resolve every tick | flight keeps launch-time aggregate |

## 8. Terra Implementation Obligations

The next implementation slice must provide pure tests for:

1. sampled union volume for full cube, slab, duplicate/overlap, disjoint boxes,
   and clamped boxes;
2. deterministic mass formula and fallback normalization;
3. weighted center and tensor invariance under source permutation and
   translation;
4. scalar inertia projection, positive-semidefinite tolerance, and axis
   normalization rejection;
5. gamma `0`, `1`, and `3` compression identities;
6. zero-mass aggregate behavior;
7. snapshot-version cache separation and canonical-shape-only caching;
8. a proof that P8 `massEquivalent` and launch impulse do not change in the
   first P11 runtime integration.

The implementation must not choose a different shape resolution, contextual
shape source, mass fallback, tensor convention, or physical impact behavior
without updating this review and repeating its counterexample matrix.
