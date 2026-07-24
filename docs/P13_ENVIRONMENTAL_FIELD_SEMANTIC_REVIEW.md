# P13 Environmental Field Semantic Review

Status: Terra High mathematical and adversarial review completed on
2026-07-22. This document freezes the semantic interpretation of P13 schema
one. `P13_ENVIRONMENTAL_FIELD_CONTRACT.md` remains authoritative for ownership,
persistence, privacy, publication, and runtime boundaries.

## Review Scope

P13 is a field of named environmental correspondences, not a universal physics
simulation. This review fixes four things before codecs or samplers exist:

1. how static dimension, biome, height, and noise layers compose;
2. where that composition is continuous, merely finite-difference sampled, or
   discontinuous;
3. the exact deterministic `salted_value_v1` calculation and golden vectors;
4. the correct interpretation of gradients, reports, and redstone output.

It does not add force, gravity, magnetism, damage, terrain mutation, mana, or
cost discounts. P15 owns physical effect interpretation.

## Semantic Domain

For snapshot `s`, dimension `d`, block-center position `p`, and canonical
channel id `c`, the scalar projection is:

```text
phi_s,d,c(p) = clamp_c(
    D_s(d, c)
  + B_s(d, biomeAt(p), c)
  + H_s(d, c, height01(p))
  + N_s(d, c, p)
)
```

The attribute field is the map `c -> phi_s,d,c(p)`. It is a finite coordinate
map, not a three-component vector. A channel id is semantic identity; it is not
an array index, a world axis, or a color. Channel order may change in a UI or
serialized snapshot without changing any field value.

All schema-one summands are finite numbers. The only final range guarantee is
the channel clamp `[-16, 16]`; individual terms are not independently clamped
unless their declaration says so. Final clamping happens exactly once after all
four contributions are added. This preserves commutativity of additive layers
and makes cancellation visible before saturation.

## Layer Composition

### Dimension Base

`D_s(d, c)` is the unique winning dimension-base value for `(d, c)`. Missing
dimension declarations contribute the channel default, normally zero. The
winner is selected by source precedence, explicit priority, then stable source
id, in that order. It is constant across a dimension for one snapshot.

### Biome Contribution

Biome contribution is the sum of an optional override and zero or more
explicit additive records:

```text
B_s(d, b, c) = override_s(d, b, c) + sum(additive_s(d, b, c))
```

- At most one `OVERRIDE` record wins for a channel and biome. It is selected by
  precedence, priority, and source id.
- Every matching `ADD` record contributes once, independent of declaration
  order. At most eight additive records may match one `(d, biome, channel)`.
- Exact biome selectors may be `OVERRIDE` or `ADD`. Biome-tag selectors are
  `ADD` only in schema one. This prevents a broad tag from silently replacing
  a specific biome's identity.
- Two exact `OVERRIDE` records with equal precedence and priority for the same
  selector/channel are a fatal ambiguity, even if their numeric value matches.
- A tag that resolves to no current biome is a rejected local record with a
  source diagnostic. It does not become a wildcard.

An override does not erase additive records. This is intentional: an exact
biome may set its local baseline while independently declared ecological tags
still contribute their bounded correspondences. Packs that need replacement
must replace the additive record itself through normal precedence rather than
relying on declaration order.

### Height Curve

Each `(dimension, channel)` has zero or one selected height curve. If none is
selected, `H = 0`. With control points `(x_i, y_i)` where
`0 <= x_0 < ... < x_n <= 1`, the value is clamped piecewise-linear
interpolation:

```text
H(t) = y_0                                 for t <= x_0
     = y_i + (y_(i+1)-y_i)(t-x_i)/(x_(i+1)-x_i)
                                           for x_i < t < x_(i+1)
     = y_n                                 for t >= x_n
```

`t` is the clamped normalized center height from the P13 contract. Curves are
continuous, including their endpoint extension, but generally not
differentiable at control points. A visual slope change is not a discontinuity
and must not be treated as a separate biome or hidden threshold.

### Noise Contribution

`N_s(d, c, p) = amplitude_c * saltedValueV1(d, c, p, scale)` where the
amplitude is finite, in `[-4, 4]`, and one of the schema-one scales is
`16`, `32`, `64`, or `128` blocks. Noise is additive, dimension/channel keyed,
and has no time or player input.

Negative amplitude is legal: it reflects the same bounded landscape through a
channel's semantic convention. It does not create a second noise stream.

## Continuity And Honest Differentiation

Within one snapshot and one fixed biome, dimension base is constant, height is
continuous piecewise-linear, and `salted_value_v1` is C1 continuous across its
own lattice-cell boundaries. Final clamp is continuous but has corners at its
minimum and maximum. Therefore a channel is continuous inside a fixed biome,
but may have slope kinks from height points and clamping.

Minecraft biome lookup is discrete. Crossing a biome boundary may create a
step in `B_s`; a centered difference that straddles the boundary is a bounded
local contrast estimate, not a classical derivative at that boundary. Reload
is also a generation boundary: old and new snapshots are different functions,
not two times of one differentiable function.

The only executable P13 calculus quantity is thus:

```text
grad_h(phi)(p) = (
  (phi(p + h e_x) - phi(p - h e_x)) / (2h),
  (phi(p + h e_y) - phi(p - h e_y)) / (2h),
  (phi(p + h e_z) - phi(p - h e_z)) / (2h)
)
```

where `h` is exactly one of `1`, `2`, or `4` blocks from the server-built P5
plan. Its quantity is `CORRESPONDENCE / BLOCK`. It is a centered finite
difference estimate evaluated only at planned loaded positions. It is neither
a force vector nor a promise of differentiability.

The magnitude is calculated as:

```text
|grad_h(phi)| = sqrt(g_x^2 + g_y^2 + g_z^2)
```

using Java's strict IEEE-754 double arithmetic. All intermediate values must
be finite. Negative zero is normalized to positive zero before serialization,
comparison, or display.

## `mathmod:salted_value_v1`

The algorithm below is a compatibility surface. The runtime must not replace
it with Minecraft random, a platform-dependent PRNG, native noise, or a later
"better" implementation under the same id.

### Canonical Key Bytes

The HMAC key is the 32-byte persisted world secret. Its message is the exact
concatenation:

```text
UTF-8("mathmod:salted_value_v1")
0x00
worldSeed as signed 64-bit little-endian two's-complement
byteLength(dimensionId) as unsigned 16-bit big-endian
UTF-8(dimensionId)
byteLength(channelId) as unsigned 16-bit big-endian
UTF-8(channelId)
```

All ids are canonical lower-case `ResourceLocation` strings before encoding.
Schema-one ids must fit the unsigned 16-bit byte-length fields. `K` is the
first eight HMAC-SHA-256 output bytes interpreted as an unsigned 64-bit
little-endian integer.

### Lattice Value

For signed integer lattice coordinates `i`, `j`, `k`, arithmetic below is
unsigned 64-bit modular arithmetic. Signed coordinate conversion preserves its
two's-complement bit pattern.

```text
mix64(z):
  z = z + 0x9E3779B97F4A7C15
  z = (z xor (z >>> 30)) * 0xBF58476D1CE4E5B9
  z = (z xor (z >>> 27)) * 0x94D049BB133111EB
  return z xor (z >>> 31)

rotl64(x, n): (x << n) or (x >>> (64-n))

hash(K, i, j, k) = mix64(
  K xor rotl64(mix64(bits(i)), 7)
    xor rotl64(mix64(bits(j)), 29)
    xor rotl64(mix64(bits(k)), 47)
)

unit(h) = (unsigned(h >>> 11)) * 2^-53
value(K, i, j, k) = 2 * unit(hash(K, i, j, k)) - 1
```

`value` is in `[-1, 1)`. Java implementation uses `long` overflow, `>>>`, and
`Long.rotateLeft`; it must not use signed division for `unit`.

### Trilinear Interpolation

For block-center position `p = (x, y, z)` and spatial scale `L`, calculate
`u = p / L`, floors `q = floor(u)`, and fractional coordinates `r = u - q`.
The fade function is:

```text
fade(r) = r^2 * (3 - 2r)
lerp(a, b, t) = a + (b - a) * t
```

Apply `lerp` to the eight `value` corners in X, then Y, then Z using
`fade(r_x)`, `fade(r_y)`, and `fade(r_z)`. The result is
`saltedValueV1`. It is C1 at lattice boundaries because fade has zero first
derivative at `0` and `1`.

No fused multiply-add, SIMD-specific shortcut, integer truncation of block
centers, locale-dependent parsing, or random fallback is permitted. Java 21's
strict floating-point semantics are the reference runtime.

### Golden Vectors

The fixed fixture uses:

```text
worldSecret = bytes 00 01 02 ... 1F
worldSeed   = 123456789
algorithm   = mathmod:salted_value_v1
amplitude   = 1
```

| Dimension | Channel | Center position | Scale | Expected double bits | Expected value |
| --- | --- | --- | ---: | --- | ---: |
| `minecraft:overworld` | `mathmod:spatial` | `(0.5, 64.5, 0.5)` | 32 | `BFA0861514E94BBA` | `-0.032272967150354728` |
| `minecraft:overworld` | `mathmod:spatial` | `(-31.5, 80.5, 48.5)` | 32 | `3FD54FFC98EE4996` | `0.33300700125359961` |
| `minecraft:the_nether` | `mathmod:spatial` | `(0.5, 64.5, 0.5)` | 32 | `3FCB79D835ABDB50` | `0.21465590115232169` |
| `minecraft:overworld` | `mathmod:stability` | `(0.5, 64.5, 0.5)` | 32 | `BFE9FB9EB541A077` | `-0.811965326324766` |
| `minecraft:overworld` | `mathmod:spatial` | `(128.5, -12.5, -96.5)` | 64 | `BFB4A83F5404E3B8` | `-0.080692251217341915` |

Terra Medium must assert `Double.doubleToRawLongBits` against these values and
add negative-coordinate, lattice-boundary, channel-separation, dimension-
separation, and secret-separation cases. A changed result requires a new
algorithm id, new fixtures, and an explicit data migration policy.

## Dimensional Survey Formula

Dimensional Survey requires three distinct canonical channels in stable
lexicographic order. For each channel `c_i`, the server computes
`m_i = |grad_h(phi_i)(anchorCenter)|` from its P5 plan. Missing, unloaded, or
invalid one-of-three samples rejects the entire survey before cost consumption.

Each channel declares a positive finite report scale `tau_i` in `[0.01, 16]`.
The signal is:

```text
r_i = min(1, m_i / tau_i)
signal = clamp(0, 15, round(15 * max(r_1, r_2, r_3)))
```

`round` is `floor(x + 0.5)` for non-negative finite `x`, not a locale or UI
rounding rule. The report's dominant channel is the channel with greatest raw
`m_i`; exact numeric ties use canonical channel id lexicographic order. The
report must retain raw quantity metadata and may show a quantized band, but its
redstone level is not a physical measurement.

For a flat fixture all six pairs agree, every `m_i = 0`, the signal is zero,
and the first canonical selected channel wins the report tie. A saturated
channel may have zero observed gradient on a clamp plateau even when its
unclamped contributors were large; the report must not claim "no underlying
correspondence" from that result.

The three gradients plan 18 samples. Schema one does not reuse a sample across
different channels, because their channel keys deliberately differ. P5 cache
reuse remains available when one identical channel-position request appears
twice in a larger future graph.

## Counterexamples And Rejections

| Case | Required interpretation or rejection |
| --- | --- |
| A biome boundary lies between `p-h` and `p+h`. | Return a bounded local contrast estimate. Do not call it a differentiable force or extrapolate it beyond the one cast. |
| A height curve has a control point at the anchor. | The estimate is valid as a finite difference; it is not an analytic derivative at the kink. |
| Positive dimension base and negative biome layer cancel. | Result may be zero. Do not drop either layer from the inspector or treat zero as a missing channel. |
| Final clamp saturates at 16. | A later gradient can be zero on the plateau. No hidden unclamped value is synchronized to players. |
| A pack defines two equal-ranked exact overrides. | Reject the candidate snapshot as ambiguous, even when both numbers happen to match. |
| A tag is empty after registry resolution. | Omit the local record with a source diagnostic; never match every biome. |
| A client sends a different sample count or precomputed value. | Ignore it and rebuild the P5 plan server-side. |
| A required centered sample is in an unloaded chunk. | Fail before escrow and leave anchor output unchanged; do not force-load or replace it with zero. |
| A position is near signed coordinate limits. | Use `floor` and modular coordinate hashing; no absolute-value shortcut, overflow exception, or accidental coordinate alias is allowed. |
| A channel list is reordered in data/UI. | Values and dominant tie-break remain keyed by canonical id, never presentation index. |
| The same seed appears in another world. | A different persisted secret changes P13 noise. This is deliberate privacy behavior, not a broken seed contract. |
| A world secret is missing. | Do not silently sample a new landscape during normal execution; fail closed until the persistence policy resolves it. |
| A player calls a large gradient "gravity". | Reject that physical label at P13. Only P15 may map an explicit field to a bounded impulse plan. |
| Ambient stability is high near an anchor. | It cannot reduce a fixed item count, satisfy an attribute, or create a free cast. |
| A reload occurs between preflight and execution. | The cast retains its captured generation. It must not combine old costs with new samples. |

## Test Obligations For Terra Medium

- Exact raw-double golden vectors for `salted_value_v1`.
- Layer algebra: additive order independence, override selection, cancellation,
  final-only clamping, and height interpolation endpoints.
- Registry cases: selector ambiguity, empty tag, source precedence, aliases,
  and atomic rejected reload retention.
- Boundary cases: block centers, negative coordinates, min/max build heights,
  biome boundary finite difference, clamp plateau, all allowed scales, and
  non-finite input rejection.
- Authority cases: client values ignored, no seed/secret in reports, KubeJS
  declarations remain data-only, and no sample loads a chunk.
- Survey cases: exact 18 planned samples, tie selection, flat zero, saturation,
  failure-before-escrow, and previous anchor signal preservation.

## Resulting Review Decisions

1. P13's spatial calculation is a scalar channel projection plus finite
   difference; the full correspondence vector is never a `Vec3`.
2. `salted_value_v1` is a world-secret keyed, C1 trilinear value field with a
   frozen byte encoding and raw-double fixtures.
3. Biome discontinuities and reloads are named boundaries, not disguised
   smooth physics.
4. Dimensional Survey reports a bounded contrast/slope observation, never a
   source of payment or an autonomous controller.
5. P15 may later give physical interpretation only through explicit types,
   caps, and target/effect plans.
